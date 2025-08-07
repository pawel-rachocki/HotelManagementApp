package org.example.view.dialog;


import org.example.controller.MainController;

import org.example.models.*;
import org.example.models.enums.StatusRezerwacji;
import org.example.service.RezerwacjaService;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SzczegolyRezerwacjiDialog extends JDialog {

    private MainController controller;
    private RezerwacjaService rezerwacjaService;
    private Rezerwacja rezerwacja;

    public SzczegolyRezerwacjiDialog(JFrame parent, MainController controller, Rezerwacja rezerwacja) {
        super(parent, "Szczegóły Rezerwacji", true);
        this.controller = controller;
        this.rezerwacja = rezerwacja;
        this.rezerwacjaService = new RezerwacjaService();
        initializeGUI();
        loadSzczegoly();
    }

    private void initializeGUI() {
        setSize(700, 800);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel główny ze szczegółami
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tytuł z numerem rezerwacji
        JLabel titleLabel = new JLabel("Szczegóły Rezerwacji: " + rezerwacja.getNumerRezerwacji());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel ze wszystkimi sekcjami
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Status rezerwacji z kolorowym tłem
        JPanel statusPanel = createStatusPanel();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(statusPanel, gbc);

        // Sekcja danych gościa
        JPanel goscPanel = createGoscPanel();
        gbc.gridy = 1;
        contentPanel.add(goscPanel, gbc);

        // Sekcja danych pokoju
        JPanel pokojPanel = createPokojPanel();
        gbc.gridy = 2;
        contentPanel.add(pokojPanel, gbc);

        // Sekcja danych rezerwacji
        JPanel rezerwacjaPanel = createRezerwacjaPanel();
        gbc.gridy = 3;
        contentPanel.add(rezerwacjaPanel, gbc);


        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Kolorowanie tła według statusu rezerwacji
        Color backgroundColor;
        switch (rezerwacja.getStatusRezerwacji()) {
            case UTWORZONA -> backgroundColor = new Color(173, 216, 230); // Light Blue
            case POTWIERDZONA -> backgroundColor = new Color(144, 238, 144); // Light Green
            case W_TRAKCIE -> backgroundColor = new Color(255, 165, 0); // Orange
            case ZAKONCZONA -> backgroundColor = new Color(192, 192, 192); // Silver
            case ANULOWANA -> backgroundColor = new Color(255, 182, 193); // Light Pink
            case ZARCHIWIZOWANA -> backgroundColor = new Color(211, 211, 211); // Light Gray
            default -> backgroundColor = Color.WHITE;
        }

        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("STATUS: " + rezerwacja.getStatusRezerwacji().getOpis().toUpperCase());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createGoscPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Gościa"));
        GridBagConstraints gbc = new GridBagConstraints();

        Gosc gosc = rezerwacja.getGosc();
        addInfoField(panel, gbc, "Imię:", gosc.getImie(), 0);
        addInfoField(panel, gbc, "Nazwisko:", gosc.getNazwisko(), 1);
        addInfoField(panel, gbc, "Telefon:", gosc.getTelefon(), 2);
        addInfoField(panel, gbc, "Email:", gosc.getEmail(), 3);

        if (gosc.getAdres() != null) {
            addInfoField(panel, gbc, "Adres:", gosc.getAdres().toString(), 4);
        }

        return panel;
    }

    private JPanel createPokojPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Pokoju"));
        GridBagConstraints gbc = new GridBagConstraints();

        Pokoj pokoj = rezerwacja.getPokoj();
        addInfoField(panel, gbc, "Numer pokoju:", String.valueOf(pokoj.getNumerPokoju()), 0);
        addInfoField(panel, gbc, "Typ pokoju:", pokoj.getTypPokoju(), 1);
        addInfoField(panel, gbc, "Liczba łóżek:", String.valueOf(pokoj.getLiczbaLozek()), 2);
        addInfoField(panel, gbc, "Cena za dobę:", String.format("%.2f zł", pokoj.getCenaZaDobe()), 3);

        // Udogodnienia
        String udogodnienia = pokoj.getUdogodnienia().isEmpty() ?
                "Brak" : String.join(", ", pokoj.getUdogodnienia());
        addInfoField(panel, gbc, "Udogodnienia:", udogodnienia, 4);

        return panel;
    }

    private JPanel createRezerwacjaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Rezerwacji"));
        GridBagConstraints gbc = new GridBagConstraints();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long liczbaDni = rezerwacja.getLiczbaDni();
        double kosztPobytu = rezerwacja.obliczKosztPobytu();

        addInfoField(panel, gbc, "Numer rezerwacji:", rezerwacja.getNumerRezerwacji(), 0);
        addInfoField(panel, gbc, "Data utworzenia:", rezerwacja.getDataUtworzenia().format(formatter), 1);
        addInfoField(panel, gbc, "Data przyjazdu:", rezerwacja.getDataPrzyjazdu().format(formatter), 2);
        addInfoField(panel, gbc, "Data wyjazdu:", rezerwacja.getDataWyjazdu().format(formatter), 3);
        addInfoField(panel, gbc, "Liczba dni:", String.valueOf(liczbaDni), 4);
        addInfoField(panel, gbc, "Liczba gości:", String.valueOf(rezerwacja.getLiczbaGosci()), 5);
        addInfoField(panel, gbc, "Koszt pobytu:", String.format("%.2f zł", kosztPobytu), 6);

        if (rezerwacja.getPracownikTworzacy() != null) {
            addInfoField(panel, gbc, "Utworzona przez:",
                    rezerwacja.getPracownikTworzacy().getPelneImieNazwisko(), 7);
        }

        return panel;
    }


    private void addInfoField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 5, 3, 10);
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1; gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
        gbc.weightx = 0.0;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton zamknijButton = new JButton("Zamknij");
        zamknijButton.setPreferredSize(new Dimension(100, 35));
        zamknijButton.addActionListener(e -> dispose());

        // Opcjonalnie: przycisk do edycji rezerwacji (jeśli status na to pozwala)
        if (rezerwacja.getStatusRezerwacji() == StatusRezerwacji.UTWORZONA ||
                rezerwacja.getStatusRezerwacji() == StatusRezerwacji.POTWIERDZONA) {

            JButton edytujButton = new JButton("Edytuj");
            edytujButton.setPreferredSize(new Dimension(100, 35));
            edytujButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this,
                        "Funkcja edycji rezerwacji będzie dostępna w przyszłej wersji",
                        "Informacja", JOptionPane.INFORMATION_MESSAGE);
            });
            panel.add(edytujButton);
        }

        panel.add(zamknijButton);

        return panel;
    }

    private void loadSzczegoly() {

        //TODO w przyszłości
    }
}
