import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;


public class RiskController implements ActionListener {
    private RiskModel riskModel;

    public RiskController(RiskModel model){
        this.riskModel = model;
    }

    /**
     * NewGame method to create a new game and set it up
     *
     * @param players the number of players
     * @param playerNames the player names
     * @return the model of the new game to be attached to the view
     */
    public void startNewGame(int players, String[] playerNames){
        //this.riskModel.newGame(players, playerNames);
    }

    private boolean isInt(String text){
        for (char c: text.toCharArray()) {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    /**
     * A controller event to ask the risk model which country was clicked first
     *
     * @param mouseEvent the event of which button is pressed
     * @return the country that is pressed
     */
    public void countrySelected(MouseEvent mouseEvent){
        this.riskModel.mapClicked(new Point(mouseEvent.getX(), mouseEvent.getY()));
    }
    /**
     * This command is for dealing with button clicks, mostly army number
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("skip")){
            riskModel.menuSkip();
        }
        else if(e.getActionCommand().equals("confirm")){
            riskModel.menuConfirm();
        }
        else if(e.getActionCommand().equals("back")){
            riskModel.menuBack();
        }
        else if(isInt(e.getActionCommand())){
            riskModel.menuNumTroops(Integer.parseInt(e.getActionCommand()));
        }
    }
}