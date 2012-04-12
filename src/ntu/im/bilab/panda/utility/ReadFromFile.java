package ntu.im.bilab.panda.utility;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author javadb.com
 */
public class ReadFromFile {
    ArrayList<String> Result = new ArrayList<String>();
	public void setResult(ArrayList<String> result)
	{
		this.Result = result;
		
	}
	public ArrayList<String> GetResult()
	{
		
		return Result;
	}
    /**
     * Reads text from a file line by line
     */
    public void readFromFile(String filename) {
        
        BufferedReader bufferedReader = null;
        
        try {
            
            //Construct the BufferedReader object
            bufferedReader = new BufferedReader(new FileReader(filename));
            
            ArrayList<String> temp = new ArrayList<String>(); 
            String line = null;
            String[] result = null;
            while ((line = bufferedReader.readLine()) != null) {
            	
            		temp.add(line.trim());
          	            	
            }
            setResult(temp);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedReader
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ReadFromFile().readFromFile("Result100000-0.txt.txt");
    }
}
