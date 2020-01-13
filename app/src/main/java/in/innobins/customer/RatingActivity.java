package in.innobins.customer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RatingActivity extends ActionBarActivity {
    TextView Orderid, Datetime, Distance, Price, Loadingtime, Unloadingtime, Waitingtime;
    Button Submit;
    RatingBar Rating;
    JSONArray jsonArray;
    Float rateStr;
    String orderId,driverId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newfeedbackandrating);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MOOVO", Context.MODE_PRIVATE);

       /* Orderid = (TextView)findViewById(R.id.orderid);
        Datetime = (TextView)findViewById(R.id.datetime);
        Distance = (TextView)findViewById(R.id.distance);
        Price = (TextView)findViewById(R.id.price);
        Loadingtime = (TextView)findViewById(R.id.lodingtime);
        Unloadingtime = (TextView)findViewById(R.id.unloadingtime);
        Waitingtime = (TextView)findViewById(R.id.waitingtime);*/
        Submit = (Button)findViewById(R.id.ratesubmit);
        Rating = (RatingBar)findViewById(R.id.rating);

        orderId = PreferenceManager.getDefaultSharedPreferences(RatingActivity.this).getString(SharedPrefUtil.KEY_ORDER_ID, "");
        driverId = PreferenceManager.getDefaultSharedPreferences(RatingActivity.this).getString(SharedPrefUtil.KEY_DRIVER_ID, "");
        Log.d("shrdorder",driverId);

        Rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                final int numStars = ratingBar.getNumStars();
                rateStr =ratingBar.getRating() ;
                final float ratingBarStepSize = ratingBar.getStepSize();
                Log.d("starval", String.valueOf(rateStr));
              //  doRating(v);
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              new rating().execute();
            }
        });
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
/*
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra("Details"));
            Orderid.setText("OrderID : "+jsonArray.getJSONObject(0).getString("orderId"));
            Datetime.setText(jsonArray.getJSONObject(0).getString("dateTime"));
            Distance.setText("Total Distance : "+jsonArray.getJSONObject(0).getString("finalDistance")+" K.M.");
            Price.setText("\u20B9 "+jsonArray.getJSONObject(0).getString("finalFare"));
            Loadingtime.setText(jsonArray.getJSONObject(0).getString("loadingTime")+" Minutes");
            Unloadingtime.setText(jsonArray.getJSONObject(0).getString("unloadingTime")+" Minutes");
            Waitingtime.setText(jsonArray.getJSONObject(0).getString("waitingTime")+" Minutes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
    }



    protected void doRating(float rating) {
        // setIsIndiacator(false) is used to allow changes/touch on the stars
        // Make sure you call this before OnRatingBarChangeListener has been
        // called to change to rating
        // rb_oneTouch.setIsIndicator(false);
        // using Math.celi(double) to set
        // the highest value to the
        // stars i.e if rating is
        // between 3 - 4 stars, then it
        // displays as 4 stars
        int _rating = (int) Math.ceil(rating);
        Rating.setRating(_rating);
        Float ratin = Float.valueOf(_rating);
        Log.d("rateval", String.valueOf(ratin));
        // setIsIndiacator(true) it won't allow to changes/touch on the stars
//        Rating.setIsIndicator(true);

    }
    public class rating extends AsyncTask<String,Void,Void>{
        JSONObject jsonResult;

        String rateurl=null;
        @Override
        protected Void doInBackground(String... strings) {
            RESTfulAPI resTfulAPI = new RESTfulAPI();
            JSONObject rate = new JSONObject();
            try {
                rate.put("orderId",orderId);
                rate.put("driverId",driverId);
                rate.put("rating",rateStr);

            }catch (JSONException e){
                e.printStackTrace();
            }
            jsonResult = resTfulAPI.getJSONfromurl("api/customer/rating/","POST", rate.toString());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (jsonResult!=null){
                try {

                    if (jsonResult.getJSONObject("datasets").getInt("confirmation")==1){
                        Intent intent = new Intent(RatingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
