package in.innobins.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sasuke on 22/9/15.
 */
public class PlacepredictionAdapter extends BaseAdapter{
    ArrayList<String> mArrayList;
    Context mContext;
    public PlacepredictionAdapter(Context context, ArrayList<String>arrayList){
        mContext = context;
        mArrayList = arrayList;
    }
    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.placepredictions, null);
        }
        else {
            view = convertView;
        }
        TextView textView = (TextView)view.findViewById(R.id.autocomplete_places_prediction);
        textView.setText(mArrayList.get(position));
        return view;
    }
}
