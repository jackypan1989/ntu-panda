package ntu.im.bilab.panda.parameter;
import java.sql.*;

public class Diversity {
	private String url;
	private Connection conn;
	private Statement stat;
	private ResultSet rs;
	
	private int techScope, generality, originality, atYear;
	private String Patent_id;
	
	public Diversity(String p_id) {
		url = null;
		conn = null;
		stat = null;
		rs = null;
		
		techScope = -1;
		generality = -1;
		originality = -1;
		atYear = -1;
		
		Patent_id = p_id;
		
		findInstance();
	}
	
	private void findInstance() {
		try {
			//�s����Ʈw
			url="jdbc:mysql://140.112.107.207/mypaper?user=root&password=123456&autoReconnect=true";//�p�D
			conn = DriverManager.getConnection(url);
			//System.out.println(Patent_id);
			String selectSQL = null;
			
			int year = 1976;
			while(atYear == -1 && year <= 2009) {
				selectSQL = "select Patent_id from content_" + year + " where Patent_id='" + Patent_id +"'";
				stat = conn.createStatement();
				rs = stat.executeQuery(selectSQL);
				if(rs.next()) {
					atYear = year;
				}
				
				else {
					year++;
				}
				
				rs.close();
				stat.close();	
			}
		}
		
		catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	public int GetTechScope () {
		try {
			if(atYear != -1) {
				techScope = 0;
				String selectSQL = "select * from `patent-class_" + atYear + "` where Patent_id='" + Patent_id +"'";
				stat = conn.createStatement();
				rs = stat.executeQuery(selectSQL);
				while(rs.next()) {
					techScope++;
				}
				rs.close();
				stat.close();
			}
			
			return techScope;
		}
		
		catch (Exception e) { 
			return techScope;
		}
	}
	
	public int GetGenerality () {
		try {
			if(atYear != -1) {
				generality = 0;
				String selectSQL = null;
				int year = atYear;
				while(year <= 2009) {
					selectSQL = "select * from `patent-referencedby_" + year + "` where Patent_id='" + Patent_id +"'";
					stat = conn.createStatement();
					rs = stat.executeQuery(selectSQL);
					while(rs.next()) {
						//System.out.println(Patent_id + "�Q" + rs.getString("Referenced_By") + "�ޥ�, �����O��: ");
						Statement _stat = null;
						ResultSet _rs = null;
						int _year = 1976, _atYear = -1;
						while(_atYear == -1 && _year <= 2009) {
							selectSQL = "select * from `patent-class_" + _year + "` where Patent_id='" + rs.getString("Referenced_By") +"'";
							_stat = conn.createStatement();
							_rs = _stat.executeQuery(selectSQL);
							while(_rs.next()) {
								//System.out.println(_rs.getString("Class"));
								_atYear = _year;
								generality++;
							}
							_rs.close();
							_stat.close();	
							_year++;
						}
					}
					rs.close();
					stat.close();
					year++;
				}
			}
			
			return generality;
		}
			
		catch (Exception e) { 
			return generality;
		}
	}
	
	public int GetOriginality () { 
		try {
			if(atYear != -1) {
				String selectSQL = "select * from `patent-referencedby_" + atYear + "` where Referenced_By='" + Patent_id +"'";
				stat = conn.createStatement();
				rs = stat.executeQuery(selectSQL);
				while(rs.next()) {
					//System.out.println(Patent_id + "�ޥ�" + rs.getString("Patent_id") + ", �����O��: ");
					Statement _stat = null;
					ResultSet _rs = null;
					int _year = 1976, _atYear = -1;
					while(_atYear == -1 && _year <= 2009) {
						selectSQL = "select * from `patent-class_" + _year + "` where Patent_id='" + rs.getString("Patent_id") +"'";
						_stat = conn.createStatement();
						_rs = _stat.executeQuery(selectSQL);
						while(_rs.next()) {
							//System.out.println(_rs.getString("Class"));
							if(originality == -1) {
								originality = 0;
							}
							_atYear = _year;
							originality++;
						}
						_rs.close();
						_stat.close();	
						_year++;
					}
				}
				
				rs.close();
				stat.close();
			}
			
			return originality;
		}
			
		catch (Exception e) { 
			return originality;
		}
	}
	
	public void PrintAll () { 
		System.out.println("TechSope: " + GetTechScope());
		System.out.println("Generality : " + GetGenerality());
		System.out.println("Originality : " + GetOriginality());
	}
	
	public static void main(String[] args) {
	}
	
	public class InsertForward {
		
		private Connection conn = null;
		private Statement stat = null;
		private ResultSet rs = null;
		private PreparedStatement pst = null;
		
		public InsertForward() { 
			
			try { 
				
				Class.forName("com.mysql.jdbc.Driver"); 
				System.out.println("Success Loading MySql Driver!");
				String url="jdbc:mysql://140.112.107.207/patent_value?user=root&password=123456";
				//String url="jdbc:mysql://daventu.no-ip.org/patent_value?user=bilab&password=bilab";
				conn = DriverManager.getConnection(url);
			    if(!conn.isClosed())
			        System.out.println("connetion is closed");
		    } 
			
			catch(ClassNotFoundException e) { 
				System.out.println("Error Loading MySql Driver:"+e.toString()); 
		    }
			
			catch(SQLException x) {
				System.out.println("SOLException :"+x.toString()); 
			} 
		}
		
		public void Select() {		
			try {
				String selectSQL = "Select Patent_id,`References Cited` from uspto_1978";
				stat = conn.createStatement();
				rs = stat.executeQuery(selectSQL);
				String p_id,cited,insert_cited,temp;
				int i,j,isdigit;
				
				while(rs.next()) {
					
					p_id = rs.getString("Patent_id");
					cited = rs.getString("References Cited");
					insert_cited = "";
					
					if(cited!=null) {//���O�s��v��reference�A�B�z
						System.out.print("start to split");
						String[] array=cited.split(" ");
				
						if(array.length>1&&array[1].equals("U.S.")) {//�u�B�z�ѦҨ��ꪺ�M�Q
							//�L�X�Ӭݬ��o
							System.out.print(p_id);
							System.out.println("->"+cited);
							
							for(i=1;!array[i].equals("Examiner:");i++) {
								
								temp="";
								isdigit = 0;//�@�}�l���]���S���Ʀr
								
								for(j=0;j<array[i].length();j++) {
							
									if(array[i].charAt(j)!=',') {
										
										if(Character.isDigit(array[i].charAt(j))) {
										isdigit++;
										}
										temp=temp+array[i].charAt(j);
									}
								}
								
								if(isdigit>4) {//���i��O�~��
									
									//System.out.println(temp);
									insertTable(p_id,temp);
									//insert_cited=insert_cited+temp+" ";
								}
							}
						}
						
						//else {//���OU.S������
							
							//System.out.println(" �DU.S.�Φ�");
							//System.out.println("->"+cited);

						//}
					}	
				}		
			}
			
			catch(SQLException e) {
				System.out.println("DropDB Exception :" + e.toString());
		    }
		    
			finally {
				Close();
		    }
		}
		
		public void insertTable(String p_id,String c_id) {
			try{
				stat =  conn.createStatement(); 
				stat.execute("INSERT INTO f_citation(Patent_id,f_id) VALUES('"+p_id+"','"+c_id+"')");
			}
			
			catch(SQLException e) { 
		      
				System.out.println("InsertDB Exception :" + e.toString()); 
		    } 
		}
		
		private void Close() { 
			try {   
				if(rs!=null) { 
					rs.close(); 
					rs = null; 
				} 
		      
				if(stat!=null) { 
					stat.close(); 
					stat = null; 
				} 
				
				if(conn!=null) {	
					conn.close();
					conn = null;
				}
				
				if(pst!=null) {
					pst.close();
					pst = null;
				}
			}
			
		    catch(SQLException e) { 
		    	System.out.println("Close Exception :" + e.toString()); 
		    } 
		} 

		public void main(String[] args) { 
			//��ݬݬO�_���` 
			InsertForward test = new InsertForward();
			//test.insertTable("RE29,094","2509545");
			test.Select();
	    }
	}
	
}


