package ru.ds.education.currency.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "curs_request")
public class CursRequestEntity {
    @Id
    @SequenceGenerator(name = "cursRequestSeq", sequenceName = "curs_request_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cursRequestSeq")
    @Column(name = "id")
    private Long idRequestEntity;
    @Column(name = "curs_date", columnDefinition = "DATETIME")
    private LocalDate curs_dateRequestEntity;
    @Column(name = "request_date")
    private LocalDateTime request_dateEntity;
    @Column(name = "correlation_id")
    private String correlation_idEntity;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum statusEntity;
}


