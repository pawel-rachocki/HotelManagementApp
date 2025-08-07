package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pracownik")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "typ_pracownika", discriminatorType = DiscriminatorType.STRING)
public abstract class Pracownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Imię nie może być puste")
    @Column(nullable = false)
    private String imie;

    @NotBlank(message = "Nazwisko nie może być puste")
    @Column(nullable = false)
    private String nazwisko;

    @NotBlank(message = "Telefon nie może być pusty")
    private String telefon;

    @Past(message = "Data urodzenia musi być z przeszłości")
    private LocalDate dataUrodzenia;

    @Column(nullable = false)
    private LocalDate dataZatrudnienia = LocalDate.now();

    @OneToMany(mappedBy = "pracownikTworzacy", fetch = FetchType.LAZY)
    private List<Rezerwacja> utworzoneRezerwacje = new ArrayList<>();

    // Konstruktory
    public Pracownik() {}

    public Pracownik(String imie, String nazwisko, String telefon, LocalDate dataUrodzenia) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.telefon = telefon;
        this.dataUrodzenia = dataUrodzenia;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public LocalDate getDataUrodzenia() { return dataUrodzenia; }
    public void setDataUrodzenia(LocalDate dataUrodzenia) { this.dataUrodzenia = dataUrodzenia; }

    public LocalDate getDataZatrudnienia() { return dataZatrudnienia; }
    public void setDataZatrudnienia(LocalDate dataZatrudnienia) { this.dataZatrudnienia = dataZatrudnienia; }

    public List<Rezerwacja> getUtworzoneRezerwacje() { return utworzoneRezerwacje; }
    public void setUtworzoneRezerwacje(List<Rezerwacja> utworzoneRezerwacje) { this.utworzoneRezerwacje = utworzoneRezerwacje; }

    // Metody pomocnicze
    public String getPelneImieNazwisko() {
        return imie + " " + nazwisko;
    }

    public abstract String getTypPracownika();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pracownik pracownik = (Pracownik) o;
        return Objects.equals(id, pracownik.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getTypPracownika() + "{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                '}';
    }
}

