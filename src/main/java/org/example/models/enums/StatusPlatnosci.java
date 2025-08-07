package org.example.models.enums;

public enum StatusPlatnosci {
    OCZEKUJACA("oczekująca"),
    ZREALIZOWANA("zrealizowana"),
    ANULOWANA("anulowana");

    private final String opis;

    StatusPlatnosci(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }
}

