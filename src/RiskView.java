import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;

/**
 * This is responsible for creating the viewport.
 * @author Kshitij Sawhney, Kevin Fox
 * @version 11 / 9 / 2020
 */

public class RiskView extends JFrame implements ActionListener {
    /** Controller for the view **/
    private final RiskController riskController;
    /** container for the MapLayeredPane **/
    private JPanel mapContainer; //JLayeredPane needs the parent Container to have a null Layout Manager
    /** JLayeredPane that contains the mapImage in the background and labels in the foreground **/
    private JLayeredPane mapLayeredPane;
    /** Layout for OptionPane that permits swapping views **/
    private CardLayout cardLayout;
    private JPanel newGamePanel;
    private JSlider newGameNumPlayers;
    /** Card Layout subpanel for ATTACK_SRC phase **/
    private JPanel attackSrcPanel;
    /** Card Layout subpanel for ATTACK_DST phase **/
    private JPanel attackDstPanel;
    private JPanel attackArmyPanel;
    private JSlider attackNumTroops;
    /** Card Layout subpanel for ATTACK_ARMY phase **/
    private JPanel attackConfirmPanel;
    /** Card Layout subpanel for ATTACK_DICE phase **/
    private JPanel dicePanel;
    /** Image background for the MapPanel **/
    private final BufferedImage mapImage;
    /** I bet you $5 they dont read this **/
    private JButton skipButton;
    /** Button to confirm completion of current phase **/
    private JButton confirmPhase;
    /** JTextPane for attackSrcPanel **/
    private JTextArea attackSrcText;
    /** JTextPane for attackDstPanel **/
    private JTextArea attackDstText;
    /** JTextPane for attackConfirmPanel **/
    private JTextArea attackConfirmText;
    /** JTextPane for attackConfirmPanel **/
    private JTextArea dicePanelText;
    /** Array of all Country objects in the map **/ //TODO use this for updating labels
    private final Country[] countryArray;
    /** JTextArea that gets updated as the game goes on **/
    private JTextArea infoArea;
    /** JPanel used for option buttons to be added **/
    private JPanel optionPanel;
    /** JButton for a player forfeit **/
    private JButton forfeitButton;
    /** JFrame for entering player names **/
    private JFrame selectFrame;
    /** ArrayList<JTextField> holds the names entered by the user **/
    private ArrayList<JTextField> names;

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
       //labelCountries(countryArray,true); // add labels for countries initially
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

        skipButton = new JButton("Skip");
        forfeitButton = new JButton("Forfeit");
        forfeitButton.setActionCommand("Forfeit");
        forfeitButton.setSize(50,40);
        forfeitButton.addActionListener(riskController);
        forfeitButton.setEnabled(false);

        skipButton.setSize(50, 40);
        skipButton.setActionCommand("skip");
        skipButton.setEnabled(false);

        confirmPhase.addActionListener(riskController);
        skipButton.addActionListener(riskController);
        confirmPhase.setVisible(false);
        confirmPhase.setEnabled(false);

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

        //attack army panel
        attackArmyPanel=new JPanel();
        attackNumTroops=new JSlider();
        attackArmyPanel.add(attackNumTroops);
        optionPanel.add(attackArmyPanel);

        //New Game Panel
        newGamePanel=new JPanel();
        newGameNumPlayers=new JSlider(2,6);
        newGamePanel.add(newGameNumPlayers);
        optionPanel.add(newGamePanel);

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

        //cardLayout.show(attackSrcPanel, Phase.ATTACK_SRC.toString());   //for now game starts in attack phase
        //cardLayout.show(newGamePanel,Phase.NEW_GAME.toString());
        return optionPanel;
    }
    /**
     * updates the RiskView with a given context
     * @param actionContext the current context for the update
     */
    public void boardUpdate(ActionContext actionContext) {
        forfeitButton.setText("Forfeit");
        forfeitButton.setActionCommand("Forfeit");
        switch (actionContext.getPhase()) {
            case NEW_GAME:
                if(actionContext.getSrcArmy() ==0) {
                    numberSelectPanel(actionContext, "Select number of players");
                }else {
                    selectFrame =new JFrame("Enter the names for the players");
                    JPanel selectPanel = new JPanel();
                    selectPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    names = new ArrayList<>(actionContext.getSrcArmy());
                    JButton submit = new JButton("Submit");
                    for(int i = 1; i<= actionContext.getSrcArmy(); i++){
                        gbc.gridy=i-1;
                        gbc.gridx=0;
                        selectPanel.add(new JLabel("Player "+i+": "),gbc);
                        JTextField name=new JTextField();
                        name.setPreferredSize(new Dimension(100,40));
                        gbc.gridx=1;
                        selectPanel.add(name,gbc);
                        names.add(name); 
                    }
                    
                    gbc.gridx=0;
                    gbc.gridy= actionContext.getSrcArmy() +2;
                    gbc.fill=GridBagConstraints.BOTH;
                    gbc.gridwidth=2;


                    submit.addActionListener(this);
                    submit.setActionCommand("names");
                    
                    selectPanel.add(submit,gbc);
                    selectPanel.setVisible(true);

                    selectFrame.setContentPane(selectPanel);
                    selectFrame.setSize(new Dimension(200,100* actionContext.getSrcArmy() +2));
                    selectFrame.setVisible(true);
                    selectFrame.setSize(500,350);
                }
                break;
            case ATTACK_SRC:
                if(selectFrame!=null) {
                    selectFrame.setVisible(false);
                }
                //Let the user interact with these buttons before here, causes bugs
                confirmPhase.setEnabled(true);
                skipButton.setEnabled(true);
                skipButton.setVisible(true);
                forfeitButton.setEnabled(true);


                labelCountries(countryArray,true);
                ((MapContainer) (mapContainer)).setActive(true);
                try {
                    attackSrcPanelEdit(actionContext.getPlayer());
                }catch(Exception e){
                    e.printStackTrace();
                }
                cardLayout.show(optionPanel, Phase.ATTACK_SRC.toString());
                confirmPhase.setText("Confirm Attacker");
                confirmPhase.setVisible(false);
                break;
            case ATTACK_DST:
                confirmPhase.setVisible(false);
                ((MapContainer) (mapContainer)).setActive(true);
                if(isNull(actionContext.getHighlightedCountries())){
                    JOptionPane.showMessageDialog(null,"No troops to use");
                    confirmPhase.setText("Select new country");
                    confirmPhase.setActionCommand("back");
                    confirmPhase.setVisible(true);
                }
                else{
                    highlightAdjacentCountries(actionContext.getHighlightedCountries(), actionContext.getSrcCountry());
                    attackDstPanelEdit(actionContext.getSrcCountry()); // to update the label in the panel
                    cardLayout.show(optionPanel, Phase.ATTACK_DST.toString());
                    confirmPhase.setText("back");
                    confirmPhase.setActionCommand("back");
                    confirmPhase.setVisible(true);
                }
                break;
            case ATTACK_ARMY:
                ((MapContainer) (mapContainer)).setActive(false);

                attackConfirmPanelEdit(actionContext.getDstCountry(), actionContext.getSrcCountry());
                cardLayout.show(optionPanel, Phase.ATTACK_ARMY.toString());
                confirmPhase.setEnabled(false);

                int attackingTroops = numberSelectPanel(actionContext,"Select number of troops: ");
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
                ((MapContainer) (mapContainer)).setActive(false);
                labelCountries(countryArray,true);
                dicePanelEdit(actionContext.getDiceRolls(), actionContext.getSrcCountry().getOwner(), actionContext.getDstCountry().getOwner(), actionContext.isAttackerVictory());
                infoPanelEdit(actionContext);
                if(actionContext.isAttackerVictory()) {
                    int retreatingTroops = -1;
                    while (retreatingTroops == -1) {
                        retreatingTroops = numberSelectPanel(actionContext, "Select number of troops to retreat: ");
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
            case GAME_OVER:
                infoPanelEdit(actionContext);

                if (JOptionPane.showConfirmDialog(null, actionContext.getPlayer().getName() + ", you Won!\nPlay again?", "Congratulations",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    forfeitButton.setText("Play Again");
                    forfeitButton.setActionCommand("newGameUser");
                } else {
                    dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
                }
                break;
            default:
                System.out.println(actionContext.getPhase());
                break;
        }
    }

    /**
     * This method is used to process the ng String for giving the player names
     * to the model
     * @param names the arraylist of player names
     */
    public void compileNames(List<JTextField> names){
        String[] namesArray=new String[names.size()];
        for(int i=0;i<names.size();i++){
            if(names.get(i).getText().length()==0){
                namesArray[i]="Player "+(i+1);
            }
            else{
                namesArray[i]=names.get(i).getText();
            }
        }
        riskController.newGame(namesArray);
    }

    /**
     * edits the attackSrcPanel with information from a context
     * @param player player whose turn is to attack
     */
    private void attackSrcPanelEdit(Player player) throws BadLocationException {
        attackSrcText.getHighlighter().removeAllHighlights();
        attackSrcText.setText(player.getName() + ", select attacking country from map");
        attackSrcText.getHighlighter().addHighlight(0,player.getName().length(), new DefaultHighlighter.DefaultHighlightPainter(player.getColor()));
    }
    /**
     * edits the text in attackSrcPanel based on the country attacking
     * @param Src country being used to attack
     */
    private void attackDstPanelEdit(Country Src) {
        attackDstText.setText("Select country to attack using " + Src.getName());
    }
    private void attackNumTroopsPanelEdit(int max){

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
        if(context.getPhase() == Phase.RETREAT_ARMY) {
            String suffix = (context.isAttackerVictory())?" won":" lost";
            String vicString =
                    context.getSrcCountry().getName() +" attacked " + context.getDstCountry().getName();
            String battleOutcomes = context.getSrcCountry().getName() +" has lost " + (context.getSrcArmyDead())+" troops.";
            String battleOutcomes2 = context.getDstCountry().getName() +" has lost " + (context.getDstArmyDead())+" troops.";

            infoArea.append("Player: "+ context.getSrcCountry().getOwner().getName() +suffix+"\n");
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
    private int numberSelectPanel(ActionContext actionContext, String message) {
        AtomicBoolean sliderUsed = new AtomicBoolean(false);
        JFrame troopSelectPanel = new JFrame();
        JOptionPane numberPane = new JOptionPane();
        JSlider slider = new JSlider();
        ChangeListener changeListener = changeEvent -> {
            JSlider theSlider = (JSlider) changeEvent.getSource();
            if (!theSlider.getValueIsAdjusting()) {
                sliderUsed.set(true);
                numberPane.setInputValue(theSlider.getValue());
            }
        };
        slider.addChangeListener(changeListener);
        numberPane.setMessage(new Object[]{message, slider});
        numberPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        numberPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JFrame f = new JFrame();
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        switch (actionContext.getPhase()) {
            case ATTACK_ARMY:
                if (actionContext.getSrcCountry().getArmy() >= 3) {
                    slider.setMaximum(actionContext.getSrcCountry().getArmy() - 1);
                    slider.setMinimum(1);
                    JDialog dialog = numberPane.createDialog(troopSelectPanel, "Select attacking troops");
                    dialog.setVisible(true);

                    if (!isNull(numberPane.getValue()) && (Integer) numberPane.getValue() == JOptionPane.OK_OPTION) {
                        return (sliderUsed.get()) ? Integer.parseInt((numberPane.getInputValue().toString())) : 0;
                    } else { //player cancelled selection
                        return -1;
                    }
                } else if (actionContext.getSrcCountry().getArmy() == 2) { // 1 troop to attack
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
                if (actionContext.getSrcArmy() - actionContext.getSrcArmyDead() >= 2) {
                    slider.setMaximum(actionContext.getSrcArmy() - actionContext.getSrcArmyDead() - 1);
                    slider.setMinimum(0);
                    JDialog dialog = numberPane.createDialog(troopSelectPanel, "You won! Select retreating troops");
                    dialog.setVisible(true);
                    if (!isNull(numberPane.getValue()) && (Integer) numberPane.getValue() == JOptionPane.OK_OPTION) {
                        return (sliderUsed.get()) ? Integer.parseInt((numberPane.getInputValue().toString())) : 0;
                    } else { //player cancelled selection
                        return -1;
                    }
                } else if (actionContext.getSrcArmy() - actionContext.getSrcArmyDead() ==1) { // 1 troop to attack
                    int x = JOptionPane.showConfirmDialog(f, "No troops to send back",
                            "Troop Retreat",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                    return 0;

                } else {
                    JOptionPane.showMessageDialog(f, "No troops to use");
                    return -1;
                }
            case NEW_GAME:
                slider.setMinimum(2);
                slider.setMaximum(6);
                JDialog dialog = numberPane.createDialog(troopSelectPanel, "Use slider to select number of Players");
                dialog.setSize(400,200);
                dialog.setVisible(true);

                if (!isNull(numberPane.getValue()) && ((Integer) numberPane.getValue() == JOptionPane.OK_OPTION)){
                    riskController.actionPerformed(new ActionEvent(this,1,""+((sliderUsed.get()) ? Integer.parseInt((numberPane.getInputValue().toString())) :2 )));
                    return (sliderUsed.get()) ? Integer.parseInt((numberPane.getInputValue().toString())) :2 ;
                } else { //player cancelled selection
                    dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
                }
            default:
                return -1;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getActionCommand().equals("names")){
           compileNames(names);
       }
    }
}