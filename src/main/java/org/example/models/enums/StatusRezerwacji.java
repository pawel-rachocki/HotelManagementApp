package org.example.models.enums;

public enum StatusRezerwacji {
    UTWORZONA("utworzona"),
    POTWIERDZONA("potwierdzona"),
    W_TRAKCIE("w trakcie"),
    ZAKONCZONA("zakończona"),
    ANULOWANA("anulowana"),
    ZARCHIWIZOWANA("zarchiwizowana");

    private final String opis;

    StatusRezerwacji(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }
}
