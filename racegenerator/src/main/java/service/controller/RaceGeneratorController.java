package service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.core.Horse;
import service.RaceGeneratorService;
import service.core.Race;

import java.util.Arrays;
import java.util.List;

@RestController
public class RaceGeneratorController {

    private final RaceGeneratorService RGService;

    @Autowired
    public RaceGeneratorController(RaceGeneratorService raceGeneratorService) {
        this.RGService = raceGeneratorService;
    }

    @GetMapping("/generate_races")
    public Race generateRace() {
        return RGService.generateRace();
    }
}
