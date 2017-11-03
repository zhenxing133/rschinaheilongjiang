package com.rschina.heilongjiang.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rschina.heilongjiang.R;

/**
 * Created by 123 on 2016/9/21.
 */
public class DialogDownload {
    Context mContext;
    Dialog downloadDialog;
    TextView confirm,cancle;
    LinearLayout layout_download;
    public int screenWidth,screenHight;

    public DialogDownload(Context context){
        mContext=context;
        downloadDialog = new Dialog(mContext,R.style.Download_dialog);// 创建自定义样式dialog
        downloadDialog.setContentView(R.layout.dialog_download);
        RelativeLayout layout = (RelativeLayout) downloadDialog.findViewById(R.id.download_dialog_view);// 加载布局
        layout_download =(LinearLayout) downloadDialog.findViewById(R.id.layout_download);
//        从下往上动画效果
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_to_top_animation);
//        layout.setAnimation(hyperspaceJumpAnimation);
        confirm = (TextView) downloadDialog.findViewById(R.id.confirm);
        cancle = (TextView) downloadDialog.findViewById(R.id.cancle);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDialog.dismiss();
            }
        });

        WindowManager.LayoutParams params = downloadDialog.getWindow().getAttributes();
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
        screenHight = wm.getDefaultDisplay().getHeight();
        params.width = screenWidth;
        params.height =(int)(screenHight*0.3);
        Window window = downloadDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置


        downloadDialog.setCancelable(true);// 可以用“返回键”取消
        downloadDialog.setCanceledOnTouchOutside(true);
        downloadDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
    }

    public  void setOnConfirmClickListener(View.OnClickListener listener){
        confirm.setOnClickListener(listener);
    }

    public void show(){
        downloadDialog.show();
    }

    public void hide(){
        downloadDialog.hide();
    }

    public void dismiss(){
        downloadDialog.dismiss();
    }

}
