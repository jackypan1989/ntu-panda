package ntu.im.bilab.panda.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ntu.im.bilab.panda.core.Config;

public class JdbcMysql {
  private Connection con = null; //Database objects
  //連接object
  private Statement stat = null;
  //執行,傳入之sql為完整字串
  private ResultSet rs = null;
  //結果集
  private PreparedStatement pst = null;
  //執行,傳入之sql為預儲之字申,需要傳入變數之位置
  //先利用?來做標示
 
    
  private String selectSQL;
  private Entry PatentRecord;
  public JdbcMysql()
  {
    try {
      Class.forName(Config.DRIVER);
      //註冊driver
      con = DriverManager.getConnection(
      Config.DATABASE_URL, Config.DATABASE_USER ,  Config.DATABASE_PASSWORD);
     
    }
    catch(ClassNotFoundException e)
    {
      System.out.println("DriverClassNotFound :"+e.toString());
    }//有可能會產生sqlexception
    catch(SQLException x) {
      System.out.println("Exception :"+x.toString());
    }
   
  }
  
  public Entry GetPatent()
  {
	  return PatentRecord;
  }
  public String GetTable(String pid)
	{
		int year;
		String table_name = "X";
		for(year = 1976 ; year <= 2009 ; year++)
		{
			try
			{
				selectSQL = "select Patent_id, Claims from uspto_"+ year + " where Patent_id =" + "'" + pid + "'" ;
				stat = con.createStatement();
			    rs = stat.executeQuery(selectSQL);			    
			    if(rs.absolute(1)== true){
			    	table_name = "uspto_" + year;	
					return table_name; 
			    }
			    			    
			}
			catch(SQLException e){System.out.println(e.toString());}		
		}
		return table_name;
		
	}
  //查詢資料
  //可以看看回傳結果集及取得資料方式
  public void DeleteNo(String TableName,String Pid)
  {
	  String DeleteSQL = "delete from "+TableName+" where patent_id='"+Pid+"'";
	  int status = 0;
	  try {
		  stat = con.createStatement();  
		status = stat.executeUpdate(DeleteSQL);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(DeleteSQL+"\t"+status);
  }
  public void SelectTable(String tname)
  {
    try
    {
      stat = con.createStatement();
      rs = stat.executeQuery(selectSQL);
      
           
      if(rs.absolute(1))
      {
    	//System.out.println("Get the table");
    	  //System.out.println("selectSQL="+selectSQL);
    	 
    	PatentRecord = new Entry(rs.getString("Patent_id"),rs.getString("Claims"));
    	
      }
      else{
    	  
    	  System.out.println("No Table");
      }
    }
    catch(SQLException e)
    {
      System.out.println("DropDB Exception :" + e.toString());
    }
    finally
    {
      Close();
    }
  }
  //完整使用完資料庫後,記得要關閉所有Object
  //否則在等待Timeout時,可能會有Connection poor的狀況
  public void Close()
  {
    try
    {
      if(rs!=null)
      {
        rs.close();
        rs = null;
      }
      if(stat!=null)
      {
        stat.close();
        stat = null;
      }
      if(pst!=null)
      {
        pst.close();
        pst = null;
      }
    }
    catch(SQLException e)
    {
      System.out.println("Close Exception :" + e.toString());
    }
  }
 

  public static void main(String[] args)
  {
    //測看看是否正常
    JdbcMysql test = new JdbcMysql();
    //test.SelectTable();
 
  }
}