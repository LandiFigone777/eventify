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
@Table(name = "immagini_evento")

public class Immagini {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_immagine")
    private Integer idImmagine;
    @Column(name = "uri_immagine")
    private String uri;
    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;
}