package org.example.view.dialog;

import org.example.controller.MainController;
import org.example.models.Gosc;
import org.example.models.Pokoj;
import org.example.service.RezerwacjaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FormularzRezerwacjiDialog extends JDialog {

    private MainController controller;
    private RezerwacjaService rezerwacjaService;
    private Gosc gosc;

    // Komponenty formularza zgodnie z mockupami
    private JTextField dataPrzyjazdField;
    private JTextField dataWyjazdField;
    private JSpinner liczbaGosciSpinner;

    // Panele wyświetlania danych gościa
    private JLabel imieLabel;
    private JLabel nazwiskoLabel;
    private JLabel telefonLabel;
    private JLabel emailLabel;

    private JButton sprawdzDostepnoscButton;
    private JButton anulujButton;

    public FormularzRezerwacjiDialog(JFrame parent, MainController controller, Gosc gosc) {
        super(parent, "Nowa Rezerwacja - Dane Pobytu", true);
        this.controller = controller;
        this.gosc = gosc;
        this.rezerwacjaService = new RezerwacjaService();
        initializeGUI();
        wypelnijDaneGoscia();
    }

    private void initializeGUI() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel główny
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Tytuł
        JLabel titleLabel = new JLabel("Nowa Rezerwacja");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Sekcja danych gościa zgodnie z mockupem
        addSectionPanel(mainPanel, gbc, "Dane Gościa", 1);

        // Pola danych gościa (tylko do odczytu)
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);

        addReadOnlyField(mainPanel, gbc, "Imię:", imieLabel = new JLabel(), 2);
        addReadOnlyField(mainPanel, gbc, "Nazwisko:", nazwiskoLabel = new JLabel(), 3);
        addReadOnlyField(mainPanel, gbc, "Telefon:", telefonLabel = new JLabel(), 4);
        addReadOnlyField(mainPanel, gbc, "Email:", emailLabel = new JLabel(), 5);

        // Sekcja danych pobytu zgodnie z mockupem
        addSectionPanel(mainPanel, gbc, "Dane Pobytu", 6);

        // Pola danych pobytu
        addFormField(mainPanel, gbc, "Data przyjazdu (dd.MM.yyyy):",
                dataPrzyjazdField = new JTextField(15), 7);
        addFormField(mainPanel, gbc, "Data wyjazdu (dd.MM.yyyy):",
                dataWyjazdField = new JTextField(15), 8);

        // Spinner dla liczby gości
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Liczba gości:"), gbc);

        liczbaGosciSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        liczbaGosciSpinner.setPreferredSize(new Dimension(100, 25));
        gbc.gridx = 1; gbc.gridy = 9;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(liczbaGosciSpinner, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());

        sprawdzDostepnoscButton = new JButton("Przejdź do wyboru pokoju");
        sprawdzDostepnoscButton.setPreferredSize(new Dimension(200, 30));
        sprawdzDostepnoscButton.addActionListener(this::sprawdzDostepnoscPokoi);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 30));
        anulujButton.addActionListener(e -> dispose());

        buttonPanel.add(sprawdzDostepnoscButton);
        buttonPanel.add(anulujButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(sprawdzDostepnoscButton);

        // Ustaw domyślne daty
        ustawDomyslneDaty();

        // Focus na pierwsze pole
        SwingUtilities.invokeLater(() -> dataPrzyjazdField.requestFocus());
    }

    private void addSectionPanel(JPanel parent, GridBagConstraints gbc, String title, int row) {
        JPanel sectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sectionPanel.setBorder(BorderFactory.createTitledBorder(title));
        sectionPanel.setPreferredSize(new Dimension(500, 40));

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        parent.add(sectionPanel, gbc);
    }

    private void addReadOnlyField(JPanel panel, GridBagConstraints gbc, String labelText,
                                  JLabel valueLabel, int row) {
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);

        // Label
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        // Value label
        valueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 1; gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(valueLabel, gbc);
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

    private void wypelnijDaneGoscia() {
        // Wypełnij dane gościa zgodnie z krok 5 scenariusza
        imieLabel.setText(gosc.getImie());
        nazwiskoLabel.setText(gosc.getNazwisko());
        telefonLabel.setText(gosc.getTelefon());
        emailLabel.setText(gosc.getEmail());
    }

    private void ustawDomyslneDaty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Data przyjazdu - jutro
        LocalDate jutro = LocalDate.now().plusDays(1);
        dataPrzyjazdField.setText(jutro.format(formatter));

        // Data wyjazdu - pojutrze
        LocalDate pojutrze = LocalDate.now().plusDays(2);
        dataWyjazdField.setText(pojutrze.format(formatter));
    }

    private void sprawdzDostepnoscPokoi(ActionEvent e) {
        try {
            // 6-7 scenariusza: walidacja danych formularza
            LocalDate dataPrzyjazdu = parsujDate(dataPrzyjazdField.getText().trim());
            LocalDate dataWyjazdu = parsujDate(dataWyjazdField.getText().trim());
            int liczbaGosci = (Integer) liczbaGosciSpinner.getValue();

            if (dataPrzyjazdu == null || dataWyjazdu == null) {
                return; // Błąd już wyświetlony w parsujDate()
            }

            // Walidacja zgodnie z wymaganiami biznesowymi
            rezerwacjaService.walidujDaneRezerwacji(dataPrzyjazdu, dataWyjazdu, liczbaGosci);

            // Krok 8 scenariusza: sprawdzenie dostępności pokoi
            List<Pokoj> dostepnePokoje = rezerwacjaService.sprawdzDostepnoscPokoi(dataPrzyjazdu, dataWyjazdu);

            if (dostepnePokoje.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Brak dostępnych pokoi w wybranym terminie.\n" +
                                "Proszę wybrać inne daty.",
                        "Brak dostępności", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Przejście do następnego kroku - wybór pokoju
            dispose();
            otworzListeDostepnychPokoi(dostepnePokoje, dataPrzyjazdu, dataWyjazdu, liczbaGosci);

        } catch (IllegalArgumentException ex) {
            // Wariant 6A: błędne dane formularza
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Błąd walidacji", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas sprawdzania dostępności: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate parsujDate(String dateText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateText, formatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Data musi mieć format dd.MM.yyyy (np. 15.06.2025)",
                    "Błąd formatu daty", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void otworzListeDostepnychPokoi(List<Pokoj> dostepnePokoje, LocalDate dataPrzyjazdu,
                                            LocalDate dataWyjazdu, int liczbaGosci) {
        // 9 wyświetlenie listy dostępnych pokoi
        dispose();

        ListaDostepnychPokoiDialog listaDialog = new ListaDostepnychPokoiDialog(
                (JFrame) getParent(), controller, gosc, dostepnePokoje,
                dataPrzyjazdu, dataWyjazdu, liczbaGosci);
        listaDialog.setVisible(true);
    }

}
