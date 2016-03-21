package com.silentspider.silentspideros;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadApps();
        loadListView();
        addClickListener();
    }

    public class AppDetail {
        CharSequence label;
        CharSequence name;
    }

    private PackageManager manager;
    private List<AppDetail> apps;
    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            apps.add(app);
        }
    }

    private ListView list;
    private void loadListView(){
        list = (ListView)findViewById(R.id.listView);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.app_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.app_item, null);
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
                MainActivity.this.startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFullScreen(true);
    }

    private void setFullScreen(final boolean fullscreen) {
        // Hide/show the system bar
        Window window = getWindow();
        window.setFlags(fullscreen ?
                        WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (fullscreen) {
            // Keep the system bar hidden in full screen mode
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
                        getWindow().setFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                    else {
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 0x4);
                    }
                }
            });
        }
        else {
            window.getDecorView().setOnSystemUiVisibilityChangeListener(null);
        }
        window.getDecorView().setSystemUiVisibility(
                fullscreen ? (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 0x4) : 0);
    }
}
