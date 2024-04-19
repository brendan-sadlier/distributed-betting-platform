package service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import service.core.Horse;
import service.RaceGeneratorService;
import service.core.Race;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
public class RaceGeneratorController {

    @Value("${server.port}")
    private int port;

    private final RaceGeneratorService RGService;

    // Please do not remove this as it fucks up the Database if it is not here
    @Autowired
    public RaceGeneratorController(RaceGeneratorService RGService) {
        this.RGService = RGService;
    }

    @PostMapping(value = "/generate-races", produces = "application/json")
    public ResponseEntity<Race> generateRace() {

        String url = "http://localhost:8082/odds";
        Race race = RGService.generateRace();
        RestTemplate template = new RestTemplate();
        HttpEntity<Race> request = new HttpEntity<>(race);

        ResponseEntity<Race> response = template.postForEntity(url, request, Race.class);
        Race raceWithOdds = response.getBody();

        assert raceWithOdds != null;
        return ResponseEntity.ok(raceWithOdds);
    }

    @GetMapping(value = "/get-horses")
    public ResponseEntity<List<Horse>> getHorses() {
        List<Horse> horses = RGService.getHorses();

        return ResponseEntity.ok(horses);
    }

}
