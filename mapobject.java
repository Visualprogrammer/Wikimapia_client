import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class mapobject {
    ArrayList<Integer> xx = new ArrayList<>();
    ArrayList<Integer> yy = new ArrayList<>();
    double x;
    double y;
    int zoom;
    int id;
    int polygon_size;
    String title;
    String[] photos_big_url;
    String[] ph_time;
    String[] cat_name;
    String desc;
    Boolean isOpened;
    ArrayList<BufferedImage> img= new ArrayList<>();
    ArrayList<Integer> category = new ArrayList<Integer>();
 public mapobject(int z, double xz, double yz, int i, ArrayList<Integer> cat, int polygon_s) {
        zoom = z;
        x = xz;
        y = yz;
        id = i;
        category = cat;
        polygon_size = polygon_s;
 }
 public void add_xy(double x, double y) {
     xx.add((int)x);
     yy.add((int)y);
 }
    public void decode_Polygon(String la1, int zoom) {
     try {
         int e = la1.length();
         int i = 0;
         int o = 0;
         int n = 0;
         double s=0;
         //       r = Wikimapia.LatLng;
         while (i < e-1) {
             int p, l = 0, c = 0;
             do {
                 p = la1.charAt(i++) - 63;
                 c |= (p & 31) << l;
                 l += 5;
             } while (p >= 32);
             n += ((c & 1) != 0) ? ~(c >> 1) : c >> 1;
             if (n > 180 * 1e6 || n < -(180 * 1e6)) {
                 // s = true;
             }
             l = 0;
             c = 0;
             do {
                 p = la1.charAt(i++) - 63;
                 c |= (p & 31) << l;
                 l += 5;
             } while (p >= 32);
             o += ((c & 1) != 0) ? ~(c >> 1) : c >> 1;
             s = o > 90 * 1e6 || o < -(90 * 1e6) ? 1e7 : 1e6;
            // System.out.println((o / s) + " " +(n / s));
             this.add_xy(maths.LatLongToPixelX(o/s, n/s, zoom), maths.LatLongToPixelY(o/s,n/s, zoom));
         }
       //  System.out.println("g");
     } catch (Exception ex) {
         System.out.println(ex.getMessage());
     }

    }
}
