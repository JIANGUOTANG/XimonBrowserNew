package jian.com.ad.db;

import android.content.Context;
import android.content.SharedPreferences;

import jian.com.ad.Constans;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ying on 17-11-23.
 */

public class SharePeferenceUtil {

    public static int getCodeNumberId(Context context){
        //获取本地保存的唯一标志码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constans.DBNAME,MODE_PRIVATE);
        int code_number = sharedPreferences.getInt(Constans.CODE_NUMBERID,0);
        return code_number;

    }

    public static void setCodeNumberId(Context context,int codeNumber){
        //设置唯一标识码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constans.DBNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constans.CODE_NUMBERID,codeNumber);
        editor.commit();
    }
    public static void setCodeNumber(Context context,String codeNumber){
        //设置唯一标识码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constans.DBNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constans.CODE_NUMBER,codeNumber);
        editor.commit();
    }

    public static String getCodeNumber(Context context){
        //获取本地保存的唯一标志码
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constans.DBNAME,MODE_PRIVATE);
        String code_number = sharedPreferences.getString(Constans.CODE_NUMBER,"");
        return code_number;

    }


}
