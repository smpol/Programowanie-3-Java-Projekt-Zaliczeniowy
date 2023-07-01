package org.pakiet;

import javax.swing.*;
import java.sql.SQLException;

public class KalendarzRun {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Kalendarz kalendarz = new Kalendarz();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
