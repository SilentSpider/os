package com.silentspider.silentspideros.apps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.silentspider.silentspideros.MainActivity;
import com.silentspider.silentspideros.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johan on 29/03/16.
 */
public class AppManager {

    private PackageManager manager;
    private List<AppManager.AppDetail> apps;
    private Activity mainActivity;
    private View view;

    public AppManager(Activity mainActivity, View view) {
        this.mainActivity = mainActivity;
        this.view = view;
        loadApps();
        loadListView();
        addClickListener();
    }

    public class AppDetail {
        CharSequence label;
        CharSequence name;
    }

    private void loadApps(){
        manager = mainActivity.getPackageManager();
        apps = new ArrayList<AppManager.AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            AppManager.AppDetail app = new AppManager.AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            apps.add(app);
        }
    }

    private ListView list;
    private void loadListView(){
        list = (ListView) view.findViewById(R.id.appList);

        ArrayAdapter<AppManager.AppDetail> adapter = new ArrayAdapter<AppManager.AppDetail>(mainActivity,
                R.layout.app_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = mainActivity.getLayoutInflater().inflate(R.layout.app_item, null);
                }
                TextView appLabel = (TextView)convertView.findViewById(R.id.app_label);
                appLabel.setText(apps.get(position).label);
                TextView appName = (TextView)convertView.findViewById(R.id.app_name);
                appName.setText(apps.get(position).name);
                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                mainActivity.startActivity(i);
            }
        });
    }

}
