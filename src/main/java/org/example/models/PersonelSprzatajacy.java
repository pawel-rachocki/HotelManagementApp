package org.example.models;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "personel_sprzatajacy")
@DiscriminatorValue("PERSONEL_SPRZATAJACY")
public class PersonelSprzatajacy extends Pracownik {

    // Konstruktory
    public PersonelSprzatajacy() {
        super();
    }

    public PersonelSprzatajacy(String imie, String nazwisko, String telefon, LocalDate dataUrodzenia) {
        super(imie, nazwisko, telefon, dataUrodzenia);
    }

    @Override
    public String getTypPracownika() {
        return "Personel Sprzątający";
    }
}

