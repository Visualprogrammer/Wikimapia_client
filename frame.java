import javax.swing.*;
import java.awt.*;

public class frame extends JFrame {
    public frame(boolean a, int x, int y, int w, int h) throws InterruptedException {
        this.setEnabled(a);
        setDefaultCloseOperation(3);
        setBounds(x,y,w,h);
    }
}
