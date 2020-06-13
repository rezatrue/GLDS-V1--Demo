package scrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.Info;
import webhandler.FireFoxOperator;

public class DetailsParser extends Parser {
	//HtmlUnitDriver driver; 
	
	public DetailsParser() {
		super();
		//driver = new HtmlUnitDriver(true);
	}
	
	private boolean isElementPresent(By by) {
		try {
			FireFoxOperator.driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	
	private boolean setLoadTimeout(String url) {
		
		try {
			FireFoxOperator.driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
			FireFoxOperator.driver.get(url);
			return true;
		} catch (TimeoutException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void fullPageScroll() {
		JavascriptExecutor jse = (JavascriptExecutor) FireFoxOperator.driver;
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
			e.printStackTrace();
		}
	}
	
	public Info parseDetails(String url) {
		
		setLoadTimeout(url);
		fullPageScroll();
		Info info = new Info();
		System.out.println(url);
		info.setLink(url);
			
			try {
				String name = FireFoxOperator.driver.findElement(nameBy).getText();
				String man[] = name.split(" ");
 				info.setFirstName(man[0]);
 				info.setLastName(man[(man.length-1)]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if(isElementPresent(companyBy)) {
					
				try {
					info.setFirm(FireFoxOperator.driver.findElement(companyBy).getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					info.setPosition(FireFoxOperator.driver.findElement(titleBy).getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				try {
					info.setOffice(FireFoxOperator.driver.findElement(OfficeBy).getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				try {
					String data = FireFoxOperator.driver.findElement(emplyedDateBy).getText();
					String date[] = data.split("-");
					info.setYearOfJoining(date[0]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			//.. different settings
			if(isElementPresent(company1By)) {
				
				try {
					info.setFirm(FireFoxOperator.driver.findElement(company1By).getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					info.setPosition(FireFoxOperator.driver.findElement(title1By).getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				try {
					info.setOffice(FireFoxOperator.driver.findElement(Office1By).getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				int position = 1;
				try {
					List<WebElement> we = FireFoxOperator.driver.findElements(positionCountBy);
					position = we.size();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					String data = FireFoxOperator.driver.findElement(By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//ul/li["+position+"]//h4/span[preceding-sibling::span[contains(.,\"Dates Employed\")]]")).getText();
					String date[] = data.split("-");
					info.setYearOfJoining(date[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			
			
			try {
				info.setNameOfSchool(FireFoxOperator.driver.findElement(universityBy).getText());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			try {
				info.setNameOfLastDegree(FireFoxOperator.driver.findElement(degreeBy).getText());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			try {
				info.setYearTheyEarnedIt(FireFoxOperator.driver.findElement(yearBy).getText());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return info;
	}
	
	
	private static By nameBy = By.xpath("//ul[1][contains(@class,\"pv-top-card--list\")]/li[1]");
	private static By titleBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//div[contains(@class, \"pv-entity__summary-info\")]/h3");
	private static By companyBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//p[preceding-sibling::p[contains(.,\"Company Name\")]]");
	private static By emplyedDateBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//span[preceding-sibling::span[contains(.,\"Dates Employed\")]]");
	private static By OfficeBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//span[preceding-sibling::span[contains(.,\"Location\")]]");
	//private static By educationBy = By.xpath("//header[@class=\"pv-profile-section__card-header\"][child::h2[contains(text(), \"Education\")]]");
	
	private static By title1By = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//ul/li[1]//h3/span[preceding-sibling::span[contains(.,\"Title\")]]");
	private static By company1By = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//h3/span[preceding-sibling::span[contains(.,'Company Name')]]");
	
	private static By Office1By = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//ul/li[1]//h4/span[preceding-sibling::span[contains(.,\"Location\")]]");
	
	private static By positionCountBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//ul/li");
	//private static By emplyedDate1By = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Experience\")]]]/li[1]//ul/li["+2+"]//h4/span[preceding-sibling::span[contains(.,\"Dates Employed\")]]");
	
	private static By universityBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Education\")]]]/li[1]//h3[contains(@class,\"pv-entity__school-name\")]");
	private static By degreeBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Education\")]]]/li[1]//span[preceding-sibling::span[contains(.,\"Degree Name\")]]");
	private static By yearBy = By.xpath("//ul[preceding-sibling::header[@class=\"pv-profile-section__card-header\"][child::h2[contains(., \"Education\")]]]/li[1]//span/time[2]");
		
}
