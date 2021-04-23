package ru.ds.education.currency.core.model;
import ru.ds.education.currency.db.entity.CurrencyEnum;

import java.time.LocalDate;

public class CurrencyModel {
    private Long idModel;
    private CurrencyEnum currencyModel;
    private float cursModel;
    private LocalDate curs_dateModel;

    public CurrencyModel(Long id, CurrencyEnum currency, float curs, LocalDate curs_date) {
        this.idModel = id;
        this.currencyModel = currency;
        this.cursModel = curs;
        this.curs_dateModel = curs_date;
    }

    public Long getIdModel() {
        return idModel;
    }

    public void setIdModel(Long idModel) {
        this.idModel = idModel;
    }

    public CurrencyEnum getCurrencyModel() {
        return currencyModel;
    }

    public void setCurrencyModel(CurrencyEnum currencyModel) {
        this.currencyModel = currencyModel;
    }

    public float getCursModel() {
        return cursModel;
    }

    public void setCursModel(float cursModel) {
        this.cursModel = cursModel;
    }

    public LocalDate getCurs_dateModel() {
        return curs_dateModel;
    }

    public void setCurs_dateModel(LocalDate curs_dateModel) {
        this.curs_dateModel = curs_dateModel;
    }

}
