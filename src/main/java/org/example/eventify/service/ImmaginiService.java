package org.example.eventify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Immagini;
import org.example.eventify.repository.ImmaginiRepository;

import java.util.List;

@Service
public class ImmaginiService {
   @Autowired
   private ImmaginiRepository immaginiRepository;

   public List<Immagini> findAll() {
      return immaginiRepository.findAll();
   }

   public Immagini findById(String id) {
      return immaginiRepository.findById(id).orElse(null);
   }

   public Immagini save(Immagini immagini) {
      return immaginiRepository.save(immagini);
   }

}