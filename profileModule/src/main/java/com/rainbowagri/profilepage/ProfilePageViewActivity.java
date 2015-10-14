package com.rainbowagri.profilepage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.AsyncTaskCompleteListener;
import com.rainbowagri.ServerCommuicator.Configuration;
import com.rainbowagri.ServerCommuicator.ConnectionDetector;
import com.rainbowagri.ServerCommuicator.TaskToFetchDetailsProfile;
import com.rainbowagri.ServerCommuicator.XMLCreatorProfile;
import com.rainbowagri.ServerCommuicator.XmlParserProfile;
 
import com.rainbowagri.data.ProfilePageData;
import com.rainbowagri.data.TaskToFetchData;
 

public class ProfilePageViewActivity extends Activity{
	ImageView profilePicture, smsProfile, callProfile;
	TextView  nameText, pincodeText, phoneNoText, phoneNoTemp, descriptionProfile, addressProfileView, cityProfileView, districtProfileView, headingViewProfile;
	
	RelativeLayout headingProfileView, mobileNumberRelativeLayout;
	LinearLayout profilePictureBackground, addressAndDescriptionLinear, pincodeLinearlayout, cityDistrictlayout;
	Button editButtonProfile;

	public static  String URL = "";
	ConnectionDetector connect;
	String coordinatorUniqueId="0", profileId = "0";
	String appName = "", mSDCardPath = "",nameTextStr = "", pincodeTextStr = "", mobileNoTextStr = "", mobileNoTempStr = "", descriptionProfileStr = "", firstNameStr="", lastNameStr ="" ;
	String 	addressProfileViewStr="", cityProfileViewStr ="", districtProfileViewStr ="",  geoLocationStr = "", shopName="",
			category="",deliveryLocation="",minimumOrder="",profileTypeStr="",deliveryDistrictStr="";
	XMLCreatorProfile xmlCreatorProfile = new XMLCreatorProfile();
	ProfilePageData profilePageData= new ProfilePageData();
	ApplicationInformation applicationInformation = new ApplicationInformation();
	byte[] data = null;
	ArrayList<HashMap<String, Object>> profileViewList = new ArrayList<HashMap<String, Object>>();
	Context myContext;   
	private final int EDIT_PROFILE = 3;  // DONT CHANGE THE NUMBER     

	Bitmap bitmapImage = null;
	
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_page_view);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.profile_page_view);
		UploadCrashReport.startService(this);*/

		myContext = this;
		profilePicture = (ImageView)findViewById(R.id.profilePicture);
		connect = new ConnectionDetector(getApplicationContext());
		nameText = (TextView)findViewById(R.id.nameTextP);
		pincodeText = (TextView)findViewById(R.id.pincodeText);
		phoneNoText = (TextView)findViewById(R.id.phoneNoText);  
		phoneNoTemp = (TextView)findViewById(R.id.phoneNoTemp);
		descriptionProfile = (TextView)findViewById(R.id.descriptionProfile);
		profilePictureBackground =  (LinearLayout)findViewById(R.id.profilePictureBackground);
		editButtonProfile = (Button)findViewById(R.id.editButtonProfile);
		addressProfileView = (TextView)findViewById(R.id.addressProfileView);
		cityProfileView = (TextView)findViewById(R.id.cityProfileView);
		headingViewProfile = (TextView)findViewById(R.id.headingViewProfile);
		districtProfileView = (TextView)findViewById(R.id.districtProfileView);
		callProfile = (ImageView)findViewById(R.id.callProfile);
		smsProfile = (ImageView)findViewById(R.id.smsProfile);
		
		/// Initial Setup
		
		 Typeface myfont = Typeface.createFromAsset(getAssets(),
 		          "fonts/Gotham-Book_0.otf");
       	     
		
		nameText.setTypeface(myfont);
		pincodeText.setTypeface(myfont);
		phoneNoText.setTypeface(myfont);
		phoneNoTemp.setTypeface(myfont);
		descriptionProfile.setTypeface(myfont);
		addressProfileView.setTypeface(myfont);
		cityProfileView.setTypeface(myfont);
		
		headingViewProfile.setTypeface(myfont);
		
		createSDCardFolder();  
		designScreen();
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			coordinatorUniqueId = bundle.getString("coordinatorUniqueId", "0");
			appName = bundle.getString("appName"," ");

			profilePageData.setCoordinatorUniqueId(coordinatorUniqueId);
			profilePageData.setAppName(appName);

			if(appName.equalsIgnoreCase("LivePriceUser"))
			{	
				editButtonProfile.setVisibility(View.GONE);
				//descriptionProfile.setHint("Return Policy");
				
			}	
			else
			{
				callProfile.setVisibility(View.GONE);
				smsProfile.setVisibility(View.GONE);
			}
		
			if(applicationInformation.isInternetConnection(myContext))
			{

				viewProfileDetails();
							
			}
			else
			{
				displayToast("No internet connection...");
				ProfilePageViewActivity.this.finish();
			}

		}

		editButtonProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				         
				saveProfilePictureLocally();
				
				addressProfileViewStr = (addressProfileViewStr.equals("Not Available"))?"":addressProfileViewStr;
			    descriptionProfileStr = (descriptionProfileStr.equals("Not Available"))?"":descriptionProfileStr;
				


				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("coordinatorUniqueId", coordinatorUniqueId);
				map.put("appName", appName);
				map.put("profileId", profileId);
				map.put("mobileNumber", mobileNoTextStr);
				map.put("firstName", firstNameStr);
				map.put("lastName", lastNameStr);
				map.put("shopName", shopName);
				map.put("pincode", pincodeTextStr);
				map.put("description", descriptionProfileStr);
				map.put("categoryList", category);
				map.put("deliveryLocation",deliveryLocation);
				map.put("minimumOrder",minimumOrder);
				map.put("address", addressProfileViewStr);
				map.put("city", cityProfileViewStr);
				map.put("district", districtProfileViewStr);
				map.put("geoLocation", geoLocationStr);
				map.put("profileType", profileTypeStr);
				map.put("deliveryDistrict",deliveryDistrictStr );
				profileViewList.add(map);
				
				Intent moveToNextPage = new Intent(ProfilePageViewActivity.this,ProfilePageActivity.class);
				
				moveToNextPage.putExtra("profileViewList", profileViewList);
				moveToNextPage.putExtra("coordinatorUniqueId", coordinatorUniqueId);
				moveToNextPage.putExtra("mobileNumber", mobileNoTextStr);
				moveToNextPage.putExtra("profileId", profileId);
				ProfilePageViewActivity.this.startActivityForResult(moveToNextPage,EDIT_PROFILE );
//				ProfilePageViewActivity.this.finish();
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("View Profile", "onStart was called");
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("View Profile", "onResume was called");

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("View Profile", "onPause was called");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("View Profile", "onStop was called");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("View Profile", "onRestart was called");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("View Profile", "onDestroy was called");
	}
	@Override
	 protected void onActivityResult(int requestCode, int resultCode,
	            Intent data) {
		 if(requestCode == EDIT_PROFILE && resultCode == RESULT_OK)
		 {
			 Bundle b = data.getExtras();
	 		
	 			Intent intent = new Intent();
	 			if(b!= null)
	 				intent.putExtras(b);
		 		setResult(Activity.RESULT_OK, intent);
		 		finish();
		 }
	 }
	
	@Override
	public void onBackPressed() {
		 
		 Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			 ProfilePageViewActivity.this.finish();
	}

	private void viewProfileDetails(){

		if (applicationInformation.isInternetConnection(myContext)) {

			XMLCreatorProfile xmlCreator = new XMLCreatorProfile();
			String requestXML = xmlCreatorProfile.serializeViewProfile(profilePageData);
			TaskToFetchData createRequest=new TaskToFetchData();
			createRequest.setRequestXML(requestXML);
			createRequest.setRequestInfo("View Profile");
			createRequest.setUrl(Configuration.VIEW_PROFILE);
			String params = "";
			TaskToFetchDetailsProfile task=new TaskToFetchDetailsProfile(this, createRequest,"text");
			task.setFetchMyData(new AsyncTaskCompleteListener<TaskToFetchData>() {
				
				@Override
				public void onTaskCompleteForDownloadAudio(TaskToFetchData result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTaskComplete(TaskToFetchData result) {
					
					try {				
						//Parse the response XML string to know the status
						XmlParserProfile mXmlParser = new XmlParserProfile();
						Document doc = mXmlParser.getDomElement(result.getResponseXML());

						NodeList nodelist = doc.getElementsByTagName("responseViewProfile");
						
						Element element = (Element) nodelist.item(0);
					    String status = ""; //mXmlParser.getValue(element, "status");
					   
					    firstNameStr = mXmlParser.getValue(element, "firstName");
					    shopName = mXmlParser.getValue(element, "shopName");
					    profileId = mXmlParser.getValue(element, "profileId");
					    //For valid vendor; Move to next screen
					 	 if ((!firstNameStr.equalsIgnoreCase("") || !shopName.equalsIgnoreCase("")) && !profileId.equals("0")) {	

							        coordinatorUniqueId = mXmlParser.getValue(element, "coordinatorUniqueId");
								    mobileNoTextStr = mXmlParser.getValue(element, "mobileNumber");
								    firstNameStr = mXmlParser.getValue(element, "firstName");
								    lastNameStr = mXmlParser.getValue(element, "lastName");
								    shopName = mXmlParser.getValue(element, "shopName");
								    pincodeTextStr = mXmlParser.getValue(element, "pincode");
								    descriptionProfileStr = mXmlParser.getValue(element, "description");
								    
								    addressProfileViewStr = mXmlParser.getValue(element, "address");
								    cityProfileViewStr = mXmlParser.getValue(element, "city");
								    districtProfileViewStr = mXmlParser.getValue(element, "district");
								    geoLocationStr  = mXmlParser.getValue(element, "geoLocation");
								    category  = mXmlParser.getValue(element, "categoryList");
								    minimumOrder=mXmlParser.getValue(element, "minOrder");
								    deliveryLocation  = mXmlParser.getValue(element, "deliveryLocation");
								    profileTypeStr = mXmlParser.getValue(element, "profileType");
								    deliveryDistrictStr = mXmlParser.getValue(element, "deliveryDistrict");
								    
								    
								    String place= "";
								    
								    URL = mXmlParser.getValue(element, "imagePath");

								    System.out.println("== URL =="+URL);
								    GetXMLTask task1 = new GetXMLTask();
									task1.execute(new String[] { URL });
//									displayToast(URL);

								    if(appName.equals("LivePrice") || appName.equals("LivePriceUser") )
								    	nameTextStr = shopName ; 
								    else
								    	nameTextStr = firstNameStr +" "+ lastNameStr ; 
								 
								    
								    if(!cityProfileViewStr.equals("") && !districtProfileViewStr.equals(""))
								    	place = cityProfileViewStr+" , "+districtProfileViewStr;
								    else if(cityProfileViewStr.equals("") && !districtProfileViewStr.equals(""))
								    	place = districtProfileViewStr;
								    else if(!cityProfileViewStr.equals("") && districtProfileViewStr.equals(""))
								    	place = cityProfileViewStr;
								    else
								    	cityDistrictlayout.setVisibility(View.GONE);
				
								    if(pincodeTextStr.equals(""))
								    	pincodeLinearlayout.setVisibility(View.GONE);
								    if(cityProfileViewStr.equals("") && districtProfileViewStr.equals(""))
								    	cityDistrictlayout.setVisibility(View.GONE);
								    else
								    	cityDistrictlayout.setVisibility(View.VISIBLE);
								    
								    addressProfileViewStr = (addressProfileViewStr.equals(""))?"Not Available":addressProfileViewStr;
								    descriptionProfileStr = (descriptionProfileStr.equals(""))?"Not Available":descriptionProfileStr;
								    
								  try
								    {
								    	  String name = connect.convertHexaToString(nameTextStr);
								    	  String descriptionProfileText = connect.convertHexaToString(descriptionProfileStr);
										  String addressProfileViewText = connect.convertHexaToString(addressProfileViewStr);
										//  String districtProfileViewText = connect.convertHexaToString(districtProfileViewStr);
								       //   String placeStr = connect.convertHexaToString(place);
								 
								    
										  nameText.setText(name);
										  descriptionProfile.setText(descriptionProfileText);
										  addressProfileView.setText(addressProfileViewText);
										//  districtProfileView.setText(districtProfileViewText);
										//  cityProfileView.setText(placeStr);
								   
								    }
								    
								    catch (Exception e) {
										// TODO: handle exception
									}
                                    
								   /* 
								    descriptionProfile.setText(descriptionProfileStr);
								    addressProfileView.setText(addressProfileViewStr);
								    cityProfileView.setText(place);
								    districtProfileView.setText(districtProfileViewStr);
								    nameText.setText(nameTextStr); 
								    */
								    
								    
								    pincodeText.setText(pincodeTextStr);
								    phoneNoText.setText("91-"+mobileNoTextStr);
								    phoneNoTemp.setText(mobileNoTextStr);
								  
				 		 
					 	 } 
					 	  //For Invalid vendor; display message 
					 	 else{
					 		 showNoProfileAlert();
					 	 }
						
					} catch (Exception e) {
						
						if(applicationInformation.isInternetConnection(myContext)){
							displayToast("Session Expired");
						}else{

							displayToast("No internet connection");
						}
						
					}
				}
			});
			 task.execute(params);
		} else {

			displayToast("No internet connection");
		}
		
	}

	  public void callToDealer(View v)
	  {
		  TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
		  if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
		    //the phone has a sim card
			  
			  RelativeLayout vwParentRow = (RelativeLayout)v.getParent();
		         
			  TextView phoneNoTemp = (TextView)vwParentRow.getChildAt(1);
			  	
			  ImageView callImage=(ImageView)vwParentRow.getChildAt(0);
			  ImageView smsImage=(ImageView)vwParentRow.getChildAt(3);
			  	int i= callImage.getId();
			  
			  	String imageName = (String)callImage.getTag();
			  	String imageName1 = (String)smsImage.getTag();
			  	
		        int c = Color.CYAN;

		        vwParentRow.refreshDrawableState(); 
			   String phoneNo1 = phoneNoTemp.getText().toString();
			   
			   if(phoneNo1.equalsIgnoreCase(""))
			   {
				   displayToast("No phone number is available");
			   }
			   else if(phoneNo1.length() > 0 && phoneNo1.length() < 10)
			   {
	        	  displayToast("Invalid mobile number.");
			   }
			   else
			   {
					  Intent intent = new Intent(Intent.ACTION_CALL);
					  intent.setData(Uri.parse("tel:"+phoneNo1 ));
					  startActivity(intent);
			   }

			 
		  } 
		  else {
			  displayToast("Please insert your sim card"); //no sim card available
		  }
		  
	  }
	  public void smsToDealer(View v)
	  {
		  TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
		  if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
		    //the phone has a sim card
		  
			  RelativeLayout vwParentRow = (RelativeLayout)v.getParent();
	
			   TextView phoneNo = (TextView)vwParentRow.getChildAt(1);
			   ImageView callImage=(ImageView)vwParentRow.getChildAt(0);
			   ImageView smsImage=(ImageView)vwParentRow.getChildAt(3);
			  	
			  	String imageName = (String)callImage.getTag();
			  	String imageName1 = (String)smsImage.getTag();
			 
		        int c = Color.CYAN;
		        
		        vwParentRow.refreshDrawableState(); 
			  
		        SharedPreferences preferences1 = getSharedPreferences("MyPreferences",                   
		                MODE_WORLD_READABLE);
		        String phoneNo1 = phoneNo.getText().toString();
		          if(phoneNo1.equalsIgnoreCase(""))
				   {
		        	  displayToast("No phone number is available.");
				   }
		          else if(phoneNo1.length() > 0 && phoneNo1.length() < 10)
				   {
		        	  displayToast("Invalid mobile number.");
				   }
				   else
				   {
					      Intent sendIntent = new Intent(Intent.ACTION_VIEW);
					      sendIntent.setData(Uri.parse("sms:"+phoneNo1));
					      sendIntent.putExtra("sms_body", "Please add your message here. ");
					      startActivity(sendIntent);
				   } 
		  }
		  else
		  {
			  displayToast("Please insert your sim card");
		  }
	  }
	  
		public void displayToast(String msg)
		{
			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
 
	    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
	        @Override
	        protected Bitmap doInBackground(String... urls) {
	            Bitmap map = null;
	            for (String url : urls) {
	                map = downloadImage(url);
	            }
	            return map;
	        }
	 
	        // Sets the Bitmap returned by doInBackground
	        @Override
	        protected void onPostExecute(Bitmap result) {

	            if(result != null)
	            {
	            	System.out.println("== result is NOT NULL");
	            	profilePictureBackground.setBackgroundDrawable(new BitmapDrawable(result));
	            	profilePicture.setImageBitmap(bitmapImage);
	            }
	            
	        }
	 
	        // Creates Bitmap from InputStream and returns it
	        private Bitmap downloadImage(String url) {
	            
	            InputStream stream = null;
	            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	            bmOptions.inSampleSize = 1;
	            Bitmap tempBitmap=null;
				Bitmap bitmap = null;
	            try {
//	                stream = getHttpConnection(url);
//	                if(stream != null)
//	                {
	                	//Display display = getWindowManager().getDefaultDisplay(); 
//	                	int screenWidth = display.getWidth();
//	                	int screenHeight = display.getHeight();
//		                bitmapImage = BitmapFactory.
//		                        decodeStream(stream, null, bmOptions);		                
//		                bitmapImage = getResizedBitmap(bitmapImage, 500, 500);	
		                
		            	
						  BitmapFactory.Options opts=new BitmapFactory.Options();
					        opts.inDither=false;                    
					        opts.inSampleSize = 8;                   
					        opts.inPurgeable=true;                 
					        opts.inInputShareable=true;             
					        opts.inTempStorage=new byte[16 * 1024]; 
					    	InputStream inputs = new java.net.URL(url).openStream();
							// Decode Bitmap
							tempBitmap = BitmapFactory.decodeStream(inputs);
						//tempBitmap= BitmapFactory.decodeStream(stream,null,opts);

						 //bitmap = Bitmap.createScaledBitmap(tempBitmap,100,100, true);
						bitmapImage= Bitmap.createScaledBitmap(tempBitmap, tempBitmap.getWidth(),tempBitmap.getHeight(), true);
						 
						if (tempBitmap!= bitmapImage){
							 tempBitmap.recycle();
						}
		                
		                
		                File file = new File(mSDCardPath,  "testImage.jpg");
		    	        if(bitmapImage != null)
		    	        {
		    				try {
		    					FileOutputStream fileOutputStream = new FileOutputStream(file);
		    					bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		    				} catch (FileNotFoundException e1) {
		    					// TODO Auto-generated catch block
		    					e1.printStackTrace();
		    				}

		    	        }
		                
		              
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	            return bitmapImage;
	        }
	 
	        // Makes HttpURLConnection and returns InputStream
	        private InputStream getHttpConnection(String urlString)
	                throws IOException {
	            InputStream stream = null;
	            URL url = new URL(urlString);
	            URLConnection connection = url.openConnection();
	 
	            try {
	                HttpURLConnection httpConnection = (HttpURLConnection) connection;
	                httpConnection.setRequestMethod("GET");
	                httpConnection.connect();
	 
	                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                    stream = httpConnection.getInputStream();
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	            return stream;
	        }
	    }
	    
	    
	    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    
	        int width = bm.getWidth();
	        int height = bm.getHeight();
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        // CREATE A MATRIX FOR THE MANIPULATION
	        Matrix matrix = new Matrix();
	        // RESIZE THE BIT MAP
//	        matrix.preRotate(90);
	        matrix.preScale(scaleWidth, scaleHeight);
	        // RECREATE THE NEW BITMAP
	        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

	        return resizedBitmap;
	    }
	    
	    private void saveProfilePictureLocally() {

	        OutputStream outStream = null;

	        File file = new File(mSDCardPath,  "temp.jpg");
	        if(bitmapImage != null)
	        {
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	        }
     
/*	        File profilePictureFile = new  File(mSDCardPath+"/temp.jpg");

			if(profilePictureFile.exists()){

			    Bitmap myBitmap = BitmapFactory.decodeFile(profilePictureFile.getAbsolutePath());
			    profilePicture.setImageBitmap(myBitmap);

			}*/

	     }
	    
	    
	    public void createSDCardFolder()
	    {
			File file0 = new File(Environment.getExternalStorageDirectory()	+ "/Rainbow");
	
			File file1 = new File(Environment.getExternalStorageDirectory()	+ "/Rainbow/Profile");

			if (!file0.exists()) {
				if (file0.mkdir()) {

				}
			}

			if (!file1.exists()) {
				if (file1.mkdir()) {

				}
			}

	    	mSDCardPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Rainbow/Profile";
	    	
	    	if(new File(mSDCardPath, "temp.jpg").exists())
	    		new File(mSDCardPath, "temp.jpg").delete();
	    }
	    
	    public void showNoProfileAlert(){

	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilePageViewActivity.this);
	    	
	        // Setting Dialog Title

	        alertDialog.setTitle(null);
	        // Setting Dialog Message
	        alertDialog.setMessage("Sorry! There is no Profile.");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            	dialog.cancel();
	            	ProfilePageViewActivity.this.finish();
	            }
	        });


	        alertDialog.show();    // Showing Alert Message
	        
	    } 
		
	    public void designScreen()
	    {
	    	headingProfileView  = (RelativeLayout)findViewById(R.id.headingProfileView);
	    	profilePictureBackground = (LinearLayout)findViewById(R.id.profilePictureBackground);
	    	mobileNumberRelativeLayout  = (RelativeLayout)findViewById(R.id.mobileNumberRelativeLayout);
	    	addressAndDescriptionLinear = (LinearLayout)findViewById(R.id.addressAndDescriptionLinear);
	    	
	    	pincodeLinearlayout  =(LinearLayout)findViewById(R.id.pincodeLinearlayout);
	    	cityDistrictlayout  =(LinearLayout)findViewById(R.id.cityDistrictlayout);
	    	
	    	ScrollView addressAndDescriptionScroll  =(ScrollView)findViewById(R.id.addressAndDescriptionScroll);

        	Display display = getWindowManager().getDefaultDisplay(); 
        	int screenHeight = display.getHeight();
        	int testLocalHeight = screenHeight;
        	int tempHeight =  screenHeight/9;
        	RelativeLayout.LayoutParams headingProfileViewMeasurement = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        			tempHeight);

        	if(profilePictureBackground != null && headingProfileView!= null && addressAndDescriptionLinear!=null && mobileNumberRelativeLayout!=null)
        	{
        		headingProfileView.getLayoutParams().height = tempHeight;
        		mobileNumberRelativeLayout.getLayoutParams().height = tempHeight;
        		//profilePictureBackground.getLayoutParams().height=(int) (tempHeight*3);
        		 profilePictureBackground.getLayoutParams().height=(int) (tempHeight*3);      		 
        	   //addressAndDescriptionLinear.getLayoutParams().height=(int) (tempHeight*3.5);
        		addressAndDescriptionScroll.getLayoutParams().height=(int) (tempHeight*3.5);
        	}
        	else
        		System.out.println("profilePictureBackground   is NULL");
     	

	    }
	    
}
