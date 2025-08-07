package org.example.dao;


import org.example.models.Pokoj;
import org.example.models.enums.StatusPokoju;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PokojDAO extends BaseDAO<Pokoj> {

    Optional<Pokoj> findByNumerPokoju(int numerPokoju);

    List<Pokoj> findByStatus(StatusPokoju status);

    List<Pokoj> findDostepnePokoje(LocalDate dataPrzyjazdu, LocalDate dataWyjazdu);

    List<Pokoj> findByTypPokoju(String typPokoju);
}

