package P4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private final Connection conn;
    private final AdresDaoPsql adao;
    private final OVChipkaartDAOPsql odao;

    public ReizigerDAOPsql(Connection conn) throws SQLException {
        this.conn = conn;
        this.adao = new AdresDaoPsql(conn);
        this.odao = new OVChipkaartDAOPsql(conn);
    }

    @Override
    public boolean save(Reiziger reiziger) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO reiziger values (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, reiziger.getId());
            preparedStatement.setString(2, reiziger.getVoorletters());
            preparedStatement.setString(3, reiziger.getTussenvoegsel());
            preparedStatement.setString(4, reiziger.getAchternaam());
            preparedStatement.setDate(5, (Date) reiziger.getGeboortedatum());

            preparedStatement.execute();

            for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                odao.save(ovChipkaart);
            }

            adao.save(reiziger.getAdres());


            return true;
        } catch (SQLException sqlException) {
            System.out.println("Opslaan geen succes.");
            return false;
        }
    }

    @Override
    public boolean update(Reiziger reiziger) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?");
            preparedStatement.setString(1, reiziger.getVoorletters());
            preparedStatement.setString(2, reiziger.getTussenvoegsel());
            preparedStatement.setString(3, reiziger.getAchternaam());
            preparedStatement.setDate(4, (Date) reiziger.getGeboortedatum());
            preparedStatement.setInt(5, reiziger.getId());

            preparedStatement.execute();

            adao.update(reiziger.getAdres());

            for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                odao.update(ovChipkaart);
            }

            return true;

        } catch (SQLException sqlException) {
            System.out.println("Update kon niet voltooid worden door een onbekende reden");
            return false;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        try {
            adao.delete(reiziger.getAdres());

            for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                odao.delete(ovChipkaart);
            }
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM reiziger WHERE reiziger_id = ?");
            preparedStatement.setInt(1, reiziger.getId());
            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Delete is fout gegaan door een onbekende reden");
            return false;
        }
    }

    @Override
    public Reiziger findById(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger WHERE reiziger_id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            String voorletters = null;
            String tussenvoegsel = null;
            String achternaam = null;
            Date geboortedatum = null;
            Reiziger reiziger;


            while (resultSet.next()) {
                voorletters = resultSet.getString("voorletters");
                tussenvoegsel = resultSet.getString("tussenvoegsel");
                achternaam = resultSet.getString("achternaam");
                geboortedatum = resultSet.getDate("geboortedatum");
            }
            reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatum);
            return reiziger;
        } catch (SQLException sqlException) {
            System.out.println("Geen reiziger gevonden met id: " + id);
            return null;
        }
    }

    @Override
    public List<Reiziger> findByGbDatum(String datum) {
        try {
            List<Reiziger> opDatum = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger WHERE geboortedatum = ?");
            preparedStatement.setDate(1, Date.valueOf(datum));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("reiziger_id");
                int reizigerId = Integer.parseInt(id);
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                Date geboortedatum = resultSet.getDate("geboortedatum");
                Reiziger reiziger = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);
                opDatum.add(reiziger);
            }
            return opDatum;
        } catch (SQLException sqlException) {
            System.out.println("Datum is niet gevonden of onjuist, controleer de input.");
            return null;
        }
    }

    @Override
    public List<Reiziger> findAll() {
        try {
            List<Reiziger> alleReizigers = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("reiziger_id");
                int reizigerId = Integer.parseInt(id);
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                Date geboortedatum = resultSet.getDate("geboortedatum");
                Reiziger reiziger = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);
                alleReizigers.add(reiziger);
            }
            return alleReizigers;
        } catch (SQLException sqlException) {
            System.out.println("Er is een onbekende fout opgetreden in findAll()");
            return null;
        }
    }
}
