package org.projet;

import javax.swing.*;

public class KalendarzRun {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Kalendarz();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
}