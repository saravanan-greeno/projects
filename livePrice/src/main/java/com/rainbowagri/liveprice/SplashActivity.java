/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.Configuration;
import com.rainbowagri.ServerCommuicator.ServerCommunicator;
import com.rainbowagri.liveprice.LoginActivity.GetCategoryDetails;
import com.rainbowagri.model.CatagoryFactory;



public class SplashActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 4000;
	String mobileNumb;
	static CatagoryFactory catagoryFactory;
	public static String TABLE_STATUS= "";
	CharSequence[] categoryItems={};
	JSONArray categories = null;
	 List<CharSequence> categoryList = new ArrayList<CharSequence>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.splash_page);
		UploadCrashReport.startService(this);
*/
		ActionBar actionbar = getActionBar();
		actionbar.hide();
		catagoryFactory = new CatagoryFactory(this);
		// Pushing to main activity or login activity


	}

	
	
	@Override
	public void onStart() {
		super.onStart();
		//new Handler().postDelayed(new Runnable() {

		//	@Override
		//	public void run() {
				SharedPreferences mPreferences = this.getSharedPreferences(
						"RainbowAgriLivePrice", MODE_PRIVATE);
				mobileNumb = mPreferences.getString("coordinatorUserId", null);
				
				if (mobileNumb != null) {
					System.out.println("the received mobile number is-==="+mobileNumb);
					 GetCategoryDetails details = new GetCategoryDetails(this);
					 details.execute(); 
					catagoryFactory.onCreate();
				
				} else if (mobileNumb == null) {
					
					System.out.println("the not received mobile number is-==="+mobileNumb);
					 Intent signupIntent = new Intent(this, UserAuthMainActivity.class);
					 this.startActivity(signupIntent);
						this.finish();
				}

			}
	
	
	class GetCategoryDetails extends AsyncTask<String, Void, String> {
        String reqParams;

        public GetCategoryDetails(Context applicationContext) {
        }

        @Override
        protected void onPreExecute() {
            System.out.println("getting hereresss");
            super.onPreExecute();
         
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;

            try {

                ServerCommunicator serverCommunicator = new ServerCommunicator();
              
                String requestURL = Configuration.GET_CATEGORIES;
                
                System.out.println("the getting url is++"+requestURL);
                try {
                    // 3. build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("category", "All");

                    // 4. convert JSONObject to JSON to String
                    reqParams = jsonObject.toString();
                    Log.d("Login request params:", reqParams);

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
                response = serverCommunicator.postJSONData(reqParams, requestURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("responseresponse responseresponse--" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            
            if (response != null) {
                try {
                	
                	 System.out.println("responseresponse issdsds--"+response);
                	processCategoryResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Please try again.Network error.", Toast.LENGTH_LONG).show();
              //  System.exit(0);
                finish();
            }
        }
    }
 
 
 
 public void processCategoryResponse(String response) throws JSONException {

     System.out.println("the receiving response is++"+response);
       // StringRelatedStuff stringRelatedStuff = new StringRelatedStuff();
        JSONObject jsonObject = new JSONObject(response);

      //  fpoNameStr = stringRelatedStuff.convertHexaToString(jsonObject.get("name").toString());
        
      
        categories = jsonObject.getJSONArray("category");
        System.out.println("the size of the list is-----"+categories.length());
        categoryList.clear();
        for (int i = 0; i < categories.length(); i++) {
             
        	JSONObject c = categories.getJSONObject(i);
           
             String idValue = c.getString("id");
             String categoryValue = c.getString("category");
          
             categoryList.add(categoryValue);
           //  categoryItems = new CharSequence[categoryList.size()];
             categoryItems = categoryList.toArray(new CharSequence[categoryList.size()]);
             System.out.println("the size of the categories is-----"+categoryItems.length);
             System.out.println("the categories are here-----"+categoryItems.toString());
             
        } 
 
        String mainCategoryalue="";
		 for(int i=0;i<categoryItems.length;i++){
			 mainCategoryalue=mainCategoryalue+"//"+categoryItems[i].toString();
		  }
		 
		 
		 
		 SharedPreferences mypref = getApplication().getSharedPreferences("RainbowAgriLivePrice",
		MODE_PRIVATE);
	SharedPreferences.Editor prefsEditr = mypref.edit();
	prefsEditr.putString("mainCategoryAllvalues",mainCategoryalue);
	prefsEditr.commit(); 
	
	 
	Intent intent = new Intent(this, CommodityDetailsActivity.class);
	this.startActivity(intent);
	this.finish();
 
        
      }
		//} );
	}
