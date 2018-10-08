package com.xqli.pluginapk;

import android.util.Log;
import android.content.Context;
import android.widget.Toast;


public class Utils {

    private static final String TAG = "Utils";

    /**
     * 计算 a+b
     *
     * @param context
     * @param a
     * @param b
     * @param name
     */
    public int printSum(Context context,int a,int b,String name){
        int sum = a + b;
        Toast.makeText(context, name+":"+sum, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "printSum: ");
        return sum;
    }

    public void printFileName(Context context,String name){
        new FileUtils().print(context,name);
    }

}