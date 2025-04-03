package org.example.eventify.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.example.eventify.model.Utente;
import org.example.eventify.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {
    @Autowired
    private UtenteService utenteService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Utente utente) {
        try {
            // Hash della password con SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(utente.getPassword().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            utente.setPassword(hexString.toString()); // Imposta la password hashata
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "/register"; // Torna alla pagina di registrazione in caso di errore
        }

        utenteService.save(utente); // Salva l'utente nel database
        return "redirect:/login"; // Reindirizza alla pagina di login
    }
}
