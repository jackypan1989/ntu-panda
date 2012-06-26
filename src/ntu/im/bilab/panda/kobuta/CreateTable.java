package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/*
 * create table and insert data to patent_mainclass_? in "mypaper"
 */
public class CreateTable {
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	static final String DATABASE_URL2 = "jdbc:mysql://140.112.107.207/mypaper";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private Connection connection2 = null;
	private Statement statement2 = null;
	//private ResultSet resultSet2 = null;
	private String result_class;
	private String IPC_mainclass;
	
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
	public void extractmainclass(String pid, int pyear) throws SQLException{
		
		String selectSQL = "SELECT `Current U.S. Class` FROM uspto_"+ pyear +" WHERE `Patent_id`='"+pid+"'";
		
		resultSet = statement.executeQuery(selectSQL);		

		if(resultSet.absolute(1) != false){
			result_class = resultSet.getString("Current U.S. Class");
			result_class = result_class.replace("&nbsp"," ");//6988080
			result_class = result_class.replace("    ","  ");
			result_class = result_class.replace("   ","  ");
			String[] s = result_class.split("Field of Search: ");
			                         //"Current International Class:    "
			                         //"Current International Class:   "
			String[] s1 = s[0].split("Current International Class:  ");//6988080
			String[] s2 = s[0].split("Intern'l Class: ");//6868619
			if(s1.length != 1){
				String[] sc = s1[1].split(" ");
				if(sc[0].length() == 4 && sc[0].matches("[a-zA-Z0-9]*") ){
					IPC_mainclass = sc[0];	
				}
				else{
					IPC_mainclass = "null";
				}
			}
			else if(s2.length != 1){// RE38746
				String[] sc = s2[1].split(" ");
				if(sc[0].length() == 4 && sc[0].matches("[a-zA-Z0-9]*") ){
					IPC_mainclass = sc[0];
				}
				else{
					IPC_mainclass = "null";
				}	
			}//end else if
			else{//if focal patent doesn't have Current International Class & Intern'l Class.
				IPC_mainclass = "null";
			}
		}
		else{
			IPC_mainclass = "null";
		}
	}
	public void DBUpdate()throws SQLException, ParseException {

		Open2();
		Open();
		statement = connection.createStatement();
		statement2 = connection2.createStatement();
		int year;
		for(year = 1993; year <= 1997; year++){
			//System.out.println(year);
		//year = 1992;
			String tablename = "uspto_"+year;
			String tablename2 = "patent-mainclass_"+year;
			
			String selectSQL = "SELECT `Patent_id` FROM "+tablename+"";
			ResultSet resultSet2 = statement.executeQuery(selectSQL);			    
			List<String> patent_id_list = new LinkedList<String>(); 
					
			while(resultSet2.next()) {
				patent_id_list.add(resultSet2.getString("Patent_id").trim());
			}
			System.out.println(patent_id_list.size());	
			Iterator<String> pItr = patent_id_list.iterator();
			while(pItr.hasNext()){
				String patentID = pItr.next();
				System.out.println("For patent "+patentID +": ");		
				extractmainclass(patentID,year);
				System.out.println(IPC_mainclass);
				//System.out.println(PatentDay);
				
				statement2.execute("INSERT INTO `"+tablename2+"` (`Patent_id`, `main_IPC_class`) VALUES ( '"+patentID+"', '"+IPC_mainclass+"')");
			}//end while
		}//end for
		
				
		System.out.println("Program End");
		Close();
		Close2();
	}
	/*CREATETABLE`patent-mainclass_1976` (
			 `Patent_id` VARCHAR( 11)NOTNULL DEFAULT'0',
			 `main_IPC_class` VARCHAR( 11)NOTNULL DEFAULT'0'
			) ENGINE=MYISAM ;*/
	public static void main(String[] args) throws ParseException, SQLException {
	
		CreateTable ct = new CreateTable();
		ct.DBUpdate();
	}//end main
}
