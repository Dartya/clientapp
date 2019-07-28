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

public class DeviceAdapter extends ArrayAdapter<DeviceData> implements View.OnClickListener {

    private ArrayList<DeviceData> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView deviceName;
    }

    public DeviceAdapter(ArrayList<DeviceData> dataSet, Context mContext) {
        super(mContext, R.layout.device_row_item, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DeviceData dataModel = (DeviceData) object;

        switch (v.getId()) {
            case R.id.paramValue:
                Snackbar.make(v, "Устройство: " + dataModel.getDeviceName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceData dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_row_item, parent, false);
            viewHolder.deviceName = convertView.findViewById(R.id.deviceName);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DeviceAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.deviceName.setText(dataModel.getDeviceName());
        viewHolder.deviceName.setOnClickListener(this);
        viewHolder.deviceName.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
