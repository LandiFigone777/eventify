package org.example.eventify.service;

import org.example.eventify.model.Evento;
import org.example.eventify.model.Invito;
import org.example.eventify.model.Utente;
import org.example.eventify.repository.InvitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Immagini;

import java.util.List;

@Service
public class InvitoService {
    @Autowired
    private InvitoRepository invitoRepository;

    public List<Invito> findAll() {
        return invitoRepository.findAll();
    }

    public Invito findById(Integer id) {
        return invitoRepository.findById(id).orElse(null);
    }

    public Invito save(Invito invito) {
        return invitoRepository.save(invito);
    }

    public Invito getInvitoByEventoAndInvitato(Evento evento, Utente utente) {
        return invitoRepository.getInvitoByEventoAndInvitato(evento, utente);
    }
}