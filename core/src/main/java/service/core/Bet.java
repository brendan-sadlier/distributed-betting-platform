package service.core;

public class Bet {

    public Bet(Horse horse, Integer amount) {
        this.horse = horse;
        this.amount = amount;
    }

    public Bet() {}

    public Horse horse;
    public Integer amount;
}
