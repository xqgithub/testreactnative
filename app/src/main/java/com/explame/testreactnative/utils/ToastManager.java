package com.explame.testreactnative.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by XQ on 2017/4/5.
 * Toast管理类，解决toast队列必须显示完后才会关闭的弊端
 */
public class ToastManager {
    private Toast toast;

    public static ToastManager instance;

    private Context context;

    public static ToastManager getInstance(Context con) {

        if (instance == null) {
            instance = new ToastManager(con);
        }
        return instance;
    }

    private ToastManager(Context context) {
        this.context = context;
    }

    public void showToast(String msg, int time) {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, msg, time);
        } else {

            toast.setText(msg);
            toast.setDuration(time);

        }
        toast.show();
    }

    public void showToast(String msg) {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            return;
        }

        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {

            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_LONG);

        }
        toast.show();
    }

    /**
     * 自定义带图片的Toast
     */
//    public void ToastCustomize(String msg) {
//
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            return;
//        }
//
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.toast_customize, null, false);
//
//        TextView toastcustomize_tv = (TextView) view
//                .findViewById(R.id.toastcustomize_tv);
//        toastcustomize_tv.setText(msg);
//
//        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(view);
//        toast.show();
//    }

    /**
     * Toast的消失
     */
    public void Disappear() {
        toast.cancel();
    }


}
