package ru.ds.education.currency.jms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestMessage {
    @JsonProperty("onDate")
    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate onDate;

}
