package com.silentspider.silentspideros.wifi;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.silentspider.silentspideros.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by johan on 29/03/16.
 */
public class WifiFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Helvetica-UltraCompressed.otf");
        TextView header = (TextView) view.findViewById(R.id.item_app_name);
        header.setTypeface(font);

        String[] values = new String[] { "Android" };
        Collections.addAll(liste, values);
        adapter2 = new ArrayAdapter<String>(getActivity(),
                R.layout.wifi_item, liste);


        ListView listView = (ListView) view.findViewById(R.id.wifiList);
        listView.setAdapter(adapter2);

        // Initiate wifi service manager
        mainWifi = (android.net.wifi.WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getActivity().getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        receiverWifi = new WifiReceiver();

        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        getActivity().registerReceiver(receiverWifi, new IntentFilter(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        addItem("Starting scan");

        Button b = (Button) view.findViewById(R.id.addButton);
        b.setOnClickListener(this);


        // Inflate the layout for this fragment
        return view;
    }

    List liste = new ArrayList<String>();
    ArrayAdapter<String> adapter2 = null;
    android.net.wifi.WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    public void onClick(View view) {
        addItem("Button");
    }

    private void addItem(String title) {
        liste.add(title);
        adapter2.notifyDataSetChanged();
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            wifiList = mainWifi.getScanResults();
            addItem("Found: " + wifiList.size() + " nets");
            for(int i = 0; i < wifiList.size(); i++){
                addItem((wifiList.get(i)).toString());
            }
        }

    }

}