import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * This is responsible for creating the viewport in which one plays the game.
 * @author Kshitij Sawhney
 * @version 11 / 2 / 2020
 */

public class RiskView extends JFrame implements ActionListener {

    private final RiskController riskController;

    private GridBagConstraints gbc = new GridBagConstraints();

    private JPanel mapPanel; //JLayeredPane needs the parent Container to have a null Layout Manager
    private JLayeredPane mapLayeredPane;
    private CardLayout cardLayout;
    private JPanel attackSrcPanel;
    private JPanel attackDstPanel;

    private final BufferedImage mapImage;

    private JButton confirmPhase;
    private JPanel attackConfirmPanel;
    private JTextPane attackDstText;
    private JTextPane attackConfirmText;
    private JTextPane attackSrcText;
    private JPanel dicePanel;
    private JTextPane dicePanelText;
    private final Country[] countryArray;

    public RiskView(RiskController controller, BufferedImage modelMapImage, Country[] countries){
        //TODO info update, country[] for indexed label updates
        mapImage = modelMapImage;
        riskController = controller;
        countryArray = countries;

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
        // cardLayout with other panels that change based on the current phase
        JPanel optionPanel = new JPanel();
        cardLayout = new CardLayout();

        optionPanel.setLayout(cardLayout);
        optionPanel.setSize(200, height - 40);

        attackSrcPanel = new JPanel();
        attackSrcText = new JTextPane();
        attackSrcText.setText("<player>, Select attacking country from map");
        attackSrcText.setFocusable(false);
        attackSrcText.setOpaque(false);
        attackSrcPanel.add(attackSrcText);
        optionPanel.add(attackSrcPanel, Phase.ATTACK_SRC.toString());

        attackDstPanel = new JPanel();
        attackDstText = new JTextPane();
        attackDstText = new JTextPane();
        attackDstText.setText("<player>, select country to be attacked");
        attackDstText.setFocusable(false);
        attackDstText.setOpaque(false);
        attackDstPanel.add(attackDstText);
        optionPanel.add(attackDstPanel, Phase.ATTACK_DST.toString());

        attackConfirmPanel = new JPanel();
        attackConfirmText = new JTextPane();
        attackConfirmText.setText("Confirm attack on <dst> using <src>");
        attackConfirmText.setFocusable(false);
        attackConfirmText.setOpaque(false);
        attackConfirmPanel.add(attackConfirmText);
        optionPanel.add(attackConfirmPanel, Phase.ATTACK_CONF.toString());

        dicePanel = new JPanel();
        dicePanelText = new JTextPane();
        dicePanelText.setText("<attacker> rolled <dice> and <defender> rolled <dice>");
        dicePanelText.setFocusable(false);
        dicePanelText.setOpaque(false);
        dicePanel.add(dicePanelText);
        optionPanel.add(dicePanel, Phase.ATTACK_DICE.toString());

        /*for future use
         * optionPanel.add(troopMoverPanelSelectSource(),"troopMoverPanelSelectSource");
         * optionPanel.add(troopMoverPanelSelectDestination(),"troopMoverPanelSelectDestination");
         * optionPanel.add(deployPanelSelectDestination()."deployPanelSelectDestination")
         */

        cardLayout.show(attackSrcPanel, Phase.ATTACK_SRC.toString());   //for now game starts in attack phase
        return optionPanel;
    }

    private void attackSelectDefenderPanel(Country Src) {
        attackDstText.setText("Select country to attack using " + Src);
    }

    private JPanel mapPanel(RiskController riskController) {
        mapPanel = new MapPanel(riskController);
        mapPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

        labelCountries(countryArray,true);

        mapPanel.setLayout(null);
        mapLayeredPane.setLayout(null);
        mapLayeredPane.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());

        mapPanel.add(mapLayeredPane);

        return mapPanel;
    }

    private void insertMapImage() {
        mapPanel.setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
        mapLayeredPane = new JLayeredPane();
        JLabel mapImageLabel = new JLabel(new ImageIcon(mapImage));
        mapImageLabel.setBounds(0, 0, mapImage.getWidth(), mapImage.getHeight());
        //inserting into Layered pane
        mapLayeredPane.add(mapImageLabel, Integer.valueOf(1));
    }

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

    private JPanel infoPanel(int height) {
        JPanel infoPanel = new JPanel();
        JTextPane infoArea = new JTextPane();
        infoArea.setFocusable(false);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200, height));
        infoPanel.add(JP);
        return infoPanel;
    }

    public void boardUpdate(ActionContext context) {
        if (context.phase == Phase.ATTACK_ARMY || context.phase == Phase.ATTACK_CONF || context.phase == Phase.ATTACK_DICE || context.phase == Phase.ATTACK_DST) {
            AttackContext attackContext = (AttackContext) (context);
            Phase currentPhase = attackContext.phase;
            confirmPhase.setActionCommand(currentPhase.toString());

            switch (currentPhase) {
                case ATTACK_DST -> {
                    ((MapPanel) (mapPanel)).setActive(true);
                    highlightAdjacentCountries(attackContext.highlightedCountries,attackContext.srcCountry);
                    attackSelectDefenderPanel(attackContext.srcCountry); // to update the label in the panel
                    cardLayout.show(attackDstPanel, Phase.ATTACK_DST.toString());
                    confirmPhase.setText("Confirm Defender");
                }
                case ATTACK_CONF -> {
                    ((MapPanel) (mapPanel)).setActive(false);
                    attackConfirmPanel(attackContext.dstCountry, attackContext.srcCountry);
                    cardLayout.show(attackConfirmPanel, Phase.ATTACK_CONF.toString());
                    confirmPhase.setEnabled(false);
                    int troops = troopSelectPanel(attackContext.srcCountry);
                    confirmPhase.setEnabled(true);
                    if (troops > 0) {
                        confirmPhase.setText("Attack with " + troops + " troops");
                        confirmPhase.setActionCommand("" + troops);
                    } else {
                        confirmPhase.setText("Pick another country");
                        confirmPhase.setActionCommand("Back");
                    }
                }
                case ATTACK_DICE -> {
                    ((MapPanel) (mapPanel)).setActive(false);
                    dicePanel(attackContext.diceRolls, attackContext.srcCountry.getOwner(), attackContext.dstCountry.getOwner());
                    cardLayout.show(dicePanel, Phase.ATTACK_DICE.toString());
                    confirmPhase.setText("Ok");
                }
                default -> {
                    ((MapPanel) (mapPanel)).setActive(true);
                    attackSelectAttackerPanel(context.player);
                    cardLayout.show(attackSrcPanel, Phase.ATTACK_SRC.toString());
                    confirmPhase.setText("Confirm Attacker");
                }
            }
        }
    }

    private void dicePanel(int[][] diceRolls, Player attacker, Player defender) {
        dicePanelText.setText(""); //make sure its empty
        dicePanelText.setText(attacker.getName() + " rolled\n" + Arrays.toString(diceRolls[0]) + "\n and " + defender.getName() + "rolled\n" + Arrays.toString(diceRolls[1]));
    }

    private void attackSelectAttackerPanel(Player player) {
        attackSrcText.setText(player + ", select attacking country from map");
    }

    private void attackConfirmPanel(Country dstCountry, Country srcCountry) {
        attackConfirmText.setText("Confirm attack on " + dstCountry + " using " + srcCountry);
    }

    private void highlightAdjacentCountries(Country[] countries,Country attacking) {
        Country[] attacker = {attacking};
        labelCountries(attacker , true);
        labelCountries(countries,false);
    }

    private void labelCountries(Country[] countries, boolean clearPrevLabels) {
        mapLayeredPane.removeAll();
        insertMapImage();
        for (Country c : countries) {
            JLabel countryLabel = new JLabel(c.getArmy() + "");
            countryLabel.setLocation(c.getVertices().get(0).x, c.getVertices().get(0).y);
            countryLabel.setOpaque(true);
            countryLabel.setBackground(c.getOwner().getColor());
            countryLabel.setText(c.getArmy() + "");
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBounds(c.getVertices().get(0).x, c.getVertices().get(0).y, 35, 15);
        }
    }

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
    }
}