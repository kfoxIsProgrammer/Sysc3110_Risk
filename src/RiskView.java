import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class RiskView extends JFrame{
    private RiskController riskController;
    private Map map;
    private int mapHeight;
    private int mapWidth;

    private JLayeredPane mapPane;
    private JLabel mapImage;
    private JLabel[] countryLabels;

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
        eventLogText=new JTextArea("Game Started\n");
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
        System.out.printf("%s\n",ac.getPhase().name());
        switch(ac.getPhase()){
            case NUM_PLAYERS:
                updatePrompt("Enter the number of players");
                updateSlider(2,6);
                updateMenuVisible(true, false, true,true,false,false);
                break;
            case PLAYER_NAME:
                updatePrompt("Player "+(ac.getPlayerId()+1)+", enter you name");
                updateMenuVisible(true, true, false,false,false,false);
                break;
            case DEPLOY_DST:
                updateMap();
                updatePrompt(ac.getPlayer(),"choose a country to deploy troops to");
                updateMenuVisible(true, false, false,false,false,false);
                break;
            case DEPLOY_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you send to "+ac.getDstCountry().getName());
                updateSlider(1,ac.getPlayer().getArmiesToAllocate());
                updateMenuVisible(true, false, true,true,true,false);
                updateMap();
                break;
            case DEPLOY_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to send "+ac.getDstArmy()+" troops to "+ac.getDstCountry().getName());
                updateMenuVisible(true, false, false,true,true,false);
                updateMap();
            case ATTACK_SRC:
                updatePrompt(ac.getPlayer(),"select a country to attack from");
                updateSlider(1,ac.getPlayer().getArmiesToAllocate());
                updateMenuVisible(true, false, false,false,true,true);
                updateMap();
                break;
            case ATTACK_DST:
                updatePrompt(ac.getPlayer(),"select a country to attack from "+ac.getSrcCountry().getName());
                updateSlider(1,ac.getPlayer().getArmiesToAllocate());
                updateMenuVisible(true, false, false,false,true,true);
                updateMap();
                break;
            case ATTACK_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you attack "+ac.getDstCountry().getName()+" with");
                updateSlider(1,ac.getPlayer().getArmiesToAllocate());
                updateMenuVisible(true, false, true,true,true,true);
                updateMap();
                break;
            case ATTACK_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to attack "+ac.getDstCountry().getName()+" with "+ac.getSrcArmy()+" troops from "+ac.getSrcCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                updateMap();
                break;
            case RETREAT_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you send back to "+ac.getSrcCountry().getName());
                updateSlider(0,ac.getSrcArmy()-ac.getSrcArmyDead());
                updateMenuVisible(true, false, true,true,true,true);
                updateMap();
                break;
            case RETREAT_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to send"+ac.getDstArmy()+" troops back to "+ac.getSrcCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                updateMap();
                break;
            case FORTIFY_SRC:
                updatePrompt(ac.getPlayer(),"select a country to transfer troops from");
                updateMenuVisible(true, false, false,false,true,true);
                updateMap();
                break;
            case FORTIFY_DST:
                updatePrompt(ac.getPlayer(),"select a country to transfer troops to");
                updateMenuVisible(true, false, false,false,true,true);
                updateMap();
                break;
            case FORTIFY_ARMY:
                updatePrompt(ac.getPlayer(),"how many troops will you send from "+ac.getSrcCountry()+" to "+ac.getDstCountry().getName());
                updateSlider(0,ac.getSrcArmy()-ac.getSrcArmyDead());
                updateMenuVisible(true, false, true,true,true,true);
                updateMap();
                break;
            case FORTIFY_CONFIRM:
                updatePrompt(ac.getPlayer(),"are you sure you want to transfer "+ac.getSrcArmy()+" troops from "+ac.getSrcCountry().getName()+" to "+ac.getDstCountry().getName());
                updateMenuVisible(true, false, false,true,true,true);
                updateMap();
                break;
        }
        menuConfirm.setActionCommand(ac.getPhase().name());
    }

    private void updateMap(){
        mapPane.removeAll();
        mapPane.add(mapImage);

        for(int i=0;i<map.getCountries().length;i++){
            Country country=map.getCountries()[i];

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
}