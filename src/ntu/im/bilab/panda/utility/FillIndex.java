package ntu.im.bilab.panda.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ntu.im.bilab.panda.database.JdbcMysql;
import ntu.im.bilab.panda.parameter.Diversity;

public class FillIndex {
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
		      "jdbc:mysql://daventu.no-ip.org/patent_value","bilab","bilab");
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
				RF.readFromFile("worthy_patents.txt");
				ArrayList<String> pidList = new ArrayList<String>();
				pidList = RF.GetResult();
		
		for(int i=0;i<pidList.size();i++){
			String ClassLabel = "1";
			String TableName = "worthy_patents";
			pid = pidList.get(i);
			System.out.println(pid);
			if(DB.GetTable(pid).equals("X")){
				
				System.out.println("   WRONG!");
				continue;
			}
//			Innovation inno = new Innovation();
//			inno.parseInnovationData(pid);
//			
//			int patentGroup =  inno.PatentGroups();
//			int patentBcite = inno.PatentedBackwardCitations();
//			int scienceLink = inno.ScienceLinks();
//			int Backcitation = inno.BackwardCitations();
//			System.out.println("Patent Groups: "+inno.PatentGroups());
//			System.out.println("PatentedBackwardCitations: "+inno.PatentedBackwardCitations());
//			System.out.println("ScienceLinks: "+inno.ScienceLinks());
//			System.out.println("BackwardCitations: "+ inno.BackwardCitations());
//			System.out.println("");
			
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
			
			
			Diversity div = new Diversity(pid);
			int techscope = div.GetTechScope();
			int generality = div.GetGenerality();
			int originality = div.GetOriginality();
//			System.out.println("TechScope: " + div.GetTechScope());
//			System.out.println("Generality: " + div.GetGenerality());
//			System.out.println("Originality: " + div.GetOriginality());
			
//			App_Int AI = new App_Int(pid);
//			int noofclaim = AI.NoClaims();
//			int noofd_claim = AI.NoDepClaim();
//			int noofi_claim = AI.NoIndepClaim();
//			int transfertimes = AI.NoTransAs();
//			int office = AI.NoPatentOffice();
//			System.out.println("Number of Claims: "+AI.NoClaims());
//			System.out.println("Number of dependent Claims: "+AI.NoDepClaim());
//			System.out.println("Number of Independent Claims: "+AI.NoIndepClaim());
//			System.out.println("Trnasfer times of the assignees: " + AI.NoTransAs());
//			System.out.println("Patent in the three major office: "+ AI.NoPatentOffice());
//			
			String updateSQL = "update "+TableName+" set"+
//														  " patentgroup="+patentGroup+
//														  ", patentedBcite="+patentBcite+
//														  ", sciencelink="+scienceLink+
//														  ", Backcitation="+Backcitation+
//														  " numofinventor="+numofinventor+
//														  ", numofass="+numofass+
//														  ", numofcite="+numofcite+
//														  ", approvetime="+approvetime+
														  " techscope="+techscope+
														  ", generality="+generality+
														  ", originality="+originality+
//														  " numofclaim="+noofclaim+
//														  ", numofd_claim="+noofd_claim+
//														  ", numofi_claim="+noofi_claim+
//														  ", transfertimes="+transfertimes+
//														  ", office="+office+
														  ", class="+ClassLabel+" where patent_id='"+pid+"'";
			//System.out.println(updateSQL);
			try {
				pst = con.prepareStatement(updateSQL);
				pst.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
						
		    System.out.print("   Success"+"\n");
			
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
