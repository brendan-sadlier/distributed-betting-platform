package service.core;

import java.util.Date;
import java.util.List;

public class Race {

    public Race(String name, Date dateAndTime, List<Horse> horses, List<Double> horseOdds, boolean hasRan) {
        this.raceName = name;
        this.dateAndTime = dateAndTime;
        this.horses = horses;
        this.horseOdds = horseOdds;
        this.hasRan = hasRan;
    }

    public Race() {}

    public String raceName;
    public Date dateAndTime;
    public List<Horse> horses;
    public List<Double> horseOdds;
    public boolean hasRan;
}
