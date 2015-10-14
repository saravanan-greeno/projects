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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author SATHISH
 *
 */
public class UserPoliciesFragment extends Fragment {
	
	public final static String TAG = "User Policied Fragment";
	ImageView logo;
	TextView title;
	WebView content;
	String contentHtml = "<h2>Title</h2><br><p>Description here  on device emulator-5554 " +
			"ActivityManager: Starting: Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] cmp=com.rainbowagri.userauth/.UserAuthMainActivity }" +
			"Dx" + 
			"trouble writing output: already prepared" +
			"[th] adb is running no UserAuth] </p>";

	/**
	 * 
	 */
	public UserPoliciesFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.ua_fragment_user_policy,
				container, false);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		logo = (ImageView) getActivity().findViewById(R.id.app_logo_user_policy);
		title = (TextView) getActivity().findViewById(R.id.user_policy_tv);
		content = (WebView) getActivity().findViewById(R.id.user_policy_content_wv);
		
		title.setTypeface(UserAuthMainActivity.gothamMediumTf);
		UserAuthMainActivity.setLogo(logo);
		
//		content.loadData(contentHtml, "text/html", "UTF-8");
		getUserPolicy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		
		menu.findItem(R.id.action_user_policy)
			.setIcon(R.drawable.ua_ic_userpolicy_inactive)
			.setEnabled(false);
	}
	
	/**
	 * Creates request parameter 
	 * REST web service request syntax
	 * 
			 	<?xml version="1.0" encoding="UTF-8"?>
				<reqGetUserPolicy>
                <appName>Broadcaster/LivePrice/CarryBag</appName>
				</reqGetUserPolicy>				
	 */
	private String serializeUserPolicyRequest(String appName){
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqGetUserPolicy");
			document.appendChild(userAuth);

			Element mobileNo = document.createElement("appName");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(appName));


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
		Log.i(TAG, "Request "+xmlString);
		return xmlString;
	}
	
	/**
	 * Get user policy from server
	 */
	private void getUserPolicy(){
		if (UserAuthMainActivity.isInternetConnected) {
			String requestArg = serializeUserPolicyRequest("LivePrice");
			XMLRequestAndResponseData userPolicyRequest = new XMLRequestAndResponseData();
			userPolicyRequest.setRequestXMLdata(requestArg);
			userPolicyRequest.setTitle("UserPolicy");
			userPolicyRequest.setUrl(getString(R.string.uri_getUserPolicy));
			String params = "";
			
			AsynchronousTaskToFetchData asynTask = new AsynchronousTaskToFetchData(getActivity(), userPolicyRequest);
			asynTask.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {
				
				@Override
				public void onTaskCompleteForDownloadAudio(XMLRequestAndResponseData result) {
					/**
					 * Not required here
					 */
					
				}
				
				@Override
				public void onTaskComplete(XMLRequestAndResponseData result) {
					// TODO Auto-generated method stub
					/**
					 * Parse the XML response
					 * 
					 * Response syntax
						<?xml version="1.0" encoding="UTF-8"?>
						<resGetUserPolicy>
						<policy>policy content</policy>
						</resGetUserPolicy>
					 */
					try {
						
						XmlParserUserAuth mXmlParser = new XmlParserUserAuth();
						Document doc = mXmlParser.getDomElement(result.getResult());
						NodeList nodelist = doc.getElementsByTagName("resGetUserPolicy");
						Element element = (Element) nodelist.item(0);
					    contentHtml = mXmlParser.getValue(element, "policy");
					    Log.i(TAG, "Response "+contentHtml);
					    
					    if (!contentHtml.isEmpty()) {
					    	content.loadData(contentHtml, "text/html", "UTF-8");
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
			asynTask.execute(params);
		}
	}
	
	


}
