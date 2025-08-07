package org.example.service;

import org.example.dao.UslugaDAOImpl;
import org.example.models.UslugaDodatkowa;
import java.util.List;

public class UslugaService {

    private final UslugaDAOImpl uslugaDAO;

    public UslugaService() {
        this.uslugaDAO = new UslugaDAOImpl();
    }

    public List<UslugaDodatkowa> getAllUslugi() {
        return uslugaDAO.findAll();
    }

    public List<UslugaDodatkowa> getUslugByKategoria(String kategoria) {
        return uslugaDAO.findByKategoria(kategoria);
    }

    public UslugaDodatkowa dodajNowaUsluge(String nazwaUslugi, String opis,
                                           Double cena, String kategoria) {
        UslugaDodatkowa usluga = new UslugaDodatkowa(nazwaUslugi, opis, cena, kategoria);
        uslugaDAO.save(usluga);
        return usluga;
    }
}
