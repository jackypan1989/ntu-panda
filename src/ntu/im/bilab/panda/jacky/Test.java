package ntu.im.bilab.panda.jacky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String s=" Inventors:  Price; Edgar E. (Webster, NY) Appl. No.:   05/515,244 Filed:  October 16, 1974";  
		//String s1=" Inventors:  Carlson; Arthur W. (Muskegon, MI)  Assignee:  E. H. Sheldon and Company (Muskegon, MI)   Appl. No.:   05/535,172 Filed:  December 23, 1974  ";
		String str=" Inventors:  Schiefer; Harry M. (Midland, MI), Laux; Raymund W. (Munich-Karlsfeld, DT), Grosse; Dietmar W. (Munich, DT)  Assignee:  Dow Corning Corporation (Midland, MI)   Appl. No.:   05/562,292 Filed:  March 26, 1975  ";
		
	
		
		Test t = new Test();
		t.getPatentFamilySize("TEST");
		/*
		t.getInventors(str);	
		t.getForeignInventors(str);	
		t.getForeignClasses(str);	
		*/
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
		System.out.println("inventors : "+total_inventors);
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
		System.out.println("foreign_inventors : "+foreign_inventors);
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
	public int getForeignClasses(String patent_id){
		
		String data = "U.S. Patent Documents     D191499 October 1961 Donay D233586 November 1974 Kopp D261601 November 1981 Kettlestrings D287093 December 1986 Ryan 1388282 August 1921 Meredith   Foreign Patent Documents     4615 ., 1891 GB     Primary Examiner:  Burke; Wallace R. <BR> Assistant Examiner:  Tabor; Lavone D. <BR> Attorney, Agent or Firm: Zarley, McKee, Thomte, Voorhees & Sease <BR>";
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
		System.out.println("foreign_classes : "+foreign_classes);
		return foreign_classes;
	    
	    /* direct fetch from uspto web 
	    StringBuilder document= new StringBuilder();
		try {
			URL url = new URL("http://patft1.uspto.gov/netacgi/nph-Parser?patentnumber="+patent_id);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(30000);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    	document.append(line + "\n");
		    reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/  
	}
	
	/* 	
	 *  Variable Number : 6
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
			Document doc = Jsoup.connect("http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&NR=RE37489E1&FT=D").get();
			Element s = doc.getElementsByClass("epoBarItem").first().getElementsByTag("strong").first();
			patent_family_size = Integer.parseInt(s.text());
			System.out.println(s.text());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return patent_family_size;
	}
	
    /*
	public int getPatentFamilySize(String patent_id){
		int patent_family_size = 0;
		
	    StringBuilder document= new StringBuilder();
		try {
			URL url = new URL("http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="+patent_id);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(30000);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    	document.append(line + "\n");
		    reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String data = document.toString();
		
		data = data.substring(data.indexOf("epoBarItem"),data.indexOf("application"));
		Pattern p = Pattern.compile("+[0-9]");
		String patent_family = Pattern.quote(data);
		patent_family_size = Integer.valueOf(patent_family);
		
		
		return patent_family_size;
	}
	*/
}