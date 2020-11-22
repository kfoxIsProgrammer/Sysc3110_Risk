import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Controller class to receive events from the view and send it to the Model
 * @author Kevin Fox
 * @version 11.09.2020
 */
public class RiskController implements ActionListener, ChangeListener {
    /** The model to send commands to **/
    private final RiskModel riskModel;

    /** Constructor for RiskController **/
    public RiskController(RiskModel model){
        this.riskModel = model;
    }
    public RiskController(){
        this.riskModel=new RiskModel(new String[]{"Volvo", "BMW", "Ford", "Mazda"});
    }
    /** Handles the new game event **/

    public void newGame(String[] playerNames){
        this.riskModel.newGame(playerNames);
    }
    /** Checks if a given string can be parsed into an integer **/
    private boolean isInt(String text){
        for (char c: text.toCharArray()) {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    /** Handles events when the map is clicked **/
    public void countrySelected(MouseEvent mouseEvent){
        this.riskModel.mapClicked(new Point(mouseEvent.getX(), mouseEvent.getY()));
    }
    /** Handles events when a menu item is clicked **/
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("skip")){
            riskModel.menuSkip();
        }
        else if(e.getActionCommand().equals("names")){
            //TODO handle new game
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
        else if(e.getActionCommand().equals(Phase.NUM_PLAYERS.name())){
            riskModel.menuConfirm();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            riskModel.sliderMoved(source.getValue());
        }
    }
}