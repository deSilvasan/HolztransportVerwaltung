
public class Main {
    public static void main(String[] args) {
    	DBConnection connection = new DBConnection();
    	Maschinen maschinen = new Maschinen(connection.getConnection());
    	Kunden kunden = new Kunden(connection.getConnection());
    	Holz holz = new Holz(connection.getConnection());
    	Angestellte angestellte = new Angestellte(connection.getConnection());
    	Holzlager lager = new Holzlager(connection.getConnection());
    	Auftrag auftrag = new Auftrag(connection.getConnection());
    }
}