import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Controller class to receive events from the view and send it to the Model
 * @author Kevin Fox
 * @version 11.09.2020
 */
public class RiskController implements ActionListener, ChangeListener, MouseListener {
    /** The model to send commands to **/
    private final RiskModel riskModel;

    /** Constructor for RiskController **/
    public RiskController(RiskModel model){
        this.riskModel = model;
    }
    public RiskController(){
        this.riskModel=new RiskModel(new String[]{"Volvo", "BMW", "Ford", "Mazda"});
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

    /** Handles events when a menu item is clicked **/
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Skip")){
            riskModel.menuSkip();
        }
        else if(e.getActionCommand().equals("names")){
            //TODO handle new game
        }
        else if(e.getActionCommand().equals("Confirm")){
            riskModel.menuConfirm();
        }
        else if(e.getActionCommand().equals("Back")){
            riskModel.menuBack();
        }
        else if(e.getActionCommand().equals(Phase.NUM_PLAYERS.name())){
            riskModel.menuConfirm();
        }
        else if(e.getActionCommand().equals(Phase.PLAYER_NAME.name())){
            riskModel.menuConfirm();
        }
        else{
            riskModel.textEntered(e.getActionCommand());
            System.out.printf("%s\n",e.getActionCommand());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            riskModel.sliderMoved(source.getValue());
        }
    }

    /** Handles events when the map is clicked **/
    @Override
    public void mouseClicked(MouseEvent e) {
        riskModel.mapClicked(new Point(e.getX(),e.getY()));
    }
    /** Poopy methods I need to implement otherwise java cries **/
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseReleased(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e){}
    @Override
    public void mouseExited(MouseEvent e){}
}