package org.example.eventify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Partecipazione;
import org.example.eventify.repository.PartecipazioneRepository;

import java.util.List;

@Service
public class PartecipazioneService {
   @Autowired
   private PartecipazioneRepository partecipazioneRepository;

   public List<Partecipazione> findAll() {
      return partecipazioneRepository.findAll();
   }

   public Partecipazione findById(Tipo id) {
      return partecipazioneRepository.findById(id).orElse(null);
   }

   public Partecipazione save(Partecipazione partecipazione) {
      return partecipazioneRepository.save(partecipazione);
   }

}