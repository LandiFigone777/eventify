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
@Table(name = "Followers")
@IdClass(FollowersId.class)

public class Followers {
    @Id
    @ManyToOne
    @JoinColumn(name = "follower", referencedColumnName = "email")
    private Utente follower;
    @Id
    @ManyToOne
    @JoinColumn(name = "followed", referencedColumnName = "email")
    private Utente followed;
}