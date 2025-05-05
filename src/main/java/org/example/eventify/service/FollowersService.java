package org.example.eventify.service;

import org.example.eventify.model.Utente;
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

   public Followers findById(Integer id) {
      return followersRepository.findById(id).orElse(null);
   }

   public Followers save(Followers followers) {
      return followersRepository.save(followers);
   }

   public void delete(Followers followers) {
      followersRepository.delete(followers);
   }

   public List<Followers> findAllFollowersByFollowed(Utente utente) {
      return followersRepository.getFollowersByFollowed(utente);
   }

   public List<Followers> findAllFollowersByFollowing(Utente utente) {
      return followersRepository.getFollowersByFollower(utente);
   }

   public boolean isFollowing(Utente follower, Utente followed) {
      return followersRepository.existsByFollowerAndFollowed(follower, followed);
   }

   public Followers findByFollowerAndFollowed(Utente follower, Utente followed) {
      return followersRepository.findByFollowerAndFollowed(follower, followed);
   }

   public Integer followersNumber(Utente followed) {
      return followersRepository.countAllByFollowed(followed);
   }

    public Integer followingNumber(Utente follower) {
        return followersRepository.countAllByFollower(follower);
    }
}