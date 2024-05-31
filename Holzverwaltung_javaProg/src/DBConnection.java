import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.parser.JSONParser;

public class DBConnection {
	private String jdbcUrl = "jdbc:postgresql://localhost:5432/holzverwaltung";
    private String username = "silva";
    private String password = "silva12345";
    private Connection con = null;
    
	public DBConnection() {
		try {
			con = DriverManager.getConnection(jdbcUrl,username,password);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public Connection getConnection() {
		return con;
	}
}
