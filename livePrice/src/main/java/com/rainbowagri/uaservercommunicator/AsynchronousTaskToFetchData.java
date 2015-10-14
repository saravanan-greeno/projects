package com.rainbowagri.uaservercommunicator;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

@SuppressLint("InlinedApi")
public class AsynchronousTaskToFetchData extends  AsyncTask<String,Void, XMLRequestAndResponseData>
{
    private static final String TAG = "FetchMyDataTask";
    ProgressDialog progressBar;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
	ConnectionDetector connection;
	File destinationFile;
	private XMLRequestAndResponseData processData;
	String destinationFileName = null, coordinatorId, response,originalVoiceName;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
    private Context context;
    private AsyncTaskCompleteListener<XMLRequestAndResponseData> listener;
	private long total;
 

 
    public AsynchronousTaskToFetchData(Context ctx,
			XMLRequestAndResponseData createRequest) {

        this.context = ctx;
        this.processData = createRequest;
//        createPath();
       // this.listener = asynTaskListner;
	}

	public void setFetchMyData(AsyncTaskCompleteListener<XMLRequestAndResponseData> listener){
    	this.listener = listener;
        
       
    }
    protected void onPreExecute()
    {
        super.onPreExecute();
       
        Log.i(TAG, "Async task started");
        progressBar = new ProgressDialog(context);
    	progressBar.setCancelable(true);
    	progressBar.setProgressStyle(AlertDialog.THEME_HOLO_DARK);

    	progressBar.setMessage("Please wait while loading...");
    	progressBar.setIndeterminate(false);
    	progressBar.setCancelable(false);
    	progressBar.show();
        
    }
 
    protected XMLRequestAndResponseData doInBackground(String... params)
    {
    	XMLRequestAndResponseData requestData = new XMLRequestAndResponseData();
    	

		
    	try {

    			requestData.setRequestXMLdata(processData.getRequestXMLdata());
    			requestData.setUrl(processData.getUrl());
    			requestData.setResult(executeMultiPartRequest(requestData));
 
    	 
		} catch (Exception e) {

			e.printStackTrace();
		}

         
        return requestData;
    }
 
    protected void onPostExecute(XMLRequestAndResponseData responseData)
    {
        super.onPostExecute(responseData);
        progressBar.dismiss();
        System.out.println("response "+responseData.getResult());
 
        	listener.onTaskComplete(responseData);
 
    }
    
    
    public String executeMultiPartRequest(
			XMLRequestAndResponseData requestXML) throws Exception {
		String strResponse = null;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		System.out.println("Get xml data == "+requestXML.getRequestXMLdata()+"   req  "+requestXML.getUrl());
		HttpPost httppost = new HttpPost(requestXML.getUrl());

		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/xml");

		try {

			StringEntity entity = new StringEntity(requestXML.getRequestXMLdata(), "UTF-8");
			entity.setContentType("application/xml");
			httppost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httppost);
			BasicResponseHandler responseHandler = new BasicResponseHandler();

			if (httpResponse != null) {
				try {
					strResponse = responseHandler.handleResponse(httpResponse);
				}

				catch (HttpResponseException e) {
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