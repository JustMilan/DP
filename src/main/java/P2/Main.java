package P2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Main {
   public static void main(String[] args) throws SQLException, IOException {

       testReizigerDAO(new ReizigerDAOPsql(getConnection()));
   }

    private static void testReizigerDAO(ReizigerDAO rdao) throws IOException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        System.out.println("\n        -------findAll()--------\n");
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();
        System.out.println("------------------------------------------");


        System.out.println("\n        -------save()--------\n");
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
        System.out.println("------------------------------------------");


        System.out.println("\n        -------findById()--------\n");
        System.out.println(rdao.findById(sietske.getId()));
        System.out.println("------------------------------------------");


        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
        System.out.println("\n        -------update()--------\n");
        System.out.print("Voer het gewenste tussenvoegsel in: ");
        BufferedReader tussenvoegselReader =
                new BufferedReader(new InputStreamReader(System.in));
        String tussenvoegsel = tussenvoegselReader.readLine();
        sietske.setTussenvoegsel(String.valueOf(tussenvoegsel));
        System.out.print("Voer de gewenste achternaam in: ");
        BufferedReader achternaamReader =
                new BufferedReader(new InputStreamReader(System.in));
        String achternaam = achternaamReader.readLine();
        sietske.setAchternaam(achternaam);
        rdao.update(sietske);
        System.out.println(rdao.findById(sietske.getId()));
        System.out.println("------------------------------------------");


        System.out.println("\n        -------delete()--------\n");
        int wId = 1000;
        String wDatum = "2020-04-20";
        Reiziger weggooier = new Reiziger(wId, "W", "eg", "Gooiertje", Date.valueOf(wDatum));
        rdao.save(weggooier);
        System.out.println("Reiziger " + rdao.findById(wId) + " bestaat.");
        rdao.delete(weggooier);
        System.out.println("Bewijs, reiziger met id " + rdao.findById(wId));
        System.out.println("------------------------------------------");


        System.out.println("\n        -------findByDatum()--------\n");
        String fDatum = "1999-09-09";
        Reiziger dReiziger = new Reiziger(9999, "S", "van", "Toorn", Date.valueOf(fDatum));
        rdao.save(dReiziger);
        System.out.println("findByDatum werkt: " + rdao.findByGbDatum(fDatum));
        System.out.println("------------------------------------------");
        System.out.println('\n' + '\n' + '\n');
    }

    public static Connection getConnection() throws SQLException {
           String url = "jdbc:postgresql://localhost/ovchip";
           Properties props = new Properties();
           props.setProperty("user", "postgres");
           props.setProperty("password", "K9wVPyHJ!.");
           props.setProperty("ssl", "false");
           Connection conn = DriverManager.getConnection(url, props);
           return conn;
    }

    public static void closeConnection(Connection connection) throws SQLException {
       connection.close();
    }
}
