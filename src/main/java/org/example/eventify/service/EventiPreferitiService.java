package org.example.eventify.service;

import org.example.eventify.model.EventiPreferiti;
import org.example.eventify.model.Evento;
import org.example.eventify.model.Invito;
import org.example.eventify.model.Utente;
import org.example.eventify.repository.EventiPreferitiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventiPreferitiService {
    @Autowired
    private EventiPreferitiRepository eventiPreferitiRepository;

    public List<EventiPreferiti> findAll() {
        return eventiPreferitiRepository.findAll();
    }

    public EventiPreferiti findById(Integer id) {
        return eventiPreferitiRepository.findById(id).orElse(null);
    }

    public EventiPreferiti save(EventiPreferiti eventoPreferito) {
        return eventiPreferitiRepository.save(eventoPreferito);
    }

    public EventiPreferiti getByLikerAndEvento(Utente utente, Evento evento) {
        return eventiPreferitiRepository.getByLikerAndEvento(utente, evento);
    }

    public void delete(EventiPreferiti eventiPreferiti) {
        eventiPreferitiRepository.delete(eventiPreferiti);
    }

    public Integer countAllByEvento(Evento evento) {
        return eventiPreferitiRepository.countAllByEvento(evento);
    }
}