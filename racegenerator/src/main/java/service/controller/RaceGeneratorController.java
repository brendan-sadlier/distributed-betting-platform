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

    @GetMapping("/test-race")
    public Race testRace() {
        List<Horse> horses = Arrays.asList(
                new Horse("Horse 1", "Brendan", "Sadlier", 3, 100),
                new Horse("Horse 2", "John", "Doe", 4, 100),
                new Horse("Horse 3", "Jane", "Doe", 5, 100),
                new Horse("Horse 4", "John", "Smith", 6, 100),
                new Horse("Horse 5", "Jane", "Smith", 7, 100)
        );

        return new Race("Test Race", "Now", horses, Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0), false);
    }
}
