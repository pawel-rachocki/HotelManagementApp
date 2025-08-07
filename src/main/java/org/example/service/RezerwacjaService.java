package org.example.service;


import org.example.dao.GoscDAOImpl;
import org.example.dao.PokojDAOImpl;
import org.example.dao.RezerwacjaDAOImpl;
import org.example.models.*;
import org.example.models.enums.StatusRezerwacji;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RezerwacjaService {

    private final GoscDAOImpl goscDAO;
    private final PokojDAOImpl pokojDAO;
    private final RezerwacjaDAOImpl rezerwacjaDAO;

    public RezerwacjaService() {
        this.goscDAO = new GoscDAOImpl();
        this.pokojDAO = new PokojDAOImpl();
        this.rezerwacjaDAO = new RezerwacjaDAOImpl();
    }

    // Krok 4 scenariusza: wyszukanie gościa w bazie
    public Optional<Gosc> wyszukajGoscia(String emailLubNazwisko) {
        if (emailLubNazwisko.contains("@")) {
            return goscDAO.findByEmail(emailLubNazwisko);
        } else {
            List<Gosc> goscie = goscDAO.findByNazwisko(emailLubNazwisko);
            return goscie.isEmpty() ? Optional.empty() : Optional.of(goscie.get(0));
        }
    }

    // 7 walidacja danych rezerwacji
    public void walidujDaneRezerwacji(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu, int liczbaGosci) {
        if (dataPrzyjazdu.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data przyjazdu musi być >= dzisiaj");
        }
        if (dataWyjazdu.isBefore(dataPrzyjazdu) || dataWyjazdu.equals(dataPrzyjazdu)) {
            throw new IllegalArgumentException("Data wyjazdu musi być > data przyjazdu");
        }
        if (liczbaGosci <= 0) {
            throw new IllegalArgumentException("Liczba gości musi być > 0");
        }
    }

    // 8 sprawdzenie dostępności pokoi
    public List<Pokoj> sprawdzDostepnoscPokoi(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu) {
        return pokojDAO.findDostepnePokoje(dataPrzyjazdu, dataWyjazdu);
    }

    // 15 utworzenie rezerwacji
    public Rezerwacja utworzRezerwacje(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu,
                                       int liczbaGosci, Gosc gosc, Pokoj pokoj,
                                       Pracownik pracownik) {

        // Walidacja danych
        walidujDaneRezerwacji(dataPrzyjazdu, dataWyjazdu, liczbaGosci);

        // Sprawdź czy pokój jest nadal dostępny
        List<Pokoj> dostepnePokoje = sprawdzDostepnoscPokoi(dataPrzyjazdu, dataWyjazdu);
        if (!dostepnePokoje.contains(pokoj)) {
            throw new IllegalStateException("Pokój nie jest już dostępny w wybranym terminie");
        }

        // Utwórz rezerwację
        Rezerwacja rezerwacja = new Rezerwacja(dataPrzyjazdu, dataWyjazdu,
                liczbaGosci, gosc, pokoj, pracownik);
        rezerwacja.setStatusRezerwacji(StatusRezerwacji.UTWORZONA);

        // Zapisz w bazie
        rezerwacjaDAO.save(rezerwacja);

        return rezerwacja;
    }
    // Dodaj do RezerwacjaService
    public List<Rezerwacja> getRezerwacjeGoscia(Long goscId) {
        return rezerwacjaDAO.findByGosc(goscId);
    }


    // Obliczenie całkowitego kosztu (pokój + usługi)
    public double obliczCalkowitaKwote(Rezerwacja rezerwacja, List<UslugaDodatkowa> uslugi) {
        double kosztPobytu = rezerwacja.obliczKosztPobytu();
        double kosztUslug = uslugi.stream().mapToDouble(UslugaDodatkowa::getCena).sum();
        return kosztPobytu + kosztUslug;
    }

    public Rezerwacja getRezerwacjaWithDetails(Long rezerwacjaId) {
        return rezerwacjaDAO.findByIdWithAssociations(rezerwacjaId);
    }

}

