import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class RiskGUI extends JFrame implements RiskView{
    private final RiskController controller;
    private Map map;

    private final JMenu loadGame;
    private final JMenu loadMap;

    private final JLayeredPane mapPane;
    private JLabel mapImage;

    private final JTextArea eventLogText;

    private final JLabel menuPlayerName;
    private final JLabel menuPhase;
    private final JTextArea menuPrompt;
    private final JTextField menuText;
    private final JSlider menuSlider;
    private final JButton menuConfirm;
    private final JButton menuBack;
    private final JButton menuSkip;
    private final JButton menuOk;

    RiskGUI(RiskModel model, Map map){
        setTitle("Risk - Global Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        this.controller = new RiskController(this,model, map);
        this.map=map;
        int mapHeight=map.getMapImage().getHeight();
        int mapWidth=map.getMapImage().getWidth();

        this.setLayout(new GridBagLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(controller);
        loadGame=new JMenu("Load Game");
        loadMap=new JMenu("Load Map");
        menuBar.add(saveGame);
        menuBar.add(loadGame);
        menuBar.add(loadMap);
        this.add(menuBar,constraintMaker(0,0,1,1));

        //Initialize image
        mapImage=new JLabel(new ImageIcon(map.getMapImage()));
        mapImage.setBounds(0,0, mapWidth, mapHeight);
        mapImage.addMouseListener(controller);
        mapPane=new JLayeredPane();
        mapPane.setPreferredSize(new Dimension(mapWidth, mapHeight));
        mapPane.add(this.mapImage);
        this.add(mapPane,constraintMaker(0,1,1,2));

        //Initialize event log
        eventLogText=new JTextArea("Game Started\n\n");
        eventLogText.setEditable(false);
        eventLogText.setLineWrap(true);

        JScrollPane eventLogPane = new JScrollPane(eventLogText);
        eventLogPane.setPreferredSize(new Dimension(mapWidth /3, mapHeight /2));
        eventLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        eventLogPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        this.add(eventLogPane,constraintMaker(1,1,1,1));

        //Initialize menu
        menuPlayerName=new JLabel("Player: ");
        menuPlayerName.setVisible(false);
        menuPhase=new JLabel("Phase: ");

        menuPrompt=new JTextArea();
        menuPrompt.setEditable(false);
        menuPrompt.setLineWrap(true);
        menuPrompt.setWrapStyleWord(true);
        menuPrompt.setPreferredSize(new Dimension(mapWidth /3, mapHeight *3/12));

        menuText=new JTextField();
        menuText.setActionCommand("Text Confirm");
        menuText.addActionListener(controller);

        menuSlider=new JSlider();
        menuSlider.setPaintLabels(true);
        menuSlider.setPaintTicks(true);
        menuSlider.addChangeListener(controller);

        menuConfirm=new JButton("Confirm");
        menuConfirm.addActionListener(controller);

        menuBack=new JButton("Back");
        menuBack.addActionListener(controller);

        menuSkip=new JButton("Skip");
        menuSkip.addActionListener(controller);

        menuOk=new JButton("Ok");
        menuOk.addActionListener(controller);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        menuPanel.add(menuPlayerName,   constraintMaker(0,0,1,1));
        menuPanel.add(menuPhase,        constraintMaker(1,0,1,1));
        menuPanel.add(menuPrompt,       constraintMaker(0,1,4,2));
        menuPanel.add(menuText,         constraintMaker(0,3,4,1));
        menuPanel.add(menuSlider,       constraintMaker(0,4,4,1));
        menuPanel.add(menuConfirm,      constraintMaker(0,5,1,1));
        menuPanel.add(menuBack,         constraintMaker(1,5,1,1));
        menuPanel.add(menuSkip,         constraintMaker(2,5,1,1));
        menuPanel.add(menuOk,           constraintMaker(3,5,1,1));

        this.add(menuPanel,constraintMaker(1,2,1,1));

        this.pack();
        this.setVisible(true);
    }

    @Override
    public void updateMap(Map map){
        this.map=map;
        controller.setMap(map);
        int mapHeight=map.getMapImage().getHeight();
        int mapWidth=map.getMapImage().getWidth();

        mapImage.removeAll();
        mapImage=new JLabel(new ImageIcon(map.getMapImage()));
        mapImage.setBounds(0,0, mapWidth, mapHeight);
        mapImage.addMouseListener(controller);
        mapPane.removeAll();
        mapPane.add(mapImage);

        eventLogText.setText("");

        menuPlayerName.setVisible(false);
        menuPhase.setVisible(false);

    }
    @Override
    public void update(ActionContext ac){
        controller.setPhase(ac.getPhase());
        controller.setPlayer(ac.getPlayer());
        updatePhase(ac);
        updatePlayerName(ac.getPlayer());

        if(ac.getPhase().equals(Phase.GAME_OVER)){
            updateMap();
            updatePrompt(ac.getPlayer(), " has won!");
            updateMenuVisible(false,false,false,false,false,false);
        }
        else {

            if (ac.getPlayer() != null && ac.getPlayer().isAI) {
                updateAI(ac);
            } else {
                updateHuman(ac);
            }
        }

    }
    private void updateHuman(ActionContext ac){
        switch(ac.getPhase()){
            case NUM_HUMANS:
                updatePrompt("Enter the number of players");
                updateSlider(0,6);
                updateMenuVisible(false, true,true,false,false, false);
                break;
            case NUM_AI:
                updatePrompt("Enter the number of AI");
                System.out.printf("\t%d\n",ac.getPlayerIndex());
                if(ac.getPlayerIndex()==0) {
                    updateSlider(2, 6);
                }else if(ac.getPlayerIndex()==1){
                    updateSlider(1,5);
                }else{
                    updateSlider(0,6-ac.getPlayerIndex());
                }
                updateMenuVisible(false, true,true,false,false, false);
                break;
            case PLAYER_NAME:
                updatePrompt("Player "+(ac.getPlayerIndex()+1)+", enter you name");
                updateMenuVisible(true, false,false,false,false, false);
                break;
            case CLAIM_COUNTRY:
                if(ac.getHighlightedCountries()!=null){
                    updateMap(ac.getHighlightedCountries());
                }
                updatePrompt(ac.getPlayer(),"claim a country");
                updateMenuVisible(false,false,false,false,false, false);
                break;
            case INITIAL_DEPLOY_DST:
            case DEPLOY_DST:
                updateMap();
                updatePrompt(ac.getPlayer(), "choose a country to deploy troops to");
                updateMenuVisible(false, false, false, false, false, false);
                break;
            case INITIAL_DEPLOY_NUM_TROOPS:
            case DEPLOY_NUM_TROOPS:
                updatePrompt(ac.getPlayer(), "how many troops will you send to " + ac.getDstCountry().getName());
                updateSlider(1, ac.getPlayer().getTroopsToDeploy());
                updateMenuVisible(false, true, true, true, false, false);
                break;
            case INITIAL_DEPLOY_CONFIRM:
            case DEPLOY_CONFIRM:
                updatePrompt(ac.getPlayer(), "are you sure you want to send " + ac.getDstArmy() + " troops to " + ac.getDstCountry().getName());
                updateMenuVisible(false, false, true, true, false, false);
                break;
            case ATTACK_SRC:
                updateMap();
                updatePrompt(ac.getPlayer(), "select a country to attack from");
                updateMenuVisible(false, false, false, false, true, false);
                break;
            case ATTACK_DST:
                updateMap(ac.getSrcCountry(),ac.getSrcCountry().getAdjacentUnownedCountries(ac.getPlayer()));
                updatePrompt(ac.getPlayer(),"select a country to attack from "+ac.getSrcCountry().getName());
                updateMenuVisible(false, false,false,true,true, false);
                break;
            case ATTACK_NUM_TROOPS:
                updatePrompt(ac.getPlayer(), "how many troops will you attack " + ac.getDstCountry().getName() + " with");
                updateSlider(1, Math.min(ac.getSrcCountry().getArmy() - 1, 3));
                updateMenuVisible(false, true, true, true, true, false);
                break;
            case ATTACK_CONFIRM:
                updatePrompt(ac.getPlayer(), "are you sure you want to attack " + ac.getDstCountry().getName() + " with " + ac.getSrcArmy() + " troops from " + ac.getSrcCountry().getName());
                updateMenuVisible(false, false, true, true, true, false);
                break;
            case DEFEND_NUM_TROOPS:
                updatePrompt(ac.getDstCountry().getOwner(), "how many troops will you defend with " + ac.getDstCountry().getName() + " with");
                updateSlider(1, Math.min(ac.getDstCountry().getArmy(), 2));
                updateMenuVisible(false, true, true, false, false, false);
                break;
            case DEFEND_CONFIRM:
                updateMap(new Country[]{ac.getSrcCountry(),ac.getDstCountry()});
                updatePrompt(ac.getPlayer(), "are you sure you want to defend " + ac.getDstCountry().getName() + " from " + ac.getSrcCountry().getName() + " with " + ac.getDstArmy() + " troops");
                updateMenuVisible(false, false, true, true, false, false);
                break;
            case RETREAT_NUM_TROOPS:
                updateMap(new Country[]{ac.getSrcCountry(),ac.getDstCountry()});
                displayRolls(ac);
                if (!ac.attackerVictory() || ac.getSrcArmy() - ac.getSrcArmyDead() < 2) {
                    updateMenuVisible(false, false, false, true, false, false);
                } else {
                    updateSlider(0, ac.getSrcArmy() - ac.getSrcArmyDead() - 1);
                    updateMenuVisible(false, true, true, false, false, false);
                }
                break;
            case RETREAT_CONFIRM:
                updatePrompt(ac.getPlayer(), "are you sure you want to send " + ac.getDstArmy() + " troops back to " + ac.getSrcCountry().getName());
                updateMenuVisible(false, false, true, true, true, false);
                break;
            case FORTIFY_SRC:
                updateMap();
                updatePrompt(ac.getPlayer(), "select a country with 2+ troops to fortify from");
                updateMenuVisible(false, false, false, false, true, false);
                break;
            case FORTIFY_DST:
                updateMap(ac.getSrcCountry(), ac.getSrcCountry().getConnectedOwnedCountries(ac.getPlayer()));
                updatePrompt(ac.getPlayer(), "select a country to fortify");
                updateMenuVisible(false, false, false, true, true, false);
                break;
            case FORTIFY_NUM_TROOPS:
                updatePrompt(ac.getPlayer(), "how many troops will you send from " + ac.getSrcCountry().getName() + " to " + ac.getDstCountry().getName());
                updateSlider(1, ac.getSrcCountry().getArmy() - 1);
                updateMenuVisible(false, true, true, true, true, false);
                break;
            case FORTIFY_CONFIRM:
                updatePrompt(ac.getPlayer(), "are you sure you want to transfer " + ac.getSrcArmy() + " troops from " + ac.getSrcCountry().getName() + " to " + ac.getDstCountry().getName());
                updateMenuVisible(false, false, true, true, true, false);
                break;
        }
    }
    private void updateAI(ActionContext ac){
        ActionContext tmp=((PlayerAI)ac.getPlayer()).getMove(ac);
        if(tmp==null){
            updatePrompt(ac.getPlayer()," is ending the phase");
            updateMenuVisible(false,false,false,false,false,true);
            return;
        }
        switch(ac.getPhase()){
            case CLAIM_COUNTRY:
                if(ac.getHighlightedCountries()!=null){
                    updateMap(ac.getHighlightedCountries());
                }
                updatePrompt(tmp.getPlayer()," claimed "+tmp.getDstCountry());
                break;
            case INITIAL_DEPLOY_DST:
            case DEPLOY_DST:
                updateMap(map.getCountries());
                updatePrompt(tmp.getPlayer(), " deployed "+tmp.getDstArmy()+" troops to "+tmp.getDstCountry());
                break;
            case ATTACK_SRC:
                updateMap(map.getCountries());
                updatePrompt(tmp.getPlayer(), " attacked "+tmp.getDstCountry()+" from "+tmp.getSrcCountry()+" with "+tmp.getSrcArmy()+" troops");
                break;
            case RETREAT_NUM_TROOPS:
                updateMap(map.getCountries());
                displayRolls(ac);
                break;
            case FORTIFY_SRC:
                updateMap(map.getCountries());
                updatePrompt(tmp.getPlayer(), "fortified "+tmp.getDstCountry()+" with "+tmp.getSrcArmy()+" troops from "+tmp.getSrcCountry());
                break;
        }
        updateMenuVisible(false,false,false,false,false,true);
    }
    @Override
    public void log(String message){
        eventLogText.append(message);
    }
    @Override
    public void updateSaveFileList(String[] saves) {
        if(saves==null || saves.length==0) {
            loadGame.setText("");
            return;
        }else{
            loadGame.setText("Load Game");
        }

        loadGame.removeAll();
        for(String filename: saves){
            JMenuItem item=new JMenuItem(filename);
            item.setActionCommand("saves/"+filename);
            item.addActionListener(controller);
            loadGame.add(item);
        }
    }
    @Override
    public void updateMapFileList(String[] maps) {
        if(maps==null || maps.length==0) {
            loadMap.setText("");
            return;
        }else{
            loadMap.setText("Load Map");
        }

        loadMap.removeAll();
        for(String filename: maps){
            JMenuItem item=new JMenuItem(filename);
            item.setActionCommand("maps/"+filename);
            item.addActionListener(controller);
            loadMap.add(item);
        }
    }

    private void displayRolls(ActionContext ac){
        StringBuilder diceStr= new StringBuilder("Attacker rolls: [");
        for(int i=0;i<ac.getDiceRolls()[0].length;i++){
            diceStr.append(ac.getDiceRolls()[0][i]);
            if(i<ac.getDiceRolls()[0].length-1){
                diceStr.append(", ");
            }
        }
        diceStr.append("]\nDefender rolls: [");
        for(int i=0;i<ac.getDiceRolls()[1].length;i++){
            diceStr.append(ac.getDiceRolls()[1][i]);
            if(i<ac.getDiceRolls()[1].length-1){
                diceStr.append(", ");
            }
        }
        diceStr.append("]\n\n");

        if(ac.attackerVictory()&&ac.getSrcArmy()-ac.getSrcArmyDead()>1){
            if(!ac.getPlayer().isAI) {
                updatePrompt(diceStr + ac.getPlayer().name + ", how many troops will you send back to " + ac.getSrcCountry().getName());
            }
            else{
                updatePrompt(diceStr.toString());
            }
        }
        else{
            updatePrompt(diceStr.toString());
        }
    }

    private void updateMap(){
        updateMap(map.getCountries());
    }
    private void updateMap(Country country, Country[] countries){
        Country[] tmp=new Country[countries.length+1];
        System.arraycopy(countries, 0, tmp, 0, countries.length);
        tmp[countries.length]=country;

        updateMap(tmp);
    }
    private void updateMap(Country[] countries){
        mapPane.removeAll();
        mapPane.add(mapImage);

        if(countries==null){
            return;
        }

        for (Country country : countries) {
            int width = 18;
            int height = 18;

            if (country.getArmy() >= 10) {
                width = 24;
            }
            if (country.getArmy() >= 100) {
                width = 30;
            }

            JLabel countryLabel = new JLabel("" + country.getArmy());
            countryLabel.setOpaque(true);
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBackground(country.getOwner().getColor());
            countryLabel.setBounds(country.getCenterCoordinates().x - width / 2, country.getCenterCoordinates().y - height / 2, width, height);
            countryLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

            mapPane.add(countryLabel, Integer.valueOf(2));
        }
    }
    private void updatePlayerName(Player player){
        if(player==null){
            return;
        }
        menuPlayerName.setVisible(true);
        menuPlayerName.setOpaque(true);
        menuPlayerName.setBackground(player.getColor());
        menuPlayerName.setText("   "+player.getName()+"   ");
    }
    private void updatePhase(ActionContext ac){
        switch (ac.getPhase()){
            case NUM_HUMANS:
            case NUM_AI:
            case PLAYER_NAME:
                menuPhase.setVisible(false);
                break;
            case CLAIM_COUNTRY:
                menuPhase.setVisible(true);
                menuPhase.setText("Claim");
                break;
            case INITIAL_DEPLOY_DST:
            case INITIAL_DEPLOY_NUM_TROOPS:
            case INITIAL_DEPLOY_CONFIRM:
            case DEPLOY_DST:
            case DEPLOY_NUM_TROOPS:
            case DEPLOY_CONFIRM:
                menuPhase.setText("Deploy "+ac.getPlayer().getTroopsToDeploy()+" Troops");
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
                menuPhase.setText("Attack");
                break;
            case DEFEND_NUM_TROOPS:
            case DEFEND_CONFIRM:
                menuPhase.setText("Defend");
                break;
            case RETREAT_NUM_TROOPS:
            case RETREAT_CONFIRM:
                menuPhase.setText("Retreat");
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_NUM_TROOPS:
            case FORTIFY_CONFIRM:
                menuPhase.setText("Fortify");
                break;
        }
    }
    private void updatePrompt(Player player, String prompt){
        menuPrompt.getHighlighter().removeAllHighlights();
        menuPrompt.setText(" "+player.name+" "+prompt);
        try{
            menuPrompt.getHighlighter().addHighlight(0,player.getName().length()+2, new DefaultHighlighter.DefaultHighlightPainter(player.getColor()));
        }catch(BadLocationException e){
            e.printStackTrace();
        }
    }
    private void updatePrompt(String prompt){
        menuPrompt.setText(prompt);
    }
    private void updateSlider(int min, int max){
        menuSlider.setMinimum(min);
        menuSlider.setMaximum(max);
        if(max-min<=10){
            menuSlider.setLabelTable(menuSlider.createStandardLabels(1));
            menuSlider.setMajorTickSpacing(1);
        }else if(max-min<=20){
            menuSlider.setLabelTable(menuSlider.createStandardLabels(2));
            menuSlider.setMajorTickSpacing(2);
        }else{
            menuSlider.setLabelTable(menuSlider.createStandardLabels(5));
            menuSlider.setMajorTickSpacing(5);
        }
    }
    private void updateMenuVisible(boolean text, boolean slider, boolean confirm, boolean back, boolean skip, boolean ok){
        menuPrompt.setVisible(true);
        menuText.setText("");
        menuText.setVisible(text);
        if(slider){
            menuSlider.setVisible(true);
            menuConfirm.setActionCommand("Number Confirm");
        }else{
            menuSlider.setVisible(false);
            menuConfirm.setActionCommand("Confirm");
        }
        menuConfirm.setVisible(confirm);
        menuBack.setVisible(back);
        menuSkip.setVisible(skip);
        menuOk.setVisible(ok);
    }

    public String saveGame(){
        return JOptionPane.showInputDialog("Enter the filename");
    }

    private GridBagConstraints constraintMaker(int x, int y, int w, int h){
        GridBagConstraints constraints=new GridBagConstraints();
        constraints.insets=new Insets(5,5,5,5);
        constraints.fill=GridBagConstraints.BOTH;
        constraints.gridx=x;
        constraints.gridy=y;
        constraints.gridwidth=w;
        constraints.gridheight=h;
        return constraints;
    }
}