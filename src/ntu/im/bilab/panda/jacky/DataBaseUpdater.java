package ntu.im.bilab.panda.jacky;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ntu.im.bilab.panda.core.Config;
import ntu.im.bilab.panda.parameter.ApplicabilityIntegrity;
import ntu.im.bilab.panda.parameter.Diversity;
import ntu.im.bilab.panda.parameter.Innovation;
import ntu.im.bilab.panda.parameter.Profile;

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
		for(int i=1360*(thread_id-1) ; i<1360*(thread_id) ; i++){
			try {
	    		ResultSet result = stmt.executeQuery("SELECT * FROM value LIMIT "+i*30+" , 30");
	    		while(result.next()){
	    			//if(result.getString("DB_Status").equals("A-1")) continue;
	    			String patent_id = result.getString("Patent_id");
	    			Patent patent = new Patent(patent_id);
	    			//System.out.println(patent_id);
	    			
	    			ParameterFinder t = new ParameterFinder();
	    			ResultSet new_data = patent.getNew_data();
	    			ResultSet old_data = patent.getOld_data();
	                
	    			t.getEC(patent_id);
	    			patent.setParameter_foreign_inventors(t.getForeignInventors(old_data.getString("Inventors")));
	    			patent.setParameter_foreign_classes(t.getForeignClasses(old_data.getString("References Cited")));
	    			patent.setParameter_patent_family_size(t.getPatentFamilySize(patent_id));
	    			patent.setParameter_patented_backward_citations(t.getPatentedBackwardCitations(patent_id));
	    			patent.setParameter_major_market(t.getMajorMarket(patent_id));
	    			patent.setParameter_foreign_priority_apps(t.getForeignPriorityApps(old_data.getString("Current U.S. Class")));
	    			patent.setParameter_years_to_receive_the_first_citation(t.getYearsToReceiveTheFirstCitation(patent));
	    			//System.out.println(patent.toString());
	    			
	    			result.updateString("DB_Status", "A-1"); 
	    			result.updateInt("foreign_inventors", patent.getParameter_foreign_inventors()); 
	    			result.updateInt("foreign_classes", patent.getParameter_foreign_classes()); 
	    			result.updateInt("family_size", patent.getParameter_patent_family_size()); 
	    			result.updateInt("patented_bwd_citations", patent.getParameter_patented_backward_citations()); 
	    			result.updateInt("major_market", patent.getParameter_patent_family_size()); 
	    			result.updateInt("foreign_priority_Apps", patent.getParameter_foreign_priority_apps()); 
	    			result.updateInt("years_receive_first_citations", patent.getParameter_years_to_receive_the_first_citation()); 
	    			
	    			patent.getOldParameter();
	    			
	    			result.updateInt("diversity_USPC",patent.getParameter_diversity_USPC());
	    			result.updateInt("num_of_claims",patent.getParameter_num_of_claims());
	    			result.updateInt("num_of_indep_claims",patent.getParameter_num_of_indep_claims());
	    			result.updateInt("num_of_dep_claims",patent.getParameter_num_of_dep_claims());
	    			result.updateInt("num_of_bwd_citations",patent.getParameter_num_of_bwd_citations());
	    			result.updateInt("science_linkage",patent.getParameter_science_linkage());
	    			result.updateInt("originality_USPC",patent.getParameter_originality_USPC());
	    			result.updateInt("generality_USPC",patent.getParameter_generality_USPC());
	    			result.updateInt("extensive_generality",0);
	    			result.updateInt("num_of_assignee_transfer",patent.getParameter_num_of_assignee_transfer());
	    			result.updateInt("num_of_patent_group",patent.getParameter_num_of_patent_group());
	    			result.updateInt("approval_time",(int)patent.getParameter_approval_time());
	    			result.updateInt("num_of_assignee",patent.getParameter_num_of_assignee());
	    			result.updateInt("num_of_citing_USpatent",patent.getParameter_num_of_citing_USpatent());
	    		    
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
	}
	
	public void updateTrainingData(){
		ArrayList<String> patent_list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader("worthy_patents.txt"));
            
            String line = null;
            while ((line = bufferedReader.readLine()) != null) 
            	patent_list.add(line.trim());           	
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Close the BufferedReader
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		System.out.println(patent_list);
		
		for(String patent_id : patent_list) {
			Patent patent = new Patent(patent_id);
			try {
				stmt.executeUpdate("INSERT INTO  `patent_value`.`value_negative` " +
						"(`Patent_id` ,`DB_Status` ,`Patent_year` ,`inventors` ,`foreign_inventors` ," +
						"`diversity_USPC` ,`foreign_classes` ,`family_size` ,`major_market` ,`num_of_claims` ," +
						"`num_of_indep_claims` ,`num_of_dep_claims` ,`patented_bwd_citations` ,	`num_of_bwd_citations` ," +
						"`science_linkage` ,`originality_USPC` ,`generality_USPC` ,	`extensive_generality` ," +
						"`num_of_assignee_transfer` ,`num_of_patent_group` ,`foreign_priority_Apps` ," +
						"`years_receive_first_citations` ,`approval_time` ,	`num_of_assignee` ,	`num_of_citing_USpatent`)" +
						" VALUES ('"+patent.getId()+"',  '',  '"+patent.getYear()+"',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0'" +
						",  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0')");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(patent_id+" has been inserted");
		}
	}
	
	public static void main(String[] args)
	{
		DataBaseUpdater dbu = new DataBaseUpdater();
		//dbu.updateParameter();
		dbu.updateTrainingData();
		dbu.Close();
	}
}
