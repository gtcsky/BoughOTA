package com.phy.app.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.util.CharacteristicPropertiesUtil;
import com.phy.app.util.UuidUtils;

import java.util.List;


public class ServiceAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private String TAG=getClass().getSimpleName();

    public ServiceAdapter(Context context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }


    public void setData(List<BluetoothGattService> list) {
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        ServiceHolder holder;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(resource, viewGroup, false);
            holder = new ServiceHolder();
            holder.name_text = row.findViewById(R.id.service_name_text);
            holder.uuid_text = row.findViewById(R.id.service_id_txt);
            holder.type_text = row.findViewById(R.id.service_type);
            row.setTag(holder);
        } else {
            holder = (ServiceHolder) row.getTag();
        }
        BluetoothGattService service = (BluetoothGattService) getItem(i);
        Log.d(TAG, "---------------------------------------------");
        Log.d(TAG, "uuid: "+service.getUuid());
        List<BluetoothGattCharacteristic>  characteristics=service.getCharacteristics();
        Log.d(TAG, "characteristics: "+characteristics.size());
        Log.d(TAG, "__________");
        for(BluetoothGattCharacteristic characteristic:characteristics){
            Log.d(TAG, "uuid:"+characteristic.getUuid()+"\t name:"+UuidUtils.getCharacterName(characteristic.getUuid())+"\t properties:"+characteristic.getProperties()+":" +CharacteristicPropertiesUtil.getPropertiesName(characteristic.getProperties()));
        }
        Log.d(TAG, "---------------------------------------------");
        holder.name_text.setText(UuidUtils.getServiceName(service.getUuid()));
        holder.uuid_text.setText("UUID:"+service.getUuid().toString());
        if (service.getType() == 0) {
            holder.type_text.setText("Primary Service");
        } else {
            holder.type_text.setText("Primary ServiceSecondary Service");
        }
        return row;
    }

    class ServiceHolder {

        TextView name_text;
        TextView uuid_text;
        TextView type_text;
        ImageView arrow_view;

    }

}
