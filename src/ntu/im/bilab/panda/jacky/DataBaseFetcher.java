package ntu.im.bilab.panda.jacky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;
import ntu.im.bilab.panda.database.JdbcMysql;

public class DataBaseFetcher extends DataBaseUtility {
	public Connection conn;
    public Statement stmt;
    
	public DataBaseFetcher(){
		
		try { 
            Class.forName(Config.DRIVER); 
            conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
            stmt = conn.createStatement();   
        } 
        catch(ClassNotFoundException e) { 
            System.out.println("Can't find driver class"); 
            e.printStackTrace(); 
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public ResultSet getTuple(String patent_id){
		int start_year = 1976;
		int end_year = 2009;
		ResultSet result = null;
		
		for(int i = start_year ; i <= end_year ; i++){
			try {
				result = stmt.executeQuery("select * from content_"+ i + " where Patent_id =" + "'" + patent_id + "'");
				
				if(result.absolute(1)){
					return result;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(result==null) System.out.println("NULL");
		return null;
	}
	
	public void getPatentData(Patent patent, String patent_id){
		// create a patent entity
		
		try {
			// for new database
			ResultSet result = getTuple(patent_id);
			String year = result.getString("Issued_Year");
		    patent.setNewData(result);
			patent.setYear(year);
		
			// for old database
			getOldDataBaseContent(patent, patent_id, year);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void getOldDataBaseContent(Patent patent, String patent_id, String year){
    	
    	try {
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("select * from uspto_"+ year + " where Patent_id =" + "'" + patent_id + "'");
			result.next();
			patent.setOldData(result);

    	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public int getYear(Patent patent , String parameter){
    	int start_year = 1976;
		int end_year = 2009;
		int year = -1;
		
		ResultSet result = null;
		
		if(parameter=="years_to_receive_the_first_citation"){
			year = Integer.parseInt(patent.getYear()) ; 
			String patent_id = patent.getId();
			
			for(int i = year ; i <= end_year ; i++){
				try {
					result = stmt.executeQuery("select * from `patent-referencedby_"+ i + "` where Patent_id =" + "'" + patent_id + "'");
					if(result.absolute(1)) return i;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}
    	return year;
    }
    
	
	
	public static void main(String[] args)
	{
		Patent p = new Patent("4995689");
	}
}
