package com.mission.schedule.utils;

import com.mission.schedule.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProgressUtil {
    Dialog dialog = null;

    public void ShowProgress(Context context, boolean b, boolean cancelable, String message) {
        if (b) {
            dialog = createLoadingDialog(context, message);
//            dialog.setTitle("提示：");
//            dialog.setMessage(message);
//            dialog.setIndeterminate(b);
            dialog.setCanceledOnTouchOutside(cancelable);// 设置点击屏幕Dialog不消失  
            dialog.show();

        } else {
            dialog.cancel();
            dialog.dismiss();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        int width = context.getResources().getDisplayMetrics().widthPixels;

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        layout.setLayoutParams(new LinearLayout.LayoutParams((int) (width / 1.5f), (int) (width / 2)));
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (msg.contains("......")) {
            msg = msg.replace("......", "") + " " + "...";
        } else if (msg.contains("...")) {
            msg = msg.replace("...", "") + " " + "...";
        } else {
            msg = msg + " " + "...";
        }
        tipTextView.setText(msg);// 设置加载信息  

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  

        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;

    }
}
