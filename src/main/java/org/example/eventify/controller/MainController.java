package org.example.eventify.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import jakarta.servlet.http.HttpSession;
import org.example.eventify.model.Utente;
import org.example.eventify.service.EmailService;
import org.example.eventify.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
    @Autowired
    private UtenteService utenteService;
    @Autowired
    private EmailService emailService;

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