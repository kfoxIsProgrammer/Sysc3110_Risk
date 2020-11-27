import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class RiskView extends JFrame{
    private RiskController riskController;
    private Map map;
    private int mapHeight;
    private int mapWidth;

    private JLayeredPane mapPane;
    private JLabel mapImage;

    private JScrollPane eventLogPane;
    private JTextArea eventLogText;

    private JPanel menuPanel;
    private JTextArea menuPrompt;
    private JTextField menuText;
    private JSlider menuSlider;
    private JButton menuConfirm;
    private JButton menuBack;
    private JButton menuSkip;

    RiskView(RiskController riskController, Map map){
        setTitle("Risk - Global Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        this.riskController=riskController;
        this.map=map;
        this.mapHeight=map.getMapImage().getHeight();
        this.mapWidth=map.getMapImage().getWidth();

        setLayout(new GridBagLayout());

        //Initialize image
        mapImage=new JLabel(new ImageIcon(map.getMapImage()));
        mapImage.setBounds(0,0,mapWidth,mapHeight);
        mapImage.addMouseListener(riskController);
        mapPane=new JLayeredPane();
        mapPane.setPreferredSize(new Dimension(mapWidth,mapHeight));
        mapPane.add(this.mapImage);
        this.add(mapPane,constraintMaker(0,0,1,2));

        //Initialize event log
        eventLogText=new JTextArea("Game Started\n\n");
        eventLogText.setEditable(false);
        eventLogText.setLineWrap(true);

        eventLogPane=new JScrollPane(eventLogText);
        eventLogPane.setPreferredSize(new Dimension(mapWidth/3, mapHeight/2));
        eventLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        eventLogPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        this.add(eventLogPane,constraintMaker(1,0,1,1));

        //Initialize menu
        menuPrompt=new JTextArea();
        menuPrompt.setEditable(false);
        menuPrompt.setLineWrap(true);
        menuPrompt.setWrapStyleWord(true);
        menuPrompt.setPreferredSize(new Dimension(mapWidth/3,mapHeight*3/12));

        menuText=new JTextField();
        menuText.addActionListener(riskController);

        menuSlider=new JSlider();
        menuSlider.setPaintLabels(true);
        menuSlider.setPaintTicks(true);
        menuSlider.addChangeListener(riskController);

        menuConfirm=new JButton("Confirm");
        menuConfirm.addActionListener(riskController);

        menuBack=new JButton("Back");
        menuBack.addActionListener(riskController);

        menuSkip=new JButton("Skip");
        menuSkip.addActionListener(riskController);

        menuPanel=new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        menuPanel.add(menuPrompt,   constraintMaker(0,0,3,3));
        menuPanel.add(menuText,     constraintMaker(0,3,3,1));
        menuPanel.add(menuSlider,   constraintMaker(0,4,3,1));
        menuPanel.add(menuConfirm,  constraintMaker(0,5,1,1));
        menuPanel.add(menuBack,     constraintMaker(1,5,1,1));
        menuPanel.add(menuSkip,     constraintMaker(2,5,1,1));

        this.add(menuPanel,constraintMaker(1,1,1,1));

        this.pack();
        this.setVisible(true);
    }

    public void update(ActionContext ac){
        System.out.printf("View Phase: %s\n",ac.getPhase().name());
        switch(ac.getPhase()){
            case NUM_PLAYERS:
                updatePrompt("Enter the number of players");
                updateSlider(2,6);
                updateMenuVisible(true, false, true,true,false,false);
                break;
            case NUM_AI:
                updatePrompt("Enter the number of AI");
                updateSlider(0,6-ac.getPlayerId());
                updateMenuVisible(true, false, true,true,false,false);
                break;
            case PLAYER_NAME:
                updatePrompt("Player "+(ac.getPlayerId()+1)+", enter you name");
                updateMenuVisible(true, true, false,false,false,false);
                break;
            case DEPLOY_DST:
                updatePrompt(ac.getPlayer(),"choose a country to deploy troops to");
                updateMenuVisible(true, false, false,false,false,false);
                updateMap();
                break;
            case DEPLOY_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you send to "+ac.getDstCountry().getName());
                updateSlider(1,ac.getPlayer().getTroopsToDeploy());
                updateMenuVisible(true, false, true,true,true,false);
                break;
            case DEPLOY_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to send "+ac.getDstArmy()+" troops to "+ac.getDstCountry().getName());
                updateMenuVisible(true, false, false,true,true,false);
                break;
            case ATTACK_SRC:
                updatePrompt(ac.getPlayer(),"select a country to attack from");
                updateMenuVisible(true, false, false,false,false,true);
                updateMap();
                break;
            case ATTACK_DST:
                updatePrompt(ac.getPlayer(),"select a country to attack from "+ac.getSrcCountry().getName());
                updateMenuVisible(true, false, false,false,true,true);
                updateMap(ac.getSrcCountry(),ac.getSrcCountry().getAdjacentUnownedCountries(ac.getPlayer()));
                break;
            case ATTACK_SRC_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you attack "+ac.getDstCountry().getName()+" with");
                updateSlider(1,Math.min(ac.getSrcArmy()-1,3));
                updateMenuVisible(true, false, true,true,true,true);
                break;
            case ATTACK_SRC_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to attack "+ac.getDstCountry().getName()+" with "+ac.getSrcArmy()+" troops from "+ac.getSrcCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                break;
            case ATTACK_DST_ARMY:
                updatePrompt(ac.getDstCountry().getOwner(),"how many troops will you defend with "+ac.getDstCountry().getName()+" with");
                updateSlider(1,Math.min(ac.getDstCountry().getArmy(), 2));
                updateMenuVisible(true, false, true,true,false,false);
                break;
            case ATTACK_DST_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to defend "+ac.getDstCountry().getName()+" from "+ac.getSrcCountry().getName()+" with "+ac.getDstArmy()+" troops");
                updateMenuVisible(true, false, false,true,false,false);
                break;
            case RETREAT_ARMY:
                displayRolls(ac);
                if (!ac.isAttackerVictory()||ac.getSrcArmy()-ac.getSrcArmyDead()<2) {
                    updateMenuVisible(true, false, false,false,true,true);
                } else {
                    updateSlider(0,ac.getSrcArmy()-ac.getSrcArmyDead()-1);
                    updateMenuVisible(true, false, true,true,false,true);
                }
                break;
            case RETREAT_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to send "+ac.getDstArmy()+" troops back to "+ac.getSrcCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                break;
            case FORTIFY_SRC:
                updatePrompt(ac.getPlayer(),"select a country with 2+ troops to fortify from");
                updateMenuVisible(true, false, false,false,false,true);
                updateMap();
                break;
            case FORTIFY_DST:
                updatePrompt(ac.getPlayer(),"select a country to fortify");
                updateMenuVisible(true, false, false,false,true,true);
                updateMap(ac.getSrcCountry(),ac.getSrcCountry().getConnectedOwnedCountries(ac.getPlayer()));
                break;
            case FORTIFY_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you send from "+ac.getSrcCountry().getName()+" to "+ac.getDstCountry().getName());
                updateSlider(1,ac.getSrcCountry().getArmy()-1);
                updateMenuVisible(true, false, true,true,true,true);
                break;
            case FORTIFY_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to transfer "+ac.getSrcArmy()+" troops from "+ac.getSrcCountry().getName()+" to "+ac.getDstCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                break;
        }
        menuConfirm.setActionCommand(ac.getPhase().name());
    }

    private void displayRolls(ActionContext ac){
        if(ac.isAttackerVictory()){
            eventLogText.append(ac.getPlayer().name+" won the battle\n\t"+
                    "Attacker lost "+ac.getSrcArmyDead()+" troops\n\t"+
                    "Defender lost "+ac.getDstArmyDead()+" troops\n");
        }else {
            eventLogText.append(ac.getPlayer().name+" lost the battle\n\t"+
                    "Attacker lost "+ac.getSrcArmyDead()+" troops\n\t"+
                    "Defender lost "+ac.getDstArmyDead()+" troops\n");
        }

        String diceStr="Attacker rolls: [";
        for(int i=0;i<ac.getDiceRolls()[0].length;i++){
            diceStr+=ac.getDiceRolls()[0][i];
            if(i<ac.getDiceRolls()[0].length-1){
                diceStr+=", ";
            }
        }
        diceStr+="]\nDefender rolls: [";
        for(int i=0;i<ac.getDiceRolls()[1].length;i++){
            diceStr+=ac.getDiceRolls()[1][i];
            if(i<ac.getDiceRolls()[1].length-1){
                diceStr+=", ";
            }
        }
        diceStr+="]\n\n";

        if(ac.isAttackerVictory()&&ac.getSrcArmy()-ac.getSrcArmyDead()>1){
            updatePrompt(diceStr+ac.getPlayer().name+", how many troops will you send back to "+ac.getSrcCountry().getName());
        }
        else{
            updatePrompt(diceStr);
        }
    }

    private void updateMap(){
        updateMap(map.getCountries());
    }
    private void updateMap(Country country, Country[] countries){
        Country[] tmp=new Country[countries.length+1];
        for(int i=0;i<countries.length;i++){
            tmp[i]=countries[i];
        }
        tmp[countries.length]=country;

        updateMap(tmp);
    }
    private void updateMap(Country[] countries){
        mapPane.removeAll();
        mapPane.add(mapImage);

        for(int i=0;i<countries.length;i++){
            Country country=countries[i];

            int width=18;
            int height=18;

            if(country.getArmy()>=10){
                width=24;
            }
            if(country.getArmy()>=100){
                width=30;
            }

            JLabel countryLabel=new JLabel(""+country.getArmy());
            countryLabel.setOpaque(true);
            countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            countryLabel.setBackground(country.getOwner().getColor());
            countryLabel.setBounds(country.getCenterCoordinates().x-width/2,country.getCenterCoordinates().y-height/2,width,height);
            countryLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,3));

            mapPane.add(countryLabel,Integer.valueOf(2));
        }
    }
    private void updatePrompt(Player player, String prompt){
        menuPrompt.getHighlighter().removeAllHighlights();
        menuPrompt.setText(" "+player.name+", "+prompt);
        try{
            menuPrompt.getHighlighter().addHighlight(0,player.getName().length()+2, new DefaultHighlighter.DefaultHighlightPainter(player.getColor()));
        }catch(BadLocationException e){}
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
    private void updateMenuVisible(boolean prompt, boolean text, boolean slider, boolean confirm, boolean back, boolean skip){
        menuPrompt.setVisible(prompt);
        menuText.setVisible(text);
        menuText.setText("");
        menuSlider.setVisible(slider);
        menuConfirm.setVisible(confirm);
        menuBack.setVisible(back);
        menuSkip.setVisible(skip);
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
    private Country[] listToArray(ArrayList<Country> countriesList){
        Country[] countriesArray=new Country[countriesList.size()];
        countriesArray=countriesList.toArray(countriesArray);
        return  countriesArray;
    }
}