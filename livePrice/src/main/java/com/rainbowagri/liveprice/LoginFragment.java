package com.rainbowagri.liveprice;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.rainbowagri.ServerCommuicator.ConnectionDetector;
import com.rainbowagri.profilepage.ProfilePageActivity;
import com.rainbowagri.uaservercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.uaservercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.uaservercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.uaservercommunicator.XmlParserUserAuth;

public class LoginFragment extends Fragment {
	
	EditText userId,password;
	Button login,forgotPwd,skip;
	ImageView logo;
	private static final  String TAG = "SignIn fragment";
	Fragment mFragment = null;
	FragmentManager mFragmentManager;
	String fragmentTag = null;
	public static String[] appName = { "rainbowagri", "broadcaster", "liveprice" };
	
	private static final int RAINBOW_AGRI = 0;
	private static final int BROADCASTER = 1;
	private static final int LIVEPRICE = 2;
	
	static final String APP_NAME_KEY = "appName";
	static final String MOBILE_NUMBER = "mobileNumber";
	static final String COORDINATOR_ID = "coordinatorUniqueId";
	static final String APP_NAME_VALUE = "Broadcaster";
	ConnectionDetector cd;
	Context context;
	OnForgotPasswordClickListener forgotPwdCallback;
	OnSkipClickListener skipUserAuthCallback;
	
	//Fragment needs an empty constructor
	public LoginFragment() {

	}

	
	@Override
	public void onAttach(Activity activity) {
		
        super.onAttach(activity);

        /**
         *  This makes sure that the UserAuth Activity has implemented
         *  the callback interface. If not, it throws an exception.
         */
        
        try {
           forgotPwdCallback  = (OnForgotPasswordClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnForgotPasswordClickListener");
        }
        try{
        	skipUserAuthCallback = (OnSkipClickListener) activity;
        }catch(ClassCastException e){
        	throw new ClassCastException(activity.toString()
                    + " must implement OnSkipClickListener");
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.i(TAG, "OnCreateView");
		return inflater.inflate(R.layout.ua_fragment_login,
				container, false);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onStart(){
		super.onStart();
		userId = (EditText) getActivity().findViewById(R.id.mobile_et);
		password = (EditText) getActivity().findViewById(R.id.password_et);
		login = (Button) getActivity().findViewById(R.id.login_bt);
		forgotPwd = (Button) getActivity().findViewById(R.id.forgot_pwd);
		skip = (Button) getActivity().findViewById(R.id.skip);
		logo = (ImageView) getActivity().findViewById(R.id.app_logo);
		cd= new ConnectionDetector(context);
		//Set image for logo image view
		UserAuthMainActivity.setLogo(logo);

		userId.setTypeface(UserAuthMainActivity.gothamBookTf);
		password.setTypeface(UserAuthMainActivity.gothamBookTf);
		login.setTypeface(UserAuthMainActivity.gothamMediumTf);
		forgotPwd.setTypeface(UserAuthMainActivity.gothamBookTf);
		skip.setTypeface(UserAuthMainActivity.gothamBookTf);
		
		GradientDrawable remarksDrawable = new GradientDrawable();
		remarksDrawable.setShape(GradientDrawable.RECTANGLE);
		remarksDrawable.setStroke(2, Color.BLACK);
		remarksDrawable.setColor(Color.rgb(182, 234, 255));
		UAcustomTextFieldBackground customBg = new UAcustomTextFieldBackground();
		try
		{
		if (Build.VERSION.SDK_INT >= 16) {

			userId.setBackground(customBg);
			password.setBackground(customBg);

		} else {

			userId.setBackgroundDrawable(customBg);
			password.setBackgroundDrawable(customBg);
		}
		}
		catch (Exception e) {
			
			Toast.makeText(getActivity(), "Something wrong..", Toast.LENGTH_LONG).show();

			 
		}
		
		
	//	userId.setBackground(customBg);
	//	password.setBackground(customBg);
		
		//Hide Skip button
		skip.setVisibility(View.GONE);
		
		//Do login when "Done" button pressed in soft key pad
		password.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.i(TAG, "Done butn pressed in keypad");
				if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| actionId == EditorInfo.IME_ACTION_DONE){
					//TODO Do login
					authenticateUser();
					
				}
				return false;
			}
		});
		//OnClickListener for "Login" button
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Resign soft keypad
				final InputMethodManager imm = (InputMethodManager) getActivity().
						getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			    
				//TODO Do login
				authenticateUser();
			
				}
		});
		
		mFragmentManager = getFragmentManager();
		
		//OnClickListener for "Forgot Password" button
		forgotPwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/** Notify the container activity about Forgot Password clicked*/
				forgotPwdCallback.onPasswordForgot(""+ userId.getText().toString());
			}
		});
		
		//OnClickListener for "Skip Login" button
		skip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/** Notify the container activity about Skip Login (UserAuth) clicked*/
				skipUserAuthCallback.onUserAuthSkip();	
				}
			});
	}
	
	/**
	 * UserId and Password validations when Login button pressed
	 */
	public boolean isValidLoginCredentials() {
		
		Log.i(TAG, "Validating Login Credentials");
		
		final String userIdStr = userId.getText().toString();
		final String passwordStr = password.getText().toString();
		
		// Ask user to enter mobile number either both userId and password fields were empty 
		// or userId field alone empty
		if(userIdStr.isEmpty() && passwordStr.isEmpty() || userIdStr.isEmpty()){
			userId.setError(getString(R.string.userid_null));
			password.setError(null);
			userId.requestFocus();
			return false;
		}
		// Ask user to enter the password when password field is null
		else if (passwordStr.isEmpty()) {
			password.setError(getString(R.string.password_null));
			userId.setError(null);
			password.requestFocus();
			return false;
		}
		// Inform user when they entered userId (Mobile number) less than 10 digits
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
	 * When "Login" button clicked or "Done" button pressed in soft keypad
	 * 
	 * Response syntax
			<?xml version="1.0" encoding="UTF-8"?>
			<resLogin>
			<status>success/active/pending/New User </status>
			<userId>1</userId>
			<isProfileAvailable>Yes/No</isProfileAvailable>
			</resLogin>
	 */
	private void authenticateUser(){
		Log.i(TAG, "Authenticating user");
		
		if(UserAuthMainActivity.isInternetConnected) {
			if(isValidLoginCredentials()){
				
				final String userIdStr = userId.getText().toString();
				final String passwordStr = password.getText().toString();
				
				String requestArg = serializeSignInCredentials(userIdStr, 
						passwordStr, UserAuthMainActivity.APP_NAME);
				Log.i(TAG, requestArg);
				
				XMLRequestAndResponseData createRequest=new XMLRequestAndResponseData();
				createRequest.setRequestXMLdata(requestArg);
				createRequest.setUrl(getString(R.string.uri_login));
				String params = "";
				AsynchronousTaskToFetchData task=new AsynchronousTaskToFetchData(this.getActivity(), createRequest);
				task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {
					
					@Override
					public void onTaskCompleteForDownloadAudio(XMLRequestAndResponseData result) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onTaskComplete(XMLRequestAndResponseData result) {
						// TODO Auto-generated method stub
						try {
							//Parse the response XML string to know the status
							XmlParserUserAuth mXmlParser = new XmlParserUserAuth();
							Document doc = mXmlParser.getDomElement(result.getResult());
							NodeList nodelist = doc.getElementsByTagName("resLogin");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");						    
						    
						    if(status.equalsIgnoreCase("active")){
						    	/**
							     * Login success & Profile available
							     */						    	
						    	Log.i(TAG, "Active user");
						    	Toast.makeText(getActivity(), "Login success", Toast.LENGTH_LONG).show();
						    	//TODO Move to MainActivity of the app
						    	//Parse userId
						    	String userId = mXmlParser.getValue(element, "userId");
						    	String shopName = mXmlParser.getValue(element, "shopName");
						    	String profileId = mXmlParser.getValue(element, "profileId");
						    	String categoryList = mXmlParser.getValue(element, "mainCategory");
						    	
						    	System.out.println("shop name-----"+shopName);
						    	System.out.println("shop profileId-----"+profileId);
						    	System.out.println("categoruy isss-----"+categoryList);
						    	//Move back to App Main Activity
						    	
						    	
						    	String value=cd.convertHexaToString(shopName);
						    	
						 		Intent intent = new Intent(getActivity(), CommodityDetailsActivity.class);
						 		intent.putExtra("userId", userId);
						 		intent.putExtra("mobile", userIdStr);
						 		intent.putExtra("profileId", profileId);
						 		intent.putExtra("shopName", value);
						 		intent.putExtra("mainCategory", categoryList);
						 	    startActivity(intent);
						 		//this.startActivity(intent);
						 	//	this.finish();
						 	   // getActivity().setResult(Activity.RESULT_OK, intent);
						    	getActivity().finish(); 
						    	
						    }else if (status.equalsIgnoreCase("invalid")) {
						    	/**
							     * Invalid login credentials
							     */		
						    	Log.i(TAG, "Invalid user");
						    	Toast.makeText(getActivity(), R.string.response_invalid, 
						 				 Toast.LENGTH_LONG).show();
						    	
							}else if (status.equalsIgnoreCase("NewUser")) {
								/**
								 * Entered mobile number not found in DB, so it's a new user
								 * Ask them to sign up
								 */		
								Log.i(TAG, "New user");
								showNewUserDialog();
								
								
							}else if (status.equalsIgnoreCase("ResetPassword")) {
								/**
								 * User requested for reset password some time before but didn't actually
								 * reset it.
								 * Ask the to get new OTC and reset password
								 */			
								Log.i(TAG, "Reset password");
								//TODO Needs to be tested
								showResetPwdDialog();
								
							}else if (status.equalsIgnoreCase("inactive")) {
								/**
								 * User was signed up before but still it isn't approved by Admin
								 */
								Log.i(TAG, "Inactive user");
								showUserInactiveAlertDialog();
								
							}else if (status.equalsIgnoreCase("updateProfile")) {
								/**
								 * Valid login credentials but profile not found
								 */
								Log.i(TAG, "Update profile");
								//TODO Parse <userId> in response and update profile
								updateProfile(mXmlParser.getValue(element, "userId"));
							}							
						} catch (Exception e) {

							if(UserAuthMainActivity.isInternetConnected){
								Toast.makeText(getActivity(), R.string.session_expired, 
						 				 Toast.LENGTH_LONG).show();
							}else{
								UserAuthMainActivity.showNoInternetToast(getActivity());
							}
						}
					}
				});
				task.execute(params);
			}
		}else{
			UserAuthMainActivity.showNoInternetToast(getActivity());
		}
	}
	
	/**
	 * Sets soucre for logo image view based on app
	 */
	private void setLogo(int i){
		switch (i) {
		case RAINBOW_AGRI:
			
			logo.setImageResource(R.drawable.ua_logo_rainbow);
			break;
			
		case BROADCASTER:
			
			logo.setImageResource(R.drawable.ua_logo_broadcaster);
			break;
		
		case LIVEPRICE:
			
			logo.setImageResource(R.drawable.ua_logo_liveprice);
			break;

		default:
			
			logo.setImageResource(R.drawable.ua_ic_launcher);
			break;
		}
	}
	
	/**
	 * Serializes login details as String in XML format 
	 * 
	 * @param mUser
	 * @return xmlString
	 * 
	 * REST Web service request syntax
	  			<?xml version="1.0" encoding="UTF-8"?>
               	<reqLogin>
				<mobileNo>10_digit_mob_numb</mobileNo>
				<password>alpha_numeric_password</password>
				<appName>Broadcaster/LivePrice</appName>
				</reqLogin>
	 */
	private String serializeSignInCredentials(String userId, String pword, String appName) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqLogin");
			document.appendChild(userAuth);

			Element mobileNo = document.createElement("mobileNo");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(userId));

			Element password = document.createElement("password");
			userAuth.appendChild(password);
			password.appendChild(document.createTextNode(pword));
			
			Element appNameElement = document.createElement("appName");
			userAuth.appendChild(appNameElement);
			appNameElement.appendChild(document.createTextNode(appName));

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(document.getDocumentElement());
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
	 * Custom AlertDialog to confirm new user for sign up
	 */
	public void showNewUserDialog(){
		AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(getActivity());
		dialogBuilder.setMessage(R.string.response_newUser)
		.setPositiveButton(R.string.signup_alert_pos_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Move to sign up activity
				Intent signupIntent = new Intent(getActivity(), ProfilePageActivity.class);
				Bundle extras = new Bundle();
				extras.putString("appName", UserAuthMainActivity.APP_NAME + ":signup");
				signupIntent.putExtras(extras);
				startActivityForResult(signupIntent, UserAuthMainActivity.SIGNUP_REQUEST);
			}
		})
		.setNegativeButton(R.string.signup_alert_neg_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog newUserDialog = dialogBuilder.create();
		newUserDialog.show();
	}
	
	/**
	 * Custom AlertDialog to reset password
	 */
	private void showResetPwdDialog(){
		AlertDialog.Builder dialogBuilder= new AlertDialog.Builder(getActivity());
		dialogBuilder.setMessage(R.string.response_reset_pwd)
		.setPositiveButton(R.string.reset_pwd_alert_pos_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Move to reset password fragment
				/** Notify the container activity about Forgot Password clicked*/
				forgotPwdCallback.onPasswordForgot(""+ userId.getText().toString());				
			}
		})
		.setNegativeButton(R.string.reset_pwd_alert_neg_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog newUserDialog = dialogBuilder.create();
		newUserDialog.show();
	}
	
	/**
	 * AlertDialog for Invalid user intimation
	 */
	private void showUserInactiveAlertDialog(){
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setMessage(R.string.response_inactive)
		.setNeutralButton(R.string.inactive_alert_neu_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog inactiveUserDialog = dialogBuilder.create();
		inactiveUserDialog.show();
	}
	
	/**
	 * Intent to show EditProfile
	 */
	private void updateProfile(String userUniqueId){
		
		Intent updateIntent = new Intent(getActivity(), ProfilePageActivity.class);
		Bundle extras = new Bundle();
		extras.putString(APP_NAME_KEY, APP_NAME_VALUE);
		extras.putString(COORDINATOR_ID, userUniqueId);
		extras.putString(MOBILE_NUMBER, userId.getText().toString());
		updateIntent.putExtras(extras);
		startActivityForResult(updateIntent, UserAuthMainActivity.UPDATE_PROFILE);
	}
	
	
	/**
	 * @author Sathish
	 * The container Activity must implement this interface so the frag can deliver messages
	 *
	 */
	public interface OnForgotPasswordClickListener{
        /** Called by LoginFragment when ForgotPassword button clicked */
		public void onPasswordForgot(String UserMob);
	}
	public interface OnSkipClickListener{
		/** Called by LoginFragment when Skip button clicked*/
		public void onUserAuthSkip();
	}		
	
	
}
