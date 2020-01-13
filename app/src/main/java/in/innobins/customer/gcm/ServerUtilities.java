package in.innobins.customer.gcm;

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
 * Created by Abhishek on 10/9/2015.
 */
public class ServerUtilities {
    public static final String BASE_SERVER_URL = "https://inno-100.appspot.com/api/customer";


    public static final String KEY_TOKEN_SENT_TO_APP = "tokenSentToApp";

//    Modify the function to below according to the conventions of the project we are working on
//
    public static JSONObject getJSONFromUrl(String completeurl){
        InputStream is = null;
        JSONObject jsonObject=null;
        String jsonstring="";
        try {
            URL url = new URL(completeurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(15000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            is = new BufferedInputStream(urlConnection.getInputStream());
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            if(s.hasNext()){
                jsonstring= s.next();
            }
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            Log.d("error", "error in getjsonfromurl MalformedUrlexception");
        } catch (IOException e) {
            Log.d("error", "error in getjsonfromurl Ioexception");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            jsonObject = new JSONObject(jsonstring);
        } catch (JSONException e) {
            Log.d("error", "Json exception");
        }
        return jsonObject;
    }





}
