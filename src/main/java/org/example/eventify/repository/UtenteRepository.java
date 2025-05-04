package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Utente;
@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {
    Utente findByUsername(String username);
}