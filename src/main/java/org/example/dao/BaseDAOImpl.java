package org.example.dao;



import org.example.dao.BaseDAO;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * Abstrakcyjna implementacja wzorca Generic DAO dla Hibernate.
 * Implementuje podstawowe operacje CRUD dla wszystkich encji w systemie.
 * Wykorzystuje generyki dla zapewnienia type safety i uniknięcia duplikacji kodu.
 * Wzorzec zgodny z najlepszymi praktykami projektowania warstwy dostępu do danych.
 */

public abstract class BaseDAOImpl<T> implements BaseDAO<T> {

    private final Class<T> entityClass;

    // Konstruktor przyjjmujący klasę encji.
    protected BaseDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // Zapis nowej encji do bazy danych
    @Override
    public void save(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            // Rollback  ze sprawdzeniem czy transaction i session są aktywne
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    System.err.println("Błąd podczas rollback: " + rollbackException.getMessage());
                }
            }
            throw new RuntimeException("Błąd podczas zapisywania encji: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Aktualizacja encji w bazie
    @Override
    public void update(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    System.err.println("Błąd podczas rollback: " + rollbackException.getMessage());
                }
            }
            throw new RuntimeException("Błąd podczas aktualizacji encji: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Usuwanie encji z bazy
    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    System.err.println("Błąd podczas rollback: " + rollbackException.getMessage());
                }
            }
            throw new RuntimeException("Błąd podczas usuwania encji: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    // Wyszykanie encji po kliczu głównym
    @Override
    public Optional<T> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wyszukiwania encji po ID: " + e.getMessage(), e);
        }
    }

    // Pobiera wszystkie encje danego typu z bazy danych
    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas pobierania wszystkich encji: " + e.getMessage(), e);
        }
    }

    // Uniwersalna metoda save lub update - Hibernate automatycznie decyduje.
    @Override
    public void saveOrUpdate(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackException) {
                    System.err.println("Błąd podczas rollback: " + rollbackException.getMessage());
                }
            }
            throw new RuntimeException("Błąd podczas zapisywania/aktualizacji encji: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}

