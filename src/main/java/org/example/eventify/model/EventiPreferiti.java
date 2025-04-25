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
@Table(name = "eventi_preferiti")

public class EventiPreferiti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_like")
    private Integer idLike;
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Utente liker;
    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;
}