package com.xqli.pluginapk;


import android.content.Context;
import android.widget.Toast;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public void write(){
        System.out.println();
        System.err.println();
    }

    public void print(Context context, String name) {
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
    }
}