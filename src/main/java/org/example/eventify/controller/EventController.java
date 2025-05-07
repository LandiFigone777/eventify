package org.example.eventify.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import org.example.eventify.Utils;
import org.example.eventify.model.*;
import org.example.eventify.repository.ImmaginiRepository;
import org.example.eventify.repository.InvitoRepository;
import org.example.eventify.repository.PartecipazioneRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    @Autowired
    private EventiPreferitiService eventiPreferitiService;
    @Autowired
    private PartecipazioneRepository partecipazioneRepository;
    @Autowired
    private InvitoService invitoService;


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
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
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
            String invito = Utils.generateUid();
            while (eventoService.existsByInvito(invito)) {
                invito = Utils.generateUid();
            }
            evento.setInvito(invito);
        }
        evento.setVisibilita(Integer.parseInt(visibilita));
        evento.setDescrizione(descrizione);
        evento.setCostoIngresso(costoIngresso);
        evento.setEtaMinima(etaMinima);
        evento.setMaxPartecipanti(maxPartecipanti);
        evento.setOrganizzatore(utente);
        eventoService.save(evento);
        addImages(immagini, evento);
        return "redirect:/home";
    }

    @GetMapping("yourEvents")
    public String yourEvents(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente != null) {
            model.addAttribute("utente", utente);
            List<Evento> eventi = eventoService.getByOrganizzatore(utente);
            model.addAttribute("eventi", eventi);
            return "yourCreations";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam Integer idEvento, @RequestParam String partecipa, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente == null) {
            return "redirect:/login";
        }
        Evento evento = eventoService.findById(idEvento);
        if (evento != null) {
            if (partecipa.equals("DISISCRIVITI")) {
                Partecipazione partecipazione = partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente);
                if (partecipazione != null) {
                    partecipazioneRepository.delete(partecipazione);
                }
            } else if (partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) == null) {
                Partecipazione partecipazione = new Partecipazione();
                partecipazione.setPartecipante(utente);
                partecipazione.setEvento(evento);
                partecipazioneService.save(partecipazione);
            }
            // Aggiungi la variabile 'partecipato' al modello
            boolean partecipato = partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) != null;
            model.addAttribute("partecipato", partecipato);
            model.addAttribute("evento", evento);
        }
        return "fragments/subscribe :: subscribe";
    }

    @GetMapping("/subscriptions")
    public String subscriptions(HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente != null) {
            model.addAttribute("utente", utente);
            List<Partecipazione> partecipazioni = partecipazioneService.getPartecipazioneByPartecipante(utente);
            List<Evento> eventiPartecipati = new ArrayList<>();
            for(Partecipazione partecipazione : partecipazioni){
                Evento evento = eventoService.findById(partecipazione.getEvento().getIdEvento());
                eventiPartecipati.add(evento);
            }
            model.addAttribute("partecipazioni", eventiPartecipati);
            return "subscriptions";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/event")
    public String showEvent(@RequestParam("id") Integer idEvento, @RequestParam(value = "invitation", required = false) String invito, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente != null) {
            Evento evento = eventoService.findById(idEvento);
            if(evento == null) {
                redirectAttributes.addAttribute("msg", "Evento non trovato");
                return "redirect:/home";
            }
            if(evento.getVisibilita() == 1){
                return eventChecks(model, evento, utente, idEvento);
            }
            if(evento.getVisibilita() == 0 && evento.getOrganizzatore().getEmail().equals(utente.getEmail())){
                return eventChecks(model, evento, utente, idEvento);
            }
            else if(evento.getVisibilita() == 0 && partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) == null && invitoService.getInvitoByEventoAndInvitato(evento, utente) != null){
                return eventChecks(model, evento, utente, idEvento);
            }
            else if(evento.getVisibilita() == 0 && (partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) != null || Objects.equals(evento.getInvito(), invito))) {
                if(partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) == null && Objects.equals(evento.getInvito(), invito)){
                    if(invitoService.getInvitoByEventoAndInvitato(evento, utente) == null) {
                        Invito invito1 = new Invito();
                        invito1.setEvento(evento);
                        invito1.setInvitato(utente);
                        invitoService.save(invito1);
                    }
                }
                return eventChecks(model, evento, utente, idEvento);
            }
            else if(evento.getVisibilita() == 0 && (partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) == null || !Objects.equals(evento.getInvito(), invito) || invitoService.getInvitoByEventoAndInvitato(evento, utente) == null)) {
                redirectAttributes.addAttribute("msg", "Evento privato, non puoi visualizzarlo se non sei invitato o non sei l'organizzatore");
                return "redirect:/home";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/likeEvent")
    public String likeEvent(@RequestParam Integer idEvento, @RequestParam String like, HttpSession session, Model model) {
        Utente utente = (Utente) session.getAttribute("user");
        if (utente == null) {
            return "redirect:/login";
        }
        Evento evento = eventoService.findById(idEvento);
        if (evento != null) {
            if(like.equals("NON HAI MESSO MI PIACE")) {
                EventiPreferiti likeEvento = new EventiPreferiti();
                likeEvento.setLiker(utente);
                likeEvento.setEvento(evento);
                eventiPreferitiService.save(likeEvento);
                model.addAttribute("liked", true);
            } else {
                EventiPreferiti likeEvento = eventiPreferitiService.getByLikerAndEvento(utente, evento);
                eventiPreferitiService.delete(likeEvento);
                model.addAttribute("liked", false);
            }
            // Add the evento object to the model
            model.addAttribute("evento", evento);
            model.addAttribute("likesNumber" , eventiPreferitiService.countAllByEvento(evento));
        }
        return "fragments/like :: like";
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

    public String eventChecks(Model model, Evento evento, Utente utente, Integer idEvento){
        model.addAttribute("evento", evento);
        if(eventiPreferitiService.getByLikerAndEvento(utente, evento) != null) {
            model.addAttribute("liked", true);
        } else {
            model.addAttribute("liked", false);
        }
        if(partecipazioneService.getPartecipazioneByEventoAndPartecipante(evento, utente) != null) {
            model.addAttribute("partecipato", true);
        } else {
            model.addAttribute("partecipato", false);
        }

        if(eventoService.findById(idEvento).getOrganizzatore().getEmail().equals(utente.getEmail())) {
            model.addAttribute("isOwner", true);
        } else {
            model.addAttribute("isOwner", false);
        }

        model.addAttribute("likesNumber" , eventiPreferitiService.countAllByEvento(evento));
        return "event";
    }

    @RestController
    @RequestMapping("/api/places")
    public class OsmPlaceController {

        @GetMapping("/osm-autocomplete")
        public ResponseEntity<List<Map<String, String>>> autocomplete(@RequestParam String query) {
            RestTemplate restTemplate = new RestTemplate();
            String requestString = "https://nominatim.openstreetmap.org/search?q="+query+"&format=json&limit=5";

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
