package ntu.im.bilab.panda.jacky;

import java.sql.ResultSet;
import java.sql.SQLException;


import ntu.im.bilab.panda.core.Config;

public class DataBaseFetcher {
	private DB db;

	public DataBaseFetcher() {
		String url = Config.NEW_DATABASE_URL;
		String user = Config.DATABASE_USER;
		String pwd = Config.DATABASE_PASSWORD;
		db = new DB(url, user, pwd);
	}

	public ResultSet getTuple(String patent_id) {
		int start_year = 1976;
		int end_year = 2009;
		ResultSet result = null;

		for (int i = start_year; i <= end_year; i++) {
			try {
				String sql = "select * from content_" + i
						+ " where Patent_id =" + "'" + patent_id + "'";
				result = db.query(sql);

				if (result.absolute(1)) {
					return result;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (result == null)
			System.out.println("NULL");
		return null;
	}

	public void getPatentData(Patent patent, String patent_id) {
		// create a patent entity
		try {
			// for new database
			ResultSet result = getTuple(patent_id);
			if (result == null)
				return;
			String year = result.getString("Issued_Year");
			patent.setNew_data(result);
			patent.setYear(year);

			// for old database
			getOldDataBaseContent(patent, patent_id, year);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getOldDataBaseContent(Patent patent, String patent_id,
			String year) {
		String url = Config.NEW_DATABASE_URL;
		String user = Config.DATABASE_USER;
		String pwd = Config.DATABASE_PASSWORD;
		DB db_old = new DB(url, user, pwd);
		String sql = "select * from uspto_" + year + " where Patent_id =" + "'"
				+ patent_id + "'";
		ResultSet result = db_old.query(sql);
		try {
			result.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		patent.setOld_data(result);
	}

	public int getYear(Patent patent, String parameter) {
		int start_year = 1976;
		int end_year = 2009;
		int year = -1;

		ResultSet result = null;

		if (parameter == "years_to_receive_the_first_citation") {
			year = Integer.parseInt(patent.getYear());
			String patent_id = patent.getId();

			for (int i = year; i <= end_year; i++) {
				try {
					String sql = "select * from `patent-referencedby_" + i
							+ "` where Patent_id =" + "'" + patent_id + "'";
					result = db.query(sql);
					if (result.absolute(1))
						return i;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return year;
	}

	public void close() {
		db.close();
	}
}
