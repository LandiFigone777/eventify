package org.example.eventify.repository;

import org.example.eventify.model.Evento;
import org.example.eventify.model.Invito;
import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface InvitoRepository extends JpaRepository<Invito, Integer> {
    public Invito getInvitoByEventoAndInvitato(Evento evento, Utente invitato);
}