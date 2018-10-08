package com.ryg.dynamicload.sample.mainhost;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = "main-host";
    private ArrayList mPluginItems = new ArrayList<PluginItem>();
    private PluginAdapter mPluginAdapter;
    private ListView mListView;
    private TextView mNoPluginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mPluginAdapter = new PluginAdapter(this);
        mListView = findViewById(R.id.plugin_list);
        mNoPluginTextView = findViewById(R.id.no_plugin);
    }

    private void initData() {
        String pluginFolder = Environment.getExternalStorageDirectory().toString() + "/DynamicLoadHost";
        File file = new File(pluginFolder);
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0) {
            mNoPluginTextView.setVisibility(View.VISIBLE);
            return;
        }

        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
            Log.d(TAG, "item.pluginPath: " + item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            if (item.packageInfo.services != null && item.packageInfo.services.length > 0) {
                item.launcherServiceName = item.packageInfo.services[0].name;
            }
            mPluginItems.add(item);
            DLPluginManager.getInstance(this).loadApk(item.pluginPath);
        }

        mListView.setAdapter(mPluginAdapter);
        mListView.setOnItemClickListener(this);
        mPluginAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PluginItem item = (PluginItem)mPluginItems.get(position);
        DLPluginManager pluginManager = DLPluginManager.getInstance(this);
        pluginManager.startPluginActivity(this, new DLIntent(item.packageInfo.packageName, item.launcherActivityName));

        //如果存在Service则调用起Service
        if (item.launcherServiceName != null) {
            //startService
            DLIntent intent = new DLIntent(item.packageInfo.packageName, item.launcherServiceName);
            //startService
            //	        pluginManager.startPluginService(this, intent);

            //bindService
            //	        pluginManager.bindPluginService(this, intent, mConnection = new ServiceConnection() {
            //                public void onServiceDisconnected(ComponentName name) {
            //                }
            //
            //                public void onServiceConnected(ComponentName name, IBinder binder) {
            //                    int sum = ((ITestServiceInterface)binder).sum(5, 5);
            //                    Log.e("MainActivity", "onServiceConnected sum(5 + 5) = " + sum);
            //                }
            //            }, Context.BIND_AUTO_CREATE);
        }

    }


    static class PluginItem {
        PackageInfo packageInfo;
        String pluginPath;
        String launcherActivityName;
        String launcherServiceName;
    }

    class PluginAdapter extends BaseAdapter{

        class ViewHolder {
            ImageView appIcon;
            TextView appName;
            TextView apkName;
            TextView packageName;
        }

        private LayoutInflater mInflater;

        public PluginAdapter(Context c){
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return mPluginItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mPluginItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent, false);
                holder = new ViewHolder();
                holder.appIcon = convertView.findViewById(R.id.app_icon);
                holder.appName = convertView.findViewById(R.id.app_name);
                holder.apkName = convertView.findViewById(R.id.apk_name);
                holder.packageName = convertView.findViewById(R.id.package_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            PluginItem item = (PluginItem)mPluginItems.get(position);
            PackageInfo packageInfo = item.packageInfo;
            holder.appIcon.setImageDrawable(DLUtils.getAppIcon(MainActivity.this, item.pluginPath));
            holder.appName.setText(DLUtils.getAppLabel(MainActivity.this, item.pluginPath));
            holder.apkName.setText(item.pluginPath.substring(item.pluginPath.lastIndexOf(File.separatorChar) + 1));
            holder.packageName.setText(packageInfo.applicationInfo.packageName + "\n" +
                    item.launcherActivityName + "\n" +
                    item.launcherServiceName);
            return convertView;
        }
    }

}
