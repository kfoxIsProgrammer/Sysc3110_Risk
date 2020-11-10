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
public class RiskController implements ActionListener{
    private RiskModel riskModel;

    public RiskController(RiskModel model){
        this.riskModel = model;
    }

    public void newGame(String[] playerNames){
        this.riskModel.newGame(playerNames);
    }

    private boolean isInt(String text){
        for (char c: text.toCharArray()) {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    public void countrySelected(MouseEvent mouseEvent){
        this.riskModel.mapClicked(new Point(mouseEvent.getX(), mouseEvent.getY()));
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("skip")){
            riskModel.menuSkip();
        }
        else if(e.getActionCommand().equals("names")){

        }
        else if(e.getActionCommand().equals("confirm")){
            riskModel.menuConfirm();
        }
        else if(e.getActionCommand().equals("back")){
            riskModel.menuBack();
        }
        else if(e.getActionCommand().equals("Forfeit")){
            riskModel.playerForfeit();

        }
        else if(isInt(e.getActionCommand())){
            riskModel.menuNumTroops(Integer.parseInt(e.getActionCommand()));
        }
    }
}