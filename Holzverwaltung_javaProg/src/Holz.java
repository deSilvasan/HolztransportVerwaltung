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
			rs.close();
			stmt.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void insertData(String name, int feuchtigkeit, String abmessungen, double kosten) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO holz (holzart, feuchtigkeitsgehalt, abmessungen, kosten) VALUES(?,?,?,?)");
			prep.setString(1, name);
			prep.setInt(2, feuchtigkeit);
			prep.setString(3, abmessungen);
			prep.setDouble(4, kosten);
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
			JSONArray holz = (JSONArray) jsonObject.get("holz");
			for (int i = 0; i < holz.size(); i++) {
				JSONObject einHolz = (JSONObject) holz.get(i);
				insertData(einHolz.get("holzart").toString(),Integer.parseInt(einHolz.get("feuchtigkeitsgehalt").toString()),
						einHolz.get("abmessungen").toString(),Double.valueOf(einHolz.get("kosten").toString()));
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String[][] getAllHolz(){
		String[][] result = null;
		try {
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM holz;");
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][4];
			prep = con.prepareStatement("SELECT holzart, feuchtigkeitsgehalt, abmessungen, kosten FROM holz;");
			rs = prep.executeQuery();
			for (int i = 0; rs.next(); i++) {
				result[i][0] = rs.getString(1);
				result[i][1] = String.valueOf(rs.getInt(2));
				result[i][2] = rs.getString(3);
				result[i][3] = String.valueOf(rs.getDouble(4));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	
	public String[][] getHolz(int auftragId){
		String[][] result = null;
		try {
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM holz h "
					+ "JOIN multiple_holz mh ON mh.holzId=h.holzId "
					+ "JOIN auftrag a ON mh.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
			prep.setInt(1, auftragId);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][4];
			prep = con.prepareStatement("SELECT h.holzart, h.abmessungen, mh.menge, h.kosten FROM holz h "
					+ "JOIN multiple_holz mh ON mh.holzId=h.holzId "
					+ "JOIN auftrag a ON mh.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
			prep.setInt(1, auftragId);
			rs = prep.executeQuery();
			for (int i = 0; rs.next(); i++) {
				result[i][0] = rs.getString(1);
				result[i][1] = rs.getString(2);
				result[i][2] = String.valueOf(rs.getInt(3));
				result[i][3] = String.valueOf(rs.getDouble(4));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
}
