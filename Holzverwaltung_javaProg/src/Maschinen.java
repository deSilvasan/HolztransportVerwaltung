import java.io.FileReader;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Maschinen {
	
	private Connection con = null;
	private String path = "data.json";
	
	public Maschinen(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM maschinen LIMIT 1;");
			rs.next();
			if(rs.getInt(1)<1) {
				getDataJSON();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String name, String maschinent, int baujahr, int bstunden, double kosten, double kostenStunden) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO maschinen (name, maschinentyp, baujahr, "
					+ "betriebsstunden, angefalleneKosten, maschinenstundenkosten) VALUES(?,?,?,?,?,?)");
			prep.setString(1, name);
			prep.setString(2, maschinent);
			prep.setInt(3, baujahr);
			prep.setInt(4, bstunden);
			prep.setDouble(5, kosten);
			prep.setDouble(6, kostenStunden);
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
			JSONArray maschinen = (JSONArray) jsonObject.get("maschinen");
			for (int i = 0; i < maschinen.size(); i++) {
				JSONObject eineMaschine = (JSONObject) maschinen.get(i);
				insertData(eineMaschine.get("name").toString(),eineMaschine.get("maschinentyp").toString(),
						Integer.parseInt(eineMaschine.get("baujahr").toString()),
						Integer.parseInt(eineMaschine.get("betriebsstunden").toString()),
						Double.parseDouble(eineMaschine.get("angefalleneKosten").toString()),
						Double.parseDouble(eineMaschine.get("maschinenstundenkosten").toString()));
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
