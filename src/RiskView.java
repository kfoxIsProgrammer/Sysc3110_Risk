import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * This is responsible for creating the viewport.
 * @author Kshitij Sawhney
 * @version 11 / 2 / 2020
 */

public class RiskView extends JFrame implements ActionListener {
    /**Controller for the view*/
    private final RiskController riskController;
    /**container for the MapLayeredPane*/
    private JPanel mapContainer; //JLayeredPane needs the parent Container to have a null Layout Manager
    /**JLayeredPane that contains the mapImage in the background and labels in the foreground */
    private JLayeredPane mapLayeredPane;
    /**Layout for OptionPane that permits swapping views*/
    private CardLayout cardLayout;
    /**cardlayout subpanel for ATTACK_SRC phase*/
    private JPanel attackSrcPanel;
    /**cardlayout subpanel for ATTACK_DST phase*/
    private JPanel attackDstPanel;
    /**cardlayout subpanel for ATTACK_ARMY phase*/
    private JPanel attackConfirmPanel;
    /**cardlayout subpanel for ATTACK_DICE phase*/
    private JPanel dicePanel;
    /**Image background for the MapPanel*/
    private final BufferedImage mapImage;
    /**Button to confirm completion of current phase*/
    private JButton confirmPhase;
    /**JTextPane for attackSrcPanel*/
    private JTextPane attackSrcText;
    /**JTextPane for attackDstPanel*/
    private JTextPane attackDstText;
    /**JTextPane for attackConfirmPanel*/
    private JTextPane attackConfirmText;
    /**JTextPane for attackConfirmPanel*/
    private JTextPane dicePanelText;
    /**Array of all Country objects in the map*/ //TODO use this for updating labels
    private final Country[] countryArray;
    /**JTextArea that gets updated as the game goes on**/
    private JTextArea infoArea;

    /**constructor for RiskView
     * @param controller controller for the RiskView
     * @param modelMapImage Buffered image to be used as the map background
     * @param countries Array of all countries on the map
     */
    public RiskView(RiskController controller, BufferedImage modelMapImage, Country[] countries){
        //TODO info update , use country.getCentercoords
        mapImage = modelMapImage;
        riskController = controller;
        countryArray = countries;

        setTitle("Risk - Global Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        //Constraints for the placement of MapPanel,SidePanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mapPanel(), gbc);

        // sidePanel for information, action and outcomes
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(sidePanel(mapContainer.getHeight()), gbc);

        pack();
        this.setVisible(true);
    }

    /**
     * Creates a mapPanel tied to a controller
     * @return the mapPanel with a corresponding background
     */
    private JPanel mapPanel() {
        mapContainer = new MapContainer(riskController);
        mapContainer.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        mapContainer.setLayout(null);
        insertMapImage(); // add image in the background
        labelCountries(countryArray,true); // add labels for countries initially
        mapContainer.add(mapLayeredPane);

        return mapContainer;
    }
    /**
     * inserts mapImage in the background of the map
     */
    private void insertMapImage() {
        mapLayeredPane = new JLayeredPane();
        mapLayeredPane.setLayout(null);
        mapLayeredPane.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());
        mapContainer.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
        JLabel mapImageLabel = new JLabel(new ImageIcon(mapImage));
        mapImageLabel.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());
        mapLayeredPane.add(mapImageLabel, Integer.valueOf(1)); //inserting into Layered pane
    }
    /**
     * creates the sidePanel and populates it with infoPanel,OptionPanel and buttons
     * @param height total height of the window
     * @return the complete sidePanel
     */
    private JPanel sidePanel(int height) {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setBorder(new LineBorder(Color.black));
        JPanel buttonPanel = new JPanel();

        sidePanel.add(infoPanel(height / 2), BorderLayout.NORTH);
        sidePanel.add(optionPanel(height / 2), BorderLayout.CENTER);

        confirmPhase = new JButton();
        confirmPhase.setSize(150, 40);

        JButton skipButton = new JButton();
        skipButton.setSize(50, 40);
        skipButton.setActionCommand("SkipPhase");


        confirmPhase.addActionListener(riskController);
        skipButton.addActionListener(riskController);

        buttonPanel.add(confirmPhase, BorderLayout.WEST);
        buttonPanel.add(skipButton, BorderLayout.EAST);

        sidePanel.setSize(new Dimension(200, height));

        return sidePanel;
    }
    /**
     * creates the infoPanel
     * @param height total height of the window
     * @return the complete infoPanel
     */
    private JPanel infoPanel(int height) {
        JPanel infoPanel = new JPanel();
        infoArea = new JTextArea();
        infoArea.setFocusable(false);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200, height));
        infoPanel.add(JP);
        return infoPanel;
    }
    /**
     * Creates the optionPanel, adjusted for a given height
     * @param height total height of the image
     * @return the created optionPanel
     */
    private JPanel optionPanel(int height) {
        // cardLayout with other panels that change based on the current phase
        JPanel optionPanel = new JPanel();
        cardLayout = new CardLayout();

        optionPanel.setLayout(cardLayout);
        optionPanel.setSize(200, height - 40);

        //creating attackSrcPanel
        attackSrcPanel = new JPanel(cardLayout);
        attackSrcText = new JTextPane();
        attackSrcText.setText("<player>, Select attacking country from map");
        attackSrcText.setFocusable(false);
        attackSrcText.setOpaque(false);
        attackSrcPanel.add(attackSrcText);
        optionPanel.add(attackSrcPanel, Phase.ATTACK_SRC.toString());

        //creating attackDstPanel
        attackDstPanel = new JPanel();
        attackDstText = new JTextPane();
        attackDstText = new JTextPane();
        attackDstText.setText("<player>, select country to be attacked");
        attackDstText.setFocusable(false);
        attackDstText.setOpaque(false);
        attackDstPanel.add(attackDstText);
        optionPanel.add(attackDstPanel, Phase.ATTACK_DST.toString());

        //creating attackConfirmPanel
        attackConfirmPanel = new JPanel();
        attackConfirmText = new JTextPane();
        attackConfirmText.setText("Confirm attack on <dst> using <src>");
        attackConfirmText.setFocusable(false);
        attackConfirmText.setOpaque(false);
        attackConfirmPanel.add(attackConfirmText);
        optionPanel.add(attackConfirmPanel, Phase.ATTACK_ARMY.toString());

        //creating dicePanel
        dicePanel = new JPanel();
        dicePanelText = new JTextPane();
        dicePanelText.setText("<attacker> rolled <dice> and <defender> rolled <dice>");
        dicePanelText.setFocusable(false);
        dicePanelText.setOpaque(false);
        dicePanel.add(dicePanelText);
        optionPanel.add(dicePanel, Phase.RETREAT_ARMY.toString());

        /*for future use
         * optionPanel.add(troopMoverPanelSelectSource(),"troopMoverPanelSelectSource");
         * optionPanel.add(troopMoverPanelSelectDestination(),"troopMoverPanelSelectDestination");
         * optionPanel.add(deployPanelSelectDestination()."deployPanelSelectDestination")
         */

        cardLayout.show(attackSrcPanel, Phase.ATTACK_SRC.toString());   //for now game starts in attack phase
        return optionPanel;
    }
    /**
     * updates the RiskView with a given context
     * @param actionContext the current context for the update
     */
    public void boardUpdate(ActionContext actionContext) {
        switch (actionContext.phase) {
            case ATTACK_ARMY:
            case ATTACK_SRC:
            case RETREAT_ARMY:
            case ATTACK_DST:
                Phase currentPhase = actionContext.phase;
                confirmPhase.setActionCommand(currentPhase.toString());

                switch (currentPhase) {
                    case ATTACK_DST:
                        ((MapContainer) (mapContainer)).setActive(true);
                        highlightAdjacentCountries(actionContext.highlightedCountries, actionContext.srcCountry);
                        attackDstPanelEdit(actionContext.srcCountry); // to update the label in the panel
                        cardLayout.show(attackDstPanel, Phase.ATTACK_DST.toString());
                        confirmPhase.setText("Confirm Defender");
                        break;
                    case ATTACK_ARMY:
                        ((MapContainer) (mapContainer)).setActive(false);
                        attackConfirmPanelEdit(actionContext.dstCountry, actionContext.srcCountry);
                        cardLayout.show(attackConfirmPanel, Phase.ATTACK_ARMY.toString());
                        confirmPhase.setEnabled(false);
                        int troops = troopSelectPanel(actionContext.srcCountry);
                        confirmPhase.setEnabled(true);
                        if (troops > 0) {
                            confirmPhase.setText("Attack with " + troops + " troops");
                            confirmPhase.setActionCommand("" + troops);
                        } else {
                            confirmPhase.setText("Pick another country");
                            confirmPhase.setActionCommand("Back");
                        }
                        break;
                    case RETREAT_ARMY:
                        ((MapContainer) (mapContainer)).setActive(false);
                        dicePanelEdit(actionContext.diceRolls, actionContext.srcCountry.getOwner(), actionContext.dstCountry.getOwner(), actionContext.attackerVictory);
                        infoPanelEdit(actionContext);
                        cardLayout.show(dicePanel, Phase.RETREAT_ARMY.toString());
                        confirmPhase.setText("Ok");
                        break;
                    default: //ATTACK_SRC is default for now
                        ((MapContainer) (mapContainer)).setActive(true);
                        attackSrcPanelEdit(actionContext.player);
                        cardLayout.show(attackSrcPanel, Phase.ATTACK_SRC.toString());
                        confirmPhase.setText("Confirm Attacker");
                        break;
                }
                break;
        }
    }
    /**
     * edits the attackSrcPanel with information from a context
     * @param player player whose turn is to attack
     */
    private void attackSrcPanelEdit(Player player) {
        attackSrcText.setText(player.getName() + ", select attacking country from map");
    }
    /**
     * edits the text in attackSrcPanel based on the country attacking
     * @param Src country being used to attack
     */
    private void attackDstPanelEdit(Country Src) {
        attackDstText.setText("Select country to attack using " + Src);
    }
    /**
     * edits the attackConfirmPanel with corresponding context information
     * @param dstCountry the country being attacked
     * @param srcCountry the country attacking
     */
    private void attackConfirmPanelEdit(Country dstCountry, Country srcCountry) {
        attackConfirmText.setText("Confirm attack on " + dstCountry + " using " + srcCountry);
    }
    /**
     * adds information to the infoPanel based on the outcomes from context
     * @param context provided for update
     */
    private void infoPanelEdit(ActionContext context) {
        if(context.phase== Phase.RETREAT_ARMY) {
            String victoryString = (context.attackerVictory) ?
                    //won
                    "won. " + context.srcCountry.getName()+" has "+ (context.srcArmy-context.srcArmyDead) + " troops left":
                    //lost
                    "lost. "+ context.srcCountry.getName()+" has "+ (context.srcArmy-context.srcArmyDead) + " troops left and"+
                            context.dstCountry.getName()+" has "+ (context.dstArmy-context.dstArmyDead) + " troops left";
            //infoArea.append("===========================");
            infoArea.append(context.srcCountry.getOwner().getName() +  " used " +context.srcCountry.getName()+
                    " to attack "+ context.dstCountry.getName() + " and "+victoryString);
        }
    }
    /**
     * edits the dicePanel with information from an AttackContext
     * @param diceRolls int[][] style array containing rolls for attacker and defender
     * @param attacker Player who chose to attack
     * @param defender Player who was attacked
     */
    private void dicePanelEdit(int[][] diceRolls, Player attacker, Player defender,boolean victorious) {
        String victorySting = (victorious) ? attacker.getName() + " won!" : attacker.getName() + " lost!";
        dicePanelText.setText(attacker.getName() + " rolled\n" + Arrays.toString(diceRolls[0])
                + "\n and " + defender.getName() + "rolled\n" + Arrays.toString(diceRolls[1]) +"\n"+victorySting);
    }
    /**
     * highlights countries adjacent to the attacking country
     * @param countries array of adjacent countries
     * @param attacking attacking country
     */
    private void highlightAdjacentCountries(Country[] countries,Country attacking) {
        Country[] attacker = {attacking};
        labelCountries(attacker , true);
        labelCountries(countries,false);
    }
    /**
     * labels countries using their coordinates
     * @param countries countries to be labelled
     * @param clearPrevLabels dictates whether previous labels need to be removed
     */
    private void labelCountries(Country[] countries, boolean clearPrevLabels) {
        if(clearPrevLabels){
        mapLayeredPane.removeAll();
        insertMapImage();}

        for (Country c : countries) {
            JLabel countryLabel = new JLabel(c.getArmy() + "");
            countryLabel.setLocation(c.getVertices()[0].x, c.getVertices()[0].y);
            countryLabel.setOpaque(true);
            countryLabel.setBackground(c.getOwner().getColor());
            countryLabel.setText(c.getArmy() + "");
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBounds(c.getCenterCoordinates().x,c.getCenterCoordinates().y, 35, 15);
        }
    }
    /**
     * creates a popup window with a slider that lets the user decide the number of troops to send to battle
     * @param attacker attacking country
     * @return the number of troops selected
     */
    private int troopSelectPanel(Country attacker) {
        JFrame parent = new JFrame();
        JOptionPane optionPane = new JOptionPane();
        JSlider slider = new JSlider(1, attacker.getArmy() - 1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        ChangeListener changeListener = changeEvent -> {
            JSlider theSlider = (JSlider) changeEvent.getSource();
            if (!theSlider.getValueIsAdjusting()) {
                optionPane.setInputValue(theSlider.getValue());
            }
        };
        slider.addChangeListener(changeListener);
        optionPane.setMessage(new Object[]{"Select the number of troops: ", slider});
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(parent, "Select attacking troops");
        dialog.setVisible(true);
        if ((Integer) optionPane.getValue() == 0) {
            return (Integer) (optionPane.getInputValue());
        } else {
            return -1; //player cancelled selection
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.printf("Asas\n");
    }
}