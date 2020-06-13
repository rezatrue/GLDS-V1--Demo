package scrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import db.LocalDBHandler;
import model.Info;

public class CsvGenerator {

	public CsvGenerator() {
	}

	public int listtoCsv(String keyword, LinkedList<Info> list) {
		
		int count = 0;
		
		System.out.println("list size 1: " + list);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Calendar cal = Calendar.getInstance();
		String fileName = dateFormat.format(cal.getTime());

		FileWriter writer = null;
		try {
			writer = new FileWriter("GLDS_" + keyword + "_list_" + fileName + ".csv");

			writer.append("First Name");
			writer.append(",");
			writer.append("Last Name");
			writer.append(",");
			writer.append("Firm");
			writer.append(",");
			writer.append("Position");
			writer.append(",");
			writer.append("Office");
			writer.append(",");
			writer.append("Year of Joining");
			writer.append(",");
			writer.append("Name of last degree");
			writer.append(",");
			writer.append("Name of school");
			writer.append(",");
			writer.append("Year they earned it");
			writer.append(",");
			writer.append("Link");
			writer.append(",");
			writer.append("\n");

			System.out.println(" -- out size-- "+ list.size());
			if(list.size() > 0) {
				Iterator<Info> it = list.iterator();
	
				while (it.hasNext()) {
					Info info = (Info) it.next();
					System.out.println(info.getLink());
					System.out.println(info.getFirstName() + " "+ info.getLastName());
					writer.append(formatForCsv(info.getFirstName()));
					writer.append(",");
					writer.append(formatForCsv(info.getLastName()));
					writer.append(",");
					writer.append(formatForCsv(info.getFirm()));
					writer.append(",");
					writer.append(formatForCsv(info.getPosition()));
					writer.append(",");
					writer.append(formatForCsv(info.getOffice()));
					writer.append(",");
					writer.append(formatForCsv(info.getYearOfJoining()));
					writer.append(",");
					writer.append(formatForCsv(info.getNameOfLastDegree()));
					writer.append(",");
					writer.append(formatForCsv(info.getNameOfSchool()));
					writer.append(",");
					writer.append(formatForCsv(info.getYearTheyEarnedIt()));
					writer.append(",");
					writer.append(info.getLink());					
					writer.append("\n");
					count++;
				}
			}

		} catch (IOException e) {
			System.out.println(" csv g Error : " + e.getMessage());
		}finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return count;
	} 

	protected String formatForCsv(String text) {
		String newText = "";
		System.out.println("text - "+ text);
		if(!(text == null))
			newText = text.replaceAll("(\\S+)", "$1");
		
		if (newText.contains(",")) {
			if (!newText.startsWith("\""))
				newText = "\"" + newText;
			if (!newText.endsWith("\""))
				newText = newText + "\"";
		}
		return newText;
	}
	
	protected String csvformatDevider(String text) {
		String newText; 
		try {
		newText = text.replaceAll("[\\t\\n\\r]", " | ") ;
		
		if (newText.contains(","))
			if (!newText.startsWith("\"") && !newText.endsWith("\""))
				newText = "\"" + newText + "\"";
		}catch (Exception e) {
			return "";
		}
		return newText;
	}
	
	

}
