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
@Table(name = "invito")

public class Invito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invito")
    private Integer idInvito;
    @ManyToOne
    @JoinColumn(name = "id_invitato", referencedColumnName = "email")
    private Utente invitato;
    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;
}