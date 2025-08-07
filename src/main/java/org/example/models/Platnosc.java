package org.example.models;


import org.example.models.enums.StatusPlatnosci;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "platnosc")
public class Platnosc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numerTransakcji;

    @NotNull(message = "Data płatności nie może być pusta")
    @Column(nullable = false)
    private LocalDate dataPlatnosci;

    @DecimalMin(value = "0.0", inclusive = false, message = "Kwota musi być większa od 0")
    @Column(nullable = false)
    private Double kwota;

    @NotBlank(message = "Metoda płatności nie może być pusta")
    private String metodaPlatnosci; // "karta", "gotówka", "przelew"

    @NotBlank(message = "Typ płatności nie może być pusty")
    private String typPlatnosci; // "zaliczka", "całość"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlatnosci statusPlatnosci = StatusPlatnosci.OCZEKUJACA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rezerwacja_id", nullable = false)
    private Rezerwacja rezerwacja;

    // Konstruktory
    public Platnosc() {
        this.numerTransakcji = generateNumerTransakcji();
        this.dataPlatnosci = LocalDate.now();
    }

    public Platnosc(Double kwota, String metodaPlatnosci, String typPlatnosci, Rezerwacja rezerwacja) {
        this();
        this.kwota = kwota;
        this.metodaPlatnosci = metodaPlatnosci;
        this.typPlatnosci = typPlatnosci;
        this.rezerwacja = rezerwacja;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumerTransakcji() { return numerTransakcji; }
    public void setNumerTransakcji(String numerTransakcji) { this.numerTransakcji = numerTransakcji; }

    public LocalDate getDataPlatnosci() { return dataPlatnosci; }
    public void setDataPlatnosci(LocalDate dataPlatnosci) { this.dataPlatnosci = dataPlatnosci; }

    public Double getKwota() { return kwota; }
    public void setKwota(Double kwota) { this.kwota = kwota; }

    public String getMetodaPlatnosci() { return metodaPlatnosci; }
    public void setMetodaPlatnosci(String metodaPlatnosci) { this.metodaPlatnosci = metodaPlatnosci; }

    public String getTypPlatnosci() { return typPlatnosci; }
    public void setTypPlatnosci(String typPlatnosci) { this.typPlatnosci = typPlatnosci; }

    public StatusPlatnosci getStatusPlatnosci() { return statusPlatnosci; }
    public void setStatusPlatnosci(StatusPlatnosci statusPlatnosci) { this.statusPlatnosci = statusPlatnosci; }

    public Rezerwacja getRezerwacja() { return rezerwacja; }
    public void setRezerwacja(Rezerwacja rezerwacja) { this.rezerwacja = rezerwacja; }

    // Metody biznesowe
    public void zrealizuj() {
        if (statusPlatnosci == StatusPlatnosci.OCZEKUJACA) {
            this.statusPlatnosci = StatusPlatnosci.ZREALIZOWANA;
        } else {
            throw new IllegalStateException("Nie można zrealizować płatności w stanie: " + statusPlatnosci);
        }
    }

    public void anuluj() {
        if (statusPlatnosci == StatusPlatnosci.OCZEKUJACA) {
            this.statusPlatnosci = StatusPlatnosci.ANULOWANA;
        } else {
            throw new IllegalStateException("Nie można anulować płatności w stanie: " + statusPlatnosci);
        }
    }

    private String generateNumerTransakcji() {
        return "TXN-" + System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platnosc platnosc = (Platnosc) o;
        return Objects.equals(id, platnosc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Platnosc{" +
                "numerTransakcji='" + numerTransakcji + '\'' +
                ", kwota=" + kwota +
                ", metodaPlatnosci='" + metodaPlatnosci + '\'' +
                ", statusPlatnosci=" + statusPlatnosci +
                '}';
    }
}

