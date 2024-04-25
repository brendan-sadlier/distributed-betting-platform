package service.core;

public class Bet {

    public Bet(String horseName, Integer amount) {
        this.horseName = horseName;
        this.amount = amount;
    }

    public Bet() {}
    public String horseName;
    public Integer amount;
}
