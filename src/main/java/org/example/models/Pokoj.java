package org.example.models;


import org.example.models.enums.StatusPokoju;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pokoj")
public class Pokoj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private int numerPokoju;

    @NotNull(message = "Typ pokoju nie może być pusty")
    private String typPokoju; // "standard", "apartament"

    @Min(value = 1, message = "Liczba łóżek musi być większa od 0")
    private int liczbaLozek;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musi być większa od 0")
    @Column(nullable = false)
    private Double cenaZaDobe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPokoju statusPokoju = StatusPokoju.DOSTEPNY;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pokoj_udogodnienia",
            joinColumns = @JoinColumn(name = "pokoj_id"))
    @Column(name = "udogodnienie")
    private List<String> udogodnienia = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "pokoj", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rezerwacja> rezerwacje = new ArrayList<>();

    // Konstruktory
    public Pokoj() {}

    public Pokoj(int numerPokoju, String typPokoju, int liczbaLozek,
                 Double cenaZaDobe, Hotel hotel) {
        this.numerPokoju = numerPokoju;
        this.typPokoju = typPokoju;
        this.liczbaLozek = liczbaLozek;
        this.cenaZaDobe = cenaZaDobe;
        this.hotel = hotel;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumerPokoju() { return numerPokoju; }
    public void setNumerPokoju(int numerPokoju) { this.numerPokoju = numerPokoju; }

    public String getTypPokoju() { return typPokoju; }
    public void setTypPokoju(String typPokoju) { this.typPokoju = typPokoju; }

    public int getLiczbaLozek() { return liczbaLozek; }
    public void setLiczbaLozek(int liczbaLozek) { this.liczbaLozek = liczbaLozek; }

    public Double getCenaZaDobe() { return cenaZaDobe; }
    public void setCenaZaDobe(Double cenaZaDobe) { this.cenaZaDobe = cenaZaDobe; }

    public StatusPokoju getStatusPokoju() { return statusPokoju; }
    public void setStatusPokoju(StatusPokoju statusPokoju) { this.statusPokoju = statusPokoju; }

    public List<String> getUdogodnienia() { return udogodnienia; }
    public void setUdogodnienia(List<String> udogodnienia) { this.udogodnienia = udogodnienia; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public List<Rezerwacja> getRezerwacje() { return rezerwacje; }
    public void setRezerwacje(List<Rezerwacja> rezerwacje) { this.rezerwacje = rezerwacje; }

    // Metody pomocnicze
    public void dodajUdogodnienie(String udogodnienie) {
        if (!udogodnienia.contains(udogodnienie)) {
            udogodnienia.add(udogodnienie);
        }
    }

    public void usunUdogodnienie(String udogodnienie) {
        udogodnienia.remove(udogodnienie);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokoj pokoj = (Pokoj) o;
        return Objects.equals(id, pokoj.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pokoj{" +
                "id=" + id +
                ", numerPokoju=" + numerPokoju +
                ", typPokoju='" + typPokoju + '\'' +
                ", liczbaLozek=" + liczbaLozek +
                ", cenaZaDobe=" + cenaZaDobe +
                ", statusPokoju=" + statusPokoju +
                '}';
    }
}
