package org.example.eventify.repository;

import org.example.eventify.model.Invito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface InvitoRepository extends JpaRepository<Invito, Integer> {}