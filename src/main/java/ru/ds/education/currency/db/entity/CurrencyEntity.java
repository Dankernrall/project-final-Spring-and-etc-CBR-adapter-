package ru.ds.education.currency.db.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="curs_data")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idEntity;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private CurrencyEnum currencyEntity;
    @Column(name = "curs")
    private float cursEntity;
    @Column(name = "curs_date", columnDefinition = "DATETIME")
    private LocalDate curs_dateEntity;

    public CurrencyEntity() {
    }

    public CurrencyEntity(CurrencyEnum currency, float curs, LocalDate curs_date) {
        this.currencyEntity = currency;
        this.cursEntity = curs;
        this.curs_dateEntity = curs_date;
    }

    public Long getIdEntity() {
        return idEntity;
    }

    public void setIdEntity(Long idEntity) {
        this.idEntity = idEntity;
    }

    public CurrencyEnum getCurrencyEntity() {
        return currencyEntity;
    }

    public void setCurrencyEntity(CurrencyEnum currencyEntity) {
        this.currencyEntity = currencyEntity;
    }

    public float getCursEntity() {
        return cursEntity;
    }

    public void setCursEntity(float cursEntity) {
        this.cursEntity = cursEntity;
    }

    public LocalDate getCurs_dateEntity() {
        return curs_dateEntity;
    }

    public void setCurs_dateEntity(LocalDate curs_dateEntity) {
        this.curs_dateEntity = curs_dateEntity;
    }
}


