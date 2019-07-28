package ru.plasticworld.clientapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import ru.plasticworld.clientapp.R;

public class ParametersAdapter extends ArrayAdapter<ParametersData> implements View.OnClickListener{

    private ArrayList<ParametersData> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView parameterName;
        TextView parameter;
        TextView device;
    }

    public ParametersAdapter(ArrayList<ParametersData> dataSet, Context mContext) {
        super(mContext, R.layout.parameter_row_item, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ParametersData dataModel=(ParametersData)object;

        switch (v.getId())
        {
            case R.id.paramValue:
                Snackbar.make(v, "Имя параметра: " +dataModel.getParamName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParametersData dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.parameter_row_item, parent, false);
            viewHolder.parameterName = convertView.findViewById(R.id.parameterName);
            viewHolder.parameter = convertView.findViewById(R.id.parameter);
            viewHolder.device = convertView.findViewById(R.id.device);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ParametersAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.parameterName.setText(dataModel.getParamName());
        viewHolder.parameter.setText(dataModel.getParameter());
        viewHolder.device.setText(dataModel.getDeviceName());
        viewHolder.parameter.setOnClickListener(this);
        viewHolder.parameter.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
