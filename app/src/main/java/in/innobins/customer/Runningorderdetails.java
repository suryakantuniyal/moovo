package in.innobins.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sasuke on 19/10/15.
 */
public class Runningorderdetails extends Fragment {
    TextView Status, Orderid, Truck, From, To, Estimatedist, EstimateFare,Bookdatetime,Labour,Title;
    LinearLayout track_ll;
    ImageView Togglebutton;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View Rootview = inflater.inflate(R.layout.runningorderdetails,null);
        track_ll = (LinearLayout)Rootview.findViewById(R.id.track_order_ll);
        Title = (TextView)Rootview.findViewById(R.id.title);
        Title.setText("order details");
        Togglebutton = (ImageView)Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        track_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trackIntent = new Intent(getActivity(),TrackMoovo.class);
                getActivity().startActivity(trackIntent);
                getActivity().finish();
            }
        });
        Status = (TextView)Rootview.findViewById(R.id.status);
        Orderid = (TextView)Rootview.findViewById(R.id.orderid);
        Truck = (TextView)Rootview.findViewById(R.id.truck);
        From = (TextView)Rootview.findViewById(R.id.pickup);
        To = (TextView)Rootview.findViewById(R.id.dropoff);
        Estimatedist = (TextView)Rootview.findViewById(R.id.distance);
        EstimateFare = (TextView)Rootview.findViewById(R.id.fare);
        Bookdatetime = (TextView)Rootview.findViewById(R.id.datetime);
        Labour = (TextView)Rootview.findViewById(R.id.labour);
        Bundle bundle = getArguments();
        String data = bundle.getString("Bundlestring");
        try {
            JSONObject jsonObject = new JSONObject(data);
            Status.setText(bundle.getString("Status"));
            Orderid.setText(jsonObject.getString("orderId"));
            Estimatedist.setText(jsonObject.getString("estimatedDistance"));
            EstimateFare.setText("\u20B9 "+jsonObject.getString("estimatedFare"));
            Bookdatetime.setText(jsonObject.getString("dateTime"));
            From.setText(jsonObject.getString("pickUp"));
            To.setText(jsonObject.getString("dropOff"));
            Truck.setText(jsonObject.getString("vehicle"));
            Labour.setText(jsonObject.getString("extra"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DATA", data);
        return  Rootview;
    }
}
