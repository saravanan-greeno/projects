/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Greeno 02 March 2015
 */
package com.rainbowagri.servercommunicator;

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
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ServerCommunicator {


    public String postJSONData(String requestJSON, String url) throws Exception {

        final JSONObject jsonObject = new JSONObject(requestJSON);
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

        return strResponse;
    }

 


   

}
