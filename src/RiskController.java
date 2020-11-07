import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Controller class to receive events from the view and send it to the Model
 * @author Kevin Fox
 * @version 11.07.2020
 */
public class RiskController implements ActionListener {

    /** Model to send commands to **/
    private RiskModel riskModel;
    /**
     * 1 param constructor for testing
     * @param model the model to send commands to
     */
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

    /**
     * Helper function to check if any chars in a string are ints
     * @param text the text to check
     * @return boolean True: there is a digit, False: there are no digits
     */
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
        //System.out.printf("{\"x\":%d,\"y\":%d},\n",mouseEvent.getX(),mouseEvent.getY());
    }
    /**
     * This command is for dealing with button clicks, mostly army number
     *
     * @param e the button click event from the view
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