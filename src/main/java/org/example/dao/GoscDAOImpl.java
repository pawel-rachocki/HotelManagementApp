package org.example.dao;


import org.example.dao.GoscDAO;
import org.example.models.Gosc;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class GoscDAOImpl extends BaseDAOImpl<Gosc> implements GoscDAO {

    public GoscDAOImpl() {
        super(Gosc.class);
    }

    // Query do wyszukiwania Gościa po emailu
    @Override
    public Optional<Gosc> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Gosc> query = session.createQuery(
                    "FROM Gosc g WHERE g.email = :email", Gosc.class);
            query.setParameter("email", email);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania gościa po email: " + e.getMessage(), e);
        }
    }

    // Query do wyszukiwania Gościa po nazwisku
    @Override
    public List<Gosc> findByNazwisko(String nazwisko) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Gosc> query = session.createQuery(
                    "FROM Gosc g WHERE LOWER(g.nazwisko) LIKE LOWER(:nazwisko)", Gosc.class);
            query.setParameter("nazwisko", "%" + nazwisko + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania gościa po nazwisku: " + e.getMessage(), e);
        }
    }

    // Sprawdzenie czy istnieje taki gośc po emailu
    @Override
    public boolean existsByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(g) FROM Gosc g WHERE g.email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas sprawdzania istnienia gościa: " + e.getMessage(), e);
        }
    }
}

