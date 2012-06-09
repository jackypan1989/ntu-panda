package ntu.im.bilab.panda.jacky;

import java.sql.ResultSet;

public class Patent{
	// data from db mypaper
	private ResultSet new_data;
	private String id;
	private String year;
    private String date;
    private String inventors;
    private String assignees;
    private String abstracts;
    private String claims;
    private String descriptions;
    private String summary;
    private String title;
    
	// data from db patent_value
	private ResultSet old_data;
	
	// parameter
	private int parameter_foreign_inventors;
	private int parameter_foreign_classes;
	private int parameter_patent_family_size;
	private int parameter_patented_backward_citations;
	private int parameter_major_market;
	private int parameter_foreign_priority_apps;
	private int parameter_years_to_receive_the_first_citation;
	
	public Patent(String patent_id) {
		// TODO Auto-generated constructor stub
		id = patent_id;
		DataBaseFetcher dbf = new DataBaseFetcher();
		dbf.getPatentData(this,patent_id);
		dbf.Close();
		
	}
    
	public String toString(){
		String attributes = "";
		attributes = attributes + "foreign_inventors : " + parameter_foreign_inventors + "\n";
		attributes = attributes + "foreign_classes : " + parameter_foreign_classes + "\n";
		attributes = attributes + "patent_family_size : " + parameter_patent_family_size + "\n";
		attributes = attributes + "patented_backward_citations : " + parameter_patented_backward_citations + "\n";
		attributes = attributes + "major_market : " + parameter_major_market + "\n";
		attributes = attributes + "foreign_priority_apps : " + parameter_foreign_priority_apps + "\n";
		attributes = attributes + "years_to_receive_the_first_citation : " + parameter_years_to_receive_the_first_citation + "\n";
		
		return attributes;
	}
	
	public void update(){
		
	}
	
	public ResultSet getNewData() {
		return new_data;
	}

	public void setNewData(ResultSet new_data) {
		this.new_data = new_data;
	}

	public ResultSet getOldData() {
		return old_data;
	}

	public void setOldData(ResultSet old_data) {
		this.old_data = old_data;
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

	public int getParameterForeignClasses() {
		return parameter_foreign_classes;
	}

	public void setParameterForeignClasses(int parameter_foreign_classes) {
		this.parameter_foreign_classes = parameter_foreign_classes;
	}

	public int getParameterForeignInventors() {
		return parameter_foreign_inventors;
	}

	public void setParameterForeignInventors(int parameter_foreign_inventors) {
		this.parameter_foreign_inventors = parameter_foreign_inventors;
	}

	public int getParameterPatentFamilySize() {
		return parameter_patent_family_size;
	}

	public void setParameterPatentFamilySize(int parameter_patent_family_size) {
		this.parameter_patent_family_size = parameter_patent_family_size;
	}

	public int getParameterPatentedBackwardCitations() {
		return parameter_patented_backward_citations;
	}

	public void setParameterPatentedBackwardCitations(
			int parameter_patented_backward_citations) {
		this.parameter_patented_backward_citations = parameter_patented_backward_citations;
	}

	public int getParameterMajorMarket() {
		return parameter_major_market;
	}

	public void setParameterMajorMarket(int parameter_major_market) {
		this.parameter_major_market = parameter_major_market;
	}

	public int getParameterForeignPriorityApps() {
		return parameter_foreign_priority_apps;
	}

	public void setParameterForeignPriorityApps(int parameter_foreign_priority_apps) {
		this.parameter_foreign_priority_apps = parameter_foreign_priority_apps;
	}

	public int getParameterYearsToReceiveTheFirstCitation() {
		return parameter_years_to_receive_the_first_citation;
	}

	public void setParameterYearsToReceiveTheFirstCitation(
			int parameter_years_to_receive_the_first_citation) {
		this.parameter_years_to_receive_the_first_citation = parameter_years_to_receive_the_first_citation;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getInventors() {
		return inventors;
	}

	public void setInventors(String inventors) {
		this.inventors = inventors;
	}

	public String getAssignees() {
		return assignees;
	}

	public void setAssignees(String assignees) {
		this.assignees = assignees;
	}

	public String getClaims() {
		return claims;
	}

	public void setClaims(String claims) {
		this.claims = claims;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	
}
