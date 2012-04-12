package ntu.im.bilab.panda.database;

public class Entry {
	private String patent_id=null,pClaim=null;	
	public Entry()
	{
		
		
	}
	public Entry(String pid,String Claim){
		patent_id = pid;
		pClaim = Claim;
			
	}
	public String GetClaim(){
		
		return pClaim;
	}
	public String GetPatentid(){
		
		return patent_id;
	}
	
}
