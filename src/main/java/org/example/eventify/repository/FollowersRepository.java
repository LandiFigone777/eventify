package org.example.eventify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.example.eventify.model.Followers;
@Repository
public interface FollowersRepository extends JpaRepository<Followers, Integer> {
    @Query(nativeQuery = true, value = """
        SELECT * 
        FROM followers 
        WHERE followed = :email
    """)
    List<Followers> findAllFollowersByFollowed(@Param("email") String email);
    @Query(nativeQuery = true, value = """
    SELECT COUNT(*) > 0 
    FROM followers 
    WHERE follower = :followerEmail AND followed = :followedEmail
    """)
    boolean isFollowing(@Param("followerEmail") String followerEmail, @Param("followedEmail") String followedEmail);
    @Query(nativeQuery = true, value = """
    SELECT * 
    FROM followers 
    WHERE follower = :followerEmail AND followed = :followedEmail
    """)
    Followers findByFollowerAndFollowed(@Param("followerEmail") String followerEmail, @Param("followedEmail") String followedEmail);
}