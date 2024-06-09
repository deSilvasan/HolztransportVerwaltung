import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
	static DBConnection connection = new DBConnection();
    public static void main(String[] args) {
    	Maschinen maschinen = new Maschinen(connection.getConnection());
    	Kunden kunden = new Kunden(connection.getConnection());
    	Holz holz = new Holz(connection.getConnection());
    	Angestellte angestellte = new Angestellte(connection.getConnection());
    	Holzlager lager = new Holzlager(connection.getConnection());
    	Auftrag auftrag = new Auftrag(connection.getConnection());
    	//things to interact with console
    	status currentStatus = status.LOGINKUNDE;
    	Scanner scanner = new Scanner(System.in);
    	String tmp ="";
    	String[] kundenDaten = new String[3];
    	DecimalFormat df = new DecimalFormat("0.00");
    	
    	System.out.println("WILLKOMMEN beim Unternehmen Schennet Holz");
    	while(currentStatus!=null) {
    		switch(currentStatus) {
        	//wenn ein Kunde sich einloggen will
        	case LOGINKUNDE:
        		System.out.println("Bitte geben sie ihren Namen ein: ");
        		tmp = scanner.nextLine();
        		if(tmp=="master") {
        			currentStatus = status.ANGESTELLTENVIEW;
        			break;
        		}
        		String[] temp = kunden.getKunde(tmp);
        		if(temp!= null) {
        			kundenDaten = temp;
        			currentStatus = status.UEBERSICHT;
            		System.out.println("Willkommen Herr/Frau "+kundenDaten[1]+"!");
        		}else {
        			System.out.println("Es wurde kein Kunden mit dem Namen "+tmp+"gefunden");
        		}
        		break;
        	//Kunde kann sich entscheiden, welche Aktion er machen möchte
        	case UEBERSICHT:
        		System.out.println(" Was möchten Sie tun? [A] ihre aktuellen Aufträge ansehen, [B] einen neuen Auftrag erstellen, [C] einen Auftrag löschen oder [D] aus dem Programm aussteigen");
        		tmp = scanner.next();
        		//Wenn Kinde Aufträge ansehen will
        		if(tmp.equals("A")) {
        			currentStatus=status.SEEAUFTRÄGE;
        		} else if(tmp.equals("B")) {
        			currentStatus = status.AUFTRAGERSTELLEN;
        		} else if(tmp.equals("C")) {
        			currentStatus = status.AUFTRAGELÖSCHEN;
        		} else if(tmp.equals("D")) {
        			currentStatus = status.LOGOUT;
        		} else {
        			System.out.println("falsche Eingabe!!");
        		}
        		break;
        	//Wenn der Kunde seine Aufträge ansehen will
        	case SEEAUFTRÄGE:
        		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
        		System.out.println("Ihre aktuellen Aufträge: ");
        		String[][] result = auftrag.getAutrag(Integer.valueOf(kundenDaten[0]));
    			//Aufträge werden ausgegeben
    			if(result.length>0) {
    				System.out.println("Rechnungs Nummer  |Zeitraum Anfang  |Zeitraum Ende   |Auftragsbeschreibung");
    				for (int i = 0; i < result.length; i++) {
						System.out.println(result[i][0]+"   |"+result[i][1]+"    |"+result[i][2]+"    |"+result[i][3]);
					}
    				//Frage nach Rechnung?
    				System.out.println("Möchten Sie die Rechnung eines Auftrages einsehen? [Y/N]");
    				tmp = scanner.next();
    				if(tmp.equals("Y")||tmp.equals("y")) {
    					System.out.println("RechnungsNr des Auftrages:");
    					tmp = scanner.next();
    					String[] auftragDaten = auftrag.getAutragRechnungsNr(Integer.valueOf(tmp));
    					String[][] maschinenDaten = maschinen.getMaschinen(Integer.valueOf(tmp));
    					String[][] holzDaten = holz.getHolz(Integer.valueOf(tmp));
    					String[][] angestellteDaten = angestellte.getAngestellte(Integer.valueOf(tmp));
    					System.out.println("");
    					System.out.println("");
    					System.out.println("---------------------------------------------------------------------------------------------------------");
    					System.out.println("|						Schennet Holz						|");
    					System.out.println("|						6474 Jerzens						|");
    					System.out.println("---------------------------------------------------------------------------------------------------------");
    					System.out.println("");
    					System.out.println("");
    					System.out.println("An ");
    					System.out.println(kundenDaten[1]+" 							Rechnungsnummer: "+auftragDaten[0]);
    					System.out.println("								Zeitraum: "+auftragDaten[2]+" - "+auftragDaten[1]);
    					System.out.println("");
    					System.out.println("RECHNUNG");
    					System.out.println("Bezeichnung			Menge		Einzelpreis			Gesamt");
    					System.out.println("---------------------------------------------------------------------------------------------------------");
    					double preisInsgesamt = 0;
    					for (int i = 0; (holzDaten!=null)&&(i < holzDaten.length); i++) {
        					double gesamtpreis = Integer.valueOf(holzDaten[i][2])+Double.valueOf(holzDaten[i][3]);
							System.out.println(holzDaten[i][0]+", "+holzDaten[i][1]+"			"+holzDaten[i][2]+"			"+holzDaten[i][3]+"			"+gesamtpreis);
							preisInsgesamt += gesamtpreis;
						}
    					for (int i = 0; (maschinenDaten!=null)&&(i < maschinenDaten.length); i++) {
							double gesamtpreis = Double.valueOf(maschinenDaten[i][2])+Integer.valueOf(maschinenDaten[i][3]);
							System.out.println(maschinenDaten[i][0]+", "+maschinenDaten[i][1]+"		"+maschinenDaten[i][3]+"			"+maschinenDaten[i][2]+"			"+gesamtpreis);
							preisInsgesamt += gesamtpreis;
						}
    					for (int i = 0; (angestellteDaten!=null)&&(i < angestellteDaten.length); i++) {
    						double gesamtpreis = Double.valueOf(angestellteDaten[i][2])+Integer.valueOf(angestellteDaten[i][3]);
    						System.out.println(angestellteDaten[i][0]+", "+angestellteDaten[i][1]+"		"+angestellteDaten[i][3]+"			"+angestellteDaten[i][2]+"			"+gesamtpreis);
    						preisInsgesamt += gesamtpreis;
						}
    					System.out.println("---------------------------------------------------------------------------------------------------------");
    					System.out.println("Nettopreis									"+df.format(preisInsgesamt));
    					System.out.println("Umsatzsteuer								"+df.format((preisInsgesamt/100)*20));
    					System.out.println("Bruttopreis									"+df.format((preisInsgesamt/100)*120));
    					System.out.println("---------------------------------------------------------------------------------------------------------");
    					System.out.println("Innerhalb von 14 Tagen auf das Bankkonto AT... überweisen, danke :)");
    					System.out.println("");
    					System.out.println("");
    				} 
    				
    			} else {
    				System.out.println("Sie haben noch keine Aufträge angelegt!");
    			}
    			currentStatus = status.UEBERSICHT;
				tmp = "";
        		break;
        	case AUFTRAGERSTELLEN:
        		System.out.println();
        		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
        		System.out.println("Von wann bis wann brauchen Sie unsere Dienstleistung?");
        		System.out.println("Anfangsdatum (Format yyyy-MM-tt):");
        		String anfangsdatum = scanner.nextLine();
        		System.out.println("Enddattum (Format yyyy-MM-tt):");
        		String enddatum = scanner.nextLine();
        		System.out.println("Beschreiben Sie den Auftrag: ");
        		String auftragsbeschreibung = scanner.nextLine();
        		System.out.println("Brauchen Sie Holz?");
        		String[][] allHolz = holz.getAllHolz();
        		System.out.println("HolzId		|HolzArt		|Feuchtigkeitsgehalt		|Abmessungen		|Kosten pro Stück");
        		for (int i = 0; i < allHolz.length; i++) {
        			System.out.println(i+"		|"+allHolz[i][0]+"		|"+allHolz[i][1]+"		|"+allHolz[i][2]+"		|"+allHolz[i][3]);
				}
        		String[][] allMaschinen = maschinen.getAllMaschinen();
        		System.out.println("MaschinenId		|Maschinenname		|Maschinentyp		|Baujahr		|Betriebsstunden		|Maschinenstundenkosten");
        		for (int i = 0; i < allMaschinen.length; i++) {
        			System.out.println();
				}
        		currentStatus = status.UEBERSICHT;
        		break;
        	case LOGOUT:
        		System.out.println("Möchten Sie ihre Änderungen im Programm speichern? [Y/N]");
        		tmp = scanner.next();
        		if(tmp.equals("Y")||tmp.equals("y")) {
        			currentStatus = null;
        			//saveDataJSON();
        			System.out.println("Auf Wiedersehen! Schönen Tag");
        		} else if (tmp.equals("N")||tmp.equals("n")) {
        			System.out.println("Auf Wiedersehen! Schönen Tag");
        		}
        		break;
        	}
    	}
    	connection.closeConnection();
    }
    private static void saveDataJSON() {
    	try {
			JSONObject mainJsonObject = new JSONObject();
			
			Connection con = connection.getConnection();
			PreparedStatement prep = con.prepareStatement("SELECT rechnungsNr, zeitraum_a, zeitraum_e, kunden_id, auftragsbeschreibung FROM auftrag;");
			ResultSet rs = prep.executeQuery();
			//Aufträge aus Datenbank holen
			JSONArray auftrag = new JSONArray();
			JSONObject jsObject = new JSONObject();
			System.out.println("works?");
			for (int i = 0; rs.next(); i++) {
				//metadaten vom Auftrag holen
				jsObject.put("zeitraum_a", rs.getDate(2));
				jsObject.put("zeitraum_e", rs.getDate(3));
				PreparedStatement prep2 = con.prepareStatement("SELECT k.name FROM kunden k "
						+ "JOIN auftrag a ON a.kunden_id = k.kunden_id WHERE a.kunden_id = ?;");
				prep2.setInt(1, rs.getInt(4));
				ResultSet rs2 = prep2.executeQuery();
				rs2.next();
				jsObject.put("kunde", rs2.getString(1));
				jsObject.put("auftragsbeschreibung",rs.getString(5));
				System.out.println("works!!");
				//maschinen-stuff vom auftrag einfügen
				JSONArray maschine = new JSONArray();
				prep2 = con.prepareStatement("SELECT m.name, mm.maschinenstunden FROM maschinen m "
						+ "JOIN multiple_maschinen mm ON m.maschinen_id = mm.maschinen_id "
						+ "JOIN auftrag a ON mm.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
				prep2.setInt(1, rs.getInt(1));
				rs2 = prep2.executeQuery();
				for (int j = 0;rs2.next(); j++) {
					JSONObject js = new JSONObject();
					js.put("name", rs2.getString(1));
					js.put("maschinenstunden", rs2.getInt(2));
					maschine.add(js);
				}
				jsObject.put("maschine", maschine);
				//holz-stuff vom auftrag einfügen
				System.out.println("works!!");
				JSONArray holzA = new JSONArray();
				prep2 = con.prepareStatement("SELECT h.holzart, mh.menge FROM holz h "
						+ "JOIN multiple_holz mh ON mh.holzId=h.holzId "
						+ "JOIN auftrag a ON mh.auftragId = a.rechnungsNr WHERE a.rechnungsNr = ?;");
				prep2.setInt(1, rs.getInt(1));
				rs2 = prep2.executeQuery();
				for (int j = 0; rs2.next(); j++) {
					JSONObject js = new JSONObject();
					js.put("holzart", rs2.getString(1));
					js.put("menge",rs2.getInt(2));
					holzA.add(js);
				}
				jsObject.put("holz", holzA);
				//angestellten-stuff vom auftrag einfügen
				System.out.println("works!!");
				JSONArray angestellten = new JSONArray();
				prep2 = con.prepareStatement("SELECT a.name, ma.stunden FROM angestellte a "
						+ "JOIN multiple_angestellte ma ON a.angestellten_id = ma.angestellteid "
						+ "JOIN auftrag af ON ma.auftragId = af.rechnungsNr WHERE af.rechnungsNR = ?;");
				prep2.setInt(1, rs.getInt(1));
				rs2 = prep2.executeQuery();
				for (int k = 0; rs2.next(); k++) {
					JSONObject js = new JSONObject();
					js.put("name", rs2.getString(1));
					js.put("stunden", rs2.getInt(2));
					angestellten.add(js);
				}
				jsObject.put("angestellte", angestellten);
				//ein Auftrag wird in das Auftrag-Array gespeichert
				auftrag.add(jsObject);
				rs2.close();
				prep2.close();
			}
			System.out.println("works!!");
			mainJsonObject.put("auftrag", auftrag);
			prep = con.prepareStatement("DELETE FROM multiple_maschinen;");
			prep.executeUpdate();
			prep = con.prepareStatement("DELETE FROM multiple_holz;");
			prep.executeUpdate();
			prep = con.prepareStatement("DELETE FROM multiple_angestellte;");
			prep.executeUpdate();
			prep = con.prepareStatement("DELETE FROM auftrag;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE auftrag_rechnungsnr_seq RESTART WITH 1;");
			prep.executeUpdate();
			//Kundendaten in JSON-File speichern
			JSONArray kunden = new JSONArray();
			prep = con.prepareStatement("SELECT name FROM kunden;");
			rs = prep.executeQuery();
			for(int i = 0; (rs.next());i++) {
				JSONObject js = new JSONObject();
				js.put("name", rs.getString(1));
				kunden.add(js);
			}
			System.out.println("works?");
			prep=con.prepareStatement("DELETE FROM kunden;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE kunden_kunden_id_seq RESTART WITH 1;");
			prep.executeUpdate();
			mainJsonObject.put("kunden", kunden);
			//Maschinendaten in JSON-File speichern
			JSONArray maschinen = new JSONArray();
			prep = con.prepareStatement("SELECT name, maschinentyp, baujahr, betriebsstunden, angefallenekosten, maschinenstundenkosten FROM maschinen m;");
			rs = prep.executeQuery();
			for (int i = 0; (rs.next()); i++) {
				JSONObject js = new JSONObject();
				js.put("name", rs.getString(1));
				js.put("maschinentyp", rs.getString(2));
				js.put("baujahr", rs.getInt(3));
				js.put("betriebsstunden", rs.getInt(4));
				js.put("angefalleneKosten", rs.getDouble(5));
				js.put("maschinenstundenkosten", rs.getDouble(6));
				maschinen.add(js);
			}
			prep = con.prepareStatement("DELETE FROM maschinen;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE maschinen_maschinen_id_seq RESTART WITH 1;");
			prep.executeUpdate();
			mainJsonObject.put("maschinen", maschinen);
			//holzlagerDaten in JSON-File speichern
			JSONArray holzlager = new JSONArray();
			prep = con.prepareStatement("SELECT holzid, mengelagernd, mindestbestand FROM holzlager;");
			rs = prep.executeQuery();
			for (int l = 0; rs.next(); l++) {
				PreparedStatement prep2 = con.prepareStatement("SELECT holzart, abmessungen FROM holz WHERE holzId = ?;");
				prep2.setInt(1, rs.getInt(1));
				ResultSet rs2 = prep2.executeQuery();
				rs2.next();
				JSONObject js = new JSONObject();
				js.put("holz", rs2.getString(1));
				js.put("abmessungen", rs2.getString(2));
				js.put("mengelagernd", rs.getInt(2));
				js.put("mindestbestand", rs.getInt(3));
				holzlager.add(js);
				rs2.close();
				prep2.close();
			}
			prep = con.prepareStatement("DELETE FROM holzlager;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE holzlager_bestandid_seq RESTART WITH 1;");
			prep.executeUpdate();
			mainJsonObject.put("holzlager", holzlager);
			//HolzDaten in JSON-File speichern
			JSONArray holz = new JSONArray();
			prep = con.prepareStatement("SELECT holzart, feuchtigkeitsgehalt, abmessungen, kosten FROM holz;");
			rs = prep.executeQuery();
			for (int j = 0; rs.next(); j++) {
				JSONObject js = new JSONObject();
				js.put("holzart", rs.getString(1));
				js.put("feuchtigkeitsgehalt", rs.getInt(2));
				js.put("abmessungen", rs.getString(3));
				js.put("kosten", rs.getDouble(4));
				holz.add(js);
			}
			prep = con.prepareStatement("DELETE FROM holz;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE holz_holzid_seq RESTART WITH 1;");
			prep.executeUpdate();
			mainJsonObject.put("holz", holz);
			//angestellteDaten in JSON-File speichern
			JSONArray angestellte = new JSONArray();
			prep = con.prepareStatement("SELECT name, gehalt, position, urlaubstage, schwarzgeld, stundenlohn FROM angestellte;");
			rs = prep.executeQuery();
			for (int k = 0; rs.next(); k++) {
				JSONObject js = new JSONObject();
				js.put("name", rs.getString(1));
				js.put("gehalt", rs.getDouble(2));
				js.put("position", rs.getString(3));
				JSONArray urlaubsArr = new JSONArray();
				Array tempArr = rs.getArray(4);
				Date[] str_Urlaub = (Date[])tempArr.getArray();
				for (int i = 0; i<str_Urlaub.length; i++) {
					JSONObject jsob = new JSONObject();
					jsob.put("datum", str_Urlaub[i]);
					urlaubsArr.add(jsob);
				}
				js.put("urlaubstage", urlaubsArr);
				js.put("schwarzgeld", rs.getDouble(5));
				js.put("stundenlohn", rs.getDouble(6));
				angestellte.add(js);
			}
			prep = con.prepareStatement("DELETE FROM angestellte;");
			prep.executeUpdate();
			prep = con.prepareStatement("ALTER SEQUENCE angestellte_angestellten_id_seq RESTART WITH 1;");
			prep.executeUpdate();
			mainJsonObject.put("angestellte", angestellte);
			BufferedWriter buff = new BufferedWriter(new FileWriter("data.json"));
			buff.write(mainJsonObject.toJSONString());
			buff.close();
			rs.close();
			prep.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
		}
    
    }
}