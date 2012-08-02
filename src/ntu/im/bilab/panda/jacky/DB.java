package ntu.im.bilab.panda.jacky;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

public class DB {
	private String url;
	private String user;
	private String pwd;
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;

	public DB(String url, String user, String pwd) {
		this.url = url;
		this.user = url;
		this.pwd = url;
		connect();
	}

	public ResultSet query(String sql){
		try {
			stmt = conn.createStatement();
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void update(String sql){
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() {
		try {
			Class.forName(Config.DRIVER);
			conn = DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException e) {
			System.out.println("Can't find driver class");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}