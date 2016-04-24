package com.silentspider.silentspideros.wifi;

import android.app.Activity;
import android.app.Fragment;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.net.wifi.*;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.silentspider.silentspideros.R;
import com.silentspider.silentspideros.TabsFragment;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by johan on 29/03/16.
 */
public class WifiFragment extends Fragment implements View.OnClickListener {

    List networkList = new ArrayList<String>();
    ArrayAdapter<String> networkListAdapter = null;
    android.net.wifi.WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    WifiManager wifiManager;


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiverWifi);
        networkStatusTimer.cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaCE-CondBold.otf");
        TextView header = (TextView) view.findViewById(R.id.selectTitle);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.scanStatus);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiPasswordTitle);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.passwordTextEdit);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.showPasswordCheckBox);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.connectButton);
        header.setTypeface(font);

        header = (TextView) view.findViewById(R.id.wifiStatusTitle);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiStatus);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiNetwork);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiSignalStrength);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiLinkSpeed);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.wifiIpAddress);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.nextButton);
        header.setTypeface(font);

        String[] values = new String[] { "Scan initiated..." };
        Collections.addAll(networkList, values);
        networkListAdapter = new WifiArrayAdapter(getActivity(),
                R.layout.wifi_item, R.id.wifi_net_title, networkList, this);


        LinearLayout selectNetwork = (LinearLayout) view.findViewById(R.id.selectNetwork);
        startBlinkAnimation(selectNetwork);

        ListView listView = (ListView) view.findViewById(R.id.wifiList);

        listView.setAdapter(networkListAdapter);

        // Initiate wifi service manager
        mainWifi = (android.net.wifi.WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);


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

        Button b = (Button) view.findViewById(R.id.connectButton);
        b.setOnClickListener(this);
        b = (Button) view.findViewById(R.id.nextButton);
        b.setOnClickListener(this);

        CheckBox showPasswordBox = (CheckBox) view.findViewById(R.id.showPasswordCheckBox);
        EditText passwordEditBox = (EditText) view.findViewById(R.id.passwordTextEdit);
                showPasswordBox.setOnClickListener(new ShowPasswordBoxListener(passwordEditBox));

        if(networkStatusTimer != null) {
            networkStatusTimer.cancel();
        }
        networkStatusTimer = new Timer();
        networkStatusTimer.schedule(new NetworkStatusTask(wifiManager), 0, 500);

        // Inflate the layout for this fragment
        return view;
    }


    private Timer animationTimer;
    private View animationView;

    public void startBlinkAnimation(View view) {
        animationView = view;
        animationTimer = new Timer();
        animationTimer.schedule(new GlowingOutlineTask(view), 0, 1500);
    }

    public void stopBlinkAnimation() {
        animationTimer.cancel();
        animationView.setBackgroundResource(R.drawable.standard);
    }

    public class GlowingOutlineTask extends TimerTask {

        private View view;
        public GlowingOutlineTask(View view) {
            this.view = view;
        }

        int i = 0;
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                TransitionDrawable transition = (TransitionDrawable) view.getBackground();

                @Override
                public void run() {
                    i++;
                    if (i % 2 == 0) { //
                        transition.startTransition(500);
                    } else {
                        transition.reverseTransition(500);
                    }

                }
            });
        }
    }

    private Timer networkStatusTimer;

    public void onClick(View view) {

        // Handle simple next button
        if(view.getId() == R.id.nextButton) {
            TabHost host = (TabHost) getActivity().findViewById(R.id.tabs_fragment);
            host.setCurrentTab(1);
            return;
        }

        ListView listView = ((ListView) getActivity().findViewById(R.id.wifiList));

        int idx = ((WifiArrayAdapter) listView.getAdapter()).getSelectedIndex();
        String ssid = listView.getAdapter().getItem(idx).toString();

        String password = ((EditText) getActivity().findViewById(R.id.passwordTextEdit)).getText().toString();

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

        wifiManager.disconnect();
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);
        wifiManager.reconnect();

        stopBlinkAnimation();
        LinearLayout statusNetwork = (LinearLayout) getActivity().findViewById(R.id.statusNetwork);
        startBlinkAnimation(statusNetwork);
    }

    public class NetworkStatusTask extends TimerTask {

        private WifiManager wifiManager;
        public NetworkStatusTask(WifiManager wifiManager) {
            this.wifiManager = wifiManager;
        }

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifiManager.getConnectionInfo();
                    if(info != null) {
                        int i = info.getIpAddress();
                        String ipAddress = "N/A";
                        Activity activity = getActivity();

                        if(i != 0) {
                            ipAddress = getIpAddressFromInt(info.getIpAddress());
                            activity.findViewById(R.id.nextButton).setVisibility(View.VISIBLE);
                        }
                        else {
                            activity.findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
                        }

                        ((TextView)activity.findViewById(R.id.wifiStatus)).setText("STATUS: " + info.getSupplicantState());
                        ((TextView)activity.findViewById(R.id.wifiNetwork)).setText("NETWORK: " + info.getSSID().substring(1, info.getSSID().length() - 1));
                        ((TextView)activity.findViewById(R.id.wifiSignalStrength)).setText("STRENGTH: " + (100 + info.getRssi()) + "%");
                        ((TextView)activity.findViewById(R.id.wifiLinkSpeed)).setText("SPEED: " + info.getLinkSpeed() + "Mbit");
                        ((TextView)activity.findViewById(R.id.wifiIpAddress)).setText("IP: " + ipAddress);
                    }
                }
            });
        }
    }


    private String getIpAddressFromInt(int ipAddress) {

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    private void addItem(String title) {
        networkList.add(title);
        networkListAdapter.notifyDataSetChanged();
    }

    public void networkSelected(int index) {
        // view is the row view returned by getView
        // The position is stored as tag, so it can be retrieved using getTag ()

        CharSequence text = "Selected network " + networkList.get(index) ;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), text, duration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                duration, duration);
        toast.show();

        stopBlinkAnimation();
        LinearLayout selectNetwork = (LinearLayout) getActivity().findViewById(R.id.connectNetwork);
        startBlinkAnimation(selectNetwork);
        getActivity().findViewById(R.id.passwordTextEdit).requestFocus();
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

            final String action = intent.getAction();
            Log.d("WifiReceiver", "WifiReceiver got action " + action);

            wifiList = mainWifi.getScanResults();
            networkList.clear();
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