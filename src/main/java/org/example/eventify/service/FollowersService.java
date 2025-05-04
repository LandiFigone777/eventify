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

   public Followers findById(Integer id) {
      return followersRepository.findById(id).orElse(null);
   }

   public Followers save(Followers followers) {
      return followersRepository.save(followers);
   }

   public void delete(Followers followers) {
      followersRepository.delete(followers);
   }

   public List<Followers> findAllFollowersByFollowed(String email) {
      return followersRepository.findAllFollowersByFollowed(email);
   }

   public boolean isFollowing(String followerEmail, String followedEmail) {
      return followersRepository.isFollowing(followerEmail, followedEmail);
   }

   public Followers findByFollowerAndFollowed(String followerEmail, String followedEmail) {
      return followersRepository.findByFollowerAndFollowed(followerEmail, followedEmail);
   }

}