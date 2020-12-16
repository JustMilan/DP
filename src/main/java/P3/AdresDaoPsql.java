package P3;

import P2.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresDaoPsql implements AdresDAO {
    private Connection conn = Main.getConnection();

    public AdresDaoPsql(Connection conn) throws SQLException {
        this.conn = conn;
    }

    @Override
    public boolean save(Adres adres) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO adres values (?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, adres.getId());
            preparedStatement.setString(2, adres.getPostcode());
            preparedStatement.setString(3, adres.getHuisnummer());
            preparedStatement.setString(4, adres.getStraat());
            preparedStatement.setString(5, adres.getWoonplaasts());
            preparedStatement.setInt(6, adres.getReizigerId());

            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Adres opslaan ging fout");
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ?, reiziger_id = ? WHERE adres_id = ?;");
            preparedStatement.setString(1, adres.getPostcode());
            preparedStatement.setString(2, adres.getHuisnummer());
            preparedStatement.setString(3, adres.getStraat());
            preparedStatement.setString(4, adres.getWoonplaasts());
            preparedStatement.setInt(5, adres.getReizigerId());
            preparedStatement.setInt(6, adres.getId());
            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Upaten van adres is fout gegaan");
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM adres WHERE adres_id = ?;");
            preparedStatement.setInt(1, adres.getId());
            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.out.println("Deleten is fout gegaan");
            return false;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM adres WHERE reiziger_id = ?;");
            preparedStatement.setInt(1, reiziger.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            int adresId = 0;
            String postcode = null;
            String huisnummer = null;
            String straat = null;
            String woonplaats = null;
            int reizigerId = 0;
            Adres adres;

            while (resultSet.next()) {
                adresId = resultSet.getInt("adres_id");
                postcode = resultSet.getString("postcode");
                huisnummer = resultSet.getString("huisnummer");
                straat = resultSet.getString("straat");
                woonplaats = resultSet.getString("woonplaats");
                reizigerId = resultSet.getInt("reiziger_id");
            }
            adres = new Adres(adresId, postcode, huisnummer, straat, woonplaats, reizigerId);
            return adres;
        } catch (SQLException sqlException) {
            System.out.println("findByReiziger ging fout");
            return null;
        }
    }

    @Override
    public List<Adres> findAll() {
        try {
            List<Adres> alleAdressen = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM adres;");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int adresId = resultSet.getInt("adres_id");
                String postcode = resultSet.getString("postcode");
                String huisnummer = resultSet.getString("huisnummer");
                String straat = resultSet.getString("straat");
                String woonplaats = resultSet.getString("woonplaats");
                int reizigerId = resultSet.getInt("reiziger_id");
                Adres adres = new Adres(adresId, postcode, huisnummer, straat, woonplaats, reizigerId);
                alleAdressen.add(adres);
            }
            return alleAdressen;
        } catch (SQLException sqlException) {
            System.out.println("Er is een onbekende fout opgetreden in findAll()");
            return null;
        }
    }
}
