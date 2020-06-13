package scrapper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

//import db.DBHandler;
import db.LocalDBHandler;
import model.ExtractionType;
import model.Info;
import webhandler.FireFoxOperator;

public class WebsiteListMain {
	//private static final String   = null;
	public LinkedList<Info> list = null;
	LocalDBHandler localDb;
	FireFoxOperator fireFoxOperator = new FireFoxOperator();
	int listSize = 0;
	private CSV_Scanner csvScanner;
	
	public WebsiteListMain() {
		list = new LinkedList<Info>();
		localDb = new LocalDBHandler();
		csvScanner = new CSV_Scanner();
		listSize = 0;
	}

	public static ExtractionType extractType = ExtractionType.onlylist; 
	
	public void setExtractionMode(ExtractionType type) { 
		System.out.println("setExtractionMode " + type.toString()); // testing
		extractType = type;
		if(type == ExtractionType.detailinfo) {
			fireFoxOperator.changeParserMode(type);
		}
		if(type == ExtractionType.fromList) {
			fireFoxOperator.changeParserMode(type);
			// print list, active selection, set mode
		}
			
	}

	public int dataScan(String fileLocation) {
		list = csvScanner.dataScan(fileLocation);
		return list.size();
	}
	
	public int scanCSV(String filePath) {
		LinkedList<Info> upLoadedList = new LinkedList<>();
		CSV_Scanner csv_Scanner = new CSV_Scanner();
		if (filePath.endsWith(".csv")) {
			upLoadedList = csv_Scanner.dataScan(filePath);
			return addToDb(upLoadedList); // "listsize " + 
		} else
			return -1; //"ERROR !!! : It's not a CSV file";
	}
	
	
	// modified 11 mar 2018 
	//11 june don't need this 
//	public String searchItemOnPage() {
//		return fireFoxOperator.currentPageStatus();
//	}

	// modified 24 May 2020
	public boolean search(String keyword, String location) {
		return fireFoxOperator.yellowpagesSearch(keyword, location);
	}

	// modified 11 mar 2018
	public boolean launcherBrowser() {
		fireFoxOperator = new FireFoxOperator();
		return fireFoxOperator.browserLauncher();
	}

	// modified 11 mar 2018
	public boolean closeBrowser() {
		return fireFoxOperator.closeBrowser();
	}

	// modified 11 mar 2018
	public int currentpage() {
		return fireFoxOperator.currentPageNumber();
	}

	// modified 11 mar 2018
	public int openNextPage() {
		return fireFoxOperator.openNextPage();
	}
	
	public void fullPageScroll() {
		fireFoxOperator.fullPageScroll();
	}
	// modified 11 mar 2018
	public int openPreviousPage() {
		return fireFoxOperator.openPreviousPage();
	}

	public int getTotalSize() {
		return listSize;
	}

	private String urlFormat = "https://www.linkedin.com/in";
	// modified 31 May 2020
	public int takeList() {
		LinkedList<Info> currentlist = null; 
		
		if(extractType == ExtractionType.detailinfo) {
			fireFoxOperator.setTabs();
			//LinkedList<String> personUrls = fireFoxOperator.personalProfilelinks();
			LinkedList<Info> infos = fireFoxOperator.takeList();
			currentlist = fireFoxOperator.takeListDetails(infos);
			fireFoxOperator.closeTab();
		}else if(extractType == ExtractionType.fromList) {
			Info info = list.pop();
			String url = info.getLink();
			System.out.println("url --- " + url);
			if(url.contains(urlFormat)) {
				localDb.update(fireFoxOperator.takeDetails(url));
			}else {
				localDb.update(info);
			}
			return 1;
		}
		else {
			currentlist = fireFoxOperator.takeList();
		}
		if(currentlist == null)
			return 0;
		else
			return addToDb(currentlist);
	}

	public int loadInfoFromDb() {
		list = localDb.selectAll();
		listSize = list.size(); 
		return listSize;
	}
	
	
	public int addToDb(LinkedList<Info> parsedlist) {
		int count = 0;
		ListIterator<Info> it = parsedlist.listIterator();
		while(it.hasNext()) {
			Info info = (Info) it.next();
			if(localDb.insert(info)) count++;
		}
		listSize += count; 
		return count;
	}
	
	public int clearList() {
		if (localDb.createNewTable()) { // drop & create table
			listSize = 0;
			return 0;
		} else {
			return -1;
		}
	}
	
	public int countData() {
		return localDb.countRecords();
	}
	
	public int printList(String keyword) {
		list = localDb.selectAll();
		CsvGenerator csv = new CsvGenerator();
		int number = csv.listtoCsv(keyword, list);
		return number;
	}

	




}
