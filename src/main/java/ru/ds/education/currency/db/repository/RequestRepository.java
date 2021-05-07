package ru.ds.education.currency.db.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ds.education.currency.db.entity.CurrencyEntity;
import ru.ds.education.currency.db.entity.CursRequestEntity;

import java.time.LocalDate;
import java.util.List;

public interface RequestRepository extends JpaRepository<CursRequestEntity, Long> {

    @Query(value = "SELECT u FROM CursRequestEntity u WHERE u.request_dateEntity = (SELECT MAX(u2.request_dateEntity) FROM CursRequestEntity u2) AND u.curs_dateRequestEntity = :curs_date")
    CursRequestEntity find(@Param("curs_date") LocalDate curs_dateRequestEntity);

    @Query(value = "SELECT u FROM CursRequestEntity u WHERE u.request_dateEntity = (SELECT MAX(u2.request_dateEntity) FROM CursRequestEntity u2) AND u.correlation_idEntity =:correlationId")
    CursRequestEntity findByCorrelationID(@Param("correlationId") String correlation_idEntity);
}

