package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.eventify.model.Followers;
@Repository
public interface FollowersRepository extends JpaRepository<Followers, TipoId> {}