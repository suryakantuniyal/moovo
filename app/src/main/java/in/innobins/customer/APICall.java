package in.innobins.customer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sasuke on 12/6/15.
 */
public class APICall {
    static InputStream is = null;
    static JSONObject jsonObject;
    String jsonstring;
    //Constructor
    public APICall(){
    }
    //json data from webservices
    public JSONObject getJSONfromurl(String completeurl, String requesttype){
        //http request
        String api = "https://inno-100.appspot.com/";
        String apiurl = api + completeurl;
        try {
            URL url = new URL(apiurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (requesttype == "POST"){
                Log.d("POSTURL", apiurl);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(5000);
            } else if (requesttype == "GET"){
                Log.d("GETURL",apiurl);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
            }
            is = new BufferedInputStream(urlConnection.getInputStream());
            if (is != null) {
                jsonstring = convertStreamToString(is);

            }
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonstring);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //method to convert stream to string
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

