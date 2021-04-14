import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class tiles {
    private Object sync1 = new Object();
    ArrayList<Integer> xNum = new ArrayList<>();
    ArrayList<Integer> yNum = new ArrayList<>();
    ArrayList<Boolean> downloadOrNo = new ArrayList<>();
    ArrayList<BufferedImage> tileImage = new ArrayList<>();
    ArrayList<Integer> xNumBe = new ArrayList<>();
    ArrayList<Integer> yNumBe = new ArrayList<>();
    private Object sync = new Object();
    //  private BufferedImage[] tileImage;
    private int lengtharrayoftile = 0;
    //  private Integer[] xNum;
    //  private Integer[] yNum;
    private int zoom; // const
    //  private Boolean[] downloadOrNo;
    private int max = 10000;
    ArrayList<Integer> Unused = new ArrayList<>();
    public int x;
    public int y;
    public String quadkey = "";
    public ArrayList<mapobject> obj = new ArrayList<mapobject>();
    public ArrayList<String> obj_arr = new ArrayList<>();
    public void set_xy(int xx, int yy) throws IOException {
        if((x!=xx) || (y!=yy)) {
            x = xx;
            y = yy;
            String q = maths.TileXYToQuadKey(x,y,zoom);
            Boolean down = true;
            for (int i = 0;i<obj_arr.size();i++) {
                if(q.equals(obj_arr.get(i))){
                    down = false;
                }
            }
            if(down) {
                quadkey = q;
                ArrayList<mapobject> m = api.Itile_get(x,y,zoom);
                for(int i = 0; i<m.size();i++) {
                    Boolean t = true;
                    for (int d =0; d<obj.size(); d++) {
                        if(m.get(i).id == obj.get(d).id) {
                            t = false;
                        }
                    }
                    if (t) {
                        obj.add(m.get(i));
                    }
                }
                obj_arr.add(q);
            }
        }
    }
    public tiles(int zoomExt) {
        zoom = zoomExt;
        // xNum = new Integer[max];
        //   yNum = new Integer[max];
        //  tileImage = new BufferedImage[max];
        //  downloadOrNo = new Boolean[max];
        // for(int i = 0; i < max; i++) {
        //     downloadOrNo[i] = false;
        // }
    }
    public void DownloadFile(int tilex , int tiley, String serverOfImage) throws IOException {
        Boolean b = true;
        synchronized (sync1) {
            for(int i = 0; i < xNumBe.size(); i++) {
                if ((tilex == xNumBe.get(i)) && (tiley == yNumBe.get(i))) {
                    b = false;
                }
            }
            xNumBe.add(tilex);
            yNumBe.add(tiley);
        }
        if(b) {
            Thread down = new Thread(new Runnable() {
                @Override
                public void run() {
                    int nowIndex = 0;
                    //  synchronized (sync) {
                    //  nowIndex = lengtharrayoftile;
                    //  lengtharrayoftile++;
                    //   yNum[nowIndex] = tiley;
                    //   xNum[nowIndex] = tilex;
                    //  }
                    String url = null;
                    //   if(serverOfImage.equals("wikimapia")) {
                    int NUM = (tilex % 4) + ((tiley % 4) * 4);
                    url = "http://i" + NUM + ".wikimapia.org/?x=" + tilex + "&y=" + tiley + "&zoom=" + zoom + "&lng=1&type=map";
                    //  }
                    URL Url = null;

                    try {
                        Url = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(Url.openStream());
                        //if (!Files.exists(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"))) {
                        //    Files.createDirectories(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"));
                        // }
                        // if (new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").exists()) {
                        //    new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").delete();
                        // }
                        //File file = new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png");

                        synchronized (sync) {
                            //  BufferedImage a = ImageIO.read(new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png"));
                            downloadOrNo.add(true);
                            tileImage.add(img);
                            yNum.add(tiley);
                            xNum.add(tilex);
                            Unused.add(0);

                        }
                        synchronized (sync1) {
                            //       ImageIO.write(img, "png", file);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } }
            );
            down.start();
        }}
    public int getZoom() {
        return zoom;
    }
    public BufferedImage getImage(int x, int y) throws IOException {
        BufferedImage a = null;
        try{ for(int i = 0; i < xNum.size(); i++) {
            Unused.set(i, Unused.get(i) + 1);
            if((x == xNum.get(i)) && (y == yNum.get(i))) {
                a = tileImage.get(i);
                Unused.set(i, 0);
                return a;
            }
        } }
        catch(Exception e) {
            this.DownloadFile(x,y, "wikimapia");
        }
        //   a = new BufferedImage(256, 256, 0);
        this.DownloadFile(x,y, "wikimapia");
        //   Graphics g = a.getGraphics();
        //  g.setColor(new java.awt.Color(180, 170, 170));
        //  g.fillRect(0, 0, 256, 256);

        return a;
    }
    public int getLength() {
        return lengtharrayoftile;
    }
    public int nowIndex() {
        return lengtharrayoftile;
    }
    public void ClearMemory(int limitOfUnused) {
        for (int q = 0; q < xNum.size(); q++) {
            if (!(limitOfUnused == 0)) {
                if (Unused.get(q) / xNum.size() > limitOfUnused) {
                    int x = xNum.get(q);
                    int y = yNum.get(q);
                    xNum.remove(q);
                    yNum.remove(q);
                    tileImage.remove(q);
                    downloadOrNo.remove(q);
                    Unused.remove(q);
                    for (int w = 0; w < xNumBe.size(); w++) {
                        if (x == xNumBe.get(w) && y == yNumBe.get(w)) {
                            xNumBe.remove(w);
                            yNumBe.remove(w);
                        }
                    }
                }
            }
            else {
                xNum = null;
                yNum = null;
                tileImage = null;
                downloadOrNo = null;
                Unused = null;
                xNumBe = null;
                yNumBe = null;
                xNum = new ArrayList<>();
                yNum = new ArrayList<>();
                tileImage = new ArrayList<>();
                downloadOrNo = new ArrayList<>();
                Unused = new ArrayList<>();
                xNumBe = new ArrayList<>();
                yNumBe = new ArrayList<>();
                obj = new ArrayList<>();
            }
        }
    }
    public void new_tiles_update(int x, int y, int zoom) throws IOException {
        api.Itile_get(x, y, zoom);
    }
}
