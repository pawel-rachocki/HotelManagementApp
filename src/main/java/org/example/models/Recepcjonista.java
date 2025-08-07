package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recepcjonista")
@DiscriminatorValue("RECEPCJONISTA")
public class Recepcjonista extends Pracownik {

    @Min(value = 1, message = "Numer recepcji musi być większy od 0")
    private int numerRecepcji;

    @ElementCollection
    @CollectionTable(name = "recepcjonista_jezyki",
            joinColumns = @JoinColumn(name = "recepcjonista_id"))
    @Column(name = "jezyk")
    private List<String> jezyki = new ArrayList<>();

    // Konstruktory
    public Recepcjonista() {
        super();
    }

    public Recepcjonista(String imie, String nazwisko, String telefon,
                         LocalDate dataUrodzenia, int numerRecepcji) {
        super(imie, nazwisko, telefon, dataUrodzenia);
        this.numerRecepcji = numerRecepcji;
    }

    // Getters i Setters
    public int getNumerRecepcji() { return numerRecepcji; }
    public void setNumerRecepcji(int numerRecepcji) { this.numerRecepcji = numerRecepcji; }

    public List<String> getJezyki() { return jezyki; }
    public void setJezyki(List<String> jezyki) { this.jezyki = jezyki; }

    // Metody pomocnicze
    public void dodajJezyk(String jezyk) {
        if (!jezyki.contains(jezyk)) {
            jezyki.add(jezyk);
        }
    }

    public void usunJezyk(String jezyk) {
        jezyki.remove(jezyk);
    }

    @Override
    public String getTypPracownika() {
        return "Recepcjonista";
    }
}

