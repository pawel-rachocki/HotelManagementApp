package org.example.view.dialog;

import org.example.controller.MainController;
import javax.swing.*;
import java.awt.*;

public class NowaRezerwacjaDialog extends JDialog {

    private MainController controller;

    public NowaRezerwacjaDialog(JFrame parent, MainController controller) {
        super(parent, "Nowa Rezerwacja", true);
        this.controller = controller;

        // przejdź do wyszukiwania gościa od razu
        SwingUtilities.invokeLater(() -> {
            dispose();
            WyszukajGosciaDialog wyszukajDialog = new WyszukajGosciaDialog(parent, controller);
            wyszukajDialog.setVisible(true);
        });
    }
}


