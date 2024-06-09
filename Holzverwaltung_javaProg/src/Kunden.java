import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Kunden {
	private Connection con = null;
	private String path = "data.json";
	
	public Kunden(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM kunden LIMIT 1;");
			rs.next();
			if(rs.getInt(1)<1) {
				getDataJSON();
			}
			stmt.close();
			rs.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String name) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO kunden (name) VALUES(?)");
			prep.setString(1, name);
			prep.executeUpdate();
			prep.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void getDataJSON() {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new BufferedReader(new FileReader(path)));
			JSONObject jsonObject = (JSONObject)obj;
			JSONArray kunden = (JSONArray) jsonObject.get("kunden");
			for (int i = 0; i < kunden.size(); i++) {
				JSONObject einKunde = (JSONObject) kunden.get(i);
				insertData(einKunde.get("name").toString());
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public String[] getKunde(String name) {
		String[] result = new String[3];
		try {
			//PreparedStatement prep = con.prepareStatement("SELECT * FROM kunden WHERE name = ? OR name LIKE CONCAT('%,?,%')");
			PreparedStatement prep = con.prepareStatement("SELECT * FROM kunden WHERE name = ?");
			prep.setString(1,name);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result[0] = String.valueOf(rs.getInt(1));
			result[1] = rs.getString(2);
			//result[2] = rs.getString(3);
			rs.close();
			prep.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
}
