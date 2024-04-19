package service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Bet;
import service.core.Horse;
import service.core.Race;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@RestController
public class BookieController {
    @Value("${server.port}")
    private int port;
    private List<Race> races = new LinkedList<>();
    RestTemplate template = new RestTemplate();

    @PostMapping(value = "races", consumes = "application/json")
    public ResponseEntity<Horse> getWinner(@RequestBody Race race) {
        String url = "http://" + getHost() + "8081/races/" + race.dateAndTime;

        ResponseEntity<Horse[]> response = template.getForEntity(url, Horse[].class);
        Horse[] winners = response.getBody();

        assert winners != null;
        return ResponseEntity.status(HttpStatus.OK).body(winners[0]);
    }

    @PostMapping(value = "races", consumes = "application/json")
    public ResponseEntity<String> getWinner(@RequestBody Bet bet) {
        Race race = races.get(races.size()-1);
        String url = "http://" + getHost() + "8081/races/" + race.dateAndTime;

        ResponseEntity<Horse[]> response = template.getForEntity(url, Horse[].class);
        Horse[] winners = response.getBody();
        assert winners != null;

        Horse winner = winners[0];
        int horsePosition = race.horses.indexOf(winner);
        Double odds = race.horseOdds.get(horsePosition);

        String result;
        if (bet.horse == winner) {
            result = "Congratulations, your horse was the winner, you won " +
                     getReward(bet.amount, odds);
        }
        else {
            result = "You lost!";
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "races", produces = "application/json")
    public ResponseEntity<Race> getRace() {
        String urlRace = "http:// " + getHost() + "8083/generate-races";
        ResponseEntity<Race> response = template.getForEntity(urlRace, Race.class);

        Race race = response.getBody();

        assert race != null;
        races.add(race);

        return ResponseEntity.ok(race);
    }

    @GetMapping(value = "all-races", produces = "application/json")
    public ResponseEntity<List<Race>> getAllRaces() {
        return ResponseEntity.status(HttpStatus.OK).body(races);
    }

    private Double getReward(Integer betSize, Double odds) {
        return betSize.doubleValue() * odds;
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":";
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
