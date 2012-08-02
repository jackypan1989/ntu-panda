package ntu.im.bilab.panda.jacky;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ntu.im.bilab.panda.parameter.ApplicabilityIntegrity;
import ntu.im.bilab.panda.parameter.Diversity;
import ntu.im.bilab.panda.parameter.Innovation;
import ntu.im.bilab.panda.parameter.Profile;

/* 
 * this class is used for patent entity, and reflect the data and parameters
 * Author : r00 jackypan1989@gmail.com 
 */

public class Patent {
	// patent id and year from USPTO
	private String id;
	private String year;
	
	// database content from database "140.112.107.207/mypaper"
	private ResultSet new_data;
	
	// database content from database "140.112.107.207/patent_value"
	private ResultSet old_data;
	
	/* info is a basic information map for this patent
	 * id, date, inventors, assignees, abstract, claims, description, summary, title
	 */
	private Map<String, String> info = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();
	

	// 1 parameter from r00(Ivy)
	private int parameter_patent_family_volume;




	// constructor
	public Patent(String patent_id) {
		id = patent_id;
		fetchDataFromDb(id);
		setInfo();
		setParams();
	}
	
	public void fetchDataFromDb(String patent_id) {
		DataBaseFetcher dbf = new DataBaseFetcher();
		dbf.getPatentData(this, patent_id);
		dbf.close();
	}
	
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	
	public void setInfo() {
		try {
			info.put("id", new_data.getString("Patent_id"));
			info.put("date", new_data.getString("Issued_Date"));
			info.put("year", new_data.getString("Issued_Year"));
			info.put("inventors", new_data.getString("Inventors"));
			info.put("assignees", new_data.getString("Assignee"));
			info.put("abstract", new_data.getString("Abstract"));
			info.put("claims", new_data.getString("Claims"));
			info.put("description", new_data.getString("Description"));
			info.put("summary", new_data.getString("Summary"));
			info.put("title", new_data.getString("Title"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getInfo() {
		return info;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public void setParams() {
		// get parameters from r99 lab
		String patent_id = this.getId();
		Innovation inno = new Innovation(patent_id);
		Profile prof = new Profile(patent_id);
		Diversity div = new Diversity(patent_id);
		ApplicabilityIntegrity AI = new ApplicabilityIntegrity(patent_id);
        
		// put r99's parameters to params map		
		//params.put("inventors",""+prof.GetInventors());
		params.put("diversity_USPC",""+div.GetTechScope());
		params.put("num_of_claims",""+AI.NoClaims());
		params.put("num_of_indep_claims",""+AI.NoIndepClaim());
		params.put("num_of_dep_claims",""+AI.NoDepClaim());
		params.put("num_of_bwd_citations",""+inno.BackwardCitations());
		params.put("science_linkage",""+inno.ScienceLinks());
		params.put("originality_USPC",""+div.GetOriginality());
		params.put("generality_USPC","-1");
		params.put("extensive_generality","-1");
		params.put("num_of_assignee_transfer",""+AI.NoTransAs());
		params.put("num_of_patent_group",""+inno.PatentGroups());
		params.put("approval_time",""+prof.GetApproveTime());
		params.put("num_of_assignee",""+prof.GetAssignee());
		params.put("num_of_citing_USpatent",""+prof.GetCitation());
		
		// get parameter from r00 jacky and put into map
		ParameterFinder pf = new ParameterFinder(patent_id);
		try {
			params.put("inventors",""+pf.getInventors(old_data.getString("Inventors")));
			params.put("foreign_inventors",""+pf.getForeignInventors(old_data.getString("Inventors")));
			params.put("foreign_classes",""+pf.getForeignClasses(old_data.getString("References Cited")));
			params.put("family_size",""+pf.getPatentFamilySize(patent_id));
			params.put("patented_bwd_citations",""+pf.getPatentedBackwardCitations(patent_id));
			params.put("major_market",""+pf.getMajorMarket(patent_id));
			params.put("foreign_priority_Apps",""+pf.getForeignPriorityApps(old_data.getString("Current U.S. Class")));
			params.put("years_receive_first_citations",""+pf.getYearsToReceiveTheFirstCitation(this));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	

	public ResultSet getNew_data() {
		return new_data;
	}

	public void setNew_data(ResultSet new_data) {
		this.new_data = new_data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ResultSet getOld_data() {
		return old_data;
	}

	public void setOld_data(ResultSet old_data) {
		this.old_data = old_data;
	}

	// ivy
	public void setParameter_patent_family_volume(
			int parameter_patent_family_volume) {
		this.parameter_patent_family_volume = parameter_patent_family_volume;
	}
	

	public int getParameter_patent_family_volume() {
		return parameter_patent_family_volume;
	}
	
	public static void main(String[] args)
	{
		Patent p = new Patent("5110638");
		//HashMap<String,String> m = (HashMap<String, String>) p.getInfo();
		HashMap<String,String> s = (HashMap<String, String>) p.getParams();
		
		Iterator<String> iterator = s.keySet().iterator();  
		   
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   String value = s.get(key).toString();  
		   
		   System.out.println(key + " " + value);  
		}  
		
	}
}
