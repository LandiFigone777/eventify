package org.example.eventify.repository;

import org.example.eventify.model.Evento;
import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Partecipazione;
@Repository
public interface PartecipazioneRepository extends JpaRepository<Partecipazione, Integer> {
    Partecipazione getPartecipazioneByEventoAndPartecipante(Evento evento, Utente partecipante);
}