package ntu.im.bilab.panda.ivy;
/* 	
 *  Variable Number : 16
 *	Method : return the num of patents cited by the particular patent
 * 	Return Type : Integer
 *  Parameter : backward_citation
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120625
 */ 

import java.sql.*;
import java.io.*;

public class BackwardCitation {
	 /*Declare Variables*/
    private Connection conn = null;	
	private String patent_id;
	private int backward_citation=0;                       //你要的答案

	public int getBackward_citation() {
		return backward_citation;
	}

	public void setBackward_citation(int backward_citation) {
		this.backward_citation = backward_citation;
	}

	public String getPatent_id() {
		return patent_id;
	}

	public void setPatent_id(String patent_id) {
		this.patent_id = patent_id;
	}
	
	//初始化
	public BackwardCitation(String patent_id){	
		setPatent_id(patent_id);
		ConnectDB();
		NumberOfBackwardCitation(getPatent_id());
	}
	
	//connect to patent database
	public void ConnectDB(){                
		try {
		      Class.forName("com.mysql.jdbc.Driver");		      
		      conn = DriverManager.getConnection("jdbc:mysql://140.112.107.122/mypaper?useUnicode=true&characterEncoding=Big5","root","1234");
	    }
		 catch(ClassNotFoundException e) { 
			   System.out.println("DriverClassNotFound :"+e.toString()); 
		}
		 catch(SQLException e) {
			   System.out.println("資料庫連線失敗 : "+e.toString()); 
		}
	}
	
	public ResultSet SelectSQL(String patent_id){
		boolean is_find_result=false;
		ResultSet select_result=null;
		
		for(int year = 1976 ; year <= 2009 && is_find_result==false; year++)
		{	
				String select_SQL = "select `References Cited` from `content_" + year + "` where Patent_id =" + "'" + patent_id + "'" ;	
				try {
					Statement stmt = conn.createStatement();
					select_result = stmt.executeQuery(select_SQL); 	    
					if(select_result.next()== true)
					{
						//System.out.print(year);
						is_find_result=true;
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		if(is_find_result==false) return null;
		else return select_result;
	}
	
	public void NumberOfBackwardCitation(String patent_id){
		ResultSet select_result=SelectSQL(patent_id);
		try {
			String backward_citation=select_result.getString("References Cited");
			String[] backward_citation_array=backward_citation.split(";");
			setBackward_citation(backward_citation_array.length);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void PrintAll(){
		System.out.println("The Patent ID you entered is: "+getPatent_id());
		System.out.println("number of backward citation: "+getBackward_citation());
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
			
		String patent_id = in.readLine();			//read  paten_ id		
			
		BackwardCitation BC = new BackwardCitation(patent_id);
		BC.PrintAll();
	}
}

