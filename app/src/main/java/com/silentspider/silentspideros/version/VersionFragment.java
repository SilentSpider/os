package com.silentspider.silentspideros.version;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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
import com.silentspider.silentspideros.apps.AppManager;
import com.silentspider.silentspideros.wifi.WifiArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by johan on 29/03/16.
 */
public class VersionFragment extends Fragment {

    List networkList = new ArrayList<String>();
    ArrayAdapter<String> networkListAdapter = null;
    List<ScanResult> wifiList;
    private AppManager appManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_version, container, false);

        appManager = new AppManager(getActivity(), view);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaCE-CondBold.otf");
        TextView header = (TextView) view.findViewById(R.id.selectTitle);
        header.setTypeface(font);

        header = (TextView) view.findViewById(R.id.updateValidation);
        header.setTypeface(font);

        header.setText("Verified: " + UpdateVerifier.verifyUpdate(getActivity()));

        return view;
    }

}