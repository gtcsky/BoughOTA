package com.phy.app.util;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;

import java.lang.reflect.Field;

public class DialogUtils {
    private static String TAG="DialogUtils";
    public static void updateBackgroundColor(AlertDialog dialog) {
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(userDialogDrawable());               //修改对话框背景色
        }

    }

    /**
     *  修改内容颜色
     * @param dialog
     */
    public static void setContextColor(AlertDialog dialog) {
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);

            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextColor(Color.WHITE);//更改内容的颜色

            Field mTitleView = mAlertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);

            TextView title = (TextView) mTitleView.get(mAlertController);
            title.setTextColor(Color.YELLOW);//更改标题的颜色

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        //更改按钮颜色
        try {
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.YELLOW);

        } catch (Exception e) {
            throw new RuntimeException("No Negative Key");
        }
        try {
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.YELLOW);
        } catch (Exception e) {
            throw new RuntimeException("No Positive Key");
        }
    }


    /**
     * 自定义对话框背景色
     *
     * @return
     */
    private static Drawable userDialogDrawable() {

        return setDialogBack(16, 16, 16, 16, 255, 60, 63, 65);
    }

    private static Drawable setDialogBack(float cTopLeft, float cTopRight, float cBottomLeft, float cBottomRight, int a, int r, int g, int b) {
        float outRectr[] = new float[]{cTopLeft, cTopLeft, cTopRight, cTopRight, cBottomRight, cBottomRight, cBottomLeft, cBottomLeft};
        RoundRectShape rectShape = new RoundRectShape(outRectr, null, null);
        ShapeDrawable normalDrawable = new ShapeDrawable(rectShape);
        normalDrawable.getPaint().setColor(Color.argb(a, r, g, b));
        return normalDrawable;
    }



    public static AlertDialog createUserDialog(android.content.Context context, AlertDialog.Builder builder, AlertDialog dialog, String title, String msg, String positiveInfo, String negativeInfo, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        if(builder==null){
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setCancelable(true);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
            if(negativeInfo!=null)
                builder.setNegativeButton(negativeInfo, negativeListener);

            if(positiveInfo!=null)
                builder.setPositiveButton(positiveInfo,positiveListener);

        }
        if(dialog==null){
            dialog=builder.create();
            updateBackgroundColor(dialog);
        }
        dialog.show();
        setContextColor(dialog);
        return dialog;
    }


    public static AlertDialog createUserDialog(android.content.Context context, AlertDialog.Builder builder, AlertDialog dialog, String title, String msg, String positiveInfo, String negativeInfo, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener,DialogInterface.OnDismissListener onDismissListener) {
        dialog=createUserDialog(context, builder,dialog, title,  msg, positiveInfo, negativeInfo,  positiveListener, negativeListener);
        if(builder!=null)
            builder.setOnDismissListener(onDismissListener);
        return dialog;
    }
}
