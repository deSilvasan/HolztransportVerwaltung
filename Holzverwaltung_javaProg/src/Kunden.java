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
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String name) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO kunden (name) VALUES(?)");
			prep.setString(1, name);
			prep.executeUpdate();
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
}
