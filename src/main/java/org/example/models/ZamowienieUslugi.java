package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "zamowienie_uslugi")
public class ZamowienieUslugi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numerZamowienia;

    @Column(nullable = false)
    private LocalDate dataZamowienia = LocalDate.now();

    @Min(value = 1, message = "Ilość musi być większa od 0")
    @Column(nullable = false)
    private int ilosc = 1;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cena jednostkowa musi być większa od 0")
    @Column(nullable = false)
    private Double cenaJednostkowa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rezerwacja_id", nullable = false)
    private Rezerwacja rezerwacja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usluga_id", nullable = false)
    private UslugaDodatkowa uslugaDodatkowa;

    // Konstruktory
    public ZamowienieUslugi() {
        this.numerZamowienia = generateNumerZamowienia();
    }

    public ZamowienieUslugi(int ilosc, Double cenaJednostkowa,
                            Rezerwacja rezerwacja, UslugaDodatkowa uslugaDodatkowa) {
        this();
        this.ilosc = ilosc;
        this.cenaJednostkowa = cenaJednostkowa;
        this.rezerwacja = rezerwacja;
        this.uslugaDodatkowa = uslugaDodatkowa;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumerZamowienia() { return numerZamowienia; }
    public void setNumerZamowienia(String numerZamowienia) { this.numerZamowienia = numerZamowienia; }

    public LocalDate getDataZamowienia() { return dataZamowienia; }
    public void setDataZamowienia(LocalDate dataZamowienia) { this.dataZamowienia = dataZamowienia; }

    public int getIlosc() { return ilosc; }
    public void setIlosc(int ilosc) { this.ilosc = ilosc; }

    public Double getCenaJednostkowa() { return cenaJednostkowa; }
    public void setCenaJednostkowa(Double cenaJednostkowa) { this.cenaJednostkowa = cenaJednostkowa; }

    public Rezerwacja getRezerwacja() { return rezerwacja; }
    public void setRezerwacja(Rezerwacja rezerwacja) { this.rezerwacja = rezerwacja; }

    public UslugaDodatkowa getUslugaDodatkowa() { return uslugaDodatkowa; }
    public void setUslugaDodatkowa(UslugaDodatkowa uslugaDodatkowa) { this.uslugaDodatkowa = uslugaDodatkowa; }

    // Metody biznesowe
    public Double getCenaRazem() {
        return cenaJednostkowa * ilosc;
    }

    private String generateNumerZamowienia() {
        return "ZAM-" + System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZamowienieUslugi that = (ZamowienieUslugi) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ZamowienieUslugi{" +
                "numerZamowienia='" + numerZamowienia + '\'' +
                ", ilosc=" + ilosc +
                ", cenaRazem=" + getCenaRazem() +
                '}';
    }
}

