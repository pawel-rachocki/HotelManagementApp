package org.example.models.enums;

public enum StatusPokoju {
    DOSTEPNY("dostępny"),
    ZAJETY("zajęty"),
    W_SPRZATANIU("w sprzątaniu");

    private final String opis;

    StatusPokoju(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }
}
