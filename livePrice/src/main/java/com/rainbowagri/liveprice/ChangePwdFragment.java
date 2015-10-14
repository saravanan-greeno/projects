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
 * @author SATHISH
 *
 */
public class ChangePwdFragment extends Fragment {
	
	public final static String TAG = "Change Password Fragment";
	TextView resetPwd;
	EditText newPwd, confirmPwd;
	Button update;
	ImageView logo;
	public final static int PWD_LENGTH = 7;
	private String userMob = null;
	
	PwdChangedListener pwdChangedCallback;

	/**
	 * 
	 */
	public ChangePwdFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		//Store User mobile number for further process
		final Bundle args = getArguments();
		userMob = args.getString(UserAuthMainActivity.USER_MOB);
		Log.i(TAG, "User Mobile Numb is: " +userMob);
		
		try {
			pwdChangedCallback = (PwdChangedListener) activity;
		} catch (ClassCastException e) {
			
			throw new ClassCastException(activity.toString()
					+ "must implement PwdChangedListener");
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
		
		return inflater.inflate(R.layout.ua_fragment_change_pwd, container, false);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onStart(){
		super.onStart();
		
		logo = (ImageView) getActivity().findViewById(R.id.app_logo_chng_pwd);
		resetPwd = (TextView) getActivity().findViewById(R.id.reset_pwd_tv);
		newPwd = (EditText) getActivity().findViewById(R.id.new_pwd_et);
		confirmPwd = (EditText) getActivity().findViewById(R.id.confirm_pwd_et);
		update = (Button) getActivity().findViewById(R.id.update_bt);
		
		resetPwd.setTypeface(UserAuthMainActivity.gothamMediumTf);
		newPwd.setTypeface(UserAuthMainActivity.gothamBookTf);
		confirmPwd.setTypeface(UserAuthMainActivity.gothamBookTf);
		update.setTypeface(UserAuthMainActivity.gothamMediumTf);
		
		UserAuthMainActivity.setLogo(logo);
		
		UAcustomTextFieldBackground customBg = new UAcustomTextFieldBackground();
		newPwd.setBackground(customBg);
		confirmPwd.setBackground(customBg);
		
		confirmPwd.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				changePwd();
				return true;
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changePwd();
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.findItem(R.id.action_sign_up).setVisible(false);
		menu.findItem(R.id.action_user_policy).setVisible(false);
	}
	
	private boolean isValidPwd(){
		
		String newPwdStr = newPwd.getText().toString();
		String confirmPwdStr = confirmPwd.getText().toString(); 
		
		System.out.println("New pwd:"+newPwdStr+"\n Confirm Pwd:"+confirmPwdStr);
		
		if(newPwdStr.isEmpty()){
			newPwd.setError(getString(R.string.new_pwd_null));
			newPwd.requestFocus();
			confirmPwd.clearFocus();
			return false;
		}
		if(newPwdStr.length() < PWD_LENGTH){
			newPwd.setError(getString(R.string.new_pwd_invalid));
			newPwd.requestFocus();
			confirmPwd.clearFocus();
			return false;
		}
		if(confirmPwdStr.isEmpty()){
			confirmPwd.setError(getString(R.string.confirm_pwd_null));
			confirmPwd.requestFocus();
			newPwd.clearFocus();
			return false;
		}
		if( (confirmPwdStr.length() < PWD_LENGTH) || !newPwdStr.equals(confirmPwdStr)){
			confirmPwd.setError(getString(R.string.new_pwd_mismatches));
			confirmPwd.requestFocus();
			newPwd.clearFocus();
			return false;
		}
		return true;
	}
	
	private void changePwd(){
		if(UserAuthMainActivity.isInternetConnected){
			
			//dismiss soft keyboard
			InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(confirmPwd.getWindowToken(), 0);
			if (isValidPwd()) {
				String requestArg = serializeChangePwd(userMob, confirmPwd.getText().toString());
				XMLRequestAndResponseData createRequest=new XMLRequestAndResponseData();
				createRequest.setRequestXMLdata(requestArg);
				createRequest.setUrl(getString(R.string.uri_change_pwd));
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
							NodeList nodelist = doc.getElementsByTagName("resResetUserPwd");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");
						    
						    if(status.equalsIgnoreCase("success")){
						    	/**
						    	 * Change password failed
						    	 * Make them to login with new credentials
						    	 */
						    	Toast.makeText(getActivity(), R.string.change_pwd_success, Toast.LENGTH_LONG)
						    	.show();
						    	pwdChangedCallback.loginAfterPwdChanged(userMob);
						    	
						    }else{
						    	/**
						    	 * Change password failed: Unknown error
						    	 */
						    	Toast.makeText(getActivity(), R.string.change_pwd_failed, Toast.LENGTH_LONG)
						    	.show();
						    }
							
						} catch (Exception e) {
							// TODO: handle exception
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
			} else {
				return;
			}
		}else{
			UserAuthMainActivity.showNoInternetToast(getActivity());
		}
	}
	
	/**
	 * Serialize web service request 
	 */
	private String serializeChangePwd(String userId, String newPwd) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqResetUserPwd");
			document.appendChild(userAuth);

			Element mobileNo = document.createElement("mobileNo");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(userId));
			
			Element otc = document.createElement("newPassword");
			userAuth.appendChild(otc);
			otc.appendChild(document.createTextNode(newPwd));
			
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

	public interface PwdChangedListener{
		
		public void loginAfterPwdChanged(String userMob);
	}
}
