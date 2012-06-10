package ntu.im.bilab.panda.jacky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

public class DataBaseUpdater extends DataBaseUtility{
	public Connection conn;
    public Statement stmt;
	
	public DataBaseUpdater(){
		try { 
	        Class.forName(Config.DRIVER); 
	        conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
	        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,  
                    ResultSet.CONCUR_UPDATABLE);   
	    } 
	    catch(ClassNotFoundException e) { 
	        System.out.println("Can't find driver class"); 
	        e.printStackTrace(); 
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void updateParameter(int thread_id){
		//4080180 , 136007
		int update_count = 0;
		for(int i=272*(thread_id-1) ; i<272*(thread_id) ; i++){
			try {
	    		ResultSet result = stmt.executeQuery("SELECT * FROM value LIMIT "+i*30+" , 30");
	    		while(result.next()){
	    			String patent_id = result.getString("Patent_id");
	    			Patent patent = new Patent(patent_id);
	    			//System.out.println(patent_id);
	    			
	    			ParameterFinder t = new ParameterFinder();
	    			ResultSet new_data = patent.getNewData();
	    			ResultSet old_data = patent.getOldData();
	   
	    			t.getEC(patent_id);
	    			patent.setParameterForeignInventors(t.getForeignInventors(old_data.getString("Inventors")));
	    			patent.setParameterForeignClasses(t.getForeignClasses(old_data.getString("References Cited")));
	    			patent.setParameterPatentFamilySize(t.getPatentFamilySize(patent_id));
	    			patent.setParameterPatentedBackwardCitations(t.getPatentedBackwardCitations(patent_id));
	    			patent.setParameterMajorMarket(t.getMajorMarket(patent_id));
	    			patent.setParameterForeignPriorityApps(t.getForeignPriorityApps(old_data.getString("Current U.S. Class")));
	    			patent.setParameterYearsToReceiveTheFirstCitation(t.getYearsToReceiveTheFirstCitation(patent));
	    			//System.out.println(patent.toString());
	    			
	    			result.updateString("DB_Status", "A-1"); 
	    			result.updateInt("foreign_inventors", patent.getParameterForeignInventors()); 
	    			result.updateInt("foreign_classes", patent.getParameterForeignClasses()); 
	    			result.updateInt("family_size", patent.getParameterPatentFamilySize()); 
	    			result.updateInt("patented_bwd_citations", patent.getParameterPatentedBackwardCitations()); 
	    			result.updateInt("major_market", patent.getParameterMajorMarket()); 
	    			result.updateInt("foreign_priority_Apps", patent.getParameterForeignPriorityApps()); 
	    			result.updateInt("years_receive_first_citations", patent.getParameterYearsToReceiveTheFirstCitation()); 
	    			
	    			result.updateRow(); 
	    			update_count++;
	    			System.out.println("thread_id : "+thread_id);
	    			System.out.println("this thread progress : "+ update_count);
	    			System.out.println(patent_id+" update successed!\n");
	    			
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	} 
			System.out.println("complete " + i +" / 136006\n");
		}
		
		
		/*
	    for(int i=0 ; i<20 ; i++){
	    	try {
	    		ResultSet result = stmt.executeQuery("SELECT Patent_id FROM value LIMIT 0 , 10000");
	    		while(result.next()){
	    			result.getString("Patent_id");
	    			
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	} 
	    }*/
	}
	
	public static void main(String[] args)
	{
		DataBaseUpdater dbu = new DataBaseUpdater();
		//dbu.updateParameter();
		dbu.Close();
	}
}
