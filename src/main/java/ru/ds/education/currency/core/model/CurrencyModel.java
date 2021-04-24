package ru.ds.education.currency.core.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.ds.education.currency.db.entity.CurrencyEnum;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor

public class CurrencyModel {
    @ApiModelProperty("Id записи")
    private Long idModel;
    @ApiModelProperty("Валюта")
    private CurrencyEnum currencyModel;
    @ApiModelProperty("Курс")
    private float cursModel;
    @ApiModelProperty("Дата")
    private LocalDate curs_dateModel;
}
