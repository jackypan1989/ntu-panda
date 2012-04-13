package ntu.im.bilab.panda.core;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String s=" Inventors:  Price; Edgar E. (Webster, NY) Appl. No.:   05/515,244 Filed:  October 16, 1974";  
		//String s1=" Inventors:  Carlson; Arthur W. (Muskegon, MI)  Assignee:  E. H. Sheldon and Company (Muskegon, MI)   Appl. No.:   05/535,172 Filed:  December 23, 1974  ";
		String s2=" Inventors:  Schiefer; Harry M. (Midland, MI), Laux; Raymund W. (Munich-Karlsfeld, DT), Grosse; Dietmar W. (Munich, DT)  Assignee:  Dow Corning Corporation (Midland, MI)   Appl. No.:   05/562,292 Filed:  March 26, 1975  ";
				
		Test t = new Test();
		t.getInventors(s2);	
		t.getForeignInventors(s2);	
	}
    
	/* 	
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
}