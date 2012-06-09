package ntu.im.bilab.panda.jacky;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParameterFinder {
	private static String ec;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParameterFinder t = new ParameterFinder();
		
		Patent patent = new Patent("PP5621");
		String patent_id = patent.getId();
		ResultSet new_data = patent.getNewData();
		ResultSet old_data = patent.getOldData();
		
		t.getEC(patent_id);
		try {
			patent.setParameterForeignInventors(t.getForeignInventors(old_data.getString("Inventors")));
			patent.setParameterForeignClasses(t.getForeignClasses(old_data.getString("References Cited")));
			patent.setParameterPatentFamilySize(t.getPatentFamilySize(patent_id));
			patent.setParameterPatentedBackwardCitations(t.getPatentedBackwardCitations(patent_id));
			patent.setParameterMajorMarket(t.getMajorMarket(patent_id));
			patent.setParameterForeignPriorityApps(t.getForeignPriorityApps(old_data.getString("Current U.S. Class")));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		patent.setParameterYearsToReceiveTheFirstCitation(t.getYearsToReceiveTheFirstCitation(patent));
		
		System.out.println(patent.toString());
		
	}
	
	/* 	
	 *  Variable Number : 1
	 *	Method : return the amount of inventors
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120413
	 *  Example : " Inventors:  Carlson; Arthur W. (Muskegon, MI)  Assignee:  E. H. Sheldon and Company (Muskegon, MI)   Appl. No.:   05/535,172 Filed:  December 23, 1974  "
	 */ 
	public int getInventors(String data){
		
		// clarify the data 
		// some data loses the inventors field, some data has no assignee 
		if(!data.contains("Inventors")){
			return 0;
		}else if(!data.contains("Assignee")){
			data = data.substring(data.indexOf("Inventors")+10,data.indexOf("Appl"));
		}else{
			data = data.substring(data.indexOf("Inventors")+10,data.indexOf("Assignee"));
		}
		
		// split into inventors array
		String[] inventors = data.split(";");
		
		// get the amount of inventors
		int total_inventors = inventors.length-1;
		//System.out.println("inventors : "+total_inventors);
		return total_inventors;
	}		
	
	/* 	
	 *  Variable Number : 2
	 *	Method : return the amount of foreign inventors by U.S. state code
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120413
	 *  Example : " Inventors:  Carlson; Arthur W. (Muskegon, MI)  Assignee:  E. H. Sheldon and Company (Muskegon, MI)   Appl. No.:   05/535,172 Filed:  December 23, 1974  "
	 */ 
	public int getForeignInventors(String data){
		
		// clarify the data 
		// some data loses the inventors field, some data has no assignee 
		if(!data.contains("Inventors")){
			return 0;
		}else if(!data.contains("Assignee")){
			data = data.substring(data.indexOf("Inventors")+10,data.indexOf("Appl"));
		}else{
			data = data.substring(data.indexOf("Inventors")+10,data.indexOf("Assignee"));
		}
		
		// split into inventors array
		String[] inventors = data.split(";");
		int total_inventors = inventors.length-1;
		int local_inventors = 0;
		int foreign_inventors = 0;
		
		// uspto patent state code in the U.S.
		String[] us_states = {"AK","AL","AR","AZ","CA","CO","CT","CZ","DC","DE",
				              "FL","GA","HI","IA","ID","IL","IN","KS","KY","LA",
				              "MA","MD","ME","MI","MN","MO","MS","MT","NE","NC",
				              "ND","NH","NJ","NM","NY","NV","OH","OK","OR","PA",
				              "PR","RI","SC","SD","TN","TX","UT","VA","VI","VT",
				              "WA","WI","WY","NB"}; 
		
		// find all local inventors
		for (int i=0 ; i<inventors.length ; i++){
			if(inventors[i].contains(")")){
				// fetch the code , ex: (Taipei, TW) => TW
				String s = inventors[i].substring(inventors[i].indexOf(")")-2 , inventors[i].indexOf(")"));
				for(String state : us_states){
					// check whether in the U.S.
					if(state.equals(s)){
						local_inventors++;
						break;
					}
				}
			}
		}
		
		// get the amount of foreign inventors
		foreign_inventors  = total_inventors - local_inventors;
		//System.out.println("foreign_inventors : "+foreign_inventors);
		return foreign_inventors;
	}
	
	/* 	
	 *  Variable Number : 6
	 *	Method : return the amount of foreign cited patent by uspto foreign document field(?) 
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120425
	 *  Example : "U.S. Patent Documents     D191499 October 1961 Donay D233586 November 1974 Kopp D261601 November 1981 Kettlestrings D287093 December 1986 Ryan 1388282 August 1921 Meredith   Foreign Patent Documents     4615 ., 1891 GB     Primary Examiner:  Burke; Wallace R. <BR> Assistant Examiner:  Tabor; Lavone D. <BR> Attorney, Agent or Firm: Zarley, McKee, Thomte, Voorhees & Sease <BR>";
	 */ 
	public int getForeignClasses(String data){
		
		int foreign_classes = 0;
		
	    // clarify data, some with other references 
	    if(data.contains("Foreign Patent Documents")){
			if(data.contains("Other References")){
				data = data.substring(data.indexOf("Foreign Patent Documents")+25,data.indexOf("Other References"));	
			}else{
				data = data.substring(data.indexOf("Foreign Patent Documents")+25,data.indexOf("Primary Examiner"));
			}
			
			// calculate if there is foreign patent
			String[] foreign_patents = data.split(",");
			foreign_classes = foreign_patents.length-1;
			
		}
		//System.out.println("foreign_classes : "+foreign_classes);
		return foreign_classes;
	}
	
	/* 	
	 *  Variable Number : 8
	 *	Method : return the size of patent family from EPO  
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120430
	 *  Example : "http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&NR=RE37489E1&FT=D";
	 */ 
	public int getPatentFamilySize(String patent_id){
	    int patent_family_size = 0;
	    
	    // fetch from EPO
		try {
			if(ec.equals("")) return -1;
			Document doc = Jsoup.connect("http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="+patent_id+ec).get();
			// find the amount of patent family size
			Element data = doc.getElementsByClass("epoBarItem").first().getElementsByTag("strong").first();
			patent_family_size = Integer.parseInt(data.text());
			//System.out.println("patent family size : "+patent_family_size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			return -1;
			//e.printStackTrace();
		}
		
		return patent_family_size;
	}
	
	/* 	
	 *  Variable Number : 9
	 *	Method : return of the parameter of major market(US,EU,JP)   
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120430
	 *  Example : "http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&NR=RE37489E1&FT=D";
	 */ 
	public int getMajorMarket(String patent_id){
	    int major_market = 1;
	    // fetch from EPO
		try {
			if(ec.equals("")) return -1;
			Document doc = Jsoup.connect("http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="+patent_id+ec).get();
			// find the amount of patent family size
			Elements elements = doc.getElementsByClass("publicationInfoColumn");
			String data = elements.text();
			if(data.contains("EP")) major_market++;
			if(data.contains("JP")) major_market++;
			
			//major_market = data.text();
			//System.out.println("major_market : "+major_market);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return major_market;
	}
	
	/* 	
	 *  Variable Number : 15
	 *	Method : return the amount of backward citations (foreign & US) from EPO   
	 * 	Return Type : Integer 
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120511
	 *  Example : "http://worldwide.espacenet.com/publicationDetails/citedDocuments?CC=US&NR=RE37489E1&FT=D";
	 */ 
	public int getPatentedBackwardCitations(String patent_id){
	    int patented_backward_citations = 0;
		
	    // fetch from EPO
		try {	
			if(ec.equals("")) return -1;
			Document doc = Jsoup.connect("http://worldwide.espacenet.com/publicationDetails/citedDocuments?CC=US&FT=D&NR="+patent_id+ec).timeout(30000).get();
			// find the amount of backward citations
			Element element = doc.getElementsByClass("epoBarItem").first().getElementsByTag("strong").first();
			patented_backward_citations = Integer.parseInt(element.text());
			//System.out.println("patented backward citations : "+patented_backward_citations);
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return patented_backward_citations;
	}
	
	
	/* 	
	 *  Variable Number : 52
	 *	Method : return the number of the foreign priority applications from USPTO   
	 * 	Return Type : Integer
	 *  Author : Guan-Yu Pan
	 * 	Last Edit Date : 20120606
	 */
	public int getForeignPriorityApps(String data){
	    int foreign_priority_apps = 0;
	    
	    // clarify data if it includes foreign application priority data
	    if(data.contains("Foreign Application Priority Data")){
			// calculate
			String[] foreign_patents = data.split("\\[");
			foreign_priority_apps = foreign_patents.length-1;
		}
	    
		return foreign_priority_apps;
	}
	
	public int getYearsToReceiveTheFirstCitation(Patent patent){
	    int years_to_receive_the_first_citation = 0;
		
	    DataBaseFetcher dbf = new DataBaseFetcher();
	    int year = dbf.getYear(patent,"years_to_receive_the_first_citation");
	    
	    // not yet receive the first citation
	    if(year == -1) return -1;
	    years_to_receive_the_first_citation = year-Integer.parseInt(patent.getYear());
		
		return years_to_receive_the_first_citation;
	}
	
	
	public void getEC(String patent_id){
		try {
			Document doc = Jsoup.connect("http://worldwide.espacenet.com/searchResults?query=US"+patent_id).get();
			Element element = doc.getElementsByClass("publicationInfoColumn").first();
			String data = element.text();
			if(data!=null && data.contains(patent_id)){
				data = data.substring(data.indexOf(patent_id));
				if(data.contains("(") && data.contains(")")){
					ec = data.substring(data.indexOf("(")+1,data.indexOf(")"));
				}
			}
			System.out.println("ec for EPO : "+ec);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			ec = "";
			System.out.println(patent_id+" cant get ec");
			//e.printStackTrace();
		}
	}
}
