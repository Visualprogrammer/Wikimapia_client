import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class api {
    private static final int CONNECTION_TIMEOUT = 1500;
    private int key_use = 0;
    private String key = "";

    public api() throws IOException {
        get_key();
    }

    private void get_key() throws IOException {
        key = Do_request("http://f0321390.xsph.ru/return.php");
        key_use = 99;
        System.out.println(key);
    }

    private static String Do_request(String nav_url) throws IOException {
        URL url = new URL(nav_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(30000);
        con.setReadTimeout(30000);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private void check_key() throws IOException {
        if (key_use < 2) {
            get_key();
        }
    }

    public String getnearest(double x, double y, double z, String lng, int count, Boolean mercator) throws IOException {
        String opt;
        if (mercator) {
            opt = "&options=mercator";
        } else {
            opt = "";
        }
        String zapurl = "http://api.wikimapia.org/?function=box&key=" + key + opt + "&x=" + x + "&y=" + y + "&z=" + z + "&language=" + lng + "&count=" + count; //http://api.wikimapia.org/?function=box&lon_min=30&lat_min=60&lon_max=32.93493&lat_max=68.8599143&format=&pack=&language=ru&key=example
        String ans = Do_request(zapurl);
        return ans;
    }

    public static ArrayList<mapobject> Itile_get(int tile_x, int tile_y, int zoom) throws IOException {
        ArrayList<mapobject> n = new ArrayList<mapobject>();
        System.out.println(maths.Url_get_tile_obj(maths.TileXYToQuadKey(tile_x, tile_y, zoom)));
       String a = Do_request(maths.Url_get_tile_obj(maths.TileXYToQuadKey(tile_x, tile_y, zoom)));
        if (a.length() > 10) {
            String[] k = a.split(" lijlkbglfdjgldflgltfhgldrjgldrlhnmtdgnlrdlyjhltfkjogitpdljlitrptkjdyljoitrdkjoidptpklrdjptdkgrdojtpdkgpordjtodjopdjoitrpjgpodrogdjrpoijoidrjgldj9pugoirdpjgoijlrdjogirjoi ");
          // System.out.println(k[3]);
            n = getobjarray(k);
       }

        return n;
        }
        public static ArrayList<mapobject> getobjarray(String[] a) {
        ArrayList<mapobject> obj = new ArrayList<>();
            for(int i =2; i<a.length; i++ ) {
                String[] b = a[i].split(" /wmClqwdvmfnmcf//////flkmbldl kjdnlvvmlnkjlsdnc ");
                String g = "";
                for(int e =7; e<b.length;e++){
                    g=g+b[e] + "|";
                }
                String[] c = {b[0],b[2], b[4], g, b[1]};

                mapobject q = new mapobject(0, 0,0,Integer.parseInt(b[0]),new ArrayList<Integer>(), Integer.parseInt(c[4]));
                int z = a[0].split(" /wmClqwdvmfnmcf//////flkmbldl kjdnlvvmlnkjlsdnc ")[0].length();
                q.decode_Polygon(c[3],z);
                obj.add(q);
            }
            return obj;
        }

    }
//}
