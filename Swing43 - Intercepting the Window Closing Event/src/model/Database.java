package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

public class Database {
	
	private LinkedList<Person> people;
	private Connection conn;

	
	public Database() {
		people = new LinkedList<Person>();
	}
	
	public void addPerson(Person person) {
		people.add(person);
	}

	public List<Person> getPeople() {
		return Collections.unmodifiableList(people);
	}
	
	public void saveToFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		Person[] persons = people.toArray(new Person[people.size()]);
		
		oos.writeObject(persons);
		oos.close();
		
	}
	
	public void loadFromFile(File file)  throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		try {
			Person[] persons = (Person[])ois.readObject();
			
			people.clear();
			people.addAll(Arrays.asList(persons));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		ois.close();
	}

	public void removePerson(int row) {
		people.remove(row);		
	}

	public void connect(String user, String password) throws Exception {
		
		if (conn != null) return;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new Exception("Driver not found");
		}

		String connString = "jdbc:mysql://localhost:3306/test_db?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		conn = DriverManager.getConnection(connString, user, password);
		
		

		
		System.out.println("Connected: " + conn);
	}

	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("Connection closed.");
			} catch (SQLException e) {
				System.out.println("Cannot close connection...");
			}
		}
	}
	
	
	public void save() throws SQLException {
		
		String checkSql = "SELECT COUNT(*) AS COUNT FROM people WHERE id=?";
		PreparedStatement checkStmt = conn.prepareStatement(checkSql);
		
		String insertSql = "INSERT INTO people (id, name, age, employment_status, taxid, us_citizen, gender, occupation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement insertStatement = conn.prepareStatement(insertSql);
		
		String updateSql = "UPDATE people SET name=?, age=?, employment_status=?, taxid=?, us_citizen=?, gender=?, occupation=? WHERE id=?";
		PreparedStatement updateStatement = conn.prepareStatement(updateSql);
		
		for (Person person: people) {
			int id = person.getId();
			String name = person.getName();
			String occupation = person.getOccupation();
			AgeCategory age = person.getAgeCategory();
			EmploymentCategory emp = person.getEmpCat();
			String tax = person.getTaxID();
			boolean isUs = person.isUsCitizen();
			Gender gender = person.getGender();
			
			checkStmt.setInt(1, id);	//replace 1st wildcard with id
			
			ResultSet checkResult = checkStmt.executeQuery();
			checkResult.next();
			
			int count = checkResult.getInt(1);
			
			if (count == 0 ) {
				System.out.println("Inserting person with ID = " + id);

				int col = 1;
				insertStatement.setInt(col++, id);
				insertStatement.setString(col++, name);
				insertStatement.setString(col++, age.name());
				insertStatement.setString(col++, emp.name());
				insertStatement.setString(col++, tax);
				insertStatement.setBoolean(col++, isUs);
				insertStatement.setString(col++, gender.name());
				insertStatement.setString(col++, occupation);

				insertStatement.executeUpdate();
			} else {
				System.out.println("Updating person with ID = " + id);
				
				int col = 1;
				updateStatement.setString(col++, name);
				updateStatement.setString(col++, age.name());
				updateStatement.setString(col++, emp.name());
				updateStatement.setString(col++, tax);
				updateStatement.setBoolean(col++, isUs);
				updateStatement.setString(col++, gender.name());
				updateStatement.setString(col++, occupation);
				updateStatement.setInt(col++, id);

				updateStatement.executeUpdate();
			}
			//System.out.println("Count for person with id " + id + " = " + count);
			
		}
		
		updateStatement.close();
		insertStatement.close();
		checkStmt.close();
	}
	
	public void load() throws SQLException {
		people.clear();
		
		String sql = "SELECT id, name, age, employment_status, taxid, us_citizen, gender, occupation FROM people ORDER BY name";
		Statement selectStatement = conn.createStatement();
		
		ResultSet results = selectStatement.executeQuery(sql);
		
		while (results.next()) {
			int id = results.getInt("id");
			String name = results.getString("name");
			String age = results.getString("age");
			String emp = results.getString("employment_status");
			String tax = results.getString("taxid");
			boolean isUs = results.getBoolean("us_citizen");
			String gender = results.getString("gender");
			String occupation = results.getString("occupation");
			
			Person person = new Person(id, name, occupation, AgeCategory.valueOf(age),
					EmploymentCategory.valueOf(emp), tax, isUs, Gender.valueOf(gender));
			System.out.println(person);
			
			people.add(person);
		}
		
		results.close();
		selectStatement.close();
		
	}
	

}
