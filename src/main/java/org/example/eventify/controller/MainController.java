package org.example.eventify.controller;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
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

        List<Utente> allUtenti = utenteService.findAll();
        List<String> usernames = new ArrayList<>();

        if (utente != null) {
            model.addAttribute("utente", utente);
            List<Integer> eventiHome = eventoService.getEventiOrderedByPopolaritaOfFollowingUsers(utente.getEmail());
            List<Integer> eventiHomeNotFollowing = eventoService.getEventiOrderedByPopolaritaOfNotFollowingUsers(utente.getEmail());
            List<Evento> eventiHomeObj = new ArrayList<>();

            for(Integer evento : eventiHome) {
                Evento eventoObj = eventoService.findById(evento);
                eventiHomeObj.add(eventoObj);
            }
            for(Integer evento : eventiHomeNotFollowing) {
                Evento eventoObj = eventoService.findById(evento);
                eventiHomeObj.add(eventoObj);
            }

            for(Utente u : allUtenti) {
                if (!u.getEmail().equals(utente.getEmail())) {
                    usernames.add(u.getUsername());
                }
            }

            model.addAttribute("eventiHome", eventiHomeObj);
            model.addAttribute("msg", msg);
            model.addAttribute("usernames", usernames);
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
    public String login(Model model,@RequestParam String emailLogin, @RequestParam String passwordLogin, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
        Utente foundUtente = utenteService.findById(emailLogin);
        if (foundUtente != null && foundUtente.getPassword().equals(Utils.hashPassword(passwordLogin))) {
            session.setAttribute("user", foundUtente);
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("msg", "Email o password errati");
            // redirectAttributes.addAttribute("msg", "Email o password errati");
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
    public String register(
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String username,
            @RequestParam String dataNascita,
            @RequestParam String indirizzo,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("user") != null) {
            redirectAttributes.addFlashAttribute("msg", "Sei già loggato");
            return "redirect:/home";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("msg", "Passwords do not match");
            return "redirect:/register?error=PasswordMismatch";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // Controllo validità email
            redirectAttributes.addFlashAttribute("msg", "Email non valida");
            return "redirect:/register";
        }

        try {
            Utente utente = new Utente();
            utente.setNome(nome);
            utente.setCognome(cognome);
            utente.setUsername(username);
            utente.setDataNascita(LocalDate.parse(dataNascita));
            utente.setIndirizzo(indirizzo);
            utente.setEmail(email);
            utente.setPassword(Utils.hashPassword(password));

            // Generate verification code
            String verificationCode = Utils.generateVerificationCode();
            utente.setVerificationCode(verificationCode);

            utenteService.save(utente); // Salva l'utente nel database
            emailService.sendEmail(email, "Registrazione completata", "La registrazione è avvenuta con successo! Il tuo codice di verifica è: " + verificationCode);
            session.setAttribute("user", utente); // Salva l'utente nella sessione
            return "redirect:/verify"; // Reindirizza alla pagina di verifica
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Errore nelle credenziali, l'email o il nome utente sono già in uso"); //facciamo vago per evitare di dare troppe info
            return "redirect:/register?error=Exception";
        }
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
            utente.setStato("VERIFICATO"); // ma quindi lo stato non è da dove viene? ma è uno stato
            utenteService.save(utente);
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("msg", "Codice di verifica errato");
            return "redirect:/verify";
        }

    }

    @GetMapping("/search")
    public String search(
            @RequestParam("searchType") String searchType,
            @RequestParam("searchQuery") String searchQuery,
            Model model) {
        List<Map<String, String>> results = new ArrayList<>();

        if (searchType.equals("user")) {
            // Cerca utenti
            List<Utente> utenti = utenteService.findAll();
            for (Utente utente : utenti) {
                if (utente.getUsername().toLowerCase().contains(searchQuery.toLowerCase())) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", utente.getUsername());
                    result.put("link", "/user/" + utente.getUsername());
                    results.add(result);
                }
            }
        } else if (searchType.equals("eventName")) {
            // Cerca eventi per nome
            List<Evento> eventi = eventoService.findAll();
            for (Evento evento : eventi) {
                if (evento.getNome().toLowerCase().contains(searchQuery.toLowerCase())) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", evento.getNome());
                    result.put("link", "/event?id=" + evento.getIdEvento());
                    results.add(result);
                }
            }
        } else if (searchType.equals("eventType")) {
            // Cerca eventi per tipo
            List<Evento> eventi = eventoService.findAll();
            for (Evento evento : eventi) {
                if (evento.getTipo().toLowerCase().contains(searchQuery.toLowerCase())) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", evento.getNome() + " (" + evento.getTipo() + ")");
                    result.put("link", "/event?id=" + evento.getIdEvento());
                    results.add(result);
                }
            }
        }

        model.addAttribute("results", results);
        return "searchResults"; // Crea una pagina per mostrare i risultati
    }
}