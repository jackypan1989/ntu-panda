package ntu.im.bilab.panda.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ntu.im.bilab.panda.parameter.Diversity;
import ntu.im.bilab.panda.parameter.Innovation;
import ntu.im.bilab.panda.parameter.Profile;
import ntu.im.bilab.panda.parameter.ApplicabilityIntegrity;

public class Main {
	public static String RemoveComma(String str)
	{
		
		return str.replace(",", "");
	}
	public static void main(String[] args) 
	{
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//read in patent id
		String pid = null;
		try {
			pid = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		//4,208,350 , 	7640076
		Innovation inno = new Innovation();
		inno.parseInnovationData(pid);
		inno.printAll();
		Profile prof = new Profile(pid);
		prof.SelectTable();
		prof.PrintAll();
		Diversity div = new Diversity(pid);
		div.PrintAll();
		ApplicabilityIntegrity AI = new ApplicabilityIntegrity(pid);
		AI.PrintAll();
		
		
	}
}
