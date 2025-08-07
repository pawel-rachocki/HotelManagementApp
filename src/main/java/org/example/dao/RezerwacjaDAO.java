package org.example.dao;



import org.example.models.Rezerwacja;
import org.example.models.enums.StatusRezerwacji;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RezerwacjaDAO extends BaseDAO<Rezerwacja> {

    Optional<Rezerwacja> findByNumerRezerwacji(String numerRezerwacji);

    List<Rezerwacja> findByGosc(Long goscId);

    List<Rezerwacja> findByStatus(StatusRezerwacji status);

    List<Rezerwacja> findByDateRange(LocalDate dataOd, LocalDate dataDo);

    List<Rezerwacja> findAnulowaneDoArchiwizacji();
}

