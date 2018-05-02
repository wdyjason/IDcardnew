package com.example.wdy.idcardnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wdy.idcardnew.function.set_camera_view;
import com.example.wdy.idcardnew.function.useful_functions;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;


public class CameraActivity extends Activity implements SurfaceHolder.Callback,
        Camera.AutoFocusCallback, Camera.PreviewCallback, View.OnClickListener {
    private static final String TAG = "CameraActivity";
    private Context context;
    private int mScreenWidth;
    private int mScreenHeight;
    private set_camera_view topView;
    private SurfaceHolder holder;
    private SurfaceView mSurfaceView = null;
    private Camera mCamera;
    private RelativeLayout rl_bk_sc;
    private TextView tv_desc;
    private ProgressBar PBar;
    private Button lightbutton;
    private String id_card_side;//身份证的正反面
    private  long opentime;
    private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedPictureSizes;
    private Camera.Parameters parameters;
    private String type;

    public Camera.Size mPictureSize;
    public Camera.Size previewSize=null;
    public Camera.Size mPreviewSize;
    public  boolean islighting =false;
    public String id_num;
    public String id_birth_year;
    public String id_minor;
    public String id_name;
    public String id_addr;
    public ScanTask mbackgroundtask;
    public  boolean backgroundtaskiscaneled=false;

    public android.hardware.Camera.AutoFocusCallback mAutoFocusCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);//隐藏标题栏实现全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        setContentView(R.layout.activity_camera);
        opentime = System.currentTimeMillis();
        context = this;

        Intent intent = getIntent();

        if (intent != null) {
            id_card_side = intent.getStringExtra("id_card_side");
            type = intent.getStringExtra("type");
        }

        getScreenMetrix(context);
        topView = new set_camera_view(context);
        initView();
        mAutoFocusCallback =new Camera.AutoFocusCallback()
        {
            public void onAutoFocus(boolean success,Camera camera)
            {
                if(success)
                {
                    mCamera.setPreviewCallbackWithBuffer(CameraActivity.this);
                    previewSize = mCamera.getParameters().getPreviewSize();
                    mCamera.addCallbackBuffer(new byte[(( previewSize.width * previewSize.height) * ImageFormat.getBitsPerPixel(ImageFormat.NV21))*2]);//add缓存
                    Log.i(TAG,"cameraAutoFocus success");
                }
                else
                {
                    mCamera.autoFocus( mAutoFocusCallback);
                    Log.i(TAG,"AutoFocusCallback");
                }
            }
        };
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            try {
                mCamera = Camera.open();//开启相机
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e + ".");
                Toast.makeText(context, "需要获取相机权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged");

        if (mCamera != null) {
            setCameraParams(mCamera, mScreenWidth, mScreenHeight);
            mCamera.setPreviewCallbackWithBuffer(this);
            previewSize = mCamera.getParameters().getPreviewSize();
            mCamera.addCallbackBuffer(new byte[(( previewSize.width * previewSize.height) * ImageFormat.getBitsPerPixel(ImageFormat.NV21))*2 ]);//add缓存
            mCamera.startPreview();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceDestroyed");
        holder.removeCallback(this);

        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();//停止预览
            mCamera.release();//释放相机资源
            mCamera = null;
        }

        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success=" + success);
            System.out.println(success);
        }
    }

    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        rl_bk_sc = (RelativeLayout) findViewById(R.id.rl_bk_sc);
        //tv_desc = (TextView) findViewById(R.id.tv_desc);
        PBar=(ProgressBar)findViewById(R.id.pbar);
        lightbutton=(Button)findViewById(R.id.light) ;
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface_view);

        PBar.setVisibility(View.GONE);
        holder = mSurfaceView.getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型

       // if (!TextUtils.isEmpty(id_card_side)) {
        //    if (id_card_side.equals("ID_CARD_SIDE_FRONT")) {
          //      tv_desc.setText("请将二代身份证正面放入识别框中");
       //     } else {
        //        tv_desc.setText("请将二代身份证反面放入识别框中");
       //     }
      //  } else {
     //       tv_desc.setText("请将二代身份证正面放入识别框中");
      //  }

        topView.draw(new Canvas());
        rl_bk_sc.setOnClickListener(this);
        lightbutton.setOnClickListener(this);
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
//        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
//        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
//        }
//        /**从列表中选取合适的分辨率*/
//        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
//        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
//            picSize = parameters.getPictureSize();
//        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
//        // 根据选出的PictureSize重新设置SurfaceView大小
//        float w = picSize.width;
//        float h = picSize.height;
//        parameters.setPictureSize(picSize.width, picSize.height);
//        mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));
//
//        // 获取摄像头支持的PreviewSize列表
//        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
//
//        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
//        }
//        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
//        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
//            parameters.setPreviewSize(preSize.width, preSize.height);
//        }

        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

        if (mSupportedPreviewSizes != null) {
            // 需要宽高切换 因为相机有90度的角度
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height, width);
            Log.e(TAG, "Preview mPreviewSize w - h : " + mPreviewSize.width + " - " + mPreviewSize.height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height, width);
            Log.e(TAG, "Preview mPictureSize w - h : " + mPictureSize.width + " - " + mPictureSize.height);
        }

        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);


        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }
    /**
     * 打开闪光灯
     */
    public synchronized void openLight() {
        Log.e(TAG, "openLight");
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 关闭闪光灯
     */
    public synchronized void offLight() {
        Log.e(TAG, "offLight");
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        }
    }
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        mCamera.addCallbackBuffer(data);
        mbackgroundtask=new ScanTask(data);
        mbackgroundtask.execute((Void)null);
        if(null!= mbackgroundtask)
        {
            switch(mbackgroundtask.getStatus())
            {
                case RUNNING:
                    return;
                case PENDING:
                    mbackgroundtask.cancel(false);
                    break;
            }

        }

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bk_sc:
                finish();
                break;
            case R.id.light:
                long nowtime = System.currentTimeMillis();

                if(!islighting && nowtime-opentime>1000)
                {
                    islighting=true;
                    openLight();
                }
                else
                {
                    islighting=false;
                    offLight();
                }
            default:
                break;
        }
    }
    @Override
    protected void onPause() {
        //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
        if( mbackgroundtask!=null && mbackgroundtask.getStatus() == AsyncTask.Status.RUNNING){
            mbackgroundtask.cancel(true);
           backgroundtaskiscaneled=true;

        }
        super.onPause();
    }



    private  class ScanTask extends AsyncTask<Void,Integer,Void>//背景任务刷新，在新的子线程进行
    {
        private byte[] mData;
        ScanTask(byte[] data)
        {
            mData=data;
        }

        @Override
        protected  Void doInBackground(Void...params)
        {
            if(backgroundtaskiscaneled) {
                return null;
            }
            else {

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File externalFile = getExternalFilesDir("/idcard/");
                    String filePath = externalFile.getAbsolutePath();
                    String fileName = System.currentTimeMillis() + "user.jpg";

                    //处理data
                    ByteArrayOutputStream baos;
                    byte[] rawImage;
                    Bitmap bitmap;
                    if (previewSize == null) {
                        previewSize = mCamera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                    }

                    BitmapFactory.Options newOpts = new BitmapFactory.Options();
                    newOpts.inJustDecodeBounds = true;

                    YuvImage yuvimage = new YuvImage(
                            mData,
                            ImageFormat.NV21,
                            previewSize.width,
                            previewSize.height,
                            null);
                    baos = new ByteArrayOutputStream();
                    yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
                    rawImage = baos.toByteArray();
                    //将rawImage转换成bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
                    if (mData == null || bitmap == null) {
                        Log.d(TAG, "There is no data!!");
                        return null;
                    } else {
                        // 根据拍照所得的数据创建位图 识别区域的身份证完整图像
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();
                        final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, (width - height) / 2, height / 6, height, height * 2 / 3);

                        int x, y, w, h;
                        int bitmap_width = bitmap1.getWidth(), bitmap_height = bitmap1.getHeight();
                        //剪裁出身份证号的图像并识别
                        x = (int) (bitmap_width * 0.340);
                        y = (int) (bitmap_height * 0.800);
                        w = (int) (bitmap_width * 0.6 + 0.5f);
                        h = (int) (bitmap_height * 0.12 + 0.5f);
                        Bitmap bit_id_num = Bitmap.createBitmap(bitmap1, x, y, w, h);
                        id_num = useful_functions.DoOcr(bit_id_num, "id_num");

                        //剪裁出生日年份并识别
                        x = (int) (bitmap_width * 0.170);
                        y = (int) (bitmap_height * 0.380);
                        w = (int) (bitmap_width * 0.13 + 0.5f);
                        h = (int) (bitmap_height * 0.10 + 0.5f);
                        Bitmap bit_id_birth_year = Bitmap.createBitmap(bitmap1, x, y, w, h);
                        id_birth_year = useful_functions.DoOcr(bit_id_birth_year, "birth");


                        if (useful_functions.is_id_num(id_num) && useful_functions.is_birth_year(id_birth_year)) {
                            //Intent i = new Intent();
                            //i.putExtra("id", id);
                            // setResult(RESULT_OK, i);
                            // onBackPressed();
                            publishProgress(20);

                            useful_functions.save_bitmap(bitmap1, filePath + fileName);
                            //裁剪出民族识别
                            x = (int) (bitmap_width * 0.400);
                            y = (int) (bitmap_height * 0.240);
                            w = (int) (bitmap_width * 0.20 + 0.5f);
                            h = (int) (bitmap_height * 0.11 + 0.5f);
                            Bitmap bit_id_minor = Bitmap.createBitmap(bitmap1, x, y, w, h);
                            id_minor = useful_functions.DoOcr(bit_id_minor, "minority");
                            if(id_minor!=null)
                            {
                                publishProgress(50);
                             //裁剪出姓名识别
                                x = (int) (bitmap_width * 0.170);
                                y = (int) (bitmap_height * 0.110);
                                w = (int) (bitmap_width * 0.20 + 0.5f);
                                h = (int) (bitmap_height * 0.12 + 0.5f);
                                Bitmap bit_id_name = Bitmap.createBitmap(bitmap1, x, y, w, h);
                                id_name = useful_functions.DoOcr(bit_id_name, "name");
                                if(id_name!=null)
                                {
                                    //裁剪出住址识别
                                    x = (int) (bitmap_width * 0.170);
                                    y = (int) (bitmap_height * 0.500);
                                    w = (int) (bitmap_width * 0.46 + 0.5f);
                                    h = (int) (bitmap_height * 0.30 + 0.5f);
                                    Bitmap bit_id_addr = Bitmap.createBitmap(bitmap1, x, y, w, h);
                                    bit_id_addr=remove_background(bit_id_addr);
                                    id_addr = useful_functions.DoOcr(bit_id_addr,"default");
                                    publishProgress(90);

                                    //裁剪出人像并本地缓存
                                    x = (int) (bitmap_width * 0.630);
                                    y = (int) (bitmap_height * 0.140);
                                    w = (int) (bitmap_width * 0.34 + 0.5f);
                                    h = (int) (bitmap_height * 0.62 + 0.5f);
                                    Bitmap id_head = Bitmap.createBitmap(bitmap1, x, y, w, h);
                                    useful_functions.save_bitmap(id_head,filePath+"cache");
                                    publishProgress(100);

                                    //启动展示界面
                                    Intent intent = new Intent(CameraActivity.this, DisplayActivity.class);
                                    intent.putExtra("id_card_side", "ID_CARD_SIDE_FRONT");
                                    intent.putExtra("id_num", id_num);
                                    intent.putExtra("id_minor", id_minor);
                                    intent.putExtra("id_name", id_name);
                                    intent.putExtra("id_addr",id_addr);
                                    intent.putExtra("id_headpath",filePath);

                                    startActivity(intent);
                                    Log.d(TAG, "doInBackground:result is" + id_num);
                                    mbackgroundtask.cancel(true);
                                    backgroundtaskiscaneled = true;
                                    finish();
                                }
                                else
                                {
                                    return null;
                                }
                            }
                            else
                            {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
//
                } else {
                    return null;
                }
            }
            return  null;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            if(values[0]==20)
            {
                PBar.setVisibility(View.VISIBLE);
            }
            if(values[0]>=20)
            {
                PBar.setProgress(values[0]);
            }

        }
    }
    public  Bitmap  remove_background(Bitmap inputpic)
    {
        Mat inputmat =new Mat();
        Utils.bitmapToMat(inputpic,inputmat);
        Imgproc.threshold(inputmat,inputmat,128,255,Imgproc.THRESH_BINARY);
        Utils.matToBitmap(inputmat,inputpic);
        return inputpic;
    }

}
