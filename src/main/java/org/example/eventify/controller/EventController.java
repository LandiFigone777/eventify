package org.example.eventify.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpSession;

@Controller
public class EventController {
    

    @GetMapping("/addEvent")
    public String addEventForm(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "addEvent";
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
