package service.oddsbuilder;

import service.core.Race;

public class OddsBuilder {
    // All references are to be prefixed with an AF (e.g. AF001000)
    public static final String PREFIX = "AF";
    public static final String COMPANY = "Auld Fellas Ltd.";

    /**
     * Quote generation:
     * 30% discount for being male
     * 20% increase for being female
     * 10% discount for males over 50
     * additional 10% discount for males over 60
     */
    public Race generateOdds(Race race) {
        return race;
    }
}
