package org.pakiet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class KalendarzDatabase {

    public Connection connection;

    public KalendarzDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
//        connection = DriverManager.getConnection("jdbc:postgresql://sxterm.mat.umk.pl:5432/BAZA",
//                "", "");
        connection = DriverManager.getConnection("jdbc:postgresql://db.kwkwxhowsldcunyaeung.supabase.co:5432/postgres?user=postgres&password=nHZ5pS7HwVbi6");

    }

    public boolean checkUserExist(String username) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT COUNT(*) FROM uzytkownicy WHERE nick = '" + username + "';";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            if (stmt.getResultSet().getInt(1) == 0) {
                addUser(username);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void addUser(String username) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO uzytkownicy (nick) VALUES ('" + username + "');";
        stmt.executeUpdate(sql);
    }

    public int getID(String username) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT id FROM uzytkownicy WHERE nick = '" + username + "';";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getInt(1);
        } else {
            return -1;
        }
    }
    public void setZadelkarowaneDni(int id, int ilosc) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "UPDATE uzytkownicy SET ilosc_dni_zadeklarowanych = " + ilosc + " WHERE id = " + id + ";";
        stmt.executeUpdate(sql);
    }
    public int getZadeklarowaneDni(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT ilosc_dni_zadeklarowanych FROM uzytkownicy WHERE id = " + id + ";";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getInt(1);
        } else {
            return -1;
        }
    }
    public int getIloscPozostalychDni(int id) throws SQLException
    {
        Statement stmt = connection.createStatement();
        String sql = "SELECT ilosc_dni_pozostalych FROM uzytkownicy WHERE id = " + id + ";";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getInt(1);
        } else {
            return -1;
        }
    }

}
