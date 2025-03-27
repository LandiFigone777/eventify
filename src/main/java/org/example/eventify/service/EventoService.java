package org.example.eventify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Evento;
import org.example.eventify.repository.EventoRepository;

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

}