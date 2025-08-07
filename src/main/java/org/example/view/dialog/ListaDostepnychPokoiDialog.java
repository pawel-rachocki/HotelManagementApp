package org.example.view.dialog;

import org.example.controller.MainController;
import org.example.models.Gosc;
import org.example.models.Pokoj;
import org.example.service.RezerwacjaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListaDostepnychPokoiDialog extends JDialog {

    private MainController controller;
    private RezerwacjaService rezerwacjaService;
    private Gosc gosc;
    private LocalDate dataPrzyjazdu;
    private LocalDate dataWyjazdu;
    private int liczbaGosci;
    private List<Pokoj> dostepnePokoje;

    // Komponenty GUI zgodnie z mockupem
    private JTable pokojTable;
    private DefaultTableModel tableModel;
    private JButton wybierzButton;
    private JButton anulujButton;
    private JLabel infoLabel;

    private Pokoj wybranyPokoj;

    public ListaDostepnychPokoiDialog(JFrame parent, MainController controller,
                                      Gosc gosc, List<Pokoj> dostepnePokoje,
                                      LocalDate dataPrzyjazdu, LocalDate dataWyjazdu,
                                      int liczbaGosci) {
        super(parent, "Lista Dostępnych Pokoi", true);
        this.controller = controller;
        this.gosc = gosc;
        this.dostepnePokoje = dostepnePokoje;
        this.dataPrzyjazdu = dataPrzyjazdu;
        this.dataWyjazdu = dataWyjazdu;
        this.liczbaGosci = liczbaGosci;
        this.rezerwacjaService = new RezerwacjaService();
        initializeGUI();
        loadPokoje();
    }

    private void initializeGUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Panel górny z informacjami o rezerwacji
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel główny z tabelą pokoi
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
        JLabel titleLabel = new JLabel("Lista Dostępnych Pokoi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titleLabel, gbc);

        // Informacje o rezerwacji zgodnie z mockupem
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        infoLabel = new JLabel(String.format(
                "<html><b>Gość:</b> %s<br>" +
                        "<b>Termin:</b> %s - %s<br>" +
                        "<b>Liczba gości:</b> %d<br>" +
                        "<b>Dostępnych pokoi:</b> %d</html>",
                gosc.getPelneImieNazwisko(),
                dataPrzyjazdu.format(formatter),
                dataWyjazdu.format(formatter),
                liczbaGosci,
                dostepnePokoje.size()
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

        // Tabela pokoi
        String[] columnNames = {
                "Numer pokoju", "Typ", "Liczba łóżek", "Cena za dobę", "Udogodnienia"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela tylko do odczytu
            }
        };

        pokojTable = new JTable(tableModel);
        pokojTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pokojTable.setRowHeight(25);
        pokojTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        pokojTable.setFont(new Font("Arial", Font.PLAIN, 11));

        // Sortowanie tabeli
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        pokojTable.setRowSorter(sorter);

        // Event listener dla wyboru pokoju
        pokojTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = pokojTable.getSelectedRow();
                wybierzButton.setEnabled(selectedRow >= 0);
            }
        });

        // Double-click dla wyboru pokoju
        pokojTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    wybierzPokoj(null);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pokojTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Label z instrukcjami
        JLabel instructionLabel = new JLabel("Wybierz pokój z listy i kliknij 'Wybierz' lub kliknij dwukrotnie na pokój");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(instructionLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        wybierzButton = new JButton("Wybierz pokój");
        wybierzButton.setPreferredSize(new Dimension(150, 35));
        wybierzButton.setEnabled(false); // Włączone po wyborze pokoju
        wybierzButton.addActionListener(this::wybierzPokoj);

        anulujButton = new JButton("Anuluj");
        anulujButton.setPreferredSize(new Dimension(100, 35));
        anulujButton.addActionListener(e -> dispose());

        panel.add(wybierzButton);
        panel.add(anulujButton);

        return panel;
    }

    private void loadPokoje() {
        // Wypełnienie tabeli danymi o pokojach
        for (Pokoj pokoj : dostepnePokoje) {
            Object[] rowData = {
                    pokoj.getNumerPokoju(),
                    pokoj.getTypPokoju(),
                    pokoj.getLiczbaLozek(),
                    String.format("%.2f zł", pokoj.getCenaZaDobe()),
                    formatujUdogodnienia(pokoj.getUdogodnienia())
            };
            tableModel.addRow(rowData);
        }

        // Automatyczne dopasowanie szerokości kolumn
        pokojTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Numer pokoju
        pokojTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Typ
        pokojTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Liczba łóżek
        pokojTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Cena
        pokojTable.getColumnModel().getColumn(4).setPreferredWidth(280); // Udogodnienia
    }

    private String formatujUdogodnienia(List<String> udogodnienia) {
        if (udogodnienia == null || udogodnienia.isEmpty()) {
            return "Brak";
        }
        return String.join(", ", udogodnienia);
    }

    private void wybierzPokoj(ActionEvent e) {
        int selectedRow = pokojTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Proszę wybrać pokój z listy",
                    "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Zmiana indeksu wiersza
        int modelRow = pokojTable.convertRowIndexToModel(selectedRow);
        wybranyPokoj = dostepnePokoje.get(modelRow);

        // Potwierdzenie wyboru pokoju
        long liczbaDni = dataPrzyjazdu.until(dataWyjazdu).getDays();
        double kosztPobytu = liczbaDni * wybranyPokoj.getCenaZaDobe();

        int result = JOptionPane.showConfirmDialog(this,
                String.format("Potwierdzenie wyboru pokoju:\n\n" +
                                "Pokój: %d (%s)\n" +
                                "Liczba łóżek: %d\n" +
                                "Cena za dobę: %.2f zł\n" +
                                "Liczba dni: %d\n" +
                                "Koszt pobytu: %.2f zł\n" +
                                "Udogodnienia: %s\n\n" +
                                "Czy chcesz wybrać ten pokój?",
                        wybranyPokoj.getNumerPokoju(),
                        wybranyPokoj.getTypPokoju(),
                        wybranyPokoj.getLiczbaLozek(),
                        wybranyPokoj.getCenaZaDobe(),
                        liczbaDni,
                        kosztPobytu,
                        formatujUdogodnienia(wybranyPokoj.getUdogodnienia())),
                "Potwierdzenie wyboru pokoju",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // 10 przejście do wyboru usług dodatkowych
            dispose();
            otworzWyborUslug();
        }
    }

    private void otworzWyborUslug() {
        dispose();

        WyborUslugDialog wyborDialog = new WyborUslugDialog(
                (JFrame) getParent(), controller, gosc, wybranyPokoj,
                dataPrzyjazdu, dataWyjazdu, liczbaGosci);
        wyborDialog.setVisible(true);
    }


    public Pokoj getWybranyPokoj() {
        return wybranyPokoj;
    }
}
