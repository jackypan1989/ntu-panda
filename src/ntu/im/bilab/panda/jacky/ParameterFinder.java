package ntu.im.bilab.panda.jacky;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* 
 * this class is used for finding the parameters for "value" system
 * Author : r00 jackypan1989@gmail.com 
 */

public class ParameterFinder {
	// special variable for EPO patent office retrieval
	private static String ec = "";

	public ParameterFinder(String patent_id){
		getEC(patent_id);
	}
	
	/*
	 * Variable Number : 1 
	 * Method : return the amount of inventors 
	 * Author : r00 jackypan1989@gmail.com 
	 * Last Edit Date : 20120413
	 */
	public int getInventors(String data) {

		// clarify the data
		// some data loses the inventors field, some data has no assignee
		if (!data.contains("Inventors")) {
			return 0;
		} else if (!data.contains("Assignee")) {
			data = data.substring(data.indexOf("Inventors") + 10,
					data.indexOf("Appl"));
		} else {
			data = data.substring(data.indexOf("Inventors") + 10,
					data.indexOf("Assignee"));
		}

		// split into inventors array
		String[] inventors = data.split(";");

		// get the amount of inventors
		int total_inventors = inventors.length - 1;
		// System.out.println("inventors : "+total_inventors);
		return total_inventors;
	}

	/*
	 * Variable Number : 2 
	 * Method : return the amount of foreign inventors by
	 *          U.S. state code 
	 * Author : r00 jackypan1989@gmail.com 
	 * Last Edit Date : 20120413
	 */
	public int getForeignInventors(String data) {

		// clarify the data
		// some data loses the inventors field, some data has no assignee
		try {
			if (!data.contains("Inventors")) {
				return 0;
			} else if (!data.contains("Assignee")) {
				data = data.substring(data.indexOf("Inventors") + 10,
						data.indexOf("Appl"));
			} else {
				data = data.substring(data.indexOf("Inventors") + 10,
						data.indexOf("Assignee"));
			}
		} catch (StringIndexOutOfBoundsException e) {
			return 0;
		}

		// split into inventors array
		String[] inventors = data.split(";");
		int total_inventors = inventors.length - 1;
		int local_inventors = 0;
		int foreign_inventors = 0;

		// uspto patent state code in the U.S.
		String[] us_states = { "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "CZ",
				"DC", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS",
				"KY", "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT",
				"NE", "NC", "ND", "NH", "NJ", "NM", "NY", "NV", "OH", "OK",
				"OR", "PA", "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VA",
				"VI", "VT", "WA", "WI", "WY", "NB" };

		// find all local inventors
		for (int i = 0; i < inventors.length; i++) {
			if (inventors[i].contains(")")) {
				// fetch the code , ex: (Taipei, TW) => TW
				String s = inventors[i].substring(
						inventors[i].indexOf(")") - 2,
						inventors[i].indexOf(")"));
				for (String state : us_states) {
					// check whether in the U.S.
					if (state.equals(s)) {
						local_inventors++;
						break;
					}
				}
			}
		}

		// get the amount of foreign inventors
		foreign_inventors = total_inventors - local_inventors;
		// System.out.println("foreign_inventors : "+foreign_inventors);
		return foreign_inventors;
	}

	/*
	 * Variable Number : 6 
	 * Method : return the amount of foreign cited patent by
	 *          uspto foreign document field(?) 
	 * Author : r00 jackypan1989@gmail.com
	 * Pan Last Edit Date : 20120425 
	 */
	public int getForeignClasses(String data) {

		int foreign_classes = 0;

		// clarify data, some with other references
		if (data.contains("Foreign Patent Documents")) {
			if (data.contains("Other References")) {
				data = data.substring(
						data.indexOf("Foreign Patent Documents") + 25,
						data.indexOf("Other References"));
			} else {
				data = data.substring(
						data.indexOf("Foreign Patent Documents") + 25,
						data.indexOf("Primary Examiner"));
			}

			// calculate if there is foreign patent
			String[] foreign_patents = data.split(",");
			foreign_classes = foreign_patents.length - 1;

		}
		// System.out.println("foreign_classes : "+foreign_classes);
		return foreign_classes;
	}

	/*
	 * Variable Number : 7 
	 * Method : return the size of patent family Volume from
	 *          EPO (nations) 
	 * Author : Ivy Hoi 
	 * Last Edit Date : 20120624 
	 */
	public int getPatentFamilyVolume(String patent_id) {
		int patent_family_volume = 0;

		// fetch from EPO
		try {
			if (ec.equals(""))
				return -1;
			Document doc = Jsoup
					.connect(
							"http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="
									+ patent_id + ec).get();
			// find the amount of patent family size
			Elements elements = doc.getElementsByClass("publicationInfoColumn");
			String data = elements.text();

			if (data.contains("Publication info: ")) {
				String publication_info = data
						.replace("Publication info: ", "");
				if (publication_info.contains(" ")) {
					String[] publications_array = publication_info.split(" ");
					String market_array[] = new String[publications_array.length];
					String market = "";
					for (int i = 0; i < publications_array.length; i = i + 2) {
						market = publications_array[i].substring(0, 2);
						boolean is_market_exist = false;
						if (i == 0) {
							market_array[i] = market;
							patent_family_volume++;
						} else {
							for (int j = 0; j < patent_family_volume; j++) {
								if (market.equals(market_array[j])) {
									is_market_exist = true;
									break;
								}
							}
						}
						if (!is_market_exist && i != 0) {
							market_array[patent_family_volume] = market;
							patent_family_volume++;
						}
					}
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
			// e.printStackTrace();
		} catch (NullPointerException e) {
			return -1;
			// e.printStackTrace();
		}
		return patent_family_volume;
	}

	/*
	 * Variable Number : 8 
	 * Method : return the size of patent family from EPO
	 * Author : r00 jackypan1989@gmail.com
	 * Last Edit Date : 20120430
	 */
	public int getPatentFamilySize(String patent_id) {
		int patent_family_size = 0;

		// fetch from EPO
		try {
			if (ec.equals(""))
				return -1;
			Document doc = Jsoup
					.connect(
							"http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="
									+ patent_id + ec).get();
			// find the amount of patent family size
			Element data = doc.getElementsByClass("epoBarItem").first()
					.getElementsByTag("strong").first();
			patent_family_size = Integer.parseInt(data.text());
			// System.out.println("patent family size : "+patent_family_size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
			// e.printStackTrace();
		} catch (NullPointerException e) {
			return -1;
			// e.printStackTrace();
		}

		return patent_family_size;
	}

	/*
	 * Variable Number : 9 
	 * Method : return of the parameter of major
	 * market(US,EU,JP) 
	 * Author : r00 jackypan1989@gmail.com 
	 * Last Edit Date : 20120430
	 */
	public int getMajorMarket(String patent_id) {
		int major_market = 1;
		// fetch from EPO
		try {
			if (ec.equals(""))
				return -1;
			Document doc = Jsoup
					.connect(
							"http://worldwide.espacenet.com/publicationDetails/inpadocPatentFamily?CC=US&FT=D&NR="
									+ patent_id + ec).get();
			// find the amount of patent family size
			Elements elements = doc.getElementsByClass("publicationInfoColumn");
			String data = elements.text();
			if (data.contains("EP"))
				major_market++;
			if (data.contains("JP"))
				major_market++;

			// major_market = data.text();
			// System.out.println("major_market : "+major_market);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
			// e.printStackTrace();
		}

		return major_market;
	}

	/*
	 * Variable Number : 15 
	 * Method : return the amount of backward citations
	 *          (foreign & US) from EPO  
	 * Author : Guan-Yu Pan 
	 * Last Edit Date : 20120511 
	 */
	public int getPatentedBackwardCitations(String patent_id) {
		int patented_backward_citations = 0;

		if (ec.equals(""))
			return -1;
		// fetch from EPO
		try {
			Document doc = Jsoup
					.connect(
							"http://worldwide.espacenet.com/publicationDetails/citedDocuments?CC=US&FT=D&NR="
									+ patent_id + ec).timeout(30000).get();
			// find the amount of backward citations
			Element element = doc.getElementsByClass("epoBarItem").first()
					.getElementsByTag("strong").first();
			patented_backward_citations = Integer.parseInt(element.text());
			// System.out.println("patented backward citations : "+patented_backward_citations);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return -1;
		}

		return patented_backward_citations;
	}
	
	/*
	 * Variable Number : 51
	 * Method : return the year of the target patent get the first citation
	 * Author : r00 jackypan1989@gmail.com
	 * Last Edit Date : 20120626
	 */
	public int getYearsToReceiveTheFirstCitation(Patent patent) {
		int years_to_receive_the_first_citation = 0;

		PatentFetcher dbf = new PatentFetcher();
		int year = dbf.getYear(patent, "years_to_receive_the_first_citation");

		// not yet receive the first citation
		if (year == -1)
			return -1;
		years_to_receive_the_first_citation = year
				- Integer.parseInt(patent.getYear());

		return years_to_receive_the_first_citation;
	}
	
	/*
	 * Variable Number : 52 
	 * Method : return the number of the foreign priority
	 *          applications from USPTO 
	 * Author : r00 jackypan1989@gmail.com
	 * Last Edit Date : 20120606
	 */
	public int getForeignPriorityApps(String data) {
		int foreign_priority_apps = 0;

		// clarify data if it includes foreign application priority data
		if (data.contains("Foreign Application Priority Data")) {
			// calculate
			String[] foreign_patents = data.split("\\[");
			foreign_priority_apps = foreign_patents.length - 1;
		}

		return foreign_priority_apps;
	}

	
    
	// get special variable for EPO patent office retrieval
	public void getEC(String patent_id) {
		try {
			Document doc = Jsoup.connect(
					"http://worldwide.espacenet.com/searchResults?query=US"
							+ patent_id).get();
			Element element = doc.getElementsByClass("publicationInfoColumn")
					.first();
			String data = element.text();
			if (data != null && data.contains(patent_id)) {
				data = data.substring(data.indexOf(patent_id));
				if (data.contains("(") && data.contains(")")) {
					ec = data.substring(data.indexOf("(") + 1,
							data.indexOf(")"));
				}
			}
			// System.out.println("ec for EPO : "+ec);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			ec = "";
			System.out.println(patent_id + " cant get ec");
			// e.printStackTrace();
		} catch (NullPointerException e) {
			ec = "";
			System.out.println(patent_id + " cant get ec");
			// e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*ParameterFinder t = new ParameterFinder();

		Patent patent = new Patent("5110638");
		String patent_id = patent.getId();
		ResultSet new_data = patent.getNew_data();
		ResultSet old_data = patent.getOld_data();

		t.getEC(patent_id);
		try {
			patent.setParameter_foreign_inventors(t
					.getForeignInventors(old_data.getString("Inventors")));
			patent.setParameter_foreign_classes(t.getForeignClasses(old_data
					.getString("References Cited")));
			patent.setParameter_patent_family_size(t
					.getPatentFamilySize(patent_id));
			patent.setParameter_patented_backward_citations(t
					.getPatentedBackwardCitations(patent_id));
			patent.setParameter_major_market(t.getMajorMarket(patent_id));
			patent.setParameter_foreign_priority_apps(t
					.getForeignPriorityApps(old_data
							.getString("Current U.S. Class")));
			patent.setParameter_years_to_receive_the_first_citation(t
					.getYearsToReceiveTheFirstCitation(patent));
			patent.setParameter_patent_family_volume(t
					.getPatentFamilyVolume(patent_id)); // ivy
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		patent.getOldParameter();

		System.out.println(patent.toString());
        */
	}
}
