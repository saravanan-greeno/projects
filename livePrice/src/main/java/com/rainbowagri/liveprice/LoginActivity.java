/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan and Sathish <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.Configuration;
import com.rainbowagri.ServerCommuicator.ServerCommunicator;
import com.rainbowagri.model.CatagoryFactory;
 
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;



public class LoginActivity extends Activity {

	static boolean isInternetConnected = false;
	static CatagoryFactory catagoryFactory;
	static final int LOGIN_REQUEST = 1;
	public static String TABLE_STATUS= "";
	CharSequence[] categoryItems={};
	JSONArray categories = null;
	 List<CharSequence> categoryList = new ArrayList<CharSequence>();
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);

		}
		return true;
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();
		setContentView(R.layout.activity_login);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_login);
		UploadCrashReport.startService(this);*/
		catagoryFactory = new CatagoryFactory(this);

		SharedPreferences mPreferences = this.getSharedPreferences(
				"RainbowAgriLivePrice", MODE_PRIVATE);
		String mobileNumb = mPreferences.getString("coordinatorUserId", null);

		if (mobileNumb != null) {
			System.out.println("the received mobile number is-==="+mobileNumb);
			 GetCategoryDetails details = new GetCategoryDetails(this);
			 details.execute();
			catagoryFactory.onCreate();
			Log.i("Mobile Number", mobileNumb);
			Intent intent = new Intent(this, CommodityDetailsActivity.class);
			this.startActivity(intent);
			this.finish();
		} else if (mobileNumb == null) {
			
			System.out.println("the not received mobile number is-==="+mobileNumb);
			
			
			
			Intent signupIntent = new Intent(this, UserAuthMainActivity.class);
			startActivityForResult(signupIntent, LOGIN_REQUEST);
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new SignInFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		/** Register for CONNECTIVITY_ACTION broadcasts */
		registerReceiver(NetworkStatusReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(NetworkStatusReceiver);
	}

	/**
	 * Inform user instantly when the INTERNET connectivity losses.
	 */
	private BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			isInternetConnected = networkInfo != null
					&& networkInfo.isConnectedOrConnecting();

			if (!isInternetConnected) {
				Toast noInternetToast = Toast.makeText(getApplicationContext(),
						"No internet connection", Toast.LENGTH_LONG);
				noInternetToast.show();
			}
		}
	};

	/**
	 * SignIn fragment with its view elements.
	 */
	public static class SignInFragment extends Fragment {

		EditText userId, password;
		Button login;
		private static final String TAG = "SignIn fragment";
		Fragment fragment = this;

		public SignInFragment() {
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			/**
			 * This makes sure that the LoginActivity has implemented the
			 * callback interface. If not, it throws an exception.
			 */

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);

			userId = (EditText) rootView.findViewById(R.id.mobile_et);
			password = (EditText) rootView.findViewById(R.id.password_et);
			login = (Button) rootView.findViewById(R.id.signin);

			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
					"fonts/Sanchez-Regular_0.ttf");
			userId.setTypeface(tf);
			password.setTypeface(tf);
			login.setTypeface(tf);

			// Do login when "Done" button pressed in soft key pad
			password.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					Log.i(TAG, "Done butn pressed in keypad");
					if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
							|| actionId == EditorInfo.IME_ACTION_DONE) {

						authenticateVendor();
					}

					return false;
				}
			});

			// OnClickListener for "Login" button
			login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent signupIntent = new Intent(getActivity(),
							UserAuthMainActivity.class);
					startActivityForResult(signupIntent, 1001);
				}
			});

			return rootView;
		}

		@Override
		public void onStart() {
			super.onStart();
			userId.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
		}

		/**
		 * UserId and Password validations when Login button pressed
		 */
		public boolean validations(String userIdStr, String passwordStr) {

			if (userIdStr.isEmpty() && passwordStr.isEmpty()
					|| userIdStr.isEmpty()) {
				userId.setError(getString(R.string.userid_null));
				password.setError(null);
				userId.requestFocus();
				return false;
			}

			else if (passwordStr.isEmpty()) {
				password.setError(getString(R.string.password_null));
				userId.setError(null);
				password.requestFocus();
				return false;
			}
			// Inform user when they entered userId (Mobile number) less than 10
			// digits
			else if (userIdStr.length() < 10) {
				userId.setError(getString(R.string.userid_invalid));
				password.setError(null);
				userId.requestFocus();
				return false;
			}
			// Inform user when they entered password less than 7 digits
			else if (passwordStr.length() < 7) {
				password.setError(getString(R.string.password_invalid));
				userId.setError(null);
				password.requestFocus();
				return false;
			}

			return true;
		}

		/**
		 * Serializes the user authentication details (Login information) as
		 * String in XML format
		 * 
		 * @param mUser
		 * @return xmlString
		 */
		private String serializeSignInCredentials(String userId, String pword) {
			String xmlString = null;
			try {
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder documentBuilder = documentBuilderFactory
						.newDocumentBuilder();
				Document document = documentBuilder.newDocument();

				Element userAuth = document.createElement("userAuth");
				document.appendChild(userAuth);

				Element mobileNo = document.createElement("mobileNo");
				userAuth.appendChild(mobileNo);
				mobileNo.appendChild(document.createTextNode(userId));

				Element password = document.createElement("password");
				userAuth.appendChild(password);
				password.appendChild(document.createTextNode(pword));

				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer();
				Properties outFormat = new Properties();
				outFormat.setProperty(OutputKeys.INDENT, "yes");
				outFormat.setProperty(OutputKeys.METHOD, "xml");
				outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				outFormat.setProperty(OutputKeys.VERSION, "1.0");
				outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperties(outFormat);
				DOMSource domSource = new DOMSource(
						document.getDocumentElement());
				OutputStream output = new ByteArrayOutputStream();
				StreamResult result = new StreamResult(output);
				transformer.transform(domSource, result);
				xmlString = output.toString();

			} catch (ParserConfigurationException e) {
			} catch (TransformerConfigurationException e) {
			} catch (TransformerException e) {
			}
			return xmlString;
		}

		/**
		 * When "Login" button clicked or "Done" button pressed in soft keypad
		 */
		private void authenticateVendor() {

			final String userIdStr = userId.getText().toString();
			String pwdStr = password.getText().toString();

			if (validations(userIdStr, pwdStr)) {
				if (isInternetConnected) {
					String requestArg = serializeSignInCredentials(userIdStr,
							pwdStr);

					XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();
					createRequest.setRequestXMLdata(requestArg);
					createRequest.setTitle("Login");
					createRequest
							.setUrl(getString(R.string.authenticate_vendor));
					String params = "";
					AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
							getActivity(), createRequest);
					task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

						@Override
						public void onTaskComplete(
								XMLRequestAndResponseData result) {

							try {
								// Parse the response XML string to know the
								// status
								XmlParser mXmlParser = new XmlParser();
								Document doc = mXmlParser.getDomElement(result
										.getResult());
								NodeList nodelist = doc
										.getElementsByTagName("responsevendorAuth");
								Element element = (Element) nodelist.item(0);
								String status = mXmlParser.getValue(element,
										"status");

								// For valid vendor; Move to next screen
								if (status.equalsIgnoreCase("success")) {
									catagoryFactory.onCreate();
									Log.i(TAG, "Login Success");

									String shopName = mXmlParser.getValue(
											element, "shopName");
									int vendorId = Integer.parseInt(mXmlParser
											.getValue(element, "vendorId"));

									SharedPreferences mPreferences = getActivity()
											.getSharedPreferences(
													"RainbowAgriLivePrice",
													MODE_PRIVATE);
									SharedPreferences.Editor mEditor = mPreferences
											.edit();
									mEditor.putString("coordinatorUserId",
											userIdStr);
									mEditor.putString("mobilenumber", userIdStr);
									mEditor.putInt("coordinatorRefId", vendorId);
									mEditor.putString("shopName", shopName);
									mEditor.commit();

									Intent intent = new Intent(getActivity(),
											CommodityDetailsActivity.class);
									getActivity().startActivity(intent);
									getActivity().finish();
								}
								// For Invalid vendor; display message
								else {
									Toast.makeText(getActivity(),
											R.string.response_failure,
											Toast.LENGTH_LONG).show();
								}

							} catch (Exception e) {
								if (LoginActivity.isInternetConnected) {
									Toast.makeText(getActivity(),
											R.string.session_expired,
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(getActivity(),
											"No internet connection",
											Toast.LENGTH_LONG).show();
								}

							}
						}
					});
					task.execute(params);
				} else {
					Toast.makeText(getActivity(), "No internet connection",
							Toast.LENGTH_LONG).show();
				}

			}
		}

		/**
		 * 
		 * @author Sathish The container Activity must implement this interface
		 *         so the fragment can deliver messages
		 * 
		 */
		public interface OnLoginClickListener {
			/** Called by LoginFragment when Login button clicked */
			public void onLoginClicked();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if (requestCode == 1001) {

				try {
					/**
					 * Make sure the request was successful
					 */
					if (resultCode == RESULT_OK) {
						catagoryFactory.onCreate();

						String vendor = data.getExtras().getString("userId");

						SharedPreferences mPreferences = getActivity()
								.getSharedPreferences("RainbowAgriLivePrice",
										MODE_PRIVATE);
						SharedPreferences.Editor mEditor = mPreferences.edit();
						mEditor.putInt("coordinatorRefId",
								Integer.parseInt(vendor));
						mEditor.putString("coordinatorUserId", data.getExtras()
								.getString("mobile"));
						mEditor.putString("mobilenumber", data.getExtras()
								.getString("mobile"));
						mEditor.putString("shopName", data.getExtras()
								.getString("shopName"));
						mEditor.putString("profileId", data.getExtras()
								.getString("profileId"));
						mEditor.commit();

						Intent intent = new Intent(getActivity(),
								CommodityDetailsActivity.class);
						getActivity().startActivity(intent);
						getActivity().finish();
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(resultCode, resultCode, data);
		/**
		 * Check for SignUp request's response
		 * 
		 */
		try {

			if (requestCode == LOGIN_REQUEST) {
				/**
				 * Make sure the request was successful
				 */
				if (resultCode == RESULT_OK) {
					 catagoryFactory.onCreate();
					Toast.makeText(this, "Login success", Toast.LENGTH_LONG)
							.show();
                    System.out.println("comein or not");
                    
                    GetCategoryDetails details = new GetCategoryDetails(this);
       			    details.execute();
					String vendor = data.getExtras().getString("userId");

					SharedPreferences mPreferences = this.getSharedPreferences(
							"RainbowAgriLivePrice", MODE_PRIVATE);
					SharedPreferences.Editor mEditor = mPreferences.edit();
					mEditor.putInt("coordinatorRefId", Integer.parseInt(vendor));
					mEditor.putString("coordinatorUserId", data.getExtras()
							.getString("mobile"));
					mEditor.putString("mobilenumber", data.getExtras()
							.getString("mobile"));

					mEditor.putString("shopName",
							data.getExtras().getString("shopName"));
					mEditor.putString("profileId",
							data.getExtras().getString("profileId"));
					
					mEditor.putString("mainCategory",
							data.getExtras().getString("mainCategory"));

					mEditor.commit();

					Intent intent = new Intent(this,
							CommodityDetailsActivity.class);
					this.startActivity(intent);
					this.finish();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * SignUp fragment with its view elements
	 */
	public static class SignUpFragment extends Fragment {

		public SignUpFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
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
        }
    }
 
 
 
 public void processCategoryResponse(String response) throws JSONException {
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
        
      }
}
