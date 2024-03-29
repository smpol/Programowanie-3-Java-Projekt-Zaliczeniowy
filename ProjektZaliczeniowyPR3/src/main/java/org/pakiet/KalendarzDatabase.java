package org.pakiet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class KalendarzDatabase {

    public Connection connection;

    public KalendarzDatabase() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://db.scfmfiwnmemndiuolvdl.supabase.co:5432/postgres?user=postgres&password=nHZ5pS7HwVbi6");
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Nie znaleziono sterownika bazy danych: " + e.getMessage());
        } catch (SQLException e) {
            throw new SQLException("Nie można połączyć się z bazą danych: " + e.getMessage());
        }
    }

    public boolean checkUserExist(String username) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT COUNT(*) FROM uzytkownicy WHERE nick = '" + username + "';";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getInt(1) != 0;
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

    public int getIloscPozostalychDni(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT ilosc_dni_pozostalych FROM uzytkownicy WHERE id = " + id + ";";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getInt(1);
        } else {
            return -1;
        }
    }

    public void setIloscPozostalychDni(int id, int ilosc) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "UPDATE uzytkownicy SET ilosc_dni_pozostalych = " + ilosc + " WHERE id = " + id + ";";
        stmt.executeUpdate(sql);
    }

    public String getTimeStamptz(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT created_at FROM uzytkownicy WHERE id = " + id + ";";
        stmt.executeQuery(sql);
        if (stmt.getResultSet().next()) {
            return stmt.getResultSet().getString(1);
        } else {
            return "";
        }
    }

    public void modifyIloscDni(int id, int ilosc_zadeklarowanych_dni, int ilosc_dni_pozostalych) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "UPDATE uzytkownicy SET ilosc_dni_zadeklarowanych = " + ilosc_zadeklarowanych_dni + ", ilosc_dni_pozostalych = " + ilosc_dni_pozostalych + " WHERE id = " + id + ";";
            stmt.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setDniWolne(int id, Set<LocalDate> selectedDays) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "DELETE FROM daty WHERE id_uzytkownika = " + id + ";";
        stmt.executeUpdate(sql);
        for (LocalDate date : selectedDays) {
            sql = "INSERT INTO daty (id_uzytkownika, data) VALUES (" + id + ", '" + date.toString() + "');";
            stmt.executeUpdate(sql);
        }
    }

    public Set<LocalDate> getDniWolne(int id) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT data FROM daty WHERE id_uzytkownika = " + id + ";";
        stmt.executeQuery(sql);
        Set<LocalDate> dniWolne = new HashSet<>();
        while (stmt.getResultSet().next()) {
            dniWolne.add(LocalDate.parse(stmt.getResultSet().getString(1)));
        }
        return dniWolne;
    }


}
