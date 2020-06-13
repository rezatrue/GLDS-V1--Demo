package application;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.ExtractionType;
import scrapper.WebsiteListMain;
import scrapper.CSV_Scanner;
import scrapper.Parser;
import webhandler.FireFoxOperator;


public class MainController  extends Service<String> implements Initializable {
	@FXML
	private TextField textMessage;
	@FXML
	private TextField textKeyword;
	@FXML
	private TextField textCurrentPage;
	@FXML
	private TextField textEndPage;
	@FXML
	private TextField textListSize;

	static boolean status = false;
	
	private Preferences prefs = null;

	// private LinkedList<Info> list = null;
	@FXML
	private ChoiceBox<String> choiceBox = new ChoiceBox<>();
	private String[] choiceBoxItems = { "Listings", "Details", "From List"};
	private WebsiteListMain websiteListMain;
	private int listSize;
	
	
	@FXML
	private Button settingBtn;

	@FXML
	public void settingBtnAction(ActionEvent event) {
		System.out.println("Setting Button");
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/application/Settings.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Settings");
			stage.setScene(new Scene(parent));
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private Button startBtn;

	MyService runService;
	@FXML
	public void startBtnAction(ActionEvent event) {
		System.out.println("Start Button");

		
		
		runService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				// event.getSource().getValue() will take return value from service thread 
				startBtn.setText(event.getSource().getValue().toString());
			}
		});
		
		if (startBtn.getText().contains("Pause")) {
			startBtn.setText("Start");
			openBrowserBtn.setDisable(false);
			System.out.println("--------------paused---------------------" + runService.getState().toString());
			
			switch(runService.getState().toString()) {
			case "RUNNING":
				runService.cancel();
				break;
			case "FAILED":
				runService.reset();
				break;
			}	
		} else if (startBtn.getText().contains("Start")) {
			startBtn.setText("Pause");
			openBrowserBtn.setDisable(true);
			textCurrentPage.setText(0 + "");
			// calling MyService start / cancel /restart based on getState() 
			System.out.println("--------------started---------------------" + runService.getState().toString());

			System.out.println(runService.getState().toString());
			
			switch(runService.getState().toString()) {
			case "READY":
				runService.start();
				break;
			case "CANCELLED":
			case "SUCCEEDED":
				runService.restart();
				break;
			case "FAILED":
				runService.reset();
				runService.restart();
				break;	
			}

		}

	}

	private class MyService extends Service<String>{

		@Override
		protected Task<String> createTask() {
			
			return new Task<String>() {
				
				@Override
				protected String call() throws Exception {
					if(WebsiteListMain.extractType == ExtractionType.fromList) {
						int amount = websiteListMain.loadInfoFromDb();
						textListSize.setText(amount+"");
						textMessage.setText("Total : " + amount + " Scraping Details...");
						int count  = 0 ;
						do {
							count += websiteListMain.takeList();
							if ( websiteListMain.list.size() < 1) 
								break;
						}while(startBtn.getText().contains("Pause"));
						textMessage.setText("Total data Scraped: "+ count + "; remains: "+ websiteListMain.list.size());
						return "Start";
					}else {
					boolean run = true;
					boolean autoSelected;
					int currentPage = websiteListMain.currentpage(); // Zero replaced
					int endPage;
					websiteListMain.fullPageScroll();
						do {
							autoSelected = auto.isSelected();
							textCurrentPage.setText(currentPage + "");
							if(currentPage == -1) break; // add
							endPage = Integer.parseInt(textEndPage.getText());
							if (currentPage <= endPage) {
								int newadded = websiteListMain.takeList();
								String sizeText = textListSize.getText();
								textListSize.setText(Integer.parseInt(sizeText) + newadded + "");
								textMessage.setText("Processing page " + currentPage);
								if (autoSelected && currentPage < endPage) {
									currentPage = websiteListMain.openNextPage();
									textCurrentPage.setText(currentPage + "");
									if(currentPage == -1) break; // add
								} else {
									run = false;
								}
							}else {
								break;
							}
						} while (autoSelected && startBtn.getText().contains("Pause") && run);
					
					textMessage.setText("Process stopped at page " + currentPage);
					openBrowserBtn.setDisable(false);
					textCurrentPage.setText(0 + "");
					return "Start";
					}
				}
			};
			
		}
		
	}
	
	
	@FXML
	private Button openBrowserBtn;

	@FXML
	public void openBrowserBtnAction(ActionEvent event) {
		System.out.println("Open Browser Button");
		String buttonText = openBrowserBtn.getText();

		System.out.println(buttonText);

		
		if (buttonText.toLowerCase().contains("open")) {
			openBrowserBtn.setText("Close");
			openBrowserBtn.setDisable(true);

			if(clearDataNotification()){
				listSize = websiteListMain.clearList();
			}else {
				listSize = websiteListMain.countData();
			}
			//textListSize.setText(String.valueOf(listSize));
			textListSize.setText(Integer.toString(listSize));
			
			ShowProgressBar showProgress = new ShowProgressBar();

			this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					String status = event.getSource().getValue().toString();
					System.out.println(status);
					if(status == "Done") {
						showProgress.close();
						textMessage.setText("New Browser is lunched, please proceed . . .");
						openBrowserBtn.setDisable(false);
						startBtn.setDisable(false);
						printListBtn.setDisable(false);
						resetBtn.setDisable(false);
						choiceBox.setDisable(false);
					}
				}
			});	
			
			// calling service start / restart based on getState() 
			System.out.println(this.getState().toString());
			
			switch(this.getState().toString()) {
			case "READY":
				this.start();
				break;
			case "CANCELLED":
			case "SUCCEEDED":
				this.restart();
				break;
			}
			
		} else {
			// show an alert message here
			websiteListMain.closeBrowser();
			openBrowserBtn.setText("Open");
			selectCsvFileBtn.setDisable(true);
			startBtn.setDisable(true);
			choiceBox.setDisable(true);
			textMessage.setText("Browser is Closed");
		}
		
	}

	@FXML
	private TextField textCsvFilePath;
	
	@FXML
	private Button selectCsvFileBtn;
	
	@FXML
	public void selectCsvFileBtnAction(ActionEvent event) {

		//stackoverflow.com/questions/25491732/how-do-i-open-the-javafx-filechooser-from-a-controller-class/25491787
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("CSV Files", "*.csv")
			);
		
        File file = fileChooser.showOpenDialog(new Stage());
        
        if(file != null) {
        	String filepath = file.getAbsolutePath();
    		if(filepath.endsWith(".csv")) {
    			listSize = websiteListMain.scanCSV(filepath);
    			if(listSize == 0) {
    				startBtn.setDisable(true);
    				filepath = "";
    				textMessage.setText("File is not in proper format");
    			}else if(listSize > 0) {
    				startBtn.setDisable(false);
    				textListSize.setText(listSize+"");
    				textMessage.setText("List size : "+ listSize);
    			}
    		}
    		textCsvFilePath.setText(filepath);
        }
	}

	@FXML
	private Button printListBtn;

	@FXML
	public void printListBtnAction(ActionEvent event) {
		System.out.println("Print List");
		int count = websiteListMain.printList(textKeyword.getText());
		textMessage.setText("New CSV file created with " + count + " entity.");
	}

	@FXML
	private Button resetBtn;

	@FXML
	public void resetBtnAction(ActionEvent event) {
		System.out.println("Reset");

		if (clearDataNotification()) {
			listSize = websiteListMain.clearList();
			if (listSize == 0)
				textListSize.setText(String.valueOf(listSize));
			textCurrentPage.setText("0");
			textEndPage.setText("25");
			choiceBox.setValue(choiceBoxItems[0]);
			websiteListMain.setExtractionMode(ExtractionType.onlylist);
			textMessage.setText("All data deleted & Profile reset");
			guireset();
		}else {
			textMessage.setText("Unable to clear previous Data");
		}

	}

	private boolean clearDataNotification() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Please Confirmation");
		alert.setHeaderText("Previously collected all data will be removed.");
		alert.setContentText("Do you want to delete all previously data?");

		java.util.Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return true;
		return false;
	}
	
	@FXML
	private CheckBox auto;

	@FXML
	public void autoCheckBoxAction(ActionEvent event) {
		if (auto.isSelected()) {
			previousPageBtn.setDisable(true);
			nextPageBtn.setDisable(true);
		} else {
			previousPageBtn.setDisable(false);
			nextPageBtn.setDisable(false);
		}
	}

	@FXML
	private Button previousPageBtn;

	@FXML
	public void previousPageBtnAction(ActionEvent event) {
		System.out.println("Previous Page");
		websiteListMain.openPreviousPage();
	}

	@FXML
	private Button nextPageBtn;

	@FXML
	public void nextPageBtnAction(ActionEvent event) {
		System.out.println("Next Page");
		websiteListMain.openNextPage();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		listSize = 0;
		runService = new MyService();
		prefs = Preferences.userRoot().node("gdb");
		selectCsvFileBtn.setDisable(true);
		startBtn.setDisable(true);
		printListBtn.setDisable(false); // testing
		resetBtn.setDisable(true); 
		openBrowserBtn.setDisable(true);

		choiceBox.getItems().addAll(choiceBoxItems);
		choiceBox.setValue(choiceBoxItems[0]);
		choiceBox.setOnAction(e -> choiceBoxSetup(choiceBox));
		choiceBox.setDisable(true); // testing
		guireset();

		websiteListMain = new WebsiteListMain();
		/*
		 * try { Parent parent =
		 * FXMLLoader.load(getClass().getResource("/application/Login.fxml"));
		 * Stage stage = new Stage(); stage.setTitle("Please login");
		 * stage.setScene(new Scene(parent)); stage.setResizable(false);
		 * stage.show(); } catch (Exception e) { e.printStackTrace(); }
		 */
		
		 String msg = "Welcome Scraper";
		textMessage.setText(msg);
		if (msg.toLowerCase().contains("welcome"))
			openBrowserBtn.setDisable(false);
		else
			openBrowserBtn.setDisable(true);
	}

		
	private Object choiceBoxSetup(ChoiceBox<String> choiceBox) {
		String item = choiceBox.getValue();
		System.out.println(item);
		ExtractionType extractType = ExtractionType.onlylist;
		if (item == choiceBoxItems[1])
			extractType = ExtractionType.detailinfo;
		else if (item == choiceBoxItems[2]) {
			extractType = ExtractionType.fromList;
			selectCsvFileBtn.setDisable(false);
		}else
			extractType = ExtractionType.onlylist;
		websiteListMain.setExtractionMode(extractType);
		return null;
	}

	private void guireset() {
		previousPageBtn.setDisable(true);
		nextPageBtn.setDisable(true);
	}

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			
			@Override
			protected String call() throws Exception {
				websiteListMain.launcherBrowser();
				return "Done";
			}
			
		};
		
	}

}
