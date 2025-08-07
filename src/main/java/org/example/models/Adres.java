package org.example.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class Adres {

    @NotBlank(message = "Ulica nie może być pusta")
    private String ulica;

    private int nrDomu;

    private Integer nrMieszkania; // nullable - może być null

    @NotBlank(message = "Kod pocztowy nie może być pusty")
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Kod pocztowy musi mieć format XX-XXX")
    private String kodPocztowy;

    @NotBlank(message = "Miasto nie może być puste")
    private String miasto;

    // Konstruktor bezparametrowy wymagany przez JPA
    public Adres() {}

    // Konstruktor z parametrami
    public Adres(String ulica, int nrDomu, Integer nrMieszkania,
                 String kodPocztowy, String miasto) {
        this.ulica = ulica;
        this.nrDomu = nrDomu;
        this.nrMieszkania = nrMieszkania;
        this.kodPocztowy = kodPocztowy;
        this.miasto = miasto;
    }

    // Gettery i Settery
    public String getUlica() { return ulica; }
    public void setUlica(String ulica) { this.ulica = ulica; }

    public int getNrDomu() { return nrDomu; }
    public void setNrDomu(int nrDomu) { this.nrDomu = nrDomu; }

    public Integer getNrMieszkania() { return nrMieszkania; }
    public void setNrMieszkania(Integer nrMieszkania) { this.nrMieszkania = nrMieszkania; }

    public String getKodPocztowy() { return kodPocztowy; }
    public void setKodPocztowy(String kodPocztowy) { this.kodPocztowy = kodPocztowy; }

    public String getMiasto() { return miasto; }
    public void setMiasto(String miasto) { this.miasto = miasto; }

    @Override
    public String toString() {
        return ulica + " " + nrDomu +
                (nrMieszkania != null ? "/" + nrMieszkania : "") +
                ", " + kodPocztowy + " " + miasto;
    }
}
