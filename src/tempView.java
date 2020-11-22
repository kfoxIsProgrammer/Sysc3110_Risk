import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Dictionary;
import java.util.Hashtable;

public class tempView extends JFrame{
    private RiskController riskController;

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

    private int mapHeight;
    private int mapWidth;

    tempView(RiskController riskController, BufferedImage mapImage, Country[] countries){
        setTitle("Risk - Global Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        this.riskController=riskController;
        this.mapHeight=mapImage.getHeight();
        this.mapWidth=mapImage.getWidth();

        setLayout(new GridBagLayout());

        //Initialize image
        this.mapImage=new JLabel(new ImageIcon(mapImage));
        this.add(this.mapImage,constraintMaker(0,0,1,2));

        //Initialize event log
        eventLogText=new JTextArea("Game Started\n");
        eventLogText.setEditable(false);
        eventLogText.setLineWrap(true);

        eventLogPane=new JScrollPane(eventLogText);
        eventLogPane.setPreferredSize(new Dimension(mapWidth/3, mapHeight/2));
        eventLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(eventLogPane,constraintMaker(1,0,1,1));

        //Initialize menu
        menuPrompt=new JTextArea();
        menuPrompt.setEditable(false);
        menuPrompt.setPreferredSize(new Dimension(mapWidth/3,mapHeight*3/12));
        menuText=new JTextField();
        menuSlider=new JSlider();
        menuSlider.setPaintLabels(true);
        menuSlider.setPaintTicks(true);
        menuSlider.addChangeListener(riskController);
        menuConfirm=new JButton("Confirm");
        menuBack=new JButton("Back");
        menuSkip=new JButton("Skip");
        menuConfirm.addActionListener(riskController);
        menuPanel=new JPanel(new GridBagLayout());
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

    public void update(ActionContext actionContext){
        switch(actionContext.getPhase()){
            case NEW_GAME:
                break;
            case NUM_PLAYERS:
                updatePrompt("Enter the number of players");
                updateSlider(2,6);
                updateMenuVisible(true, false, true,true,false,false);
                break;
            case PLAYER_NAME:
                updatePrompt("Player "+1+", enter you name");
                break;
        }
        menuConfirm.setActionCommand(actionContext.getPhase().name());
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

    public static void main(String[] args) {
        Map map=new MapImport("maps/demo.zip").getMap();

        tempView test=new tempView(new RiskController(),map.getMapImage(),map.getCountries());

        ActionContext ac=new ActionContext(Phase.NUM_PLAYERS,new PlayerHuman("Test",Color.BLUE,10));
        test.update(ac);
    }
}
