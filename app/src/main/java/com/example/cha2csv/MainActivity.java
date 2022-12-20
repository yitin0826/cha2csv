package com.example.cha2csv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.androidexception.filepicker.dialog.SingleFilePickerDialog;

public class MainActivity extends AppCompatActivity {

    private TextView txt_lp4file,txt_result;
    private Button btn_upload,btn_conversion;
    private String filePath,path,time1,time2;
    private String myData = "";
    public char[] a;
    private int cha_size,x,startS,pic,picend;
    private double dou,dou_2;
    private ArrayList be,samp2;
    private ArrayList CHA_LI_datanum,CHA_LI_data16,CHA_LI_datasamp,CHA_LI_data4;
    private ArrayList CHA_LII_datanum,CHA_LII_data16,CHA_LII_datasamp,CHA_LII_data4;
    private byte[] content,c1,c2;
    private Celldata cell;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private Date begin,end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        initParameter();
    }

    private void initParameter(){
        txt_lp4file = findViewById(R.id.txt_lp4file);
        txt_result = findViewById(R.id.txt_result);
        btn_upload = findViewById(R.id.btn_upload);
        btn_conversion = findViewById(R.id.btn_conversion);
        btn_upload.setOnClickListener(lis);
        btn_conversion.setOnClickListener(lis);
    }

    private boolean permissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_upload:
                    if(permissionGranted()) {
                        SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(MainActivity.this,
                                () -> txt_lp4file.setText("Canceled!!"),
                                files -> getPath(files[0].getPath()));
                        singleFilePickerDialog.show();
                    }
                    else{
                        requestPermission();
                    }
                    break;
                case R.id.btn_conversion:
                    begin = new Date(System.currentTimeMillis());
                    time1 = sdf.format(begin);
                    readCHA();
                    double diff = end.getTime() - begin.getTime();
                    double seconds = (diff / (1000));
                    txt_result.setText("end="+time2+"\n"+"begin:"+time1+"\n"+seconds);
                    break;
            }
        }
    };

    private void getPath(String fileName){
        if(fileName != ""){
            txt_lp4file.setText((fileName));
            filePath = fileName;
        }
        File file = new File(filePath);
        path = file.getPath();
        Log.d("path",path);
    }

    private void readCHA(){
        x = 64;
        a = new char[32*1024*1024];
        cha_size = LcndUtil.len(filePath,a);
        content = LcndUtil.readFromByteFile(filePath);
        Log.d("cha_size",String.valueOf(cha_size));
        Log.d("content_len",String.valueOf(content.length));
        /** Lead1 **/
        CHA_LI_datanum = new ArrayList();
        CHA_LI_data16 = new ArrayList();
        CHA_LI_datasamp = new ArrayList();
        CHA_LI_data4 = new ArrayList();
        c1 = new byte[cha_size];
        for (int L1=0; L1<cha_size; L1+=136){
            int y = L1+x;
            int j = 0;
            for (int k=L1; k<y; k++){
                c1[j] = content[k];
                j++;
            }
            onecelldata(c1,c1.length,1);
            CHA_LI_datanum.add(cell.getLen());
            CHA_LI_data16.add(cell.getList(1));
            CHA_LI_datasamp.add(cell.getList(2));
            CHA_LI_data4.add(cell.getList(3));
        }
        startS = 0;
        dou = Math.floor((startS*1000)/32);
        pic = (int) dou;
        double samp_len = CHA_LI_datasamp.size();
        dou_2 = (samp_len/32)*31-5;
        picend = (int) dou_2;

        /** Lead2 **/
        CHA_LII_datanum = new ArrayList();
        CHA_LII_data16 = new ArrayList();
        CHA_LII_datasamp = new ArrayList();
        CHA_LII_data4 = new ArrayList();
        be = new ArrayList();
        samp2 = new ArrayList();
        c2 = new byte[cha_size];
        for (int L2=64; L2<cha_size; L2+=136){
            int y = L2+x;
            int j = 0;
            for (int k=L2; k<y; k++){
                c2[j] = content[k];
                j++;
            }
            onecelldata(c2,c2.length,2);
            CHA_LII_datanum.add(cell.getLen());
            CHA_LII_data16.add(cell.getList(1));
            CHA_LII_datasamp.add(cell.getList(2));
            CHA_LII_data4.add(cell.getList(3));
        }
        for(int i=pic; i<picend; i++){
            List s = Arrays.asList(CHA_LII_datasamp.get(i));
            String f = s.get(0).toString().substring(1,191).replaceAll(" ","");
            List<String> myList = new ArrayList<>(Arrays.asList(f.split(",")));
            samp2.addAll(myList);
        }
        for(int i=0; i<samp2.size(); i++){
            int k = Integer.parseInt(samp2.get(i).toString());
            samp2.set(i,((k-2048)*5)*0.001);
        }

        makeCSV();
        end = new Date(System.currentTimeMillis());
        time2 = sdf.format(end);
    }

    public void onecelldata(byte[] cha, int len, int c){
        ArrayList<String> onecell = new ArrayList();
        for(int i=0; i<64; i++){
            String newcha = Integer.toBinaryString(cha[i]);
            int wordnum = newcha.length();
            if (wordnum != 8){
                for(int k=0; k<(8-wordnum); k++){
                    String x = '0'+newcha;
                    newcha = x;
                }
                if(wordnum > 8){
                    String x = newcha.substring(24);
                    newcha = x;
                }
            }
            else{
                newcha = Integer.toBinaryString(cha[i]);
                String x = newcha.substring(0,8);
                newcha = x;
            }
            onecell.add(newcha);
        }

        ArrayList<String> newcell = new ArrayList<>();
        for(int i=0; i<64; i+=2){
            String low = onecell.get(i);
            String hi = onecell.get(i+1);
            String new16 = hi+low;
            newcell.add(new16);
        }

        ArrayList<Integer> spv = new ArrayList<>();
        ArrayList<String> datav = new ArrayList<>();
        for(int i=0; i<32; i++){
            String sp = newcell.get(i).substring(4,16);
            String dat = newcell.get(i).substring(0,4);
            int x = Integer.parseInt(sp,2);
            spv.add(x);
            datav.add(dat);
        }
        cell = new Celldata(len,newcell,spv,datav);
    }

    private void makeCSV(){
        new Thread(() ->{
            String date = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).format(System.currentTimeMillis());
            String fileName = "[" + date + "].csv";
            String[] title = {"Lead2"};
            StringBuffer csvText = new StringBuffer();
            for (int i = 0; i < title.length; i++) {
                csvText.append(title[i] + ",");
            }
            for (int i = 0; i < samp2.size(); i++) {
                csvText.append("\n" + samp2.get(i));
            }

            Log.d("CSV", "makeCSV: " + csvText);
            runOnUiThread(() -> {
                try {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    builder.detectFileUriExposure();
                    FileOutputStream out = openFileOutput(fileName, Context.MODE_PRIVATE);
                    out.write((csvText.toString().getBytes()));
                    out.close();
                    File fileLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);
                    FileOutputStream fos = new FileOutputStream(fileLocation);
                    fos.write(csvText.toString().getBytes());
                    Uri path = Uri.fromFile(fileLocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    Log.d("location", "makeCSV: " + fileLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }
}