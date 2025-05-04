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

        if (utente == null) {
            return "redirect:/home?msg=Utente non trovato";
        }

        Utente currentUser = (Utente) session.getAttribute("user");
        boolean isFollowing = false;
        if (currentUser != null) {
            isFollowing = followersService.findAll().stream()
                .anyMatch(f -> f.getFollower().equals(currentUser) && f.getFollowed().equals(utente));
        }

        model.addAttribute("utente", utente);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("eventi", eventoService.getByOrganizzatore(utente));
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

    Followers existingFollow = followersService.findAll().stream()
        .filter(f -> f.getFollower().equals(currentUser) && f.getFollowed().equals(followed))
        .findFirst()
        .orElse(null);

    if (existingFollow != null) {
        followersService.delete(existingFollow);
    } else {
        Followers newFollow = new Followers();
        newFollow.setFollower(currentUser);
        newFollow.setFollowed(followed);
        followersService.save(newFollow);
    }

    return "redirect:/user/" + followed.getUsername();
    }
}