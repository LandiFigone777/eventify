package org.example.eventify.service;

import org.example.eventify.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Evento;
import org.example.eventify.repository.EventoRepository;

import java.lang.annotation.Native;
import java.util.List;

@Service
public class EventoService {
   @Autowired
   private EventoRepository eventoRepository;

   public List<Evento> findAll() {
      return eventoRepository.findAll();
   }

   public Evento findById(Integer id) {
      return eventoRepository.findById(id).orElse(null);
   }

   public Evento save(Evento evento) {
      return eventoRepository.save(evento);
   }

   public List<Evento> getByVisibilita(Integer visibilita) {
      return eventoRepository.findByVisibilita(visibilita);
   }

    public List<Evento> getByOrganizzatore(Utente utente) {
        return eventoRepository.getByOrganizzatore(utente);
    }

    public List<Integer> getEventiOrderedByPopolarita() {
        return eventoRepository.getEventiOrderedByPopolarita();
    }
}