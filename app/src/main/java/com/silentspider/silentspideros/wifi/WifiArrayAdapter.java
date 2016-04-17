package com.silentspider.silentspideros.wifi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.silentspider.silentspideros.R;

import java.util.List;

/**
 * Created by johan on 16/04/16.
 */
public class WifiArrayAdapter extends ArrayAdapter<String> implements View.OnClickListener {

    private WifiFragment fragment;
    private int selectedIndex = -1;

    public WifiArrayAdapter(Context context, int resource, int textViewResourceId, List objects, WifiFragment fragment) {
        super(context, resource, textViewResourceId, objects);
        this.fragment = fragment;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/HelveticaCE-CondBold.otf");
        TextView header = (TextView) view.findViewById(R.id.wifi_net_title);
        header.setTypeface(font);
        view.setTag(position);
        view.setOnClickListener(this);

        if(position == selectedIndex) {
            view.setBackgroundResource(R.drawable.listitem_selected);
        }
        else {
            view.setBackgroundResource(R.drawable.listitem);
        }

        return view;
    }

    @Override
    public void onClick ( View view ) {
        selectedIndex = Integer.parseInt(view.getTag().toString());
        fragment.networkSelected(selectedIndex);
        view.setSelected(true);
        notifyDataSetChanged();
    }

}
