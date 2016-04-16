package com.silentspider.silentspideros.wifi;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silentspider.silentspideros.R;

import java.util.List;

/**
 * Created by johan on 16/04/16.
 */
public class WifiArrayAdapter extends ArrayAdapter<String> {

    public WifiArrayAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/HelveticaCE-CondBold.otf");
        TextView header = (TextView) view.findViewById(R.id.wifi_net_title);
        header.setTypeface(font);
        return view;
    }
}
