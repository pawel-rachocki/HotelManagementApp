package org.example.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "usluga_dodatkowa")
public class UslugaDodatkowa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa usługi nie może być pusta")
    @Column(nullable = false)
    private String nazwaUslugi;

    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String opis;

    @DecimalMin(value = "0.0", inclusive = false, message = "Cena musi być większa od 0")
    @Column(nullable = false)
    private Double cena;

    @NotBlank(message = "Kategoria nie może być pusta")
    private String kategoria; // "wyżywienie", "wellness", "transport", "inne"

    @OneToMany(mappedBy = "uslugaDodatkowa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ZamowienieUslugi> zamowienia = new ArrayList<>();

    // Konstruktory
    public UslugaDodatkowa() {}

    public UslugaDodatkowa(String nazwaUslugi, String opis, Double cena, String kategoria) {
        this.nazwaUslugi = nazwaUslugi;
        this.opis = opis;
        this.cena = cena;
        this.kategoria = kategoria;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNazwaUslugi() { return nazwaUslugi; }
    public void setNazwaUslugi(String nazwaUslugi) { this.nazwaUslugi = nazwaUslugi; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public Double getCena() { return cena; }
    public void setCena(Double cena) { this.cena = cena; }

    public String getKategoria() { return kategoria; }
    public void setKategoria(String kategoria) { this.kategoria = kategoria; }

    public List<ZamowienieUslugi> getZamowienia() { return zamowienia; }
    public void setZamowienia(List<ZamowienieUslugi> zamowienia) { this.zamowienia = zamowienia; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UslugaDodatkowa that = (UslugaDodatkowa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UslugaDodatkowa{" +
                "nazwaUslugi='" + nazwaUslugi + '\'' +
                ", cena=" + cena +
                ", kategoria='" + kategoria + '\'' +
                '}';
    }
}

