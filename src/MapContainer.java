import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kshitij Sawhney
 * @version 11 / 6 / 20
 */
class MapContainer extends JPanel {
    private RiskController riskController;
    private boolean isActive;

    public void setActive(boolean active) {
        isActive = active;
    }

    public MapContainer(RiskController controller){
        riskController = controller;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                riskController.countrySelected(e);
            }
        });


    }
}