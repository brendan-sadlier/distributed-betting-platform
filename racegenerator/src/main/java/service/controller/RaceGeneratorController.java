package service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.core.Horse;
import service.RaceGeneratorService;
import service.core.Race;

import java.util.Arrays;
import java.util.List;

@RestController
public class RaceGeneratorController {

    @Value("${server.port}")
    private int port;

    private final RaceGeneratorService RGService = new RaceGeneratorService();

    @GetMapping(value = "/generate-races", produces = "application/json")
    public ResponseEntity<Race> generateRace() {
        Race generatedRace = RGService.generateRace();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(generatedRace);
    }

}
