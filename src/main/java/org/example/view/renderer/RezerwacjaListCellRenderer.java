package org.example.view.renderer;

import org.example.models.Rezerwacja;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;


/**
 * Niestandardowy Renderer dla JList dla obiektrów Rezerwacja.
 * Rozszerza DefaultListCellRenderer
 * Implementuje wymaganie z dokumentacji: "po kliknięciu na wybranego gościa wyświetla się drugi ListBox z jego rezerwacjami".
 */
public class RezerwacjaListCellRenderer extends DefaultListCellRenderer {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    //Główna metoda renderująca pojedynczy element listy rezerwacji.
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        // Defaultowa implentacja
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // customowa implementacja - formatowanie
        if (value instanceof Rezerwacja) {
            Rezerwacja rezerwacja = (Rezerwacja) value;
            setText(String.format("<html><b>%s</b><br><small>%s - %s | Status: %s</small></html>",
                    rezerwacja.getNumerRezerwacji(),
                    rezerwacja.getDataPrzyjazdu().format(DATE_FORMATTER),
                    rezerwacja.getDataWyjazdu().format(DATE_FORMATTER),
                    rezerwacja.getStatusRezerwacji().getOpis()));

            // Kolorowanie według statusu
            switch (rezerwacja.getStatusRezerwacji()) {
                case UTWORZONA -> setForeground(Color.BLUE);
                case POTWIERDZONA -> setForeground(Color.GREEN);
                case W_TRAKCIE -> setForeground(Color.ORANGE);
                case ANULOWANA -> setForeground(Color.RED);
                default -> setForeground(Color.BLACK);
            }
        }

        return this;
    }
}

