package org.example.eventify.repository;

import org.example.eventify.model.EventiPreferiti;
import org.example.eventify.model.Evento;
import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EventiPreferitiRepository extends JpaRepository<EventiPreferiti, Integer> {
    EventiPreferiti getByLikerAndEvento(Utente utente, Evento evento);

    Integer countAllByEvento(Evento evento);
}