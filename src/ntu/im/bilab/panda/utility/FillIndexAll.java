package ntu.im.bilab.panda.utility;

/*
 * 填所有Patent id 的index value
 *  
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.lang.Runtime;

import ntu.im.bilab.panda.database.JdbcMysql;
import ntu.im.bilab.panda.parameter.Innovation;


public class FillIndexAll {
	private static Connection con = null; //Database objects
	  //連接object
	  private Statement stat = null;
	  //執行,傳入之sql為完整字串
	  private ResultSet rs = null;
	  //結果集
	  private static PreparedStatement pst = null;
	  //執行,傳入之sql為預儲之字申,需要傳入變數之位置
	  //先利用?來做標示
	static String pid= null;
	
	public static void main(String[] args) {
		JdbcMysql DB = new JdbcMysql();
		// TODO Auto-generated method stub
		try {
		      Class.forName("com.mysql.jdbc.Driver");
		      //註冊driver
		      con = DriverManager.getConnection(
		      "jdbc:mysql://140.112.107.207/patent_value","bilab","bilab");
		      //取得connection
		//jdbc:mysql://localhost/test?useUnicode=true&characterEncoding=Big5
		//localhost是主機名,test是database名
		//useUnicode=true&characterEncoding=Big5使用的編碼 
		     
		    }
		    catch(ClassNotFoundException e)
		    {
		      System.out.println("DriverClassNotFound :"+e.toString());
		    }//有可能會產生sqlexception
		    catch(SQLException x) {
		      System.out.println("Exception :"+x.toString());
		    }
		    
		    
				ReadFromFile RF = new ReadFromFile();
				RF.readFromFile("patents_index_all.txt");
				ArrayList<String> pidList = new ArrayList<String>();
				pidList = RF.GetResult();
		Runtime r = Runtime.getRuntime(); 
		for(int i=0;i<pidList.size();i++){
			String ClassLabel = "OOO"; //代表已經處理過
			String TableName = "patents_index";
			pid = pidList.get(i);
			pid = pid.trim();
			System.out.println(pid);
//			if(DB.GetTable(pid).equals("X")){
//				
//				System.out.println("   WRONG!");
//				continue;
//			}
			Innovation inno = new Innovation();
			inno.parseInnovationData(pid);
//			
			int patentGroup =  inno.PatentGroups();
			int patentBcite = inno.PatentedBackwardCitations();
			int scienceLink = inno.ScienceLinks();
			int Backcitation = inno.BackwardCitations();
			System.out.println("Patent Groups: "+patentGroup);
			System.out.println("PatentedBackwardCitations: "+patentBcite);
			System.out.println("ScienceLinks: "+scienceLink);
			System.out.println("BackwardCitations: "+ Backcitation);
			System.out.println("");
			
//			Profile prof = new Profile(pid);
//			prof.SelectTable();
//			int numofinventor = prof.GetInventors();
//			int numofass = prof.GetAssignee();
//			int numofcite = prof.GetCitation();
//			long approvetime = prof.GetApproveTime();
			
//			System.out.println("Number of Inventors: " + prof.GetInventors() + "\n" 
//	    			+ "Number of Assignees: " + prof.GetAssignee() + "\n" 
//	    			+ "Number of Citations: " + prof.GetCitation() + "\n" 
//	    			+ "Approve Time Period (days) :" + prof.GetApproveTime() + " " + "days");
			
			
//			Diversity div = new Diversity(pid);
//			int techscope = div.GetTechScope();
//			int generality = div.GetGenerality();
//			int originality = div.GetOriginality();
//			System.out.println("TechScope: " + techscope);
//			System.out.println("Generality: " + generality);
//			System.out.println("Originality: " + originality);
			
//			App_Int AI = new App_Int(pid);
//			int noofclaim = AI.NoClaims();
//			System.out.println("--------------------FINISH NoClaims()------------------------------");
//			int noofd_claim = AI.NoDepClaim();
//			System.out.println("--------------------FINISH NoDepClaims()------------------------------");
//			int noofi_claim = AI.NoIndepClaim();
//			System.out.println("--------------------FINISH NoIndepClaims()------------------------------");
//			int transfertimes = AI.NoTransAs();
//			System.out.println("--------------------FINISH Transfertimes()------------------------------");
//			int office = AI.NoPatentOffice();
//			System.out.println("--------------------FINISH PatentOffice()------------------------------");
//			System.out.println("Number of Claims: "+noofclaim);
//			System.out.println("Number of dependent Claims: "+noofd_claim);
//			System.out.println("Number of Independent Claims: "+noofi_claim);
//			System.out.println("Trnasfer times of the assignees: " + transfertimes);
//			System.out.println("Patent in the three major office: "+ office);
//			
			String updateSQL = "update "+TableName+" set"+
														  " patentgroup="+patentGroup+
														  ", patentedBcite="+patentBcite+
														  ", sciencelink="+scienceLink+
														  ", Backcitation="+Backcitation+
//														  ", numofinventor="+numofinventor+
//														  ", numofass="+numofass+
//														  ", numofcite="+numofcite+
//														  ", approvetime="+approvetime+
//														  ", techscope="+techscope+
//														  ", generality="+generality+
//														  ", originality="+originality+
//														  ", numofclaim="+noofclaim+
//														  ", numofd_claim="+noofd_claim+
//														  ", numofi_claim="+noofi_claim+
//														  ", transfertimes="+transfertimes+
//														  ", office="+office+
														  ", class='"+ClassLabel+"' where patent_id='"+pid+"'";
			//System.out.println(updateSQL);
			try {
				pst = con.prepareStatement(updateSQL);
				//System.out.println("SQL="+updateSQL);
				pst.executeUpdate();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
					
		    System.out.println("   Success"+"\n");
		    
		    //System.out.println("Free memory:"+ r.freeMemory());
		    //System.out.println("Total memory:"+ r.totalMemory());
		    
		    
		    System.gc();	
		}
				
														  
																
				
				
			
			//System.out.println(rs.getFetchSize());
			//while(rs.next()){
			//	System.out.println(rs.getString("Patent_id"));
				
			//}
	
		 
		
		
		
		
//		Innovation inno = new Innovation();
//		inno.parseInnovationData(pid);
//		inno.printAll();
//		Profile prof = new Profile(pid);
//		prof.SelectTable();
//		Diversity div = new Diversity();
//		div.PrintAll(pid);
//		App_Int AI = new App_Int(pid);
//		AI.PrintAll();

	}

}

