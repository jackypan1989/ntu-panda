package ntu.im.bilab.panda.kobuta;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/*
 * read training data then insert patent_id and patent_year to database
 */
public class FillPatentID {
	
	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	private Connection connection = null;
	private Statement statement = null;
	private String selectSQL;
	
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
	public void Update(String PatentID, String Class) throws SQLException{
		if(Class.equals("N")){
			//System.out.println("here");
		selectSQL = "INSERT INTO licensability_negative (`Patent_id`, `class`) VALUES ('"+PatentID+"', '"+Class+"') ";
		statement.execute(selectSQL);
		System.out.println("sucess!");
		}
	}

	public static void main ( String args [] ) throws IOException, SQLException{
		BufferedReader buf = new BufferedReader( new FileReader("D:\\workspace\\Train1.txt") );
		FillPatentID fill = new FillPatentID();
		String s;
		fill.Open();
		int count =0;
		// 5247469 duplicate for Y & N
		while( (s = buf.readLine() )!= null){
			
			String[] TrainData = s.split("	");// split by tab
			System.out.println(TrainData[0]);
			System.out.println(TrainData[1]);
			count++;
			fill.Update(TrainData[0],TrainData[1]);
		}
		System.out.println(count);
		fill.Close();
	}
}
