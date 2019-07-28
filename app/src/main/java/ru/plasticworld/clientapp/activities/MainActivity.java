package ru.plasticworld.clientapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Produce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.data.MeasurementsAdapter;
import ru.plasticworld.clientapp.fragments.DeviceFragment;
import ru.plasticworld.clientapp.fragments.HomeFragment;
import ru.plasticworld.clientapp.fragments.ParamFragment;
import ru.plasticworld.clientapp.logic.ConnectedThread;
import ru.plasticworld.clientapp.logic.MyHandler;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

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
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit();
        setTitle("Измерения");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "ыыы", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        //код работы с блютус
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

        h = new MyHandler(mytext);
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(MacAddress);
        Log.d(LOG_TAG, "***Получили удаленный Device***"+device.getName());

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

        try     {
            btSocket.close();
        } catch (IOException e2) {
            MyError("Fatal Error", "В onPause() Не могу закрыть сокет" + e2.getMessage() + ".");
        }
    }

    private void MyError(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Produce
    public String produceEvent() {
        return "Starting up";
    }

}
