package service;


import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.core.Race;
import service.repositories.HorseRepository;
import service.core.Horse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class RaceGeneratorService {

    private static final String[] COURSES = {"Curragh", "Dundalk", "Fairyhouse", "Leopardstown", "Punchestown", "Thurles"};
    private static final String[] TYPES = {"Cup", "Derby", "Gala", "Sprint", "Classic"};

    @Autowired
    HorseRepository horseRepository;

    public Race generateRace() {

        List<Horse> horses = horseRepository.findAll();
        List<Double> odds = new ArrayList<>();

        System.out.println("Fetched " + horses.size() + " horses from the database.");
        Collections.shuffle(horses, new Random(System.currentTimeMillis()));
        horses = horses.subList(0, Math.min(10, horses.size()));

        String raceName = generateRandomCourse() + " " + generateRandomType();

        LocalDateTime raceDateTime = LocalDateTime.now().plusMinutes(2);

        for (Horse horse : horses) {
            odds.add((double) (horse.score / horses.size()));
        }

        return new Race(raceName, raceDateTime, horses, odds, false);

    }

    public List<Horse> getHorses() {
        return horseRepository.findAll();
    }

    private String generateRandomCourse() {
        return COURSES[new Random().nextInt(COURSES.length)];
    }

    private String generateRandomType() {
        return TYPES[new Random().nextInt(TYPES.length)];
    }
}
