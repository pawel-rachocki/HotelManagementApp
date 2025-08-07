package org.example.dao;

import org.example.dao.BaseDAO;
import org.example.models.UslugaDodatkowa;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UslugaDAOImpl extends BaseDAOImpl<UslugaDodatkowa> {

    public UslugaDAOImpl() {
        super(UslugaDodatkowa.class);
    }

    // Wyszukuje usługi dodatkowe według kategorii.
    public List<UslugaDodatkowa> findByKategoria(String kategoria) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<UslugaDodatkowa> query = session.createQuery(
                    "FROM UslugaDodatkowa u WHERE u.kategoria = :kategoria", UslugaDodatkowa.class);
            query.setParameter("kategoria", kategoria);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania usług po kategorii: " + e.getMessage(), e);
        }
    }
}

