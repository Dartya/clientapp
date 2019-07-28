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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import java.util.ArrayList;

import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.activities.Values;
import ru.plasticworld.clientapp.data.MeasurementsAdapter;
import ru.plasticworld.clientapp.data.MeasurementsData;

public class HomeFragment extends Fragment {
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);
    private ArrayList<MeasurementsData> dataModels = new ArrayList<>();
    private MeasurementsAdapter adapter;
    private String temp;
    private ListView listView;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ListView getListView() {
        return listView;
    }

    public ArrayList<MeasurementsData> getDataModels() {
        return dataModels;
    }

    public void setDataModels(ArrayList<MeasurementsData> dataModels) {
        this.dataModels = dataModels;
    }

    public MeasurementsAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MeasurementsAdapter adapter) {
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.lv1);

        if (Values.temp == null){
            temp = "";
        }else{
            temp = Values.temp;
        }
        dataModels.add(new MeasurementsData("Пульс, левая рука", "99", "уд/мин"));
        dataModels.add(new MeasurementsData("Температура тела", temp, "гр. С"));
        adapter = new MeasurementsAdapter(dataModels, view.getContext());
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MeasurementsData dataModel= dataModels.get(position);
                Snackbar.make(view, dataModel.getName()+": "+dataModel.getValue()+" "+dataModel.getUnit(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        bus.register(this);
    }

    @Subscribe
    public void getMessage(String s) {
        Snackbar.make(getView(), "ку-ку", Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();
    }
}
