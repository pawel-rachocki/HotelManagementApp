package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa hotelu nie może być pusta")
    @Column(nullable = false)
    private String nazwaHotelu;

    @Embedded
    @Valid
    private Adres adres;

    @NotBlank(message = "Telefon nie może być pusty")
    private String telefon;

    @Email(message = "Email musi mieć prawidłowy format")
    @NotBlank(message = "Email nie może być pusty")
    private String email;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pokoj> pokoje = new ArrayList<>();

    // Konstruktory
    public Hotel() {}

    public Hotel(String nazwaHotelu, Adres adres, String telefon, String email) {
        this.nazwaHotelu = nazwaHotelu;
        this.adres = adres;
        this.telefon = telefon;
        this.email = email;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNazwaHotelu() { return nazwaHotelu; }
    public void setNazwaHotelu(String nazwaHotelu) { this.nazwaHotelu = nazwaHotelu; }

    public Adres getAdres() { return adres; }
    public void setAdres(Adres adres) { this.adres = adres; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Pokoj> getPokoje() { return pokoje; }
    public void setPokoje(List<Pokoj> pokoje) { this.pokoje = pokoje; }

    // Metody pomocnicze
    public void dodajPokoj(Pokoj pokoj) {
        pokoje.add(pokoj);
        pokoj.setHotel(this);
    }

    public void usunPokoj(Pokoj pokoj) {
        pokoje.remove(pokoj);
        pokoj.setHotel(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", nazwaHotelu='" + nazwaHotelu + '\'' +
                ", adres=" + adres +
                ", telefon='" + telefon + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

