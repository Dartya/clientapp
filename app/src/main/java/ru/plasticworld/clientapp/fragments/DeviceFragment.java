package ru.plasticworld.clientapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.data.DeviceAdapter;
import ru.plasticworld.clientapp.data.DeviceData;

public class DeviceFragment extends Fragment {

    private ArrayList<DeviceData> dataModels = new ArrayList<>();
    private DeviceAdapter adapter;

    public static DeviceFragment newInstance() {
        Bundle args = new Bundle();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //инициализация листа измерений
        ListView listView = view.findViewById(R.id.lv2);

        dataModels.add(new DeviceData("HC-06"));
        dataModels.add(new DeviceData("Mi Band 3"));

        adapter = new DeviceAdapter(dataModels, view.getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DeviceData dataModel= dataModels.get(position);

                Snackbar.make(view, "Устройство: "+dataModel.getDeviceName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }
}
