package service.core;

import java.util.Date;

public class Horse {
    public Horse(String horseName, String jockey, String trainer, Integer age, Integer score) {
        this.horseName = horseName;
        this.jockey = jockey;
        this.trainer = trainer;
        this.age = age;
        this.score = score;
    }

    public Horse() {}

    public String horseName;
    public String jockey;
    public String trainer;
    public Integer age;
    public Integer score;
}
