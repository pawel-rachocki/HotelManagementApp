package org.example.dao;


import org.example.models.Gosc;
import java.util.List;
import java.util.Optional;

public interface GoscDAO extends BaseDAO<Gosc> {

    Optional<Gosc> findByEmail(String email);

    List<Gosc> findByNazwisko(String nazwisko);

    boolean existsByEmail(String email);
}

