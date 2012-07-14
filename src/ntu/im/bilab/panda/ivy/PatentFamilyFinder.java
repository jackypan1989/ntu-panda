package ntu.im.bilab.panda.ivy;

import java.io.IOException;

//import Jsoup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PatentFamilyFinder {
	// special variable for EPO patent office retrieval
	private static String ec = "";

	public PatentFamilyFinder(String patent_id){
		getEC(patent_id);
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
		PatentFamilyFinder patent_family = new PatentFamilyFinder("5110638");
		int patent_volume=patent_family.getPatentFamilyVolume("5110638");
		System.out.println("patent_volume: "+patent_volume);
	}
}
