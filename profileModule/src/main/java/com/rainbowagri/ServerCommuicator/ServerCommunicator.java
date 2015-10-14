/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Greeno 02 March 2015
 */
package com.rainbowagri.ServerCommuicator;

import android.graphics.Bitmap;
import android.util.Log;
 

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerCommunicator {
String response;

    public String postJSONData(String requestJSON, String url) throws Exception {
System.out.println("the parsing url is---"+url);
        final JSONObject jsonObject = new JSONObject(requestJSON);
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        String result;
        try {
                /* forming th java.net.URL object */
            URL urlConn = new URL(url);
            urlConnection = (HttpURLConnection) urlConn.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
         /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(requestJSON);
            out.close();
                 /* optional request header */

                /* for Get request */
            // urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode == 200) {
                String line;


                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }

                response = stringBuilder.toString();

            } }finally{
                urlConnection.disconnect();
            }


            return response;

    }


               /* BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

                //throw new HttpException(responseCode+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;*/

       /* final JSONObject jsonObject = new JSONObject(requestJSON);
        String strResponse = null;
        HttpParams httpParams = new BasicHttpParams();
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Accept", "application/json");
        httppost.addHeader("Content-Type", "application/json");

        try {

            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            entity.setContentType("application/json");
            httppost.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httppost);
            BasicResponseHandler responseHandler = new BasicResponseHandler();

            if (httpResponse != null) {
                try {
                    strResponse = responseHandler.handleResponse(httpResponse);
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                strResponse = null;
            }
            Log.e("WCFTEST", "WCFTEST ********** Response" + strResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return strResponse;*/
    }




