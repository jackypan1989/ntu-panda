package ntu.im.bilab.panda.utility;

import java.util.ArrayList;

import ntu.im.bilab.panda.database.JdbcMysql;

public class RemoveProblemNo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ReadFromFile RF = new ReadFromFile();
		RF.readFromFile("unworthy_patents(problemNO).txt");
		ArrayList<String> R = new ArrayList<String>();
		R =RF.GetResult();
		
		JdbcMysql DB = new JdbcMysql();
		for(String str:R){
			DB.DeleteNo("unworthy_patents", str);			
		}
		
	}

}
