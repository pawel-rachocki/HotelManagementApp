package org.example.view.dialog;

import org.example.controller.MainController;
import org.example.models.Gosc;
import org.example.service.RezerwacjaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class WyszukajGosciaDialog extends JDialog {

    private MainController controller;
    private RezerwacjaService rezerwacjaService;
    private JTextField searchField;
    private JButton szukajButton;
    private JButton anulujButton;
    private JButton nowyGoscButton;
    private Gosc znalezionyGosc;

    public WyszukajGosciaDialog(JFrame parent, MainController controller) {
        super(parent, "Wyszukaj Gościa", true);
        this.controller = controller;
        this.rezerwacjaService = new RezerwacjaService();
        initializeGUI();
    }

    private void initializeGUI() {
        setSize(450, 250);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel główny
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Tytuł
        JLabel titleLabel = new JLabel("Wyszukaj gościa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Label
        JLabel instructionLabel = new JLabel("Wprowadź email lub nazwisko gościa:");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(instructionLabel, gbc);

        // Pole tekstowe
        searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(searchField, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());

        szukajButton = new JButton("Szukaj");
        szukajButton.setPreferredSize(new Dimension(100, 30));
        szukajButton.addActionListener(this::szukajGoscia);

        nowyGoscButton = new JButton("Zarejestruj nowego gościa");
        nowyGoscButton.setPreferredSize(new Dimension(180, 30));
        nowyGoscButton.addActionListener(this::zarejestrujNowegoGoscia);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 30));
        anulujButton.addActionListener(e -> dispose());

        buttonPanel.add(szukajButton);
        buttonPanel.add(nowyGoscButton);
        buttonPanel.add(anulujButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key support
        getRootPane().setDefaultButton(szukajButton);

        // Focus na pole tekstowe
        SwingUtilities.invokeLater(() -> searchField.requestFocus());
    }

    private void szukajGoscia(ActionEvent e) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Proszę wprowadzić email lub nazwisko gościa",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 4 : System weryfikuje, czy podane dane znajdują się w bazie danych
            Optional<Gosc> goscOpt = rezerwacjaService.wyszukajGoscia(searchText);

            if (goscOpt.isPresent()) {
                znalezionyGosc = goscOpt.get();

                // Wyświetl potwierdzenie znalezienia gościa
                int result = JOptionPane.showConfirmDialog(this,
                        String.format("Znaleziono gościa:\n\n" +
                                        "Imię i nazwisko: %s\n" +
                                        "Email: %s\n" +
                                        "Telefon: %s\n\n" +
                                        "Czy chcesz kontynuować z tym gościem?",
                                znalezionyGosc.getPelneImieNazwisko(),
                                znalezionyGosc.getEmail(),
                                znalezionyGosc.getTelefon()),
                        "Gość znaleziony",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    // Przejdź do następnego kroku - formularz nowej rezerwacji
                    otworzFormularzRezerwacji();
                }
            } else {
                // 3A: Gość nie znaleziony w bazie danych
                pokazBladGoscNieZnaleziony();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas wyszukiwania gościa: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pokazBladGoscNieZnaleziony() {
        // Zgodnie z mockupem "Wyszukaj gościa - błąd"
        int result = JOptionPane.showConfirmDialog(this,
                "Gość nie znaleziony w bazie danych.\n\n" +
                        "Czy chcesz zarejestrować nowego gościa?",
                "Gość nie znaleziony",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            zarejestrujNowegoGoscia(null);
        }
    }

    private void zarejestrujNowegoGoscia(ActionEvent e) {
        // 3A : System wywołuje p.u. "Zarejestruj nowego gościa"
        dispose();

        ZarejestrujGosciaDialog rejestrujDialog = new ZarejestrujGosciaDialog((JFrame) getParent(), controller);
        rejestrujDialog.setVisible(true);
    }


    private void otworzFormularzRezerwacji() {
        dispose();

        // 5 : System wyświetla formularz nowej rezerwacji z już wypełnionymi danymi gościa
        FormularzRezerwacjiDialog formularzDialog = new FormularzRezerwacjiDialog(
                (JFrame) getParent(), controller, znalezionyGosc);
        formularzDialog.setVisible(true);
    }


    // Getter dla znalezionego gościa
    public Gosc getZnalezionyGosc() {
        return znalezionyGosc;
    }
}
