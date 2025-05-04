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
@Table(name = "followers")

public class Followers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idDellaSuperVergogna;
    @ManyToOne
    @JoinColumn(name = "follower", referencedColumnName = "email")
    private Utente follower;
    @ManyToOne
    @JoinColumn(name = "followed", referencedColumnName = "email")
    private Utente followed;
}