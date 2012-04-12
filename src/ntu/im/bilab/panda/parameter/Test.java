package ntu.im.bilab.panda.parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
	
	private int foreign_inventors;
	
	/**
	 * @param args
	 */
	
	
	
	
	
	
	public int getForeignInventors(String patent_id){
		
		
		
		
		
		return foreign_inventors;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Enter a Patent ID: ");		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
		
		//read in patent id		
		String patent_id;
		try {
			patent_id = in.readLine();
			Test test = new Test();
			test.getForeignInventors(patent_id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

}
