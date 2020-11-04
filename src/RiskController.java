import java.awt.event.MouseEvent;

public class RiskController implements ActionListener {
    private RiskModel riskModel;

    public RiskController(RiskModel model){
        this.riskModel = model;
        this.riskView = view;
    }

    /**
     * NewGame method to create a new game and set it up
     * @param players the number of players
     * @param playerNames the player names
     * @return the model of the new game to be attached to the view
     */
    public void startNewGame(int players, String[] playerNames){
        this.riskModel.startNewGame(players, playerNames);
    }


    /**
     * A controller event to ask the risk model which country was clicked first
     * @param mouseEvent the event of which button is pressed
     * @return the country that is pressed
     */
    public void countrySelected(MouseEvent mouseEvent){
        this.riskModel.countryHasBeenSelected(mouseEvent.getX(), mouseEvent.getY());
    }

    /**
     * This command is for dealing with button clicks, mostly army number
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.riskModel.sendAction(e.getActionCommand());
    }
*/
}
