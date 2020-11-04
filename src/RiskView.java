/**
 * This is responsible for creating the viewport in which one plays the game.
 * @authour Kshitij Sawhney
 * @version 11 / 2 / 2020
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RiskView extends JFrame {

    private RiskModel riskmodel;
    private RiskController riskController;

    private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel mapPanel; //JLayeredPane needs the parent Container to have a null Layout Manager
    private JLayeredPane mapLayeredPane;
    private JPanel infoPanel;

    private String path = "map.png";
    private BufferedImage mapImage= ImageIO.read(new File(path));

    private JTextArea infoArea;
    private JLabel mapImageLabel;
    private JLabel currentPhase;
    private JButton skipPhase;

    public RiskView(RiskModel riskmodel) throws IOException {
        setTitle("Risk - GLobal Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mapPanel(),gbc);
        /*ensures mapPanel is loaded into the frame so dimension information can be used by
        for correctly placing other panels
         */
        pack();

        gbc.anchor=GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;

        add(infoPanel(mapPanel.getHeight()),gbc);

        pack();
        setResizable(false);
        setVisible(true);

    }
    private JPanel mapPanel() throws IOException {
        mapPanel = new MapPanel();
        mapPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        mapImage = ImageIO.read(new File(path));

        mapPanel.setPreferredSize(new Dimension(mapImage.getWidth(),mapImage.getHeight()));

        mapLayeredPane = new JLayeredPane();
        //inserting into Layered pane
        mapImageLabel = new JLabel(new ImageIcon(mapImage));
        mapImageLabel.setBounds(0,0,mapImage.getWidth(),mapImage.getHeight());
        mapLayeredPane.add(mapImageLabel, Integer.valueOf(1));

        mapPanel.setLayout(null);
        mapLayeredPane.setLayout(null);
        mapLayeredPane.setBounds(0,0,mapImage.getWidth(),mapImage.getHeight());

        mapPanel.add(mapLayeredPane);

        return mapPanel;
    }

    private JPanel infoPanel(int height){
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
        infoPanel.setBorder(new LineBorder(Color.black));

        infoArea = new JTextArea();
        infoArea.setFocusable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        ((DefaultCaret)infoArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200,(int)height/2));
        infoPanel.add(JP,0);

        infoPanel.setSize(new Dimension(200,height));

        return infoPanel;
    }

    public void boardUpdate(RiskModel riskModel){

    }

    public static void main(String[] args) throws Exception{
        new RiskView();
        //TODO create board render based on currentl game state
    }
}


class MapPanel extends JPanel{
    public MapPanel(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(e.getX()+" "+e.getY());
                RiskController.countrySelected(e);
            }
        });
    }
}
