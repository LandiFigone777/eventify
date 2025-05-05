package org.example.eventify.repository;

import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.example.eventify.model.Followers;
@Repository
public interface FollowersRepository extends JpaRepository<Followers, Integer> {
    List<Followers> getFollowersByFollowed(Utente utente);
    List<Followers> getFollowersByFollower(Utente utente);
    boolean existsByFollowerAndFollowed(Utente follower, Utente followed);
    Followers findByFollowerAndFollowed(Utente follower, Utente followed);
    Integer countAllByFollowed(Utente followed);
    Integer countAllByFollower(Utente follower);
}