import java.util.ArrayList;

public class maths {
    public static int max_width_heigth(int zoom) {
        return (int)(Math.pow(2,8+zoom));
    }
    private final double EarthRadius = 6378137;
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    public static String TileXYToQuadKey(int tileX, int tileY, int zoom)
    {int x = (int)tileX/2/2/2;
    int y = (int)tileY/2/2/2;
    int z =zoom - 3;
        String n;
        int t,i,e;
        double a =  (Math.log(256)) /  0.6931471805599453;//Math.log(2);
        t = x;
        e=y;
        i=z;
        Integer[][] u = {{-2, 1},{0, 2},{2, 3}};
        Integer o[] = u[(int) (a - 8)];
        n = "0";
        int s;
        t = Math.round(t);
        e = Math.round((1 << i - o[0]) - e - 1);
        i -= o[1];
        while (i >= 0) {
            s = 1 << i;
            n += ((t & s) > 0 ? 1 : 0) + ((e & s) > 0 ? 2 : 0);
            i--;
        }
      //  System.out.println(QuadKeyToTileXY(n.toString()));
        return n;
    }
    public static String Url_get_tile_obj(String tileQuad){
//http://wikimapia.org/z1/itiles/030/230/303/122/121.xy
        String url = "http://f0321390.xsph.ru/xy.php?url=wikimapia.org/z1/itiles/";
        int a = 0;
      //  tileQuad.replace(/(\d{3})(?!$)/g, "$1/"),;
        for(int i=0;i<tileQuad.length(); i++) {
            url += tileQuad.charAt(i);
            if((a >= 2)&&(i>1)) {
                url+="/";
                a = 0;
            } else {
                a++;
            }
        }
        return url + ".xy";
    }
    public static String QuadKeyToTileXY(String quadKey)
    {int tileX;
    int tileY;
    int levelOfDetail;
        tileX = tileY = 0;
        levelOfDetail = quadKey.length();
        for (int i = levelOfDetail; i > 0; i--)
        {
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i))
            {
                case '0':
                    break;

                case '1':
                    tileX |= mask;
                    break;

                case '2':
                    tileY |= mask;
                    break;

                case '3':
                    tileX |= mask;
                    tileY |= mask;
                    break;

                default:

            }
        }
        return tileY + " " + tileX;
    }
    public static mapobject decode_Polygon(String la1, mapobject mapobj, int zoom) {
        int e = la1.length();
        int i = 0;
        int o = 0;
        int n = 0;
        double s=0;
        //       r = Wikimapia.LatLng;
        while (i < e) {
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
         //   System.out.println((o / s) + " " +(n / s));
            mapobj.add_xy(LatLongToPixelX(o/s, n/s, zoom), LatLongToPixelY(o/s,n/s, zoom));
        }
     //   System.out.println("g");
        return mapobj;
    }
    private static double Clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    private static long MapSize(int levelOfDetail)
    {
        return (long) 256 << levelOfDetail;
    }

    public static int LatLongToPixelX(double latitude, double longitude, int levelOfDetail)
    {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        longitude = Clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        long mapSize = MapSize(levelOfDetail);
        return (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
    }
    public static int LatLongToPixelY(double latitude, double longitude, int levelOfDetail)
    {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        longitude = Clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        long mapSize = MapSize(levelOfDetail);
        return (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
    }

}
