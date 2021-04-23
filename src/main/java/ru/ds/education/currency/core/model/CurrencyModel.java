package ru.ds.education.currency.core.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ds.education.currency.db.entity.CurrencyEnum;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor

public class CurrencyModel {
    private Long idModel;
    private CurrencyEnum currencyModel;
    private float cursModel;
    private LocalDate curs_dateModel;
}
