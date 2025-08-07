package org.example.models;


import org.example.models.enums.StatusRezerwacji;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "rezerwacja")
public class Rezerwacja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numerRezerwacji;

    @Column(nullable = false)
    private LocalDate dataUtworzenia = LocalDate.now();

    @NotNull(message = "Data przyjazdu nie może być pusta")
    @Future(message = "Data przyjazdu musi być z przyszłości")
    @Column(nullable = false)
    private LocalDate dataPrzyjazdu;

    @NotNull(message = "Data wyjazdu nie może być pusta")
    @Column(nullable = false)
    private LocalDate dataWyjazdu;

    @Min(value = 1, message = "Liczba gości musi być większa od 0")
    @Column(nullable = false)
    private int liczbaGosci;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRezerwacji statusRezerwacji = StatusRezerwacji.UTWORZONA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gosc_id", nullable = false)
    private Gosc gosc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokoj_id", nullable = false)
    private Pokoj pokoj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pracownik_id", nullable = false)
    private Pracownik pracownikTworzacy;

    @OneToMany(mappedBy = "rezerwacja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ZamowienieUslugi> zamowieniaUslug = new ArrayList<>();

    @OneToMany(mappedBy = "rezerwacja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Platnosc> platnosci = new ArrayList<>();

    @OneToOne(mappedBy = "rezerwacja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Rachunek rachunek;

    // Konstruktory
    public Rezerwacja() {
        this.numerRezerwacji = generateNumerRezerwacji();
    }

    public Rezerwacja(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu,
                      int liczbaGosci, Gosc gosc, Pokoj pokoj, Pracownik pracownikTworzacy) {
        this();
        this.dataPrzyjazdu = dataPrzyjazdu;
        this.dataWyjazdu = dataWyjazdu;
        this.liczbaGosci = liczbaGosci;
        this.gosc = gosc;
        this.pokoj = pokoj;
        this.pracownikTworzacy = pracownikTworzacy;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumerRezerwacji() { return numerRezerwacji; }
    public void setNumerRezerwacji(String numerRezerwacji) { this.numerRezerwacji = numerRezerwacji; }

    public LocalDate getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(LocalDate dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }

    public LocalDate getDataPrzyjazdu() { return dataPrzyjazdu; }
    public void setDataPrzyjazdu(LocalDate dataPrzyjazdu) { this.dataPrzyjazdu = dataPrzyjazdu; }

    public LocalDate getDataWyjazdu() { return dataWyjazdu; }
    public void setDataWyjazdu(LocalDate dataWyjazdu) { this.dataWyjazdu = dataWyjazdu; }

    public int getLiczbaGosci() { return liczbaGosci; }
    public void setLiczbaGosci(int liczbaGosci) { this.liczbaGosci = liczbaGosci; }

    public StatusRezerwacji getStatusRezerwacji() { return statusRezerwacji; }
    public void setStatusRezerwacji(StatusRezerwacji statusRezerwacji) { this.statusRezerwacji = statusRezerwacji; }

    public Gosc getGosc() { return gosc; }
    public void setGosc(Gosc gosc) { this.gosc = gosc; }

    public Pokoj getPokoj() { return pokoj; }
    public void setPokoj(Pokoj pokoj) { this.pokoj = pokoj; }

    public Pracownik getPracownikTworzacy() { return pracownikTworzacy; }
    public void setPracownikTworzacy(Pracownik pracownikTworzacy) { this.pracownikTworzacy = pracownikTworzacy; }

    public List<ZamowienieUslugi> getZamowieniaUslug() { return zamowieniaUslug; }
    public void setZamowieniaUslug(List<ZamowienieUslugi> zamowieniaUslug) { this.zamowieniaUslug = zamowieniaUslug; }

    public List<Platnosc> getPlatnosci() { return platnosci; }
    public void setPlatnosci(List<Platnosc> platnosci) { this.platnosci = platnosci; }

    public Rachunek getRachunek() { return rachunek; }
    public void setRachunek(Rachunek rachunek) { this.rachunek = rachunek; }

    // Metody biznesowe zgodnie z diagramem stanu
    public void potwierdz() {
        if (statusRezerwacji == StatusRezerwacji.UTWORZONA) {
            this.statusRezerwacji = StatusRezerwacji.POTWIERDZONA;
        } else {
            throw new IllegalStateException("Nie można potwierdzić rezerwacji w stanie: " + statusRezerwacji);
        }
    }

    public void rozpocznijPobyt() {
        if (statusRezerwacji == StatusRezerwacji.POTWIERDZONA) {
            this.statusRezerwacji = StatusRezerwacji.W_TRAKCIE;
            if (pokoj != null) {
                pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.ZAJETY);
            }
        } else {
            throw new IllegalStateException("Nie można rozpocząć pobytu dla rezerwacji w stanie: " + statusRezerwacji);
        }
    }

    public void zakonczPobyt() {
        if (statusRezerwacji == StatusRezerwacji.W_TRAKCIE) {
            this.statusRezerwacji = StatusRezerwacji.ZAKONCZONA;
            if (pokoj != null) {
                pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.W_SPRZATANIU);
            }
        } else {
            throw new IllegalStateException("Nie można zakończyć pobytu dla rezerwacji w stanie: " + statusRezerwacji);
        }
    }

    public void anuluj() {
        if (statusRezerwacji == StatusRezerwacji.UTWORZONA ||
                statusRezerwacji == StatusRezerwacji.POTWIERDZONA) {

            // Sprawdź czy anulowanie jest możliwe (>24h przed przyjazdem)
            if (dataPrzyjazdu.minusDays(1).isAfter(LocalDate.now())) {
                this.statusRezerwacji = StatusRezerwacji.ANULOWANA;
                if (pokoj != null) {
                    pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.DOSTEPNY);
                }
            } else {
                throw new IllegalStateException("Nie można anulować rezerwacji mniej niż 24h przed przyjazdem");
            }
        } else {
            throw new IllegalStateException("Nie można anulować rezerwacji w stanie: " + statusRezerwacji);
        }
    }

    public void archiwizuj() {
        if (statusRezerwacji == StatusRezerwacji.ANULOWANA) {
            this.statusRezerwacji = StatusRezerwacji.ZARCHIWIZOWANA;
        } else {
            throw new IllegalStateException("Można archiwizować tylko anulowane rezerwacje");
        }
    }

    // Metody pomocnicze
    public void aktualizujDostepnosc() {
        if (pokoj != null) {
            switch (statusRezerwacji) {
                case UTWORZONA, POTWIERDZONA -> pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.DOSTEPNY);
                case W_TRAKCIE -> pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.ZAJETY);
                case ZAKONCZONA -> pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.W_SPRZATANIU);
                case ANULOWANA, ZARCHIWIZOWANA -> pokoj.setStatusPokoju(org.example.models.enums.StatusPokoju.DOSTEPNY);
            }
        }
    }

    public long getLiczbaDni() {
        return dataPrzyjazdu.until(dataWyjazdu).getDays();
    }

    public double obliczKosztPobytu() {
        if (pokoj != null) {
            return getLiczbaDni() * pokoj.getCenaZaDobe();
        }
        return 0.0;
    }

    private String generateNumerRezerwacji() {
        return "REZ-" + System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rezerwacja that = (Rezerwacja) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rezerwacja{" +
                "numerRezerwacji='" + numerRezerwacji + '\'' +
                ", dataPrzyjazdu=" + dataPrzyjazdu +
                ", dataWyjazdu=" + dataWyjazdu +
                ", statusRezerwacji=" + statusRezerwacji +
                '}';
    }
}

