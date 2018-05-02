package com.example.wdy.idcardnew.function;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by wdy19 on 2018/1/31.
 */

public class set_camera_view extends View{


    private int viewWidth;
    private int viewHeight;

    public int rectWidth;
    public int rectHeght;


    private int rectTop;
    private int rectLeft;
    private int rectRight;
    private int rectBottom;


    private int lineLen;

    private static final int LINE_WIDTH = 5;



    private Paint linePaint;
    private Paint linePaint2;
    private Rect rect;


    public set_camera_view(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        Activity activity = (Activity) context;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        viewWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        viewHeight= wm.getDefaultDisplay().getHeight();//拿到屏幕的高
             //viewHeight,界面的高,viewWidth,界面的宽
        //高度不需要dp转换px,不然整体相机会向上移动一小节
//        viewHeight = panelHeght - (int) DisplayUtil.dp2px(activity,TOP_BAR_HEIGHT + BOTTOM_BTN_HEIGHT);


        Log.i("message_i", "viewHeight:" + viewHeight + "," + "viewWidth:" + viewWidth);

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);
//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);

        rectWidth = viewWidth * 2 / 3;
        rectHeght = viewWidth;

        Log.d("message_i", "rectWidth:" + rectWidth + "," + "rectHeght:" + rectHeght);

        // 相对于此view
//        rectTop = (viewHeight - rectHeght) / 2;
//        rectLeft = (viewWidth - rectWidth) / 2;
//        rectBottom = rectTop + rectHeght;
//        rectRight = rectLeft + rectWidth;

        rectTop = (viewHeight - viewWidth) / 2;
        rectLeft = viewWidth / 6;
        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;

        //rectTop:636,rectLeft:26,rectBottom:1283,rectRight:1053
        Log.v("message_i", "rectTop:" + rectTop + "," + "rectLeft:" + rectLeft + "," + "rectBottom:" + rectBottom
                + "," + "rectRight:" + rectRight);

        lineLen = viewWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        linePaint2 = new Paint();
        linePaint2.setAntiAlias(true);
        linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint2.setStrokeWidth(3);
        linePaint2.setTextSize(35);


    }

    public set_camera_view(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        Activity activity = (Activity) context;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        viewWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        viewHeight = wm.getDefaultDisplay().getHeight();//拿到屏幕的高

        //高度不需要dp转换px,不然整体相机会向上移动一小节
//        viewHeight = panelHeght - (int) DisplayUtil.dp2px(activity,TOP_BAR_HEIGHT + BOTTOM_BTN_HEIGHT);


        Log.d("message_i", "------------------------------------------------------------------------------");
        Log.i("message_i", "viewHeight:" + viewHeight + "," + "viewWidth:" + viewWidth);
        //viewHeight:1920,viewWidth:1080

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

//        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity, LEFT_PADDING + RIGHT_PADDING);
//        rectHeght = (int) (rectWidth * 3 / 2);

        rectWidth = viewWidth * 2 / 3;
        rectHeght = viewWidth;

        Log.d("message_i", "rectWidth:" + rectWidth + "," + "rectHeght:" + rectHeght);
        //rectWidth:1027,rectHeght:647

        // 相对于此view
//        rectTop = (viewHeight - rectHeght) / 2;
//        rectLeft = (viewWidth - rectWidth) / 2;
//        rectBottom = rectTop + rectHeght;
//        rectRight = rectLeft + rectWidth;

        //width:1920 height:1080  viewHeight:1920,viewWidth:1080
        //(x,y,width,height) (width - height) / 2, height / 6, height, height * 2 / 3
        rectTop = (viewHeight - viewWidth) / 2;
        rectLeft = viewWidth / 6;
        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;

        Log.i("message_i", "rectTop:" + rectTop + "," + "rectLeft:" + rectLeft + "," + "rectBottom:" + rectBottom
                + "," + "rectRight:" + rectRight);
        //rectTop:636,rectLeft:26,rectBottom:1283,rectRight:1053

        //rectTop:146,rectLeft:26,rectBottom:1773,rectRight:1053

        lineLen = viewWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        linePaint2 = new Paint();
        linePaint2.setAntiAlias(true);
        linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint2.setStrokeWidth(3);
        linePaint2.setTextSize(35);

        //rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
       // Paint.FontMetricsInt fontMetrics = linePaint2.getFontMetricsInt();
       // baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        //linePaint2.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画蒙层
        //linePaint2.setColor(Color.TRANSPARENT);
        //canvas.drawRect(rect, linePaint2);
        linePaint2.setColor(0xa0000000);//颜色代码 前两位表示透明度
        rect = new Rect(0, viewHeight / 2 + rectHeght / 2, viewWidth, viewHeight);
        canvas.drawRect(rect, linePaint2);

        rect = new Rect(0, 0, viewWidth, viewHeight / 2 - rectHeght / 2);
        canvas.drawRect(rect, linePaint2);

        rect = new Rect(0, viewHeight / 2 - rectHeght / 2, (viewWidth - rectWidth) / 2, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, linePaint2);

        rect = new Rect(viewWidth - (viewWidth - rectWidth) / 2, viewHeight / 2 - rectHeght / 2, viewWidth, viewHeight / 2 + rectHeght / 2);
        canvas.drawRect(rect, linePaint2);

        //重制rect  并画文字  把文字置于rect中间
        //rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);



//
        canvas.drawLine(rectLeft, rectTop, rectLeft + lineLen, rectTop,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectTop, rectRight, rectTop,
                linePaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectRight, rectTop, rectRight, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom, rectLeft + lineLen, rectBottom,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectBottom, rectRight, rectBottom,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom - lineLen, rectLeft, rectBottom,
                linePaint);
        canvas.drawLine(rectRight, rectBottom - lineLen, rectRight, rectBottom,
                linePaint);
         // 1080*1920  -》   rectWidth:720,rectHeght:1080
       int x ,y ,w,h;
        //绘制身份证号的矩形框
       /* x=(int) (rectHeght* 0.340);
        y=(int) ( rectWidth * 0.800);
        w=(int) (rectHeght * 0.6 + 0.5f);
        h=(int) ( rectWidth* 0.12 + 0.5f);
        Log.d("Rect", "Rect_num: "+"x="+x +"  "+"y="+y+"   "+"w="+w+"   "+"h="+h);
        //1080*1920-》x=367  y=576   w=648   h=86
        rect = new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);
        //绘制身份证出生日期框
        x=(int) (rectHeght* 0.170);
        y=(int) ( rectWidth * 0.380);
        w=(int) (rectHeght * 0.13 + 0.5f);
        h=(int) ( rectWidth* 0.10 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);
        //绘制性别框
        x=(int) (rectHeght* 0.170);
        y=(int) ( rectWidth * 0.240);
        w=(int) (rectHeght * 0.10 + 0.5f);
        h=(int) ( rectWidth* 0.13 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);

        //绘制姓名框
        x=(int) (rectHeght* 0.170);
        y=(int) ( rectWidth * 0.110);
        w=(int) (rectHeght * 0.20 + 0.5f);
        h=(int) ( rectWidth* 0.12 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);

        //绘制民族框
        x=(int) (rectHeght* 0.400);
        y=(int) ( rectWidth * 0.240);
        w=(int) (rectHeght * 0.20 + 0.5f);
        h=(int) ( rectWidth* 0.11 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);

        //绘制住址框
        x=(int) (rectHeght* 0.170);
        y=(int) ( rectWidth * 0.500);
        w=(int) (rectHeght * 0.46 + 0.5f);
        h=(int) ( rectWidth* 0.30 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);*/

        //绘制人像框
        x=(int) (rectHeght* 0.630);
        y=(int) ( rectWidth * 0.140);
        w=(int) (rectHeght * 0.34 + 0.5f);
        h=(int) ( rectWidth* 0.62 + 0.5f);
        rect= new Rect(rectRight-y-h,rectTop+x,rectRight-y,rectTop+x+w);
        canvas.drawRect(rect,linePaint);
        //绘制文字
        canvas.saveLayer(0, 0,1000 ,2000, linePaint2, Canvas.ALL_SAVE_FLAG);//新图层
        linePaint2.setColor(Color.WHITE);
        linePaint2.setTextAlign(Paint.Align.CENTER);
        canvas.rotate(90, 40, viewHeight/2);//旋转画笔若再使用则旋转-90回去
        linePaint2.setTextSize(40);
        canvas.drawText("请将身份证正面放入框中并尽量使各部分对齐",40,viewHeight/2,linePaint2);
        canvas.restore();//保存新绘制的图层



    }


}
