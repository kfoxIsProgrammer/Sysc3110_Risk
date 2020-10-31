/**Rendering the map based on the current state of the game
 * @authour Kshitij Sawhney
 * @version 10 / 31 / 2020
 */

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.*;

public class MapRender extends JFrame implements MouseListener{
    int x,y;
    private JPanel cPane;
    Image img =Toolkit.getDefaultToolkit().getImage("maps//map.png");
    public MapRender() throws IOException{
        addMouseListener(this);
        cPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img,0,0,getWidth(),getHeight(),null);
            }
        };
        setContentPane(cPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        x=e.getX();
        y=e.getY();
        //TODO what do we do with these coordinates?
    }

    @Override
    public void mousePressed(MouseEvent e) {
        return;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        return;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        return;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        return;
    }

    public static void main(String[] args) throws Exception{
        new MapRender();
    }
}