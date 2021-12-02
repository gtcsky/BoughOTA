package com.phy.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Device;

import java.util.List;

/**
 * 清单列表adapter
 * <p>
 * Created by DavidChen on 2018/5/30.
 */

public class DeviceAdapter extends BaseRecyclerViewAdapter<Device> {
    private String TAG = getClass().getSimpleName();
    private OnDeleteClickLister mDeleteClickListener;
    private OnConnectClickLister mConnectClickListener;
    private OnItemClickListener mOnItemClickListener;               //声明一下这个接口
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemTouchListener mOnItemTouchListener;
    private OnCreateContextMenuListener mOnContextMenuListener;

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */


    public DeviceAdapter(Context context, List<Device> data) {

        super(context, data, R.layout.item_device_list);

    }

    @Override
    protected void onBindData(final RecyclerViewHolder holder, Device bean, final int position) {

        View view = holder.getView(R.id.tv_disconnect);
        view.setTag(position);
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
//                        Log.d(TAG, "mDeleteClickListener: not empty");
                        mDeleteClickListener.onDeleteClick(v, (Integer) v.getTag());
                    } else {
//                        Log.d(TAG, "mDeleteClickListener: empty");
                    }
                }
            });
        }
        View connectView = holder.getView(R.id.tv_connect);
        connectView.setTag(position);
        if (!connectView.hasOnClickListeners()) {
            connectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConnectClickListener != null) {
                        mConnectClickListener.onConnectClick(v, (Integer) v.getTag());
                    }
                }
            });
        }

        View view2 = holder.getView(R.id.list_item_grid);

        view2.setTag(position);
        if (!view2.hasOnClickListeners()) {
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
//                        Log.d(TAG, "onClick: not empty");
                        mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
                    } else {
//                        Log.d(TAG, "onClick: empty");
                    }
                }
            });

            view2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
//                    Log.d(TAG, "onLongClick: set long listener");
                    return false;

                }
            });
            view2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    Log.d(TAG, "onTouch: x=" + event.getX());
                    mOnItemTouchListener.onItemTouch(v, (Integer) v.getTag(),event);
                    return false;
                }
            });
        }

//        view2.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                mOnContextMenuListener.onCreateContextMenu(menu,v,menuInfo);
//            }
//        });

        ((TextView) holder.getView(R.id.name_text)).setText(bean.getDevice().getName());
        ((TextView) holder.getView(R.id.mac_text)).setText(bean.getDevice().getAddress());
        ((TextView) holder.getView(R.id.signal_text)).setText(bean.getRssi() + "");
        ImageView signal_image = (ImageView) holder.getView(R.id.signal_image);
//        holder.signal_image = row.findViewById(R.id.signal_image);
        if (bean.getRssi() <= 0 && bean.getRssi() >= -60) {
            signal_image.setImageResource(R.mipmap.signal_4);
        } else if (-70 <= bean.getRssi() && bean.getRssi() < -60) {
            signal_image.setImageResource(R.mipmap.signal_3);
        } else if (-80 <= bean.getRssi() && bean.getRssi() < -70) {
            signal_image.setImageResource(R.mipmap.signal_2);
        } else {
            signal_image.setImageResource(R.mipmap.signal_1);
        }

        TextView connectText = (TextView) holder.getView(R.id.connect_text);
        if (PHYApplication.getApplication().getConnectedDevices().get(bean.getDevice().getAddress()) != null) {
            if (PHYApplication.getApplication().getConnectedDevices().get(bean.getDevice().getAddress()).isfIsConnected()) {
                connectText.setText(R.string.connected_info);
                holder.getView(R.id.tv_connect).setVisibility(View.GONE);
                holder.getView(R.id.tv_disconnect).setVisibility(View.VISIBLE);


            } else {
                connectText.setText(R.string.disconnected_info);
                holder.getView(R.id.tv_connect).setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_disconnect).setVisibility(View.GONE);
            }
        } else {
            connectText.setText(R.string.disconnected_info);
            holder.getView(R.id.tv_connect).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_disconnect).setVisibility(View.GONE);
        }
    }

    public void setOnDeleteClickListener(OnDeleteClickLister listener) {

        this.mDeleteClickListener = listener;
//        Log.d(TAG, "setOnDeleteClickListener: set delete listener");

    }

    public interface OnDeleteClickLister {

        void onDeleteClick(View view, int position);

    }

    public void setOnConnectClickListener(OnConnectClickLister listener) {

        this.mConnectClickListener = listener;
//        Log.d(TAG, "setOnDeleteClickListener: set delete listener");

    }

    public interface OnConnectClickLister {

        void onConnectClick(View view, int position);

    }

    public interface OnCreateContextMenuListener {

        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

    }


    public void setOnCreateContextMenuListener(OnCreateContextMenuListener onMenuClickListener) {

        this.mOnContextMenuListener = onMenuClickListener;

    }

    public interface OnItemClickListener {
        /**
         * item点击回调
         * <p>
         * //         * @param adapter  The Adapter where the click happened.
         *
         * @param v        The view that was clicked.
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        this.mOnItemClickListener = onItemClickListener;

    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int pos);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    public interface OnItemTouchListener {
        void onItemTouch(View view, int pos, MotionEvent event);
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListener = onItemTouchListener;
    }

}
