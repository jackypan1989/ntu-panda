package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExtractIPCMain {
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null; 
	
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
	}//end innovation constructor
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

	public static void main(String[] args) throws SQLException{
		ExtractIPCMain ec = new ExtractIPCMain();
		ec.Open();
		
		int year;
		for(year = 1976 ; year <= 2009 ; year++)
		{
			String selectSQL = "SELECT `Patent_id`,`Current U.S. Class` FROM uspto_"+ year+"";
			ec.statement = ec.connection.createStatement();
			ec.resultSet = ec.statement.executeQuery(selectSQL);			    
			if(ec.resultSet.absolute(1)== true)
			    break;	    	
		}
		
		ec.Close();
	}
}
