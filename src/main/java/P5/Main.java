package P5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
        ProductDAOPsql pdao = new ProductDAOPsql(conn);
        ReizigerDAOPsql rdao = new ReizigerDAOPsql(conn);
        OVChipkaartDAOPsql odao = new OVChipkaartDAOPsql(conn);
        odao.setPdao(pdao);
        testProduct(pdao, rdao, odao);
        closeConnection(conn);
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost/ovchip";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "K9wVPyHJ!.");
        props.setProperty("ssl", "false");
        return DriverManager.getConnection(url, props);
    }

    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public static void testProduct(ProductDAO pdao, ReizigerDAO rdao, OVChipkaartDAO odao) {
        System.out.println("findAll");
        System.out.println((pdao.findAll()));
        System.out.println("---------------------");

        Reiziger reiziger = new Reiziger(9732, "M", "les", "Kernel", java.sql.Date.valueOf("2000-02-05"));
        OVChipkaart ovChipkaart = new OVChipkaart(99999, java.sql.Date.valueOf("2029-09-05"), 3, 112.99f, reiziger.getId());
        OVChipkaart ovChipkaart1 = new OVChipkaart(99998, java.sql.Date.valueOf("2027-06-12"), 2, 20.56f, reiziger.getId());
        Product product = new Product(123, "test", "testproduct", 10.00f);

        System.out.println("save\n");

        System.out.println("save reiziger");
        rdao.save(reiziger);
        System.out.println("save ovchipkaart");
        odao.save(ovChipkaart);
        odao.save(ovChipkaart1);

        product.getOvChipkaart().add(ovChipkaart);
        product.getOvChipkaart().add(ovChipkaart1);

        System.out.println("save product\n");
        pdao.save(product);

        System.out.println(pdao.findAll());

        System.out.println("---------------------");



        System.out.println("update");

        System.out.println("voor update");
        System.out.println(pdao.findByOVChipkaart(ovChipkaart));

        product.setNaam("Na update");
        pdao.update(product);

        System.out.println("na update");
        System.out.println(pdao.findByOVChipkaart(ovChipkaart));

        System.out.println("---------------------");


        System.out.println("delete");
        pdao.delete(product);
        System.out.println(pdao.findByOVChipkaart(ovChipkaart));
        System.out.println(pdao.findAll());
        System.out.println("---------------------");
    }
}
