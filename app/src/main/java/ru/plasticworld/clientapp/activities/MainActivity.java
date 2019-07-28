package ru.plasticworld.clientapp.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.squareup.otto.Produce;
import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.HeartRateNotifyListener;
import com.zhaoxiaodan.miband.listeners.NotifyListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import ru.plasticworld.clientapp.App;
import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.data.MeasurementsAdapter;
import ru.plasticworld.clientapp.data.remote.MeasurementServer;
import ru.plasticworld.clientapp.fragments.DeviceFragment;
import ru.plasticworld.clientapp.fragments.HomeFragment;
import ru.plasticworld.clientapp.fragments.ParamFragment;
import ru.plasticworld.clientapp.logic.ConnectedThread;
import ru.plasticworld.clientapp.logic.MyHandler;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private static final MeasurementServer measurementServer = App.getInstance().getMeasurementServer();

    //константа, передаваемая интенту включения BlueTooth, если последний выключен
    private static final int REQUEST_ENABLE_BT = 1;
    final int ArduinoData = 1;
    final String LOG_TAG = "myLogs";
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private static String MacAddress = "98:D3:31:80:14:16"; // MAC-адрес БТ модуля
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread MyThred = null;
    public String mytext;
    private MyHandler h;
    private int heart = 0;

    static final String[] BUTTONS = new String[]{
            "Connect",
            "showServicesAndCharacteristics",
            "read_rssi",
            "battery_info",
            "setUserInfo",
            "setHeartRateNotifyListener",
            "startHeartRateScan",
            "miband.startVibration(VibrationMode.VIBRATION_WITH_LED);",
            "miband.startVibration(VibrationMode.VIBRATION_WITHOUT_LED);",
            "miband.startVibration(VibrationMode.VIBRATION_10_TIMES_WITH_LED);",
            "stopVibration",
            "setNormalNotifyListener",
            "setRealtimeStepsNotifyListener",
            "enableRealtimeStepsNotify",
            "disableRealtimeStepsNotify",
            "miband.setLedColor(LedColor.ORANGE);",
            "miband.setLedColor(LedColor.BLUE);",
            "miband.setLedColor(LedColor.RED);",
            "miband.setLedColor(LedColor.GREEN);",
            "setSensorDataNotifyListener",
            "enableSensorDataNotify",
            "disableSensorDataNotify",
            "pair",
    };
    private static final String TAG = "==[mibandtest]==";
    private static final int Message_What_ShowLog = 1;
    private MiBand miband;
    private TextView logView;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message m) {
            switch (m.what) {
                case Message_What_ShowLog:
                    String text = (String) m.obj;
                    logView.setText(text);
                    break;
            }
        }
    };

    private HealthDataStore mStore;
    private String APP_TAG = "tag";
    Set<HealthPermissionManager.PermissionKey> mKeys = new HashSet<>();
    private HealthConnectionErrorResult mConnError;
    private MainActivity mInstance = null;

    private long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    private Long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment.newInstance())
                            .commit();
                    setTitle("Измерения");
                    return true;
                case R.id.navigation_notifications:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, DeviceFragment.newInstance())
                            .commit();
                    setTitle("Устройства");
                    return true;
                case R.id.navigation_dashboard:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, ParamFragment.newInstance())
                            .commit();
                    setTitle("Параметры");
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.header_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;

        mKeys.add(new HealthPermissionManager.PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mKeys.add(new HealthPermissionManager.PermissionKey(HealthConstants.BloodGlucose.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        mStore = new HealthDataStore(this, mConnectionListener);
        mStore.connectService();

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit();
        setTitle("Измерения");

        Intent intent = this.getIntent();
        final

        //КНОПКА!!
                FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BluetoothDevice device = Values.device;
                miband = new MiBand(getApplicationContext());
                if (miband != null) {
                    miband.connect(Values.device, new ActionCallback() {

                        @Override
                        public void onSuccess(Object data) {

                            Log.d(TAG,
                                    "Удачного подключения !!!");

                            Toast.makeText(getApplicationContext(), "Mi Band подключен!", Toast.LENGTH_LONG);

                            miband.setDisconnectedListener(new NotifyListener() {
                                @Override
                                public void onNotify(byte[] data) {
                                    Log.d(TAG,
                                            "Соединение отключено !!!");
                                }
                            });
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {

                            Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                        }
                    });
                    miband.setHeartRateScanListener(new HeartRateNotifyListener() {
                        @Override
                        public void onNotify(int heartRate) {
                            Log.d(TAG, "heart rate: " + heartRate);
                            heart = heartRate;
                        }
                    });
                }

                Snackbar.make(view, heart + " уд/мин", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();

            }
        });


        miband = new MiBand(this);
        if (miband != null && Values.device != null) {
            System.out.println("************************ПОШЕЛ КОНЕКТ!!!!!!************************");
            miband.connect(Values.device, new ActionCallback() {

                @Override
                public void onSuccess(Object data) {

                    Log.d(TAG,
                            "Удачного подключения !!!");

                    Toast.makeText(getApplicationContext(), "Mi Band подключен!", Toast.LENGTH_LONG);

                    miband.setDisconnectedListener(new NotifyListener() {
                        @Override
                        public void onNotify(byte[] data) {
                            Log.d(TAG,
                                    "Соединение отключено !!!");
                        }
                    });
                }

                @Override
                public void onFail(int errorCode, String msg) {

                    Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                }
            });

            miband.setHeartRateScanListener(new HeartRateNotifyListener() {
                @Override
                public void onNotify(int heartRate) {
                    Log.d(TAG, "heart rate: " + heartRate);
                }
            });
        }


        //код работы с блютус АРДУИНО
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bluetoothDevice :
                pairedDevices) {
            Log.d(LOG_TAG, bluetoothDevice.getAddress() + " : " + bluetoothDevice.getName());
        }

        //mytext = findViewById(R.id.txtrobot);

        //создание адаптера блютус в программе
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //включение блютус
        if (btAdapter != null) {
            if (btAdapter.isEnabled()) {
                //mytext.setText("Bluetooth включен.");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            MyError("Fatal Error", "Bluetooth ОТСУТСТВУЕТ");
        }

        h = new MyHandler(mytext, measurementServer);
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(MacAddress);
        Log.d(LOG_TAG, "***Получили удаленный Device***" + device.getName());

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.d(LOG_TAG, "...Создали сокет...");
        } catch (IOException e) {
            MyError("Fatal Error", "В onResume() Не могу создать сокет: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();
        Log.d(LOG_TAG, "***Отменили поиск других устройств***");

        Log.d(LOG_TAG, "***Соединяемся...***");
        try {
            btSocket.connect();
            Log.d(LOG_TAG, "***Соединение успешно установлено***");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                MyError("Fatal Error", "В onResume() не могу закрыть сокет" + e2.getMessage() + ".");
            }
        }

        MyThred = new ConnectedThread(btSocket, LOG_TAG, h);
        MyThred.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "...In onPause()...");

        if (MyThred.status_OutStrem() != null) {
            MyThred.cancel();
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            MyError("Fatal Error", "В onPause() Не могу закрыть сокет" + e2.getMessage() + ".");
        }
    }

    private void MyError(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    public void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }

    @Produce
    public String produceEvent() {
        return "Starting up";
    }


    private HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {
                // Check whether the permissions that this application needs are acquired

                //pmsManager.requestPermissions(mKeys, this@MainActivity).setResultListener(mPermissionListener)

                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeys);
                if (resultMap.containsValue(java.lang.Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    pmsManager.requestPermissions(mKeys, mInstance).
                    setResultListener(mPermissionListener);
//                    readStepSynchronously(1, 1000)
                } else {
                    // Get the current step count and display it
                    // ...
                }
            } catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getCanonicalName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {

        }

        @Override
        public void onDisconnected() {

        }
    };

    private HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener = new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

        public void onResult(HealthPermissionManager.PermissionResult result) {
            Log.d(APP_TAG, "Permission callback is received.");
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

            if (resultMap.containsValue(java.lang.Boolean.FALSE)) {
                println("not enough permissions");
//                readStepSynchronously(1, 1000)
                // Requesting permission fails
            } else {
//                readStepSynchronously()
                // Get the current step count and display it
            }
        }
    };

    private void readStepSynchronously() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setLocalTimeRange(HealthConstants.StepCount.START_TIME, HealthConstants.StepCount.TIME_OFFSET,
                        getStartTimeOfToday(), getStartTimeOfToday() + ONE_DAY_IN_MILLIS)
                .build();

        try {
            // Checks the result immediately
            resolver.read(request).setResultListener(stepResult);


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> stepResult = new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {

        public void onResult(HealthDataResolver.ReadResult result) {

            try {
                int count = result.getCount();
                System.out.println("-----Count equals $count------");
                Iterator<HealthData> iterator = result.iterator();

                while (iterator.hasNext()) {
                    HealthData data = iterator.next();
                    data.getFloat(HealthConstants.StepCount.COUNT);
                    println("-----StepCount equals $stepCount------");
                }
            } finally {
                result.close();
            }
        }
    };
}