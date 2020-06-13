package scrapper;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import model.Info;
import webhandler.FireFoxOperator;

public class ListParser extends Parser  {

	public ListParser(){
		super();
	}
	
	private boolean isElementPresent(By by) {
		try {
			FireFoxOperator.driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	public LinkedList<Info> parse(){
		list = new LinkedList<Info>();
		
		//div[@class="result"]
		String personInfoXpath = "//ul[contains(@class,\"search-results__list\")]/li";
		if(!isElementPresent(By.xpath(personInfoXpath))) return list;
		
		List<WebElement> webElements = FireFoxOperator.driver.findElements(By.xpath(personInfoXpath)); 
		
		Iterator<WebElement> it = webElements.iterator();
		
		while(it.hasNext()) {
			Info info = new Info();
			WebElement personElement = it.next();
			if(isPresent(personElement, personNameBy)) {
				String fullName = personElement.findElement(personNameBy).getText();
				String name[] = fullName.split(" ");
				info.setFirstName(name[0]);
				info.setLastName(name[1]);
			}
				
			if(isPresent(personElement, linkBy)) {
				String link = personElement.findElement(linkBy).getAttribute("href");
				info.setLink(link);
			}
				
			if(isPresent(personElement, titleBy)) {
				try {
				String title = personElement.findElement(titleBy).getText();
				info.setPosition(title);
				}catch (Exception e){}
			}
				
			if(isPresent(personElement, addressBy))
				info.setOffice(personElement.findElement(addressBy).getText());
								
			if(info != null) 
			   list.add(info);
		}
		
	return list;
	}
	
	private boolean isPresent(WebElement element, By by) {
		try {
			element.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	private static By personNameBy = By.xpath(".//h3//span[contains(@class,\"actor-name\")]");
	private static By linkBy = By.xpath(".//a[parent::div[contains(@class,\"search-result__info\")]]");
	private static By titleBy = By.xpath(".//p[contains(@class,\"subline-level-1\")]");
	private static By addressBy = By.xpath(".//p[contains(@class,\"subline-level-2\")]");
	
	
}
