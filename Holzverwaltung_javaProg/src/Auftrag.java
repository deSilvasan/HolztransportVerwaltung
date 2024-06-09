import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Auftrag {
	private Connection con = null;
	private String path = "data.json";
	
	public Auftrag(Connection conn) {
		this.con = conn;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM auftrag LIMIT 1;");
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
	public String[][] getAutrag(int KundenId) {
		String[][] result = null;
		try {
			PreparedStatement prep = con.prepareStatement("SELECT count(*) FROM auftrag WHERE kunden_Id = ?");
			prep.setInt(1, KundenId);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[rs.getInt(1)][4];
			prep = con.prepareStatement("SELECT rechnungsNr, zeitraum_a, zeitraum_e, auftragsbeschreibung FROM auftrag WHERE kunden_Id = ?");
			prep.setInt(1, KundenId);
			rs = prep.executeQuery();
			for (int i = 0; rs.next(); i++) {
				result[i][0]=String.valueOf(rs.getInt(1));
				result[i][1]=String.valueOf(rs.getDate(2));
				result[i][2]=String.valueOf(rs.getDate(3));
				result[i][3]=rs.getString(4);
			}
			rs.close();
			prep.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	
	public String[] getAutragRechnungsNr(int rechnungsNr) {
		String[] result = null;
		try {
			PreparedStatement prep = con.prepareStatement("SELECT rechnungsNr, zeitraum_a, zeitraum_e, auftragsbeschreibung FROM auftrag WHERE rechnungsNr = ?");
			prep.setInt(1, rechnungsNr);
			ResultSet rs = prep.executeQuery();
			rs.next();
			result = new String[4];
			result[0]=String.valueOf(rs.getInt(1));
			result[1]=String.valueOf(rs.getDate(2));
			result[2]=String.valueOf(rs.getDate(3));
			result[3]=rs.getString(4);
			rs.close();
			prep.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
	private void insertData(Date zeitraum_a, Date zeitraum_b, String kundeN, String auftragsbeschreibung, String[][] maschinen, String[][]holz, 
			String[][] angestellte) {
		try {
			//kundenId dmit man Auftrag erstellen kann
			PreparedStatement prep = con.prepareStatement("SELECT kunden_id FROM kunden WHERE name = ?");
			prep.setString(1, kundeN);
			ResultSet rs = prep.executeQuery();
			rs.next();
			// auftrag erstellen
			prep = con.prepareStatement("INSERT INTO auftrag (zeitraum_a, zeitraum_e, kunden_id, auftragsbeschreibung) VALUES(?,?,?,?)");
			prep.setDate(1, zeitraum_a);
			prep.setDate(2, zeitraum_b);
			prep.setInt(3, rs.getInt(1));
			prep.setString(4, auftragsbeschreibung);
			prep.executeUpdate();
			//rechnungsNr holen
			prep = con.prepareStatement("SELECT rechnungsNr FROM auftrag WHERE kunden_id = ? AND zeitraum_a = ?");
			prep.setInt(1,rs.getInt(1));
			prep.setDate(2, zeitraum_a);
			rs = prep.executeQuery();
			rs.next();
			int auftrag_id = rs.getInt(1);
			for (int i = 0; i < maschinen.length; i++) {
				//maschinenId heraussuchen
				prep = con.prepareStatement("SELECT maschinen_id FROM maschinen WHERE name = ?");
				prep.setString(1,maschinen[i][0]);
				rs = prep.executeQuery();
				rs.next();
				System.out.println(maschinen[i][0]);
				System.out.println(maschinen[i][1]);
				System.out.println(rs.getInt(1));
				System.out.println(maschinen.length);
				//maschinen in multiple_maschinen einfügen
				prep = con.prepareStatement("INSERT INTO multiple_maschinen VALUES (?,?,?)");
				prep.setInt(1, auftrag_id);
				prep.setInt(2, rs.getInt(1));
				int maschinenAnzahl = Integer.valueOf(maschinen[i][1]);
				prep.setInt(3, maschinenAnzahl);
				prep.executeUpdate();
				System.out.println("maschinen");
			}
			for (int j = 0; j < holz.length; j++) {
				//holzId heraussuchen
				prep = con.prepareStatement("SELECT holzid FROM holz WHERE holzart = ?");
				prep.setString(1, holz[j][0]);
				rs = prep.executeQuery();
				rs.next();
				//holz in multiple_holz einfügen
				prep = con.prepareStatement("INSERT INTO multiple_holz VALUES (?,?,?)");
				prep.setInt(1, auftrag_id);
				prep.setInt(2, rs.getInt(1));
				prep.setInt(3, Integer.valueOf(holz[j][1]));
				prep.executeUpdate();
				System.out.println("holz");
			}
			for (int j = 0; j < angestellte.length; j++) {
				//angestelltenId heraussuchen
				prep = con.prepareStatement("SELECT angestellten_id FROM angestellte WHERE name = ?");
				prep.setString(1,angestellte[j][0]);
				rs = prep.executeQuery();
				rs.next();
				//angestellte in multiple angestellte einfügen
				prep = con.prepareStatement("INSERT INTO multiple_angestellte VALUES (?,?,?)");
				prep.setInt(1, auftrag_id);
				prep.setInt(2, rs.getInt(1));
				prep.setInt(3, Integer.valueOf(angestellte[j][1]));
				prep.executeUpdate();
				System.out.println("angestellte");
			}
				rs.close();
				prep.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getCause());
		}
	}
	
	private void getDataJSON() {
		try {
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new BufferedReader(new FileReader(path)));
			JSONObject jsonObject = (JSONObject)obj;
			JSONArray auftraege = (JSONArray) jsonObject.get("auftrag");
			//aufträge aus JSON-File herausholen und einzeln in DB einlesen
			for (int i = 0; i < auftraege.size(); i++) {
				JSONObject einAuftrag = (JSONObject) auftraege.get(i);
				//maschinen in ein Array einfügen
				JSONArray maschinen = (JSONArray) einAuftrag.get("maschine");
				String[][] maschinen2 = new String[maschinen.size()][2];
				for (int j = 0; j < maschinen.size(); j++) {
					JSONObject m = (JSONObject) maschinen.get(j);
					maschinen2[j][0] = m.get("name").toString();
					maschinen2[j][1] = m.get("maschinenstunden").toString();
				}
				//holz in ein Array einfügen
				JSONArray holz = (JSONArray) einAuftrag.get("holz");
				String[][] holz2 = new String[holz.size()][2];
				for (int j = 0; j < holz.size(); j++) {
					JSONObject h = (JSONObject) holz.get(j);
					holz2[j][0] = h.get("holzart").toString();
					holz2[j][1] = h.get("menge").toString();
				}

				//angestellten in ein Array einfügen
				JSONArray angestellte = (JSONArray) einAuftrag.get("angestellte");
				String[][] angestellte2 = new String[angestellte.size()][2];
				for (int j = 0; j < angestellte.size(); j++) {
					JSONObject a = (JSONObject) angestellte.get(j);
					angestellte2[j][0] = a.get("name").toString();
					angestellte2[j][1] = a.get("stunden").toString();
				}

				//Arrays und andere Daten in insertData-Methode geben
				insertData(Date.valueOf(einAuftrag.get("zeitraum_a").toString()),Date.valueOf(einAuftrag.get("zeitraum_e").toString()),einAuftrag.get("kunde").toString(),
						einAuftrag.get("auftragsbeschreibung").toString(), maschinen2, holz2,angestellte2);
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getCause());
			System.out.println(e.getStackTrace());
		}
	}
}
