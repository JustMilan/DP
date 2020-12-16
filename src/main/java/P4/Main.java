package P4;

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
        Connection conn = getConnection();

//        testReizigerDAO(new ReizigerDAOPsql(conn));
//        testAdresDAO(new AdresDaoPsql(conn), new ReizigerDAOPsql(conn));
        testOVChipkaartDAO(new OVChipkaartDAOPsql(conn), new ReizigerDAOPsql(conn));
        closeConnection(conn);
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
        return DriverManager.getConnection(url, props);
    }

    public static void testAdresDAO(AdresDaoPsql adao, ReizigerDAOPsql rdao) throws IOException {
        System.out.println("---------- Test AdresDAO -------------");

        System.out.println("\n        -------findAll()--------\n");
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdressenDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }
        System.out.println();
        System.out.println("------------------------------------------");

        System.out.println("\n        -------save()--------\n");

        Adres thuis = new Adres(123, "2968GB", "15", "Waal", "Waal", 77);
        System.out.print("[Test] Eerst " + adressen.size() + " reizigers, na ReizigerDAO.save() ");
        adao.save(thuis);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " reizigers\n");
        System.out.println("------------------------------------------");


        System.out.println("\n        -------findByReiziger()--------\n");
        String geboortedatum = "2001-03-02";
        int reizigerId = 132;
        Reiziger nieuwe = new Reiziger(reizigerId, "I", "P", "Maar", Date.valueOf(geboortedatum));
        rdao.save(nieuwe);
        int adresId = 321221;
        Adres adres = new Adres(adresId, "1234AB", "23A", "ABStraat", "Alfabet", nieuwe.getId());
        adao.save(adres);
        System.out.println(adao.findByReiziger(nieuwe));
        System.out.println("------------------------------------------");


        System.out.println("\n        -------update()--------\n");
        int rId = 34;
        int aId = 3142;
        Reiziger nieuwer = new Reiziger(rId, "I", "P", "Maar", Date.valueOf("2016-06-05"));
        rdao.save(nieuwer);
        Adres updatester = new Adres(aId, "1202AD", "34", "Jupperstraat", "Joekshoek", nieuwer.getId());
        adao.save(updatester);
        System.out.println("Voer de gewenste postcode in: ");
        BufferedReader postcodeReader =
                new BufferedReader(new InputStreamReader(System.in));
        String postcode = postcodeReader.readLine();
        updatester.setPostcode(postcode);
        System.out.print("Voer het gewenste huisnummer in: ");
        BufferedReader huisnummerReader =
                new BufferedReader(new InputStreamReader(System.in));
        String huisnummer = huisnummerReader.readLine();
        updatester.setHuisnummer(huisnummer);

        System.out.println("Voor update: " + adao.findByReiziger(nieuwer));
        adao.update(updatester);
        System.out.println("Na update: " + adao.findByReiziger(nieuwer));
        System.out.println("------------------------------------------");

        System.out.println("\n        -------delete()--------\n");
        int aaId = 4316;
        int rrId = 4530098;
        Reiziger rreiziger = new Reiziger(rrId, "K", "e", "T", Date.valueOf("1910-10-05"));
        Adres deleter = new Adres(aaId, "4321FL", "43", "Loekostrosso", "Gerenderwaard", rrId);
        rdao.save(rreiziger);
        adao.save(deleter);
        System.out.println("Bewijs dat deleter bestaat: " + adao.findByReiziger(rreiziger));
        adao.delete(deleter);
        System.out.println("Bewijs dat deleter verwijderd is: " + adao.findByReiziger(rreiziger));
        System.out.println("------------------------------------------");
    }

    public static void testOVChipkaartDAO(OVChipkaartDAOPsql odao, ReizigerDAOPsql rdao) {
        System.out.println("---------- Test OVChipkaarDAO -------------");

        System.out.println("\n        -------save()--------\n");
        Reiziger reiziger = new Reiziger(35445, "Z", "xes", "Xyu", Date.valueOf("2001-09-25"));
        Adres adres = new Adres(654, "4568ER", "44", "heuvelstraat", "Tiel", 35445);
        OVChipkaart testOv = new OVChipkaart(65498, Date.valueOf("2021-09-15"), 2, 22.50f, 35445);

        reiziger.setAdres(adres);
        reiziger.getOvChipkaarten().add(testOv);

        rdao.save(reiziger);

        System.out.println(odao.findByReiziger(reiziger));

        System.out.println("------------------------------------------");

        System.out.println("\n        -------update()--------\n");
        System.out.println("Voor update: " + odao.findByReiziger(reiziger));
        testOv.setSaldo(66.50f);
        rdao.update(reiziger);

        System.out.println("Na update: " + odao.findByReiziger(reiziger));

        System.out.println("------------------------------------------");

        System.out.println("\n        -------findByReiziger()--------\n");
        System.out.println(odao.findByReiziger(reiziger));
        System.out.println("------------------------------------------");

        System.out.println("\n        -------delete()--------\n");
        rdao.delete(reiziger);
        System.out.println("------------------------------------------");
    }

    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
