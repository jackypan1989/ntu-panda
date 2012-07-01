package ntu.im.bilab.panda.parameter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Innovation {

	static final String DRIVER = "com.mysql.jdbc.Driver";
	static final String DATABASE_URL = "jdbc:mysql://140.112.107.207/patent_value";   //which database
	//static final String DATABASE_URL = "jdbc:mysql://daventu.no-ip.org/patent_value";   //which database
	static final String USERNAME = "root";
	static final String PASSWORD = "123456";
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null; 
	private String innovationDataTemp;
	private String[] innovationDataSplit = new String[5];
	private String USPatent = null;
	private String ForeignPatent = null;
	private String OtherREF = null;
	private int USPatentNum=0;
	private int ForeignPatentNum=0;
	private int OtherREFNum=0;
	
	 private String selectSQL;
	//constructor
	 public Innovation(){
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
		}//end innovation constructor
	 
	 
	public Innovation(String Patent_id){
		try {
			Class.forName( DRIVER );
			connection = DriverManager.getConnection( DATABASE_URL, USERNAME, PASSWORD );
			statement = connection.createStatement();
			this.parseInnovationData(Patent_id);
			
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
	}//end innovation constructor
	
	public void GetTable(String pid)
	{
		int year;
		String table_name = "";
		for(year = 1976 ; year <= 2009 ; year++)
		{
			//if(year==1998 || year==2002 || year==2003)continue;
			try
			{
				//selectSQL = "select References Cited,Patent_id, Claims from uspto_"+ year + " where Patent_id =" + "'" + pid + "'" ;
				selectSQL = "SELECT `References Cited` FROM uspto_"+ year +" WHERE `Patent_id`='"+pid+"'";
				statement = connection.createStatement();
				resultSet = statement.executeQuery(selectSQL);			    
			    if(resultSet.absolute(1)== true)
			    	break;		    
			}
			catch(SQLException e){System.out.println(e.toString());}		
		}
		
		//table_name = "uspto_" + year;	
		//return table_name;
	}
	public void parseInnovationData( String PatentID ){
		GetTable(PatentID);
		try {
			//resultSet = statement.executeQuery( "SELECT `References Cited` FROM uspto_1980 WHERE `Patent_id`='"+PatentID+"'" );
			resultSet = statement.executeQuery(selectSQL);
			
			//process query results
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();     

	        while ( resultSet.next() )    //print result of every column
	        {
	        	for ( int i = 1; i <= numberOfColumns; i++ ){
	        	   innovationDataTemp = resultSet.getObject( i ).toString();
	               //System.out.printf( "%-8s\t", resultSet.getObject( i ) );
	        	}//end for
	        } // end while
	        //System.out.println();
	        
		} //end try
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}//end catch
		
		
		//§R°£et al.
		Pattern expression1 = Pattern.compile( "et al." );
		Matcher matcher1 = expression1.matcher( innovationDataTemp );
		innovationDataTemp = matcher1.replaceAll("");
        
		//find three parameters
		innovationDataSplit = innovationDataTemp.split("Primary Examiner:");
        innovationDataSplit = innovationDataSplit[0].split("<BR> Other References <BR>"); 
        if( innovationDataSplit.length>1 ) OtherREF = innovationDataSplit[innovationDataSplit.length-1];
        else OtherREF="No Data";
        
        innovationDataSplit = innovationDataSplit[0].split("Foreign Patent Documents     ");  
        if( innovationDataSplit.length>1 ) ForeignPatent = innovationDataSplit[innovationDataSplit.length-1];
        else ForeignPatent="No Data";
        
        innovationDataSplit = innovationDataSplit[0].split("U.S. Patent Documents     ");   
        if( innovationDataSplit.length>1 ) USPatent = innovationDataSplit[innovationDataSplit.length-1];
        else USPatent="No Data";
        //System.out.println(OtherREF);
        //System.out.println(ForeignPatent);
        //System.out.println(USPatent);
        
        //U.S Patent
        if( USPatent.equals("No Data")==false ){
        	while( USPatent.length() != 0 ){
        		int x=0;
                x=USPatent.indexOf(" ",0);
                
                if( USPatent.substring(0,x).matches("[a-z]|[0-9]+") && USPatent.substring(0,x).length()== 7  ){
                	//System.out.println(USPatent.substring(0,x));
                	USPatentNum++;
                	USPatent = USPatent.substring(x+1);
                }//end if
                else{
                	USPatent = USPatent.substring(x+1);
                }//end else                        
            }//end while   
        }//end if

        //foreign patent
        if( ForeignPatent.equals("No Data")==false ){
        	while( ForeignPatent.indexOf(" ",0)!= -1 ){
        		int x=0;
                x=ForeignPatent.indexOf(" ",1);
                x=ForeignPatent.indexOf(" ",x+1);
                x=ForeignPatent.indexOf(" ",x+1);
                x=ForeignPatent.indexOf(" ",x+1);
                  
                if(x==-1) break;

                ForeignPatent = ForeignPatent.substring(x+1);         
                ForeignPatentNum++;
        	}//end while   
        }//end if
       
        //other reference
        if( OtherREF.equals("No Data")==false ){
        	String[] other = new String[50];
        	int y;
        	y = OtherREF.lastIndexOf("<BR>");
        	OtherREF = OtherREF.substring(0,y-1);
        	other = OtherREF.split("<BR>");
        	
        	OtherREFNum = other.length;
        }//end if
       
	}//end parseInnovationData
	
	//U.S. Patent Documents, Foreign Patent Documents ,Other Publication
	public int PatentGroups()
	{
		return USPatentNum;
	}//end PatentGroups
	
	public int PatentedBackwardCitations()
	{
		return USPatentNum+ForeignPatentNum;
	}//end PatentedBackwardCitations
	
	public int ScienceLinks()
	{
		return OtherREFNum;
	}//end ScienceLinks
	
	public int BackwardCitations()
	{
		return USPatentNum+ForeignPatentNum+OtherREFNum;
	}//end BackwardCitations
	
	public void printAll()
	{
		System.out.println("Patent Groups: "+PatentGroups());
		System.out.println("PatentedBackwardCitations: "+PatentedBackwardCitations());
		System.out.println("ScienceLinks: "+ScienceLinks());
		System.out.println("BackwardCitations: "+ BackwardCitations());
		System.out.println("");
		
	}
	
	public static void main(String[] args) {
		//Innovation application = new Innovation();
		String patent = "7472125";
		//application.parseInnovationData( patent );   //inset patent id
		//System.out.println(application.PatentGroups());
		//System.out.println("me"+application.PatentedBackwardCitations());
		//System.out.println(application.ScienceLinks());
	}//end main

}//end innovation
