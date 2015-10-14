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

import com.rainbowagri.uaservercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.uaservercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.uaservercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.uaservercommunicator.XmlParserUserAuth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author Sathish
 *
 */
public class VerifyOTCFragment extends Fragment {
	
	public final static String TAG = "Verify OTC Fragment";
	private final static int OTC_COUNT = 6;
	private String userMob = null;
	TextView resetCode;
	EditText otc;
	Button done, resend;
	ImageView logo;
	
	InitiateChangePasswordListener changePasswordCallback;

	/**
	 * 
	 */
	public VerifyOTCFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		//Store User mobile number for further process
		final Bundle args = getArguments();
		userMob = args.getString(UserAuthMainActivity.USER_MOB);
		Log.i(TAG, "User Mobile Numb is: " +userMob);
		
		try{
			changePasswordCallback = (InitiateChangePasswordListener) activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString()
					+ " must implement InitiateChangePasswordListener");
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
		
//		if(savedInstanceState != null){
//			mobNumb.setText(savedInstanceState.getString(USER_MOB));
//		}
		
		return inflater.inflate(R.layout.ua_fragment_verify_otc,
				container, false);
	}
	
	@SuppressLint("NewApi")
	@Override
    public void onStart() {
        super.onStart();
        
		logo = (ImageView) getActivity().findViewById(R.id.app_logo_verift_otc);
		resetCode = (TextView) getActivity().findViewById(R.id.reset_code_tv);
		otc = (EditText) getActivity().findViewById(R.id.otc_et);
		done = (Button) getActivity().findViewById(R.id.otc_done_bt);
		resend = (Button) getActivity().findViewById(R.id.resend);
		
		resetCode.setTypeface(UserAuthMainActivity.gothamMediumTf);
		otc.setTypeface(UserAuthMainActivity.gothamBookTf);
		done.setTypeface(UserAuthMainActivity.gothamMediumTf);
		resend.setTypeface(UserAuthMainActivity.gothamBookTf);
		UserAuthMainActivity.setLogo(logo);
		
		UAcustomTextFieldBackground customBg = new UAcustomTextFieldBackground();
		otc.setBackground(customBg);
		
	}
	
	public void onResume(){
		super.onResume();
		
		otc.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				verifyOtc();
				return true;
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Validate OTC and request server
				verifyOtc();
			}
		});
		
		resend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Request server to resend OTC 
				resendOtc();
			}
		});
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.findItem(R.id.action_sign_up).setVisible(false);
		menu.findItem(R.id.action_user_policy).setVisible(false);
	}
	
	private boolean isValidOTC(){
		final String mobNumbStr = otc.getText().toString();
		
		if(mobNumbStr.isEmpty()){
			otc.setError(getString(R.string.otc_null));
			otc.requestFocus();
			return false;
		}// Inform user when they entered Mobile number less than 10 digits
		else if (mobNumbStr.length() < OTC_COUNT) {
			otc.setError(getString(R.string.otc_invalid));
			otc.requestFocus();
			return false;
		}
		return true;
	}
	/**
	 * 
	 */
	private void verifyOtc(){

		if(UserAuthMainActivity.isInternetConnected){
			
			//dismiss soft keyboard
			InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(otc.getWindowToken(), 0);
			Log.i(TAG, serializeVerifyOTC(userMob));
			
			if (isValidOTC()) {
				
				String requestArg = serializeVerifyOTC(userMob);
				
				XMLRequestAndResponseData createRequest=new XMLRequestAndResponseData();
				createRequest.setRequestXMLdata(requestArg);
				createRequest.setUrl(getString(R.string.uri_verify_otc));
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
							NodeList nodelist = doc.getElementsByTagName("resVerifyOTC");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");
						    
						    if(status.equalsIgnoreCase("success")){
						    	/**
						    	 * Correct user, allow to reset password
						    	 */
						    	changePasswordCallback.changePwdFor(userMob);
						    }else{
						    	/**
						    	 * Incorrect user, restrict from resetting password
						    	 */
						    	otc.setError(getString(R.string.otc_invalid));
								otc.requestFocus();
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
			} 
		}else {
			UserAuthMainActivity.showNoInternetToast(getActivity());
		}
	}
	
	/**
	 * Serialize web service request 
	 */
	private String serializeVerifyOTC(String userId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqVerifyOTC");
			document.appendChild(userAuth);

			Element mobileNo = document.createElement("mobileNo");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(userId));
			
			Element otc = document.createElement("otc");
			userAuth.appendChild(otc);
			otc.appendChild(document.createTextNode(this.otc.getText().toString()));
			
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
	 * Resent OTC to reset password when user failed receiving the 
	 * OTC from previous fragment
	 */
	private void resendOtc(){
		
		Bundle args = getArguments();
		if(UserAuthMainActivity.isInternetConnected){
			String reqArg = args.getString("resendOTC");
			XMLRequestAndResponseData createRequest=new XMLRequestAndResponseData();
			createRequest.setRequestXMLdata(reqArg);
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
					    	 * OTC to reset password has been resent successfully
					    	 */
					    	Toast.makeText(getActivity(), R.string.resent_success, Toast.LENGTH_LONG).show();
					    }else{
					    	/**
					    	 * Sending OTC to reset password failed
					    	 */
					    	Toast.makeText(getActivity(), R.string.send_otc_failed_title +". " + 
					    	 R.string.send_otc_failed_msg, Toast.LENGTH_LONG).show();
					    }
					} catch (Exception e) {
						// TODO: handle exception
					}
			
					
				}
			});
			task.execute(params);
		}
		
	}
	
	public interface InitiateChangePasswordListener{
		
		public void changePwdFor(String userMob);
	}

}
