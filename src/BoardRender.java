import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BoardRender extends JFrame {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JPanel mapPanel;
    private JPanel infoPanel;

    private BufferedImage mapImage;

    private JTextArea infoArea;
    private JLabel mapImageLabel;
    private JButton phaseButton;

    public BoardRender() throws IOException {
        setTitle("Risk - GLobal Domination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setResizable(true);
        setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mapPanel(),gbc);
        /*ensures mapPanel is loaded into the frame so dimension information can be used by
        for correctly placing other panels
         */
        pack();

        gbc.anchor=GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 0;

        add(infoPanel(mapPanel.getHeight()),gbc);

        pack();
        setResizable(false);
        setVisible(true);

    }
    private JPanel mapPanel() throws IOException {
        String path = "map.png";
        mapPanel = new MapPanel();
        mapPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        mapImage = ImageIO.read(new File(path));
        mapPanel.setPreferredSize(new Dimension(mapImage.getWidth(),mapImage.getHeight()));
        mapImageLabel = new JLabel(new ImageIcon(mapImage));
        mapPanel.add(mapImageLabel);
        return mapPanel;
    }

    private JPanel infoPanel(int height){
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
        infoPanel.setBorder(new LineBorder(Color.black));

        infoArea = new JTextArea();
        infoArea.setFocusable(true);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        ((DefaultCaret)infoArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane JP = new JScrollPane(infoArea);
        JP.setPreferredSize(new Dimension(200,(int)height/2));
        infoPanel.add(JP,0);

        infoPanel.setSize(new Dimension(200,height));

        return infoPanel;
    }

    public static void main(String[] args) throws Exception{
        new BoardRender();
        //TODO create board render based on currentl game state
    }
}
class MapPanel extends JPanel{
    public MapPanel(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(e.getX()+" "+e.getY());
                //ToDo something with these coords
            }
        });
    }
}
