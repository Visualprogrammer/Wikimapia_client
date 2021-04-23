import javax.swing.*;
import java.awt.*;

public class help extends JFrame {
    public help() throws InterruptedException {
        this.setEnabled(true);
        setDefaultCloseOperation(3);
        setBounds(100, 100, 800, 500);
    }
    public static int drstr(Graphics g, int x, int y, String str) {
        g.drawString(str,x,y);
        y+=20;
        return y;
    }
}
