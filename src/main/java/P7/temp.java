package P7;

import org.hibernate.Session;

import java.util.List;

public class temp {
    public static void testDAOHibernate(Session session, ReizigerDAO rdao, AdresDAO adao) {
        try {
            System.out.println("\nTest AdresDAO:");
            //maak nieuwe reiziger aan voor de testAdresDAO, deze is nodig voor sommige tests
            Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(("1981-03-14")));
            rdao.save(sietske);

            //maak nieuw adres aan
            Adres adres = new Adres(88, "3532SE", "71", "Spinozaweg", "Utrecht", 77);
            adao.save(adres);
            System.out.println(adres);

            //update adres + find by reiziger
            adres.setHuisnummer("64");
            adao.update(adres);

            Adres sietskeUpdatedAdres = adao.findByReiziger(sietske);
            System.out.println(sietskeUpdatedAdres);

            //nieuwe reiziger + nieuw adres gekoppeld
            sietske.setAdres(sietskeUpdatedAdres);
            System.out.println(sietske);

            //verwijder adres
            adao.delete(adres);

            //Verwijder reiziger
            boolean sietskeDoeiDoei = rdao.delete(sietske);
            System.out.println("Is sietske weg? " + sietskeDoeiDoei);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testReizigerDAO(ReizigerDAOHibernate rdao) {
        try {

            // Haal alle reizigers op uit de database
            List<Reiziger> reizigers = rdao.findAll();
            System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
            for (Reiziger r : reizigers) {
                System.out.println(r);
            }
            System.out.println();

            // Maak een nieuwe reiziger aan en persisteer deze in de database
            Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
            System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
            rdao.save(sietske);
            reizigers = rdao.findAll();
            System.out.println(reizigers.size() + " reizigers\n");

            //Update reiziger
            sietske.setAchternaam("Kees");
            rdao.update(sietske);

            //findById
            Reiziger reizigerById = rdao.findById(77);
            System.out.println(reizigerById);

            //findByGbdatum
            List<Reiziger> reizigersByGbDatum = rdao.findByGbDatum("1981-03-14");
            System.out.println(reizigersByGbDatum);

            //Verwijder reiziger
            boolean sietskeDoeiDoei = rdao.delete(sietske);
            System.out.println("Is sietske weg? " + sietskeDoeiDoei + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testOVChipkaartDAO(OVChipkaartDAOHibernate odao, ReizigerDAOHibernate rdao) {
        try {

            System.out.println("\nTest OVChipkaartDAO:");

            //maak nieuwe reiziger aan voor de testOVChipkaartDAO, deze is nodig voor sommige tests
            Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));

            //nieuwe Ov-kaart
            OVChipkaart ovChipkaart1 = new OVChipkaart(1111, java.sql.Date.valueOf("2021-01-01"), 1, 10, 77);
            OVChipkaart ovChipkaart2 = new OVChipkaart(22222,java.sql.Date.valueOf("2022-02-02"), 1, 20, 77);
            OVChipkaart ovChipkaart3 = new OVChipkaart(33333, java.sql.Date.valueOf("2023-03-03"), 1, 30, 77);

            //nieuwe producten
            Product product1 = new Product(11, "Test1", "beschrijving1", 11);
            Product product2 = new Product(22, "Test2", "beschrijving2", 22);

            //ov kaarten toevoegen aan reiziger
            sietske.getOvChipkaart().add(ovChipkaart1);
            sietske.getOvChipkaart().add(ovChipkaart2);
            sietske.getOvChipkaart().add(ovChipkaart3);
            System.out.println(sietske.getOvChipkaart());

            //product toevoegen aan ovchipkaart
            ovChipkaart1.getProducten().add(product1);
            ovChipkaart1.getProducten().add(product2);

            //persisteer reiziger + bijbehorende ov-chipkaarten
            rdao.save(sietske);

            //persisteer ovkaart + bijbehorende producten
            odao.save(ovChipkaart1);
            System.out.println("Opgeslagen OV: " + ovChipkaart1 + "\n+ de producten van deze ov: " + ovChipkaart1.getProducten());

            //Update OV-Kaart naar 2e klas
            System.out.println("Voor Update: " + ovChipkaart1.getProducten());
            ovChipkaart1.setKlasse(2);
            //test of de ovkaarten van dit product wel juist worden geupdatet
            ovChipkaart1.getProducten().remove(product1);
            odao.update(ovChipkaart1);
            System.out.println("Na Update: " + ovChipkaart1);

            //find by Id (en de bijbehorende ovkaarten van de reiziger)
            Reiziger sietskeById = rdao.findById(77);
            System.out.println("Updated Reiziger + de bijbehorende ovkaarten: " + sietskeById + "\n");

            //Verwijder reiziger + ovchipkaarten van deze reiziger
            boolean sietskeDoeiDoei = rdao.delete(sietske);
            System.out.println("Is sietske weg? " + sietskeDoeiDoei);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testProductDAO(ProductDAOHibernate pdao, OVChipkaartDAOHibernate odao, ReizigerDAOHibernate rdao) {
        try {
            System.out.println("\nTest ProductDAO:");

            //maak nieuwe reiziger aan voor de testOVChipkaartDAO, deze is nodig voor sommige tests
            Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));

            //nieuwe Ov-kaart
            OVChipkaart ovChipkaart1 = new OVChipkaart(11111, java.sql.Date.valueOf("2021-01-01"), 1, 10, 77);

            //nieuwe producten
            Product product1 = new Product(111, "Test1", "beschrijving1", 11);

            //voeg ovkaart toe aan product
            product1.getOvChipkaarten().add(ovChipkaart1);

            //Persisteer de reiziger, met haar ov-kaarten en de producten van de ov-kaarten
            rdao.save(sietske);
            odao.save(ovChipkaart1);
            pdao.save(product1);

            //update het product
            product1.setBeschrijving("ultieme test");
            System.out.println(product1);
            pdao.update(product1);

            //find alle producten
            List<Product> alleProducten = pdao.findAll();
            System.out.println("[Test] ProductDAO.findAll() geeft de volgende producten:");
            for (Product product : alleProducten)
                System.out.println(product);
            System.out.println("\n");

            //find de producten van een bepaalde ovchipkaart
            List<Product> producten = pdao.findByOVChipkaart(ovChipkaart1);
            System.out.println("[Test] ProductDAO.findByOVChipkaart() geeft de volgende producten:");
            for (Product product : producten)
                System.out.println(product);

            //verwijder product, ovkaart, reiziger
            pdao.delete(product1);
            odao.delete(ovChipkaart1);
            rdao.delete(sietske);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
