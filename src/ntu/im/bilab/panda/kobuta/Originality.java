package ntu.im.bilab.panda.kobuta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Originality {

	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/mypaper";   //which database
	static final String DATABASE_URL2 = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	static int OLDEST_YEAR = 1976;
	static int YOUNGEST_YEAR = 2009;
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private Connection connection2 = null;
	private Statement statement2 = null;
	private ResultSet resultSet2 = null; 
	
	private String bwd_result = null;
	private String[] bwd_patent = null;
	private float OriginalityIndexIPC;
	private float OriginalityIndexUSPC;
	private float CumulateIndexIPC = 0;
	private float CumulateIndexUSPC = 0;
	private Map<String, Integer> IPCMap;
	private Map< String, Integer > USPCMap;
	private int NumberOfBwd = 0;
	private int focal_year = 0;

	
	public float GetOriIndexIPC(){
		return OriginalityIndexIPC;
	}
	public float GetOriIndexUSPC(){
		return OriginalityIndexUSPC;
	}
	
	public void Open(){
		try {
			Class.forName( DRIVER );
			connection = DriverManager.getConnection( DATABASE_URL, USERNAME, PASSWORD );
			statement = connection.createStatement();
			
		}//end try 
		catch( SQLException sqlException )
		{
			sqlException.printStackTrace();
			System.exit(1);
		}//end catch
		catch ( ClassNotFoundException classNotFound ) 
		{
			classNotFound.printStackTrace();
		}//end catch
	}
	public void Close(){
		    try
		    {
		      if(connection!=null)
		      {
		        connection.close();
		        connection = null;
		      }
		    }
		    catch(SQLException e)
		    {
		      System.out.println("Close Exception :" + e.toString());
		    }
		  }	
	public void Open2(){
		try {
			Class.forName( DRIVER );
			connection2 = DriverManager.getConnection( DATABASE_URL2, USERNAME, PASSWORD );
			statement2 = connection2.createStatement();
			
		}//end try 
		catch( SQLException sqlException )
		{
			sqlException.printStackTrace();
			System.exit(1);
		}//end catch
		catch ( ClassNotFoundException classNotFound ) 
		{
			classNotFound.printStackTrace();
		}//end catch
	}
	public void Close2(){
		    try
		    {
		      if(connection2!=null)
		      {
		        connection2.close();
		        connection2 = null;
		      }
		    }
		    catch(SQLException e)
		    {
		      System.out.println("Close Exception :" + e.toString());
		    }
		  }
	
	public void GetTable(String pid) throws SQLException
	{//search table content_1976-2009
		int year;
		
		for(year = OLDEST_YEAR ; year <= YOUNGEST_YEAR ; year++)
		{
			String selectSQL = "SELECT `References Cited` FROM content_"+ year +" WHERE `Patent_id`='"+pid+"'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSQL);			    
			if(resultSet.absolute(1)== true){
			    focal_year = year;
				break;		    
			}	
		}
	}
	public String GetMainIPClass(int pYear,String pID) throws SQLException{
		//find the Main IPC for a particular patent
		String selectSQL_IPC = "SELECT `main_IPC_class` FROM `patent-mainclass_"+ pYear +"` WHERE `Patent_id`='"+pID+"'";
		ResultSet resultIPC = statement.executeQuery(selectSQL_IPC);
		if(resultIPC.absolute(1) == true){
			String tmpIPC = resultIPC.getString("main_IPC_class");
			//System.out.println("GetMainIPC "+tmpIPC+" in "+pYear);
			return tmpIPC;
		}
		else{
			return "NULL";
		}
		 
	}
	public String GetMainUSPClass(int pYear,String pID) throws SQLException{
		//find the Main USPC for a particular patent
		String selectSQL_USPC = "SELECT `mainclass` FROM `patent-mainccl_"+ pYear +"` WHERE `Patent_id`='"+pID+"'";
		ResultSet resultUSPC = statement.executeQuery(selectSQL_USPC);
		if(resultUSPC.absolute(1) == true){
			String tmpUSPC = resultUSPC.getString("mainclass");
			//System.out.println("GetMainUSPC "+tmpUSPC+" in "+pYear);
			return tmpUSPC;
		}
		else{
			return "NULL";
		}
	}
	public void CountIPC_Origin(int NumberOfPatent){
		float tmp =(float) NumberOfPatent/NumberOfBwd;
		CumulateIndexIPC  = (float)CumulateIndexIPC + (tmp*tmp);
	}
	public void CountUSPC_Origin(int NumberOfPatent){
		float tmp =  (float)NumberOfPatent/NumberOfBwd;
		CumulateIndexUSPC  = (float)CumulateIndexUSPC + (tmp*tmp);
	}
	public void OriginIndex( String PatentID) throws SQLException{
		GetTable(PatentID);
		if(resultSet.absolute(1) != false){
			bwd_result = resultSet.getString("References Cited");
			IPCMap = new HashMap<String, Integer>();
			USPCMap = new HashMap<String, Integer>();
			
			if(bwd_result == null || bwd_result.equals("NULL")){ //if focal patent's "references cited" is null
				OriginalityIndexIPC = 0;
				OriginalityIndexUSPC = 0;
				System.out.println("This Patent"+PatentID+"didn't cite other patent");
			}
			else if(focal_year == OLDEST_YEAR){
				bwd_patent = bwd_result.split(";");
				NumberOfBwd = bwd_patent.length;
				System.out.println("Sorry! Database only implement patents from 1976~2009....");
			}
			else{
				bwd_patent = bwd_result.split(";");
				NumberOfBwd = bwd_patent.length;
				//System.out.println("Number of BWD "+NumberOfBwd);
				
				for (int i=0; i < NumberOfBwd; i++) { //for all bwd-citation patents, find its' IPC and USPC, calculate Originality Index 
					//System.out.println("Search bwd patent "+bwd_patent[i]+".....");
					int flag = 0;
					int flag2 = 0;
					for (int year = OLDEST_YEAR; year <= focal_year; year++) {	
							String bwd_IPC = GetMainIPClass(year, bwd_patent[i]);
							String bwd_USPC = GetMainUSPClass(year, bwd_patent[i]);
							
							//build Hashmap for counting the number of different class that bwdcitations have cited
							if(!bwd_IPC.equals("NULL") && bwd_IPC != null ){
								if(IPCMap.containsKey(bwd_IPC)){
									int value = IPCMap.get(bwd_IPC);
									value++;
									IPCMap.put(bwd_IPC, value);
								}
								else{
									IPCMap.put(bwd_IPC, 1);
								}
								flag = 1;
								//System.out.println(bwd_patent[i]+"'s IP Class is "+bwd_IPC);
							}
							
							if(!bwd_USPC.equals("NULL") && bwd_USPC != null){
								if(USPCMap.containsKey(bwd_USPC)){
									int value = USPCMap.get(bwd_USPC);
									value++;
									USPCMap.put(bwd_USPC, value);
								}
								else{
									USPCMap.put(bwd_USPC, 1);
								}
								flag2 = 1;
								//sSystem.out.println(bwd_patent[i]+"'s USPC Class is "+bwd_USPC);
							}
							
							if(flag*flag2!=0){// if both class had been found.
								break;
							}
					}//end for
					if(flag*flag2 == 0){
						//System.out.println("This Patent "+bwd_patent[i]+" is not in the database");
					}
					
				}//end for
				
				
				if(!IPCMap.isEmpty()){
					//System.out.println("HERE");
					Collection<Integer> collectIPC = IPCMap.values();
					Iterator<Integer> iteIPC = collectIPC.iterator();
					while(iteIPC.hasNext()){
						CountIPC_Origin(iteIPC.next());
						//System.out.println("THIS "+iteIPC.next());
					}
					OriginalityIndexIPC = 1-CumulateIndexIPC;
					CumulateIndexIPC =0;
				}
				if(!USPCMap.isEmpty()){	
					Collection<Integer> collectUSPC = USPCMap.values();
					Iterator<Integer> iteUSPC = collectUSPC.iterator();
					
					while(iteUSPC.hasNext()){
						CountUSPC_Origin(iteUSPC.next());
						//System.out.println("AND"+ iteUSPC.next());
					}
					OriginalityIndexUSPC = 1- CumulateIndexUSPC;
					CumulateIndexUSPC =0;
				}
				
			}//end else
		}
		else{
			OriginalityIndexUSPC =-1;
			OriginalityIndexIPC  =-1;
		}
		
		
		
	}//end OriginIndex
	public void DBUpdate()throws SQLException{

		Open2();
		Open();
			
		String selectSQL2 = "SELECT Patent_id FROM attacker_expert";
		statement2 = connection2.createStatement();
		resultSet2 = statement2.executeQuery(selectSQL2);			    
		List<String> patent_id_list = new LinkedList<String>(); 
				
		while(resultSet2.next()) {
			patent_id_list.add(resultSet2.getString("Patent_id").trim());
		}
				
		Iterator<String> pItr = patent_id_list.iterator();
		while(pItr.hasNext()){
			String patentID = pItr.next();
			System.out.println("For patent "+patentID +": ");
			OriginIndex(patentID);
			statement2.executeUpdate("UPDATE attacker_expert SET originality_IPC = '"+OriginalityIndexIPC+"', originality_USPC = '"+ OriginalityIndexUSPC +"' WHERE Patent_id = '"+patentID+"'");
			//System.out.println(patentID+"->"+new_patent_id);
		}		
		System.out.println("Program End");
		Close();
		Close2();
	}
	
	
	
	public static void main(String[] args) throws SQLException {
		//String patentID = "4490855";
		Originality origin = new Originality();
		origin.DBUpdate();
	}//end main

}//end BwdSelfCiation
