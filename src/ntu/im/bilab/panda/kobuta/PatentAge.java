package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;



public class PatentAge {
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/mypaper";   //which database
	static final String DATABASE_URL2 = "jdbc:mysql://140.112.107.207/patent_value";   //which database
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
	private int PatentDay =0;
	
	
	public int GetPatentAgeSinceIssuedDate(){
		return PatentDay;
	}
	public int GetApprovalTime(){
		return AppTime;
	}
	public int GetNumOfInventors(){
		return NumOfInventors;
	}
	public void Open(){
		try {
			Class.forName( DRIVER );
			connection = DriverManager.getConnection( DATABASE_URL, USERNAME, PASSWORD );
			statement = connection.createStatement();
			
		}//end try 
		catch( SQLException sqlException )
		{
			sqlException.printStackTrace();
			System.exit(1);
		}//end catch
		catch ( ClassNotFoundException classNotFound ) 
		{
			classNotFound.printStackTrace();
		}//end catch
	}
	public void Close(){
	    try
	    {
	      if(connection!=null)
	      {
	        connection.close();
	        connection = null;
	      }
	    }
	    catch(SQLException e)
	    {
	      System.out.println("Close Exception :" + e.toString());
	    }
	  }
	public void Open2(){
		try {
			Class.forName( DRIVER );
			connection2 = DriverManager.getConnection( DATABASE_URL2, USERNAME, PASSWORD );
			statement2 = connection2.createStatement();
			
		}//end try 
		catch( SQLException sqlException )
		{
			sqlException.printStackTrace();
			System.exit(1);
		}//end catch
		catch ( ClassNotFoundException classNotFound ) 
		{
			classNotFound.printStackTrace();
		}//end catch
	}
	public void Close2(){
		    try
		    {
		      if(connection2!=null)
		      {
		        connection2.close();
		        connection2 = null;
		      }
		    }
		    catch(SQLException e)
		    {
		      System.out.println("Close Exception :" + e.toString());
		    }
	 }

	public void GetTable(String pid) throws SQLException{
		int year;
		for(year = OLDEST_YEAR ; year <= YOUNGEST_YEAR ; year++)
		{
			String selectSQL = "SELECT `Issued_Date`,`Issued_Year` FROM content_"+ year +" WHERE `Patent_id`='"+pid+"'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);			    
			if(resultSet.absolute(1)== true)
			    break;	    	
		}
	}
	public void countPatentAge(String PatentID) throws ParseException, SQLException{
		GetTable(PatentID);
		if(resultSet.absolute(1) !=  false){
			 issued_date = resultSet.getString("Issued_Date");
			 issued_date = issued_date.replace("  ","");
			 issued_year = resultSet.getString("Issued_Year");
			 issued = issued_date + ", " + issued_year;
			 System.out.println(issued);
			 
			 DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);

			 Date d1 = df.parse(issued); 
			 Date d2 = df.parse(DATACOLLECT_DATE);
			 long t1 = d2.getTime() - d1.getTime();
			 long time = 1000*3600*24;
					
			 if(t1/time > 0){
				 PatentDay = (int) (t1/time);
			 }
			 else{
				 System.out.println("Unable to caculate... "); 
			 }	
		}
		else{
			PatentDay = -1;
		}
		
	
	}
	//Num of Inventors + Approval time 
	public void GetTable2(String pid) throws SQLException{
		int year;
		for(year = OLDEST_YEAR ; year <= YOUNGEST_YEAR; year++)
		{
			String selectSQL = "SELECT `Inventors` FROM uspto_"+ year +" WHERE `Patent_id`='"+pid+"'";
			statement2 = connection2.createStatement();
			resultSet_inventors = statement2.executeQuery(selectSQL);			    
			if(resultSet_inventors.absolute(1)== true)
			    break;	    	
		}
	}
	public void countApprovalTime() throws ParseException{
		 DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
		 Date d1 = df.parse(applied_date); 
		 Date d2 = df.parse(issued);
		 long t1 = d2.getTime() - d1.getTime();
		 long time = 1000*3600*24;
				
		 if(t1/time > 0){
			 AppTime = (int) (t1/time);
		 }
		 else{
			 System.out.println("Unable to caculate... "); 
		 }	
	}
	public void parseInventors(String PatentID) throws SQLException, ParseException{
		GetTable2(PatentID);
		if(resultSet_inventors.absolute(1) != false){
			String result_all = resultSet_inventors.getString("Inventors");
			String[] s = result_all.split("Filed:  ");
			if(s.length != 1){
				applied_date = s[1].replace("                        ","");
				//System.out.println("DDD"+applied_date);
				countApprovalTime();
				s = s[0].split("Assignee:  ");
				inventors = s[0].split(";");
				NumOfInventors = inventors.length;
			}
			else{
				AppTime = -1;
			}
			
			//System.out.println(NumOfInventors);
			//System.out.println(AppTime);
		}//end if
		else{
			AppTime = -1;
			NumOfInventors = -1;
		}
	}
	public void DBUpdate()throws SQLException, ParseException {

		Open2();
		Open();
			
		String selectSQL = "SELECT Patent_id FROM licensability_negative";
		statement2 = connection2.createStatement();
		resultSet2 = statement2.executeQuery(selectSQL);			    
		List<String> patent_id_list = new LinkedList<String>(); 
				
		while(resultSet2.next()) {
			patent_id_list.add(resultSet2.getString("Patent_id").trim());
		}
				
		Iterator<String> pItr = patent_id_list.iterator();
		while(pItr.hasNext()){
			String patentID = pItr.next();
			System.out.println("For patent "+patentID +": ");		
			countPatentAge(patentID);
			parseInventors(patentID);
			//System.out.println(PatentDay);
			statement2.executeUpdate("UPDATE licensability_negative SET num_of_inventors = '" + NumOfInventors + "' , approval_time = '"+ AppTime +"' WHERE Patent_id = '"+patentID+"'");
			//statement2.executeUpdate("UPDATE attacker_expert SET approval_time = '"+ AppTime +"',patent_age_since_issued_date = '" + PatentDay + "' WHERE Patent_id = '"+patentID+"'");
		}
				
		System.out.println("Program End");
		Close();
		Close2();
	}
	
	public static void main(String[] args) throws ParseException, SQLException {
		PatentAge pa = new PatentAge();
		pa.DBUpdate();
		/*pa.Open();
		pa.Open2();
		pa.countPatentAge("RE29501");
		pa.parseInventors("RE29501");
		pa.Close();
		pa.Close2();*/
	}//end main
	
	
	

}// end class