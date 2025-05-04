package org.example.eventify.repository;

import org.example.eventify.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Evento;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByVisibilita(Integer visibilita);

    List<Evento> getByOrganizzatore(Utente organizzatore);

    @Query(nativeQuery = true, value = """
            SELECT evento.id_evento FROM
            evento LEFT JOIN eventi_preferiti ON evento.id_evento = eventi_preferiti.id_evento
            WHERE evento.visibilita = 1 AND evento.creatore IN (SELECT followers.followed FROM followers WHERE followers.follower = :utente)
            GROUP BY evento.id_evento
            ORDER BY COUNT(eventi_preferiti.id_evento) DESC""")
    List<Integer> getEventiOrderedByPopolaritaOfFollowingUsers(@Param("utente") String utente);

    @Query(nativeQuery = true, value = """
            SELECT evento.id_evento FROM
            evento LEFT JOIN eventi_preferiti ON evento.id_evento = eventi_preferiti.id_evento
            WHERE evento.visibilita = 1 AND evento.creatore NOT IN (SELECT followers.followed FROM followers WHERE followers.follower = :utente)
            GROUP BY evento.id_evento
            ORDER BY COUNT(eventi_preferiti.id_evento) DESC""")
    List<Integer> getEventiOrderedByPopolaritaOfNotFollowingUsers(@Param("utente") String utente);

    List<Evento> findByOrganizzatoreAndVisibilita(Utente organizzatore, Integer visibilita);

    Boolean existsByInvito(String invito);
}