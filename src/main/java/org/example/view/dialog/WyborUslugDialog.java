package org.example.view.dialog;


import org.example.controller.MainController;
import org.example.models.Gosc;
import org.example.models.Pokoj;
import org.example.models.UslugaDodatkowa;
import org.example.service.UslugaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class WyborUslugDialog extends JDialog {

    private MainController controller;
    private UslugaService uslugaService;
    private Gosc gosc;
    private Pokoj wybranyPokoj;
    private LocalDate dataPrzyjazdu;
    private LocalDate dataWyjazdu;
    private int liczbaGosci;

    // Komponenty GUI
    private JPanel uslugPanel;
    private Map<UslugaDodatkowa, JCheckBox> uslugaCheckBoxes;
    private JLabel kosztUslugLabel;
    private JButton dalejButton;
    private JButton powrotButton;
    private JButton anulujButton;

    private List<UslugaDodatkowa> wybraneUslugi;
    private double kosztUslug = 0.0;

    public WyborUslugDialog(JFrame parent, MainController controller,
                            Gosc gosc, Pokoj wybranyPokoj,
                            LocalDate dataPrzyjazdu, LocalDate dataWyjazdu,
                            int liczbaGosci) {
        super(parent, "Wybór Usług Dodatkowych", true);
        this.controller = controller;
        this.gosc = gosc;
        this.wybranyPokoj = wybranyPokoj;
        this.dataPrzyjazdu = dataPrzyjazdu;
        this.dataWyjazdu = dataWyjazdu;
        this.liczbaGosci = liczbaGosci;
        this.uslugaService = new UslugaService();
        this.uslugaCheckBoxes = new HashMap<>();
        this.wybraneUslugi = new ArrayList<>();
        initializeGUI();
        loadUslugi();
    }

    private void initializeGUI() {
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel górny z informacjami
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel główny z usługami
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();

        // Tytuł
        JLabel titleLabel = new JLabel("Wybór Usług Dodatkowych");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titleLabel, gbc);

        // Informacje o rezerwacji
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        long liczbaDni = dataPrzyjazdu.until(dataWyjazdu).getDays();
        double kosztPokoju = liczbaDni * wybranyPokoj.getCenaZaDobe();

        JLabel infoLabel = new JLabel(String.format(
                "<html><b>Gość:</b> %s<br>" +
                        "<b>Pokój:</b> %d (%s)<br>" +
                        "<b>Termin:</b> %s - %s (%d dni)<br>" +
                        "<b>Koszt pokoju:</b> %.2f zł</html>",
                gosc.getPelneImieNazwisko(),
                wybranyPokoj.getNumerPokoju(),
                wybranyPokoj.getTypPokoju(),
                dataPrzyjazdu.format(formatter),
                dataWyjazdu.format(formatter),
                liczbaDni,
                kosztPokoju
        ));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(infoLabel, gbc);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Label z instrukcjami
        JLabel instructionLabel = new JLabel("Wybierz usługi dodatkowe (opcjonalnie):");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(instructionLabel, BorderLayout.NORTH);

        // Panel z checkboxami usług
        uslugPanel = new JPanel();
        uslugPanel.setLayout(new BoxLayout(uslugPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(uslugPanel);
        scrollPane.setPreferredSize(new Dimension(550, 350));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Dostępne usługi"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel z kosztem usług
        JPanel costPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        kosztUslugLabel = new JLabel("Koszt usług: 0.00 zł");
        kosztUslugLabel.setFont(new Font("Arial", Font.BOLD, 14));
        kosztUslugLabel.setForeground(new Color(0, 100, 0));
        costPanel.add(kosztUslugLabel);
        panel.add(costPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        powrotButton = new JButton("Powrót");
        powrotButton.setPreferredSize(new Dimension(100, 35));
        powrotButton.addActionListener(this::powrotDoWyboruPokoju);

        dalejButton = new JButton("Dalej");
        dalejButton.setPreferredSize(new Dimension(100, 35));
        dalejButton.addActionListener(this::przejdzDoPodsumowania);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 35));
        anulujButton.addActionListener(e -> dispose());

        panel.add(powrotButton);
        panel.add(dalejButton);
        panel.add(anulujButton);

        return panel;
    }

    private void loadUslugi() {
        try {
            // Pobierz wszystkie dostępne usługi z bazy danych
            List<UslugaDodatkowa> dostepneUslugi = uslugaService.getAllUslugi();

            for (UslugaDodatkowa usluga : dostepneUslugi) {
                JPanel uslugaPanel = createUslugaPanel(usluga);
                this.uslugPanel.add(uslugaPanel);
                this.uslugPanel.add(Box.createVerticalStrut(5));
            }

            // Jeśli brak usług w bazie, dodaj przykładowe
            if (dostepneUslugi.isEmpty()) {
                dodajPrzykladoweUslugi();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas ładowania usług: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);

            // Dodaj przykładowe usługi - tylko do prezentacji celów prezentacji
            dodajPrzykladoweUslugi();
        }
    }

    private JPanel createUslugaPanel(UslugaDodatkowa usluga) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Checkbox z nazwą usługi
        JCheckBox checkBox = new JCheckBox(usluga.getNazwaUslugi());
        checkBox.setFont(new Font("Arial", Font.BOLD, 12));
        checkBox.addActionListener(e -> aktualizujKosztUsług());
        uslugaCheckBoxes.put(usluga, checkBox);

        // Panel z informacjami o usłudze
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(checkBox, gbc);

        // Cena usługi
        JLabel cenaLabel = new JLabel(String.format("%.2f zł", usluga.getCena()));
        cenaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        cenaLabel.setForeground(new Color(0, 100, 0));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 0, 0);
        infoPanel.add(cenaLabel, gbc);

        // Opis usługi
        if (usluga.getOpis() != null && !usluga.getOpis().trim().isEmpty()) {
            JLabel opisLabel = new JLabel("<html><i>" + usluga.getOpis() + "</i></html>");
            opisLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(5, 20, 0, 0);
            infoPanel.add(opisLabel, gbc);
        }

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private void dodajPrzykladoweUslugi() {
        // Przykładowe usługi zgodnie z mockupem z dokumentacji
        UslugaDodatkowa sniadanie = new UslugaDodatkowa("Śniadanie",
                "Śniadanie kontynentalne", 50.0, "wyżywienie");
        UslugaDodatkowa parking = new UslugaDodatkowa("Parking",
                "Miejsce parkingowe", 20.0, "transport");
        UslugaDodatkowa spa = new UslugaDodatkowa("Spa",
                "Dostęp do strefy wellness", 100.0, "wellness");

        List<UslugaDodatkowa> przykladoweUslugi = List.of(sniadanie, parking, spa);

        for (UslugaDodatkowa usluga : przykladoweUslugi) {
            JPanel uslugaPanel = createUslugaPanel(usluga);
            this.uslugPanel.add(uslugaPanel);
            this.uslugPanel.add(Box.createVerticalStrut(5));
        }
    }

    private void aktualizujKosztUsług() {
        kosztUslug = 0.0;
        wybraneUslugi.clear();

        for (Map.Entry<UslugaDodatkowa, JCheckBox> entry : uslugaCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                UslugaDodatkowa usluga = entry.getKey();
                wybraneUslugi.add(usluga);
                kosztUslug += usluga.getCena();
            }
        }

        kosztUslugLabel.setText(String.format("Koszt usług: %.2f zł", kosztUslug));
    }

    private void powrotDoWyboruPokoju(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz wrócić do wyboru pokoju?\n" +
                        "Wybrane usługi zostaną utracone.",
                "Potwierdzenie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            // Tutaj można dodać powrót do ListaDostepnychPokoiDialog
            JOptionPane.showMessageDialog(getParent(),
                    "Powrót do listy dostępnych pokoi...",
                    "Informacja", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void przejdzDoPodsumowania(ActionEvent e) {
        // Krok 11-13 przejście do podsumowania rezerwacji
        dispose();
        otworzPodsumowanieRezerwacji();
    }

    private void otworzPodsumowanieRezerwacji() {
        dispose();

        PodsumowanieRezerwacjiDialog podsumowanieDialog = new PodsumowanieRezerwacjiDialog(
                (JFrame) getParent(), controller, gosc, wybranyPokoj,
                dataPrzyjazdu, dataWyjazdu, liczbaGosci, wybraneUslugi, kosztUslug);
        podsumowanieDialog.setVisible(true);
    }


    public List<UslugaDodatkowa> getWybraneUslugi() {
        return wybraneUslugi;
    }

    public double getKosztUslug() {
        return kosztUslug;
    }
}
