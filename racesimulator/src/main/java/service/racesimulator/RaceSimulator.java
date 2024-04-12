package service.racesimulator;

import service.core.Horse;
import service.core.Race;

public class RaceSimulator {

    /*
        Weighted random selection is used to ensure that horses with higher
        scores have a higher chance of winning.
    */
    public Horse[] simulateRace(Race race){
        Horse[] winners = new Horse[3];
        RandomCollection<Horse> rc = new RandomCollection<Horse>();

        for (Horse horse : race.horses){
            rc.add(horse.score, horse);
        }
        for (int i = 0; i < 3; i++){
            winners[i] = rc.next();
        }
        return winners;
    }

}


