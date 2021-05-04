package ru.ds.education.currency.core.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.ds.education.currency.db.entity.StatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
public class CursRequestModel {
    @ApiModelProperty("Id записи в Request")
    private Long idRequestEntity;
    @ApiModelProperty("За какое число запрашивается валюта")
    private LocalDate curs_dateRequestEntity;
    @ApiModelProperty("Дата запроса")
    private LocalDateTime request_dateEntity;
    @ApiModelProperty("Correlation ID")
    private String correlation_idEntity;
    @ApiModelProperty("Status")
    private StatusEnum statusEntity;
}
