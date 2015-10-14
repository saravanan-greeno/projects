/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 10 september 2014
 */
package com.rainbowagri.servercommunicator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.rainbowagri.data.GenericAction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



@SuppressLint("InlinedApi")
public class AsynchronousTaskToFetchData extends
		AsyncTask<String, Void, XMLRequestAndResponseData> {
	private static final String TAG = "FetchMyDataTask";
	ProgressDialog progressBar;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ConnectionDetector connection;
	File destinationFile;
	private XMLRequestAndResponseData processData;
	String destinationFileName = null, coordinatorId, response,
			originalVoiceName;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
	private Context context;
	private AsyncTaskCompleteListener<XMLRequestAndResponseData> listener;

	/**
	 * common async class for fetching and updating UI background
	 */
	public AsynchronousTaskToFetchData(Context ctx,
			XMLRequestAndResponseData createRequest) {

		this.context = ctx;
		this.processData = createRequest;
	}

	public void setFetchMyData(
			AsyncTaskCompleteListener<XMLRequestAndResponseData> listener) {
		this.listener = listener;

	}

	protected void onPreExecute() {
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

	protected XMLRequestAndResponseData doInBackground(String... params) {
		XMLRequestAndResponseData requestData = new XMLRequestAndResponseData();
		try {
			GenericAction.loadingMore = true;
			requestData.setRequestXMLdata(processData.getRequestXMLdata());
			requestData.setUrl(processData.getUrl());
			requestData.setResult(executeMultiPartRequest(requestData));

		} catch (Exception e) {

			e.printStackTrace();
		}

		return requestData;
	}

	protected void onPostExecute(XMLRequestAndResponseData responseData) {
		super.onPostExecute(responseData);
		progressBar.dismiss();
		listener.onTaskComplete(responseData);

	}

	/**
	 * Method for http connection establishment
	 */
	public String executeMultiPartRequest(XMLRequestAndResponseData requestXML)
			throws Exception {
		String strResponse = null;

		/*loseableHttpClient httpClient = HttpClients.createDefault();

		// Request configuration can be overridden at the request level.
		// They will take precedence over the one set at the client level.
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.build();

		HttpGet httpget = new HttpGet(url);

		httpget.setConfig(requestConfig);
		httpget.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Firefox/3.6.13");

// Execution context can be customized locally.
		HttpClientContext context = HttpClientContext.create();
		// Contextual attributes set the local context level will take
		// precedence over those set at the client level.
		context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);

		try {

			// Execute the method.
			HttpResponse response = httpClient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				throw new IllegalStateException("Method failed: " + response.getStatusLine());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			StringBuffer buf = new StringBuffer();
			String output;
			while ((output = br.readLine()) != null) {
				buf.append(output);
			}

			content = buf.toString();

		} catch (Exception e) {
			throw e;
		} finally {
			// Release the connection.
			httpClient.close();
		}
		*/



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
			wr.writeBytes(requestXML.getRequestXMLdata());
			wr.flush();
			wr.close();

                /* for Get request */
		//	urlConnection.setRequestMethod("POST");
			int statusCode = urlConnection.getResponseCode();

			System.out.println("the statusCode url is---"+statusCode);

                /* 200 represents HTTP OK */
			if (statusCode ==  200) {
				/*BufferedReader in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				System.out.println(response.toString());*/

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



		/*HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(requestXML.getUrl());

		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/xml");

		try {

			StringEntity entity = new StringEntity(
					requestXML.getRequestXMLdata(), "UTF-8");
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
		}*/

		//return strResponse;






}