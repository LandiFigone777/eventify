package org.example.eventify.repository;

import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Evento;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByVisibilita(Integer visibilita);
    List<Evento> getByOrganizzatore(Utente organizzatore);
}