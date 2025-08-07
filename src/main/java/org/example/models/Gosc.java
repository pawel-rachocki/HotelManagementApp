package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "gosc")
public class Gosc {

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

    @Email(message = "Email musi mieć prawidłowy format")
    @NotBlank(message = "Email nie może być pusty")
    @Column(unique = true)
    private String email;

    // walidacja daty urodzenia
    @Past(message = "Data urodzenia musi być z przeszłości")
    private LocalDate dataUrodzenia;

    @Embedded
    @Valid
    private Adres adres;

    private String typGoscia = "fizyczny"; // "fizyczny" z defaultu

    @OneToMany(mappedBy = "gosc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rezerwacja> rezerwacje = new ArrayList<>();

    // Konstruktory
    public Gosc() {}

    public Gosc(String imie, String nazwisko, String telefon, String email,
                LocalDate dataUrodzenia, Adres adres) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.telefon = telefon;
        this.email = email;
        this.dataUrodzenia = dataUrodzenia;
        this.adres = adres;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataUrodzenia() { return dataUrodzenia; }
    public void setDataUrodzenia(LocalDate dataUrodzenia) { this.dataUrodzenia = dataUrodzenia; }

    public Adres getAdres() { return adres; }
    public void setAdres(Adres adres) { this.adres = adres; }

    public String getTypGoscia() { return typGoscia; }
    public void setTypGoscia(String typGoscia) { this.typGoscia = typGoscia; }

    public List<Rezerwacja> getRezerwacje() { return rezerwacje; }
    public void setRezerwacje(List<Rezerwacja> rezerwacje) { this.rezerwacje = rezerwacje; }

    // Metody pomocnicze
    public String getPelneImieNazwisko() {
        return imie + " " + nazwisko;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gosc gosc = (Gosc) o;
        return Objects.equals(id, gosc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Gosc{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
