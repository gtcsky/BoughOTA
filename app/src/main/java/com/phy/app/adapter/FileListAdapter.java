package com.phy.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.beans.Device;

import java.util.List;

/**
 * FileListAdapter
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class FileListAdapter extends ArrayAdapter {

    private Context context;
    private int resource;

    public FileListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    public void setData(List<String> list){
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

    public void setData(String name){
        add(name);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        FileListAdapter.FileHolder holder;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(resource,viewGroup,false);
            holder = new FileListAdapter.FileHolder();
            holder.fileName_text = row.findViewById(R.id.file_name_text);

            row.setTag(holder);
        } else {
            holder = (FileListAdapter.FileHolder) row.getTag();
        }

        String fileName = (String) getItem(i);

        holder.fileName_text.setText(fileName);

        return row;
    }

    class FileHolder{
        TextView fileName_text;
    }

}
