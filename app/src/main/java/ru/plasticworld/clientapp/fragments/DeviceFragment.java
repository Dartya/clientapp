package ru.plasticworld.clientapp.fragments;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.zhaoxiaodan.miband.MiBand;

import java.util.ArrayList;
import java.util.HashMap;

import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.activities.MainActivity;
import ru.plasticworld.clientapp.activities.Values;
import ru.plasticworld.clientapp.data.DeviceAdapter;
import ru.plasticworld.clientapp.data.DeviceData;

public class DeviceFragment extends Fragment {

    private ArrayList<DeviceData> dataModels = new ArrayList<>();
    private DeviceAdapter adapter;

    private ToggleButton toggleButton;

    private static final String TAG = "==[mibandtest]==";
    private MiBand miband;


    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();

    public static DeviceFragment newInstance() {
        Bundle args = new Bundle();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d(TAG,
                    "Найти близлежащие устройства Bluetooth: name:" + device.getName() + ",uuid:"
                            + device.getUuids() + ",add:"
                            + device.getAddress() + ",type:"
                            + device.getType() + ",bondState:"
                            + device.getBondState() + ",rssi:" + result.getRssi());
            //получаем имя устройства
            String name = device.getName();
            //если не пустое и равно
            if (name != null && (name.equals("Mi Band 3") || name.equals("HC-06"))) {
                String address = device.getAddress();
                int type = device.getType();
                String item = name + "|" + address;
                if (!devices.containsKey(item)) {
                    devices.put(item, device);
                    adapter.add(new DeviceData(item));
                }
            }

//            String item = device.getName() + "|" + device.getAddress();
//            if (!devices.containsKey(item)) {
//                devices.put(item, device);
//                adapter.add(new DeviceData(item));
//            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        miband = new MiBand(view.getContext());

        toggleButton = view.findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()){
                    MiBand.stopScan(scanCallback);
                } else {
                    MiBand.startScan(scanCallback);
                }
            }
        });

        //инициализация листа устроуйств
        ListView listView = view.findViewById(R.id.lv2);

//        dataModels.add(new DeviceData("HC-06"));
//        dataModels.add(new DeviceData("Mi Band 3"));

        adapter = new DeviceAdapter(dataModels, view.getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DeviceData dataModel = dataModels.get(position);

                String item = dataModel.getDeviceName();
                System.out.println(item);
                if (devices.containsKey(item)) {

                    Log.d(TAG, "Остановить сканирование...");
                    MiBand.stopScan(scanCallback);

                    BluetoothDevice device = devices.get(item);
                    Values.device = device;

//                    Intent intent = new Intent();
//                    intent.putExtra("device", device);
//                    intent.setClass(view.getContext(), MainActivity.class);
                }
            }
        });
    }



}
