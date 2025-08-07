package org.example.view;


import org.example.controller.MainController;
import org.example.models.Gosc;
import org.example.models.Rezerwacja;
import org.example.view.renderer.GoscListCellRenderer;
import org.example.view.renderer.RezerwacjaListCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {

    private MainController controller;

    // Komponenty GUI zgodnie z wymaganiami dokumentacji
    private JList<Gosc> goscieList;
    private DefaultListModel<Gosc> goscieListModel;
    private JList<Rezerwacja> rezerwacjeList;
    private DefaultListModel<Rezerwacja> rezerwacjeListModel;

    private JButton nowaRezerwacjaButton;
    private JButton wyszukajGosciaButton;
    private JButton odswiezButton;

    public MainFrame() {
        this.controller = new MainController(this);
        initializeGUI();
        loadInitialData();
    }

    private void initializeGUI() {
        setTitle("System Zarządzania Hotelem - Hotel Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Layout głównego okna
        setLayout(new BorderLayout());

        // Panel górny z przyciskami
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel główny z listami
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Panel statusu
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());

        nowaRezerwacjaButton = new JButton("Nowa Rezerwacja");
        nowaRezerwacjaButton.setFont(new Font("Arial", Font.BOLD, 12));
        nowaRezerwacjaButton.addActionListener(e -> controller.otworzNowaRezerwacja());

        wyszukajGosciaButton = new JButton("Wyszukaj Gościa");
        wyszukajGosciaButton.addActionListener(e -> controller.otworzWyszukajGoscia());

        odswiezButton = new JButton("Odśwież");
        odswiezButton.addActionListener(e -> controller.odswiezDane());

        panel.add(nowaRezerwacjaButton);
        panel.add(wyszukajGosciaButton);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(odswiezButton);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel z listą gości
        JPanel gosciePanel = createGosciePanel();
        panel.add(gosciePanel);

        // Panel z rezerwacjami wybranego gościa
        JPanel rezerwacjePanel = createRezerwacjePanel();
        panel.add(rezerwacjePanel);

        return panel;
    }

    private JPanel createGosciePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Goście"));

        // Lista gości zgodnie z wymaganiami dokumentacji
        goscieListModel = new DefaultListModel<>();
        goscieList = new JList<>(goscieListModel);
        goscieList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        goscieList.setCellRenderer(new GoscListCellRenderer());

        // Event listener dla wyboru gościa (single click)
        goscieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Gosc wybranyGosc = goscieList.getSelectedValue();
                if (wybranyGosc != null) {
                    controller.wczytajRezerwacjeGoscia(wybranyGosc);
                }
            }
        });

        // NOWA FUNKCJONALNOŚĆ: Double-click listener
        goscieList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Gosc wybranyGosc = goscieList.getSelectedValue();
                    if (wybranyGosc != null) {
                        // Przejdź bezpośrednio do nowej rezerwacji dla wybranego gościa
                        controller.otworzNowaRezerwacjaDlaGoscia(wybranyGosc);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(goscieList);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    private JPanel createRezerwacjePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Rezerwacje wybranego gościa"));

        // Lista rezerwacji
        rezerwacjeListModel = new DefaultListModel<>();
        rezerwacjeList = new JList<>(rezerwacjeListModel);
        rezerwacjeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rezerwacjeList.setCellRenderer(new RezerwacjaListCellRenderer());

        //Double-click listener dla rezerwacji
        rezerwacjeList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Rezerwacja wybranaRezerwacja = rezerwacjeList.getSelectedValue();
                    if (wybranaRezerwacja != null) {
                        // Pokaż szczegóły wybranej rezerwacji
                        controller.pokazSzczegolyRezerwacji(wybranaRezerwacja);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(rezerwacjeList);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Label z instrukcjami
        JLabel instructionLabel = new JLabel("<html><i>Podwójne kliknięcie: szczegóły rezerwacji</i></html>");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panel.add(instructionLabel, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("System gotowy");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(statusLabel);

        return panel;
    }

    // Metody publiczne dla kontrolera
    public void updateGoscieList(List<Gosc> goscie) {
        SwingUtilities.invokeLater(() -> {
            goscieListModel.clear();
            for (Gosc gosc : goscie) {
                goscieListModel.addElement(gosc);
            }
        });
    }

    public void updateRezerwacjeList(List<Rezerwacja> rezerwacje) {
        SwingUtilities.invokeLater(() -> {
            rezerwacjeListModel.clear();
            for (Rezerwacja rezerwacja : rezerwacje) {
                rezerwacjeListModel.addElement(rezerwacja);
            }
        });
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Informacja", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    private void loadInitialData() {
        controller.wczytajPoczatkoweDane();
    }
}

