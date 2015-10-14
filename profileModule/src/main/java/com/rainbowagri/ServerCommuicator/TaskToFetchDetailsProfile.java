
package com.rainbowagri.ServerCommuicator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.rainbowagri.data.TaskToFetchData;

@SuppressLint("InlinedApi")
public class TaskToFetchDetailsProfile extends  AsyncTask<String,Void, TaskToFetchData>
{
    private static final String TAG = "FetchMyDataTask";
    public ProgressDialog progressBar;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
//	ConnectionDetector connection;
	File destinationFile;
	private TaskToFetchData processData;
	String destinationFileName = null, coordinatorId, response,originalVoiceName, typeOfData;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
    private Context context;
    private AsyncTaskCompleteListener<TaskToFetchData> listener;
	private long total;
 

 
    public TaskToFetchDetailsProfile(Context ctx,
			TaskToFetchData createRequest, String typeOfData) {

        this.context = ctx;
        this.processData = createRequest;
        this.typeOfData = typeOfData;
//        createPath();
       // this.listener = asynTaskListner;
	}

	public void setFetchMyData(AsyncTaskCompleteListener<TaskToFetchData> listener){
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
 
    protected TaskToFetchData doInBackground(String... params)
    {
    	TaskToFetchData requestData = new TaskToFetchData();
    	

		
    	try {

    			requestData.setRequestXML(processData.getRequestXML());
    			requestData.setUrl(processData.getUrl());
    			if(typeOfData.equals("imageUpload"))
    				requestData.setResponseXML(executeMultiPartImageRequest(requestData));
    			else if(typeOfData.equals("imageDownload"))
    				requestData.setResponseXML(executeMultiPartImageRequest(requestData));
    			else 
    				requestData.setResponseXML(executeMultiPartRequest(requestData));
 
    	 
		} catch (Exception e) {

			e.printStackTrace();
		}

         
        return requestData;
    }
 
    protected void onPostExecute(TaskToFetchData responseData)
    {
        super.onPostExecute(responseData);
        if ((this.progressBar != null) && this.progressBar.isShowing()){
        	progressBar.dismiss();
        }
      
        System.out.println("response wwwww"+responseData.getResponseXML());
 
        listener.onTaskComplete(responseData);
 
    }
    
    
    public String executeMultiPartRequest(
			TaskToFetchData requestXML) throws Exception {
		String strResponse = null;
		/*HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		System.out.println("Get xml data == "+requestXML.getRequestXML()+"   req  "+requestXML.getUrl());
		HttpPost httppost = new HttpPost(requestXML.getUrl());

		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/xml");

		

		try {

			StringEntity entity = new StringEntity(requestXML.getRequestXML(), "UTF-8");
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

		return strResponse;*/


		InputStream inputStream = null;
		HttpURLConnection urlConnection = null;
		String result;
		try {

			System.out.println("the getting url is---"+requestXML.getUrl());
                /* forming th java.net.URL object */
			URL url = new URL(requestXML.getUrl());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(15000);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("POST");

                 /* optional request header */
			urlConnection.setRequestProperty("Content-Type", "application/xml");

                /* optional request header */
			urlConnection.setRequestProperty("Accept", "application/xml");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.connect();

			DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
			wr.writeBytes(requestXML.getRequestXML());
			wr.flush();
			wr.close();

			int statusCode = urlConnection.getResponseCode();

			System.out.println("the statusCode url is---"+statusCode);

                /* 200 represents HTTP OK */
			if (statusCode ==  200) {

				InputStream stream = urlConnection.getInputStream();
				InputStreamReader isReader = new InputStreamReader(stream );

//put output stream into a string
				BufferedReader br = new BufferedReader(isReader );

				String line;
				System.out.println("the getting url is- iokkkkkkk--"+requestXML.getUrl());
				//BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				StringBuilder stringBuilder = new StringBuilder();
				while ((line=br.readLine()) != null) {
					stringBuilder.append(line + '\n');
				}

				response = stringBuilder.toString();

			} }finally{
			urlConnection.disconnect();
		}

		System.out.println("the getting response is---"+response);
		return response;
	}

 
    public String executeMultiPartImageRequest(
			TaskToFetchData requestXML) throws Exception {
		String strResponse = null;
		
        HttpClient client = new DefaultHttpClient() ;
        HttpPost postRequest = new HttpPost (requestXML.getUrl()) ;
//        HttpPost postRequest = new HttpPost (Configuration.SAVE_PROFILE) ;
        File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Rainbow/Profile/",
				requestXML.getRequestXML()+".jpg");
        
     /*   File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/GXpense/",
				requestXML.getRequestXML()+".jpg");*/
        
        
//   	 File file= new File(R.drawable.susi);
       // postRequest.addHeader("Accept", "application/xml");
   	   //.addHeader("Content-Type", "application/xml");
     
        
        try
        {     
            //Set various attributes 
            MultipartEntity multiPartEntity = new MultipartEntity () ;
            multiPartEntity.addPart("profileId", new StringBody(requestXML.getRequestXML() != null ? requestXML.getRequestXML() : "")) ;
            multiPartEntity.addPart("profilePicture", new StringBody(file.getName()));//fileName != null ? fileName : file.getName())) ;
  
            FileBody fileBody = new FileBody(file, "application/octect-stream") ;
            //Prepare payload
            multiPartEntity.addPart("attachment", fileBody) ;
          
            
            //Set to request body
            postRequest.setEntity(multiPartEntity) ;
             
            //Send request
            HttpResponse response = client.execute(postRequest) ;
            
           
            BasicResponseHandler responseHandler = new BasicResponseHandler();
//    	    String strResponse = null;
            //Verify response if any
            if (response != null)
            {
            	
	            try
	            {
	            	strResponse = responseHandler.handleResponse(response);
	   	           
	 	        } catch (HttpResponseException e) {
		        	System.out.println("===  HttpResponseException =="+e.toString());
		            e.printStackTrace();  
		        } catch (IOException e) {
		        	System.out.println("===  IOException =="+e.toString());
		            e.printStackTrace();
		        }
	    
	        
	    }
	    Log.e("WCFTEST", "WCFTEST ********** Response" + strResponse);    


	 }
	 catch (Exception ex)
	 {
	 ex.printStackTrace();
	 }


		return strResponse;
	}
 
    
/*    public Bitmap executeImageDownload(
			TaskToFetchData requestXML) throws Exception {
		String strResponse = null;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		System.out.println("Get xml data == "+requestXML.getRequestXML()+"   req  "+requestXML.getUrl());
		HttpPost httppost = new HttpPost(requestXML.getUrl());

		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/xml");

		try {

			StringEntity entity = new StringEntity(requestXML.getRequestXML(), "UTF-8");
			entity.setContentType("application/xml");
			httppost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httppost);
			BasicResponseHandler responseHandler = new BasicResponseHandler();

			if (httpResponse != null) {
				try {
					Bitmap strResponse1 = responseHandler.(httpResponse);
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
	}*/
}