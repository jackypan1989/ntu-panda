package ntu.im.bilab.panda.turtle;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.sql.Connection;

import ntu.im.bilab.panda.core.Config;
import ntu.im.bilab.panda.parameter.Diversity;

public class ForwardCite {

	static int lowestYear = 1976;
	static int highestYear = 2009;
	static String dataCollect_date = "January 1, 2010";
	String patent_id;
	int patent_year;
	Map<String,Integer> fwPatents;
		
	public static void main(String[] args) throws Exception{
		//D262492 1981
		
		ForwardCite forwardcite = new ForwardCite("D262492");
		Map<String, Integer> fw_result = forwardcite.getForward();
		/*
		int num_of_fwd_citations = fw_result.get("num_of_fwd_citations");
		int num_of_fwd_3years = fw_result.get("num_of_fwd_3years");
		int num_of_fwd_5years = fw_result.get("num_of_fwd_5years");
		float ave_num_of_fwd = forwardcite.getAvgForward();
		float fwd_selfcitation_rate = forwardcite.getFwSelfCite();
		float generality_IPC = forwardcite.getGenerality("ipc");
		float generality_USPC = forwardcite.getGenerality("ccl");
		float extensive_generality = forwardcite.getExtGenerality();
		System.out.println(forwardcite.patent_id+" "+forwardcite.patent_year+" "
				+num_of_fwd_citations+" "+num_of_fwd_3years+" "+num_of_fwd_5years+" "+ave_num_of_fwd+" "
				+fwd_selfcitation_rate+""+generality_IPC+" "+generality_USPC);
		*/
	}
	
	public ForwardCite(String id){
		this.patent_id = id;
		this.patent_year = getPatentYear(id);
		this.fwPatents = getForwardList(this.patent_id, this.patent_year);
	}
	
	//給予patent_id，找到patent_year
	static int getPatentYear(String patent_id){
		int patent_year = -1;
		try{
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
			if(conn!=null && !conn.isClosed()){
				Statement stmt = conn.createStatement();
				String selectSQL = "SELECT `Patent_year` FROM `patent_id_year` WHERE `Patent_id` ='"+patent_id+"'";
				ResultSet result = stmt.executeQuery(selectSQL);
				if(result.next())
					patent_year = result.getInt(1); 
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
		return patent_year;
	}
	
	//計算兩日期的差額(天) date2 - date1 
	static int calDate(String date1, String date2){
	  DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
	  int dateResult = 0;
	  
	  try {   
		   	Date d1 = df.parse(date1);
	       	Date d2 = df.parse(date2);
		   	long t1 = d2.getTime() - d1.getTime();
	       	long time = 1000*3600*24;
		    
		   	if(t1/time > 0){
			   dateResult = (int) (t1/time);
		   	}else{
		      System.out.println("Unable to caculate... ");
		   	}
		   }catch(ParseException e) {
			   System.out.println("Unable to parse... ");
		   }
	  return dateResult;
	}
	
	//給 patent_id 得到issuedDate ex:December 29, 1981
	static String getDateById(String patent_id, int patent_year) throws Exception{
		String date = null;
		Class.forName(Config.DRIVER);
		Connection conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
		if(conn!=null && !conn.isClosed()){
				Statement stmt = conn.createStatement();
				String selectSQL = "SELECT `Issued_Date` FROM `content_"+patent_year+"` WHERE `Patent_id` ='"+patent_id+"'";
				ResultSet result = stmt.executeQuery(selectSQL);
				result.next();
				date = result.getString(1)+", "+patent_year; 
		}
		conn.close();
		return date.trim();
	 }
		
	//算最後一年的X年內forward citation，需要考慮date
	static int calForwardLast(String patent_id, int patent_year, String fwPatent_id ,int lastYear) throws Exception{ 	
		int count = 0;
		int withincount = lastYear-patent_year;
		String issued_date = getDateById(patent_id, patent_year);
		String fwIssued_date = getDateById(fwPatent_id, lastYear);
		
		if(calDate(issued_date, fwIssued_date) < 365*withincount)
			count = 1;
	
		return count;
	}
		
	//根據patentID和年份，取得該專利的forward Cite專利列表，包含ID和年份
	static Map<String, Integer> getForwardList(String patent_id, int patent_year){
		Map<String,Integer> fwPatents = new TreeMap<String,Integer>();
		
		try{
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
			if(conn!=null && !conn.isClosed()){
				for(int i = patent_year; i<=highestYear; i++){
					Statement stmt = conn.createStatement();
					String selectSQL = "SELECT `Referenced_By` FROM `patent-referencedby_"+
							i+"` WHERE `Patent_id` ='"+patent_id+"'";
					ResultSet result = stmt.executeQuery(selectSQL);
					while(result.next()){
						String fwPatent_id = result.getString(1).trim();
						fwPatents.put(fwPatent_id, i);
					}				
				}
				
				conn.close();
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return fwPatents;
	}
	
	//get assigee
	static String getAssignee(String patent_id, int patent_year) throws Exception{
		String assignee = "";
	
		Class.forName(Config.DRIVER);
		Connection conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
		if(conn!=null && !conn.isClosed()){
			Statement stmt = conn.createStatement();
			String selectSQL = "SELECT `Assignee` FROM `content_"+patent_year+"` WHERE `Patent_id` ='"+patent_id+"'";
			ResultSet result = stmt.executeQuery(selectSQL);
			result.next();
			
			if(result.getString(1) != null)
				assignee = result.getString(1).trim();
		}
		conn.close();
		return assignee;
	}
				
	//No 19、 No 20、 No 21, 給予專利代碼、專利年限，算出Forward Citation(all、 within 3、5 years)
	public Map<String, Integer> getForward() throws Exception{
		int forwardCite_all = 0;
		int forwardCite_3 = 0;
		int forwardCite_5 = 0;
		if(this.fwPatents==null)
			this.fwPatents = getForwardList(patent_id, patent_year);
		//Map<String,Integer> fwPatents = getForwardList(patent_id, patent_year);
		Map<String, Integer> result = new TreeMap<String, Integer>();
		forwardCite_all = this.fwPatents.size();
		
		Iterator<String> fwItr = this.fwPatents.keySet().iterator();
		while(fwItr.hasNext()){
			String fwPatent_id = fwItr.next();
			int fwPatent_year = this.fwPatents.get(fwPatent_id);
			
			if(fwPatent_year < (this.patent_year+3)){
				forwardCite_3 += 1;
			}else if(fwPatent_year == (this.patent_year+3)){
				forwardCite_3 += calForwardLast(this.patent_id, this.patent_year, fwPatent_id, patent_year+3);
			}
				
			if(fwPatent_year < (this.patent_year+5)){
				forwardCite_5 += 1;
			}else if(fwPatent_year == (this.patent_year+5) ){
				forwardCite_5 += calForwardLast(this.patent_id, this.patent_year, fwPatent_id, this.patent_year+5);
		
			}
		}
		//System.out.println("forwardCite_all:"+forwardCite_all);
		//System.out.println("forwardCite_3:"+forwardCite_3);
		//System.out.println("forwardCite_5:"+forwardCite_5);
		
		result.put("num_of_fwd_citations", forwardCite_all);
		result.put("num_of_fwd_3years", forwardCite_3);
		result.put("num_of_fwd_5years", forwardCite_5);
		return result;
	}
	
	//No.26 No.49 No.50 算出# of forward citations with same assignee(all、 within 3、5 years)
	public float getFwSelfCite() throws Exception{
		String assignee = getAssignee(patent_id, patent_year);;
		int fwSelfCite_all = 0;
		int fwSelfCite_3 = 0;
		int fwSelfCite_5 = 0;
		float fwSelfCiteRate = 0;
		if(this.fwPatents==null)
			this.fwPatents = getForwardList(this.patent_id, this.patent_year);
		//Map<String,Integer> fwPatents = getForwardList(patent_id, patent_year);//獲得forwardList
		//Map<String, Integer> result = new TreeMap<String, Integer>();
		//System.out.println(assignee);
		
		Iterator<String> itr = this.fwPatents.keySet().iterator();
		while(itr.hasNext()){
			String fwPatent_id = itr.next();
			int fwPatent_year = fwPatents.get(fwPatent_id);
			String assigneeF = getAssignee(fwPatent_id, fwPatent_year);
		
			//self citation
			if(assignee!="" && assignee.equals(assigneeF)){
				fwSelfCite_all++;
				
				if(fwPatent_year<this.patent_year+3){
					fwSelfCite_3++;
				}else if(fwPatent_year==this.patent_year+3){	
					fwSelfCite_3 += calForwardLast(this.patent_id, this.patent_year, fwPatent_id, this.patent_year+3);
				}
				
				if(fwPatent_year<this.patent_year+5){
					fwSelfCite_5++;
				}else if(fwPatent_year==this.patent_year+5){
					fwSelfCite_5 += calForwardLast(this.patent_id, this.patent_year, fwPatent_id, this.patent_year+5);					
				}
			}
		}
		
		if(this.fwPatents.size() > 0)
			fwSelfCiteRate = (float)fwSelfCite_all/this.fwPatents.size();
	
		//System.out.println("fwSelfCite_all:"+fwSelfCite_all);
		//System.out.println("fwSelfCite_3:"+fwSelfCite_3);
		//System.out.println("fwSelfCite_5:"+fwSelfCite_5);
		//System.out.println("fwSelfCiteRate:"+fwSelfCiteRate);
		return fwSelfCiteRate;
	}
	
	//取得一個專利的主要USPC、IPC class，classType有ccl或ipc兩種
	static String getMainClass(String patent_id, int patent_year, String classType){
		/*String str = "Current U.S. Class: 426/55  ; 426/479; 426/480; 426/518; 426/59; 452/138; 99/538 " +
				"Current International Class:  A22C 17/00&nbsp(20060101); A22C 17/04&nbsp(20060101); A23L 001/31&nbsp(); A23L 001/325&nbsp() " +
				"Field of Search:   ";*/
		String str = "";
		try{
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(Config.DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
			if(conn!=null && !conn.isClosed()){
				Statement stmt = conn.createStatement();
				String selectSQL = "SELECT `Current U.S. Class` FROM `uspto_"+patent_year+"` WHERE `Patent_id` ='"+patent_id+"'";
				ResultSet result = stmt.executeQuery(selectSQL);
				if(result.next())
					str = result.getString(1);
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(str);
		
		String cclTitle = "Current U.S. Class:";
		String ipcTitle = "Current International Class:";
		String fieldTitle = "Field of Search:";
		String result = "";
		//解析 main U.S. Class
		if(classType.equals("ccl")){
			String cclStr = "";
			if(str.contains(ipcTitle)){
				cclStr = str.substring(str.indexOf(cclTitle)+cclTitle.length(), str.indexOf(ipcTitle));
			}else{
				cclStr = str.substring(str.indexOf(cclTitle)+cclTitle.length(), str.indexOf(fieldTitle));
			}	
			String[] cclStrs = cclStr.split(";");
			String[] mainCclStr = cclStrs[0].trim().split("/");	
			//System.out.println(mainCclStr[0]);
			result = mainCclStr[0];
			
		}else if(classType.equals("ipc")){ //解析 main IPC class
			String ipcStr = "";
			if(str.contains(ipcTitle)){
				//System.out.println(str);
				if(str.contains(fieldTitle))
					ipcStr = str.substring(str.indexOf(ipcTitle)+ipcTitle.length(), str.indexOf(fieldTitle));
				else
					ipcStr = str.substring(str.indexOf(ipcTitle)+ipcTitle.length());
				String[] ipcStrs = ipcStr.split(";");
				String[] mainIpcStr = ipcStrs[0].trim().split(" ");
				//System.out.println(mainIpcStr[0]);
				result = mainIpcStr[0];
				
			}
		}
		
		return result;
	}
	
	//取得一個專利的主要USPC class
	static String getMainCCL(String patent_id, int patent_year){
		String mainccl = "";
		
		try{
			Class.forName(Config.DRIVER);
			Connection conn = DriverManager.getConnection(Config.NEW_DATABASE_URL, Config.DATABASE_USER, Config.DATABASE_PASSWORD);
			if(conn!=null && !conn.isClosed()){
				Statement stmt = conn.createStatement();
				String selectSQL = "SELECT `mainclass` FROM `patent-mainccl_"+patent_year+"` WHERE `Patent_id` ='"+patent_id+"'";
				ResultSet result = stmt.executeQuery(selectSQL);
				result.next();
				mainccl = result.getString(1);
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return mainccl;
	}
	
	//No.32 算出Generality，classType有ccl或ipc兩種
	public float getGenerality(String classType){
		float generality = 0;
		if(this.fwPatents==null)
			this.fwPatents = getForwardList(this.patent_id, this.patent_year);
		//Map<String,Integer> fwPatents = getForwardList(patent_id, patent_year); //forwardCite列表
		Map<String,Integer> class_num = new TreeMap<String,Integer>();//每個類別各含有多少個其類別的forwardCite數目
		int fwSize = this.fwPatents.size();
		
		Iterator<String> fItr = this.fwPatents.keySet().iterator();
		while(fItr.hasNext()){
			String fwPatent_id = fItr.next();
			int fwPatent_year = this.fwPatents.get(fwPatent_id);
			String mainClass = "";
			//by uspc
			if(classType.equals("ccl")){
				mainClass = getMainCCL(fwPatent_id, fwPatent_year);
				if(mainClass.contains("/"))
					mainClass = mainClass.substring(0, mainClass.indexOf("/"));
			}				
			else{ //by ipc
				//mainClass = Download.getMainIpc(fwPatent_id);
				//System.out.print(fwPatent_id+" "+fwPatent_year);
				mainClass = getMainClass(fwPatent_id, fwPatent_year,"ipc");
			}
			//System.out.println(fwPatent_id+" "+mainClass);
			
			if(!mainClass.equals("")){
				if(class_num.containsKey(mainClass)){
					class_num.put(mainClass, class_num.get(mainClass)+1);
				}else{
					class_num.put(mainClass, 1);
				}
			}	
		}
		
		if(fwSize>0){
			generality = 1;
			//int subcount = 0;
			Iterator<String> cItr = class_num.keySet().iterator();
			while(cItr.hasNext()){
				String mainClass = cItr.next();
				if(mainClass.equals("")){
					//subcount++;
					continue;					
				}
				int number = class_num.get(mainClass);
				generality -= (((float)number/fwSize)*((float)number/fwSize));
			}			
		}
		
		//System.out.println(class_num);
	    //System.out.println("generality:"+generality);
		return generality;
	}
	
	//No.23 算出Avg number of Forward Citations
	public float getAvgForward() throws Exception{
		float patentAge = calDate(getDateById(this.patent_id, this.patent_year), dataCollect_date)/365f;
		if(this.fwPatents==null)
			this.fwPatents = getForwardList(this.patent_id, this.patent_year);
		//Map<String,Integer> fwPatents = getForwardList(patent_id, patent_year); //forwardCite列表
		float avgForward = this.fwPatents.size()/patentAge;
		//System.out.println(avgForward);
		
		return avgForward;
	}
	
	//No.34 算出Extensive Generality 
	public float getExtGenerality(){
		float exGenerality = 0;
		int empty_ccl = 0;
		if(this.fwPatents==null)
			this.fwPatents = getForwardList(this.patent_id, this.patent_year);
		//Map<String,Integer> fwPatents = getForwardList(patent_id, patent_year);
		Diversity diversity = new Diversity(patent_id);
		int generality = diversity.GetGenerality();
		
        if(this.fwPatents.size()>0){
        	exGenerality = (float)generality/this.fwPatents.size();
        }
         //System.out.println(exGenerality);
		return exGenerality;
	}
}
