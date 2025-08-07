package org.example;


import org.example.view.MainFrame;
import org.example.utlis.HibernateUtil;

import javax.swing.*;

public class HotelManagementApp {

    public static void main(String[] args) {
        // Ustawienie Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nie można ustawić Look and Feel: " + e.getMessage());
        }

        // Uruchomienie GUI
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Błąd podczas uruchamiania aplikacji: " + e.getMessage(),
                        "Błąd krytyczny", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });

        // Shutdown dla prawidłowego zamknięcia Hibernate
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            HibernateUtil.shutdown();
        }));
    }
}

