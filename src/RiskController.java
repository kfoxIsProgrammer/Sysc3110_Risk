import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Controller class to receive events from the view and send it to the Model
 * @author Kevin Fox, Omar Hashmi
 * @version 12.09.2020
 */
public class RiskController implements ActionListener, ChangeListener, MouseListener {
    /** The model to send commands to **/
    private final RiskModel model;
    private final RiskGUI view;
    private Phase phase;
    private Player player;
    private Map map;
    private int numBuffer;

    /** Constructor for RiskController **/
    public RiskController(RiskGUI view,RiskModel model, Map map){
        this.view=view;
        this.model=model;
        this.map=map;
    }

    /**
     * Setter for Phase
     * @param phase the phase to the set to
     */
    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Setter for player
     * @param player the player
     */
    public void setPlayer(Player player){
        this.player=player;
    }
    public void setMap(Map map){
        this.map=map;
    }

    /** A button is clicked **/
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Skip")){
            skip();
        }
        else if(e.getActionCommand().equals("Confirm")){
            confirm();
        }
        else if(e.getActionCommand().equals("Ok")){
            ok();
        }
        else if(e.getActionCommand().equals("Number Confirm")){
            number();
        }
        else if(e.getActionCommand().equals("Text Confirm")){
            text(((JTextField)e.getSource()).getText());
            ((JTextField)e.getSource()).setText("");
        }
        else if(e.getActionCommand().equals("Back")){
            back();
        }
        else if(e.getActionCommand().equals("Save Game")){
            saveGame();
        }
        else if(e.getActionCommand().split("/")[0].equals("saves")){
            loadGame(e.getActionCommand());
        }
        else if(e.getActionCommand().split("/")[0].equals("maps")){
            loadMap(e.getActionCommand());
        }
    }
    /** Save game button is pressed*/
    private void saveGame(){
        model.exportGame(view.saveGame());
    }
    /** Load game button is pressed*/
    private void loadGame(String filename){
        model.importGame(filename);
    }
    /** Load Map is pressed*/
    private void loadMap(String filename){
        model.importMap(filename);
    }
    /** confirm button is pressed*/
    private void confirm(){
        model.menuConfirm();
    }
    /** ok button is pressed*/
    private void ok(){
        model.menuOk();
    }
    /** skip button pressed*/
    private void skip(){
        model.menuSkip();
    }
    /** back button pressed*/
    private void back(){
        model.menuBack();
    }
    /** The slider is used*/
    private void number(){
        switch(phase){
            case NUM_HUMANS:
                model.numHumans(numBuffer);
                break;
            case NUM_AI:
                model.numAI(numBuffer);
                break;
            case INITIAL_DEPLOY_NUM_TROOPS:
            case DEPLOY_NUM_TROOPS:
            case ATTACK_NUM_TROOPS:
            case DEFEND_NUM_TROOPS:
            case RETREAT_NUM_TROOPS:
            case FORTIFY_NUM_TROOPS:
                model.numTroops(numBuffer);
                break;
        }
    }
    /** Player entered text */
    private void text(String text){
        if (phase == Phase.PLAYER_NAME) {
            model.playerName(text);
        }
    }

    /** The Slider is moved **/
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            numBuffer=source.getValue();
        }
    }

    /** Handles events when the map is clicked **/
    @Override
    public void mouseClicked(MouseEvent e){
        if(player.isAI){
            return;
        }

        Point point=new Point(e.getX(),e.getY());

        Country clickedCountry=map.pointToCountry(point);
        System.out.printf("(%d,%d):\t",point.x,point.y);
        if(clickedCountry==null){
            System.out.printf("No Country\n");
        }
        else{
            System.out.printf("%s\n",clickedCountry.getName());
            model.countrySelected(clickedCountry);
        }
    }
    /** Methods I need to implement otherwise java cries **/
    @Override
    public void mousePressed(MouseEvent e){}
    @Override
    public void mouseReleased(MouseEvent e){}
    @Override
    public void mouseEntered(MouseEvent e){}
    @Override
    public void mouseExited(MouseEvent e){}

}