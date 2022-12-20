package com.example.cha2csv;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LcndUtil {
    public static int len (String path, char[] a){
        File f = new File(path);
        try {
            FileInputStream fs = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fs);
            InputStreamReader isr = new InputStreamReader(in,"ISO-8859-1");
            BufferedReader br = new BufferedReader(isr);
            int length;
            length = br.read(a,0,32*1024*1024);
            br.close();
            isr.close();
            in.close();
            fs.close();
            return length;
        }catch (IOException e){
            e.printStackTrace();
        }
        return 0;
    }

    public static byte[] readFromByteFile(String pathname){
        File filename = new File(pathname);
        try{
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while((size = in.read(temp)) != -1){
                out.write(temp, 0, size);
            }
            in.close();
            byte[] content = out.toByteArray();
            return content;
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] a = new byte[1024];
        return a;
    }

    public static ArrayList bytes2HexString(byte[] b) {
        ArrayList<String> str_16 = new ArrayList();
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str_16.add(hex.toUpperCase());
        }

        return str_16;
    }
}
