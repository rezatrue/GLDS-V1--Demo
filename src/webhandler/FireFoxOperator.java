package webhandler;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import model.ExtractionType;
import model.Info;
import scrapper.ListParser;
import scrapper.Parser;
import scrapper.WebsiteListMain;
import scrapper.DetailsParser;


public class FireFoxOperator {

	private String profileName = "default";
	private String geckodriverdir;

	public static WebDriver driver = null;
	private String urlFormat = "https://www.linkedin.com/in";

	private Preferences prefs;

	private Parser parser = null; // changing
	
	public FireFoxOperator() {
		prefs = Preferences.userRoot().node("gdb");
		this.profileName = prefs.get("profilename", "");
		this.geckodriverdir = prefs.get("geckodriverlocation", "");
		parser = new ListParser(); // changing
	}

	public void changeParserMode(ExtractionType type) {
		if(type == ExtractionType.detailinfo) {
			parser = new DetailsParser(); //checkit
		}else if(type == ExtractionType.fromList) {
			parser = new DetailsParser();
		}
		else{
			parser = new ListParser();
		}
		
	}

	public LinkedList<Info> takeList() {
		
		parser = new ListParser();
		LinkedList<Info> currentlist = parser.parse();
		return currentlist;
	}
	protected String mainTabHandaler;
	protected String subWindowHandler;
	public void setTabs() {
		System.out.println("setTabs");
		mainTabHandaler = driver.getWindowHandle();
		//((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		((JavascriptExecutor) driver).executeScript("window.open();");
	   	  Set<String> handles = driver.getWindowHandles();
		  Iterator<String> iterator = handles.iterator();
		  while (iterator.hasNext()) {
		   subWindowHandler = iterator.next();
		  }
	}
	public int switchToMainTab() {
		if(mainTabHandaler != null) {
			driver.switchTo().window(mainTabHandaler);
			return 0;
		}else {
			return -1;
		}
	}
	public int switchToSubTab() {
		if(subWindowHandler != null) {
			driver.switchTo().window(subWindowHandler);
			return 0;
		}else {
			return -1;
		}
	}
	
	
	public Info takeDetails(String url) {
		parser = new DetailsParser();
		return parser.parseDetails(url);
	}
	
	public LinkedList<Info> takeListDetails(LinkedList<Info> infos) { 
		switchToSubTab();
		parser = new DetailsParser();
		LinkedList<Info> currentlist = new LinkedList<>();
		
		Iterator<Info> it = infos.iterator();
		Info into;
		while(it.hasNext()) {
			into = it.next();
			if(into.getLink().contains(urlFormat)) {
				currentlist.add(parser.parseDetails(into.getLink()));
			}else {
				currentlist.add(into);
			}
		}
		
		return currentlist;
	}
	
	public boolean browserLauncher() {
		//  testing 
		
		ProfilesIni profile = new ProfilesIni();
		FirefoxProfile myprofile = profile.getProfile(profileName);

		myprofile.setPreference("network.proxy.type", 0);
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(FirefoxDriver.PROFILE, myprofile);

		System.setProperty("webdriver.gecko.driver", geckodriverdir);
		
		driver = new FirefoxDriver(capabilities);
		
//		System.setProperty("webdriver.gecko.driver", geckodriverdir);
//		driver = new FirefoxDriver();
		return true;
	}

	private final String SEARCHXPATH = "//div[@id=\"query-container\"]/label/input[@id=\"query\"]";
	private final String LOCATIONXPATH = "//div[@id=\"location-container\"]/label/input[@id=\"location\"]";
	private final String SEARCHBUTTONXPATH = "//button[@type=\"submit\" and @value=\"Find\"]";

	public boolean yellowpagesSearch(String keyword, String location) {
		try {
			if (isElementPresent(By.xpath(SEARCHXPATH))) {
				// new version
				driver.findElement(By.xpath(SEARCHXPATH)).clear();
				driver.findElement(By.xpath(SEARCHXPATH)).sendKeys(keyword);
				driver.findElement(By.xpath(LOCATIONXPATH)).clear();
				driver.findElement(By.xpath(LOCATIONXPATH)).sendKeys(location);
				driver.findElement(By.xpath(SEARCHBUTTONXPATH)).click();
			} else
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public String currentPageStatus() {
		By byCurentPage = By.xpath(SEARCHBUTTONXPATH);
		if (isElementPresent(byCurentPage))
			return "SEARCHPAGE";
		return null;
	}

	public boolean closeTab() {
		driver.close();
		switchToMainTab();
		return true;
	}
	public boolean closeBrowser() {
		driver.quit();
		return true;
	}

	public String getSourseCode() {
		fullPageScroll();
		String pageSource = "";
		pageSource = driver.getPageSource().toString();
		// System.out.println(pageSource);
		return pageSource;
	}
	// June 10 2020
	public LinkedList<String> personalProfilelinks() {
		
		switchToMainTab();
		
		LinkedList<String> links = new LinkedList<>();
		
		By linkBy = By.xpath("//ul[contains(@class,\"search-results__list\")]/li//a[parent::div[contains(@class,\"search-result__info\")]]");
			
		List<WebElement> webElements = FireFoxOperator.driver.findElements(linkBy); 
		Iterator<WebElement> it = webElements.iterator();
		while(it.hasNext()) {
			links.add(it.next().getAttribute("href"));
		}
		return links;
	}
	// June 08 2020
	public int currentPageNumber() {
		int responsepage = 1; // if there is pagination it means it has only on page
		
		String currentPage = "//button/span[1][following-sibling::span[contains(text(),\"Current page\")]]";
		
		if(isElementPresent(By.xpath(currentPage))){
			try {
			String currentPageString = driver.findElement(By.xpath(currentPage)).getText();
			System.out.println(" <- "+ currentPageString +" ->");
			responsepage = Integer.parseInt(currentPageString);
			System.out.println(responsepage);
			}catch( Exception e){
				System.out.println(" <- catch - >");
				responsepage = -1; // error  
			}
		}
		return responsepage;
	}
	
	// June 08 2020
	public int openNextPage() {
		if(WebsiteListMain.extractType == ExtractionType.detailinfo) {
			switchToMainTab();
		}
		System.out.println(" <- openNextPage clicked");
		int responsepage = -1;
		
		String newPage = "//button[contains(@class,\"next\") and not(contains(@class, \"disabled\"))]";
		
		if(isElementPresent(By.xpath(newPage))){
			try {
				driver.findElement(By.xpath(newPage)).click();
				fullPageScroll();
				responsepage = currentPageNumber();
			}catch( Exception e){
				System.out.println(" <- catch - >" + e.getMessage());
			}
//			finally{
//				driver.findElement(By.xpath(newPage)).click();
//			}
		}
		
		return responsepage;
		
	}
	// June 08 2020
	public int openPreviousPage() {
		if(WebsiteListMain.extractType == ExtractionType.detailinfo) {
			switchToMainTab();
		}
		System.out.println(" <- openPreviousPage clicked");
		int responsepage = -1;
		
		String prePage = "//button[contains(@class,\"previous\") and not(contains(@class, \"disabled\"))]";
		
		if(isElementPresent(By.xpath(prePage))){
			try {
				driver.findElement(By.xpath(prePage)).click();
				fullPageScroll();
				responsepage = currentPageNumber();
			}catch( Exception e){
				System.out.println(" <- catch error - >" + e.getMessage());
			}
//			finally{
//				driver.findElement(By.xpath(prePage)).click();
//			}
			
		}
		
		return responsepage;

	}

	public int switchingPage(By by) {
		//int switchedpage = 0;
		// driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		try {
			driver.findElement(by).click();
			fullPageScroll();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());;
		}
		/*
		int limits = 5;
		do {
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			switchedpage = currentPageNumber();
			limits--;
		} while ((switchedpage == 0) && limits > 0);
		*/
		System.out.println(currentPageNumber() + " <- currentPageNumber");

		return currentPageNumber();
	}
	// set different info
	public boolean setUrl(String type) {
//		if (type.toLowerCase().contains("salesnav"))
//			driver.get(salesNavUrl);
//		else
//			driver.get(url);
		return true;
	}

	public void fullPageScroll() {
		// https://stackoverflow.com/questions/42982950/how-to-scroll-down-the-page-till-bottomend-page-in-the-selenium-webdriver
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		try {
			jse.executeScript("scroll(0, 250);");
			Thread.sleep(500);
			jse.executeScript("scroll(0, 550);");
			Thread.sleep(1000);
			jse.executeScript("scroll(0, 750);");
			Thread.sleep(500);
			jse.executeScript("scroll(0, 950);");
			Thread.sleep(1000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if I direct go to bottom of the page page full content don't load
		// jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	// For public profile link
	public boolean newTabOpener() {
		String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
		driver.findElement(By.tagName("html")).sendKeys(selectLinkOpeninNewTab);
		return true;
	}

	public boolean newTabCloser() {
		String selectLinkCloseNewTab = Keys.chord(Keys.CONTROL, "w");
		driver.findElement(By.tagName("html")).sendKeys(selectLinkCloseNewTab);
		return true;
	}

	public boolean linkOpener(String profileUrl) {
		// driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.get(profileUrl);
		return true;
	}

	public String collectProfileLink() {
		return driver.findElement(By.tagName("a")).getText();

	}

	String infoBtnCssSelector = "#topcard > div.module-footer > ul > li > button";
	String infoBtnCssSelector1 = ".more-info-tray > table:nth-child(4) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)";

	public String getPublicLink(String salesProLink) {
		driver.get(salesProLink);
		if (!findAndClick(infoBtnCssSelector))
			return salesProLink;
		try {
			WebElement element = driver.findElement(By.cssSelector(infoBtnCssSelector1));
			return element.getText();
		} catch (Exception e) {
		}

		return salesProLink;
	}

	private boolean findAndClick(String selector) {
		try {
			By by = By.cssSelector(selector);
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			driver.findElement(by).click();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
