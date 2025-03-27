package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Evento;
@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {}