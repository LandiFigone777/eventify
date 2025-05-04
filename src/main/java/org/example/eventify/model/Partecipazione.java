package org.example.eventify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "partecipazione")

public class Partecipazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idDellaVergogna;
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Utente partecipante;
    @ManyToOne
    @JoinColumn(name = "id_evento", referencedColumnName = "id_evento")
    private Evento evento;
}