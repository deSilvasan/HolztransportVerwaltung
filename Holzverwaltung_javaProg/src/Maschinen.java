import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
			//wenn keine Daten in der DB sind werden welche über JSON-Import hinzugefügt
			if(rs.getInt(1)<1) {
				getDataJSON();
			}
			rs.close();
			stmt.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public String[][] getAllMaschinen(){
		String[][] result = null;
		try {
			//die anzahl der Maschinen für Array suchen
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM maschinen m;");
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][6];
			//Maschinen anhand von auftragID heraussuchen
			prep = con.prepareStatement("SELECT name, maschinentyp, baujahr, betriebsstunden, maschinenstundenkosten FROM maschinen m;");
			rs = prep.executeQuery();
			//Array befüllen
			for (int i = 0;rs.next(); i++) {
				result[i][0] = rs.getString(1);
				result[i][1] = rs.getString(2);
				result[i][2] = String.valueOf(rs.getInt(3));
				result[i][3] = String.valueOf(rs.getInt(4));
				result[i][4] = String.valueOf(rs.getDouble(5));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	
	public String[][] getMaschinen(int auftragId) {
		String[][] result = null;
		try {
			//die anzahl der Maschinen für Array suchen
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM maschinen m "
					+ "JOIN multiple_maschinen mm ON m.maschinen_id = mm.maschinen_id "
					+ "JOIN auftrag a ON mm.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
			prep.setInt(1,auftragId);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][4];
			//Maschinen anhand von auftragID heraussuchen
			prep = con.prepareStatement("SELECT m.name, m.maschinentyp, m.maschinenstundenkosten, mm.maschinenstunden FROM maschinen m "
					+ "JOIN multiple_maschinen mm ON m.maschinen_id = mm.maschinen_id "
					+ "JOIN auftrag a ON mm.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
			prep.setInt(1,auftragId);
			rs = prep.executeQuery();
			//Array befüllen
			for (int i = 0;rs.next(); i++) {
				result[i][0] = rs.getString(1);
				result[i][1] = rs.getString(2);
				result[i][2] = String.valueOf(rs.getDouble(3));
				result[i][3] = String.valueOf(rs.getInt(4));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
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
