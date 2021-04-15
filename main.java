import java.io.IOException;

public class main {
    public static void main(String[] args) throws InterruptedException, IOException {
        frame f = new frame(true,100, 100, 800, 500);
        map Map = new map(60, 30, 14, 1080, 1920, "wikimapia", f.getInsets().top);
      //  api api = new api();
        f.add(Map);
        f.setVisible(true);
        while(true) {
            // Map.viewDiagn("Рис.");
            Map.drawMap(f.getGraphics());
            Thread.sleep(40);
        }
    }
}
