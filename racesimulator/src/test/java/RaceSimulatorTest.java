import org.junit.Assert;
import org.junit.Test;
import service.core.Horse;
import service.core.Race;
import service.racesimulator.RaceSimulator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RaceSimulatorTest {

    @Test
    public void testRaceSimulator(){
        RaceSimulator simulator = new RaceSimulator();
        List<Horse> horses = new ArrayList<>();
        horses.add(new Horse("horse1", "jockey1", "trainer1", 5, 20));
        horses.add(new Horse("horse2", "jockey2", "trainer2", 3, 30));
        horses.add(new Horse("horse3", "jockey3", "trainer3", 6, 40));
        horses.add(new Horse("horse4", "jockey4", "trainer4", 1, 60));
        horses.add(new Horse("horse5", "jockey5", "trainer5", 2, 90));
        Race race = new Race("race1", LocalDateTime.now(), horses, new ArrayList<>(), false);
        Horse[] winners = simulator.simulateRace(race);
        Assert.assertEquals(3, winners.length);
    }
}
