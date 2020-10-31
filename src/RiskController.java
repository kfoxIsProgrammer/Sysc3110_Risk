import java.awt.event.MouseEvent;

public class RiskController {

    private RiskModel riskModel;
    private RiskView  riskView;

    public RiskController(){
        this.riskModel = model;
        this.riskView = view;
    }

    /**
     * NewGame method to create a new game and set it up
     * @param players the number of players
     * @param playerNames the player names
     * @return the model of the new game to be attached to the view
     */
    public model newGame(int players, String[] playerNames){
        this.riskModel = sendNewGameModel(new RiskModel(players, playerNames));
        view.sendNewGameModel(riskModel);
    }


    /**
     * A controller event to ask the risk model which country was clicked first
     * @param mouseEvent the event of which button is pressed
     * @return the country that is pressed
     */
    public Country getFirstCountry(MouseEvent mouseEvent){
        view.sendCountryToFocus(model.getCountryByCoordinate(mouseEvent.getX(), mouseEvent.getY()));
    }

    /**
     * A controller event to ask the risk model which country was clicked second
     * @param mouseEvent the event of which button is pressed
     * @return the country that is pressed
     */
    public Country getSecondCountry(MouseEvent mouseEvent){
        view.sendCountryToAttack(model.getCountryByCoordinate(mouseEvent.getX(), mouseEvent.getY()));
    }

    /**
     * Send the battleObject from the model attack, to show the output of a battle
     * @param focusedCountry attacking Country
     * @param countryToBeAttacked defending country
     * @param numberOfUnits number of units attacking
     * @return
     */
    public BattleObject getBattleOutcome(Country focusedCountry, Country countryToBeAttacked, int numberOfUnits){
        model.getAttackOutcome(focusedCountry, countryToBeAttacked, numberOfUnits);
    }

}
