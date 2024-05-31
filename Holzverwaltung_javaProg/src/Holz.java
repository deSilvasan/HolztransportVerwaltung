import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Holz {
	private Connection con = null;
	private String path = "data.json";
	
	public Holz(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM holz LIMIT 1;");
			rs.next();
			if(rs.getInt(1)<1) {
				getDataJSON();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String name, int feuchtigkeit, String abmessungen) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO holz (holzart, feuchtigkeitsgehalt, abmessungen) VALUES(?,?,?)");
			prep.setString(1, name);
			prep.setInt(2, feuchtigkeit);
			prep.setString(3, abmessungen);
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
			JSONArray holz = (JSONArray) jsonObject.get("holz");
			for (int i = 0; i < holz.size(); i++) {
				JSONObject einHolz = (JSONObject) holz.get(i);
				insertData(einHolz.get("holzart").toString(),Integer.parseInt(einHolz.get("feuchtigkeitsgehalt").toString()),
						einHolz.get("abmessungen").toString());
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
