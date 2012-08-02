package ntu.im.bilab.panda.jacky;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import ntu.im.bilab.panda.core.Config;

public class PatentUpdater {
	public DB db;

	public PatentUpdater() {
		String url = Config.DATABASE_URL;
		String user = Config.DATABASE_USER;
		String pwd = Config.DATABASE_PASSWORD;
		db = new DB(url, user, pwd);
	}

	public void updateParams(String table_name, int thread_id) {
		int update_count = 0;
		try {
			String sql = "SELECT * FROM " + table_name;
			ResultSet result = db.query(sql);
			while (result.next()) {
				if (result.getString("DB_Status").equals("A-1"))
					continue;
				String patent_id = result.getString("Patent_id");
				Patent patent = new Patent(patent_id);

				//Map<String, String> info = patent.getInfo();
				Map<String, String> params = patent.getParams();

				Iterator<String> iterator = params.keySet().iterator();

				result.updateString("DB_Status", "A-1");
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = params.get(key).toString();
					if (key.equals("originality_USPC")
							|| key.equals("generality_USPC")
							|| key.equals("extensive_generality")) {
						result.updateFloat(key, Float.parseFloat(value));
					}
					result.updateInt(key, Integer.parseInt(value));
				}

				result.updateRow();
				update_count++;
				System.out.println("thread_id : " + thread_id);
				System.out.println("this thread progress : " + update_count);
				System.out.println("patent " + patent_id
						+ " update successed!\n");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("complete " + i +" / 136006\n");

	}

	public void updateTrainingData() {
		ArrayList<String> patent_list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(
					"unworthy_patents.txt"));

			String line = null;
			while ((line = bufferedReader.readLine()) != null)
				patent_list.add(line.trim());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Close the BufferedReader
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(patent_list);

		for (String patent_id : patent_list) {
			Patent patent = new Patent(patent_id);
			System.out.println(patent_id);

			String sql = "INSERT INTO  `patent_value`.`value_negative` "
					+ "(`Patent_id` ,`DB_Status` ,`Patent_year` ,`inventors` ,`foreign_inventors` ,"
					+ "`diversity_USPC` ,`foreign_classes` ,`family_size` ,`major_market` ,`num_of_claims` ,"
					+ "`num_of_indep_claims` ,`num_of_dep_claims` ,`patented_bwd_citations` ,	`num_of_bwd_citations` ,"
					+ "`science_linkage` ,`originality_USPC` ,`generality_USPC` ,	`extensive_generality` ,"
					+ "`num_of_assignee_transfer` ,`num_of_patent_group` ,`foreign_priority_Apps` ,"
					+ "`years_receive_first_citations` ,`approval_time` ,	`num_of_assignee` ,	`num_of_citing_USpatent`)"
					+ " VALUES ('"
					+ patent.getId()
					+ "',  '',  '"
					+ patent.getYear()
					+ "',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0'"
					+ ",  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0',  '0')";
			db.update(sql);

			System.out.println(patent_id + " has been inserted");
		}
	}

	public void close(){
		db.close();
	}
	
	public static void main(String[] args) {
		PatentUpdater dbu = new PatentUpdater();
		dbu.updateParams("value_positive", 1);
		// dbu.updateTrainingData();
		dbu.close();
	}
}
