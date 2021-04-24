package ru.ds.education.currency.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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


}


