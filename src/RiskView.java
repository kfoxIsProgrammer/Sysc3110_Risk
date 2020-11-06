import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is responsible for creating the viewport in which one plays the game.
 * @author Kshitij Sawhney
 * @version 11 / 2 / 2020
 */

public class RiskView extends JFrame implements ActionListener {

    private RiskModel riskmodel;
    private RiskController riskController;

    private GridBagConstraints gbc = new GridBagConstraints();

    private JPanel mapPanel; //JLayeredPane needs the parent Container to have a null Layout Manager
    private JLayeredPane mapLayeredPane;
    private JPanel infoPanel;
    private JPanel optionPanel; // cardLayout with other panels that change based on the current phase
    private CardLayout cardLayout;
    private JPanel attackSrcPanel;
    private JPanel attackDstPanel;

    private String mapImagePath ;//= "map.png";
    private BufferedImage mapImage;

    private JTextPane infoArea;
    private JLabel mapImageLabel;
    private Phase currentPhase;
    private JPanel sidePanel;
    private JButton confirmPhase;
    private JPanel attackConfirmPanel;
    private JTextPane attackDstText;
    private JTextPane attackConfirmText;
    private JTextPane attackSrcText;
    private JPanel dicePanel;
    private JTextPane dicePanelText;

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

         // sidePanel for information, action and outcomes
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(sidePanel(mapPanel.getHeight()), gbc);
    }

    private JPanel optionPanel(int height) {
        optionPanel = new JPanel();
        cardLayout = new CardLayout();
        optionPanel.setLayout(cardLayout);
        optionPanel.setSize(200,height-40);
        attackSrcPanel = new JPanel();
        attackSrcText = new JTextPane();
        attackSrcText.setText("<player>, Select attacking country from map");
        attackSrcText.setFocusable(false);
        attackSrcText.setOpaque(false);
        attackSrcPanel.add(attackSrcText);
        optionPanel.add(attackSrcPanel,Phase.ATTACK_SRC.toString());

        attackDstPanel = new JPanel();
        attackDstText = new JTextPane();
        attackDstText = new JTextPane();
        attackDstText.setText("<player>, select country to be attacked");
        attackDstText.setFocusable(false);
        attackDstText.setOpaque(false);
        attackDstPanel.add(attackDstText);
        optionPanel.add(attackDstPanel,Phase.ATTACK_DST.toString());

        attackConfirmPanel = new JPanel();
        attackConfirmText =new JTextPane();
        attackConfirmText.setText("Confirm attack on <dst> using <src>");
        attackConfirmText.setFocusable(false);
        attackConfirmText.setOpaque(false);
        attackConfirmPanel.add(attackConfirmText);
        optionPanel.add(attackConfirmPanel,Phase.ATTACK_CONF.toString());

        dicePanel = new JPanel();
        dicePanelText = new JTextPane();
        dicePanelText.setText("<attacker> rolled <dice> and <defender> rolled <dice>");
        dicePanelText.setFocusable(false);
        dicePanelText.setOpaque(false);
        dicePanel.add(dicePanelText);
        optionPanel.add(dicePanel,Phase.ATTACK_DICE.toString());

        /*for future use
        * optionPanel.add(troopMoverPanelSelectSource(),"troopMoverPanelSelectSource");
        * optionPanel.add(troopMoverPanelSelectDestination(),"troopMoverPanelSelectDestination");
        * optionPanel.add(deployPanelSelectDestination()."deployPanelSelectDestination")
         */

        cardLayout.show(attackSrcPanel,Phase.ATTACK_SRC.toString());   //for now game starts in attack phase
        return optionPanel;
    }

    private void attackSelectDefenderPanel(Country Src) {
        attackDstText.setText("Select country to attack using "+ Src);
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

    private JPanel sidePanel(int height) {
        sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setBorder(new LineBorder(Color.black));

       sidePanel.add(infoPanel(height/2),BorderLayout.NORTH);
       sidePanel.add(optionPanel(height/2),BorderLayout.CENTER);

       confirmPhase = new JButton();
       confirmPhase.setSize(200, 40);
       confirmPhase.addActionListener(riskController);
       sidePanel.add(confirmPhase,BorderLayout.SOUTH);

       sidePanel.setSize(new Dimension(200, height));

        return sidePanel;
    }

    private JPanel infoPanel(int height) {
        infoPanel = new JPanel();
        infoArea = new JTextPane();
        infoArea.setFocusable(false);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200, height));
        infoPanel.add(JP);
        return infoPanel;
    }

    public void boardUpdate(ActionContext context){
        if(context.phase == Phase.ATTACK_ARMY|| context.phase==Phase.ATTACK_CONF||context.phase==Phase.ATTACK_DICE||context.phase==Phase.ATTACK_DST){
            AttackContext attackContext = (AttackContext)(context);
            currentPhase =attackContext.phase;
            confirmPhase.setActionCommand(currentPhase.toString());

            switch(currentPhase){
                case Phase.ATTACK_DST.toString():
                    highlightAdjacentCountries(context.highlightedCountries); //TODO convert to country[]
                    attackSelectDefenderPanel(attackContext.srcCountry); // to update the label in the panel
                    cardLayout.show(attackDstPanel,Phase.ATTACK_DST.toString());
                    confirmPhase.setText("Confirm Defender");
                    break;

                case Phase.ATTACK_CONF.toString():
                    attackConfirmPanel(attackContext.dstCountry,attackContext.srcCountry);
                    cardLayout.show(attackConfirmPanel,Phase.ATTACK_CONF.toString());
                    confirmPhase.setText("Attack");

                case Phase.ATTACK_DICE.toString():
                    dicePanel(attackContext.diceRolls, attackContext.srcCountry.getOwner(), attackContext.dstCountry.getOwner());
                    cardLayout.show(dicePanel,Phase.ATTACK_DICE.toString());
                    confirmPhase.setText("Ok");

                case Phase.ATTACK_SRC.toString(): // for now, attack phase is default
                default:
                    attackSelectAttackerPanel(context.player);
                    cardLayout.show(attackSrcPanel,Phase.ATTACK_SRC.toString());
                    confirmPhase.setText("Confirm Attacker");
                    break;
            }
        }
    }

    private void dicePanel(int[][] diceRolls, Player attacker ,Player defender) {
        dicePanelText.setText(""); //make sure its empty
        dicePanelText.setText(attacker.getName() + " rolled\n"+ Arrays.toString(diceRolls[0]) + "\n and " +defender.getName()+ "rolled\n"+ Arrays.toString(diceRolls[1]));
    }

    private void attackSelectAttackerPanel(Player player) {
        attackSrcText.setText(player+", select attacking country from map");
    }

    private void attackConfirmPanel(Country dstCountry, Country srcCountry) {
        attackConfirmText.setText("Confirm attack on " + dstCountry+ " using "+srcCountry);
    }

    private void highlightAdjacentCountries(ArrayList<Country> countries){
        labelCountries(countries);
    }

    private void labelCountries(ArrayList<Country> countries) {
        mapLayeredPane.removeAll();
        insertMapImage();
        for(Country c: countries){
            JLabel countryLabel = new JLabel(c.getArmy()+"");
            countryLabel.setLocation(c.getVertices().get(0).x,c.getVertices().get(0).y);
            countryLabel.setOpaque(true);
            countryLabel.setBackground(Color.blue); //TODO convert to Country.getOwner.getColor()
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBounds(c.getVertices().get(0).x,c.getVertices().get(0).y,35,15);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Confirm Defender")){
            labelCountries(riskmodel.getCountries());
        }
        riskController.actionPerformed(e);
    }
}
