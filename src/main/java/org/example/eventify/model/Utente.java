package org.example.eventify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Utente")

public class Utente {
    @Id
    private String email;
    private String password;
    private String nome;
    private String cognome;
    @Column(name = "data_nascita")
    private Date dataNascita;
    private Integer organizzatore;
    private String stato;
    private String citta;
    private String cap;
    private String via;
    @Column(name = "num_civico")
    private String numeroCivico;
}