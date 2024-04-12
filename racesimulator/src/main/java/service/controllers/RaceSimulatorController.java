package service.controllers;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.core.Horse;
import service.core.Race;
import service.racesimulator.RaceSimulator;

@RestController
public class RaceSimulatorController {
    private final Map<String, Horse[]> races = new TreeMap<>();
    private final RaceSimulator simulator = new RaceSimulator();

    @PostMapping(value="/races", consumes="application/json")
    public ResponseEntity<Horse[]> simulateRace(@RequestBody Race race) {
        Horse[] winners = simulator.simulateRace(race);
        races.put(race.dateAndTime.toString(), winners);
        String url = "http://" + getHost() + "/races/" + race.dateAndTime.toString();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(winners);
    }

    @GetMapping(value="/races/{raceTime}", produces={"application/json"})
    public ResponseEntity<Horse[]> getRace(@PathVariable String raceTime) {
        Horse[] winners = races.get(raceTime);
        if (winners == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(winners);
    }

    @Value("${server.port}")
    private int port;

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "localhost:" + port;
        }
    }
}