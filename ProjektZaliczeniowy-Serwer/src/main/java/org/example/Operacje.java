package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Operacje extends Thread {

    private Socket socket;
    public BazaDanych db;

    {
        try {
            db = new BazaDanych();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Operacje(Socket socket) throws SQLException, ClassNotFoundException {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {

            System.out.println("klinet: " + socket.getInetAddress().getHostAddress());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject zap = new JSONObject(lina);
            JSONObject odp = new JSONObject();

            if (zap.getString("operacja").equals("Sprawdzenie_czy_jest_user"))
                odp = checkUserExist(zap.getString("nickname"));
            if (zap.getString("operacja").equals("Pobranie_informacji_o_userze"))
                odp = getInfoAboutUser(zap.getString("nickname"));
            if (zap.getString("operacja").equals("Dodanie_usera"))
                odp = addUser(zap.getString("nickname"), zap.getInt("ilosc_dni"));
            if (zap.getString("operacja").equals("Ustawienie_dni_wolnych")) {
                setDniWolne(zap.getInt("id_uzytkownika"), zap.getJSONArray("dni_wolne"));
            }
            if (zap.getString("operacja").equals("Policzenie_dni_na_rok"))
                odp = countDaysOnYear(zap.getInt("id_uzytkownika"), zap.getInt("wybrany_rok"));
            if (zap.getString("operacja").equals("Modyfikacja_ilosci_dni"))
                modifyIloscDni(zap.getInt("id_uzytkownika"), zap.getInt("ilosc_zadeklarowanych_dni"), zap.getInt("ilosc_pozostalych_dni"));



            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(odp.toString());
            bw.newLine();
            bw.flush();
            socket.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    JSONObject addUser(String nickname, int ilosc_dni) throws SQLException {
        JSONObject zapytanie = new JSONObject();
        db.addUser(nickname);
        int id = db.getID(nickname);
        db.setZadelkarowaneDni(id, ilosc_dni);
        db.setIloscPozostalychDni(id, ilosc_dni);
        zapytanie.put("id_uzytkownika", db.getID(nickname));
        return zapytanie;
    }
    JSONObject checkUserExist(String nickname) throws SQLException {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("czy_jest_user_wynik", db.checkUserExist(nickname));
        //System.out.println("FUNKCJA czy_jest_user_wynik: " + zapytanie.getBoolean("czy_jest_user_wynik"));
        return zapytanie;
    }

    JSONObject getInfoAboutUser(String nickname) throws SQLException {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("id_uzytkownika", db.getID(nickname));
        zapytanie.put("ilosc_zadeklarowanych_dni", db.getZadeklarowaneDni(zapytanie.getInt("id_uzytkownika")));
        zapytanie.put("ilosc_pozostalych_dni", db.getIloscPozostalychDni(zapytanie.getInt("id_uzytkownika")));
        zapytanie.put("TimeStamptz", db.getTimeStamptz(zapytanie.getInt("id_uzytkownika")));

        //zapytanie.put("getDniWolne", db.getDniWolne(zapytanie.getInt("id_uzytkownika")));
        Set<LocalDate> temp= db.getDniWolne(zapytanie.getInt("id_uzytkownika"));
        LocalDate[] dniWolne = temp.toArray(new LocalDate[temp.size()]);
        zapytanie.put("dniWolne", dniWolne);

        return zapytanie;
    }

    JSONObject countDaysOnYear(int id, int rok) throws SQLException {
        JSONObject zapytanie = new JSONObject();
        try {
            zapytanie.put("ilosc_dni_w_wybranym_roku", db.countDaysOnYear(id, rok));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return zapytanie;
    }
    void setDniWolne(int id_uzytkownika, JSONArray dniWolne) throws SQLException {
        //convert JSONArray to Set<LocalDate> in pattern yyyy-MM-dd
        Set<LocalDate> temp = new HashSet<>();
        for (int i = 0; i < dniWolne.length(); i++) {
            temp.add(LocalDate.parse(dniWolne.getString(i)));
        }
        db.setDniWolne(id_uzytkownika, temp);
    }

    void modifyIloscDni(int id_uzytkownika, int ilosc_zadeklarowanych_dni, int ilosc_pozostalych_dni)
    {
        db.modifyIloscDni(id_uzytkownika, ilosc_zadeklarowanych_dni, ilosc_pozostalych_dni);
    }
}
