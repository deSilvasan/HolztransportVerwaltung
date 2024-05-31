import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Holzlager {
	private Connection con = null;
	private String path = "data.json";
	
	public Holzlager(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM holzlager LIMIT 1;");
			rs.next();
			if(rs.getInt(1)<1) {
				getDataJSON();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String holzart, String abmessungen, int mengelagernd, int mindestbestand) {
		try {
			//holzId von holz tabelle suchen
			PreparedStatement prep = con.prepareStatement("SELECT holzId FROM holz WHERE holzart = ? AND abmessungen = ?");
			prep.setString(1, holzart);
			prep.setString(2, abmessungen);
			ResultSet rs = prep.executeQuery();
			rs.next();
			int holzId = rs.getInt(1); 
			//holzlagerstätte auffüllen
			prep = con.prepareStatement("INSERT INTO holzlager (holzId, mengelagernd, mindestbestand) VALUES(?,?,?)");
			prep.setInt(1, holzId);
			prep.setInt(2, mengelagernd);
			prep.setInt(3, mindestbestand);
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
			JSONArray holzlager = (JSONArray) jsonObject.get("holzlager");
			for (int i = 0; i < holzlager.size(); i++) {
				JSONObject einHolz = (JSONObject) holzlager.get(i);
				insertData(einHolz.get("holz").toString(), einHolz.get("abmessungen").toString(), Integer.parseInt(einHolz.get("mengelagernd").toString()),
						Integer.parseInt(einHolz.get("mindestbestand").toString()));
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
