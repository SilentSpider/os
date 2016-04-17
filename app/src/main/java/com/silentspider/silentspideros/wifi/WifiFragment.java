package com.silentspider.silentspideros.wifi;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.net.wifi.*;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.silentspider.silentspideros.R;

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


        String[] values = new String[] { "Android" };
        Collections.addAll(networkList, values);
        networkListAdapter = new WifiArrayAdapter(getActivity(),
                R.layout.wifi_item, R.id.wifi_net_title, networkList, this);


        LinearLayout selectNetwork = (LinearLayout) view.findViewById(R.id.selectNetwork);
        startBlinkAnimation(selectNetwork);

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


    private Timer animationTimer;
    private View animationView;

    public void startBlinkAnimation(View view) {
        animationView = view;
        animationTimer = new Timer();
        animationTimer.schedule(new MyTimerTask(view), 0, 1500);
    }

    public void stopBlinkAnimation() {
        animationTimer.cancel();
        TransitionDrawable transition = (TransitionDrawable) animationView.getBackground();
        transition.reverseTransition(500);
    }

    public class MyTimerTask extends TimerTask {

        private View view;
        public MyTimerTask(View view) {
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
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
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