package org.projet;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Set;


public class OperacjeDoSerwera {

    boolean checkUserExist(String nickname) {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Sprawdzenie_czy_jest_user");
        zapytanie.put("nickname", nickname);
        JSONObject odp = operacja(zapytanie);
        return odp.getBoolean("czy_jest_user_wynik");
    }

    JSONObject getInfoAboutUser(String nickname) {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Pobranie_informacji_o_userze");
        zapytanie.put("nickname", nickname);
        return operacja(zapytanie);
    }

    JSONObject addUser(String nickname, int ilosc_dni) {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Dodanie_usera");
        zapytanie.put("nickname", nickname);
        zapytanie.put("ilosc_dni", ilosc_dni);
        return operacja(zapytanie);
    }

    void setDniWolne(int id, Set<LocalDate> selectedDays) {
        JSONObject zapytanie = new JSONObject();
        JSONArray selectedDaysJson = new JSONArray();
        for (LocalDate day : selectedDays) {
            selectedDaysJson.put(day.toString());
        }
        zapytanie.put("operacja", "Ustawienie_dni_wolnych");
        zapytanie.put("id_uzytkownika", id);
        zapytanie.put("dni_wolne", selectedDaysJson);
        operacja(zapytanie);
    }
    void modifyIloscDni(int id_uzytkownika, int ilosc_zadeklarowanych_dni)
    {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Modyfikacja_ilosci_dni");
        zapytanie.put("id_uzytkownika", id_uzytkownika);
        zapytanie.put("ilosc_zadeklarowanych_dni", ilosc_zadeklarowanych_dni);
        operacja(zapytanie);
    }
    JSONObject operacja(JSONObject wybrana_oberacja) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bw.write(wybrana_oberacja.toString());
            bw.newLine();
            bw.flush();

            String linia = br.readLine();

            return new JSONObject(linia);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Nie można połączyć się z serwerem", "Błąd", JOptionPane.ERROR_MESSAGE);
              System.exit(1);
        }
        return null;
    }
}