package P7;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Testklasse - deze klasse test alle andere klassen in deze package.
 * <p>
 * System.out.println() is alleen in deze klasse toegestaan (behalve voor exceptions).
 *
 * @author tijmen.muller@hu.nl
 */
public class Main {
    // Creëer een factory voor Hibernate sessions.
    private static final SessionFactory factory;

    static {
        try {
            // Create a Hibernate session factory
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retouneer een Hibernate session.
     *
     * @return Hibernate session
     * @throws HibernateException
     */
    private static Session getSession() throws HibernateException {
        return factory.openSession();
    }

    public static void main(String[] args) throws SQLException {
        testFetchAll();
        testDAOHibernate();
    }

    /**
     * P6. Haal alle (geannoteerde) entiteiten uit de database.
     */
    private static void testFetchAll() {
        Session session = getSession();
        try {
            Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                Query query = session.createQuery("from " + entityType.getName());

                System.out.println("[Test] Alle objecten van type " + entityType.getName() + " uit database:");
                for (Object o : query.list()) {
                    System.out.println("  " + o);
                }
                System.out.println();
            }
        } finally {
            session.close();
        }
    }

    public static void testDAOHibernate() {
        AdresDAOHibernate adao = new AdresDAOHibernate(getSession());
        ReizigerDAOHibernate rdao = new ReizigerDAOHibernate(getSession());
        OVChipkaartDAOHibernate odao = new OVChipkaartDAOHibernate(getSession());
        ProductDAOHibernate pdao = new ProductDAOHibernate(getSession());

        Reiziger reiziger = new Reiziger(10, "M", "", "Dol", java.sql.Date.valueOf("2001-02-03"));
        Adres adres = new Adres(10, "2968GB", "15", "Waal", "Waal", 10);
        OVChipkaart ovChipkaart = new OVChipkaart(10101, java.sql.Date.valueOf("2029-09-10"), 3, 1010.10f, 10);
        Product product = new Product(10, "TEST DP7", "TEST PRODUCT VOOR DP7", 10.00f);

        System.out.println("------ REIZIGER -----");
        System.out.println("--- save + findAll ---");
        System.out.println(rdao.findAll());
        rdao.save(reiziger);
        System.out.println(rdao.findAll());

        System.out.println("--- update + findById ---");
        System.out.println(rdao.findById(reiziger.getReiziger_id()));

        System.out.println("\n\n------ ADRES -----");
        System.out.println("--- save + findAll ---");
        System.out.println(adao.findAll());
        adao.save(adres);
        System.out.println(adao.findAll());

        System.out.println("--- update + findByReiziger ---");
        adres.setHuisnummer("15a");
        adao.update(adres);
        System.out.println(adao.findByReiziger(reiziger));

        System.out.println("--- delete ---");
        adao.delete(adres);
        System.out.println(adao.findAll());


        System.out.println("\n\n------ PRODUCT -----");
        System.out.println("--- save + findAll ---");
        System.out.println(pdao.findAll());
        pdao.save(product);
        System.out.println(pdao.findAll());

        System.out.println("--- update ---");
        product.setPrijs(20.00f);
        System.out.println(pdao.findAll());


        System.out.println("\n\n------ OVCHIPKAART + findByReiziger -----");
        System.out.println("--- save ---");
        odao.save(ovChipkaart);
        System.out.println(odao.findByReiziger(reiziger));

        System.out.println("--- update ---");
        ovChipkaart.setSaldo(2020.20f);
        odao.update(ovChipkaart);
        System.out.println(odao.findByReiziger(reiziger));

        System.out.println("--- wijs product toe ---");
        ovChipkaart.getProducten().add(product);
        odao.update(ovChipkaart);
        System.out.println(odao.findByReiziger(reiziger));


        System.out.println("\n\n----- DELETE EVERYTHING -----");

        System.out.println("--- delete ovchipkaart ---");
        odao.delete(ovChipkaart);

        System.out.println("--- delete product ---");
        pdao.delete(product);
        System.out.println(pdao.findAll());

        System.out.println("---- delete reiziger ----");
        rdao.delete(reiziger);
        System.out.println(rdao.findById(reiziger.getReiziger_id()));
    }
}
