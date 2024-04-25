package service.core;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Race {

    public Race(String name, LocalDateTime dateAndTime, List<Horse> horses, List<Double> horseOdds, boolean hasRan) {
        this.raceName = name;
        this.dateAndTime = dateAndTime.toString();
        this.horses = horses;
        this.horseOdds = horseOdds;
        this.hasRan = hasRan;
    }

    public Race() {}

    public String raceName;
    public String dateAndTime;
    public List<Horse> horses;
    public List<Double> horseOdds;
    public boolean hasRan;
    public String raceEndpoint;
}
