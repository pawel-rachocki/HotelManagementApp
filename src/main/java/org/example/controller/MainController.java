package org.example.controller;


import org.example.models.Gosc;
import org.example.models.Rezerwacja;
import org.example.service.GoscService;
import org.example.service.RezerwacjaService;
import org.example.view.MainFrame;
import org.example.view.dialog.FormularzRezerwacjiDialog;
import org.example.view.dialog.NowaRezerwacjaDialog;
import org.example.view.dialog.SzczegolyRezerwacjiDialog;
import org.example.view.dialog.WyszukajGosciaDialog;

import javax.swing.*;
import java.util.List;

/**
 * Główny kontroler aplikacji zgodny z wzorcem MVC.
 * Zarządza komunikacją między warstwą widoku (View) a warstwą serwisów (Model).
 * Implementuje funkcjonalności zgodnie z dokumentacją projektową systemu zarządzania hotelem.
 */

public class MainController {

    // Główne okno aplikacji - View
    private MainFrame mainFrame;

    // Serwis do obsługi operacji na gościach
    private GoscService goscService;

    // Serwis do obsługi operacji na rezerwacjach
    private RezerwacjaService rezerwacjaService;

    public MainController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.goscService = new GoscService();
        this.rezerwacjaService = new RezerwacjaService();
    }

    public void wczytajPoczatkoweDane() {
        try {
            // Pobierz wszystkich gości z warstwy serwisów
            List<Gosc> goscie = goscService.getAllGoscie();
            // Zaktualizuj widok zgodnie z wzorcem MVC
            mainFrame.updateGoscieList(goscie);
        } catch (Exception e) {
            mainFrame.showError("Błąd podczas wczytywania danych: " + e.getMessage());
        }
    }

    public void wczytajRezerwacjeGoscia(Gosc gosc) {
        try {
            List<Rezerwacja> rezerwacje = rezerwacjaService.getRezerwacjeGoscia(gosc.getId());
            mainFrame.updateRezerwacjeList(rezerwacje);
        } catch (Exception e) {
            mainFrame.showError("Błąd podczas wczytywania rezerwacji: " + e.getMessage());
        }
    }

    public void otworzNowaRezerwacja() {
        NowaRezerwacjaDialog dialog = new NowaRezerwacjaDialog((JFrame) mainFrame, this);
        dialog.setVisible(true);
    }

    public void otworzWyszukajGoscia() {
        WyszukajGosciaDialog dialog = new WyszukajGosciaDialog((JFrame) mainFrame, this);
        dialog.setVisible(true);
    }

    public void odswiezDane() {
        wczytajPoczatkoweDane();
    }
    public void otworzNowaRezerwacjaDlaGoscia(Gosc gosc) {
        // Przejdź bezpośrednio do formularza rezerwacji z wypełnionymi danymi gościa
        FormularzRezerwacjiDialog formularzDialog = new FormularzRezerwacjiDialog(
                (JFrame) mainFrame, this, gosc);
        formularzDialog.setVisible(true);
    }

//    public void pokazSzczegolyRezerwacji(Rezerwacja rezerwacja) {
//        // Otwórz dialog ze szczegółami rezerwacji
//        SzczegolyRezerwacjiDialog szczegolyDialog = new SzczegolyRezerwacjiDialog(
//                (JFrame) mainFrame, this, rezerwacja);
//        szczegolyDialog.setVisible(true);
//    }

    public void pokazSzczegolyRezerwacji(Rezerwacja rezerwacja) {
        try {
            // Pobierz rezerwację z asocjacjami
            Rezerwacja pelnaRezerwacja = rezerwacjaService.getRezerwacjaWithDetails(rezerwacja.getId());

            // Otwórz dialog ze szczegółami rezerewaCJI
            SzczegolyRezerwacjiDialog szczegolyDialog = new SzczegolyRezerwacjiDialog(
                    (JFrame) mainFrame, this, pelnaRezerwacja);
            szczegolyDialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog((JFrame) mainFrame,
                    "Błąd podczas ładowania szczegółów rezerwacji: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }



}

