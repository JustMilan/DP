package P1;

import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {

        String url = "jdbc:postgresql://localhost/ovchip";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "K9wVPyHJ!.");
        props.setProperty("ssl", "false");
        Connection conn = DriverManager.getConnection(url, props);

        Statement statement = conn.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM reiziger");

        System.out.println("Alle reizigers:");

        while (resultSet.next()) {
            String reizigerId = resultSet.getString("reiziger_id");
            String voorletters = resultSet.getString("voorletters");
            String tussenvoegsel = resultSet.getString("tussenvoegsel");
            String achternaam = resultSet.getString("achternaam");
            String geboortedatum = resultSet.getString("geboortedatum");

            if (tussenvoegsel == null) {
                System.out.println(String.format("#%s: %s. %s (%s)", reizigerId, voorletters, achternaam, geboortedatum));
            } else {
                System.out.println(String.format("#%s: %s. %s %s (%s)", reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum));
            }
        }
        resultSet.close();
        statement.close();
    }
}