package com.example.wdy.idcardnew.function;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.content.ContextWrapper;

import com.example.wdy.idcardnew.MainActivity;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wdy19 on 2018/2/25.
 */

public class useful_functions {
    private static final String TAG = "useful_functions";
    private static final String TESSBASE_PATH = Environment.getExternalStorageDirectory() + File.separator;
    private static final String DEFAULT_LANGUAGE = "eng";//英文数据包
    private static final String CHINESE_LANGUAGE = "chi_sim";//中文数据包
  static   public String  DoOcr (Bitmap ocrbitmap, String ocr_location)
    {
        TessBaseAPI baseApi = new TessBaseAPI();

        switch (ocr_location)
        {
            case "id_num":
                baseApi.init(TESSBASE_PATH,DEFAULT_LANGUAGE );
                baseApi.setVariable("tessedit_char_whitelist", "0123456789Xx");
                break;
            case"sex":
                baseApi.init(TESSBASE_PATH,CHINESE_LANGUAGE);
                baseApi.setVariable("tessedit_char_whitelist", "男女");
                break;
            case"birth":
                baseApi.init(TESSBASE_PATH,DEFAULT_LANGUAGE );
                baseApi.setVariable("tessedit_char_whitelist", "0123456789");
                break;
            case"minority":
                baseApi.init(TESSBASE_PATH,CHINESE_LANGUAGE);
                baseApi.setVariable("tessedit_char_whitelist", "汉壮满回苗维吾尔土家彝蒙古藏布依侗瑶朝鲜白哈尼哈萨克黎傣畲傈僳仡佬东乡高山拉祜水佤纳西羌土仫佬锡伯柯尔克孜达斡尔景颇毛南撒拉布朗塔吉克阿昌普米鄂温克怒京基诺德昂保安俄罗斯裕固乌孜别克门巴鄂伦春独龙塔塔尔赫哲珞巴族");
                break;
                default:
                    baseApi.init(TESSBASE_PATH,CHINESE_LANGUAGE);
                    baseApi.setVariable("tessedit_char_blacklist", "。，、＇：∶；?‘’“”〝〞ˆˇ﹕︰﹔﹖﹑·¨….¸;！´？！～—ˉ｜‖＂〃｀@﹫¡¿﹏﹋﹌︴々﹟#﹩$﹠&﹪%*﹡﹢﹦﹤‐￣¯―﹨ˆ˜﹍﹎+=<＿_-ˇ~﹉﹊（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}「」【】︵︷︿︹︽_﹁﹃︻︶︸﹀︺︾ˉ﹂﹄︼ ");
                    break;
        }
        ocrbitmap = ocrbitmap.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(ocrbitmap);
        String ocrtext = baseApi.getUTF8Text();
        baseApi.clear();
        Log.e(TAG, "识别结果：" + ocrtext );
        return ocrtext;
    }
    static public boolean is_id_num (String id_num)
    {
        if(id_num.length()==18){
        //运用正则表达式验证身份证号的逻辑
        Pattern pattern = Pattern.compile("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
        Matcher matcher = pattern.matcher(id_num);
        if(matcher.matches())
        {
            //通过计算校验最后一位
         int id_each_num[]=new int[18];
         for(int i=0;i<17;i++)
         {
             id_each_num[i] =Integer.parseInt(id_num.substring(i,i+1));
            // Log.i(TAG, "is_id_num: each_num"+(i+1)+"="+id_each_num[i]);
         }
         String last_idnum=id_num.substring(17,18);
         int last_num =0;
         //通过最后一位计算出应得余数
         if(last_idnum.equals("X")||last_idnum.equals("x"))
         {
             last_num = 2;
         }
         else
         {
             last_num=Integer.parseInt(last_idnum);
             if(last_num<2)
             {
                 last_num=1-last_num;
             }
             else
             {
                 last_num=12-last_num;
             }
         }
        //通过前17位加权计算出总和并除去11取余数得result
         int result = id_each_num[0]*7+id_each_num[1]*9+id_each_num[2]*10+id_each_num[3]*5+id_each_num[4]*8+id_each_num[5]*4+id_each_num[6]*2
                 +id_each_num[7]+id_each_num[8]*6+id_each_num[9]*3+id_each_num[10]*7+id_each_num[11]*9+id_each_num[12]*10+id_each_num[13]*5
                 +id_each_num[14]*8+id_each_num[15]*4+id_each_num[16]*2;
            Log.i(TAG, "is_id_num: sum ="+result);
         result=result%11;
            Log.i(TAG, "is_id_num: result="+result+"last_num="+last_num);
         if(result==last_num)
         {
             Log.i(TAG, "is_id_num: "+"is idcard numbers!");
             return  true;

         }
         else
             {
                 Log.i(TAG, "is_id_num: "+"is NOT idcard numbers!");
              return  false;
             }


        }
        else
        {
            return false;
        }
        }
        else
        {
            return  false;
        }

    }
    static public boolean is_birth_year(String birth_year)
    {
        if(birth_year.length()==4)
        {
            Pattern pattern = Pattern.compile("^(18|19|([23]\\d))\\d{2}$");
            Matcher matcher = pattern.matcher(birth_year);
            return  matcher.matches();
        }
        else
        {
            return  false;
        }
    }
   static public void  save_bitmap(Bitmap savebitmap,String Path)
    {
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(Path);//默认覆盖已有文件
            savebitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            // 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static  boolean copyAssetFile(Context context) throws Exception
    {

        String dir = TESSBASE_PATH + "tessdata";
        String filePath_eng = TESSBASE_PATH + "tessdata"+File.separator+"eng.traineddata";
        String filePath_chs =TESSBASE_PATH + "tessdata"+File.separator+"chi_sim.traineddata";
        File f = new File(dir);
        if (f.exists()) {
        } else {
            f.mkdirs();
        }
        AssetManager assetManager = context.getAssets();
        File dataFile_chs =new File(filePath_chs);
        File dataFile_eng = new File(filePath_eng);
        String [] cn_partfilelist = assetManager.list("cn");
        String [] eng_partfilelist =assetManager.list("eng");
        if (dataFile_eng.exists()&&dataFile_chs.exists()) {
            Log.e(TAG, "copyAssetFile:"+"both tessdata file is already existed!");
            return true;// 文件存在
        }
        else
        {
          if(!dataFile_eng.exists())
          {
             try
             {
                 mergeFile(context,eng_partfilelist,filePath_eng,true);
             }
             catch (IOException e)
             {
                 e.printStackTrace();
             }

              Log.e(TAG, "copyAssetFile:"+"eng_file is already copied &existed!");

          }
            if(!dataFile_chs.exists())
            {
                try
                {
                    mergeFile(context,cn_partfilelist,filePath_chs,false);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                Log.e(TAG, "copyAssetFile:"+"chs_file is already copied &existed!");
            }
        }
        return  true;
    }
    public static void mergeFile(Context c, String [] partFilelist, String dst,boolean is_eng)
            throws IOException
    {
        if (!new File(dst).exists() )
        {
            OutputStream out = new FileOutputStream(dst);
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            for(int i=0;i< partFilelist.length;i++){
                // 获得输入流 ,注意文件的路径
                if(is_eng)
                {
                    in = c.getAssets().open("eng/"+partFilelist[i]);
                    Log.e(TAG, "mergeFile: eng"+is_eng+partFilelist[i] );
                }else
                {
                    in = c.getAssets().open("cn/"+partFilelist[i]);
                }
                while((readLen = in.read(buffer)) != -1){
                    out.write(buffer, 0, readLen);
                }
                out.flush();
                in.close();
            }
            // 把所有小文件都进行写操作后才关闭输出流，这样就会合并为一个文件了
            out.close();
        }
            Log.e(TAG, "mergeFile: "+"write successful!!" );
        }

    public static  String justice_sex(String id_num)
    {
        int res;
        res=Integer.parseInt(id_num.substring(16,17));
        res=res%2;
        if(res==0)
        {
            return "女";
        }
        else
        {
            return "男";
        }
    }
    public static void Save2txt(String filepath,String msg) {
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(filepath, true));
            bfw.write(msg);
            bfw.newLine();//换行
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
