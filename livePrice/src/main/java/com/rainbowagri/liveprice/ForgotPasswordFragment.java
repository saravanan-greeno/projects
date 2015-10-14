/**
 * 
 */
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.rainbowagri.profilepage.ProfilePageActivity;
import com.rainbowagri.uaservercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.uaservercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.uaservercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.uaservercommunicator.XmlParserUserAuth;

/**
 * @author Sathish
 *
 */
public class ForgotPasswordFragment extends Fragment {
	
	private static final  String TAG = "Forgot Password Fragment";
	final static String USER_MOB = "userMob";
	TextView frgPwd;
	EditText mobNumb;
	Button getSMS;
	ImageView logo;
	
	OnOTCSendSuccessListner onOTCSendSuccessCallback;

	/**
	 * 
	 */
	public ForgotPasswordFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onAttach(Activity activity) {
		
        super.onAttach(activity);

        // This makes sure that the UserAuth Activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
        	onOTCSendSuccessCallback  = (OnOTCSendSuccessListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOTCSendSuccessListner");
        }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.i(TAG, "OnCreateView");		
		
		if(savedInstanceState != null){
			mobNumb.setText(savedInstanceState.getString(USER_MOB));
		}
		
		return inflater.inflate(R.layout.ua_fragment_forgot_pwd,
				container, false);
	}
	
	@SuppressLint("NewApi")
	@Override
    public void onStart() {
        super.onStart();
        
		logo = (ImageView) getActivity().findViewById(R.id.app_logo_frg_pwd);
		frgPwd = (TextView) getActivity().findViewById(R.id.frg_pwd_tv);
		mobNumb = (EditText) getActivity().findViewById(R.id.mobile_numb_et);
		getSMS = (Button) getActivity().findViewById(R.id.get_sms_bt);
		
		frgPwd.setTypeface(UserAuthMainActivity.gothamMediumTf);
		mobNumb.setTypeface(UserAuthMainActivity.gothamBookTf);
		getSMS.setTypeface(UserAuthMainActivity.gothamMediumTf);
//		logo.setImageResource(R.drawable.logo_small_liveprice);
		UserAuthMainActivity.setLogo(logo);
		
		UAcustomTextFieldBackground customBg = new UAcustomTextFieldBackground();
		mobNumb.setBackground(customBg);
        
        Bundle args = getArguments();
        if(args != null){
        	//Set Mobile number based on arguments
        	mobNumb.setText(args.getString(USER_MOB));
        }
	}
	
	public void onResume(){
		super.onResume();
		
		mobNumb.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				sendOTC();
				return true;
			}
		});
		
		getSMS.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View v) {
				// TODO Request server to send OTC

				sendOTC();
			}
		});
	}
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current mobile number
        outState.putString(USER_MOB, mobNumb.getText().toString());
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.findItem(R.id.action_sign_up).setVisible(false);
		menu.findItem(R.id.action_user_policy).setVisible(false);
	}
	
	/**
	 * Checks for 10 digit mobile number in Mobile number edit text field
	 */
	private boolean isValidMobileNumb(){
		
		final String mobNumbStr = mobNumb.getText().toString();
		
		if(mobNumbStr.isEmpty()){
			mobNumb.setError(getString(R.string.userid_null));
			mobNumb.requestFocus();
			return false;
		}// Inform user when they entered Mobile number less than 10 digits
		else if (mobNumbStr.length() < 10) {
			mobNumb.setError(getString(R.string.userid_invalid));
			mobNumb.requestFocus();
			return false;
		}
		return true;
	}
	
	public void sendOTC(){
		
		//dismiss soft keyboard
		InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mobNumb.getWindowToken(), 0);
		
		if(UserAuthMainActivity.isInternetConnected){
//			Toast.makeText(getActivity(), "Valid Mobile Number", Toast.LENGTH_LONG).show();
			
			
			if(isValidMobileNumb()){
				final String requestArg = serializeSendOTC(mobNumb.getText().toString());
				
				XMLRequestAndResponseData createRequest=new XMLRequestAndResponseData();
				createRequest.setRequestXMLdata(requestArg);
				createRequest.setUrl(getString(R.string.uri_send_otc));
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
							NodeList nodelist = doc.getElementsByTagName("resSendOTC");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");
						    
						    if(status.equalsIgnoreCase("success")){
						    	/**
						    	 * OTC to reset password has been sent successfully
						    	 */
						    	onOTCSendSuccessCallback.OTCSentTo(mobNumb.getText().toString(), requestArg);
						    }else if (status.equalsIgnoreCase("failure")) {
								/**
								 * Sending OTC failed due to some unknown error
								 * Inform user to try after some time 
								 */
						    	sendOTCFailedDialog();
							}else if (status.equalsIgnoreCase("NewUser")) {
								//TODO Not implemented in server
								/**
								 * Mobile number not registered in the app
								 */
						    	showNewUserDialog();
							}
							
						} catch (Exception e) {
							// TODO: handle exception
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
				
			}else{
				return;
			}
		}else{
			UserAuthMainActivity.showNoInternetToast(getActivity());
		}
	}
	/**
	 * Serialize web service request 
	 */
	private String serializeSendOTC(String userId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqSendOTC");
			document.appendChild(userAuth);

			Element mobileNo = document.createElement("mobileNo");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(userId));
			
			Element appNameElement = document.createElement("appName");
			userAuth.appendChild(appNameElement);
			appNameElement.appendChild(document.createTextNode(UserAuthMainActivity.APP_NAME));

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
	 * Custom alert dialog when sending OTC fails
	 */
	
	private void sendOTCFailedDialog(){
		
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		dialogBuilder.setTitle(R.string.send_otc_failed_title)
		.setMessage(R.string.send_otc_failed_msg)
		.setNeutralButton(R.string.send_otc_alert_neu_but, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog otcFailedDialog = dialogBuilder.create();
		otcFailedDialog.show();
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
				extras.putString("appName", "Broadcaster:signup");
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
	 * 
	 * @author SATHISH
	 *
	 */
	public interface OnOTCSendSuccessListner{
		
		public void OTCSentTo(String mobNumb, String reqArgu);
	}
}
