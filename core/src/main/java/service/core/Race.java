package service.core;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Race {

    public Race(String name, LocalDateTime dateAndTime, List<Horse> horses, List<Double> horseOdds, boolean hasRan) {
        this.raceName = name;
        this.dateAndTime = dateAndTime;
        this.horses = horses;
        this.horseOdds = horseOdds;
        this.hasRan = hasRan;
    }

    public Race() {}

    public String raceName;
    public LocalDateTime dateAndTime;
    public List<Horse> horses;
    public List<Double> horseOdds;
    public boolean hasRan;
}
