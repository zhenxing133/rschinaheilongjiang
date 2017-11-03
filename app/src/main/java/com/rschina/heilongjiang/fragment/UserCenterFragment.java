package com.rschina.heilongjiang.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.activity.FeadBackActivity;
import com.rschina.heilongjiang.activity.ForgetActivity;
import com.rschina.heilongjiang.activity.SettingActivity;
import com.rschina.heilongjiang.adapter.LoadingDialog;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.model.FileCache;
import com.rschina.heilongjiang.utils.ImageBig;
import com.rschina.heilongjiang.utils.PickPhoto;
import com.rschina.heilongjiang.utils.UserSharedPrefs;
import com.rschina.heilongjiang.views.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import kr.co.namee.permissiongen.PermissionGen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/10/10.
 */

public class UserCenterFragment extends Fragment implements View.OnClickListener {

    private String user;
    private Context mContext;
    private TextView tv_user;
    private RelativeLayout tv_clean;
    private String file;
    //private ImageView iv_back;
    private Dialog dialog;
    private RelativeLayout fead_back;
    private TextView setting;
    private RelativeLayout tv_logout;
    private TextView set_pwd;
    private Dialog auto_dislog;
    private CircleImageView iv_user_head;
    public int screenWidth,screenHight;
    private RelativeLayout tv_collection;
    private UserSharedPrefs sp;
    private String username;
    private FileCache fileCache;
    private static final int TAKE_PHOTO = 3022;
    private static final int PICK_PHOTO = 3021;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_usercenter,null);
        sp = new UserSharedPrefs(getActivity());
        fileCache = new FileCache(getActivity());
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_clean = (RelativeLayout) view.findViewById(R.id.tv_clean);
        fead_back = (RelativeLayout) view.findViewById(R.id.tv_feadback);
        setting = (TextView) view.findViewById(R.id.setting);
        tv_logout = (RelativeLayout) view.findViewById(R.id.tv_logout);
        tv_collection = (RelativeLayout) view.findViewById(R.id.tv_collection);
        set_pwd = (TextView) view.findViewById(R.id.set_pwd);
        iv_user_head = (CircleImageView) view.findViewById(R.id.personal_head_icon);
        if (getArguments() != null) {
            user = getArguments().getString("param");
            //Log.e("yzx", user1 + "====");
            tv_user.setText("帐号:" + user);
            sp.openEditor();
            sp.setUsername(user);
            sp.closeEditor();
        } else {
            username = sp.getUsername();
            //Log.e("yzx", username + "000000000");
            tv_user.setText("帐号:" + username);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initListener();
        if (username == null) {
            tv_user.setText("帐号:" + user);
        } else {
            tv_user.setText("帐号:" + username);
        }

        iv_user_head.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //申请权限
                PermissionGen.with(getActivity()).addRequestCode(100)
                        .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA).request();
                //自定义dailog
                auto_dislog = new Dialog(getActivity(), R.style.avatar_dialog);
                auto_dislog.setCancelable(true);
                auto_dislog.setCanceledOnTouchOutside(true);
                auto_dislog.setContentView(R.layout.dialog_photo_select);
                TextView take_camner = (TextView) auto_dislog.findViewById(R.id.take_photo);
                TextView take_from_phonecamner = (TextView) auto_dislog.findViewById(R.id.take_from_phone);
                TextView cancle_select_photo = (TextView) auto_dislog.findViewById(R.id.cancle_select_photo);
                //拍照
                take_camner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auto_dislog.dismiss();
                        file = fileCache.getImageCacheDir().getAbsolutePath() + File.separator + "avatar.jpg";

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file)));
                        startActivityForResult(intent, TAKE_PHOTO);
                    }
                });
                //从手机相册
                take_from_phonecamner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auto_dislog.dismiss();
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_PHOTO);
                    }
                });
                //取消
                cancle_select_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        auto_dislog.dismiss();
                    }
                });
                WindowManager.LayoutParams params = auto_dislog.getWindow().getAttributes();
                WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
                screenWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
                screenHight = wm.getDefaultDisplay().getHeight();
                params.width = (int) (screenWidth * 0.99); // 宽度设置为屏幕的0.8
                params.height = (int) (screenHight * 0.28); // 宽度设置为屏幕的0.8
                Window window = auto_dislog.getWindow();
                window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
                auto_dislog.show();

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void initListener() {
        tv_clean.setOnClickListener(this);
        fead_back.setOnClickListener(this);
        setting.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        set_pwd.setOnClickListener(this);
        tv_collection.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_pwd:
                Intent intent1 = new Intent(getActivity(), ForgetActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_clean:
                dialog = LoadingDialog.createLoadingDialog(getActivity(), "缓存清理中...");
                dialog.show();
                //handler.sendEmptyMessageDelayed(0, 4000);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        SystemClock.sleep(3500);
                        dialog.dismiss();
                        Looper.prepare();
                        Toast.makeText(getActivity(),"缓存清理成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }.start();
                break;
            case R.id.tv_feadback:
                final Intent intent = new Intent(getActivity(), FeadBackActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                Intent intent2 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_logout:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否注销登入?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OkHttpClient okhttp = new OkHttpClient();
                        Request request = new Request.Builder().url(Const.LOG_OUT).build();
                        Call call = okhttp.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                Toast.makeText(getActivity(), "注销失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String s = response.body().string();
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    if (obj.getInt("code") == 1) {
                                        ((MainActivity)getActivity()).gotoUserFragment();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();

                break;
            case R.id.tv_collection:
                //Intent markIntent = new Intent(getActivity(), CollectionActivity.class);
                //startActivity(markIntent);
                ((MainActivity)getActivity()).gotoCollectionFragment();
                break;
        }
    }

    public static UserCenterFragment newInstance(String text) {
        UserCenterFragment fragment = new UserCenterFragment();
        Bundle args = new Bundle();
        args.putString("param", text);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 头像回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("yzx", "onActivityResult");
//        if (resultCode == Activity.RESULT_OK) {
            Log.e("yzx", "onActivityResult1");
            String path = null;
            switch (requestCode) {
                case PickPhoto.TAKE_PHOTO:
                    Log.e("yzx", "onActivityResult2");
                    path = ImageBig.scalePicture(mContext, fileCache.getImageCacheDir().getAbsolutePath()
                                    + File.separator + "avatar.jpg", 800, 800);
                    Log.e("yzx", "path" + path);
                    if (path != null) {
                        Log.e("yzx", "onActivityResult3");
                        iv_user_head.setImageURI(Uri.parse(path));
                    }
                    break;
                case PickPhoto.PICK_PHOTO:
                    //4.4版本以上用这个方法
                    showCurrentImage(data);
                    break;
                default:
                    break;
            }
//        }
    }

    private void showCurrentImage(Intent data) {
        Uri uri = data.getData();
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            //如果是document类型的uri，刚通过document id处理
            String docid = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析数字格式的id
                String id = docid.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docid));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的直接获取图片路径
            imagePath = uri.getPath();
        }
        //显示图片
        iv_user_head.setImageURI(Uri.parse(imagePath));
    }

    /**
     * 设置图片路径
     *
     * @return
     */
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
