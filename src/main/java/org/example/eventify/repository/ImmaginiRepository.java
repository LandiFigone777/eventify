package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Immagini;
@Repository
public interface ImmaginiRepository extends JpaRepository<Immagini, String> {}