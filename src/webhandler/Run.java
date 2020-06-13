package webhandler;

import org.openqa.selenium.chrome.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Run {
	static WebDriver driver;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello:");
		
		
		
		
			String mydata = "Los Angeles, CA 90026";
			System.out.println(mydata.replaceAll("(\\S+)", "$1"));
			Pattern pattern = Pattern.compile("(\\S+)");
			Matcher matcher = pattern.matcher(mydata);
			if (matcher.find()) {
				System.out.println(matcher.group(1));
			}
		
		
		//runfirefoxDefaultProfile();
		/*
		
		openNavLink();
		getData();
		
		*/ 
		//takeDetailsInfo();
		//runChoromeDefaultProfile();
		//separatingCityStateZip();
	}

	// no result
	private static void takeDetailsInfo() {
		Calendar calendar = Calendar.getInstance(); 
		System.out.println("Start : "+calendar.getTime());
		driver.get("https://www.yellowpages.com/search?search_terms=restaurants&geo_location_terms=Los%20Angeles%2C%20CA&s=average_rating");
		String mainTabHandaler = driver.getWindowHandle();
		
		//((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		((JavascriptExecutor) driver).executeScript("window.open();");
		String subWindowHandler = null;
	   	  Set<String> handles = driver.getWindowHandles();
		  Iterator<String> iterator = handles.iterator();
		  while (iterator.hasNext()) {
		   subWindowHandler = iterator.next();
		  }
		  
		  int count = 0;
	        do {
	        	System.out.println("loop : "+calendar.getTime());
	        	count++;
	        	System.out.println("count : " + count++);	
		        LinkedList<String> list = getBusinessPageUrls();
		        Iterator<String> it = list.iterator();
		        driver.switchTo().window(subWindowHandler); 
		        while(it.hasNext()) {
		        	driver.get(it.next());
		        	//getData();
		        	System.out.println(driver.findElement(By.xpath("//div[@class=\"sales-info\"]/h1")).getText().toString());
		        }
		        driver.switchTo().window(mainTabHandaler);
		        if(openNextPage() == -1) break;
	        }while (count < 5);
	        System.out.println( "End : "+calendar.getTime());
		  
		  driver.quit();
		  
		  
		}
	
	public static int openNextPage() {
		System.out.println(" <- openNextPage clicked");
		int responsepage = -1;
		
		String newPage = "//div[@class=\"pagination\"]/ul/li/a[@class=\"next ajax-page\"]";
		
		if(isElementPresent(By.xpath(newPage))){
			try {
			String nextPageUrl = driver.findElement(By.xpath(newPage)).getAttribute("href");
			System.out.println(" <- "+ nextPageUrl +" -> " + (nextPageUrl.indexOf("&page=")+6) + " : "+ nextPageUrl.length());
			String nextPageNumberString = nextPageUrl.substring(nextPageUrl.indexOf("&page=")+6, nextPageUrl.length());
			responsepage = Integer.parseInt(nextPageNumberString);
			System.out.println(responsepage);
			}catch( Exception e){
				System.out.println(" <- catch - >");
			}finally{
				driver.findElement(By.xpath(newPage)).click();	
			}
		}
		
		return responsepage;
		
	}
	private static boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	public static boolean newTabOpener() {
		//String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"t");
		//driver.findElement(By..tagName("html")).sendKeys(selectLinkOpeninNewTab);
		return true;
	}
	public static boolean newTabCloser() {
		//String selectLinkCloseNewTab = Keys.chord(Keys.CONTROL, "w");
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"w");
		//driver.findElement(By.tagName("html")).sendKeys(selectLinkCloseNewTab);
		return true;
	}
	public static boolean linkOpener(String profileUrl) {
		// driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.get(profileUrl);
		return true;
	}
	
	// success 30 may 2020
	private static void openNavLink() {
		//driver.get("D:\\Yellow Pages\\restaurants\\Restaurants details 1.html");
		//driver.get("D:\\Yellow Pages\\restaurants\\Restaurants details 2.html");
		driver.get("https://www.yellowpages.com/search?search_terms=restaurants&geo_location_terms=Los%20Angeles%2C%20CA&s=average_rating");
		
		for(int i=0; i < 2 ;i++) {
			
			String nextPage = getNextPageUrl();
			
			LinkedList<String> busUrls = (LinkedList<String>) getBusinessPageUrls();
			Iterator<String> it = busUrls.iterator();
			while (it.hasNext()) {
				String busUrl = (String) it.next();
				driver.get(busUrl);
				getData();
			}
			
			if(nextPage.length() > 1) {
				driver.get(nextPage);
			}
			else {
				break;
			}
			
		}
		//driver.get("https://www.yellowpages.com/los-angeles-ca/mip/si-laa-22674318");
		//getData();
//		WebElement webElement = driver.findElement(By.id(""));
//		webElement.sendKeys("Hello");
//		webElement.sendKeys(Keys.ENTER);

	}
	
	private static LinkedList<String> getBusinessPageUrls() {
		List<WebElement> webElements = new LinkedList<>();
		By items = By.xpath("//div[@class=\"result\"]//div[@class=\"info\"]//h2[@class=\"n\"]/a[@class=\"business-name\"]");
		webElements = driver.findElements(items);
		LinkedList<String> businessPageUrls = new LinkedList<>();
		Iterator<WebElement> it = webElements.iterator();
		while(it.hasNext()) {
			businessPageUrls.add(it.next().getAttribute("href"));
		}
		
		return businessPageUrls;
	}
	
	
	private static String getNextPageUrl() {
		
		WebDriverWait wait = new WebDriverWait(driver, 20);
		By nextPageItem = By.xpath("//div[@class=\"pagination\"]/ul/li/a[@class=\"next ajax-page\"]");
		
		//fullPageScroll();
		
		// get the "Add Item" element
		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(nextPageItem));
		System.out.println(element.getAttribute("href"));
		if(element.getAttribute("href").contains("www.yellowpages.com"))
			return element.getAttribute("href");
		else
			return "";
		//element.click();
		
		//trigger the reaload of the page
		//driver.findElement(nextPageItem).click();

		// wait the element "Add Item" to become stale
		//wait.until(ExpectedConditions.stalenessOf(element));

		// click on "Add Item" once the page is reloaded
		//wait.until(ExpectedConditions.presenceOfElementLocated(nextPageItem)).click();	
	}
	
	private static void getData() {
		WebElement webElement ;
		
		try {
			webElement = driver.findElement(By.xpath("//div[@class=\"sales-info\"]/h1"));
			System.out.println("Title: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/div[@class=\"contact\"]/h2[@class=\"address\"]"));
			System.out.println("Address: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/section[@class=\"ratings\"]/a[@class=\"yp-ratings\"]/div[contains(@class, 'rating-stars')]"));
			System.out.println("Rating_YP: \n " + webElement.getAttribute("class"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/section[@class=\"ratings\"]/a[@class=\"yp-ratings\"]/span[@class=\"count\"]"));
			System.out.println("Rating_YP: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/div[@class=\"contact\"]/p[@class=\"phone\"]"));
			System.out.println("Phone: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/div[@class=\"contact\"]/div[@class=\"time-info\"]/div"));
			System.out.println("Open_status_now: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@class=\"primary-info\"]/div[@class=\"years-in-business\"]/div[@class=\"count\"]/div[@class=\"number\"]"));
			System.out.println("Years_in_Business: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//div[@class=\"business-card-footer\"]/a[contains(@class,\"email-business\")]"));
			System.out.println("Email: \n " + webElement.getAttribute("href"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//div[@class=\"business-card-footer\"]/a[contains(@class,\"website-link\")]"));
			System.out.println("Website: \n " + webElement.getAttribute("href"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"general-info\"]"));
			System.out.println("General Info: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"open-hours\"]/div[@class=\"open-details\"]/table"));
			System.out.println("Regular Hours: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[preceding-sibling::dt[contains(text(), \"Services/Products\")]][1]"));
			System.out.println("Services/Products: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"brands\"]"));
			System.out.println("Brands: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"payment\"]"));
			System.out.println("Payment method: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[preceding-sibling::dt[contains(text(),'Price Range')]][1]"));
			System.out.println("Price_Range: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"location-description\"]"));
			System.out.println("Location: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"neighborhoods\"]"));
			System.out.println("Neighborhoods: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"aka\"]"));
			System.out.println("AKA: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"weblinks\"]"));
			System.out.println("Other_Links: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"categories\"]"));
			System.out.println("Categories: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webElement = driver.findElement(By.xpath("//section[@id=\"business-info\"]/dl/dd[@class=\"other-information\"]"));
			System.out.println("Other Information: \n " + webElement.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static String infoBtnCssSelector = "#topcard > div.module-footer > ul > li > button";
	static String infoBtnCssSelector1 = ".more-info-tray > table:nth-child(4) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)";

	public static String getPublicLink(String salesProLink) {
		if (salesProLink.toLowerCase().contains("linkedin.com/sales")) {
			driver.get(salesProLink);
			findAndClick(infoBtnCssSelector);
			WebElement element = driver.findElement(By.cssSelector(infoBtnCssSelector1));
			// System.out.println(element.getText());
			return element.getText();
		}
		return salesProLink;
	}

	public static void findAndClick(String selector) {
		By by = By.cssSelector(selector);
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		driver.findElement(by).click();
	}

	public static String getSourseCode() {
		fullPageScroll();
		String pageSource = "";
		pageSource = driver.getPageSource().toString();
		System.out.println(pageSource);
		return pageSource;
	}

	public static void fullPageScroll() {
		// https://stackoverflow.com/questions/42982950/how-to-scroll-down-the-page-till-bottomend-page-in-the-selenium-webdriver
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		try {
			jse.executeScript("scroll(0, 250);");
			Thread.sleep(1000);
			jse.executeScript("scroll(0, 550);");
			Thread.sleep(1000);
			jse.executeScript("scroll(0, 750);");
			Thread.sleep(500);
			jse.executeScript("scroll(0, 4550);");
			Thread.sleep(500);
			jse.executeScript("scroll(0, 6000);");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if I direct go to bottom of the page page full content don't load
		// jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	// running system installed firefox [Testing date : 10 Mar 2018]
	// requires : selenium-server-standalone-3.10.0.jar,
	// client-combined-3.10.0.jar & geckodriver.exe

	protected static void runfirefoxDefaultProfile() {

		ProfilesIni profile = new ProfilesIni();
		FirefoxProfile myprofile = profile.getProfile("default");

		myprofile.setPreference("network.proxy.type", 0);
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(FirefoxDriver.PROFILE, myprofile);
		
		System.setProperty("webdriver.gecko.driver",
				"Geckodriver\\v0.26.0-win64\\geckodriver.exe");

		driver = new FirefoxDriver(capabilities);
		//driver = new FirefoxDriver();
		driver.get("https://www.yellowpages.com");

	}

	protected static void runChoromeDefaultProfile() {
	

		Proxy proxy = new Proxy();
		proxy = null;
//		proxy.setHttpProxy("rdc-proxy.server.com:8080");
//		proxy.setSslProxy("rdc-proxy.server.com:8080");
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("proxy", proxy);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		
		System.setProperty("webdriver.chrome.driver", ".\\ChromeDriver\\win32_83.0.4103.39\\chromedriver.exe");

        driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);          
        driver.get("https://www.yellowpages.com/search?search_terms=restaurants&geo_location_terms=Los%20Angeles%2C%20CA&s=average_rating");
        driver.manage().window().maximize();
        String subWindowHandler = null;
        
        String mainTabHandaler = driver.getWindowHandle();
		//((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		((JavascriptExecutor) driver).executeScript("window.open();");
	   	  Set<String> handles = driver.getWindowHandles();
		  Iterator<String> iterator = handles.iterator();
		  while (iterator.hasNext()) {
		   subWindowHandler = iterator.next();
		  }
        
        int count = 0;
        do {
        	count++;
        	System.out.println("count : " + count++);	
	        LinkedList<String> list = getBusinessPageUrls();
	        Iterator<String> it = list.iterator();
	        driver.switchTo().window(subWindowHandler);  
	        while(it.hasNext()) {
	        	driver.get(it.next());
	        	getData();
	        	//System.out.println(driver.findElement(By.xpath("")).toString());
	        }
	        driver.switchTo().window(mainTabHandaler);
	        if(openNextPage() == -1) break;
        }while (count < 5);
        
	}
	
	

	
	private static void separatingCityStateZip() {
		String mydata = "Los Angeles, CA 90026";
		Pattern pattern = Pattern.compile("([^,]+), ([A-Z]{2}) (\\d{5})");
		Matcher matcher = pattern.matcher(mydata);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
		}
		
		
		//String mydata1 = "144 Kings Highway, S.W. Dover, DE 19901";
		//String mydata1 = "2299 Lewes-Georgetown Hwy, Georgetown, DE 19947";
		//String mydata1 = "580 North Dupont Highway, Dover, DE 19901";
		String mydata1 = "PO Box 778, Dover, DE 19901";
		Pattern pattern1 = Pattern.compile("([^,]+), ([^,]+), ([A-Z]{2}) (\\d{5})");
		Matcher matcher1 = pattern1.matcher(mydata1);
		if (matcher1.find()) {
			System.out.println(matcher1.group(1));
			System.out.println(matcher1.group(2));
			System.out.println(matcher1.group(3));
			System.out.println(matcher1.group(4));
		}
		
		
		
	}
	
}
