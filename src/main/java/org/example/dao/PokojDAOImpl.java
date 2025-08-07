package org.example.dao;


import org.example.dao.PokojDAO;
import org.example.models.Pokoj;
import org.example.models.enums.StatusPokoju;
import org.example.models.enums.StatusRezerwacji;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PokojDAOImpl extends BaseDAOImpl<Pokoj> implements PokojDAO {

    public PokojDAOImpl() {
        super(Pokoj.class);
    }

    //Query do zwracania dostępnych pokoi ( niezarezerowanych)
    @Override
    public List<Pokoj> findDostepnePokoje(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Pokoj> query = session.createQuery(
                    "FROM Pokoj p WHERE p.id NOT IN " +
                            "(SELECT r.pokoj.id FROM Rezerwacja r WHERE " +
                            "(r.dataPrzyjazdu < :dataWyjazdu AND r.dataWyjazdu > :dataPrzyjazdu) " +
                            "AND r.statusRezerwacji IN (:statusy))",
                    Pokoj.class);

            query.setParameter("dataPrzyjazdu", dataPrzyjazdu);
            query.setParameter("dataWyjazdu", dataWyjazdu);


            query.setParameter("statusy", Arrays.asList(
                    StatusRezerwacji.UTWORZONA,
                    StatusRezerwacji.POTWIERDZONA,
                    StatusRezerwacji.W_TRAKCIE
            ));

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas sprawdzania dostępności pokoi: " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<Pokoj> findByNumerPokoju(int numerPokoju) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Pokoj> query = session.createQuery(
                    "FROM Pokoj p WHERE p.numerPokoju = :numerPokoju", Pokoj.class);
            query.setParameter("numerPokoju", numerPokoju);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public List<Pokoj> findByStatus(StatusPokoju status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Pokoj> query = session.createQuery(
                    "FROM Pokoj p WHERE p.statusPokoju = :status", Pokoj.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    @Override
    public List<Pokoj> findByTypPokoju(String typPokoju) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Pokoj> query = session.createQuery(
                    "FROM Pokoj p WHERE p.typPokoju = :typPokoju", Pokoj.class);
            query.setParameter("typPokoju", typPokoju);
            return query.getResultList();
        }
    }
}

