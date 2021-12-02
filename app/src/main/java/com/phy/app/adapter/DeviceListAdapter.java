package com.phy.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Device;

import java.util.List;

/**
 * DeviceListAdapter
 *
 * @author:zhoululu
 * @date:2018/4/14
 */

public class DeviceListAdapter extends ArrayAdapter{
    private String TAG=getClass().getSimpleName();
    private Context context;
    private int resource;

    public DeviceListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public void setData(List<Device> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        DeviceHolder holder;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(resource,viewGroup,false);
            holder = new DeviceHolder();
            holder.name_text = row.findViewById(R.id.name_text);
            holder.mac_text = row.findViewById(R.id.mac_text);
            holder.signal_image = row.findViewById(R.id.signal_image);
            holder.signal_text = row.findViewById(R.id.signal_text);
            holder.type_text = row.findViewById(R.id.type_text);
            holder.connect_text = row.findViewById(R.id.connect_text);

            row.setTag(holder);
        } else {
            holder = (DeviceHolder) row.getTag();
        }
        Device device = (Device) getItem(i);

        holder.name_text.setText(device.getDevice().getName());
        holder.mac_text.setText(device.getDevice().getAddress());
        holder.signal_text.setText(context.getString(R.string.label_signal_dbm,  device.getRssi()+""));
        if (PHYApplication.getApplication().getConnectedDevices().get(device.getDevice().getAddress()) != null) {
            if(PHYApplication.getApplication().getConnectedDevices().get(device.getDevice().getAddress()).isfIsConnected()){
                holder.connect_text.setText(R.string.connected_info);
            }else{
                holder.connect_text.setText(R.string.disconnected_info);
            }
        } else {
            holder.connect_text.setText(R.string.disconnected_info);
        }
        //holder.type_text.setText(device.getBroadcastType()+"");
        if(device.getRssi() <= 0 && device.getRssi() >= -60){
            holder.signal_image.setImageResource(R.mipmap.signal_4);
        }else if(-70 <= device.getRssi() && device.getRssi() < -60){
            holder.signal_image.setImageResource(R.mipmap.signal_3);
        }else if(-80 <= device.getRssi() && device.getRssi() < -70){
            holder.signal_image.setImageResource(R.mipmap.signal_2);
        }else {
            holder.signal_image.setImageResource(R.mipmap.signal_1);
        }
        Log.d(TAG, device.getDevice().getAddress()+"---"+device.getRssi());
        return row;
    }

    class DeviceHolder{

        TextView name_text;
        TextView mac_text;
        ImageView signal_image;
        TextView signal_text;
        TextView type_text;
        TextView connect_text;

    }

}
