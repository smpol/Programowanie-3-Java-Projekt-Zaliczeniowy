package org.pakiet;

import javax.swing.*;
import java.sql.SQLException;

public class KalendarzRun {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Kalendarz();
            } catch (SQLException | ClassNotFoundException e) {
                // Wyświetl okno dialogowe z błędem
                JOptionPane.showMessageDialog(null, "Nie można połączyć się z bazą danych. Błąd: " + e.getMessage(), "Błąd połączenia z bazą danych", JOptionPane.ERROR_MESSAGE);
                // Zakończ program
                System.exit(1);
            }
        });
    }
}