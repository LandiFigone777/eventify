package org.example.eventify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Utente;
import org.example.eventify.repository.UtenteRepository;

import java.util.List;

@Service
public class UtenteService {
   @Autowired
   private UtenteRepository utenteRepository;

   public List<Utente> findAll() {
      return utenteRepository.findAll();
   }

   public Utente findById(String id) {
      return utenteRepository.findById(id).orElse(null);
   }

   public Utente save(Utente utente) {
      return utenteRepository.save(utente);
   }

}