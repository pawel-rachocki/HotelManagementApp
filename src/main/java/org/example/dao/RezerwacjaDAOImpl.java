package org.example.dao;


import org.example.dao.RezerwacjaDAO;
import org.example.models.Rezerwacja;
import org.example.models.enums.StatusRezerwacji;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja DAO dla encji Rezerwacja - główna klasa dostępu do danych rezerwacji.
 * Rozszerza BaseDAOImpl
 * Implementuje funkcjonalności z dokumentacji: sprawdzanie dostępności, archiwizowanie, zarządzanie statusami.
 */
public class RezerwacjaDAOImpl extends BaseDAOImpl<Rezerwacja> implements RezerwacjaDAO {

    public RezerwacjaDAOImpl() {
        super(Rezerwacja.class);
    }


    /**
     * Wyszukuje rezerwację po unikalnym numerze rezerwacji
     */
    @Override
    public Optional<Rezerwacja> findByNumerRezerwacji(String numerRezerwacji) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.numerRezerwacji = :numerRezerwacji", Rezerwacja.class);
            query.setParameter("numerRezerwacji", numerRezerwacji);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji po numerze: " + e.getMessage(), e);
        }
    }

    // Pobiera wszystkie rezerwacdje gościa + sortowanie DESC
    @Override
    public List<Rezerwacja> findByGosc(Long goscId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.gosc.id = :goscId ORDER BY r.dataUtworzenia DESC", Rezerwacja.class);
            query.setParameter("goscId", goscId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji gościa: " + e.getMessage(), e);
        }
    }


    /**
     * Filtruje rezerwacje według statusu
     */
    @Override
    public List<Rezerwacja> findByStatus(StatusRezerwacji status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.statusRezerwacji = :status", Rezerwacja.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji po statusie: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Rezerwacja> findByDateRange(LocalDate dataOd, LocalDate dataDo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.dataPrzyjazdu >= :dataOd AND r.dataWyjazdu <= :dataDo " +
                            "ORDER BY r.dataPrzyjazdu", Rezerwacja.class);
            query.setParameter("dataOd", dataOd);
            query.setParameter("dataDo", dataDo);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji w przedziale dat: " + e.getMessage(), e);
        }
    }

    /**
     * Implementuje wymaganie 17: automatyczne archiwizowanie anulowanych rezerwacji po 90 dniach.
     */
    @Override
    public List<Rezerwacja> findAnulowaneDoArchiwizacji() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Automatyczne archiwizowanie po 90 dniach od anulowania
            LocalDate dataGraniczna = LocalDate.now().minusDays(90);

            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.statusRezerwacji = :statusAnulowana " +
                            "AND r.dataUtworzenia <= :dataGraniczna", Rezerwacja.class);
            query.setParameter("statusAnulowana", StatusRezerwacji.ANULOWANA);
            query.setParameter("dataGraniczna", dataGraniczna);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji do archiwizacji: " + e.getMessage(), e);
        }
    }

    // Dodatkowe metody pomocnicze

    public List<Rezerwacja> findAktywnePrzezGosc(Long goscId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.gosc.id = :goscId " +
                            "AND r.statusRezerwacji IN (:statusy) " +
                            "ORDER BY r.dataPrzyjazdu", Rezerwacja.class);
            query.setParameter("goscId", goscId);
            query.setParameter("statusy", Arrays.asList(
                    StatusRezerwacji.UTWORZONA,
                    StatusRezerwacji.POTWIERDZONA,
                    StatusRezerwacji.W_TRAKCIE
            ));
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania aktywnych rezerwacji gościa: " + e.getMessage(), e);
        }
    }

    public List<Rezerwacja> findRezerwacjeNaDzisiaj() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate dzisiaj = LocalDate.now();
            Query<Rezerwacja> query = session.createQuery(
                    "FROM Rezerwacja r WHERE r.dataPrzyjazdu = :dzisiaj " +
                            "AND r.statusRezerwacji = :statusPotwierdzona", Rezerwacja.class);
            query.setParameter("dzisiaj", dzisiaj);
            query.setParameter("statusPotwierdzona", StatusRezerwacji.POTWIERDZONA);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania rezerwacji na dzisiaj: " + e.getMessage(), e);
        }
    }
    /**
     * Sprawdza kolizje terminów
     * Zapobiega podwójnym rezerwacjom tego samego pokoju.
     */
    public boolean sprawdzKolizjeTerminow(Long pokojId, LocalDate dataPrzyjazdu,
                                          LocalDate dataWyjazdu, Long wykluczRezerwacjeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM Rezerwacja r WHERE r.pokoj.id = :pokojId " +
                    "AND (r.dataPrzyjazdu < :dataWyjazdu AND r.dataWyjazdu > :dataPrzyjazdu) " +
                    "AND r.statusRezerwacji IN (:statusy)";

            if (wykluczRezerwacjeId != null) {
                hql += " AND r.id != :wykluczId";
            }

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("pokojId", pokojId);
            query.setParameter("dataPrzyjazdu", dataPrzyjazdu);
            query.setParameter("dataWyjazdu", dataWyjazdu);
            query.setParameter("statusy", Arrays.asList(
                    StatusRezerwacji.POTWIERDZONA,
                    StatusRezerwacji.W_TRAKCIE
            ));

            if (wykluczRezerwacjeId != null) {
                query.setParameter("wykluczId", wykluczRezerwacjeId);
            }

            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas sprawdzania kolizji terminów: " + e.getMessage(), e);
        }
    }
    public Rezerwacja findByIdWithAssociations(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pierwsze zapytanie - podstawowe dane rezerwacji z gościem, pokojem i pracownikiem
            Query<Rezerwacja> query1 = session.createQuery(
                    "SELECT DISTINCT r FROM Rezerwacja r " +
                            "LEFT JOIN FETCH r.gosc " +
                            "LEFT JOIN FETCH r.pokoj " +
                            "LEFT JOIN FETCH r.pracownikTworzacy " +
                            "WHERE r.id = :id",
                    Rezerwacja.class);
            query1.setParameter("id", id);
            Rezerwacja rezerwacja = query1.uniqueResult();

            if (rezerwacja == null) {
                return null;
            }

            // Drugie zapytanie - zamówienia usług
            Query<Rezerwacja> query2 = session.createQuery(
                    "SELECT DISTINCT r FROM Rezerwacja r " +
                            "LEFT JOIN FETCH r.zamowieniaUslug zu " +
                            "LEFT JOIN FETCH zu.uslugaDodatkowa " +
                            "WHERE r.id = :id",
                    Rezerwacja.class);
            query2.setParameter("id", id);
            query2.uniqueResult(); // To załaduje zamówienia do Persistence Context

            // Trzecie zapytanie - płatności
            Query<Rezerwacja> query3 = session.createQuery(
                    "SELECT DISTINCT r FROM Rezerwacja r " +
                            "LEFT JOIN FETCH r.platnosci " +
                            "WHERE r.id = :id",
                    Rezerwacja.class);
            query3.setParameter("id", id);
            query3.uniqueResult(); // To załaduje płatności do Persistence Context

            return rezerwacja;

        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania rezerwacji z asocjacjami: " + e.getMessage(), e);
        }
    }


}

