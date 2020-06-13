package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
	@FXML
	private TextField txtuserName;
	@FXML
	private PasswordField txtpassword;
	@FXML
	private TextField txtprofile;
	@FXML
	private TextField txtdriver;
	@FXML
	private PasswordField txtdbServer;
	@FXML
	private Button saveBtn;

	private Preferences prefs;

	@FXML
	public void saveBtnAction() {
		prefs.put("ypuser", txtuserName.getText());
		prefs.put("yppassword", txtpassword.getText());

		prefs.put("ypdataserver", txtdbServer.getText());

		prefs.put("profilename", txtprofile.getText());
		prefs.put("geckodriverlocation", txtdriver.getText());

		Stage stage = (Stage) txtuserName.getScene().getWindow();
		stage.close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		prefs = Preferences.userRoot().node("gdb");
		txtuserName.setText(prefs.get("ypuser", ""));
		txtpassword.setText(prefs.get("yppassword", ""));
		txtdbServer.setText(prefs.get("ypdataserver", ""));
		txtprofile.setText(prefs.get("profilename", ""));
		txtdriver.setText(prefs.get("geckodriverlocation", ""));
		
	}

}
