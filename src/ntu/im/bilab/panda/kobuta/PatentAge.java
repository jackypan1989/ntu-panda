package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;

/*
 * Author: kobuta
 * Number of variables: 3
 * 1.PatentAgeIssued = Patent_age_since_issued_data(data collection date - issued date)
 * 2.PatentAgeIssued = Patent_age_since_issued_data(data collection date - issued date)
 * 3.AppTime = approval time(issued date - applied date)
 * 4.NumOfInventors = number of inventors
 * No.1 use CountPatentAge(PatentID)
 * No.2 use ParseInventors(PatentID)
 * No.3 & 4 use ParseInventors(PatentID)
 */

public class PatentAge {
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/mypaper"; // which																			// database
	static final String DATABASE_URL2 = "jdbc:mysql://140.112.107.207/patent_value"; // which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";

	static int OLDEST_YEAR = 1976;
	static int YOUNGEST_YEAR = 2009;

	private String DATACOLLECT_DATE = "Jan 1, 2010";

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private Connection connection2 = null;
	private Statement statement2 = null;
	private ResultSet resultSet2 = null;
	private ResultSet resultSet_inventors = null;

	private String issued_date;
	private String issued_year;
	private String issued;
	private String applied_date;
	private String[] inventors;
	private int NumOfInventors;
	private int AppTime;
	private int PatentAgeIssued = 0;
	private int PatentAgeApplied = 0;
	
	public PatentAge(String PatentID) throws ParseException, SQLException{
		CountPatentAge(PatentID);
		ParseInventors(PatentID);
	}
	
	public int GetPatentAgeIssued(){
		return PatentAgeIssued;
	}
	public int GetPatentAgeApplied(){
		return PatentAgeIssued;
	}
	public int GetNumOfInventors(){
		return NumOfInventors;
	}
	public int GetApprovalTime(){
		return AppTime;
	}
	/*
	 * connect to database
	 */
	public void Open() {
		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(DATABASE_URL, USERNAME,
					PASSWORD);
			statement = connection.createStatement();

		}// end try
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.exit(1);
		}// end catch
		catch (ClassNotFoundException classNotFound) {
			classNotFound.printStackTrace();
		}// end catch
	}

	public void Close() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	public void Open2() {
		try {
			Class.forName(DRIVER);
			connection2 = DriverManager.getConnection(DATABASE_URL2, USERNAME,
					PASSWORD);
			statement2 = connection2.createStatement();

		}// end try
		catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.exit(1);
		}// end catch
		catch (ClassNotFoundException classNotFound) {
			classNotFound.printStackTrace();
		}// end catch
	}

	public void Close2() {
		try {
			if (connection2 != null) {
				connection2.close();
				connection2 = null;
			}
		} catch (SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}

	/*
	 * select data from database
	 */
	public void GetTable(String pid) throws SQLException {
		int year;
		for (year = OLDEST_YEAR; year <= YOUNGEST_YEAR; year++) {
			String selectSQL = "SELECT `Issued_Date`,`Issued_Year` FROM content_"
					+ year + " WHERE `Patent_id`='" + pid + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);
			if (resultSet.absolute(1) == true)
				break;
		}
	}

	public void GetTable2(String pid) throws SQLException {// for counting
															// NumOfInvnetors
															// and AppTime
		int year;
		for (year = OLDEST_YEAR; year <= YOUNGEST_YEAR; year++) {
			String selectSQL = "SELECT `Inventors` FROM uspto_" + year
					+ " WHERE `Patent_id`='" + pid + "'";
			statement2 = connection2.createStatement();
			resultSet_inventors = statement2.executeQuery(selectSQL);
			if (resultSet_inventors.absolute(1) == true)
				break;
		}
	}

	/*
	 * main part of this program
	 */
	public void CountPatentAge(String PatentID) throws ParseException,
			SQLException {

		GetTable(PatentID);
		if (resultSet.absolute(1) != false) {
			issued_date = resultSet.getString("Issued_Date");
			issued_date = issued_date.replace("  ", "");
			issued_year = resultSet.getString("Issued_Year");
			issued = issued_date + ", " + issued_year;

			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
					Locale.ENGLISH);

			Date d1 = df.parse(issued);
			Date d2 = df.parse(DATACOLLECT_DATE);
			long t1 = d2.getTime() - d1.getTime();
			long time = 1000 * 3600 * 24;

			if (t1 / time > 0) {
				PatentAgeIssued = (int) (t1 / time);
			} else {
				System.out.println("Unable to caculate... ");
			}
		} else {
			PatentAgeIssued = -1;
		}
	}

	public void countApprovalTime() throws ParseException {
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
				Locale.ENGLISH);
		Date d1 = df.parse(applied_date);
		Date d2 = df.parse(issued);
		long t1 = d2.getTime() - d1.getTime();
		long time = 1000 * 3600 * 24;

		if (t1 / time > 0) {
			AppTime = (int) (t1 / time);
		} else {
			System.out.println("Unable to caculate... ");
		}
	}

	public void countPatentAgeApplied() throws ParseException {
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
				Locale.ENGLISH);
		Date d1 = df.parse(applied_date);
		Date d2 = df.parse(DATACOLLECT_DATE);
		long t1 = d2.getTime() - d1.getTime();
		long time = 1000 * 3600 * 24;

		if (t1 / time > 0) {
			PatentAgeApplied = (int) (t1 / time);
		} else {
			System.out.println("Unable to caculate... ");
		}
	}

	public void ParseInventors(String PatentID) throws SQLException,
			ParseException {

		GetTable2(PatentID);
		if (resultSet_inventors.absolute(1) != false) {
			String result_all = resultSet_inventors.getString("Inventors");
			String[] s = result_all.split("Filed:  ");
			if (s.length != 1) {
				applied_date = s[1].replace("                        ", "");
				countApprovalTime();
				countPatentAgeApplied();
				s = s[0].split("Assignee:  ");
				inventors = s[0].split(";");
				NumOfInventors = inventors.length;
			} else {
				AppTime = -1;
				PatentAgeApplied = -1;
			}
		}// end if
		else {
			AppTime = -1;
			NumOfInventors = -1;
			PatentAgeApplied = -1;
		}
	}

	/*
	 * Update variables to database
	 */
	public void DBUpdate() throws SQLException, ParseException {

		Open2();
		Open();

		String selectSQL = "SELECT Patent_id FROM attacker_expert";
		statement2 = connection2.createStatement();
		resultSet2 = statement2.executeQuery(selectSQL);
		List<String> patent_id_list = new LinkedList<String>();

		while (resultSet2.next()) {
			patent_id_list.add(resultSet2.getString("Patent_id").trim());
		}

		Iterator<String> pItr = patent_id_list.iterator();
		while (pItr.hasNext()) {
			String patentID = pItr.next();
			System.out.println("For patent " + patentID + ": ");
			CountPatentAge(patentID);
			ParseInventors(patentID);
			// System.out.println(PatentAgeIssued);

			/*
			 * statement2
			 * .executeUpdate("UPDATE value_expert SET approval_time = '" +
			 * AppTime + "' WHERE Patent_id = '" + patentID + "'");
			 */
			statement2
					.executeUpdate("UPDATE attacker_expert SET `patent_age_since_application_date` = '"
							+ PatentAgeApplied
							+ "', `patent_age_since_issued_date` = '"
							+ PatentAgeIssued
							+ "' WHERE Patent_id = '"
							+ patentID + "'");

		}

		System.out.println("Program End");
		Close();
		Close2();
	}

	public static void main(String[] args) throws ParseException, SQLException {

		//PatentAge pa = new PatentAge();
		//pa.DBUpdate();
		/*
		 * pa.Open(); pa.Open2();
		 * 
		 * pa.CountPatentAge("RE29501"); pa.ParseInventors("RE29501");
		 * pa.Close(); pa.Close2();
		 */

	}// end main

}// end class