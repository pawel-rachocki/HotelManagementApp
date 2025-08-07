package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "kierownik")
@DiscriminatorValue("KIEROWNIK")
public class Kierownik extends Pracownik {

    @NotBlank(message = "Obszar odpowiedzialności nie może być pusty")
    private String obszarOdpowiedzialnosci;

    // Konstruktory
    public Kierownik() {
        super();
    }

    public Kierownik(String imie, String nazwisko, String telefon,
                     LocalDate dataUrodzenia, String obszarOdpowiedzialnosci) {
        super(imie, nazwisko, telefon, dataUrodzenia);
        this.obszarOdpowiedzialnosci = obszarOdpowiedzialnosci;
    }

    // Getters i Setters
    public String getObszarOdpowiedzialnosci() { return obszarOdpowiedzialnosci; }
    public void setObszarOdpowiedzialnosci(String obszarOdpowiedzialnosci) {
        this.obszarOdpowiedzialnosci = obszarOdpowiedzialnosci;
    }

    @Override
    public String getTypPracownika() {
        return "Kierownik";
    }
}

