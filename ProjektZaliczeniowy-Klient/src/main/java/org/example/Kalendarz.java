package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Kalendarz {

    OperacjeDoSerwera operacjeDoSerwera = new OperacjeDoSerwera();
    private int ilosc_zadeklarowanych_dni = 0;
    private int pierwsza_data_miesiac = 0;
    private int pierwsza_data_rok = 0;

    private int id_uzytkownika = -1;
    private JFrame frame;
    private JPanel calendarPanel;
    private JList<String> selectedDaysList;
    private DefaultListModel<String> selectedDaysModel;
    private JLabel daysRemainingLabel;
    private JLabel monthYearLabel;

    private int selectedYear;
    private int selectedMonth;
    private Set<LocalDate> selectedDays;
    private int ilosc_pozostalych_dni;
    private String nickname;

    public Kalendarz() throws SQLException, ClassNotFoundException {
        initialize();
        selectedDays = new HashSet<>();
        LocalDate currentDate = LocalDate.now();
        selectedYear = currentDate.getYear();
        selectedMonth = currentDate.getMonthValue();

        showNicknameInputDialog();
        generateCalendar();
    }

    private void initialize() {
        UIManager.put("OptionPane.yesButtonText", "Tak");
        UIManager.put("OptionPane.noButtonText", "Nie");
        UIManager.put("OptionPane.cancelButtonText", "Anuluj");
        UIManager.put("OptionPane.okButtonText", "OK");

        frame = new JFrame("Kalendarz");
        frame.setBounds(100, 100, 810, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton previousMonthButton = new JButton("<");
        previousMonthButton.setBounds(10, 10, 50, 20);
        frame.getContentPane().add(previousMonthButton);

        JButton nextMonthButton = new JButton(">");
        nextMonthButton.setBounds(70, 10, 50, 20);
        frame.getContentPane().add(nextMonthButton);

        calendarPanel = new JPanel();
        calendarPanel.setBounds(10, 40, 560, 310);
        calendarPanel.setLayout(new GridLayout(0, 7));
        frame.getContentPane().add(calendarPanel);

        selectedDaysModel = new DefaultListModel<>();
        selectedDaysList = new JList<>(selectedDaysModel);
        JScrollPane selectedDaysScrollPane = new JScrollPane(selectedDaysList);
        selectedDaysScrollPane.setBounds(580, 40, 200, 310);
        frame.getContentPane().add(selectedDaysScrollPane);

        daysRemainingLabel = new JLabel("");
        daysRemainingLabel.setBounds(580, 10, 200, 14);
        frame.getContentPane().add(daysRemainingLabel);

        monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setBounds(230, 10, 200, 20);
        frame.getContentPane().add(monthYearLabel);

        JButton confirmButton = new JButton("Potwierdź wybór dni wolnych");
        confirmButton.setBounds(580, 360, 200, 30);
        frame.getContentPane().add(confirmButton);

        JButton modifyButton = new JButton("Modyfikuj ilość dni");
        modifyButton.setBounds(580, 400, 200, 30);
        frame.getContentPane().add(modifyButton);


        previousMonthButton.addActionListener(e -> {
            //selectPreviousMonth();
            selectPreviousMonth(selectedMonth, selectedYear);
            generateCalendar();
        });

        nextMonthButton.addActionListener(e -> {
            selectNextMonth();
            generateCalendar();
        });

        confirmButton.addActionListener(e -> {
            try {
                confirmSelectedDays();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        modifyButton.addActionListener(e -> modifyIloscDni());

        selectedDaysList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = selectedDaysList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        removeSelectedDay(selectedIndex);
                    }
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                int result = JOptionPane.showConfirmDialog(frame,
                        "Czy na pewno chcesz wyjść z programu? Niezapisany postęp zostanie utracony", "Wyjście z programu",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                else if (result == JOptionPane.NO_OPTION)
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });

        frame.setVisible(true);
    }

    private void selectPreviousMonth(int poprzedni_miesiac, int poprzedni_rok) {
        if (pierwsza_data_miesiac == poprzedni_miesiac && pierwsza_data_rok == poprzedni_rok) {
            //make alert
            JOptionPane.showMessageDialog(frame, "Nie możesz cofnąć się w czasie. Wtedy nie miales konta i nie deklarowales dni wolnych.");
        } else {
            selectedMonth--;
            if (selectedMonth < 1) {
                selectedMonth = 12;
                selectedYear--;
            }
        }
    }

    private void selectNextMonth() {
        selectedMonth++;
        if (selectedMonth > 12) {
            selectedMonth = 1;
            selectedYear++;
        }
    }

    private void generateCalendar() {
        calendarPanel.removeAll();

        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth - 1, 1);

        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] daysOfWeek = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Nd"};

        for (String day : daysOfWeek) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            calendarPanel.add(label);
        }

        for (int i = 1; i < startDayOfWeek; i++) {
            JLabel emptyLabel = new JLabel("");
            calendarPanel.add(emptyLabel);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.addActionListener(e -> toggleDayOff(dayButton));

            LocalDate date = LocalDate.of(selectedYear, selectedMonth, day);
            if (selectedDays.contains(date) && date.getMonthValue() == selectedMonth) {
                dayButton.setBackground(Color.RED);
            }

            calendarPanel.add(dayButton);
        }

        updateDaysRemainingLabel();
        updateMonthYearLabel();

        frame.revalidate();
        frame.repaint();
    }

    private void toggleDayOff(JButton dayButton) {
        int day = Integer.parseInt(dayButton.getText());

        LocalDate date = LocalDate.of(selectedYear, selectedMonth, day);
        if (selectedDays.contains(date)) {
            selectedDays.remove(date);
            dayButton.setBackground(null);
            selectedDaysModel.removeElement(formatDate(date));
            ilosc_pozostalych_dni++;
        } else {
            if (ilosc_pozostalych_dni <= 0) {
                return;
            }

            selectedDays.add(date);
            dayButton.setBackground(Color.RED);
            selectedDaysModel.addElement(formatDate(date));
            ilosc_pozostalych_dni--;
        }

        updateDaysRemainingLabel();
    }

    private void updateDaysRemainingLabel() {
        daysRemainingLabel.setText("Pozostało do wybrania: " + ilosc_pozostalych_dni);
    }

    private void updateMonthYearLabel() {
        String monthYear = YearMonth.of(selectedYear, selectedMonth).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        monthYearLabel.setText(monthYear);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private void removeSelectedDay(int selectedIndex) {
        String selectedDay = selectedDaysModel.getElementAt(selectedIndex);
        selectedDaysModel.removeElementAt(selectedIndex);

        LocalDate date = LocalDate.parse(selectedDay, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        selectedDays.remove(date);
        ilosc_pozostalych_dni++;

        updateDaysRemainingLabel();
        generateCalendar();
    }

    private void confirmSelectedDays() throws SQLException {

        operacjeDoSerwera.setDniWolne(id_uzytkownika, selectedDays);
        JOptionPane.showMessageDialog(frame, "Wybrane dni wolne zostały potwierdzone!");
        operacjeDoSerwera.modifyIloscDni(id_uzytkownika, ilosc_zadeklarowanych_dni, ilosc_pozostalych_dni);
    }

    private void showNicknameInputDialog() {
        nickname = JOptionPane.showInputDialog(frame, "Podaj swój nick:");
        if (nickname == null || nickname.isEmpty()) {
            System.exit(0);
        }

        try {
            if (operacjeDoSerwera.checkUserExist(nickname)) {

                JSONObject wynik = operacjeDoSerwera.getInfoAboutUser(nickname);
                id_uzytkownika = wynik.getInt("id_uzytkownika");

                ilosc_zadeklarowanych_dni = wynik.getInt("ilosc_zadeklarowanych_dni");
                ilosc_pozostalych_dni = wynik.getInt("ilosc_pozostalych_dni");
                String temp = wynik.getString("TimeStamptz");
                pierwsza_data_miesiac = Integer.parseInt(temp.substring(5, 7));
                pierwsza_data_rok = Integer.parseInt(temp.substring(0, 4));

                JSONArray dniWolne = wynik.getJSONArray("dniWolne");
                for (int i = 0; i < dniWolne.length(); i++) {
                    selectedDays.add(LocalDate.parse(dniWolne.getString(i), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }

                copySelectedDaysToSelectedDaysModel();

                JOptionPane.showMessageDialog(frame, "Witaj ponownie " + nickname + "!");
                copySelectedDaysToSelectedDaysModel();
                updateDaysRemainingLabel();

            } else {

                LocalDate temp = LocalDate.now();
                pierwsza_data_miesiac = temp.getMonthValue();
                pierwsza_data_rok = temp.getYear();


                JOptionPane.showMessageDialog(frame, "Witaj " + nickname + "!");
                showDaysRemainingInputDialog();
            }
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }

        //showDaysRemainingInputDialog();
    }

    private void showDaysRemainingInputDialog() {
        String input = JOptionPane.showInputDialog(frame, "Wpisz ile dni wolnych chcesz wybrać:");
        if (input == null || input.isEmpty()) {
            System.exit(0);
        }

        try {
            int daysToChoose = Integer.parseInt(input);
            JSONObject wynik = operacjeDoSerwera.addUser(nickname, daysToChoose);
            id_uzytkownika = wynik.getInt("id_uzytkownika");
            ilosc_pozostalych_dni = daysToChoose;
            ilosc_zadeklarowanych_dni = daysToChoose;
            updateDaysRemainingLabel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Błędna liczba dni. Program zostanie zamknięty.");
            System.exit(0);
        }
    }

    private void copySelectedDaysToSelectedDaysModel() {
        selectedDaysModel.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (LocalDate selectedDay : selectedDays) {
            selectedDaysModel.addElement(selectedDay.format(formatter));
        }
    }

    private void modifyIloscDni() {
        //popup z pytaniem o ilosc dni
        String input = JOptionPane.showInputDialog(frame, "Wpisz ile dni wolnych chcesz wybrać:");
        if (input == null || input.isEmpty()) {
            System.exit(0);
        }
        //check czy liczba
        if (input.matches("[0-9]+")) {
            int nowa_ilosc_deklar = Integer.parseInt(input);
            if (nowa_ilosc_deklar < 0) {
                JOptionPane.showMessageDialog(frame, "Błędna liczba dni");
                //System.exit(0);
            } else if (nowa_ilosc_deklar < ilosc_zadeklarowanych_dni - ilosc_pozostalych_dni) {
                JOptionPane.showMessageDialog(frame, "Nie możesz wybrać mniej dni niż już wybrałeś. Usun wybrane dni.");
            } else {
                ilosc_pozostalych_dni = nowa_ilosc_deklar - (ilosc_zadeklarowanych_dni - ilosc_pozostalych_dni);
                ilosc_zadeklarowanych_dni = nowa_ilosc_deklar;
                operacjeDoSerwera.modifyIloscDni(id_uzytkownika, ilosc_zadeklarowanych_dni, ilosc_pozostalych_dni);

                updateDaysRemainingLabel();
            }


        }
    }
}
