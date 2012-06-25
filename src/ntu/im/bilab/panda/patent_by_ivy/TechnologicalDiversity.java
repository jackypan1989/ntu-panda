package ntu.im.bilab.panda.patent_by_ivy;
/* 	
 *  Variable Number : 4
 *	Method : return the different four-digit IPC classification codes of a patent
 * 	Return Type : Integer
 *  Parameter : technological_diversity
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120625
 *  Example :  IPC H01L 21/302 is re-assigned to “H01L”
 */ 
import java.sql.*;
import java.io.*;


public class TechnologicalDiversity {
	 /*Declare Variables*/
    private Connection conn = null;	
	private String patent_id;
	private int technological_diversity=0;              //這個參數是你要的結果
	
	public int getTechnological_diversity() {
		return technological_diversity;
	}

	public void setTechnological_diversity(int technological_diversity) {
		this.technological_diversity = technological_diversity;
	}

	public String getPatent_id() {
		return patent_id;
	}

	public void setPatent_id(String patent_id) {
		this.patent_id = patent_id;
	}
	
	//初始化
	public TechnologicalDiversity(String patent_id){	
		setPatent_id(patent_id);
		ConnectDB();
		DifferentIPCClassification(getPatent_id());
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
				String select_SQL = "select Class from `patent-class_" + year + "` where Patent_id =" + "'" + patent_id + "'" ;	
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
	
	public void DifferentIPCClassification(String patent_id){
		ResultSet select_result=SelectSQL(patent_id);
		if(select_result==null){
			setTechnological_diversity(-1);
		}
		else{
			try {
				select_result.last();
				setTechnological_diversity(select_result.getRow());
			} catch (SQLException e) {
				setTechnological_diversity(-1);
				e.printStackTrace();
			}
		}
	}
	
	public void PrintAll(){
		System.out.println("The Patent ID you entered is: "+getPatent_id());
		System.out.println("number of different four-digit IPC classification codes: "+getTechnological_diversity());
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
			
		String patent_id = in.readLine();			//read  paten_ id		
			
		TechnologicalDiversity techno_diversity = new TechnologicalDiversity(patent_id);
		techno_diversity.PrintAll();
	}
}
