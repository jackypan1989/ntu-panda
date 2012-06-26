package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
/*
 * Author: kobuta
 * Number of variables: 2
 * 1.BwdSelfCitationRate = backward self-citation rate
 * 2.NumberOfBwd = number of backward citations
 * No.1 & 2 use SelfCitationRate(PatentID).
 */

public class BackwardCitation {

	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/mypaper";   //which database
	static final String DATABASE_URL2 = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	static int OLDEST_YEAR = 1976;
	static int YOUNGEST_YEAR = 2009;
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null; 
	private Connection connection2 = null;
	private Statement statement2 = null;
	private ResultSet resultSet2 = null; 
	
	private int focal_year;
	private String focal_assignee = null;
	private String bwd_result = null;
	private String[] bwd_patent = null;
	private float BwdSelfCitationRate;
	int NumberOfBwd = 0;
	
	/*
	 * connect to database
	 */
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
	/*
	 * select data from database
	 */
	public void GetTable(String pid) throws SQLException
	{//search table content_1976-2009
		int year;
		
		for(year = OLDEST_YEAR ; year <= YOUNGEST_YEAR ; year++)
		{
			
			String selectSQL = "SELECT `References Cited` FROM content_"+ year +" WHERE `Patent_id`='"+pid+"'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);			    
			if(resultSet.absolute(1)== true){
			    focal_year = year;
				break;		    
			}	
		}
	}
	/*
	 * main part of this program
	 */
	public String GetAssignee(int pYear, String pID) throws SQLException{
			String selectSQL_BwdAss = "SELECT `Assignee` FROM content_"+ pYear +" WHERE `Patent_id`='"+pID+"'";
			ResultSet resultBwdAss = statement.executeQuery(selectSQL_BwdAss);
			if(resultBwdAss.absolute(1) == true){
				String tmpASS = resultBwdAss.getString("Assignee");
				System.out.println("GetAssignee : "+tmpASS);
				return tmpASS;
			}	
			else{
				return "NULL";
			}
		
	}
	public void SelfCitationRate( String PatentID) throws SQLException{
		
		GetTable(PatentID);
		if(resultSet.absolute(1) != false){ //if patent is in this database
			bwd_result = resultSet.getString("References Cited");
			
			if(bwd_result==null || bwd_result.equals("")){
				//if focal patent's "references cited" is null, it means focal patent didn't cite other patent or has missing data.
				BwdSelfCitationRate =0;
				NumberOfBwd = 0;
			}
			else{
				bwd_patent = bwd_result.split(";");
				NumberOfBwd = bwd_patent.length;
				if(focal_year == OLDEST_YEAR){  //system have no reference information before 1976
					BwdSelfCitationRate = -1;
				}
				else{
					focal_assignee = GetAssignee(focal_year, PatentID);
					
					if (focal_assignee == null || focal_assignee.equals("")) {//if focal patent doesn't have assignee.
						BwdSelfCitationRate = 0;
					}
					else {
						
						int SameAssCount = 0;
						for (int i=0; i < NumberOfBwd; i++) {// for all backward-citation patents find its assignee and compare to the focal one.
							String bwd_assignee = null;
							for (int year = OLDEST_YEAR; year <= focal_year; year++) {
								bwd_assignee = GetAssignee(year, bwd_patent[i]);
								if (focal_assignee.equals(bwd_assignee) && !bwd_assignee.equals("NULL")) {
									SameAssCount++;
									break;
								}		
							}//end for
						}//end for
						
						BwdSelfCitationRate = (float) SameAssCount / NumberOfBwd;
						SameAssCount =0;
					}//end else
				}//end else	
			}//end else
		}
		else{// database doesn't have focal patent
			focal_year = -1;
			NumberOfBwd = -1;
			BwdSelfCitationRate = -1;
		}	
	}
	/*
	 * Update variables to database
	 */
	public void DBUpdate()throws SQLException {

		Open2();
		Open();
			
		String selectSQL = "SELECT Patent_id FROM attacker_expert";
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
			SelfCitationRate(patentID);
			statement2.executeUpdate("UPDATE attacker_expert SET bwd_selfcitation_rate = '"+BwdSelfCitationRate+"', num_of_bwd_citations = '"+ NumberOfBwd +"', Patent_year ='"+focal_year+"' WHERE Patent_id = '"+patentID+"'");
		}
				
		System.out.println("Program End");
		Close();
		Close2();
	}
	
	public static void main(String[] args) throws SQLException {
		/*BackwardCitation BW = new BackwardCitation();
		BW.DBUpdate();
		BW.Open();
		BW.Open2();
		BW.SelfCitationRate("D403673");
		BW.Close();
		BW.Close2();*/
	}//end main

}//end BwdCiation
