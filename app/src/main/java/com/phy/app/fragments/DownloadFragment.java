package com.phy.app.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phy.app.R;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.ble.OperateConstant;
import com.phy.app.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadFragment extends  DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private String TAG = getClass().getSimpleName();
    private ToggleButton hexBtn;
    private TextView closeView;
    private TextView clearView;
    private TextView readView;
    private EditText contextArea;
    private OnActionClickListener mOnActionClickListener;
//    private OnUpdateEditTextCallback mOnUpdateEditTextCallback;
//    private EventBus fragmentEventBus;
    private boolean isHexMode;

    public static DownloadFragment newInstance() {
        Bundle args = new Bundle();
        DownloadFragment fragment = new DownloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
//        window.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.circle5_white));
//        window.setWindowAnimations(R.style.BottomDialog_Animation);
        //设置边距
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.72), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_download, container, false);
        hexBtn = root.findViewById(R.id.hex_status);
        closeView = root.findViewById(R.id.close_text);
        readView = root.findViewById(R.id.read_text);
        clearView = root.findViewById(R.id.clear_text);
        contextArea = root.findViewById(R.id.message_txt);
        hexBtn.setOnCheckedChangeListener(this);
        clearView.setOnClickListener(this);
        closeView.setOnClickListener(this);

        readView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionClickListener != null) {
                    mOnActionClickListener.onActionClick(v);
                } else {
                    Log.d(TAG, "mOnActionClickListener is null ");
                }
            }
        });
        return root;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_text) {
            this.dismiss();
        } else if (v.getId() == R.id.read_text) {
            Log.d(TAG, "onClick: read");

        } else if (v.getId() == R.id.clear_text) {
            contextArea.setText("");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.hex_status) {
            if (isChecked) {
//                Log.d(TAG, "onCheckedChanged: On");
                isHexMode=true;
            } else {
                isHexMode=false;
//                Log.d(TAG, "onCheckedChanged: Off");
            }
        }
    }

    public interface OnActionClickListener {
        /**
         * item点击回调
         */
        void onActionClick(View v);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.mOnActionClickListener = listener;
    }

//    public interface OnUpdateEditTextCallback {
//        void onUpdateEditText(View v,byte[] value);
//    }
//
//    public void setOnUpdateEditTextCallback(OnUpdateEditTextCallback callback) {
//        this.mOnUpdateEditTextCallback = callback;
//    }


    public void updateEditArea(NotifyInfo notifyInfo) {
        if(isHexMode){
            contextArea.append(Util.hex2AsciiStr(notifyInfo.getData()));
            contextArea.append(System.lineSeparator());
        }else{
            try {
                if(notifyInfo.getUuid().equalsIgnoreCase(OperateConstant.USER_CURRENT_BATTERY_READ_UUID)||notifyInfo.getUuid().equalsIgnoreCase(OperateConstant.USER_REAL_BATTERY_READ_UUID)){
                    int offset=notifyInfo.getData()[0]&0xff;
                    int minVolt=(notifyInfo.getData()[1]&0xff)*256;
                    minVolt+=notifyInfo.getData()[2]&0xff;
                    int maxVolt=(notifyInfo.getData()[3]&0xff)*256;
                    maxVolt+=notifyInfo.getData()[4]&0xff;
                    int current=minVolt+offset;
                    String str=String.format("最低:%.2fv\t 最高:%.2fv\n",minVolt*0.01f,maxVolt*0.01f);
                    contextArea.append(str);
                    str=String.format("当前:%.2fv\n",current*0.01f);
                    contextArea.append(str);
                }else{
                    contextArea.append(Util.Hex2Str(notifyInfo.getData()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("接收到的数据不是字符串");
            }
            contextArea.append(System.lineSeparator());
        }
        contextArea.setSelection(contextArea.length());         //光标切换至尾端

    }
//    public void updateEditArea(byte[] value) {
//        if (isHexMode) {
//            contextArea.append(Util.hex2AsciiStr(value));
//            contextArea.append(System.lineSeparator());
//        } else {
//            try {
//                contextArea.append(Util.Hex2Str(value));
//            } catch (Exception e) {
//                throw new RuntimeException("接收到的数据不是字符串");
//            }
//            contextArea.append(System.lineSeparator());
//        }
//        contextArea.setSelection(contextArea.length());         //光标切换至尾端
//    }



//    public void onEvent(FragmentEvent event) {
//        int type = event.eventType;
//        if (type == 1) {
//            Log.d("", "onEvent type 1:" + event.data);
//        }
//        else if (type == 2) {
//            Log.d("", "onEvent type 2:" + (event.data instanceof DataCallBack));
//            if (event.data instanceof DataCallBack) {
//                actCallback = ((DataCallBack) event.data);//得到回调
//
//                //发消息给MainActivity
//                MyEvent event2 = new MyEvent();
//                event2.data = "call main activity method";
//                eventBus.post(event2);//发布消息
//            }
//        }
//    }
}