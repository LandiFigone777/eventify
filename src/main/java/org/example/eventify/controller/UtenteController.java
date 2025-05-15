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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        model.addAttribute("loggedUser", loggedUser);
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
        if(Objects.equals(followedEmail, currentUser.getEmail())) {
            return "redirect:/user/" + currentUser.getUsername();
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

    @GetMapping("/user/{username}/followers")
    public String getFollowers(@PathVariable String username, Model model) {
        Utente utente = utenteService.findByUsername(username);
        if (utente == null) {
            return "redirect:/home?msg=Utente non trovato";
        }

        List<Followers> followersList = followersService.findAllFollowersByFollowed(utente);
        List<Utente> followersListModel = new ArrayList<>();

        for(Followers follower : followersList) {
            Utente followerUser = utenteService.findById(follower.getFollower().getEmail());
            followersListModel.add(followerUser);
        }

        model.addAttribute("followers", followersListModel);

        return "followers";
    }

    @GetMapping("/user/{username}/following")
    public String getFollowing(@PathVariable String username, Model model) {
        Utente utente = utenteService.findByUsername(username);
        if (utente == null) {
            return "redirect:/home?msg=Utente non trovato";
        }

        List<Followers> followingList = followersService.findAllFollowersByFollowing(utente);
        List<Utente> followingListModel = new ArrayList<>();

        for(Followers following : followingList) {
            Utente followingUser = utenteService.findById(following.getFollowed().getEmail());
            followingListModel.add(followingUser);
        }

        model.addAttribute("following", followingListModel);

        return "following";
    }

    @GetMapping("/userSearch")
    public String searchUser(Model model, HttpSession session) {
        Utente loggedUser = (Utente) session.getAttribute("user");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        List<Utente> allUtenti = utenteService.findAll();
        List<String> filteredUtenti = new ArrayList<>();

        for (Utente utente : allUtenti) {
            if (!utente.getEmail().equals(loggedUser.getEmail())) {
                filteredUtenti.add(utente.getUsername());
            }
        }

        model.addAttribute("usernames", filteredUtenti);
        return "userSearch";
    }
}