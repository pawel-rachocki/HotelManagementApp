package org.example.service;



import org.example.dao.PracownikDAOImpl;
import org.example.models.Recepcjonista;
import org.example.models.Pracownik;
import java.util.List;

public class PracownikService {

    private final PracownikDAOImpl pracownikDAO;

    public PracownikService() {
        this.pracownikDAO = new PracownikDAOImpl();
    }


    // na potrzeby testowania przypadu użycia znajdowanie pierwszego recepcjionisty na liście
    public Recepcjonista getFirstRecepcjonista() {
        List<Pracownik> pracownicy = pracownikDAO.findAll();
        return pracownicy.stream()
                .filter(p -> p instanceof Recepcjonista)
                .map(p -> (Recepcjonista) p)
                .findFirst()
                .orElse(null);
    }
}
