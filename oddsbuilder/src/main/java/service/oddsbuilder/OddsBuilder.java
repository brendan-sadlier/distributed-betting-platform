package service.oddsbuilder;

import service.core.Race;
import service.core.Horse;

public class OddsBuilder {
    /**
     * Odds generation:
     * The scores of all the horses all added up
     * The odds for each horse is inversely proportional to its score versus
     * of the sum of all scores of all horses in the race.
     * The higher a horses score, the lower its odds
     */
    public Race generateOdds(Race race) {
        Double sumOfScores = 0.0;
        for (Horse horse : race.horses){
            sumOfScores += horse.score;
        }
        for (int i = 0; i < race.horses.size(); i++){
            race.horseOdds.set(i, sumOfScores / race.horses.get(i).score);
        }
        return race;
    }
}
