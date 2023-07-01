package org.pakiet;

import javax.swing.*;
import java.sql.SQLException;

public class KalendarzRun {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Kalendarz();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
