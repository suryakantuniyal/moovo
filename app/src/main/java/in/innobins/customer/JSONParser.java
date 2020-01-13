package in.innobins.customer;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public JSONParser() {
    }
    public String getJSONFromUrl(String url) {
        try {
            URL urlnew = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlnew.openConnection();
            urlConnection.setConnectTimeout(5000);
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                is = new BufferedInputStream(urlConnection.getInputStream());
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            if (is != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }
        return json;

    }
}

