package in.innobins.customer;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class Route {
    GoogleMap mMap;
    Context context;
    ArrayList<LatLng> startEndPoints;
    ArrayList<String> startEndPlace;
    String result;

    public boolean drawRoute(GoogleMap map, Context c, ArrayList<LatLng> points, ArrayList<String> place, boolean withIndications, boolean optimize, String resultjson)
    {
        mMap = map;
        context = c;
        startEndPoints = points;
        startEndPlace = place;
        result = resultjson;
        try {
            drawPath(result, withIndications);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

//    <!--android:theme="@android:style/Theme.Holo.Light"-->

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    private void drawPath(String  result, boolean withSteps) {

        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(8)
                        .color(Color.rgb(89, 129, 244)).geodesic(true));
            }

            // Add markers
            mMap.addMarker(new MarkerOptions()
                    .position(startEndPoints.get(0))
                    .title(startEndPlace.get(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            mMap.addMarker(new MarkerOptions()
                    .position(startEndPoints.get(1))
                    .title(startEndPlace.get(1))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startEndPoints.get(0));
            builder.include(startEndPoints.get(1));
            LatLngBounds bounds = builder.build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,100);
            mMap.moveCamera(cameraUpdate);
        }
        catch (JSONException e) {

        }
    }

    /**
     * Class that represent every step of the directions. It store distance, location and instructions
     */
    private class Step
    {
        public String distance;
        public LatLng location;
        public String instructions;

        Step(JSONObject stepJSON)
        {
            JSONObject startLocation;
            try {

                distance = stepJSON.getJSONObject("distance").getString("text");
                startLocation = stepJSON.getJSONObject("start_location");
                location = new LatLng(startLocation.getDouble("lat"),startLocation.getDouble("lng"));
                try {
                    instructions = URLDecoder.decode(Html.fromHtml(stepJSON.getString("html_instructions")).toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                };

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}