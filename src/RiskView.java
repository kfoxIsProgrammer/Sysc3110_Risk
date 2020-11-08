import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;

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
    private JTextArea attackSrcText;
    /**JTextPane for attackDstPanel*/
    private JTextArea attackDstText;
    /**JTextPane for attackConfirmPanel*/
    private JTextArea attackConfirmText;
    /**JTextPane for attackConfirmPanel*/
    private JTextArea dicePanelText;
    /**Array of all Country objects in the map*/ //TODO use this for updating labels
    private final Country[] countryArray;
    /**JTextArea that gets updated as the game goes on**/
    private JTextArea infoArea;

    private JPanel optionPanel;
    private JButton forfeitButton;

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

        setResizable(false);
        //Constraints for the placement of MapPanel,SidePanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mapPanel(), gbc);
        pack();

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
        confirmPhase.setSize(200, 40);

        JButton skipButton = new JButton("Skip");
        forfeitButton = new JButton("Forfeit");
        forfeitButton.setActionCommand("Forfeit");
        forfeitButton.setSize(50,40);

        skipButton.setSize(50, 40);
        skipButton.setActionCommand("skip");

        confirmPhase.addActionListener(riskController);
        skipButton.addActionListener(riskController);

        buttonPanel.add(confirmPhase, BorderLayout.WEST);
        buttonPanel.add(skipButton, BorderLayout.EAST);
        buttonPanel.add(forfeitButton,BorderLayout.CENTER);

        sidePanel.add(buttonPanel,BorderLayout.SOUTH);

        sidePanel.setSize(new Dimension(300, height));

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
        JP.setPreferredSize(new Dimension(300, height));
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
        optionPanel = new JPanel();
        cardLayout = new CardLayout();

        optionPanel.setLayout(cardLayout);
        optionPanel.setSize(300, height - 40);

        //creating attackSrcPanel
        attackSrcPanel = new JPanel(cardLayout);
        attackSrcText = new JTextArea();
        attackSrcText.setText("<player>, Select attacking country from map");
        attackSrcText.setFocusable(false);
        attackSrcText.setLineWrap(true);
        attackSrcText.setWrapStyleWord(true);
        attackSrcText.setOpaque(false);
        attackSrcText.setSize(optionPanel.getSize());
        attackSrcPanel.add(attackSrcText);
        optionPanel.add(attackSrcPanel, Phase.ATTACK_SRC.toString());

        //creating attackDstPanel
        attackDstPanel = new JPanel();
        attackDstText = new JTextArea();
        attackDstText.setText("<player>, select country to be attacked");
        attackDstText.setFocusable(false);
        attackDstText.setLineWrap(true);
        attackDstText.setWrapStyleWord(true);
        attackDstText.setOpaque(false);
        attackDstText.setSize(optionPanel.getSize());
        attackDstPanel.add(attackDstText);
        optionPanel.add(attackDstPanel, Phase.ATTACK_DST.toString());

        //creating attackConfirmPanel
        attackConfirmPanel = new JPanel();
        attackConfirmText = new JTextArea();
        attackConfirmText.setLineWrap(true);
        attackConfirmText.setWrapStyleWord(true);
        attackConfirmText.setText("Confirm attack on <dst> using <src>");
        attackConfirmText.setFocusable(false);
        attackConfirmText.setOpaque(false);
        attackConfirmText.setSize(optionPanel.getSize());
        attackConfirmPanel.add(attackConfirmText);
        optionPanel.add(attackConfirmPanel, Phase.ATTACK_ARMY.toString());

        //creating dicePanel
        dicePanel = new JPanel();
        dicePanelText = new JTextArea();
        dicePanelText.setText("<attacker> rolled <dice> and <defender> rolled <dice>");
        dicePanelText.setFocusable(false);
        dicePanelText.setLineWrap(true);
        dicePanelText.setWrapStyleWord(true);
        dicePanelText.setOpaque(false);
        attackConfirmText.setSize(optionPanel.getSize());
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
        forfeitButton.setText("Forfeit");
        switch (actionContext.phase) {
            case ATTACK_SRC:
                labelCountries(countryArray,true);
                ((MapContainer) (mapContainer)).setActive(true);
                attackSrcPanelEdit(actionContext.player);
                cardLayout.show(optionPanel, Phase.ATTACK_SRC.toString());
                confirmPhase.setText("Confirm Attacker");
                confirmPhase.setVisible(false);
                break;
            case ATTACK_DST:
                ((MapContainer) (mapContainer)).setActive(true);
                if(isNull(actionContext.highlightedCountries)){
                    displayPopup("Cannot attack with 1 troop, select a different country");
                    confirmPhase.setText("Select another country");
                    confirmPhase.setActionCommand("back");
                    confirmPhase.setVisible(true);
                }else{
                        highlightAdjacentCountries(actionContext.highlightedCountries, actionContext.srcCountry);
                        attackDstPanelEdit(actionContext.srcCountry); // to update the label in the panel
                        cardLayout.show(optionPanel, Phase.ATTACK_DST.toString());
                        confirmPhase.setText("back");
                        confirmPhase.setActionCommand("back");
                        confirmPhase.setVisible(true);
                        break;}
                    case ATTACK_ARMY:
                        ((MapContainer) (mapContainer)).setActive(false);
                        attackConfirmPanelEdit(actionContext.dstCountry, actionContext.srcCountry);
                        cardLayout.show(optionPanel, Phase.ATTACK_ARMY.toString());
                        confirmPhase.setEnabled(false);
                        int attackingTroops = 0;
                        while (attackingTroops==0){
                            attackingTroops = troopSelectPanel(actionContext,"Select number of troops: ");
                        }
                        if (attackingTroops>0){
                            confirmPhase.setText("Attack with "+attackingTroops+" troops");
                            confirmPhase.setActionCommand(""+attackingTroops);
                        }else{
                            confirmPhase.setText("select countries again");
                            confirmPhase.setActionCommand("back");
                        }
                        confirmPhase.setEnabled(true);
                        confirmPhase.setVisible(true);
                        break;
                    case RETREAT_ARMY:
                        System.out.println("here");
                        ((MapContainer) (mapContainer)).setActive(false);
                        labelCountries(countryArray,true);
                        dicePanelEdit(actionContext.diceRolls, actionContext.srcCountry.getOwner(), actionContext.dstCountry.getOwner(), actionContext.attackerVictory);
                        infoPanelEdit(actionContext);
                        if(actionContext.attackerVictory) {
                            int retreatingTroops = -1;
                            while (retreatingTroops == -1) {
                                retreatingTroops = troopSelectPanel(actionContext, "Select number of troops to retreat: ");
                                if (retreatingTroops >= 0) {
                                    confirmPhase.setText("Send " + retreatingTroops + " troops back");
                                    confirmPhase.setActionCommand("" + retreatingTroops);
                                } else {
                                    confirmPhase.setText("Ok");
                                   confirmPhase.setActionCommand("skip");
                                }
                            }
                        }else{
                            confirmPhase.setText("Ok");
                            confirmPhase.setActionCommand("skip");
                        }
                        cardLayout.show(optionPanel, Phase.RETREAT_ARMY.toString());
                        confirmPhase.setVisible(true);
                        break;
            case FORFEIT_CLICKED:
                confirmPhase.setVisible(false);
                if (JOptionPane.showConfirmDialog(null, actionContext.player.getName() + "You are about to forfeit your battle! Confirm", "WARNING",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    forfeitButton.setText("Confirm forfeit?");
                    forfeitButton.setActionCommand("Forfeit");
                } else {
                    confirmPhase.setText("Cancel forfeit");
                    confirmPhase.setActionCommand("back");
                }
                    default:
                        System.out.println(actionContext.phase);
                break;
        }
    }

    private void displayPopup(String s) {
        JFrame f=new JFrame();
        JOptionPane.showMessageDialog(f,"No troops to use");
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
        attackDstText.setText("Select country to attack using " + Src.getName());
    }
    /**
     * edits the attackConfirmPanel with corresponding context information
     * @param dstCountry the country being attacked
     * @param srcCountry the country attacking
     */
    private void attackConfirmPanelEdit(Country dstCountry, Country srcCountry) {
        if(!isNull(dstCountry)) {
            attackConfirmText.setText("Confirm attack on " + dstCountry.getName() + " using " + srcCountry.getName());
        }
    }
    /**
     * adds information to the infoPanel based on the outcomes from context
     * @param context provided for update
     */
    private void infoPanelEdit(ActionContext context) {
        if(context.phase== Phase.RETREAT_ARMY) {
            String suffix = (context.attackerVictory)?" won":" lost";
            String vicString =
                    context.srcCountry.getName() +" attacked " + context.dstCountry.getName();
            String battleOutcomes = context.srcCountry.getName() +" has lost " + (context.srcArmyDead)+" troops.";
            String battleOutcomes2 = context.dstCountry.getName() +" has lost " + (context.dstArmyDead)+" troops.";

            infoArea.append("Player: "+ context.srcCountry.getOwner().getName() +suffix+"\n");
            infoArea.append(vicString+"\n");
            infoArea.append(battleOutcomes+"\n");
            infoArea.append(battleOutcomes2+"\n");
        }
    }
    /**
     * edits the dicePanel with information from an AttackContext
     * @param diceRolls int[][] style array containing rolls for attacker and defender
     * @param attacker Player who chose to attack
     * @param defender Player who was attacked
     */
    private void dicePanelEdit(Integer[][] diceRolls, Player attacker, Player defender,boolean victorious) {
        String victorySting = (victorious) ? attacker.getName() + " won!" : attacker.getName() + " lost!";
        dicePanelText.setText(attacker.getName() + " rolled\n" + Arrays.toString(diceRolls[0])
                + "\n and " + defender.getName() + " rolled\n" + Arrays.toString(diceRolls[1]) +"\n"+victorySting);
    }
    /**
     * highlights countries adjacent to the attacking country
     * @param countries array of adjacent countries
     * @param attacking attacking country
     */
    private void highlightAdjacentCountries(Country[] countries,Country attacking) {
        Country[] attacker = {attacking};
        labelCountries(attacker , true);
        if(!(countries.length == 0)) {
            labelCountries(countries, false);
        }
    }
    /**
     * labels countries using their coordinates
     * @param countries countries to be labelled
     * @param clearPrevLabels dictates whether previous labels need to be removed
     */
    private void labelCountries(Country[] countries, boolean clearPrevLabels) {
        if(clearPrevLabels){
        mapLayeredPane.removeAll();
        insertMapImage();
        mapContainer.add(mapLayeredPane);
        repaint();

        }

        for (Country c : countries) {
            JLabel countryLabel = new JLabel(c.getArmy() + "");
            countryLabel.setLocation(c.getCenterCoordinates().x, c.getCenterCoordinates().y);
            countryLabel.setOpaque(true);
            countryLabel.setBackground(c.getOwner().getColor());
            countryLabel.setText(c.getArmy() + "");
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBounds(c.getCenterCoordinates().x,c.getCenterCoordinates().y, 35, 15);
            mapLayeredPane.add(countryLabel,Integer.valueOf(2));
        }
        repaint();
    }
    /**
     * creates a popup window with a slider that lets the user decide the number of troops to send to battle
     * @param actionContext context information
     * @return the number of troops selected
     */
    private int troopSelectPanel(ActionContext actionContext,String message) {
        AtomicBoolean sliderUsed = new AtomicBoolean(false);
        JFrame troopSelectPanel = new JFrame();
        JOptionPane troopPane = new JOptionPane();
        JSlider slider = new JSlider();
        ChangeListener changeListener = changeEvent -> {
            JSlider theSlider = (JSlider) changeEvent.getSource();
            if (!theSlider.getValueIsAdjusting()) {
                sliderUsed.set(true);
                troopPane.setInputValue(theSlider.getValue());
            }
        };
        slider.addChangeListener(changeListener);
        troopPane.setMessage(new Object[]{message, slider});
        troopPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        troopPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JFrame f = new JFrame();
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        switch (actionContext.phase) {
            case ATTACK_ARMY:
                if (actionContext.srcCountry.getArmy() >= 3) {
                    slider.setMaximum(actionContext.srcCountry.getArmy() - 1);
                    slider.setMinimum(1);
                    JDialog dialog = troopPane.createDialog(troopSelectPanel, "Select attacking troops");
                    dialog.setVisible(true);

                    if (!isNull(troopPane.getValue()) && (Integer) troopPane.getValue() == JOptionPane.OK_OPTION) {
                        return (sliderUsed.get()) ? Integer.parseInt((troopPane.getInputValue().toString())) : 0;
                    } else { //player cancelled selection
                        return -1;
                    }
                } else if (actionContext.srcCountry.getArmy() == 2) { // 1 troop to attack
                    int x = JOptionPane.showConfirmDialog(f, "Attack with 1 troop?");
                    if (x == JOptionPane.YES_OPTION) {
                        return 1;
                    } else { //no troops to attack
                        return -1;
                    }
                } else {
                    JOptionPane.showMessageDialog(f, "No troops to use");
                    return -1;
                }
            case RETREAT_ARMY:
                if (actionContext.srcArmy-actionContext.srcArmyDead >= 2) {
                    slider.setMaximum(actionContext.srcArmy-actionContext.srcArmyDead - 1);
                    slider.setMinimum(0);
                    JDialog dialog = troopPane.createDialog(troopSelectPanel, "You won! Select retreating troops");
                    dialog.setVisible(true);
                    if (!isNull(troopPane.getValue()) && (Integer) troopPane.getValue() == JOptionPane.OK_OPTION) {
                        return (sliderUsed.get()) ? Integer.parseInt((troopPane.getInputValue().toString())) : 0;
                    } else { //player cancelled selection
                        return -1;
                    }
                } else if (actionContext.srcArmy-actionContext.srcArmyDead==1) { // 1 troop to attack
                    int x = JOptionPane.showConfirmDialog(f, "No troops to send back");
                    return 0;

                } else {
                    JOptionPane.showMessageDialog(f, "No troops to use");
                    return -1;
                }
            default:
                return -1;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.printf("BRUH\n");
    }
}