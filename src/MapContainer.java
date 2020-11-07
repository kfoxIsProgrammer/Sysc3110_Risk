import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The clickable container that will send mouseEvents to the controller.
 * Only when the map is clicked that these events fire.
 * @author Kshitij Sawhney, Kevin Fox (Documentation)
 * @version 11 / 6 / 20
 */
class MapContainer extends JPanel {
    /** RiskController that recieves MouseEvents**/
    private RiskController riskController;
    /**Boolean flag to allow click events if the phase is correct**/
    private boolean isActive;

    /**
     * 1 param constructor
     * @param controller that recieves MouseEvent clicks
     */
    public MapContainer(RiskController controller){
        riskController = controller;
        addMouseListener(new MouseAdapter() {
            @Override
            /**
             * Adding on MouseClick to send the event to the Controller
             */
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                riskController.countrySelected(e);
            }
        });
    }
    /**
     * Setter for isActive
     * @param active boolean True: allow, False: Disable
     */
    public void setActive(boolean active) {
        isActive = active;
    }
}