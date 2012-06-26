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
	
	// 8 parameters from r00(jackypan1989)
	private int parameter_inventors;
	private int parameter_foreign_inventors;
	private int parameter_foreign_classes;
	private int parameter_patent_family_size;
	private int parameter_patented_backward_citations;
	private int parameter_major_market;
	private int parameter_foreign_priority_apps;
	private int parameter_years_to_receive_the_first_citation;

	// 1 parameter from r00(Ivy)
	private int parameter_patent_family_volume;

	// 14 old parameters from r99 lab
	private int parameter_diversity_USPC;
	private int parameter_num_of_claims;
	private int parameter_num_of_indep_claims;
	private int parameter_num_of_dep_claims;
	private int parameter_num_of_bwd_citations;
	private int parameter_science_linkage;
	private int parameter_originality_USPC;
	private int parameter_generality_USPC;
	private int parameter_extensive_generality;
	private int parameter_num_of_assignee_transfer;
	private int parameter_num_of_patent_group;
	private long parameter_approval_time;
	private int parameter_num_of_assignee;
	private int parameter_num_of_citing_USpatent;

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
		dbf.Close();
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
		params.put("inventors",""+prof.GetInventors());
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
	
	public void getOldParameter() {
		String patent_id = this.getId();
		Innovation inno = new Innovation(patent_id);
		Profile prof = new Profile(patent_id);
		Diversity div = new Diversity(patent_id);
		ApplicabilityIntegrity AI = new ApplicabilityIntegrity(patent_id);

		parameter_inventors = prof.GetInventors();
		parameter_diversity_USPC = div.GetTechScope();
		parameter_num_of_claims = AI.NoClaims();
		parameter_num_of_indep_claims = AI.NoDepClaim();
		parameter_num_of_dep_claims = AI.NoIndepClaim();
		parameter_num_of_bwd_citations = inno.BackwardCitations();
		parameter_science_linkage = inno.ScienceLinks();
		parameter_originality_USPC = div.GetOriginality();
		parameter_generality_USPC = div.GetGenerality();
		parameter_extensive_generality = -1;
		parameter_num_of_assignee_transfer = AI.NoTransAs();
		parameter_num_of_patent_group = inno.PatentGroups();
		parameter_approval_time = prof.GetApproveTime();
		parameter_num_of_assignee = prof.GetAssignee();
		parameter_num_of_citing_USpatent = prof.GetCitation();
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

	public int getParameter_foreign_inventors() {
		return parameter_foreign_inventors;
	}

	public void setParameter_foreign_inventors(int parameter_foreign_inventors) {
		this.parameter_foreign_inventors = parameter_foreign_inventors;
	}

	public int getParameter_foreign_classes() {
		return parameter_foreign_classes;
	}

	public void setParameter_foreign_classes(int parameter_foreign_classes) {
		this.parameter_foreign_classes = parameter_foreign_classes;
	}

	public int getParameter_patent_family_size() {
		return parameter_patent_family_size;
	}

	public void setParameter_patent_family_size(int parameter_patent_family_size) {
		this.parameter_patent_family_size = parameter_patent_family_size;
	}

	public int getParameter_patented_backward_citations() {
		return parameter_patented_backward_citations;
	}

	public void setParameter_patented_backward_citations(
			int parameter_patented_backward_citations) {
		this.parameter_patented_backward_citations = parameter_patented_backward_citations;
	}

	public int getParameter_major_market() {
		return parameter_major_market;
	}

	public void setParameter_major_market(int parameter_major_market) {
		this.parameter_major_market = parameter_major_market;
	}

	public int getParameter_foreign_priority_apps() {
		return parameter_foreign_priority_apps;
	}

	public void setParameter_foreign_priority_apps(
			int parameter_foreign_priority_apps) {
		this.parameter_foreign_priority_apps = parameter_foreign_priority_apps;
	}

	public int getParameter_years_to_receive_the_first_citation() {
		return parameter_years_to_receive_the_first_citation;
	}

	public void setParameter_years_to_receive_the_first_citation(
			int parameter_years_to_receive_the_first_citation) {
		this.parameter_years_to_receive_the_first_citation = parameter_years_to_receive_the_first_citation;
	}

	public int getParameter_inventors() {
		return parameter_inventors;
	}

	public void setParameter_inventors(int parameter_inventors) {
		this.parameter_inventors = parameter_inventors;
	}

	public int getParameter_diversity_USPC() {
		return parameter_diversity_USPC;
	}

	public void setParameter_diversity_USPC(int parameter_diversity_USPC) {
		this.parameter_diversity_USPC = parameter_diversity_USPC;
	}

	public int getParameter_num_of_claims() {
		return parameter_num_of_claims;
	}

	public void setParameter_num_of_claims(int parameter_num_of_claims) {
		this.parameter_num_of_claims = parameter_num_of_claims;
	}

	public int getParameter_num_of_indep_claims() {
		return parameter_num_of_indep_claims;
	}

	public void setParameter_num_of_indep_claims(
			int parameter_num_of_indep_claims) {
		this.parameter_num_of_indep_claims = parameter_num_of_indep_claims;
	}

	public int getParameter_num_of_dep_claims() {
		return parameter_num_of_dep_claims;
	}

	public void setParameter_num_of_dep_claims(int parameter_num_of_dep_claims) {
		this.parameter_num_of_dep_claims = parameter_num_of_dep_claims;
	}

	public int getParameter_num_of_bwd_citations() {
		return parameter_num_of_bwd_citations;
	}

	public void setParameter_num_of_bwd_citations(
			int parameter_num_of_bwd_citations) {
		this.parameter_num_of_bwd_citations = parameter_num_of_bwd_citations;
	}

	public int getParameter_science_linkage() {
		return parameter_science_linkage;
	}

	public void setParameter_science_linkage(int parameter_science_linkage) {
		this.parameter_science_linkage = parameter_science_linkage;
	}

	public int getParameter_originality_USPC() {
		return parameter_originality_USPC;
	}

	public void setParameter_originality_USPC(int parameter_originality_USPC) {
		this.parameter_originality_USPC = parameter_originality_USPC;
	}

	public int getParameter_generality_USPC() {
		return parameter_generality_USPC;
	}

	public void setParameter_generality_USPC(int parameter_generality_USPC) {
		this.parameter_generality_USPC = parameter_generality_USPC;
	}

	public int getParameter_extensive_generality() {
		return parameter_extensive_generality;
	}

	public void setParameter_extensive_generality(
			int parameter_extensive_generality) {
		this.parameter_extensive_generality = parameter_extensive_generality;
	}

	public int getParameter_num_of_assignee_transfer() {
		return parameter_num_of_assignee_transfer;
	}

	public void setParameter_num_of_assignee_transfer(
			int parameter_num_of_assignee_transfer) {
		this.parameter_num_of_assignee_transfer = parameter_num_of_assignee_transfer;
	}

	public int getParameter_num_of_patent_group() {
		return parameter_num_of_patent_group;
	}

	public void setParameter_num_of_patent_group(
			int parameter_num_of_patent_group) {
		this.parameter_num_of_patent_group = parameter_num_of_patent_group;
	}

	public long getParameter_approval_time() {
		return parameter_approval_time;
	}

	public void setParameter_approval_time(long parameter_approval_time) {
		this.parameter_approval_time = parameter_approval_time;
	}

	public int getParameter_num_of_assignee() {
		return parameter_num_of_assignee;
	}

	public void setParameter_num_of_assignee(int parameter_num_of_assignee) {
		this.parameter_num_of_assignee = parameter_num_of_assignee;
	}

	public int getParameter_num_of_citing_USpatent() {
		return parameter_num_of_citing_USpatent;
	}

	public void setParameter_num_of_citing_USpatent(
			int parameter_num_of_citing_USpatent) {
		this.parameter_num_of_citing_USpatent = parameter_num_of_citing_USpatent;
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
		HashMap<String,String> m = (HashMap<String, String>) p.getInfo();
		HashMap<String,String> s = (HashMap<String, String>) p.getParams();
		
		Iterator<String> iterator = s.keySet().iterator();  
		   
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   String value = s.get(key).toString();  
		   
		   System.out.println(key + " " + value);  
		}  
		
	}
}
