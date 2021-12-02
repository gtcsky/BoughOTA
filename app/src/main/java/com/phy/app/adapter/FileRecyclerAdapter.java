package com.phy.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.phy.app.R;

import java.util.List;

public class FileRecyclerAdapter extends BaseRecyclerViewAdapter<String> {
    private String TAG = getClass().getSimpleName();
    private OnDeleteClickLister mDeleteClickListener;
    private OnItemClickListener mOnItemClickListener;//声明一下这个接口
    private OnItemLongClickListener onItemLongClickListener;
    private OnCreateContextMenuListener mOnContextMenuListener;
    private OnItemTouchListener mOnItemTouchListener;

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */


    public FileRecyclerAdapter(Context context, List<String> data) {

        super(context, data, R.layout.item_file_list);

    }

    @Override
    protected void onBindData(final RecyclerViewHolder holder, String fileName, final int position) {

        View deleteView = holder.getView(R.id.tv_delete);
        deleteView.setTag(position);
        if (!deleteView.hasOnClickListeners()) {
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDeleteClickListener != null) {
                        mDeleteClickListener.onDeleteClick(v, (Integer) v.getTag());
                    }
                }
            });
        }
//        Log.d(TAG, "onBindData: "+position);
        View fileNameView = holder.getView(R.id.file_name_text);
        fileNameView.setTag(position);
        if (!fileNameView.hasOnClickListeners()) {
            fileNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
                    } else {
                    }
                }
            });

            fileNameView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
                    return false;

                }
            });

            fileNameView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mOnItemTouchListener.onItemTouch(v, (Integer) v.getTag(),event);
                    return false;
                }
            });
        }
        ((TextView)fileNameView).setText(fileName);
        deleteView.setVisibility(View.VISIBLE);


//        TextView connectText = (TextView) holder.getView(R.id.connect_text);
//        if (PHYApplication.getApplication().getConnectedDevices().get(bean.getDevice().getAddress()) != null) {
//            if (PHYApplication.getApplication().getConnectedDevices().get(bean.getDevice().getAddress()).isfIsConnected()) {
//                connectText.setText(R.string.connected_info);
//                holder.getView(R.id.tv_connect).setVisibility(View.GONE);
//                holder.getView(R.id.tv_disconnect).setVisibility(View.VISIBLE);
//
//
//            } else {
//                connectText.setText(R.string.disconnected_info);
//                holder.getView(R.id.tv_connect).setVisibility(View.VISIBLE);
//                holder.getView(R.id.tv_disconnect).setVisibility(View.GONE);
//            }
//        } else {
//            connectText.setText(R.string.disconnected_info);
//            holder.getView(R.id.tv_connect).setVisibility(View.VISIBLE);
//            holder.getView(R.id.tv_disconnect).setVisibility(View.GONE);
//        }
    }

    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
//        Log.d(TAG, "setOnDeleteClickListener: set delete listener");

    }

    public interface OnDeleteClickLister {
        void onDeleteClick(View view, int position);
    }

//    public void setOnConnectClickListener(DeviceAdapter.OnConnectClickLister listener) {
//        this.mConnectClickListener = listener;
//    }
//
//    public interface OnConnectClickLister {
//        void onConnectClick(View view, int position);
//    }

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


    public interface OnItemTouchListener{
        void onItemTouch(View view, int pos, MotionEvent event);
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener){
        this.mOnItemTouchListener=onItemTouchListener;
    }
}
