package ntu.im.bilab.panda.parameter;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;


public class Profile {	
	
	/*Declare Variables*/
	private Connection con = null;	
	private Statement stat = null;	
	private ResultSet rs = null;	
	private PreparedStatement pst = null;
	
	private String selectSQL, patent_id, patent_tableName;
	
	private int inventors,	
				assignees, 				
				citations;
	
	private long approve_time;
	
	private String issued_date, filed_date;
	
	public Profile(String Patent_ID)
	{	
		//initialize all variables
		patent_id = Patent_ID;
					
		inventors = 0;
		assignees = 0;
		approve_time = 0;
		citations = 0;
		Connect_DB();
		patent_tableName = Find_table(patent_id);		
		selectSQL = "select Patent_id,Inventors, `Issued date`,`References Cited` from "+ patent_tableName + " where Patent_id =" + "'" + Patent_ID + "'" ;
		SelectTable();
	}	
	
	
	/*DB operation*/
	public void Connect_DB()
	{
		//connect to patent database
		try {
		      Class.forName("com.mysql.jdbc.Driver");		      
		      con = DriverManager.getConnection(
		      "jdbc:mysql://140.112.107.207/patent_value?useUnicode=true&characterEncoding=Big5","root","123456");
//		      Class.forName("com.mysql.jdbc.Driver");		      
//		      con = DriverManager.getConnection(
//		      "jdbc:mysql://daventu.no-ip.org/patent_value?useUnicode=true&characterEncoding=Big5","bilab","bilab");
		    }
		    catch(ClassNotFoundException e) { System.out.println("DriverClassNotFound :"+e.toString()); }
		    catch(SQLException x) {	System.out.println("資料庫連線失敗 : "+x.toString()); }
	}
		
	public void Close_DB()
	{
		try
	    {
	      if(rs!=null)
	      {
	        rs.close();
	        rs = null;
	      }
	      if(stat!=null)
	      {
	        stat.close();
	        stat = null;
	      }
	      if(pst!=null)
	      {
	        pst.close();
	        pst = null;
	      }
	    }
	    catch(SQLException e) { System.out.println("Close Exception :" + e.toString()); }
	}
		
	/*Find table*/
	public String Find_table(String pid)
	{
		int year;
		String table_name = "";
		for(year = 1976 ; year <= 2009 ; year++)
		{
			
			try
			{			
				selectSQL = "select Patent_id,Inventors, `Issued date`,`References Cited` from uspto_"+ year + " where Patent_id =" + "'" + pid + "'" ;			
				stat = con.createStatement();
			    rs = stat.executeQuery(selectSQL);			    
			    if(rs.absolute(1)== true)
			    {
			    	//System.out.print(year);
			    	break;
			    }			
			}
			catch(SQLException e){System.out.println(e.toString());}		
		}

		//System.out.print(year);
		table_name = "uspto_" + year;
		
		return table_name;
	}
	
	/*Process Approve Time*/
	public static long Approve_Time(String dateStr1,String dateStr2)
	{
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
        ParsePosition pos1 = new ParsePosition(0);
        ParsePosition pos2 = new ParsePosition(0);
        java.util.Date date1= sdf.parse(dateStr1,pos1);
        java.util.Date date2= sdf.parse(dateStr2,pos2);
        long dateDiff = 0;
        if(date1.getTime()<date2.getTime()) {
            dateDiff = ((date2.getTime()/1000-date1.getTime()/1000)/(24*60*60));
        }
        else {
            dateDiff = ((date1.getTime()/1000-date2.getTime()/1000)/(24*60*60));
        }
        return dateDiff;
        
	}
		
	public enum Month {		 
		January("01"), February("02"), March("03"), April("04"), May("05"), June("06"), July("07"), August("08"),  
		September("09"), October("10"), November("11"), December("12");
	    private String intMonth;	 
	    private Month(String intMonth) { this.intMonth = intMonth; }
	    public String getintMonth() { return intMonth; }
	}
				
	public static String Process_Date(String d)
	{		
		
		String delims = "[ ,]+";
		String[] tokens = d.split(delims);
		String result="";
//		for(String str:tokens){
//			System.out.println(str);
//		}
		//due to false parsing of database (`Issued date` column)
		if(tokens.length>3)
		{
			
			Month m = Enum.valueOf(Month.class, tokens[1]);		
			result = tokens[3]+m.intMonth+tokens[2]; 		
		}
		
		//correct parsing
		else
		{
			Month m = Enum.valueOf(Month.class, tokens[0]);		
			result = tokens[2]+m.intMonth+tokens[1];
		}
		return result; 		
	}
		
	
	public void SelectTable()
	{
		try
	    {		
			stat = con.createStatement();
		    rs = stat.executeQuery(selectSQL);

			String temp, cited;
		
			
		    while(rs.next())
	        {	    	
		    	temp = rs.getString("Inventors");	    	  
		    	issued_date = rs.getString("Issued date").trim();	
		    	cited = rs.getString("References Cited");
		    	
		    	//check whether inventor column has data
		    	if(temp.length()!=4)
		    	{
		    		String[] splited_temp = temp.split(":");
		    		String[] inventor_count = splited_temp[1].split(";");
		    		inventors = inventor_count.length;
		    		filed_date = splited_temp[splited_temp.length-1].trim();		    		
		    		if(temp.contains("Assignee")==true)
					  {
						  String[] assignee_count = splited_temp[2].split(";");
						  assignees = assignee_count.length;
					  }
					  else
						  assignees = 0;
		    	}
		    	else
				{
		    		inventors = 0;
					assignees = 0;
					approve_time = 0;
				}
		    	
		    	//check whether references cited exists and count the number of citations
		    	if(cited.contains("U.S. Patent Documents") == true)
		    	{	
		    		
		    		String[] temp2 = cited.split(":");
		    		cited = temp2[0];
		    		temp2 = cited.split(" ");
		    		for(int i = 0 ; i < temp2.length ; i++)
		    		{		    			
		    			if(temp2[i].contains("Foreign") == true)
		    				break;
		    			else
		    			{
		    				//approach 1
			    			if(temp2[i].length() > 4)
			    			{
			    				char[] c = temp2[i].toCharArray();
			    				if(Character.isDigit(c[c.length-1]) == true)
			    					citations++;			    				
			    			}		    			
		    			}
		    		}	    		
		    	}	    	
		    	
		    	
		    	//count approved time period(unit: days)
		    	filed_date = Process_Date(filed_date);
		    	issued_date = Process_Date(issued_date);		    	
		    	approve_time = Approve_Time( filed_date,issued_date);	    	
		    	
//		    	System.out.println("Number of Inventors: " + inventors + "\n" 
//		    			+ "Number of Assignees: " + assignees + "\n" 
//		    			+ "Number of Citations: " + citations + "\n" 
//		    			+ "Approve Time Period (days) :" + approve_time + " " + "days");
	        }        
	    }	
	    catch(SQLException e) { System.out.println("DropDB Exception :" + e.toString()); }
	    finally { Close_DB(); }
	}
	public int GetInventors()
	{
		return inventors;
	}
	public int GetAssignee()
	{
		return assignees;
	}
	public int GetCitation()
	{
		return citations;
	}
	public long GetApproveTime()
	{
		return approve_time;
	}
	
	public void PrintAll()
	{
    	System.out.println("Number of Inventors: " + inventors + "\n" 
		+ "Number of Assignees: " + assignees + "\n" 
		+ "Number of Citations: " + citations + "\n" 
		+ "Approve Time Period (days) :" + approve_time + " " + "days");
        System.out.println("");
		
	}
	
	public static void main(String[] args) throws IOException 
	{				
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
		
		//read in patent id		
		String pid = in.readLine();			
		
	
		
		Profile p = new Profile(pid);		
		
		//select info and count the value of all variables and disconnect DB
		p.SelectTable();
		p.PrintAll();
	}
		
}


