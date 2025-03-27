package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Partecipazione;
@Repository
public interface PartecipazioneRepository extends JpaRepository<Partecipazione, Integer> {}