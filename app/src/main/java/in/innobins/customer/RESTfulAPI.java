package in.innobins.customer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sasuke on 18/10/15.
 */
public class RESTfulAPI {
    static InputStream is = null;
    static OutputStream os = null;
    static JSONObject jsonObject;
    String jsonstring;
    //Constructor
    public RESTfulAPI(){
    }
    //json data from webservices
    public JSONObject getJSONfromurl(String completeurl, String requesttype, String data){
        //http request
        String api = "https://inno-100.appspot.com/";
        String apiurl = api + completeurl;
        String search_s = null;
        try {
            URL url = new URL(apiurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (requesttype == "POST"){
                Log.d("POSTURL", apiurl);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setUseCaches(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(data);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } else if (requesttype == "GET"){
                Log.d("GETURL",apiurl);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(15000);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setUseCaches(true);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();
            }
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                is = new BufferedInputStream(urlConnection.getInputStream());
                if (is != null){
                    jsonstring = convertStreamToString(is);
                } else {
                    return null;
                }
            } else {
                return null;
            }
            urlConnection.disconnect();
        } catch (IOException e) {
            Log.d("ERROR SERVER CALL", e.toString());
            return  null;
        }
        try {
            Log.d("json", jsonstring);
            if (jsonstring != null){
                jsonObject = new JSONObject(jsonstring);
            } else {
                jsonObject = null;
            }
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
