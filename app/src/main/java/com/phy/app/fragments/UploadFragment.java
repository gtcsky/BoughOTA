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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phy.app.R;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.util.HexString;
import com.phy.app.util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private String TAG=getClass().getSimpleName();
    private ToggleButton hexBtn;
    private ToggleButton notifyHexBtn;
    private TextView closeView;
    private TextView clearView;
    private TextView writeView;
    private EditText contentArea;
    private UploadFragment.OnActionClickListener mOnActionClickListener;
    private boolean isHexMode,isNotifyHexMode;
    private LinearLayout notifyLayout;
    private EditText notifyContext;
    public static UploadFragment newInstance() {
        Bundle args = new Bundle();
        UploadFragment fragment = new UploadFragment();
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
        //设置边距
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.72), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
//        Log.d(TAG, "onCreateView: ");
        hexBtn = root.findViewById(R.id.hex_status);
        closeView = root.findViewById(R.id.close_text);
        writeView = root.findViewById(R.id.write_text);
        clearView = root.findViewById(R.id.clear_text);
        contentArea = root.findViewById(R.id.message_txt);
        notifyLayout=root.findViewById(R.id.nofity_title_layout);
        notifyContext=root.findViewById(R.id.notity_txt);
        notifyHexBtn=root.findViewById(R.id.notify_hex_status);

        notifyHexBtn.setOnCheckedChangeListener(this);
        notifyHexBtn.setChecked(true);
        hexBtn.setOnCheckedChangeListener(this);
        hexBtn.setChecked(true);
        clearView.setOnClickListener(this);
        closeView.setOnClickListener(this);

        writeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionClickListener != null) {
                    mOnActionClickListener.onActionClick(v);
                    notifyContext.setText("");
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
        } else if (v.getId() == R.id.write_text) {
            Log.d(TAG, "onClick: write");

        } else if (v.getId() == R.id.clear_text) {
            contentArea.setText("");
            notifyContext.setText("");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.hex_status) {
            if (isChecked) {
                isHexMode = true;
            } else {
                isHexMode = false;
            }
        } else if (buttonView.getId() == R.id.notify_hex_status) {
            if (isChecked) {
                isNotifyHexMode = true;
            } else {
                isNotifyHexMode = false;
            }

        }
    }

    public byte[] getEditAreaContent(){
        if(isHexMode){
            return HexString.parseHexString(contentArea.getText().toString().trim());
        }else{
            return contentArea.getText().toString().trim().getBytes();
        }

    }

    public interface OnActionClickListener {
        /**
         * item点击回调
         */
        void onActionClick(View v);
    }

    public void setOnActionClickListener(UploadFragment.OnActionClickListener listener) {
        this.mOnActionClickListener = listener;
    }




    public void updateNotifyInfo(NotifyInfo notifyInfo) {
        updateNotifyInfo(notifyInfo.getData());
    }

    public void updateNotifyInfo(byte[] value) {
        notifyLayout.setVisibility(View.VISIBLE);
        notifyContext.setVisibility(View.VISIBLE);
        if(isNotifyHexMode) {
            notifyContext.append(Util.hex2AsciiStr(value));
//            notifyContext.append(System.lineSeparator());
        } else {
            notifyContext.append(Util.Hex2Str(value));
//            notifyContext.append(System.lineSeparator());
        }
//        notifyContext.setSelection(notifyContext.length());         //光标切换至尾端
    }
}