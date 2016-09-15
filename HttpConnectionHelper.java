package com.witel.firzan.mss.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


public class HttpConnectionHelper{

    private static final int BUFFER_SIZE = 1024;

    public static boolean isNetworkOn(Context context){
        ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static String doConnection(String url, Map<String, String> mapParams) {

        String response = null;

        try {
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("charset", "UTF-8");
            httpURLConnection.setRequestProperty("accept", "application/json");
            if (mapParams != null){
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(getEncodedPostString(mapParams));
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }else{
                httpURLConnection.setRequestMethod("GET");
            }
            httpURLConnection.connect();
            int statusCode = httpURLConnection.getResponseCode();
            //System.out.println("Status code network helper : " + statusCode);

            InputStream inputStream = null;
            if (statusCode == 200){
                inputStream = httpURLConnection.getInputStream();
            } else{
                inputStream = httpURLConnection.getErrorStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder resultBulider = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                resultBulider.append(line);
            }

            response = resultBulider.toString();
            //System.out.println("Response : " + response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            return "TIMEOUT";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getEncodedPostString(Map<String, String> params) throws UnsupportedEncodingException {

        StringBuilder resultStrBuilder = new StringBuilder();
        boolean firstIteration = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (firstIteration) {
                firstIteration = false;
            } else {
                resultStrBuilder.append("&");
            }
            resultStrBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            resultStrBuilder.append("=");
            resultStrBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return resultStrBuilder.toString();
    }

    public static boolean DownloadFile(final Map map){

        final boolean[] flag_ = {true};
        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl=null,file_name=null;
                httpUrl=map.get("file").toString();//http url
                file_name=map.get("file_name").toString();
                String dir=map.get("dir").toString();

                if(httpUrl.length()<4){
                    return;
                }

                File output= new File(Environment.getExternalStorageDirectory(),dir+file_name);

                System.out.println(httpUrl);
                System.out.println(file_name);
                if (output.exists()){
                    output.delete();
                }
                HttpURLConnection httpConn = null;
                try {
                    URL url = new URL(httpUrl);
                    httpConn = (HttpURLConnection) url
                            .openConnection();
                    int responseCode = httpConn.getResponseCode();
                    // always check HTTP response code first
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        InputStream inputStream = httpConn.getInputStream();
                        // String saveFilePath = saveDir + File.separator + fileName;

                        // opens an output stream to save into file
                        FileOutputStream outputStream = null;

                        outputStream = new FileOutputStream(output.getPath());

                        int bytesRead = -1;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();

                    } else {
                        System.out
                                .println("No file to download. Server replied HTTP code: "
                                        + responseCode);

                    }
                } catch (FileNotFoundException e){
                    if (output.exists()){
                        output.delete();
                    }
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    if (output.exists()){
                        output.delete();
                    }
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally{
                    httpConn.disconnect();
                }
            }
        }).start();


        return true;
    }
}