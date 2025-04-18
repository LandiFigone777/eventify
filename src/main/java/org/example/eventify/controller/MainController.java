package org.example.eventify.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.servlet.http.HttpSession;
import org.example.eventify.model.Evento;
import org.example.eventify.model.Utente;
import org.example.eventify.service.EmailService;
import org.example.eventify.service.EventoService;
import org.example.eventify.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

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
    public String homePage(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente != null) {
            model.addAttribute("utente", utente);
            return "home";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        Utente utente = new Utente();
        model.addAttribute("utente", utente);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String emailLogin, @RequestParam String passwordLogin, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
        Utente foundUtente = utenteService.findById(emailLogin);
        if (foundUtente != null && foundUtente.getPassword().equals(hashPassword(passwordLogin))) {
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
    public String register(@ModelAttribute Utente utente, HttpSession session, RedirectAttributes redirectAttributes) throws NoSuchAlgorithmException {
        try {
            utente.setPassword(hashPassword(utente.getPassword())); // Hash della password
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "/register"; // Torna alla pagina di registrazione in caso di errore
        }

        // Generate verification code
        String verificationCode = generateVerificationCode();
        utente.setVerificationCode(verificationCode);

        utenteService.save(utente); // Salva l'utente nel database
        emailService.sendEmail(utente.getEmail(), "Registrazione completata", "La registrazione è avvenuta con successo! Il tuo codice di verifica è: " + verificationCode);
        session.setAttribute("user", utente); // Salva l'utente nella sessione
        return "redirect:/verify"; // Reindirizza alla pagina di login
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // Genera un numero casuale da 0 a 9
        }
        return code.toString();
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

    @GetMapping("/addEvent")
    public String addEventForm(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "addEvent";
    }

    @GetMapping("/publicEvents")
    public String getPublicEvents(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        List<Evento> eventiPubblici = eventoService.getByVisibilita(1);
        model.addAttribute("eventiPubblici", eventiPubblici);
        return "publicEvents";
    }

    @RestController
    @RequestMapping("/api/places")
    public static class OsmPlaceController {

        @GetMapping("/osm-autocomplete")
        public ResponseEntity<List<Map<String, String>>> autocomplete(@RequestParam String query) {
            RestTemplate restTemplate = new RestTemplate();
            String requestString = "https://nominatim.openstreetmap.org/search?q="+query+"&format=json&addressdetails=1&limit=5";

            String url = String.format(
                    requestString,
                    UriUtils.encode(query, StandardCharsets.UTF_8)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "LaTuaApp/1.0 (email@esempio.com)"); // necessario per Nominatim

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class
            );

            // Map i risultati per il frontend
            List<Map<String, Object>> results = response.getBody();
            List<Map<String, String>> suggestions = results.stream().map(item -> {
                Map<String, String> m = new HashMap<>();
                m.put("displayName", (String) item.get("display_name"));
                return m;
            }).toList();

            return ResponseEntity.ok(suggestions);
        }
    }


    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e){
            throw new NoSuchAlgorithmException("Errore durante l'hashing della password");
        }
    }
}