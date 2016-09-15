package com.witel.firzan.mss.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.witel.firzan.mss.app.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Firzan on 25/05/2016.
 */
public class CustomGlobal {

    private static String today="";
    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
    public static void checkDirectories(){

        File profile = new File(Environment.getExternalStorageDirectory(),
                Config.PROFILE);
        File docs = new File(Environment.getExternalStorageDirectory(),
                Config.DOCS);
        File images = new File(Environment.getExternalStorageDirectory(),
                Config.IMAGES);
        File files = new File(Environment.getExternalStorageDirectory(),
                Config.FILES);
        File tt= new File(Environment.getExternalStorageDirectory(),
                Config.TIME_TABLE);
        File pta = new File(Environment.getExternalStorageDirectory(),
                Config.PTA);
        File holiday = new File(Environment.getExternalStorageDirectory(),
                Config.HOLIDAYS);
        if (!profile.exists()){
            profile.mkdirs();
        }
        if (!docs.exists()){
            docs.mkdirs();
        }
        if (!images.exists()){
            images.mkdirs();
        }
        if (!files.exists()){
            files.mkdirs();
        }
        if (!tt.exists()){
            tt.mkdirs();
        }
        if (!pta.exists()){
            pta.mkdirs();
        }
        if (!holiday.exists()){
            holiday.mkdirs();
        }
    }
    public static String getFileName(){
        Random random = new Random();
        //random.nextInt(max - min + 1) + min
        int randomNum = random.nextInt((90000 - 999) + 1) + 999;
        return System.currentTimeMillis() + "_" + randomNum;
    }

    //get ACTION INTENT VIEW
    public static String getApplicationType(String url){
        url.toLowerCase();
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            return  "application/msword";
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            return "application/pdf";
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            return  "application/vnd.ms-powerpoint";
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            return  "application/vnd.ms-excel";
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            return  "application/x-wav";
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            return "application/rtf";
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            return  "audio/x-wav";
        } else if(url.toString().contains(".gif")) {
            // GIF file
            return  "image/gif";
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            return  "image/jpeg";
        } else if(url.toString().contains(".txt")) {
            // Text file
            return  "text/plain";
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            return "video/*";
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            //intent.setDataAndType(uri, "*/*");
        }
        return "application/*";
    }

    public static Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    public static File getTempFile() {
        if (isSDCARDMounted()) {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "/MssApp/tmp/");
            if (!file.exists()) {
                file.mkdirs();
            }
            File f = new File(Environment.getExternalStorageDirectory(),
                    "/MssApp/tmp/tmp.jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            return f;
        } else {
            return null;
        }
    }

    public static File getRealFile(String imgName,String dir){
        if (isSDCARDMounted()){
            File file = new File(Environment.getExternalStorageDirectory(),
                    dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File f = new File(Environment.getExternalStorageDirectory(),
                    dir+imgName);
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            return f;
        }else{
            return null;
        }
    }

    private static boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static String getStringByteSize(long size)
    {
        if(size > 1024 * 1024)  //mega
        {
            return String.format("%.1f MB", size / (float)(1024 * 1024));
        }
        else if(size > 1024)  //kilo
        {
            return String.format("%.1f KB", size / 1024.0f);
        }
        else
        {
            return String.format("%d B",size);
        }
    }

    public static boolean checkServiceAvailable(Context ctx){
        PackageManager pm = ctx.getPackageManager();
        //final boolean deviceHasCameraFlag =
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }
    public static void hideKeyboard(Activity activity) {
        System.out.println("called");
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}

