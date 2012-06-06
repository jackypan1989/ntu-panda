package ntu.im.bilab.panda.jacky;

import java.sql.ResultSet;

public class Patent{
	// data from db mypaper
	private ResultSet new_data;
	private String id;
	private String year;

	// data from db patent_value
	private ResultSet old_data;

	
	// parameter
	private int parameter_foreign_inventors;
	private int parameter_foreign_classes;
	private int parameter_patent_family_size;
	private int parameter_patented_backward_citations;
	private int parameter_major_market;
	
	public Patent(String patent_id) {
		// TODO Auto-generated constructor stub
		id = patent_id;
		DataBaseFetcher dbf = new DataBaseFetcher();
		dbf.getPatentData(this,patent_id);
		
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

	
}
