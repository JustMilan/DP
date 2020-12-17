package P7;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ProductDAOHibernate implements ProductDAO {
    private Session session;

    public ProductDAOHibernate(Session session) {
        this.session = session;
    }

    @Override
    public boolean save(Product product) {
        try {
            Transaction transaction = this.session.beginTransaction();
            session.save(product);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        try {
            Transaction transaction = this.session.beginTransaction();
            session.update(product);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        try {
            Transaction transaction = this.session.beginTransaction();
            session.delete(product);
            transaction.commit();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        try {
            Transaction transaction = this.session.beginTransaction();
            List<Product> alleProducten = session.createQuery("FROM Product", Product.class).getResultList();
            List<Product> ovChipkaartProducten = new ArrayList<>();
            for (Product product : alleProducten) {
                if (product.getOvChipkaarten().contains(ovChipkaart)) {
                    ovChipkaartProducten.add(product);
                }
            }

            transaction.commit();
            return ovChipkaartProducten;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            Transaction transaction = this.session.beginTransaction();
            List<Product> producten = session.createQuery("FROM Product", Product.class).getResultList();
            transaction.commit();
            return producten;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
