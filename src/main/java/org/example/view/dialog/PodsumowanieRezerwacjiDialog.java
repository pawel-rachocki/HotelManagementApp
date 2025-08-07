package org.example.view.dialog;

import org.example.controller.MainController;
import org.example.models.*;

import org.example.service.PracownikService;
import org.example.service.RezerwacjaService;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PodsumowanieRezerwacjiDialog extends JDialog {

    private MainController controller;
    private RezerwacjaService rezerwacjaService;
    private Gosc gosc;
    private Pokoj wybranyPokoj;
    private LocalDate dataPrzyjazdu;
    private LocalDate dataWyjazdu;
    private int liczbaGosci;
    private List<UslugaDodatkowa> wybraneUslugi;
    private double kosztUslug;

    // Komponenty GUI zgodnie z mockupem
    private JPanel daneGosciaPanel;
    private JPanel danePokojuPanel;
    private JPanel daneRezerwacjiPanel;
    private JPanel uslugPanel;
    private JLabel calkowitaKwotaLabel;
    private JComboBox<String> metodaPlatnosciCombo;
    private JButton potwierdzButton;
    private JButton powrotButton;
    private JButton anulujButton;

    private double kosztPokoju;
    private double calkowitaKwota;

    public PodsumowanieRezerwacjiDialog(JFrame parent, MainController controller,
                                        Gosc gosc, Pokoj wybranyPokoj,
                                        LocalDate dataPrzyjazdu, LocalDate dataWyjazdu,
                                        int liczbaGosci, List<UslugaDodatkowa> wybraneUslugi,
                                        double kosztUslug) {
        super(parent, "Podsumowanie Rezerwacji", true);
        this.controller = controller;
        this.gosc = gosc;
        this.wybranyPokoj = wybranyPokoj;
        this.dataPrzyjazdu = dataPrzyjazdu;
        this.dataWyjazdu = dataWyjazdu;
        this.liczbaGosci = liczbaGosci;
        this.wybraneUslugi = wybraneUslugi;
        this.kosztUslug = kosztUslug;
        this.rezerwacjaService = new RezerwacjaService();

        obliczKoszty();
        initializeGUI();
    }

    private void obliczKoszty() {
        long liczbaDni = dataPrzyjazdu.until(dataWyjazdu).getDays();
        kosztPokoju = liczbaDni * wybranyPokoj.getCenaZaDobe();
        calkowitaKwota = kosztPokoju + kosztUslug;
    }

    private void initializeGUI() {
        setSize(700, 800);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel główny z podsumowaniem
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tytuł
        JLabel titleLabel = new JLabel("Podsumowanie Rezerwacji");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel ze wszystkimi sekcjami
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Sekcja danych gościa zgodnie z mockupem
        daneGosciaPanel = createDaneGosciaPanel();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(daneGosciaPanel, gbc);

        // Sekcja danych pokoju
        danePokojuPanel = createDanePokojuPanel();
        gbc.gridy = 1;
        contentPanel.add(danePokojuPanel, gbc);

        // Sekcja danych rezerwacji
        daneRezerwacjiPanel = createDaneRezerwacjiPanel();
        gbc.gridy = 2;
        contentPanel.add(daneRezerwacjiPanel, gbc);

        // Sekcja usług dodatkowych
        uslugPanel = createUslugPanel();
        gbc.gridy = 3;
        contentPanel.add(uslugPanel, gbc);

        // Sekcja całkowitej kwoty
        JPanel kwotaPanel = createKwotaPanel();
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 15, 0);
        contentPanel.add(kwotaPanel, gbc);

        // Sekcja metody płatności
        JPanel platnoscPanel = createPlatnoscPanel();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(platnoscPanel, gbc);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    //Tworzenie panelu z danymi gościa
    private JPanel createDaneGosciaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Gościa"));
        GridBagConstraints gbc = new GridBagConstraints();

        addInfoField(panel, gbc, "Imię:", gosc.getImie(), 0);
        addInfoField(panel, gbc, "Nazwisko:", gosc.getNazwisko(), 1);
        addInfoField(panel, gbc, "Telefon:", gosc.getTelefon(), 2);
        addInfoField(panel, gbc, "Email:", gosc.getEmail(), 3);

        return panel;
    }

    // Tworzenie panelu z danymi o pokojach
    private JPanel createDanePokojuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Pokoju"));
        GridBagConstraints gbc = new GridBagConstraints();

        addInfoField(panel, gbc, "Numer:", String.valueOf(wybranyPokoj.getNumerPokoju()), 0);
        addInfoField(panel, gbc, "Typ pokoju:", wybranyPokoj.getTypPokoju(), 1);
        addInfoField(panel, gbc, "Liczba łóżek:", String.valueOf(wybranyPokoj.getLiczbaLozek()), 2);
        addInfoField(panel, gbc, "Cena za dobę:", String.format("%.2f zł", wybranyPokoj.getCenaZaDobe()), 3);

        // Udogodnienia
        String udogodnienia = wybranyPokoj.getUdogodnienia().isEmpty() ?
                "Brak" : String.join(", ", wybranyPokoj.getUdogodnienia());
        addInfoField(panel, gbc, "Udogodnienia:", udogodnienia, 4);

        return panel;
    }

    //Tworzenie panelu z danymi o rezerwacji
    private JPanel createDaneRezerwacjiPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dane Rezerwacji"));
        GridBagConstraints gbc = new GridBagConstraints();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long liczbaDni = dataPrzyjazdu.until(dataWyjazdu).getDays();

        addInfoField(panel, gbc, "Data przyjazdu:", dataPrzyjazdu.format(formatter), 0);
        addInfoField(panel, gbc, "Data wyjazdu:", dataWyjazdu.format(formatter), 1);
        addInfoField(panel, gbc, "Liczba dni:", String.valueOf(liczbaDni), 2);
        addInfoField(panel, gbc, "Liczba gości:", String.valueOf(liczbaGosci), 3);
        addInfoField(panel, gbc, "Koszt pokoju:", String.format("%.2f zł", kosztPokoju), 4);

        return panel;
    }

    // Tworzenie panelu z usługami dodatkowymi do wyboru
    private JPanel createUslugPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Usługi Dodatkowe"));

        if (wybraneUslugi.isEmpty()) {
            JLabel brakLabel = new JLabel("Brak wybranych usług");
            brakLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            brakLabel.setHorizontalAlignment(SwingConstants.CENTER);
            brakLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(brakLabel, BorderLayout.CENTER);
        } else {
            JPanel uslugListPanel = new JPanel();
            uslugListPanel.setLayout(new BoxLayout(uslugListPanel, BoxLayout.Y_AXIS));

            for (UslugaDodatkowa usluga : wybraneUslugi) {
                JPanel uslugaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                uslugaPanel.add(new JLabel("• " + usluga.getNazwaUslugi()));
                uslugaPanel.add(new JLabel(String.format("%.2f zł", usluga.getCena())));
                uslugListPanel.add(uslugaPanel);
            }

            panel.add(uslugListPanel, BorderLayout.CENTER);

            // Koszt usług
            JLabel kosztUslugLabel = new JLabel(String.format("Razem usługi: %.2f zł", kosztUslug));
            kosztUslugLabel.setFont(new Font("Arial", Font.BOLD, 12));
            kosztUslugLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            kosztUslugLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
            panel.add(kosztUslugLabel, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel createKwotaPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 255, 240));

        calkowitaKwotaLabel = new JLabel(String.format("CAŁKOWITY KOSZT: %.2f zł", calkowitaKwota));
        calkowitaKwotaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        calkowitaKwotaLabel.setForeground(new Color(0, 120, 0));
        panel.add(calkowitaKwotaLabel);

        return panel;
    }

    private JPanel createPlatnoscPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Metoda Płatności"));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel metodaLabel = new JLabel("Wybierz metodę płatności:");
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        panel.add(metodaLabel, gbc);

        String[] metodyPlatnosci = {"karta", "gotówka", "przelew"};
        metodaPlatnosciCombo = new JComboBox<>(metodyPlatnosci);
        metodaPlatnosciCombo.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(metodaPlatnosciCombo, gbc);

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

        powrotButton = new JButton("Powrót");
        powrotButton.setPreferredSize(new Dimension(100, 35));
        powrotButton.addActionListener(this::powrotDoUslug);

        potwierdzButton = new JButton("Potwierdź Rezerwację");
        potwierdzButton.setPreferredSize(new Dimension(180, 35));
        potwierdzButton.setBackground(new Color(0, 150, 0));
        potwierdzButton.setForeground(Color.WHITE);
        potwierdzButton.setFont(new Font("Arial", Font.BOLD, 12));
        potwierdzButton.addActionListener(this::potwierdzRezerwacje);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 35));
        anulujButton.addActionListener(e -> dispose());

        panel.add(powrotButton);
        panel.add(potwierdzButton);
        panel.add(anulujButton);

        return panel;
    }

    private void powrotDoUslug(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz wrócić do wyboru usług?",
                "Potwierdzenie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            // Tutaj można dodać powrót do WyborUslugDialog
            JOptionPane.showMessageDialog(getParent(),
                    "Powrót do wyboru usług dodatkowych...",
                    "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void potwierdzRezerwacje(ActionEvent e) {
        try {
            String metodaPlatnosci = (String) metodaPlatnosciCombo.getSelectedItem();

            // Pobierz pierwszego recepcjonistę z bazy danych
            PracownikService pracownikService = new PracownikService();
            Recepcjonista recepcjonista = pracownikService.getFirstRecepcjonista();

            if (recepcjonista == null) {
                JOptionPane.showMessageDialog(this,
                        "Brak recepcjonistów w systemie. Skontaktuj się z administratorem.",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Utworzenie rezerwacji przez serwis
            Rezerwacja nowaRezerwacja = rezerwacjaService.utworzRezerwacje(
                    dataPrzyjazdu, dataWyjazdu, liczbaGosci, gosc, wybranyPokoj, recepcjonista);

            dispose();
            pokazPotwierdzenie(nowaRezerwacja);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas tworzenia rezerwacji: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void pokazPotwierdzenie(Rezerwacja rezerwacja) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        int result = JOptionPane.showConfirmDialog(getParent(),
                String.format("REZERWACJA ZOSTAŁA UTWORZONA!\n\n" +
                                "Numer rezerwacji: %s\n" +
                                "Gość: %s\n" +
                                "Pokój: %d (%s)\n" +
                                "Termin: %s - %s\n" +
                                "Całkowity koszt: %.2f zł\n" +
                                "Status: %s\n\n" +
                                "Czy chcesz utworzyć kolejną rezerwację?",
                        rezerwacja.getNumerRezerwacji(),
                        gosc.getPelneImieNazwisko(),
                        wybranyPokoj.getNumerPokoju(),
                        wybranyPokoj.getTypPokoju(),
                        dataPrzyjazdu.format(formatter),
                        dataWyjazdu.format(formatter),
                        calkowitaKwota,
                        rezerwacja.getStatusRezerwacji().getOpis()),
                "Rezerwacja utworzona",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Krok 17A scenariusza: nowa rezerwacja
            controller.otworzNowaRezerwacja();
        }
        // Krok 19 scenariusza: zakończenie przypadku użycia
    }
}

