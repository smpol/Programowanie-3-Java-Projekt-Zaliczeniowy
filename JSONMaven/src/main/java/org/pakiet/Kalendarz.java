package org.pakiet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Kalendarz {
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
    private Set<Integer> selectedDays;
    private int daysRemaining;

    public Kalendarz() {
        initialize();
        selectedDays = new HashSet<>();
        LocalDate currentDate = LocalDate.now();
        selectedYear = currentDate.getYear();
        selectedMonth = currentDate.getMonthValue();
        generateCalendar();
    }

    private void initialize() {
        frame = new JFrame("Kalendarz");
        frame.setBounds(100, 100, 850, 450);
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

        daysRemainingLabel = new JLabel("Pozostało do wybrania: 0");
        daysRemainingLabel.setBounds(580, 10, 200, 14);
        frame.getContentPane().add(daysRemainingLabel);

        monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setBounds(230, 10, 200, 20);
        frame.getContentPane().add(monthYearLabel);

        confirmButton = new JButton("Potwierdź wybór dni wolnych");
        confirmButton.setBounds(580, 360, 250, 30);
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

            if (selectedDays.contains(day)) {
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

        if (selectedDays.contains(day)) {
            selectedDays.remove(day);
            dayButton.setBackground(null);
            selectedDaysModel.removeElement(formatDate(day));
            daysRemaining++;
        } else {
            if (daysRemaining <= 0) {
                return;
            }

            selectedDays.add(day);
            dayButton.setBackground(Color.RED);
            selectedDaysModel.addElement(formatDate(day));
            daysRemaining--;
        }

        updateDaysRemainingLabel();
    }

    private void updateDaysRemainingLabel() {
        daysRemainingLabel.setText("Pozostało do wybrania: " + daysRemaining);
    }

    private void updateMonthYearLabel() {
        String monthYear = YearMonth.of(selectedYear, selectedMonth).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        monthYearLabel.setText(monthYear);
    }

    private String formatDate(int day) {
        return String.format("%02d-%02d-%d", day, selectedMonth, selectedYear);
    }

    private void confirmSelectedDays() {
        JOptionPane.showMessageDialog(frame, "Wybrane dni wolne zostały potwierdzone!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Kalendarz kalendarz = new Kalendarz();
                kalendarz.showUserInputDialog();
            }
        });
    }

    private void showUserInputDialog() {
        String nickname = JOptionPane.showInputDialog(frame, "Podaj swój nick:", "Witaj!", JOptionPane.PLAIN_MESSAGE);
        if (nickname != null) {
            showDaysOffInputDialog(nickname);
        }
    }

    private void showDaysOffInputDialog(String nickname) {
        JTextField daysOffTextField = new JTextField();
        Object[] message = {
                "Pozostało do wybrania:", daysOffTextField
        };
        int option = JOptionPane.showConfirmDialog(frame, message, "Dni wolne dla " + nickname, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                daysRemaining = Integer.parseInt(daysOffTextField.getText());
                updateDaysRemainingLabel();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Podano nieprawidłową liczbę dni.", "Błąd", JOptionPane.ERROR_MESSAGE);
                showDaysOffInputDialog(nickname);
            }
        }
    }
}
