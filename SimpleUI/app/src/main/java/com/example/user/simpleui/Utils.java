package com.example.user.simpleui;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 2016/6/20.
 */
public class Utils {
    public static void writeFile(Context context, String fileName, String content)
    {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String fileName)
    {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer, 0, buffer.length);
            fis.close();
            String string = new String(buffer);
            return string;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
