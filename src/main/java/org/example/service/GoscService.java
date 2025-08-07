package org.example.service;


import org.example.dao.GoscDAOImpl;
import org.example.models.Adres;
import org.example.models.Gosc;
import java.time.LocalDate;
import java.util.List;

public class GoscService {

    private final GoscDAOImpl goscDAO;

    public GoscService() {
        this.goscDAO = new GoscDAOImpl();
    }

    // Metoda potrzebna dla MainController
    public List<Gosc> getAllGoscie() {
        return goscDAO.findAll();
    }

    // 3A : rejestracja nowego gościa
    public Gosc zarejestrujNowegoGoscia(String imie, String nazwisko, String telefon,
                                        String email, LocalDate dataUrodzenia, Adres adres) {

        // Sprawdź czy gość już istnieje
        if (goscDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Gość o podanym emailu już istnieje");
        }

        // Utwórz nowego gościa
        Gosc nowyGosc = new Gosc(imie, nazwisko, telefon, email, dataUrodzenia, adres);
        goscDAO.save(nowyGosc);

        return nowyGosc;
    }
}


