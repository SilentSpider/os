package com.silentspider.silentspideros.chat;

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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.silentspider.silentspideros.R;
import com.silentspider.silentspideros.wifi.WifiArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by johan on 29/03/16.
 */
public class ChatFragment extends Fragment {

    List networkList = new ArrayList<String>();
    ArrayAdapter<String> networkListAdapter = null;
    List<ScanResult> wifiList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaCE-CondBold.otf");
        TextView header = (TextView) view.findViewById(R.id.selectTitle);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.mobileTitle);
        header.setTypeface(font);
        header = (TextView) view.findViewById(R.id.desktopTitle);
        header.setTypeface(font);

        WebView browser = (WebView) view.findViewById(R.id.webView);
        browser.clearCache(true);
        browser.clearHistory();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        browser.setWebViewClient(new LocalWebViewClient());
        browser.loadUrl("http://127.0.0.1:3000/");

        // Inflate the layout for this fragment
        return view;
    }

    private class LocalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}