package com.example.wdy.idcardnew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wdy.idcardnew.function.useful_functions;

public class DisplayActivity extends AppCompatActivity implements View.OnClickListener {
    public String id_card_side;
    public String id_num;
    public String id_sex;
    public String id_birth;
    public String id_minor;
    public String id_name;
    public String id_addr;
    public String id_headpath;
    public Bitmap Bm_id_head;

    public  TextView tv_idnum;
    public  TextView tv_birth;
    public  TextView tv_sex;
    public  TextView tv_minor;
    public  TextView tv_name;
    public TextView  tv_addr;
    public ImageView iv_head;
    public Button btn_cancel;
    public  Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        if (intent != null)
        {
            id_card_side = intent.getStringExtra("id_card_side");
            id_num = intent.getStringExtra("id_num");
            id_minor = intent.getStringExtra("id_minor");
            id_name =intent.getStringExtra("id_name");
            id_addr=intent.getStringExtra("id_addr");
            id_headpath=intent.getStringExtra("id_headpath");
        }
        init_UI();

    }
    private   void init_UI()
    {
        tv_idnum=(TextView)findViewById(R.id.tv_idnum_dis);
        tv_birth=(TextView)findViewById(R.id.tv_birth_dis);
        tv_sex=(TextView)findViewById(R.id.tv_sex_dis);
        tv_minor=(TextView)findViewById(R.id.tv_minor_dis);
        tv_name=(TextView)findViewById(R.id.tv_name_dis);
        tv_addr=(TextView)findViewById(R.id.tv_addr_dis);
        iv_head=(ImageView)findViewById(R.id.iv_head) ;
        btn_cancel=(Button)findViewById(R.id.buttonleft);
        btn_save=(Button)findViewById(R.id.buttonright);

        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        //读取本地缓存头像
        Bm_id_head= BitmapFactory.decodeFile(id_headpath+"cache");

        //通过身份证号码实现性别判断
        id_sex=useful_functions.justice_sex(id_num);

        //通过身份证号截取出出生日期
        id_birth=id_num.substring(6,10)+"年"+remove0(id_num.substring(10,11))+id_num.substring(11,12)+"月"+remove0(id_num.substring(12,13))+id_num.substring(13,14)+"日";

        tv_idnum.setText(id_num);
        tv_sex.setText(id_sex);
        tv_birth.setText(id_birth);
        tv_minor.setText(id_minor);
        tv_name.setText(id_name);
        tv_addr.setText(id_addr);
        iv_head.setImageBitmap(Bm_id_head);


    }
    public  String remove0(String str)
    {
        if(str.equals("0"))
        {
            str=" ";
            return str;
        }
        else
        {
            return str;
        }
    }

    @Override
     public void onClick(View view)
    {
        switch (view.getId())
        {
            case (R.id.buttonleft):
                Intent intent = new Intent(DisplayActivity.this, CameraActivity.class);
                intent.putExtra("id_card_side", "ID_CARD_SIDE_FRONT");
                intent.putExtra("type", "idcardFront");
                startActivity(intent);

                finish();
            break;
            case(R.id.buttonright):
                useful_functions.Save2txt(id_headpath+"result.txt","姓名："+id_name);
                useful_functions.Save2txt(id_headpath+"result.txt","民族："+id_minor);
                useful_functions.Save2txt(id_headpath+"result.txt","出身日期："+id_birth);
                useful_functions.Save2txt(id_headpath+"result.txt","身份证号码："+id_num);
                useful_functions.Save2txt(id_headpath+"result.txt","住址："+id_addr);
                Toast.makeText(this,"文件已成功保存到"+id_headpath+"result.txt !",Toast.LENGTH_LONG).show();
                finish();
                break;
                default:
                    break;

        }
    }
}
