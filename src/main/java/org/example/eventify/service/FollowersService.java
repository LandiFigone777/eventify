package org.example.eventify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.eventify.model.Followers;
import org.example.eventify.repository.FollowersRepository;

import java.util.List;

@Service
public class FollowersService {
   @Autowired
   private FollowersRepository followersRepository;

   public List<Followers> findAll() {
      return followersRepository.findAll();
   }

   public Followers findById(Tipo id) {
      return followersRepository.findById(id).orElse(null);
   }

   public Followers save(Followers followers) {
      return followersRepository.save(followers);
   }

}