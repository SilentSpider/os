package com.silentspider.silentspideros.wifi;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.*;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

    List networkList = new ArrayList<String>();
    ArrayAdapter<String> networkListAdapter = null;
    android.net.wifi.WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Helvetica-UltraCompressed.otf");
        TextView header = (TextView) view.findViewById(R.id.item_app_name);
        header.setTypeface(font);

        String[] values = new String[] { "Android" };
        Collections.addAll(networkList, values);
        networkListAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.wifi_item, networkList);


        ListView listView = (ListView) view.findViewById(R.id.wifiList);
        listView.setAdapter(networkListAdapter);

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

        Button b = (Button) view.findViewById(R.id.connectButton);
        b.setOnClickListener(this);

        CheckBox showPasswordBox = (CheckBox) view.findViewById(R.id.showPasswordCheckBox);
        EditText passwordEditBox = (EditText) view.findViewById(R.id.passwordTextEdit);
                showPasswordBox.setOnClickListener(new ShowPasswordBoxListener(passwordEditBox));

        // Inflate the layout for this fragment
        return view;
    }


    public void onClick(View view) {

        String ssid = ((ListView) view.findViewById(R.id.wifiList)).getSelectedItem().toString();
        String password = ((EditText) view.findViewById(R.id.passwordTextEdit)).getText().toString();

        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid +  "\"";
        wc.preSharedKey = "\"" + password + "\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);

    }

    private void addItem(String title) {
        networkList.add(title);
        networkListAdapter.notifyDataSetChanged();
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            wifiList = mainWifi.getScanResults();
            addItem("Found: " + wifiList.size() + " nets");
            for(int i = 0; i < wifiList.size(); i++){
                addItem((wifiList.get(i)).SSID);
            }
        }
    }

    class ShowPasswordBoxListener implements View.OnClickListener {
        EditText passwordEditBox;
        public ShowPasswordBoxListener(EditText passwordEditBox) {
            this.passwordEditBox = passwordEditBox;
        }
        @Override
        public void onClick(View v) {
            if(((CheckBox) v).isChecked()) {
                passwordEditBox.setTransformationMethod(null);
            }
            else {
                passwordEditBox.setTransformationMethod(new PasswordTransformationMethod());
            }
        }
    }


}