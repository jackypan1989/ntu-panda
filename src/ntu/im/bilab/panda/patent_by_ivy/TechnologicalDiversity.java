package ntu.im.bilab.panda.patent_by_ivy;
/* 	
 *  Variable Number : 4
 *	Method : return the different four-digit IPC classification codes of a patent
 * 	Return Type : Integer
 *  Parameter : technological_diversity_IPC
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120625
 *  Example :  IPC H01L 21/302 is re-assigned to “H01L”
 */ 

/* 	
 *  Variable Number : 5
 *	Method : return three-digit USPC classification codes of a patent
 * 	Return Type : Integer
 *  Parameter : technological_diversity_USPC
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120701
 *  Example :  # of different three-digit USPC classification codes of a patent (For example: 438/8 is re-assigned to “438”)
 */ 
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.*;


public class TechnologicalDiversity {
	 /*Declare Variables*/
    private Connection conn = null;	
	private String patent_id;
	private int diversity_IPC=0;              //Variable Number : 4
	private int diversity_USPC=0;             //Variable Number : 5
	
	public int getDiversity_IPC() {
		return diversity_IPC;
	}

	public void setDiversity_IPC(int diversity_IPC) {
		this.diversity_IPC = diversity_IPC;
	}
	
	public int getDiversity_USPC() {
		return diversity_USPC;
	}

	public void setDiversity_USPC(int diversity_USPC) {
		this.diversity_USPC = diversity_USPC;
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
		DifferentUSPCClassification(getPatent_id());
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
	
	public ResultSet SelectSQL(String patent_id, String select_table, String select_column){
		boolean is_find_result=false;
		ResultSet select_result=null;
		
		for(int year = 1976 ; year <= 2009 && is_find_result==false; year++)
		{	
				//String select_SQL = "select Class from `patent-class_" + year + "` where Patent_id =" + "'" + patent_id + "'" ;
				String select_SQL = "select `"+ select_column +"` from `" + select_table + "_" + year + "` where Patent_id =" + "'" + patent_id + "'" ;	
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
		ResultSet select_result=SelectSQL(patent_id, "patent-class", "Class");
		if(select_result==null){
			setDiversity_IPC(-1);
		}
		else{
			try {
				select_result.last();
				setDiversity_IPC(select_result.getRow());
			} catch (SQLException e) {
				setDiversity_IPC(-1);
				e.printStackTrace();
			}
		}
	}
	
	public void DifferentUSPCClassification(String patent_id){
		int diversity_USPC = -1;
		
		ResultSet select_result = SelectSQL(patent_id, "patent-ccl", "CCL");

		if(select_result==null){
			setDiversity_USPC(-1);
		}
		else{
			try {
				String ccl = "";
				String ccl_code[];
	
				select_result.last();
				int num_of_ccl = select_result.getRow();
				String ccl_array[] = new String[num_of_ccl];
				
				select_result.first();
				for(int i=0; i<num_of_ccl; i++){
					ccl = select_result.getString("CCL");
					ccl_code = ccl.split("\\/");
					boolean is_ccl_code_exist = false;
					
					if(diversity_USPC == -1){
						ccl_array[0] = ccl_code[0];
						diversity_USPC = 1;
						is_ccl_code_exist = true;
					}
					else{
						for (int j = 0; j < diversity_USPC; j++) {
							if (ccl_code[0].equals(ccl_array[j])) {
								is_ccl_code_exist = true;
								break;
							}
						}
					}
					
					if (!is_ccl_code_exist) {
						ccl_array[diversity_USPC] = ccl_code[0];
						diversity_USPC++;
					}
					select_result.next();
				}
				
				setDiversity_USPC(diversity_USPC);
			} catch (SQLException e) {
				setDiversity_USPC(-1);
				e.printStackTrace();
			}
		}
	}
	
	public void PrintAll(){
		System.out.println("The Patent ID you entered is: "+getPatent_id());
		System.out.println("Diversity_IPC : "+getDiversity_IPC());
		System.out.println("Diversity_USPC : "+getDiversity_USPC());
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
			
		String patent_id = in.readLine();			//read  paten_ id		
			
		TechnologicalDiversity techno_diversity = new TechnologicalDiversity(patent_id);
		techno_diversity.PrintAll();
	}
}