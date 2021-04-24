import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class tiles {
    private Object syncObj1 = new Object();
    private Object sync1 = new Object();
    private Object syncObj = new Object();
    int max_tiles;
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
        if ((x != xx) || (y != yy)) {
            x = xx;
            y = yy;
            String q = maths.TileXYToQuadKey(x, y, zoom);
            Boolean down = true;
            for (int i = 0; i < obj_arr.size(); i++) {
                if (q.equals(obj_arr.get(i))) {
                    down = false;
                }
            }
            if (down) {
                obj_arr.add(q);
                Thread downobj = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<mapobject> m = null;
                            int xz;
                            int yz;
                            synchronized (syncObj) {
                                xz = xx;
                                yz = yy;
                                quadkey = q;
                            }
                            m = api.Itile_get(xz, yz, zoom);
                            synchronized (syncObj1) {
                                for (int i = 0; i < m.size(); i++) {
                                    Boolean t = true;
                                    for (int d = 0; d < obj.size(); d++) {
                                        if (m.get(i).id == obj.get(d).id) {
                                            t = false;
                                        }
                                    }
                                    if (t) {
                                        obj.add(m.get(i));
                                    }
                                }
                                obj_arr.add(q);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                downobj.start();
            }
        }
    }

    public tiles(int zoomExt) {
        zoom = zoomExt;
        max_tiles = (int) (256 << zoom)/256;
        // xNum = new Integer[max];
        //   yNum = new Integer[max];
        //  tileImage = new BufferedImage[max];
        //  downloadOrNo = new Boolean[max];
        // for(int i = 0; i < max; i++) {
        //     downloadOrNo[i] = false;
        // }
    }

    public void DownloadFile(int tilex, int tiley, String serverOfImage) throws IOException {
        Boolean b = true;
        synchronized (sync1) {
            for (int i = 0; i < xNumBe.size(); i++) {
                if ((tilex == xNumBe.get(i)) && (tiley == yNumBe.get(i))) {
                    b = false;
                }
            }
            xNumBe.add(tilex);
            yNumBe.add(tiley);
        }
        if (b) {
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
                    if (serverOfImage.equals("wikimapia")) {
                        int NUM = (tilex % 4) + ((tiley % 4) * 4);
                        url = "http://i" + NUM + ".wikimapia.org/?x=" + tilex + "&y=" + tiley + "&zoom=" + zoom + "&lng=1&type=map";
                    } else if (serverOfImage.equals("OSM")) {
                        url = "https://tile.openstreetmap.org/" + zoom + "/" + tilex + "/" + tiley + ".png";
                    }
                    URL Url = null;

                    try {
                        Url = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    BufferedImage img = null;
                    try {
                        URLConnection conn = Url.openConnection();
                        if (serverOfImage.equals("OSM")) {
                            conn.addRequestProperty("authority", "tile.openstreetmap.org");
                            conn.addRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                            conn.addRequestProperty("accept-encoding", "deflate");
                            conn.addRequestProperty("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
                            conn.addRequestProperty("cache-control", "no-cache");
                            conn.addRequestProperty("dnt", "1");
                            conn.addRequestProperty("pragma", "no-cache");
                            conn.addRequestProperty("sec-fetch-dest", "document");
                            conn.addRequestProperty("sec-fetch-mode", "navigate");
                            conn.addRequestProperty("sec-fetch-site", "none");
                            conn.addRequestProperty("sec-fetch-user", "?1");
                            conn.addRequestProperty("upgrade-insecure-requests", "1");
                            conn.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36 OPR/75.0.3969.218);");
                        }
                        InputStream in = conn.getInputStream();
                        img = ImageIO.read(in);
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
                }
            }
            );
            down.start();
        }
    }

    public int getZoom() {
        return zoom;
    }

    public BufferedImage getImage(int x, int y, String serv) throws IOException {
        BufferedImage a = null;
        try {
            for (int i = 0; i < xNum.size(); i++) {
                Unused.set(i, Unused.get(i) + 1);
                if ((x == xNum.get(i)) && (y == yNum.get(i))) {
                    a = tileImage.get(i);
                    Unused.set(i, 0);
                    return a;
                }
            }
        } catch (Exception e) {
            if((x<=max_tiles) && (y <= max_tiles)) {
                this.DownloadFile(x, y, serv);
           }
        }
        //   a = new BufferedImage(256, 256, 0);
       if((x<=max_tiles) && (y <= max_tiles)) {
            this.DownloadFile(x, y, serv);
       }
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
            } else {
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
                //  obj = new ArrayList<>();
                //   obj_arr = new ArrayList<>();
            }
        }
    }

    public void clear_obj() {
        //    obj_arr = new ArrayList<>();
    }

    public void new_tiles_update(int x, int y, int zoom) throws IOException {
        api.Itile_get(x, y, zoom);
    }
}
