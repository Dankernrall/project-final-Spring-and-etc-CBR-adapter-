package ru.ds.education.currency.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ds.education.currency.db.entity.CurrencyEntity;
import ru.ds.education.currency.db.entity.CurrencyEnum;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    @Query("select u from CurrencyEntity u where u.currencyEntity = :currency and u.curs_dateEntity = :curs_date")
    List<CurrencyEntity> findByDate(@Param("currency") CurrencyEnum currencyEntity, @Param("curs_date") LocalDate curs_date);
}

