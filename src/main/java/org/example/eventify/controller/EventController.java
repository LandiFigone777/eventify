package org.example.eventify.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.eventify.model.Partecipazione;
import org.example.eventify.model.Utente;
import org.example.eventify.model.Evento;
import org.example.eventify.model.Immagini;
import org.example.eventify.repository.ImmaginiRepository;
import org.example.eventify.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpSession;

@Controller
public class EventController {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EventoService eventoService;
    @Autowired
    private ImmaginiService immaginiService;
    @Autowired
    private PartecipazioneService partecipazioneService;


    @GetMapping("/addEvent")
    public String addEventForm(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "addEvent";
    }

    @PostMapping("/addEvent")
    public String addEvent(@RequestParam String nome, @RequestParam String dataOraInizio,
                           @RequestParam String dataOraFine, @RequestParam String indirizzo,
                           @RequestParam String numCivico,
                           @RequestParam String tipo, @RequestParam String visibilita,
                           @RequestParam String descrizione, @RequestParam Integer etaMinima,
                           @RequestParam Float costoIngresso,
                           @RequestParam Integer maxPartecipanti, @RequestParam List<MultipartFile> immagini, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("user");
        Evento evento = new Evento();
        evento.setNome(nome);
        evento.setDataOraInizio(LocalDateTime.parse(dataOraInizio));
        evento.setDataOraFine(LocalDateTime.parse(dataOraFine));
        evento.setIndirizzo(indirizzo);
        evento.setNumeroCivico(numCivico);
        evento.setTipo(tipo);
        if(visibilita.equals("pubblico")) {
            visibilita = "1";
        } else if(visibilita.equals("privato")) {
            visibilita = "0";
        }
        evento.setVisibilita(Integer.parseInt(visibilita));
        evento.setDescrizione(descrizione);
        evento.setCostoIngresso(costoIngresso);
        evento.setEtaMinima(etaMinima);
        evento.setMaxPartecipanti(maxPartecipanti);
        evento.setOrganizzatore(utente);
        evento.setLikes(0);
        eventoService.save(evento);
        addImages(immagini, evento);
        return "redirect:/home";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam Integer idEvento, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("user");
        Evento evento = eventoService.findById(idEvento);
        if (evento != null) {
            Partecipazione partecipazione = new Partecipazione();
            partecipazione.setPartecipante(utente);
            partecipazione.setEvento(evento);
            partecipazioneService.save(partecipazione);
        }
        return "redirect:/home";
    }

    public void addImages(List<MultipartFile> immagini, Evento evento){

        String uploadsDir = new File("src/main/resources/uploads").getAbsolutePath();


        for (MultipartFile file : immagini) {
            String fileName = file.getOriginalFilename();
            File destinazione = new File(uploadsDir + File.separator + fileName);

            try {
                file.transferTo(destinazione);
                Immagini immagine = new Immagini();
                immagine.setUri(fileName);
                immagine.setEvento(evento);
                immaginiService.save(immagine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RestController
    @RequestMapping("/api/places")
    public class OsmPlaceController {

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
}
