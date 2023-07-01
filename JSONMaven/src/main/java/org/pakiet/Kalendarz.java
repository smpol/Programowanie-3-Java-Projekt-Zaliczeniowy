package org.pakiet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.SQLException;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Kalendarz {

    private boolean nowy_uzytkownik = false;
    private int ilosc_zadeklarowanych_dni = 0;
    private int ilosc_uzytych_dni = 0;

    private int id_uzytkownika = -1;
    KalendarzDatabase db = new KalendarzDatabase();
    private JFrame frame;
    private JButton previousMonthButton;
    private JButton nextMonthButton;
    private JPanel calendarPanel;
    private JList<String> selectedDaysList;
    private DefaultListModel<String> selectedDaysModel;
    private JLabel daysRemainingLabel;
    private JLabel monthYearLabel;
    private JButton confirmButton;

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
        frame.setBounds(100, 100, 800, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        previousMonthButton = new JButton("<");
        previousMonthButton.setBounds(10, 10, 50, 20);
        frame.getContentPane().add(previousMonthButton);

        nextMonthButton = new JButton(">");
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

        confirmButton = new JButton("Potwierdź wybór dni wolnych");
        confirmButton.setBounds(580, 360, 200, 30);
        frame.getContentPane().add(confirmButton);

        previousMonthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectPreviousMonth();
                generateCalendar();
            }
        });

        nextMonthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectNextMonth();
                generateCalendar();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmSelectedDays();
            }
        });

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

    private void selectPreviousMonth() {
        selectedMonth--;
        if (selectedMonth < 1) {
            selectedMonth = 12;
            selectedYear--;
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
            dayButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toggleDayOff(dayButton);
                }
            });

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

    private void confirmSelectedDays() {
        JOptionPane.showMessageDialog(frame, "Wybrane dni wolne zostały potwierdzone!");
    }

    private void showNicknameInputDialog() throws SQLException {
        nickname = JOptionPane.showInputDialog(frame, "Podaj swój nick:");
        if (nickname == null || nickname.isEmpty()) {
            System.exit(0);
        }

        try {
            if (db.checkUserExist(nickname)) {
                id_uzytkownika = db.getID(nickname);
                ilosc_zadeklarowanych_dni = db.getZadeklarowaneDni(id_uzytkownika);
                ilosc_pozostalych_dni = db.getIloscPozostalychDni(id_uzytkownika);
                JOptionPane.showMessageDialog(frame, "Witaj ponownie " + nickname + "!");
                updateDaysRemainingLabel();

            } else {
                //db.addUser(nickname);
                id_uzytkownika = db.getID(nickname);

                JOptionPane.showMessageDialog(frame, "Witaj " + nickname + "!");
                showDaysRemainingInputDialog();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

            ilosc_pozostalych_dni = daysToChoose;
            ilosc_zadeklarowanych_dni = daysToChoose;
            db.setZadelkarowaneDni(id_uzytkownika, ilosc_zadeklarowanych_dni);
            updateDaysRemainingLabel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Błędna liczba dni. Program zostanie zamknięty.");
            System.exit(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
