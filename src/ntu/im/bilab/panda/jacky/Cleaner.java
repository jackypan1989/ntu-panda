package ntu.im.bilab.panda.jacky;

import java.sql.*;

import ntu.im.bilab.panda.core.Config;

// Author : jackypan1989@gmail
public class Cleaner {
	public static void main(String[] args) {
		String url = Config.DATABASE_URL;
		String user = Config.DATABASE_USER;
		String pwd = Config.DATABASE_PASSWORD;
		DB db = new DB(url, user, pwd);

		String sql = "SELECT Patent_id FROM patents_index LIMIT 0 , 10000";
		ResultSet result = db.query(sql);
		String[] patent_id = new String[10000];

		int i = 0;
		try {
			while (result.next()) {
				patent_id[i] = result.getString("Patent_id");
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int j = 0; j < patent_id.length; j++) {
			String new_patent_id = patent_id[j].replaceAll(",", "");
			sql = "UPDATE patents_index SET Patent_id = '" + new_patent_id
					+ "' WHERE Patent_id = '" + patent_id[j] + "'";
			db.query(sql);
		}
	}
}
