package P5;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private final Connection conn;
    private OVChipkaartDAOPsql odao = null;

    public ProductDAOPsql(Connection conn) throws SQLException {
        this.conn = conn;
        this.odao = new OVChipkaartDAOPsql(conn);
    }

    @Override
    public boolean save(Product product) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO product VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, product.getProductNummer());
            preparedStatement.setString(2, product.getNaam());
            preparedStatement.setString(3, product.getBeschrijving());
            preparedStatement.setFloat(4, product.getPrijs());
            preparedStatement.execute();

            PreparedStatement p1 = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?, ?, ?)");
            System.out.println(product.getOvChipkaart());
            for (OVChipkaart ovChipkaart : product.getOvChipkaart()) {
                p1.setInt(1, ovChipkaart.getKaartNummer());
                p1.setInt(2, product.getProductNummer());
                p1.setString(3, "gekocht");
                p1.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                p1.execute();
            }


            return true;

        } catch (SQLException sqlException) {
            System.out.println("Couldn't save \n" + sqlException.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE product SET product_nummer = ?, naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?");
            preparedStatement.setInt(1, product.getProductNummer());
            preparedStatement.setString(2, product.getNaam());
            preparedStatement.setString(3, product.getBeschrijving());
            preparedStatement.setFloat(4, product.getPrijs());
            preparedStatement.setInt(5, product.getProductNummer());
            preparedStatement.execute();

            PreparedStatement p1 = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE product_nummer = ?");
            p1.setInt(1, product.getProductNummer());
            p1.execute();


            PreparedStatement p2 = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?, ?, ?)");
            for (OVChipkaart ovChipkaart : product.getOvChipkaart()) {
                p2.setInt(1, ovChipkaart.getKaartNummer());
                p2.setInt(2, product.getProductNummer());
                p2.setString(3, "gekocht");
                p2.setDate(4, Date.valueOf(LocalDate.now()));
                p2.execute();
            }
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Couldn't update \n" + sqlException.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        try {
            PreparedStatement p1 = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE product_nummer = ?");
            p1.setInt(1, product.getProductNummer());
            p1.execute();

            PreparedStatement p2 = conn.prepareStatement("DELETE FROM product WHERE product_nummer = ?");
            p2.setInt(1, product.getProductNummer());
            return p2.execute();

        } catch (SQLException sqlException) {
            System.out.println("Couldn't delete\n" + sqlException.getMessage()
            );
            return false;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT p.product_nummer, naam, beschrijving, prijs FROM ov_chipkaart_product ocp JOIN product p on ocp.product_nummer = p.product_nummer WHERE ocp.kaart_nummer = ?");
            preparedStatement.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Product> alleProducten = new ArrayList<>();

            while (resultSet.next()) {
                int nummer = resultSet.getInt("product_nummer");
                String naam = resultSet.getString("naam");
                String beschrijving = resultSet.getString("beschrijving");
                float prijs = resultSet.getFloat("prijs");
                Product product = new Product(nummer, naam, beschrijving, prijs);
                alleProducten.add(product);
            }

            return alleProducten;
        } catch (SQLException sqlException) {
            System.out.println("Couldn't find product with ovkaart" + ovChipkaart.toString());
            return null;
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            List<Product> alleProducten = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM product;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productNummer = resultSet.getInt("product_nummer");
                String naam = resultSet.getString("naam");
                String beschrijving = resultSet.getString("beschrijving");
                float prijs = resultSet.getFloat("prijs");
                Product product = new Product(productNummer, naam, beschrijving, prijs);
                alleProducten.add(product);
            }
            return alleProducten;
        } catch (SQLException sqlException) {
            System.out.println("Er is een onbekende fout opgetreden in findAll()");
            return null;
        }
    }
}
