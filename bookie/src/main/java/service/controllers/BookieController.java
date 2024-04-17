package service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Horse;
import service.core.Race;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class BookieController {
    @Value("${server.port}")
    private int port;
    private List<Race> races = new ArrayList<>();
    RestTemplate template = new RestTemplate();

//    @GetMapping(value = "races/{raceTime}", consumes = "application/json")
//    public ResponseEntity<Horse> getWinner(@PathVariable String raceTime) {
//        String url = "http://localhost:8080/races/" + raceTime;
//        RestTemplate template = new RestTemplate();
//
//        ResponseEntity<Horse[]> response = template.getForEntity(url, Horse[].class);
//        Horse[] winners = response.getBody();
//
//        assert winners != null;
//        return ResponseEntity.status(HttpStatus.OK).body(winners[0]);
//    }

    @PostMapping(value = "races", consumes = "application/json")
    public ResponseEntity<Horse> getWinner(@RequestBody Race race) {
        String url = "http://" + getHost() + ":8081/races/" + race.dateAndTime;

        ResponseEntity<Horse[]> response = template.getForEntity(url, Horse[].class);
        Horse[] winners = response.getBody();

        assert winners != null;
        return ResponseEntity.status(HttpStatus.OK).body(winners[0]);
    }

    @GetMapping(value = "races", produces = "application/json")
    public ResponseEntity<Race> getRace() {
        String url = "http:// " + getHost() + "8083/generate_races";
        ResponseEntity<Race> response = template.getForEntity(url, Race.class);

        Race race = response.getBody();

        assert race != null;
        races.add(race);

        return ResponseEntity.ok(race);
    }

    @GetMapping(value = "all-races", produces = "application/json")
    public ResponseEntity<List<Race>> getAllRaces() {
        return ResponseEntity.status(HttpStatus.OK).body(races);
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":";
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
