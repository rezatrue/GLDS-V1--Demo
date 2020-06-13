package model;

public class Info {
	private String firstName, lastName, firm, position, office, yearOfJoining, nameOfLastDegree, nameOfSchool, yearTheyEarnedIt, Link;
	
	public Info() {
	}
	public Info(String firstName, String lastName, String firm, String position,
			String office, String yearOfJoining, String nameOfLastDegree, String nameOfSchool,
			String yearTheyEarnedIt, String Link) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.firm = firm;
		this.position = position;
		this.office = office;
		this.yearOfJoining = yearOfJoining;
		this.nameOfLastDegree = nameOfLastDegree;
		this.nameOfSchool = nameOfSchool;
		this.yearTheyEarnedIt = yearTheyEarnedIt;
		this.Link = Link;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirm() {
		return firm;
	}
	public void setFirm(String firm) {
		this.firm = firm;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getYearOfJoining() {
		return yearOfJoining;
	}
	public void setYearOfJoining(String yearOfJoining) {
		this.yearOfJoining = yearOfJoining;
	}
	public String getNameOfLastDegree() {
		return nameOfLastDegree;
	}
	public void setNameOfLastDegree(String nameOfLastDegree) {
		this.nameOfLastDegree = nameOfLastDegree;
	}
	public String getNameOfSchool() {
		return nameOfSchool;
	}
	public void setNameOfSchool(String nameOfSchool) {
		this.nameOfSchool = nameOfSchool;
	}
	public String getYearTheyEarnedIt() {
		return yearTheyEarnedIt;
	}
	public void setYearTheyEarnedIt(String yearTheyEarnedIt) {
		this.yearTheyEarnedIt = yearTheyEarnedIt;
	}
	public String getLink() {
		return Link;
	}
	public void setLink(String link) {
		Link = link;
	}
	
}
