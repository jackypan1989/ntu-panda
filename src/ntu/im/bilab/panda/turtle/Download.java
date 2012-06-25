package ntu.im.bilab.panda.turtle;

import java.io.*;
import java.net.*;

public class Download {

	public static void main(String[] args){
		//getMainIpc("6,923,014");
		//getMainIpc("D339,456");
		//getMainIpc("D491673");
	}
	
	static String getMainIpc(String patent_id){
		String main_ipc = "";
		
		try {
			URL url = new URL("http://patft.uspto.gov/netacgi/nph-Parser" +
					"?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm" +
					"&r=1&f=G&l=50&s1="+patent_id+".PN.&OS=PN/"+patent_id+"&RS=PN/"+patent_id);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			boolean isFindIPC = false;
			String line = null;
						
			while ((line = reader.readLine()) != null){
				if(line.contains("Current International Class:")){ //<TD VALIGN=TOP ALIGN="RIGHT" WIDTH="80%">2502</TD></TR>
					//System.out.println(line);
					isFindIPC = true;
					line = reader.readLine();
					//System.out.println(line);
					if(!line.contains("</TD>")){
						line = reader.readLine();
						//System.out.println(line);			
					}
					break;	
				}				
			}
			
			if(isFindIPC)
				main_ipc = parseIpc(line);
			//System.out.println(main_ipc);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return main_ipc.trim();
	}
	
	static String parseIpc(String str){
		String main_ipc = "";
		//str = "<TD VALIGN=TOP ALIGN=\"RIGHT\" WIDTH=\"80%\">2502</TD></TR>";
		if(str.contains("<TD")){
			String strList[] = str.trim().split(";");
		    strList = strList[0].split(">");
		    strList = strList[1].split(" ");
		    strList = strList[0].split("<");
			main_ipc = strList[0];
		}else{
			String strList[] = str.trim().split(";");
			if(strList[0].contains("</TD>"))
				strList = strList[0].split("<");
			else
				strList = strList[0].split(" ");
			main_ipc = strList[0];
		}
		
		return main_ipc;
	}
}
