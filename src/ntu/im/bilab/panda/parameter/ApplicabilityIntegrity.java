package ntu.im.bilab.panda.parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

/*
 * 包含Applicability 變數 Transfer of assignees,Apps in Major 3 Patent Office
 *     Integrity     變數Claims, independent claims, Dependent claims 
 * 
 * 
 * 
 */
public class ApplicabilityIntegrity {
	private String PNO = null;
	private int NoTransTime = 0;
	private int NoPatentOff = 0;
	private int NoClaims = 0;
	private int NoIndepClaim = 0;
	private int NoDepClaim = 0;

	public ApplicabilityIntegrity(String pid) {
		PNO = pid;
		// NoClaims();
		// NoTransAs();
		// NoPatentOffice();

	}

	public int GetTransTime() {
		return NoTransTime;

	}

	public int GetPatentOff() {
		return NoPatentOff;
	}

	public int GetClaims() {

		return NoClaims;
	}

	public int GetIndepClaim() {
		return NoIndepClaim;
	}

	public int GetNoDepClaim() {
		return NoDepClaim;

	}

	public int NoPatentOffice() {
		// System.out.println("-----Start of Major office-----");
		FetchMajorOffice FMO = new FetchMajorOffice(PNO);
		// System.out.println("-----Middle of Major office-----");
		NoPatentOff = FMO.GetNoMoffice();
		FMO = null;
		// System.out.println("-----End of Major office-----");
		return NoPatentOff;
	}

	public int NoTransAs() {
		FetchTransAss FTA = new FetchTransAss(PNO);

		NoTransTime = FTA.getNumberTransTime();
		FTA = null;
		return NoTransTime;
	}

	public int NoClaims() {
		JdbcMysql DBConn = new JdbcMysql();
		String tname = null;
		tname = DBConn.GetTable(PNO);
		DBConn.SelectTable(tname);

		Entry tEntry = new Entry();
		tEntry = DBConn.GetPatent();

		// System.out.println(tEntry);
		// new java.util.Scanner(System.in).nextLine(); //pause

		String pid = tEntry.GetPatentid();
		String pClaim = tEntry.GetClaim();

		// System.out.println(pClaim);
		// System.out.println("---------------------------------");
		String[] str;
		str = pClaim.split("[0-9]+\\.");

		Count_Dep_Claim(str); // differentiate the dependent claim（we can
								// speculate the indep. claims from dependent
								// one）
		int deduct = 0; // some of descriptions is not belongs to claim
		for (int i = 0; i < str.length; i++) {
			if (i == 0) {
				deduct++;
				continue;
			}
			if (str[i].matches("\\s*")) {
				deduct++;
			}
			// System.out.println(i+":"+str[i]);
		}

		NoClaims = str.length - deduct;
		tEntry = null; // release object
		DBConn.Close();
		DBConn = null;

		return NoClaims;
	}

	public void Count_Dep_Claim(String[] Claims) {
		int counter = 0;
		for (int i = 1; i < Claims.length; i++) { // row 0 isn't claim
													// description
			if (Claims[i].contains("claim") || Claims[i].contains("Claim")) {
				counter++;
			}
		}
		NoDepClaim = counter;
	}

	public int NoIndepClaim() {
		return NoClaims - NoDepClaim;
	}

	public int NoDepClaim() {

		return NoDepClaim;
	}

	public void PrintAll() {
		System.out.println();
		System.out.println("Number of Claims: " + NoClaims);
		System.out.println("Number of dependent Claims: " + NoDepClaim);
		System.out.println("Number of Independent Claims: " + NoIndepClaim);
		System.out.println("Trnasfer times of the assignees: " + NoTransTime);
		System.out.println("Patent in the three major office: " + NoPatentOff);

	}

	public static void main(String[] args) throws IOException {
		System.out.println("Enter a Patent ID: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		// read in patent id
		String pid = in.readLine();
		ApplicabilityIntegrity AI = new ApplicabilityIntegrity(pid);
		AI.PrintAll();

	}

	public class FetchMajorOffice {
		private int NoMoffice = 1; // already existed in USPTO

		public FetchMajorOffice(String PNO) {

			// String PatentNo = new String(PNO);
			// System.out.println("\t\tStart Fetch office website");
			// StringBuffer document= new StringBuffer();
			StringBuilder document = new StringBuilder();
			try {
				URL url = new URL(
						"http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm&r=1&f=G&l=50&s1="
								+ PNO + ".PN.&OS=PN/" + PNO + "&RS=PN/" + PNO);
				// System.out.println("\t\t\tBefore connection");
				URLConnection conn = url.openConnection();
				// System.out.println("\t\t\tAfter connection");
				conn.setConnectTimeout(30000);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null)
					document.append(line + "\n");

				reader.close();
				// conn = null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// System.out.println("\t\tend Fetch office website");
			String str;
			str = document.toString();
			// System.out.print(document.toString());

			// System.out.println("\t\tStart Fetch office Parsing");
			if (str.indexOf("Foreign Patent Documents") > 0) {
				document = document.delete(0,
						document.indexOf("Foreign Patent Documents") - 1);// get
																			// rid
																			// of
																			// unimportant
																			// part
				// System.out.println(document.toString());
				str = document.substring(document.indexOf("<TABLE "),
						document.indexOf("</TABLE>")).toString();// get entire
																	// table to
																	// count
																	// Major
																	// office

				// System.out.println(str);
				if (str.indexOf("JP") > 0) {
					NoMoffice++;
					// System.out.println("JP++");
				}

				if (str.indexOf("EP") > 0) {
					NoMoffice++;
					// System.out.println("EP++");
				}

				// System.out.println();

			}
			System.out.println("\t\tend Fetch office Parsing");
			// else{
			// NoMoffice = 1;
			// System.out.println("FAIL");
			// }
			document.setLength(0); // clean stringbuilder to avoid out of memory
									// problem

			// System.out.print(document.toString());
			// System.out.println(str);
			// System.out.print(NoTranstime);

		}

		public int GetNoMoffice() {
			return NoMoffice;
		}
	}

	public class FetchTransAss {
		private int NumberTransTime = 0;

		public FetchTransAss(String PNO) {
			String PatentNo = new String(PNO);

			// StringBuffer document= new StringBuffer();
			StringBuilder document = new StringBuilder();
			try {
				URL url = new URL(
						"http://assignments.uspto.gov/assignments/q?db=pat&qt=pat&reel=&frame=&pat="
								+ PatentNo
								+ "&pub=&asnr=&asnri=&asne=&asnei=&asns=");
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(30000);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null)
					document.append(line + "\n");

				// conn = null;
				reader.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String str;
			String[] TotalAss;
			Integer NoTranstime = new Integer(0);

			// System.out.print(document.toString());

			str = document.toString();

			if (str.indexOf("Total Assignments:") > 0) {

				// if(str.matches(".*Total Assignments:.+[0-9]+.*")){
				// if(str.matches(".+Total Assignments:.*[0-9].+")){
				// System.out.println("OK");
				str = document.substring(
						document.indexOf("Total Assignments:"),
						document.indexOf("Total Assignments:") + 32).replace(
						"\n", " ");

				TotalAss = str.split(":");
				NoTranstime = Integer.parseInt(TotalAss[1].trim());

				// }else{

				// }

			} else {
				NoTranstime = 0;
				// System.out.println("FAIL");

			}
			document.setLength(0); // clean stringbuilder to avoid out of memory
									// problem
			// System.out.print(document.toString());
			// System.out.println(str);
			// System.out.print(NoTranstime);
			NumberTransTime = NoTranstime;
		}

		public int getNumberTransTime() {
			return NumberTransTime;
		}

		public void main(String args[]) {
			// new FetchTransAss("5480270");
			// new FetchTransAss("6421895");
			// new FetchTransAss("7738977");
		}

	}

	public class JdbcMysql {
		private Connection con = null; // Database objects
		// 連接object
		private Statement stat = null;
		// 執行,傳入之sql為完整字串
		private ResultSet rs = null;
		// 結果集
		private PreparedStatement pst = null;
		// 執行,傳入之sql為預儲之字申,需要傳入變數之位置
		// 先利用?來做標示

		private String selectSQL;
		private Entry PatentRecord;

		public JdbcMysql() {
			try {
				Class.forName(Config.DRIVER);
				// 註冊driver
				con = DriverManager.getConnection(Config.DATABASE_URL,
						Config.DATABASE_USER, Config.DATABASE_PASSWORD);

			} catch (ClassNotFoundException e) {
				System.out.println("DriverClassNotFound :" + e.toString());
			}// 有可能會產生sqlexception
			catch (SQLException x) {
				System.out.println("Exception :" + x.toString());
			}

		}

		public Entry GetPatent() {
			return PatentRecord;
		}

		public String GetTable(String pid) {
			int year;
			String table_name = "X";
			for (year = 1976; year <= 2009; year++) {
				try {
					selectSQL = "select Patent_id, Claims from uspto_" + year
							+ " where Patent_id =" + "'" + pid + "'";
					stat = con.createStatement();
					rs = stat.executeQuery(selectSQL);
					if (rs.absolute(1) == true) {
						table_name = "uspto_" + year;
						return table_name;
					}

				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			}
			return table_name;

		}

		// 查詢資料
		// 可以看看回傳結果集及取得資料方式
		public void DeleteNo(String TableName, String Pid) {
			String DeleteSQL = "delete from " + TableName
					+ " where patent_id='" + Pid + "'";
			int status = 0;
			try {
				stat = con.createStatement();
				status = stat.executeUpdate(DeleteSQL);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(DeleteSQL + "\t" + status);
		}

		public void SelectTable(String tname) {
			try {
				stat = con.createStatement();
				rs = stat.executeQuery(selectSQL);

				if (rs.absolute(1)) {
					// System.out.println("Get the table");
					// System.out.println("selectSQL="+selectSQL);

					PatentRecord = new Entry(rs.getString("Patent_id"),
							rs.getString("Claims"));

				} else {

					System.out.println("No Table");
				}
			} catch (SQLException e) {
				System.out.println("DropDB Exception :" + e.toString());
			} finally {
				Close();
			}
		}

		// 完整使用完資料庫後,記得要關閉所有Object
		// 否則在等待Timeout時,可能會有Connection poor的狀況
		public void Close() {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (pst != null) {
					pst.close();
					pst = null;
				}
			} catch (SQLException e) {
				System.out.println("Close Exception :" + e.toString());
			}
		}
	}

	public class Entry {
		private String patent_id = null, pClaim = null;

		public Entry() {

		}

		public Entry(String pid, String Claim) {
			patent_id = pid;
			pClaim = Claim;

		}

		public String GetClaim() {

			return pClaim;
		}

		public String GetPatentid() {

			return patent_id;
		}

	}
}
