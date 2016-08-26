package com.example.android.booklisting;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;

public class DataHandler {

    static String stream = null;

    public DataHandler(){
        //required constructor
    }

    public String getStreamData(String URLString){
        try {
            URL url = new URL(URLString);
            HttpURLConnection URLConnection = (HttpURLConnection) url.openConnection();

            // check status of connection
            if(URLConnection.getResponseCode()==200) {
                InputStream inputStream = new BufferedInputStream(URLConnection.getInputStream());


                // read inputStream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                stream = stringBuilder.toString();
                URLConnection.disconnect();
            }else{
                //do nothing
            }
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{

        }
        return stream;
    }
}