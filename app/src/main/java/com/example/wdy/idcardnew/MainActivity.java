package com.example.wdy.idcardnew;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wdy.idcardnew.function.useful_functions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private Context context;

    private static final int num = 123;

    public  String TAG ="mainactiity";
    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =this;
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);//隐藏标题栏实现全屏
        setContentView(R.layout.activity_main);
        requireSomePermission();


    }

   @AfterPermissionGranted(num)
   private void requireSomePermission() {
       String[] perms = {
               // 把你想要申请的权限放进这里就行，注意用逗号隔开
               Manifest.permission.CAMERA,
               Manifest.permission.READ_EXTERNAL_STORAGE,
               Manifest.permission.WRITE_EXTERNAL_STORAGE
       };
       if (EasyPermissions.hasPermissions(this, perms)) {
           // Already have permission, do the thing
           // ...
           //复制asset下的训练数据
           try
           {
               useful_functions.copyAssetFile(context);
           }catch (Exception e)
           {
               e.printStackTrace();
           }

           // 计时器
           Timer timer =new Timer();//计时器应该是开启了一个子线程，在子线程里不能直接开始Toast，应先初始化消息队列
           //实例化计时器
           TimerTask task =new TimerTask() {
               @Override
               public void run() {
                   //跳转至下一个activity

                       Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                       intent.putExtra("id_card_side", "ID_CARD_SIDE_FRONT");
                       intent.putExtra("type", "idcardFront");
                       startActivity(intent);

                       finish();
               }
           };
           //设置时间
           timer.schedule(task,2000);
           Toast.makeText(this, "已获取所需权限，准备预览!", Toast.LENGTH_LONG).show();
       } else {
           // Do not have permissions, request them now
           EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask_again),
                   num, perms);
       }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "权限被阻止请开启!", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getApplicationContext(), mLoaderCallback);
            Log.i(TAG, "onResume sucess load OpenCV...");
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}

