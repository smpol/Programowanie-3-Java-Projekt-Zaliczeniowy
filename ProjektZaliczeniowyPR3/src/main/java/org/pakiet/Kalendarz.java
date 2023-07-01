package org.pakiet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.SQLException;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Kalendarz {


    private int ilosc_zadeklarowanych_dni = 0;
    private int pierwsza_data_miesiac = 0;
    private int pierwsza_data_rok = 0;

    private int id_uzytkownika = -1;
    KalendarzDatabase db = new KalendarzDatabase();
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
        JOptionPane.showMessageDialog(frame, "Wybrane dni wolne zostały potwierdzone!");
        db.setDniWolne(id_uzytkownika, selectedDays);
        db.modifyIloscDni(id_uzytkownika, ilosc_zadeklarowanych_dni, ilosc_pozostalych_dni);
    }

    private void showNicknameInputDialog() {
        nickname = JOptionPane.showInputDialog(frame, "Podaj swój nick:");
        if (nickname == null || nickname.isEmpty()) {
            System.exit(0);
        }

        try {
            if (db.checkUserExist(nickname)) {
                id_uzytkownika = db.getID(nickname);
                selectedDays = db.getDniWolne(id_uzytkownika);
                copySelectedDaysToSelectedDaysModel();
                ilosc_zadeklarowanych_dni = db.getZadeklarowaneDni(id_uzytkownika);
                ilosc_pozostalych_dni = db.getIloscPozostalychDni(id_uzytkownika);
                String temp = db.getTimeStamptz(id_uzytkownika);
                pierwsza_data_miesiac = Integer.parseInt(temp.substring(5, 7));
                pierwsza_data_rok = Integer.parseInt(temp.substring(0, 4));

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
        } catch (SQLException | HeadlessException e) {
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
            db.addUser(nickname);
            id_uzytkownika = db.getID(nickname);
            ilosc_pozostalych_dni = daysToChoose;
            ilosc_zadeklarowanych_dni = daysToChoose;
            db.setZadelkarowaneDni(id_uzytkownika, ilosc_zadeklarowanych_dni);
            db.setIloscPozostalychDni(id_uzytkownika, ilosc_pozostalych_dni);
            updateDaysRemainingLabel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Błędna liczba dni. Program zostanie zamknięty.");
            System.exit(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

                System.out.println("ilosc_zadeklarowanych_dni: " + ilosc_zadeklarowanych_dni);
                System.out.println("ilosc_pozostalych_dni: " + ilosc_pozostalych_dni);


                db.modifyIloscDni(id_uzytkownika, ilosc_zadeklarowanych_dni, ilosc_pozostalych_dni);

                updateDaysRemainingLabel();
            }


        }
    }
}
