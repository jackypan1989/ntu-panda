package ntu.im.bilab.panda.ivy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;




public class FillAttributeIndex {
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://140.112.107.207/patent_value";
	static String user = "root";
	static String password = "123456";
	static int lowestYear = 1976;
	static int highestYear = 2009;
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		String updateTable = "attacker_expert";
		//String updateTable = "attacker_troll_negative";
		//String updateTable = "attacker_troll_positive";
		//String updateTable = "licensability_negative";
		//String updateTable = "licensability_positive";
		
		try { 
			Class.forName(driver); 
			conn = DriverManager.getConnection(url, user , password);	
			stmt = conn.createStatement(); 

			ResultSet result = stmt.executeQuery("SELECT Patent_id, Patent_year FROM "+updateTable); 
			
				List<String> patent_id_list = new LinkedList<String>();
				List<String> patent_year_list = new LinkedList<String>();
				
				while(result.next()) {
					patent_id_list.add(result.getString("Patent_id").trim());
					patent_year_list.add(result.getString("Patent_year").trim());
				}
				
				Iterator<String> pItr = patent_id_list.iterator();
				Iterator<String> yItr = patent_year_list.iterator();
				
				//int num_of_csc=-1;
				//int patent_age_since_issued_date=-1;
				//int diversity_IPC=-1;
				int diversity_USPC=-1;
				//int family_volume=-1;
				//int family_size=-1;
				//int major_market=-1;
				
				while(pItr.hasNext()){
					String patent_id = pItr.next();
					String patent_year = yItr.next();
					//String db_num_of_csc = result.getString("num_of_csc").trim();
					//String db_diversity_IPC = result.getString("diversity_IPC").trim();
					//String db_patent_age_since_issued_date = result.getString("patent_age_since_issued_date").trim();
					
					
					//System.out.println(patent_id+" "+patent_year);
					if(!patent_year.equals("-1")){
						//呼叫我的程式
					/*	NumberOfCrossStateCooperation cross_state = new NumberOfCrossStateCooperation(patent_id);
						num_of_csc=cross_state.getNumber_of_cross_state();
						patent_age_since_issued_date=cross_state.getPatent_age();*/
						
						TechnologicalDiversity techno_diversity = new TechnologicalDiversity(patent_id);
						//diversity_IPC=techno_diversity.getDiversity_IPC();
						diversity_USPC=techno_diversity.getDiversity_IPC();
						
					/*	ParameterFinder patent_family = new ParameterFinder(patent_id);
						family_volume=patent_family.getPatentFamilyVolume(patent_id);
						family_size=patent_family.getPatentFamilySize(patent_id);
						major_market=patent_family.getMajorMarket(patent_id);*/
						
					//	String updateSQL = "UPDATE `"+updateTable+"` SET `diversity_IPC`='"+diversity_IPC+"', `num_of_csc`='"+num_of_csc+"', `patent_age_since_issued_date`='"+patent_age_since_issued_date+"' WHERE `Patent_id`='"+patent_id+"'";
					//	String updateSQL = "UPDATE `"+updateTable+"` SET `family_volume`='"+family_volume+"', `family_size`='"+family_size+"', `major_market`='"+major_market+"' WHERE `Patent_id`='"+patent_id+"'";
						String updateSQL = "UPDATE `"+updateTable+"` SET `diversity_USPC`='"+diversity_USPC+"' WHERE `Patent_id`='"+patent_id+"'";
					//	stmt.executeUpdate(updateSQL);
						
					//	System.out.println(" t diversity: "+diversity_IPC+"    num_of_csc: "+num_of_csc+"    patent_age: "+patent_age_since_issued_date+" inserted");
					//	System.out.println("patent_id: "+patent_id+"     year: "+patent_year+"     family_volume: "+family_volume+"    family_size: "+family_size+"    major_market: "+major_market+"   inserted");
						System.out.println("patent_id: "+patent_id+"     year: "+patent_year+"     diversity_USPC: "+diversity_USPC+"      inserted");
					}
				}
		}
		catch(ClassNotFoundException e) { 
			System.out.println("Can't find driver class"); 
			e.printStackTrace(); 
		} 
		catch(SQLException e) { 
			e.printStackTrace(); 
		}
		finally {
			if(stmt != null) {
				try {
					stmt.close();
				} 
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
}
