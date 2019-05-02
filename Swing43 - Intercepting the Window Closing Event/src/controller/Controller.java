package controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import gui.FormEvent;
import model.AgeCategory;
import model.Database;
import model.EmploymentCategory;
import model.Gender;
import model.Person;

public class Controller {

	Database db = new Database();
	
	public List<Person> getPeople() {
		return db.getPeople();
	}

	public void addPerson(FormEvent ev) {
		String name = ev.getName();
		String occupation = ev.getOccupation();
		int ageCat = ev.getageCategory();
		String empCat = ev.getEmploymenCategory();
		boolean isUs = ev.isUsCitizen();
		String taxId = ev.getTaxID();
		String gender = ev.getGender();

		AgeCategory ageCategory;

		switch (ageCat) {
		case 0:
			ageCategory = AgeCategory.child;
			break;
		case 1:
			ageCategory = AgeCategory.adult;
			break;
		case 2:
			ageCategory = AgeCategory.senior;
			break;
		default:
			ageCategory = null;
		}
		
		
		EmploymentCategory empCategory;
		
		if (empCat.equals("Employed")) {
			empCategory = EmploymentCategory.employed;
		} else if (empCat.equals("Self-employed")) {
			empCategory = EmploymentCategory.selfEmployed;
		} else if (empCat.equals("Unemployed")) {
			empCategory = EmploymentCategory.unemployed;
		} else {
			empCategory = EmploymentCategory.other;
			System.err.println(empCat);
		}
		
		Gender genCat;
		
		if (gender.equals("male")) {
			genCat = Gender.male;
		} else if (gender.equals("female")) {
			genCat = Gender.female;
		} else {
			genCat = null;
		}


		Person person = new Person(name, occupation, ageCategory, empCategory, taxId, isUs, genCat);

		db.addPerson(person);
	}
	
	public void saveToFile(File file) throws IOException {
		db.saveToFile(file);
	}
	
	public void loadFromFile(File file) throws IOException {
		db.loadFromFile(file);
	}

	public void removePerson(int row) {
		db.removePerson(row);		
	}
	
	public void save() throws SQLException {
		db.save();
	}
	
	public void disconnect() {
		db.disconnect();
	}
	
	public void connect(String user, String password) throws Exception {
		db.connect(user, password);
	}
	
	public void load() throws SQLException {
		db.load();
	}
}
