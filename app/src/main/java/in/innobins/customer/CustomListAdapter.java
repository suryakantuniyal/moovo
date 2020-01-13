package in.innobins.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sasuke on 11/9/15.
 */
public class CustomListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<OrderItem> orderItems;

    public CustomListAdapter(Context context, ArrayList<OrderItem> morderItems) {
        mContext = context;
        orderItems = morderItems;
    }
    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItems.get(position);
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
            view = inflater.inflate(R.layout.customorderlist, null);
        }
        else {
            view = convertView;
        }
        TextView Date = (TextView) view.findViewById(R.id.datetime);
        TextView pickup = (TextView)view.findViewById(R.id.pickup);

        Date.setText( orderItems.get(position).mdatetime );
        pickup.setText(orderItems.get(position).mpickup);

        return view;
    }
}
