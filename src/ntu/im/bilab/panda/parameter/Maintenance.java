package ntu.im.bilab.panda.parameter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

//TODO 取得backward citation, patent issued year的功能是否應該做在Profile或其他地方？
//getLengthOfDescription, getCHLByMeanUsingCurDate, getCHLByMedianUsingCurDate

/**
 * 
 * @author Stalin
 *
 */
public class Maintenance {

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	public Maintenance() {
		ConnectDB();
	}
	
	//TODO 資料庫連接是否應該統一集中

	private void ConnectDB() {
		//用小蛇的db -> 比較清楚
		String url = "jdbc:mysql://140.112.107.122/mypaper";
		String user = "root";
		String password = "1234";
		//TODO 
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void CloseDB() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void releaseQueryResource() {
		if (resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (statement != null) {
			try {
				statement.close();
				statement = null;
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * Retrieve word length of description in the specific USPC patent
	 * @param patentId - USPC ID of a patent
	 * @return word length of a patent
	 */
	public int getLengthOfDescription(String patentId) {
		String description = "";
		int lengthOfDescription = 0;

		for (int year = 1976; year <= 2009; year++) {
			// get patent description by traversing all table
			String tableName = "content_" + Integer.toString(year);
			String sql = "SELECT `Description`  FROM `" + tableName	+ "` WHERE `Patent_id` = '" + patentId + "'";
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet.next() == true) {
					// successful query, retrieve the description and end query
					// process
					description = resultSet.getString("Description");
					break;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		lengthOfDescription = description.split(" ").length;

		// release resource
		releaseQueryResource();

		return lengthOfDescription;
	}

	/**
	 * retrieve issued year of the focal patent
	 * @param patentId - focal patent id
	 * @return issued year of the given patent
	 */
	private int getPatentIssuedYear(String patentId) {
		int issuedYear = 0;
		for (int year = 1976; year <= 2009; year++) {
			// traversing all table
			String tableName = "content_" + Integer.toString(year);
			String sql = "SELECT `Issued_Year`  FROM `" + tableName
					+ "` WHERE `Patent_id` = '" + patentId + "'";
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				if (resultSet.next() == true) {
					// successful query, retrieve the description and end query
					// process
					issuedYear = resultSet.getInt("Issued_Year");
					break;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		// release
		releaseQueryResource();
		return issuedYear;
	}

	//TODO this could be move to other part of the code
	/**
	 * retrieve all patents (paired with its issued year) cited by the focal patent
	 * @param patentId - focal patent id
	 * @return backward-cited patents, paired with its issued year
	 */
	public HashMap<String, Integer> getBackwardCitationsWithYear(String patentId) {
		// get the issued year of the given patent
		HashMap<String, Integer> backwardCitationsWithYear = new HashMap<String, Integer>();
		ArrayList<String> backwardCitations = new ArrayList<String>();
		int patentIssuedYear;

		patentIssuedYear = getPatentIssuedYear(patentId);

		// patent issued year got, retrieve all its backward citations
		try {
			String bcSql = "SELECT `Patent_id`  FROM `patent-referencedby_"	+ patentIssuedYear + "` WHERE `Referenced_By` = '" + patentId + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(bcSql);
			while (resultSet.next()) {
				backwardCitations.add(resultSet.getString("Patent_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		releaseQueryResource();
		System.out.println(backwardCitations.toString());

		// get the age of all backward citations and assign them to hashmap
		for (String bcPatentId : backwardCitations) {
			for (int year = 1976; year <= patentIssuedYear; year++) {
				// traversing all table
				String tableName = "content_" + Integer.toString(year);
				String sql = "SELECT `Issued_Year`  FROM `" + tableName	+ "` WHERE `Patent_id` = '" + bcPatentId + "'";
				try {
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);
					if (resultSet.next() == true) {
						// successful query, retrieve the description and end
						// query process
						int bcPatentYear = resultSet.getInt("Issued_Year");
						backwardCitationsWithYear.put(bcPatentId, bcPatentYear);
						// TODO question: some cited patent are before 1976, not in database
						//      in thesis, there is a separated database store these info
						//      but only contains data within 1992-1995 (approx.)
						break;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		releaseQueryResource();

		return backwardCitationsWithYear;
	}

	/**
	 * NOTE: NOT RECOMMENED since this is not the method used by Ching-Tun's thesis. <br />
	 * Get the cited-half-life (by mean) of the given patent, which use issued year of the focal patent as basis.
	 * @param patentId - focal patent id
	 * @return cited-half-life (by mean), based on issued year of the focal patent
	 */
	public double getCHLByMean(String patentId) {
		HashMap<String, Integer> backwardCitationsWithYear = getBackwardCitationsWithYear(patentId);
		int patentIssuedYear = getPatentIssuedYear(patentId);
		// TODO maybe should use filed date to calculate
		int bcPatentAgeSum = 0;
		double meanAge = 0.0;

		for (String bcPatentId : backwardCitationsWithYear.keySet()) {
			bcPatentAgeSum += (patentIssuedYear - backwardCitationsWithYear.get(bcPatentId));
		}

		meanAge = (double) bcPatentAgeSum / (double) backwardCitationsWithYear.size();

		return meanAge;
	}

	/**
	 * NOTE: NOT RECOMMENED since this is not the method used by Ching-Tun's thesis. <br />
	 * Get the cited-half-life (by median) of the given patent, which use issued year of the focal patent as basis.
	 * @param patentId - focal patent id
	 * @return cited-half-life (by median), based on issued year of the focal patent
	 */
	public double getCHLByMedian(String patentId) {
		HashMap<String, Integer> backwardCitationsWithYear = getBackwardCitationsWithYear(patentId);
		int patentIssuedYear = getPatentIssuedYear(patentId);
		// TODO maybe should use filed date to calculate
		ArrayList<Integer> bcPatentAges = new ArrayList<Integer>(
				backwardCitationsWithYear.size());
		double medianAge = 0.0;

		for (String bcPatentId : backwardCitationsWithYear.keySet()) {
			bcPatentAges.add(patentIssuedYear - backwardCitationsWithYear.get(bcPatentId));
		}

		Collections.sort(bcPatentAges);
		System.out.println(bcPatentAges.toString());
		if (bcPatentAges.size() % 2 == 1) {
			medianAge = bcPatentAges.get((bcPatentAges.size() - 1) / 2);
		} else {
			int upper = bcPatentAges.get(bcPatentAges.size() / 2);
			int lower = bcPatentAges.get((bcPatentAges.size() / 2) - 1);
			medianAge = (double) (upper + lower) / 2.0;
		}

		return medianAge;
	}

	// Ching-Tun's thesis use "current year" as base of calculation.
	// It is because it's designed for "maintenance decision"
	// the cited-half-life is used to decide if the focal patent is too old or not
	// for example, in 2005 manager should decide whether a 1994-issued patent should be maintained
	// manager see CHL at 2005 not at 1994. That is the reason why use 2005 rather than 1994
	
	/**
	 * Get the cited-half-life (by mean) of the given patent by using current year as basis.<br />
	 * NOTE: RECOMMENDED - This approach is used in the Ching-Tun's thesis.
	 * @param patentId - focal patent id
	 * @return cited-half-life (by median), based on current year
	 */
	public double getCHLByMeanUsingCurDate(String patentId) {
		HashMap<String, Integer> backwardCitationsWithYear = getBackwardCitationsWithYear(patentId);
		int patentIssuedYear = Calendar.getInstance().get(Calendar.YEAR);
		// TODO in the thesis, use the "maintenance decision date/year" as base of cited half life
		int bcPatentAgeSum = 0;
		double meanAge = 0.0;

		for (String bcPatentId : backwardCitationsWithYear.keySet()) {
			bcPatentAgeSum += (patentIssuedYear - backwardCitationsWithYear.get(bcPatentId));
		}

		meanAge = (double) bcPatentAgeSum / (double) backwardCitationsWithYear.size();

		return meanAge;
	}

	/**
	 * Get the cited-half-life (by median) of the given patent by using current year as basis. <br />
	 * NOTE: RECOMMENDED - This approach is used in the Ching-Tun's thesis.
	 * @param patentId - focal patent id
	 * @return cited-half-life (by median)
	 */
	public double getCHLByMedianUsingCurDate(String patentId) {
		HashMap<String, Integer> backwardCitationsWithYear = getBackwardCitationsWithYear(patentId);
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		// TODO in the thesis, use the "maintenance decision date/year" as base of cited half life
		ArrayList<Integer> bcPatentAges = new ArrayList<Integer>(
				backwardCitationsWithYear.size());
		double medianAge = 0.0;

		for (String bcPatentId : backwardCitationsWithYear.keySet()) {
			bcPatentAges.add(currentYear - backwardCitationsWithYear.get(bcPatentId));
		}

		Collections.sort(bcPatentAges);
		System.out.println(bcPatentAges.toString());
		if (bcPatentAges.size() % 2 == 1) {
			medianAge = bcPatentAges.get((bcPatentAges.size() - 1) / 2);
		} else {
			int upper = bcPatentAges.get(bcPatentAges.size() / 2);
			int lower = bcPatentAges.get((bcPatentAges.size() / 2) - 1);
			medianAge = (double) (upper + lower) / 2.0;
		}

		return medianAge;
	}
}

