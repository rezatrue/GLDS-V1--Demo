package db;
/*
 * Sqlite DB for keeping data in local machine
 * http://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import model.Info;

public class LocalDBHandler {
	
	private final String DB_NAME = "glds.db";
	private String TABLE_NAME = "people"; // project base name
	private Connection conn = null;

	public LocalDBHandler() {
	}
		
	public void connect() {
	        try {
	            String url = "jdbc:sqlite:sqlite/db/" + DB_NAME;
	            conn = DriverManager.getConnection(url);
	            
	            System.out.println("Connection to SQLite has been established.");
	            
	        } catch (SQLException e) {
	            System.out.println("0"+e.getMessage());
	        }
	    }
	
	public void closeConnection() {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("10"+ex.getMessage());
            }
	}
	
	private boolean dropTable() {
		boolean status = true;
		// Drop table if previous information exist
		String sqlTDrop = "drop table IF EXISTS " + TABLE_NAME;
        
        if(conn==null)
        	connect();
        
        Statement stmt = null;
        
        try {
			if(conn!=null) {
				stmt = conn.createStatement();
			    stmt.execute(sqlTDrop);
			}
		} catch (SQLException e) {
			status = false;
			System.out.println("1"+e.getMessage());
		}finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println("2"+e.getMessage());
			}
		}
        System.out.println(" drop : " + status );
        return status;
	}
	
	public boolean createNewTable() {
		// Drop table if previous information exist
		if(!dropTable())
			return false;
        // SQL statement for creating a new table
        String sqlTCreate = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " ("
                + "	Link text PRIMARY KEY,"
                + "	First_Name text,"
                + "	Last_Name text,"
                + "	Firm text,"
                + "	Position text,"
                + "	Office text,"
                + "	Year_of_Joining text,"
                + "	Name_of_last_degree text,"
                + "	Name_of_school text,"
                + "	Year_they_earned_it text"
                + ");";
        
        if(conn==null)
        	connect();
        
        Statement stmt = null;
        
        try {
			if(conn!=null) {
				stmt = conn.createStatement();
			    stmt.execute(sqlTCreate);
			}
		} catch (SQLException e) {
			System.out.println("1"+e.getMessage());
		}finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println("2"+e.getMessage());
			}
		}
        
        return true;
    }
	
	public int countRecords() {
		int count = 0;
		String sql = "SELECT count(*) FROM " + TABLE_NAME;
		if(conn == null)
        	connect();
		Statement stmt = null;
		ResultSet rset = null;
		if(conn != null) {
			try {
				stmt = conn.createStatement();
				rset = stmt.executeQuery(sql);
				rset.next();
				count = rset.getInt(1);
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}finally {
				try {
					stmt.close();
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("total : " + count);
		return count;
	}
	
	public LinkedList<Info> selectAll(){
		LinkedList<Info> list  = new LinkedList<>();
		
        String sql = "SELECT * FROM " + TABLE_NAME;
        if(conn == null)
        	connect();
        
        Statement stmt = null;
        ResultSet rs = null;
        if(conn!=null) {
        	
        	try {
				stmt  = conn.createStatement();
				rs    = stmt.executeQuery(sql);
				// loop through the result set
				while (rs.next()) {
					Info info = new Info(
				                       rs.getString("First_Name"),
				                       rs.getString("Last_Name"), 
				                       rs.getString("Firm"),
				                       rs.getString("Position"), 
				                       rs.getString("Office"),
				                       rs.getString("Year_of_Joining"), 
				                       rs.getString("Name_of_last_degree"),
				                       rs.getString("Name_of_school"),
				                       rs.getString("Year_they_earned_it"),
				                       rs.getString("Link")
							);
					list.add(info);
					
				}
			} catch (SQLException e) {
				System.out.println("5"+e.getMessage());
			}finally {
				try {
					rs.close();
					stmt.close();
				} catch (SQLException e) {
					System.out.println("6"+e.getMessage());
				}
				
			}
			
        }
        return list;
	}

	
	public boolean update(Info info) {
		String sql = "UPDATE " 
				+ TABLE_NAME 
				+ " SET "
				+ "First_Name = ?, Last_Name = ?, Firm = ?, Position = ?, "
				+ "Office = ?, Year_of_Joining = ?, Name_of_last_degree = ?, "
				+ "Name_of_school = ?, Year_they_earned_it = ? "
				+ "WHERE "
				+ "Link = ?";
		PreparedStatement pstmt = null;
		if(conn == null)
        	connect();
		if(conn != null & info.getLink() != null) {
	        try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, info.getFirstName());
				pstmt.setString(2, info.getLastName());
				pstmt.setString(3, info.getFirm());
				pstmt.setString(4, info.getPosition());
				pstmt.setString(5, info.getOffice());
				pstmt.setString(6, info.getYearOfJoining());
				pstmt.setString(7, info.getNameOfLastDegree());
				pstmt.setString(8, info.getNameOfSchool());
				pstmt.setString(9, info.getYearTheyEarnedIt());
				pstmt.setString(10, info.getLink());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("3"+e.getMessage());
				return false;
			}finally {
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println("4"+e.getMessage());
				}
			}
        }
        return true;
	}
	
	
	public boolean insert(Info info) {
		String sql = "INSERT INTO " 
					+ TABLE_NAME 
					+ " (Link, First_Name, Last_Name, Firm, Position, Office,"
					+ " Year_of_Joining, Name_of_last_degree, Name_of_school, Year_they_earned_it) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement pstmt = null;
        if(conn == null)
        	connect();
        if(conn != null) {
	        try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, info.getLink());
				pstmt.setString(2, info.getFirstName());
				pstmt.setString(3, info.getLastName());
				pstmt.setString(4, info.getFirm());
				pstmt.setString(5, info.getPosition());
				pstmt.setString(6, info.getOffice());
				pstmt.setString(7, info.getYearOfJoining());
				pstmt.setString(8, info.getNameOfLastDegree());
				pstmt.setString(9, info.getNameOfSchool());
				pstmt.setString(10, info.getYearTheyEarnedIt());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("3"+e.getMessage());
				return false;
			}finally {
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println("4"+e.getMessage());
				}
			}
        }
        return true;
	}
	
}
