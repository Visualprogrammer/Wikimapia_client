import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.random;

public class map extends JPanel implements KeyEventDispatcher, Runnable, MouseMotionListener, MouseWheelListener, MouseListener {
    Boolean openimg = false;
    int opnimg = 0;
    ArrayList<Polygon> openi = new ArrayList<>();
    Boolean needDownload = true;
    Font Title = new Font("title", Font.ITALIC, 25);
    Font Desc = new Font("desc", Font.BOLD, 17);
    mapobject opened=new mapobject(0,0,0,0,null,0);
    String split_ph = " jkdfndgujihsuttvawjyajgtuyhfuyhjyffdjwauygeyiwgishjsjkshieihgyesiuheuiegugiuig ";
    api API = new api();
    boolean seehelp = false;
    int limit = 6;
    BufferedImage test = ImageIO.read(new URL("https://polarnick.com/static/unicorn.png").openStream());
    int lengtharrayoftile = 0;
    int leftTileX;
    int leftTileY;
    int top = 0;
    // int leftx; возможно не нужно
    // int lefty;
    int zoom; // приближение
    double lat = 0; // широта
    double lon = 0; // долгота
    boolean seepolygons = true;
    int maxTileByX = 0; // максимальное количество квадратов по ширине окна
    int maxTileByY = 0; // максимальное количество квадратов по высоте окна
    int h; // высота окна
    int w; // ширина окна
    int x; // координата x карты в проекции Меркатора
    int y; // координата y карты в проекции Меркатора
    int shiftX; // сдвиг по x квадрата карты
    int shiftY; // сдвиг по y квадрата карты
    int usershiftXDown = 10; // = 10 сдвиг по нажатию кнопки
    int usershiftYRight = 10; // = 10 сдвиг по нажатию кнопки
    int usershiftXUp = -10; // = - 10 сдвиг по нажатию кнопки
    int usershiftYLeft = -10; // = - 10 сдвиг по нажатию кнопки
    mapobject choosed = new mapobject(0,0,0,0,null,0);
    // Boolean[] tileImageStatus;// двумерный массив статуса загрузки [max x][max y]
    //Integer[] xTilesNum; // номер по x квадрата
    // Integer[] yTilesNum; // номер  по y квадрата
    // BufferedImage[] tileImage; // одномерный массив изображений [max x][max y]
    String server;
    tiles[] tiles = new tiles[23 - 2]; // от 3 до 22

    @Override
    public void run() { //второй поток (загрузка изображений)
        if (needDownload) {
            needDownload = false;
            int centerX = (int) Math.floor(x / 256);
            int centerY = (int) Math.floor(y / 256);
            viewDiagn(centerX + " x центр " + centerY + " y центр");
            int startX = (int) (centerX - 5);// Math.floor(maxTileByX / 2));
            int startY = (int) (centerY - 5);//Math.floor(maxTileByY / 2));
            leftTileX = startX;
            leftTileY = startY;
            //  viewDiagn("Download started");
            int a = 0;
            for (int i = 0; i < maxTileByX; i++) {
                for (int p = 0; p < maxTileByY; p++) {
                    int tilex = startX + i;
                    int tiley = startY + p;
                    try {
                        tiles[zoom - 3].DownloadFile(tilex, tiley, server);
                    } catch (IOException e) {

                    }
                }
            }
        }
        //while (false) {
        //  if (needDownload) {
        //    needDownload = false;
        // }
        // try {
        //    Thread.sleep(40);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        // }
        //}
    }

    public map(double latA, double lonA, int z, int hei, int wei, String serv, int t) throws InterruptedException, IOException { //
        test = ImageIO.read(new URL("https://polarnick.com/static/unicorn.png").openStream());
        opened.isOpened = false;
        top = t;
        server = serv;
        zoom = z;
        lat = latA;
        lon = lonA;
        h = hei;
        w = wei;
        viewDiagn("https://tile.openstreetmap.org/" + getTileNumber(lat, lon, zoom) + ".png");
        viewDiagn(getX());
        viewDiagn(getY());
        x = ((int) (getX() / 256)) * 256;
        y = ((int) (getY() / 256)) * 256;
        shiftX = x - (getTileX() * 256);
        shiftY = y - (getTileY() * 256);
        // leftx = x - (hei/2); вынесено в отрисовку
        //   lefty = y - (wei/2);
        // maxTileByX = (int)(Math.ceil((w/256) + 3));
        // maxTileByY = (int)(Math.ceil((h/256) + 3));
        maxTileByX = 16;
        maxTileByY = 13;
        //   for(int i = 0; i<maxTileByY-1; i++) { // запись статуса не загружено
        //      for(int p = 0; p<maxTileByX; p++) {
        //         tileImageStatus[i][p] = false;
        //     }
        //  }
        for (int i = 0; i < 23 - 2; i++) {
            tiles[i] = new tiles(i + 3);
        }
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        Thread childTread = new Thread(this);
        childTread.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        this.viewDiagn("Repaint");
        try {
            this.drawMap(g);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTileNumber(final double lat, final double lon, final int zoom) { // получение номера квадрата
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        viewDiagn(xtile * 256);
        viewDiagn(ytile * 256);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

    public int getX() { // получение координаты x
        double l = lon * PI / 180; // преобразуем в радианы
        int x = (int) ((256 / (2 * PI)) * (Math.pow(2, zoom)) * (l + PI));
        return x;
    }

    public int getY() { // получение координаты y
        double l = lat * PI / 180; // преобразуем в радианы
        int y = (int) ((256 / (2 * PI)) * (Math.pow(2, zoom)) * (PI - Math.log(Math.tan((PI / 4) + (l / 2)))));
        return y;
    }

    public int getTileX() {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return xtile;
    }

    public int getTileY() {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ytile;
    }

    public void viewDiagn(String a) {
        //System.out.println(a);
    }

    public void viewDiagn(int a) {
        //System.out.println(a);
    }

    public void viewDiagn(double a) {
        //System.out.println(a);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        //    System.out.println("Новое событие кнопок клавиатуры!" + e.getKeyCode());
        String typeOfEvent = "unknown";
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            typeOfEvent = "pressed";
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            typeOfEvent = "released";
        } else if (e.getID() == KeyEvent.KEY_TYPED) {
            typeOfEvent = "typed";
        }
        int centerx = (x - shiftX) / 256;
        int centery = (y - shiftY) / 256;
        String key = "unknown";
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            key = "space";
        } else if ((e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode()==KeyEvent.VK_A)) {
            key = "enter";
            shiftX += usershiftYLeft;
        } else if ((e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode()==KeyEvent.VK_W)) {
            key = "enter";
            shiftY += usershiftXUp;
        } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT)  || (e.getKeyCode()==KeyEvent.VK_D)){
            key = "enter";
            shiftX += usershiftYRight;
        } else if ((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode()==KeyEvent.VK_S)) {
            key = "enter";
            shiftY += usershiftXDown;
        } else if (e.getKeyCode() == 107) { //приближение плюс
            lengtharrayoftile = 0;
            // tileImage = new BufferedImage[maxTileByY * maxTileByX + 10000]; // создание массива для всех квадратов
            //   tileImageStatus = new Boolean[maxTileByY * maxTileByX + 10000];
            //  xTilesNum = new Integer[maxTileByX * maxTileByY + 10000];
            //  yTilesNum = new Integer[maxTileByY * maxTileByX + 10000];
           zoomUp();

        } else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
            key = "code#" + e.getKeyCode();
            lengtharrayoftile = 0;
            //  tileImage = new BufferedImage[maxTileByY * maxTileByX + 10000]; // создание массива для всех квадратов
            //  tileImageStatus = new Boolean[maxTileByY * maxTileByX + 10000];
            // xTilesNum = new Integer[maxTileByX * maxTileByY + 10000];
            //  yTilesNum = new Integer[maxTileByY * maxTileByX + 10000];
            zoomDown();
        } else if(e.getKeyCode() == KeyEvent.VK_1) {
            server = "OSM";
            for (int i = 0; i < 23 - 2; i++) {
                tiles[i] = new tiles(i + 3);
            }
        } else if(e.getKeyCode() == KeyEvent.VK_2) {
            server = "wikimapia";
            for (int i = 0; i < 23 - 2; i++) {
                tiles[i] = new tiles(i + 3);
            }
        } else if (e.getKeyCode()==KeyEvent.VK_V) {
            seepolygons = true;
        } else if(e.getKeyCode() == KeyEvent.VK_B) {
            seepolygons = false;
        } else if(e.getKeyCode()==KeyEvent.VK_0) {
            seehelp = true;
        }

        if (shiftX > 256) {
            x = x + 256;
            shiftX = shiftX - 256;
            //   needDownload = true;
        }
        if (shiftY > 256) {

            y = y + 256;
            shiftY = shiftY - 256;
            //    needDownload = true;
        }
        if (shiftX < -256) {

            x -= 256;
            shiftX += 256;
            //   needDownload = true;

        }
        if (shiftY < -256) {

            y -= 256;
            shiftY += 256;
            //   needDownload = true;

        }
        for (int r = 0; r < tiles.length; r++) {
            if (!(r == zoom - 3)) {
                tiles[r].ClearMemory(1);
            } else {
                tiles[r].ClearMemory(limit);
            }
        }
        viewDiagn((String.valueOf(x / 256 + " " + y / 256)));
        //    System.out.println("type=" + typeOfEvent + " keyCode=" + key);
        return false;
    }

    // private String DownloadFile(String url, int tilex, int tiley) throws IOException {
    //    URL Url = new URL(url);
    //  BufferedImage img = ImageIO.read(Url.openStream());
    //   if (!Files.exists(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"))) {
    //       Files.createDirectories(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"));
    //   }
    //   if (new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").exists()) {
    ///       //  new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").delete();
    //  } else {
    //       File file = new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png");
    ///      ImageIO.write(img, "png", file);
    //  }
    //  return null;
    // }

    // private BufferedImage DownFile(String url, int tilex, int tiley) throws IOException {
    ///     URL Url = new URL(url);
    //    BufferedImage img = ImageIO.read(Url.openStream());
    //     if (!Files.exists(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"))) {
    //       Files.createDirectories(Paths.get("D:\\image\\" + zoom + "\\" + tilex + "\\"));
    //   }
    //    if (new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").exists()) {
    //        new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png").delete();
    //    } else {
    //    File file = new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png");
    //      ImageIO.write(img, "png", file);
    //  }
    //   return ImageIO.read(new File("D:\\image\\" + zoom + "\\" + tilex + "\\" + tiley + ".png"));
    //  }

    public void drawMap(Graphics gx) throws IOException {
        try {
        BufferedImage buff = new BufferedImage(1920, 1080, 1);
        Graphics g = buff.getGraphics();
        // viewDiagn("Отрисовка");
            if(!seehelp) {
                if (!opened.isOpened) {
                    tiles[zoom - 3].set_xy(x / 256, y / 256);
                    int zx = x / 256;
                    int zy = y / 256;
                    for (int x1 = (zx - 6); x1 < (zx + 6); x1++) {
                        for (int y1 = (zy - 6); y1 < (zy + 6); y1++) {
                            try {
                                tiles[zoom - 3].set_xy(x1, y1);
                                //    g.fillRect(-x + x1*256 - shiftX, -y + y1*256 - shiftY, 256,256);
                                g.drawImage(tiles[zoom - 3].getImage(x1, y1, server), -x + x1 * 256 - shiftX + w / 2, -y + y1 * 256 - shiftY + h / 2, null);
                                //  g.drawRect(-x + x1 * 256 - shiftX + w / 2, -y + y1 * 256 - shiftY + h / 2, 256, 256);
                                //      g.drawString(x1 + " "+ y1,-x + x1*256 - shiftX + 20, -y + y1*256 - shiftY +120);
                            } catch (Exception e) {
                            }
                        }
                    }
                    if (seepolygons) {
                        ArrayList<mapobject> obj = tiles[zoom - 3].obj;
                        for (int i = 0; i < obj.size(); i++) {
                            Polygon qwe = new Polygon();
                            for (int a = 0; a < obj.get(i).xx.size(); a++) {
                                qwe.addPoint(-x + obj.get(i).xx.get(a) * 2 * 2 - shiftX + w / 2, -y + obj.get(i).yy.get(a) * 2 * 2 - shiftY + h / 2);
                            }
                            g.setColor(Color.BLACK);
                            g.drawPolygon(qwe);
                        }
                    }
                    Polygon qwe = new Polygon();
                    for (int i = 0; i < choosed.xx.size(); i++) {
                        qwe.addPoint(-x + choosed.xx.get(i) * 2 * 2 - shiftX + w / 2, -y + choosed.yy.get(i) * 2 * 2 - shiftY + h / 2);
                    }
                    g.setColor(new Color(196, 79, 79, 200));
                    if (qwe.npoints > 2) {
                        g.fillPolygon(qwe);
                    }//g.drawImage(test,500,500,null);
                    if (!seehelp) {
                        g.setColor(Color.cyan);
                        g.fillRect(0, 790, 400, 300);
                        g.setColor(Color.BLACK);
                        g.setFont(Desc);
                        g.drawString("Для открытия подсказки нажмите 0", 10, 815);
                    }
                    gx.drawImage(buff, 0, 0, null);

                } else {if(!openimg) {

                    g.setFont(Title);
                    int count = 70;
                    int otstup = 20;
                    g.drawString(opened.title, otstup, count);
                    count += 30;
                    int ph_i = 0;
                    for (int i = 0; i < opened.img.size(); i++) {
                        Polygon w = new Polygon();
                        w.addPoint(otstup + ph_i * 85, count);
                        w.addPoint(otstup + ph_i * 85+75,count);
                        w.addPoint(otstup + ph_i * 85+75, count + 75);
                        w.addPoint(otstup + ph_i * 85,count + 75);
                        openi.add(w);
                        g.drawImage(opened.img.get(ph_i), otstup + ph_i * 85, count, 75, 75, null);
                        ph_i++;
                    }
                    count += 90;
                    g.setFont(Desc);
                    for (int o = 0; o < opened.desc_spl.size(); o++) {
                        g.drawString(opened.desc_spl.get(o), otstup, count);
                        count += 20;
                    }
                    if (opened.cat_name.length > 0) {
                        String cat = opened.cat_name[0];
                        for (int i = 1; i < opened.cat_name.length; i++) {
                            cat += ", " + opened.cat_name[i];
                        }
                        g.drawString("Категории объекта: " + cat, otstup, count);
                    }
                    opened.isDraw = true;
                    //      g.drawImage(test,500,500,null);
                    gx.drawImage(buff, 0, 0, null);
                } else {
                    g.drawImage(opened.img_full.get(opnimg), 100,100,null);
               gx.drawImage(buff,0,0,null);
                }}
            } else {
                Graphics gr = g;
                gr.setColor(Color.WHITE);
                gr.fillRect(0,0,9000,9000);
                gr.setColor(Color.BLACK);
                gr.setFont(Title);
                int x= 10;
                int y = 70;
                y= help.drstr(gr, x,y,"Назначения клавиш");
                y= help.drstr(gr, x,y,"V - показать контура объектов");
                y= help.drstr(gr, x,y,"B - скрыть контура");
                y= help.drstr(gr, x,y,"W или стрелка вверх - перемещение на север");
                y= help.drstr(gr, x,y,"S или стрелка вниз - перемещение на юг");
                y= help.drstr(gr, x,y,"A или стрелка влево - перемещение на запад");
                y= help.drstr(gr, x,y,"D или стрелка вправо - перемещение на восток");
                y= help.drstr(gr, x,y,"- или колёсико мыши вниз - отдаление");
                y= help.drstr(gr, x,y,"+ или колёсико мыши вверх - приближение");
                y= help.drstr(gr, x,y,"1 - карта OSM");
                y= help.drstr(gr, x,y,"2 - карта Wikimapia");
                gx.drawImage(buff,0,0,null);
            }
        // for (int i1 = 0; i1 < lengtharrayoftile; i1++) {
        //   // g.fillRect(p*256 - shiftX, i*256 - shiftY, 256,256);
        // try {
        //   // if (tileImageStatus[i1][p1]) {
        //   if ((Math.abs(xTilesNum[i1] - (x/256)) < 20) && ((Math.abs(yTilesNum[i1] - (y/256))) < 20)) {
        //      g.drawImage(tileImage[i1], xTilesNum[i1]*256 - x - shiftX + 512, yTilesNum[i1] * 256 - y - shiftY+512, null);
        //   }
        } catch (Exception ee) {
        }
         }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ArrayList<mapobject> poly = new ArrayList<>();
        ArrayList<mapobject> s = tiles[zoom-3].obj;
        int gtax, gtay, vrx,vry;
        for(int i = 0; i<s.size(); i++) {
            Polygon qwe = new Polygon();
            for (int a = 0; a < s.get(i).xx.size(); a++) {
                gtax = s.get(i).xx.get(a);
                gtay = s.get(i).yy.get(a);
                vrx = -x + gtax * 2 * 2 - shiftX + w / 2;
                vry =-y + gtay * 2 * 2 - shiftY + h / 2;
                qwe.addPoint(vrx, vry);
            }
            if(qwe.contains(e.getX(), (e.getY() + 25))) {
                poly.add(s.get(i));
            }

        }
        if(poly.size() > 0) {
        int size = poly.get(0).polygon_size;
        choosed = poly.get(0);
        for(int i = 1; i<poly.size(); i++) {
            int k = poly.get(i).polygon_size;
            if(k<size) {
                choosed = poly.get(i);
            }
        }
    }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
       int a = e.getWheelRotation();
       a = -a;
        if (e.isControlDown())
        {
            if (e.getWheelRotation() < 0)
            {

            }
            else
            {

            }
        }
       if(a>0){
           System.out.println("mouse wheel Up");
         zoomUp();
       } else if (a<0){
           System.out.println("mouse wheel Down");
          zoomDown();
       }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        seehelp = false;
        if(!opened.isOpened) {
            try {

                opened = null;
          //      opened.img_full.clear();
               // opened.img = new ArrayList<>();
                opened = choosed;
                opened.isDraw=false;
                int id = opened.id;
                String[] ans = API.getById(id);
                opened.title = ans[0];
                opened.desc = ans[1];
                opened.photos_big_url = ans[2].split(split_ph);
                opened.ph_time = ans[3].split(split_ph);
                opened.cat_name = ans[4].split(split_ph);
                opened.desc_spl = new ArrayList<>();
                int prv = opened.img.size();
                for(String v:opened.photos_big_url) {
                    try {
                        if(v.length()>7 && prv < 1) {
                            opened.img.add(ImageIO.read(new URL(v.replaceAll("big", "75")).openStream()));
                            opened.img_full.add(ImageIO.read(new URL(v).openStream()));
                        }
                    } catch(Exception ex) {

                    }
                }
                int length = opened.desc.length();
                int len = 0;
                if(length>190) {
                    String[] b = opened.desc.split(" ");
                    String returned = "";
                    for(String q:b) {
                        if((returned+" " + q).length()<160) {
                            returned += " " + q;
                        } else {
                            opened.desc_spl.add(returned);
                            returned = q;
                        }
                    }
                    opened.desc_spl.add(returned);
                    returned = "";
                } else {
                    opened.desc_spl.add(opened.desc);
                }

                opened.isOpened = true;
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        } else {
            boolean b = true;
            for(int i = 0; i<opened.img_full.size();i++) {
                if(openi.get(i).contains(e.getX(), e.getY())){
                    opnimg = i;
                    openimg = true;
                    b = false;
                }
            }
            if(b) {
                openimg = false;
                opened.isOpened = false;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    public void zoomUp() {
        if (zoom < 18) {
            tiles[zoom - 3].clear_obj();
            tiles[zoom - 3].ClearMemory(limit);
            zoom++;
            x = x * 2;
            y = y * 2;
            shiftX *= 2;
            shiftY *= 2;
        }
    }
    public void zoomDown() {
        if(zoom > 2) {
            tiles[zoom - 3].clear_obj();
            tiles[zoom - 3].ClearMemory(limit);
            zoom--;
            x = x / 2;
            y = y / 2;
            shiftX /= 2;
            shiftY /= 2;
        }
    }
    // public Boolean getNeedDownload(int x, int y) {
    //      Boolean c = true;
    //     for(int a = 0; a<lengtharrayoftile + 1; a++) {
    //       if(x == xTilesNum[a] && y == yTilesNum[a]) {
    ///            c = false;
    //        } else {
    //        }
    //    }
    //     return c;
    //}

}
