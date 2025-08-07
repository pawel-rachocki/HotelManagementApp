package org.example.view.renderer;


import org.example.models.Gosc;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer dla JList obiektów Gosc.
 * Zgodny z wzorcem MVC - warstwa View odpowiedzialna za prezentację danych.
 */
public class GoscListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        // Defaultowo z klasy nadrzędnej
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Formatowanie dodatkowe tekstu:
        // <b> - pogrubienie imienia i nazwiska
        // <br> - nowa linia dla separacji
        // <small> - mniejsza czcionka dla emaila
        if (value instanceof Gosc) {
            Gosc gosc = (Gosc) value;
            setText(String.format("<html><b>%s</b><br><small>%s</small></html>",
                    gosc.getPelneImieNazwisko(), gosc.getEmail()));
        }

        // Zwróć ten komponent (JLabel) gotowy do wyświetlenia
        return this;
    }
}

