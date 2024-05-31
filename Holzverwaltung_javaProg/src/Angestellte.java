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
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
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
