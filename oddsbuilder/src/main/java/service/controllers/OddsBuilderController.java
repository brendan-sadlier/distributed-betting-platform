package service.controllers;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.core.Race;
import service.oddsbuilder.OddsBuilder;

@RestController
public class OddsBuilderController {
    private Map<String, Race> races = new TreeMap<>();
    private OddsBuilder oddsBuilder = new OddsBuilder();

    @PostMapping(value="/quotations", consumes="application/json")
    public ResponseEntity<Race> createQuotation(@RequestBody Race race) {
        Race raceWithOdds = oddsBuilder.generateOdds(race);
        String url = "http://" + getHost() + "/odds/" + raceWithOdds.dateAndTime.toString();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(raceWithOdds);
    }

    @GetMapping(value="/quotations/{raceTime}", produces={"application/json"})
    public ResponseEntity<Race> getQuotation(@PathVariable String raceTime) {
        Race race = races.get(raceTime);
        if (race == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(race);
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