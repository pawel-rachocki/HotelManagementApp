package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "rachunek")
public class Rachunek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numerRachunku;

    @Column(nullable = false)
    private LocalDate dataWystawienia = LocalDate.now();

    @DecimalMin(value = "0.0", message = "Kwota razem nie może być ujemna")
    @Column(nullable = false)
    private Double kwotaRazem = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rezerwacja_id", nullable = false)
    private Rezerwacja rezerwacja;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rachunek_uslugi",
            joinColumns = @JoinColumn(name = "rachunek_id"),
            inverseJoinColumns = @JoinColumn(name = "usluga_id")
    )
    private List<UslugaDodatkowa> uslugi = new ArrayList<>();

    // Konstruktory
    public Rachunek() {
        this.numerRachunku = generateNumerRachunku();
    }

    public Rachunek(Rezerwacja rezerwacja) {
        this();
        this.rezerwacja = rezerwacja;
        obliczKwoteRazem();
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumerRachunku() { return numerRachunku; }
    public void setNumerRachunku(String numerRachunku) { this.numerRachunku = numerRachunku; }

    public LocalDate getDataWystawienia() { return dataWystawienia; }
    public void setDataWystawienia(LocalDate dataWystawienia) { this.dataWystawienia = dataWystawienia; }

    public Double getKwotaRazem() { return kwotaRazem; }
    public void setKwotaRazem(Double kwotaRazem) { this.kwotaRazem = kwotaRazem; }

    public Rezerwacja getRezerwacja() { return rezerwacja; }
    public void setRezerwacja(Rezerwacja rezerwacja) { this.rezerwacja = rezerwacja; }

    public List<UslugaDodatkowa> getUslugi() { return uslugi; }
    public void setUslugi(List<UslugaDodatkowa> uslugi) { this.uslugi = uslugi; }

    // Metody biznesowe
    public void obliczKwoteRazem() {
        double suma = 0.0;

        // Dodaj koszt pobytu
        if (rezerwacja != null) {
            suma += rezerwacja.obliczKosztPobytu();

            // Dodaj koszty usług dodatkowych
            for (ZamowienieUslugi zamowienie : rezerwacja.getZamowieniaUslug()) {
                suma += zamowienie.getCenaRazem();
            }
        }

        this.kwotaRazem = suma;
    }

    public void dodajUsluge(UslugaDodatkowa usluga) {
        if (!uslugi.contains(usluga)) {
            uslugi.add(usluga);
            obliczKwoteRazem();
        }
    }

    public void usunUsluge(UslugaDodatkowa usluga) {
        if (uslugi.remove(usluga)) {
            obliczKwoteRazem();
        }
    }

    private String generateNumerRachunku() {
        return "RACH-" + System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rachunek rachunek = (Rachunek) o;
        return Objects.equals(id, rachunek.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rachunek{" +
                "numerRachunku='" + numerRachunku + '\'' +
                ", dataWystawienia=" + dataWystawienia +
                ", kwotaRazem=" + kwotaRazem +
                '}';
    }
}

