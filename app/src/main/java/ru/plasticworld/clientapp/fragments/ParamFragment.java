package ru.plasticworld.clientapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

import ru.plasticworld.clientapp.R;
import ru.plasticworld.clientapp.data.ParametersAdapter;
import ru.plasticworld.clientapp.data.ParametersData;

public class ParamFragment extends Fragment {

    private ArrayList<ParametersData> dataModels = new ArrayList<>();
    private ParametersAdapter adapter;

    public static ParamFragment newInstance() {
        Bundle args = new Bundle();
        ParamFragment fragment = new ParamFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.param_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //инициализация листа параметров
        ListView listView = view.findViewById(R.id.lv3);

        dataModels.add(new ParametersData("Температура тела", "Температура", "HC-06"));
        dataModels.add(new ParametersData("Пульс в левой руке", "Пульс", "Mi Band 3"));

        adapter = new ParametersAdapter(dataModels, view.getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ParametersData dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getParamName()+": "+dataModel.getParameter()+" "+dataModel.getDeviceName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        System.out.println(listView.getAdapter().getItem(0).getClass().toString());
    }

}
