package scrapper;

import java.util.LinkedList;

import model.ExtractionType;
import model.Info;

public class Parser {
	
	protected LinkedList<Info> list = null;
	protected Info info = null;

	public LinkedList<Info> parse() {
		return list;
	}


	public Info parseDetails(String url) {
		return info;
	}
	
}
