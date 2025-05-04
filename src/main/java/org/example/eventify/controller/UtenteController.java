package org.example.eventify.controller;

import org.example.eventify.model.Followers;
import org.example.eventify.model.Utente;
import org.example.eventify.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class UtenteController {
    @Autowired
    private UtenteService utenteService;
    @Autowired
    private FollowersService followersService;
    @Autowired
    private EventoService eventoService;
    
    @GetMapping("/user/{username}")
    public String getUserProfile(@PathVariable String username, Model model, HttpSession session) {
        Utente utente = utenteService.findByUsername(username);
        Utente loggedUser = (Utente) session.getAttribute("user");
        if (utente == null) {
            return "redirect:/home?msg=Utente non trovato";
        }

        boolean isFollowing;
        if (loggedUser != null) {
            isFollowing = followersService.isFollowing(loggedUser, utente);
        }
        else {
            return "redirect:/login";
        }

        model.addAttribute("utente", utente);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("followersNumber", followersService.followersNumber(utente));
        model.addAttribute("followingNumber", followersService.followingNumber(utente));

        if (loggedUser.getEmail().equals(utente.getEmail())) {
            model.addAttribute("eventi", eventoService.getByOrganizzatore(utente));
        } else {
            model.addAttribute("eventi", eventoService.getPublicEventsByOrganizzatore(utente));
        }

        return "user";
    }

    @PostMapping("/follow")
    public String followUser(@RequestParam String followedEmail, HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Utente followed = utenteService.findById(followedEmail);
        if (followed == null) {
            return "redirect:/home?msg=Utente non trovato";
        }

        boolean isFollowing = followersService.isFollowing(currentUser, followed);

        if (isFollowing) {
            // Unfollow the user
            Followers existingFollow = followersService.findByFollowerAndFollowed(currentUser, followed);
            if (existingFollow != null) {
                followersService.delete(existingFollow);
            }
        } else {
            // Follow the user
            Followers newFollow = new Followers();
            newFollow.setFollower(currentUser);
            newFollow.setFollowed(followed);
            followersService.save(newFollow);
        }

        return "redirect:/user/" + followed.getUsername();
    }
}