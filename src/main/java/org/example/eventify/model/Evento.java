package org.example.eventify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Evento")

public class Evento {
    @Id
    @Column(name = "id_evento")
    private Integer idEvento;
    @ManyToOne
    @JoinColumn(name = "creatore", referencedColumnName = "email")
    private Utente organizzatore;
    private String nome;
    @Column(name = "data_ora_inizio")
    private LocalDateTime dataOraInizio;
    @Column(name = "data_ora_fine")
    private LocalDateTime dataOraFine;
    private String stato;
    private String citta;
    private String cap;
    private String via;
    @Column(name = "num_civico")
    private String numeroCivico;
    private String tipo;
    private Integer visibilita;
    private String descrizione;
    @Column(name = "costo")
    private Float costoIngresso;
    @Column(name = "partecipanti_max")
    private Integer maxPartecipanti;
    private Integer likes;
}