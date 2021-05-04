package ru.ds.education.currency.db.repository;

import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ds.education.currency.db.entity.CurrencyEntity;
import ru.ds.education.currency.db.entity.CursRequestEntity;

import java.time.LocalDate;
import java.util.List;

public interface RequestRepository extends JpaRepository<CursRequestEntity, Long> {

    @Query("select max(u.request_dateEntity) from CursRequestEntity u where u.curs_dateRequestEntity = :curs_date")
    CursRequestEntity find(@Param("curs_date") LocalDate curs_dateRequestEntity);
}

