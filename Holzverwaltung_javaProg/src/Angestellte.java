import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Angestellte {
	private Connection con = null;
	private String path = "data.json";
	
	public Angestellte(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM angestellte LIMIT 1;");
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
	
	public String[][] getAllAngestellte(){
		String[][] result = null;
		try {
			//die Anzahl der Angestellten für Auftrag suchen
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM angestellte;");
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][6];
			//angestellte anhand von auftragId heraussuchen
			prep = con.prepareStatement("SELECT name, gehalt, position, schwarzgeld, stundenlohn FROM angestellte;");
			rs = prep.executeQuery();
			//Array befüllen
			for (int i = 0; rs.next(); i++) {
				result[i][0]=rs.getString(1);
				result[i][1]=String.valueOf(rs.getDouble(2));
				result[i][2]=rs.getString(3);
				result[i][3]=String.valueOf(rs.getDouble(4));
				result[i][5]=String.valueOf(rs.getDouble(5));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	
	public String[][] getAngestellte(int auftragId){
		String[][] result = null;
		try {
			//die Anzahl der Angestellten für Auftrag suchen
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM angestellte a "
					+ "JOIN multiple_angestellte ma ON a.angestellten_id = ma.angestellteid "
					+ "JOIN auftrag af ON ma.auftragId = af.rechnungsNr WHERE af.rechnungsNr = ?");
			prep.setInt(1, auftragId);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][4];
			//angestellte anhand von auftragId heraussuchen
			prep = con.prepareStatement("SELECT a.position, a.name, a.stundenlohn, ma.stunden FROM angestellte a "
					+ "JOIN multiple_angestellte ma ON a.angestellten_id = ma.angestellteid "
					+ "JOIN auftrag af ON ma.auftragId = af.rechnungsNr WHERE af.rechnungsNr = ?");
			prep.setInt(1, auftragId);
			rs = prep.executeQuery();
			//Array befüllen
			for (int i = 0; rs.next(); i++) {
				result[i][0]=rs.getString(1);
				result[i][1]=rs.getString(2);
				result[i][2]=String.valueOf(rs.getDouble(3));
				result[i][3]=String.valueOf(rs.getInt(4));
			}
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	
	private void insertData(String name, double gehalt, String position, Date[] urlaubstage, double schwarzgeld, double stundenlohn) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO angestellte (name, gehalt, position, "
					+ "urlaubstage, schwarzgeld, stundenlohn) VALUES(?,?,?,?,?,?)");
			prep.setString(1, name);
			prep.setDouble(2,gehalt);
			prep.setString(3, position);
			prep.setObject(4, urlaubstage);
			prep.setDouble(5, schwarzgeld);
			prep.setDouble(6, stundenlohn);
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
			JSONArray angestellte = (JSONArray) jsonObject.get("angestellte");
			for (int i = 0; i < angestellte.size(); i++) {
				JSONObject eineAngestellte = (JSONObject) angestellte.get(i);
				JSONArray urlaubstage = (JSONArray) eineAngestellte.get("urlaubstage");
				Date[] urlaub;
				if(urlaubstage!=null) {
					urlaub = new Date[urlaubstage.size()];
					for (int j = 0; j < urlaubstage.size(); j++) {
						JSONObject ur = (JSONObject) urlaubstage.get(j);
						urlaub[j] = Date.valueOf(ur.get("datum").toString());
					}
				}else {
					urlaub = null;
				}
				insertData(eineAngestellte.get("name").toString(),Double.parseDouble(eineAngestellte.get("gehalt").toString()),
						eineAngestellte.get("position").toString(),urlaub,Double.parseDouble(eineAngestellte.get("schwarzgeld").toString()),	
						Double.parseDouble(eineAngestellte.get("stundenlohn").toString()));
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
