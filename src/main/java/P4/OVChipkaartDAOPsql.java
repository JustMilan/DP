package P4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private final Connection conn;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ov_chipkaart WHERE reiziger_id = ?");
            preparedStatement.setInt(1, reiziger.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<OVChipkaart> ovChipkaarten = new ArrayList<>();

            int kaartnummer;
            Date geldigTot;
            int klasse;
            float saldo;
            int reizigerId;

            while (resultSet.next()) {
                kaartnummer = resultSet.getInt("kaart_nummer");
                geldigTot = resultSet.getDate("geldig_tot");
                klasse = resultSet.getInt("klasse");
                saldo = resultSet.getFloat("saldo");
                reizigerId = resultSet.getInt("reiziger_id");
                ovChipkaarten.add(new OVChipkaart(kaartnummer, geldigTot, klasse, saldo, reizigerId));
            }

            return ovChipkaarten;

        } catch (SQLException sqlException) {
            System.out.println("Niet gevonden");
            return null;
        }
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO ov_chipkaart values (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, ovChipkaart.getKaartNummer());
            preparedStatement.setDate(2, ovChipkaart.getGeldigTot());
            preparedStatement.setInt(3, ovChipkaart.getKlasse());
            preparedStatement.setFloat(4, ovChipkaart.getSaldo());
            preparedStatement.setInt(5, ovChipkaart.getReizigerId());

            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Save went wrong");
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE ov_chipkaart SET kaart_nummer = ?, geldig_tot = ?, klasse = ?, saldo = ? WHERE reiziger_id = ?");
            preparedStatement.setInt(1, ovChipkaart.getKaartNummer());
            preparedStatement.setDate(2, ovChipkaart.getGeldigTot());
            preparedStatement.setInt(3, ovChipkaart.getKlasse());
            preparedStatement.setFloat(4, ovChipkaart.getSaldo());
            preparedStatement.setInt(5, ovChipkaart.getReizigerId());

            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Update kon niet voltooid worden door een onbekende reden");
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM ov_chipkaart WHERE kaart_nummer = ?");
            preparedStatement.setInt(1, ovChipkaart.getKaartNummer());
            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Delete is fout gegaan door een onbekende reden");
            return false;
        }
    }
}
