package org.example.eventify.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.example.eventify.model.Evento;
import org.example.eventify.model.Utente;
import org.example.eventify.service.EmailService;
import org.example.eventify.service.EventoService;
import org.example.eventify.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.eventify.Utils;

@Controller
public class MainController {
    @Autowired
    private UtenteService utenteService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EventoService eventoService;

    @GetMapping("/")
    public String home(RedirectAttributes redirectAttributes) {
        Utente utente = new Utente();
        redirectAttributes.addAttribute("utente", utente);
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model, @RequestParam(required = false) String msg) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente != null) {
            model.addAttribute("utente", utente);
            List<Integer> eventiHome = eventoService.getEventiOrderedByPopolarita();
            List<Evento> eventiHomeObj = new ArrayList<>();
            for(Integer evento : eventiHome) {
                Evento eventoObj = eventoService.findById(evento);
                eventiHomeObj.add(eventoObj);
            }
            model.addAttribute("eventiHome", eventiHomeObj);
            model.addAttribute("msg", msg);
            return "home";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/exit")
    public String exit(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        if(session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        Utente utente = new Utente();
        model.addAttribute("utente", utente);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String emailLogin, @RequestParam String passwordLogin, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
        Utente foundUtente = utenteService.findById(emailLogin);
        if (foundUtente != null && foundUtente.getPassword().equals(Utils.hashPassword(passwordLogin))) {
            session.setAttribute("user", foundUtente);
            return "redirect:/home";
        } else {
            redirectAttributes.addAttribute("msg", "Email o password errati");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        Utente utente = new Utente();
        model.addAttribute("utente", utente);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Utente utente, HttpSession session){
        if(session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        try {
            utente.setPassword(Utils.hashPassword(utente.getPassword()));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "/register";
        }

        // Generate verification code
        String verificationCode = Utils.generateVerificationCode();
        utente.setVerificationCode(verificationCode);

        utenteService.save(utente); // Salva l'utente nel database
        emailService.sendEmail(utente.getEmail(), "Registrazione completata", "La registrazione è avvenuta con successo! Il tuo codice di verifica è: " + verificationCode);
        session.setAttribute("user", utente); // Salva l'utente nella sessione
        return "redirect:/verify"; // Reindirizza alla pagina di login
    }

    @GetMapping("/publicEvents")
    public String publicEvents(Model model) {
        List<Evento> eventiPubblici = eventoService.getByVisibilita(1);
        model.addAttribute("eventiPubblici", eventiPubblici);
        return "publicEvents";
    }

    @GetMapping("/verify")
    public String verifyForm(Model model) {
        Utente utente = new Utente();
        model.addAttribute("utente", utente);
        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("code") String code, RedirectAttributes redirectAttributes, HttpSession session) {
        System.out.println("Verification code: " + code);
        System.out.println("User verification code: " + ((Utente) session.getAttribute("user")).getVerificationCode());
        if(((Utente) session.getAttribute("user")).getVerificationCode().equals(code)) {
            Utente utente = (Utente) session.getAttribute("user");
            utente.setStato("VERIFICATO");
            utenteService.save(utente);
            return "redirect:/home";
        } else {
            redirectAttributes.addAttribute("msg", "Codice di verifica errato");
            return "redirect:/verify";
        }

    }
}