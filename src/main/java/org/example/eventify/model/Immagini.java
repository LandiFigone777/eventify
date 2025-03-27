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
@Table(name = "Immagini")

public class Immagini {
    @Id
    private String uri;
    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;
}