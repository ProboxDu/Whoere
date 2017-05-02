package cn.proboxdu.whoere;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ViewFlipper mViewFlipper ;
    private BottomNavigationView navigation;
    private  EditText editMessage;
    private final int mMaxAccountNum = 100;
    private AccountUtils Account = new AccountUtils("","","","","","","");
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    init();
                    mViewFlipper.setDisplayedChild(0);
                    getAccountInformationList();
                    return true;
                case R.id.navigation_messages:
                    mViewFlipper.setDisplayedChild(1);
                    getMessageList();
                   // navigation.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_users:
                    mViewFlipper.setDisplayedChild(2);
                    getAccountList();
                    return true;
            }
            return false;
        }
    };
    private Button.OnClickListener mOnClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mMessage = editMessage.getText().toString();
            if (mMessage.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "Don't allow empty messages to be sent!", Toast.LENGTH_LONG).show();
                return;
            }
            if (Client.SendMessage(Account,mMessage))
            {
                //navigation.setVisibility(View.VISIBLE);
                getMessageList();
                //Toast.makeText(getApplicationContext(), "The message has been sent.", Toast.LENGTH_LONG).show();
            }
        }
    };

    private EditText.OnFocusChangeListener mOnFocusChangeListener
            = new View.OnFocusChangeListener(){

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
            {
                //navigation.setVisibility(View.GONE);
            }else
            {
                //navigation.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Account.setUsername(getIntent().getStringExtra("name"));
        Account.setPassword(getIntent().getStringExtra("password"));
        Account.setLastMessageId(0);

        init();
        final SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(findViewById(R.id.container), this);
        watcher.addSoftKeyboardStateListener(
                new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
                    @Override
                    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                        //处理一些键盘打开的事情
                        navigation.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSoftKeyboardClosed() {
                        //处理一些键盘关闭的事情
                        navigation.setVisibility(View.VISIBLE);
                    }
                }
        );
        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        mViewFlipper.setDisplayedChild(0);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Button send_message = (Button) findViewById(R.id.send_message);
        send_message.setOnClickListener(mOnClickListener);
        editMessage = (EditText) findViewById(R.id.editText);;
        editMessage.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    private void init() {
        Account.setChannel("1");
        Account.setOptions("stud");
        getGpsAddress();
        Account.setIp(getIpAddress());
        if (!Client.UpdateAccount(Account)) {
            Toast.makeText(getApplicationContext(), "Update failed.", Toast.LENGTH_LONG).show();
        }
    }
    private void getMessageList() {
        String[] mListTitle = new String[mMaxAccountNum];
        String[] mListStr = new String[mMaxAccountNum];
        String[] accountlist = Client.RecieveMessage(Account);
        int len = Integer.parseInt(accountlist[0])-Account.getLastMessageId();
        //Account.setLastMessageId(Integer.parseInt(accountlist[0]));
        for (int i=0;i<len;i++)
        {
            mListTitle[i]=accountlist[2*i+1];
            mListStr[i]=accountlist[2*i+2];
        }
        ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
        for (int i=0;i<len;i++)
        {
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("title", mListTitle[i]);
            item.put("text", mListStr[i]);
            mData.add(item);
        }
        ListView listView =(ListView)findViewById(R.id.messagelist);
        SimpleAdapter adapter =new SimpleAdapter(this,mData,android.R.layout.simple_list_item_2,
                new String[]{"title","text"},new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
        //ListView listView =(ListView)findViewById(R.id.messagelist);
        //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Client.RecieveMessage(Account)));
    }

    private void getAccountInformationList() {
        ListView listView =(ListView)findViewById(R.id.infolist);
        SimpleAdapter adapter =new SimpleAdapter(this,Account.toList(),android.R.layout.simple_list_item_2,
                new String[]{"title","text"},new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
        //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Account.toList()));
    }

    private void getAccountList()
    {
        String[] mListTitle = new String[mMaxAccountNum];
        String[] mListStr = new String[mMaxAccountNum];
        String[] accountlist = Client.getAccountList(Account);
        int len = Integer.parseInt(accountlist[0]);
        for (int i=1;i<=len;i++)
        {
            String tmp[] = accountlist[i].split("#");
            mListTitle[i-1]=tmp[0];//AccountName
            mListStr[i-1]=tmp[5];//AccountIp
        }
        ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();
        for (int i=0;i<len;i++)
        {
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("title", mListTitle[i]);
            item.put("text", mListStr[i]);
            mData.add(item);
        }
        ListView listView =(ListView)findViewById(R.id.userlist);
        SimpleAdapter adapter =new SimpleAdapter(this,mData,android.R.layout.simple_list_item_2,
                new String[]{"title","text"},new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
        //ListView listView =(ListView)findViewById(R.id.userlist);
        //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Client.getAccountList(Account)));
    }

    private void getGpsAddress() {
        double latitude = 0.0;
        double longitude = 0.0;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                    latitude = location.getLatitude(); //经度
                    longitude = location.getLongitude(); //纬度
                }
            }catch (SecurityException e)
            {
                Log.getStackTraceString(e);
            }
        } else {
            LocationListener locationListener = new LocationListener() {

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {
                }
                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {
                }
                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                    latitude = location.getLatitude(); //经度
                    longitude = location.getLongitude(); //纬度
                }
            }catch (SecurityException e)
            {
                Log.getStackTraceString(e);
            }
        }
        Account.setGpsx(""+latitude);
        Account.setGpsy(""+longitude);
    }

    public String getIpAddress() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            return (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    ((ipAddress >> 24) & 0xFF);
        } else {
            try {
                NetworkInterface networkInterface;
                InetAddress inetAddress;
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    networkInterface = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
                return null;
            } catch (SocketException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
