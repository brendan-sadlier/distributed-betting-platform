package service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.core.Race;
import service.repositories.HorseRepository;
import service.core.Horse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class RaceGeneratorService {

    private static final String[] HOURS = {"10:00", "14:00", "16:00", "20:00"};
    private static final String[] COURSES = {"Curragh", "Dundalk", "Fairyhouse", "Leopardstown", "Punchestown", "Thurles"};
    private static final String[] TYPES = {"Cup", "Derby", "Gala", "Sprint", "Classic"};

    @Autowired
    private HorseRepository horseRepository;

    public Race generateRace() {

        List<Horse> horses = horseRepository.findAll();
        List<Double> odds = new ArrayList<>();

        System.out.println("Fetched " + horses.size() + " horses from the database.");
        Collections.shuffle(horses, new Random(System.currentTimeMillis()));
        horses = horses.subList(0, Math.min(10, horses.size()));

        String raceName = generateRandomCourse() + " " + generateRandomType();
        String raceDateAndTime = LocalDate.now().toString() + " " + generateRandomTime();

        for (Horse horse : horses) {
            odds.add((double) (horse.score / horses.size()));
        }

        return new Race(raceName, raceDateAndTime, horses, odds, false);

    }



    private String generateRandomTime() {
        return HOURS[new Random().nextInt(HOURS.length)];
    }

    private String generateRandomCourse() {
        return COURSES[new Random().nextInt(COURSES.length)];
    }

    private String generateRandomType() {
        return TYPES[new Random().nextInt(TYPES.length)];
    }
}
