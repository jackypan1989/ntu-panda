package ntu.im.bilab.panda.ivy;
/* 	
 *  Variable Number : 3
 *	Method : return the num of different states in which the inventors belongs 
 * 	Return Type : Integer
 *  Parameter : number_of_cross_state
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120625
 */ 

/* 	
 *  Variable Number : 35
 *	Method : return the data collection date - application date   (unit of day)
 * 	Return Type : Integer
 *  Parameter : patent_age
 *  Author : Ivy Hoi
 * 	Last Edit Date : 20120625
 *  Example :  IPC H01L 21/302 is re-assigned to “H01L”
 */ 
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.io.*;

public class db_patent_value {
	    /*Declare Variables*/
	    private Connection conn = null;	
		private String patent_id;
		private int number_of_cross_state=0;
		private int patent_age=0;
		private String DATACOLLECT_DATE = "May 5, 2010";

		public String getPatent_id() {
			return patent_id;
		}

		public void setPatent_id(String patent_id) {
			this.patent_id = patent_id;
		}

		public int getNumber_of_cross_state() {
			return number_of_cross_state;
		}

		public void setNumber_of_cross_state(int number_of_cross_state) {
			this.number_of_cross_state = number_of_cross_state;
		}
		
		public String getDATACOLLECT_DATE() {
			return DATACOLLECT_DATE;
		}
		
		public int getPatent_age() {
			return patent_age;
		}

		public void setPatent_age(int patent_age) {
			this.patent_age = patent_age;
		}

		//初始化
		public db_patent_value(String patent_id){	
			setPatent_id(patent_id);
			ConnectDB();
			NumberOfCrossState(getPatent_id());
			//if(getNumber_of_cross_state()>0)setNumber_of_cross_state(getNumber_of_cross_state()-1);  //if it has n states, number of different states should -1.
		}
		
		//connect to patent database
		public void ConnectDB(){                
			try {
			      Class.forName("com.mysql.jdbc.Driver");		      
			      conn = DriverManager.getConnection("jdbc:mysql://140.112.107.207/patent_value?useUnicode=true&characterEncoding=Big5","root","123456");
		    }
			 catch(ClassNotFoundException e) { 
				   System.out.println("DriverClassNotFound :"+e.toString()); 
			}
			 catch(SQLException e) {
				   System.out.println("資料庫連線失敗 : "+e.toString()); 
			}
		}
		
		//search patent_id  from DB(db_patent_value) and get the result
		public ResultSet SelectSQL(String patent_id){
			boolean is_find_result=false;
			ResultSet select_result=null;
			
			for(int year = 1976 ; year <= 2009 && is_find_result==false; year++)
			{	
					String select_SQL = "select * from uspto_"+ year + " where Patent_id =" + "'" + patent_id + "'" ;			
					try {
						Statement stmt = conn.createStatement();
						select_result = stmt.executeQuery(select_SQL); 	    
						if(select_result.next()== true)
						{
							is_find_result=true;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			
			if(is_find_result==false) return null;
			else return select_result;
		}
		
		//analysis column "Inventors" to get the number of cross state cooperation
	   public void NumberOfCrossState(String patent_id){
		   String column_inventors="";
			ResultSet select_result=SelectSQL(patent_id);
			if(select_result!=null){
				try {
					column_inventors=select_result.getString("Inventors");						
				} catch (SQLException e) {
					e.printStackTrace();
				}	 
					//column_inventors="Inventors:  Connolly; Kevin J. (Ballston Lake, NY), Bingley; George W. (Kankakee, IL), Holt; Karl K. (Hartland, WI)  Assignee:  Garden Way Incorporated (Troy, NY)     [*] Notice:  The portion of the term of this patent subsequent to September 13, 2008 has been disclaimed. Appl. No.:   07/795,630 Filed:  November 21, 1991";
					
					//"column_inventors" contains "Inventors", "Assignee", "[*] Notice:"....., but we only need info of "inventors".
					if(column_inventors.contains("Inventors: ")){ 
						String substring_of_inventors=GetSubstringOfColumnInventors(column_inventors, "Inventors");
						if(substring_of_inventors!="" || substring_of_inventors!="null") CountDifferentStates(substring_of_inventors);
						else setNumber_of_cross_state(-1);
					}
					else{
						setNumber_of_cross_state(-1);
					}
					
					if(column_inventors.contains("Filed:")){ 
						String substring_of_filed_day=GetSubstringOfColumnInventors(column_inventors, "Filed");
						if(substring_of_filed_day!="") GetPatentAge(substring_of_filed_day);
						else setNumber_of_cross_state(-1);
					}
					else{
						setNumber_of_cross_state(-1);
					}
			}
			else{
				setNumber_of_cross_state(-1);
				System.out.println("can't find patent id: "+patent_id);
			}
	   }
	  
	   //get the information of "Inventors:".   Example of result: " Kotera; Makoto (Tokyo, JP), Hikita; Sadayuki (Tokyo, JP)  Assignee"
	   public String GetSubstringOfColumnInventors(String column_inventors, String substring){
		   String substring_of_inventors="";
		   String[] column_inventors_array=column_inventors.split(":");
		   for(int i=0; i<column_inventors_array.length; i++){
			   if(column_inventors_array[i].contains(substring)){
				   if((i+1)!=column_inventors_array.length) substring_of_inventors=column_inventors_array[i+1];
			   }
		   }
		   return substring_of_inventors;
	   }
	   
	   public void CountDifferentStates(String substring_of_inventors){
		   //split("),")     -> get the info of inventors, include name and state
		   String[] inventor_array=substring_of_inventors.split("\\),");
		   String[] name_and_state_array;
		   String state_name;
		   
		   String state_array[]= new String[inventor_array.length];   //save different state from Inventors
		   
		   //split(", ")     -> get the info of state of every inventors
		   for(int i=0; i<inventor_array.length; i++){
			  if(inventor_array[i].contains(", ")){
				  name_and_state_array=inventor_array[i].split(", ");
				  int temp_length=name_and_state_array.length-1;
				  state_name=name_and_state_array[temp_length].substring(0, 2);  //取兩個字母
				  state_array=save_different_state(state_name, state_array);
			  }
			  else if((i+1)<inventor_array.length){
				  if(!inventor_array[i+1].contains(", ")){
					  inventor_array[i+1]=inventor_array[i]+"\\),"+inventor_array[i+1];
				  }
			  }
		   }
	   }
	   
	   //if it is a new state, save it to "state_array"
	   public String[] save_different_state(String state, String[] state_array){
		   int state_array_length=getNumber_of_cross_state();
		   if(state_array_length==0){
			   state_array[0]=state;
			   state_array_length++;
			   setNumber_of_cross_state(state_array_length);
		   }
		   else{
			   boolean is_different_state=true;
			   for(int i=0; i<state_array_length && is_different_state; i++){
					 if(state_array[i].equals(state)){
						 is_different_state=false;
					 }
			   }
			   if(is_different_state==true){
				   state_array[state_array_length]=state;
				   //update data
				   state_array_length++;
					setNumber_of_cross_state(state_array_length);
			   }
		   }
		   return state_array;
	   }
	   
	   public void GetPatentAge(String substring_of_inventors){

		   String application_date=substring_of_inventors.trim();
		  // application_date = application_date.replace(" ", "");
		   
		   DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
			try { 
				
					Date d1 = df.parse(application_date); 
					Date d2 = df.parse(getDATACOLLECT_DATE());
					long t1 = d2.getTime() - d1.getTime();
					long time = 1000*3600*24;
				
					if(t1/time > 0){
						int PatentDay = (int) (t1/time);
						setPatent_age(PatentDay);
					}
					else{
						setPatent_age(-1);
						System.out.println("Unable to caculate... "); 
					}
			} 
			catch(ParseException e) { 
				setPatent_age(-1);
				System.out.println("Unable to parse... "); 
			} 
	   }
	   
	   public void PrintAll(){
			System.out.println("The Patent ID you entered is: "+getPatent_id());
			System.out.println("Number of cross state: "+getNumber_of_cross_state());
			System.out.println("Patent age: "+getPatent_age());
	   }

	   public static void main(String[] args) throws IOException {				
			System.out.println("Enter a Patent ID: ");		
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
			
			String patent_id = in.readLine();			//read  paten_ id		
			
			db_patent_value cross_state = new db_patent_value(patent_id);
			cross_state.PrintAll();
	    }
}

