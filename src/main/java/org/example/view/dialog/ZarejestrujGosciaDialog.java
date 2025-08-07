package org.example.view.dialog;


import org.example.controller.MainController;
import org.example.models.Adres;
import org.example.models.Gosc;
import org.example.service.GoscService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ZarejestrujGosciaDialog extends JDialog {

    private MainController controller;
    private GoscService goscService;

    // Pola formularza
    private JTextField imieField;
    private JTextField nazwiskoField;
    private JTextField telefonField;
    private JTextField emailField;
    private JTextField dataUrodzeniaField;

    // Pola adresu
    private JTextField ulicaField;
    private JTextField nrDomuField;
    private JTextField nrMieszkaniaField;
    private JTextField kodPocztowyField;
    private JTextField miastoField;

    private JButton zapiszButton;
    private JButton anulujButton;

    private Gosc zarejestrowangosc;

    public ZarejestrujGosciaDialog(JFrame parent, MainController controller) {
        super(parent, "Zarejestruj Nowego Gościa", true);
        this.controller = controller;
        this.goscService = new GoscService();
        initializeGUI();
    }

    private void initializeGUI() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel główny z formularzem
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Tytuł
        JLabel titleLabel = new JLabel("Rejestracja Nowego Gościa");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Sekcja danych osobowych
        addSectionLabel(mainPanel, gbc, "Dane osobowe:", 1);

        addFormField(mainPanel, gbc, "Imię:", imieField = new JTextField(20), 2);
        addFormField(mainPanel, gbc, "Nazwisko:", nazwiskoField = new JTextField(20), 3);
        addFormField(mainPanel, gbc, "Telefon:", telefonField = new JTextField(20), 4);
        addFormField(mainPanel, gbc, "Email:", emailField = new JTextField(20), 5);
        addFormField(mainPanel, gbc, "Data urodzenia (dd.MM.yyyy):",
                dataUrodzeniaField = new JTextField(20), 6);

        // Sekcja adresu
        addSectionLabel(mainPanel, gbc, "Adres zamieszkania:", 7);

        addFormField(mainPanel, gbc, "Ulica:", ulicaField = new JTextField(20), 8);
        addFormField(mainPanel, gbc, "Nr domu:", nrDomuField = new JTextField(20), 9);
        addFormField(mainPanel, gbc, "Nr mieszkania (opcjonalnie):",
                nrMieszkaniaField = new JTextField(20), 10);
        addFormField(mainPanel, gbc, "Kod pocztowy:", kodPocztowyField = new JTextField(20), 11);
        addFormField(mainPanel, gbc, "Miasto:", miastoField = new JTextField(20), 12);

        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());

        zapiszButton = new JButton("Zarejestruj Gościa");
        zapiszButton.setPreferredSize(new Dimension(150, 30));
        zapiszButton.addActionListener(this::zarejestrujGoscia);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 30));
        anulujButton.addActionListener(e -> dispose());

        buttonPanel.add(zapiszButton);
        buttonPanel.add(anulujButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(zapiszButton);

        // Focus na pierwsze pole
        SwingUtilities.invokeLater(() -> imieField.requestFocus());
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, String text, int row) {
        JLabel sectionLabel = new JLabel(text);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 5, 0);
        panel.add(sectionLabel, gbc);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText,
                              JTextField field, int row) {
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);

        // Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        // Field
        gbc.gridx = 1; gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void zarejestrujGoscia(ActionEvent e) {
        try {
            // Walidacja danych zgodnie z wymaganiami biznesowymi
            if (!walidujDane()) {
                return;
            }

            // Parsowanie daty urodzenia
            LocalDate dataUrodzenia = parsujDateUrodzenia();
            if (dataUrodzenia == null) {
                return;
            }

            // Parsowanie numeru mieszkania (opcjonalne)
            Integer nrMieszkania = null;
            String nrMieszkaniaText = nrMieszkaniaField.getText().trim();
            if (!nrMieszkaniaText.isEmpty()) {
                try {
                    nrMieszkania = Integer.parseInt(nrMieszkaniaText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Numer mieszkania musi być liczbą",
                            "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Parsowanie numeru domu
            int nrDomu;
            try {
                nrDomu = Integer.parseInt(nrDomuField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Numer domu musi być liczbą",
                        "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Utworzenie obiektu adresu
            Adres adres = new Adres(
                    ulicaField.getText().trim(),
                    nrDomu,
                    nrMieszkania,
                    kodPocztowyField.getText().trim(),
                    miastoField.getText().trim()
            );

            // Rejestracja gościa przez serwis
            zarejestrowangosc = goscService.zarejestrujNowegoGoscia(
                    imieField.getText().trim(),
                    nazwiskoField.getText().trim(),
                    telefonField.getText().trim(),
                    emailField.getText().trim(),
                    dataUrodzenia,
                    adres
            );

            // Potwierdzenie rejestracji
            JOptionPane.showMessageDialog(this,
                    String.format("Gość został pomyślnie zarejestrowany!\n\n" +
                                    "Imię i nazwisko: %s\n" +
                                    "Email: %s\n" +
                                    "Telefon: %s",
                            zarejestrowangosc.getPelneImieNazwisko(),
                            zarejestrowangosc.getEmail(),
                            zarejestrowangosc.getTelefon()),
                    "Rejestracja zakończona",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

            // Przejście do formularza rezerwacji z zarejestrowanym gościem
            otworzFormularzRezerwacji();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas rejestracji gościa: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean walidujDane() {
        // Walidacja wymaganych pól zgodnie z dokumentacją
        if (imieField.getText().trim().isEmpty()) {
            pokazBlad("Imię jest wymagane");
            return false;
        }

        if (nazwiskoField.getText().trim().isEmpty()) {
            pokazBlad("Nazwisko jest wymagane");
            return false;
        }

        if (telefonField.getText().trim().isEmpty()) {
            pokazBlad("Telefon jest wymagany");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            pokazBlad("Email jest wymagany");
            return false;
        }

        // Walidacja formatu email
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            pokazBlad("Email musi mieć prawidłowy format");
            return false;
        }

        if (dataUrodzeniaField.getText().trim().isEmpty()) {
            pokazBlad("Data urodzenia jest wymagana");
            return false;
        }

        // Walidacja pól adresu
        if (ulicaField.getText().trim().isEmpty()) {
            pokazBlad("Ulica jest wymagana");
            return false;
        }

        if (nrDomuField.getText().trim().isEmpty()) {
            pokazBlad("Numer domu jest wymagany");
            return false;
        }

        if (kodPocztowyField.getText().trim().isEmpty()) {
            pokazBlad("Kod pocztowy jest wymagany");
            return false;
        }

        // Walidacja formatu kodu pocztowego (XX-XXX)
        String kodPocztowy = kodPocztowyField.getText().trim();
        if (!kodPocztowy.matches("\\d{2}-\\d{3}")) {
            pokazBlad("Kod pocztowy musi mieć format XX-XXX");
            return false;
        }

        if (miastoField.getText().trim().isEmpty()) {
            pokazBlad("Miasto jest wymagane");
            return false;
        }

        return true;
    }

    // Formatowanie daty urodzenia z walidacją
    private LocalDate parsujDateUrodzenia() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate dataUrodzenia = LocalDate.parse(dataUrodzeniaField.getText().trim(), formatter);

            // Sprawdź czy data nie jest z przyszłości
            if (dataUrodzenia.isAfter(LocalDate.now())) {
                pokazBlad("Data urodzenia nie może być z przyszłości");
                return null;
            }

            return dataUrodzenia;
        } catch (DateTimeParseException ex) {
            pokazBlad("Data urodzenia musi mieć format dd.MM.yyyy (np. 15.03.1990)");
            return null;
        }
    }

    private void pokazBlad(String message) {
        JOptionPane.showMessageDialog(this, message, "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
    }

    private void otworzFormularzRezerwacji() {
        FormularzRezerwacjiDialog formularzDialog = new FormularzRezerwacjiDialog(
                (JFrame) getParent(), controller, zarejestrowangosc);
        formularzDialog.setVisible(true);
    }


    public Gosc getZarejestrowangosc() {
        return zarejestrowangosc;
    }
}

