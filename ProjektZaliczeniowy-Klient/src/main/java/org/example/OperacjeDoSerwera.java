package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Set;


public class OperacjeDoSerwera {

    //    public static void main(String[] args) {
//        try {
//            Socket socket = new Socket("127.0.0.1", 8080);
//
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//
//            JSONObject zapytanie = new JSONObject();
//
//
//            zapytanie.put("operacja", "Sprawdzenie_czy_jest_user");
//            zapytanie.put("nickname", "test");
//
//            bw.write(zapytanie.toString());
//            bw.newLine();
//            bw.flush();
//
//            String linia = br.readLine();
//            JSONObject odp = new JSONObject(linia);
//
//
//            if (odp.getBoolean("czy_jest_user_wynik")) {
//                System.out.println("jest user");
//            } else {
//                System.out.println("nie ma usera");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
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
        JSONObject odp = operacja(zapytanie);
        return odp;
    }

    JSONObject addUser(String nickname, int ilosc_dni) {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Dodanie_usera");
        zapytanie.put("nickname", nickname);
        zapytanie.put("ilosc_dni", ilosc_dni);

        JSONObject odp = operacja(zapytanie);
        return odp;
    }

    void setDniWolne(int id, Set<LocalDate> selectedDays) {
        JSONObject zapytanie = new JSONObject();

        //convert selectedDays to JSONArray in pattern yyyy-MM-dd
        JSONArray selectedDaysJson = new JSONArray();
        for (LocalDate day : selectedDays) {
            selectedDaysJson.put(day.toString());
        }
        zapytanie.put("operacja", "Ustawienie_dni_wolnych");
        zapytanie.put("id_uzytkownika", id);
        zapytanie.put("dni_wolne", selectedDaysJson);
        operacja(zapytanie);
    }
    void modifyIloscDni(int id_uzytkownika, int ilosc_zadeklarowanych_dni, int ilosc_pozostalych_dni)
    {
        JSONObject zapytanie = new JSONObject();
        zapytanie.put("operacja", "Modyfikacja_ilosci_dni");
        zapytanie.put("id_uzytkownika", id_uzytkownika);
        zapytanie.put("ilosc_zadeklarowanych_dni", ilosc_zadeklarowanych_dni);
        zapytanie.put("ilosc_pozostalych_dni", ilosc_pozostalych_dni);
        operacja(zapytanie);
    }
    JSONObject operacja(JSONObject wybrana_oberacja) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//            zapytanie.put("operacja", "Sprawdzenie_czy_jest_user");
//            zapytanie.put("nickname", "test");

            bw.write(wybrana_oberacja.toString());
            bw.newLine();
            bw.flush();

            String linia = br.readLine();
            JSONObject odp = new JSONObject(linia);

            return odp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}