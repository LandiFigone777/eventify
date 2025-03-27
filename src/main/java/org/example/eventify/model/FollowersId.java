package org.example.eventify.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowersId implements Serializable {
    private String follower;
    private String followed;

    // Getters, setters, hashCode, and equals methods
    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowersId that = (FollowersId) o;
        return Objects.equals(follower, that.follower) && Objects.equals(followed, that.followed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, followed);
    }
}