import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is responsible for creating the viewport in which one plays the game.
 * @authour Kshitij Sawhney
 * @version 11 / 2 / 2020
 */

public class RiskView extends JFrame {

    private RiskModel riskmodel;
    private RiskController riskController;

    private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel mapPanel; //JLayeredPane needs the parent Container to have a null Layout Manager
    private JLayeredPane mapLayeredPane;
    private JPanel infoPanel;
    private JPanel optionPanel; // cardLayout with other panels that change based on the current phase
    private CardLayout cardLayout;
    private JPanel attackSelectAttackerPanel;
    private JPanel attackSelectDefenderPanel;

    private String mapImagePath ;//= "map.png";
    private BufferedImage mapImage;

    private JTextArea infoArea;
    private JLabel mapImageLabel;
    private String currentPhase;
    private JButton skipPhase;

    public RiskView(RiskModel riskmodel) throws IOException {
        riskController = new RiskController(riskmodel);
        mapImagePath = riskmodel.getMapImagePath();
        setTitle("Risk - Global Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mapPanel(riskController), gbc);
        /*ensures mapPanel is loaded into the frame so dimension information can be used by
        for correctly placing other panels
         */
        pack();

         // JTextArea in the top-right that gives constant information about the current state of the game like "player X's turn"
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;

        add(infoPanel(mapPanel.getHeight()), gbc);

        //JPanel in the bottom-right which changes with every move and gives next options at each successive update
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;

        add(optionPanel(mapPanel.getHeight()),gbc);
        pack();
        setResizable(false);
        setVisible(true);

    }

    private JPanel optionPanel(int height) {
        optionPanel = new JPanel();
        cardLayout = new CardLayout();
        optionPanel.setLayout(cardLayout);
        optionPanel.add(attackSelectAttackerPanel(),Phase.ATTACK_SRC.toString());
        optionPanel.add(attackSelectDefenderPanel(null),Phase.ATTACK_DST.toString());
        /*for future use
        *optionPanel.add(troopMoverPanelSelectSource(),"troopMoverPanelSelectSource");
        * *optionPanel.add(troopMoverPanelSelectDestination(),"troopMoverPanelSelectDestination");
        * optionPanel.add(deployPanelSelectDestination()."deployPanelSelectDestination")
         */

        cardLayout.show(attackSelectAttackerPanel,Phase.ATTACK_SRC.toString());   //for now game starts in attack phase
        return optionPanel;
    }

    private JPanel attackSelectDefenderPanel(Country Src) {
        //TODO add functionality
        attackSelectDefenderPanel = new JPanel();
        JLabel attackLabel = new JLabel("Select country to attack using " + Src);
        attackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        attackSelectDefenderPanel.add(attackLabel);
        return new JPanel();
    }

    private JPanel attackSelectAttackerPanel() {
        attackSelectAttackerPanel = new JPanel();
        attackSelectAttackerPanel.add(new JLabel("Select attacking country from map"));
        //TODO add functionality
        return attackSelectAttackerPanel;
    }

    private JPanel mapPanel(RiskController riskController) throws IOException {
        mapPanel = new MapPanel(riskController);
        mapPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        mapImage = ImageIO.read(new File(mapImagePath));

       labelCountries(riskmodel.getCountries());

        mapPanel.setLayout(null);
        mapLayeredPane.setLayout(null);
        mapLayeredPane.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());

        mapPanel.add(mapLayeredPane);

        return mapPanel;
    }

    private void insertMapImage() {
        mapPanel.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
        mapLayeredPane = new JLayeredPane();
        mapImageLabel = new JLabel(new ImageIcon(mapImage));
        mapImageLabel.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());
        //inserting into Layered pane
        mapLayeredPane.add(mapImageLabel, Integer.valueOf(1));
    }

    private JPanel infoPanel(int height) {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new LineBorder(Color.black));

        infoArea = new JTextArea();
        infoArea.setFocusable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        ((DefaultCaret) infoArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200, (int) height / 2));
        infoPanel.add(JP, 0);

        infoPanel.setSize(new Dimension(200, height));

        return infoPanel;
    }

    public void boardUpdate(AttackContext attackContext){
        //TODO update board with every move
        currentPhase = attackContext.phase.toString();
        switch(currentPhase){
            case Phase.ATTACK_DST.toString():
                attackSelectDefenderPanel(attackContext.srcCountry);
                cardLayout.show(attackSelectDefenderPanel,Phase.ATTACK_DST.toString());
                highlightAdjacentCountries(attackContext.highlightedCountries);
                break;
            case Phase.ATTACK_SRC: // for now, attack phase is default
            default:
                cardLayout.show(attackSelectAttackerPanel,Phase.ATTACK_SRC.toString());
                break;
        }
    }

    private void highlightAdjacentCountries(ArrayList<Country> countries){
        mapLayeredPane.removeAll();
        labelCountries(countries);
    }

    private void labelCountries(ArrayList<Country> countries) {
        insertMapImage();
        for(Country c: countries){
            JLabel countryLabel = new JLabel(c.getArmy()+"");
            countryLabel.setLocation(c.getVertices().get(0).getKey(),c.getVertices().get(0).getValue());
            countryLabel.setOpaque(true);
            countryLabel.setBackground(Color.blue);
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBounds(c.getVertices().get(0).getKey(),c.getVertices().get(0).getValue(),35,15);
        }
    }
}
