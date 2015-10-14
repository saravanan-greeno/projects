/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Saravanan <saravanan.s@greeno.in>,  
 */
package com.rainbowagri.liveprice;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbowagri.data.GenericAction;
import com.rainbowagri.data.OrderHistoryDetails;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XmlParser;

public class OrderHistory extends Activity {
	private TextView toDate;
	private TextView fromDate;

	private int year;
	private int month;
	private int day;
	ListView mlistView;
	XMLCreator xmlCreator;
	static final int DATE_PICKER_ID_FROMDATE = 1111;
	static final int DATE_PICKER_ID_TODATE = 2222;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
	ProgressDialog progressBar;
	int vendorId;
	String result, requestArg, fromDateValue, toDateValue, status;
	LinearLayout noInternetConnection, noDataLayout;
	Button tabToLoad;
	private ArrayList<OrderHistoryDetails> commodityList;
	ImageView imgAvatar;
	OrderDetailsAdapter mAdapter1;
	static boolean isInternetConnected = false;
	private final Calendar mCalendar = Calendar.getInstance();
	public static String ORDER_ID = "orderId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_history);
		
		GenericAction.trackerSetup("Order History", "seller");
		fromDate = (TextView) findViewById(R.id.fromDate);
		toDate = (TextView) findViewById(R.id.todate);
		noInternetConnection = (LinearLayout) findViewById(R.id.noInternetConnection);
		noDataLayout = (LinearLayout) findViewById(R.id.noDataLayout);
		tabToLoad = (Button) findViewById(R.id.tabToLoad);
		imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
		imgAvatar.setImageResource(R.drawable.okay2);
		xmlCreator = new XMLCreator();
		progressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressBar.setCancelable(true);
		//getActionBar().setTitle("Order History");
		mlistView = (ListView) findViewById(R.id.list);
		SharedPreferences mPreferences = this.getSharedPreferences(
				getString(R.string.shared_references_name), MODE_PRIVATE);
		vendorId = mPreferences.getInt(getString(R.string.coordinator_ref_id),
				0);

		fromDateValue = fromDate.getText().toString();
		toDateValue = toDate.getText().toString();

		/**
		 * when click on calendar class
		 */
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				new OnDateSetListener() {

					public void onDateSet(DatePickerDialog datePickerDialog,
							int year, int month, int day) {

						fromDate.setText(new StringBuilder().append(pad(day))
								.append("-").append(pad(month + 1)).append("-")
								.append(pad(year)));

						StringBuilder dateValue = new StringBuilder()
								.append(pad(year)).append("-")
								.append(pad(month + 1)).append("-")
								.append(pad(day));

						fromDateValue = dateValue.toString();

					}

				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));

		final DatePickerDialog datePickerDialogForTo = DatePickerDialog
				.newInstance(
						new OnDateSetListener() {

							public void onDateSet(
									DatePickerDialog datePickerDialog,
									int year, int month, int day) {

								toDate.setText(new StringBuilder()
										.append(pad(day)).append("-")
										.append(pad(month + 1)).append("-")
										.append(pad(year)));

								StringBuilder dateValue = new StringBuilder()
										.append(pad(year)).append("-")
										.append(pad(month + 1)).append("-")
										.append(pad(day));
								toDateValue = dateValue.toString();
							}

						}, mCalendar.get(Calendar.YEAR), mCalendar
								.get(Calendar.MONTH),
						mCalendar.get(Calendar.DAY_OF_MONTH));

		isInternetConnected = checkInternetConnection();
		fetchOrderHistory();

		fromDate.setOnClickListener(new OnClickListener() {

			private String tag;

			@Override
			public void onClick(View v) {
				datePickerDialog.show(getFragmentManager(), tag);
				;
			}
		});

		toDate.setOnClickListener(new OnClickListener() {

			private String tag;

			@Override
			public void onClick(View v) {
				datePickerDialogForTo.show(getFragmentManager(), tag);
				;
			}
		});

		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				OrderHistoryDetails items = (OrderHistoryDetails) mlistView
						.getItemAtPosition(position);

				OrderHistory.ORDER_ID = items.getOrderId();

				Intent intent = new Intent(OrderHistory.this,
						OrderStatusActivity.class);
				startActivity(intent);
			}
		});

		imgAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fromDate.getText().toString().equals("")
						&& toDate.getText().toString().equals("")) {
					fetchOrderHistory();
				} else if (fromDate.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Select from Date",
							Toast.LENGTH_LONG).show();
				} else if (toDate.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Select to Date",
							Toast.LENGTH_LONG).show();
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date1 = null;
					Date date2 = null;
					try {
						date1 = sdf.parse(fromDateValue);
						date2 = sdf.parse(toDateValue);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (date1.after(date2)) {
						Toast.makeText(getApplicationContext(),
								"Select valid Date", Toast.LENGTH_LONG).show();
					}

					else if (date1.before(date2)) {

						if (isInternetConnected) {
							new loadOrderDetails().execute();
						} else {

							Toast.makeText(getApplicationContext(),
									"No internet connection here",
									Toast.LENGTH_LONG).show();
						}

					} else if (date1.compareTo(date2) == 0) {

						if (isInternetConnected) {
							new loadOrderDetails().execute();
						} else {

							Toast.makeText(getApplicationContext(),
									"No internet connection here",
									Toast.LENGTH_LONG).show();
						}
					}
				}

			}
		});
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	/**
	 * getting current date from calender
	 */
	public String getCurrentDate() {
		DatePicker picker = new DatePicker(getApplicationContext());
		StringBuilder builder = new StringBuilder();

		builder.append((picker.getMonth() + 1) + "/");// month is 0 based
		builder.append(picker.getDayOfMonth() + "/");
		builder.append(picker.getYear());
		return builder.toString();
	}

	public void fetchOrderHistory() {
		if (isInternetConnected) {
			new loadOrderDetails().execute();
		} else {

			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * creating xml file of commodity details for request to server
	 */
	public String serializeCommodityDetails(String fromDateValue,
			String toDateValue, String vendorId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewPriceDetails = document
					.createElement("vendorOrderHistory");
			document.appendChild(viewPriceDetails);

			Element vendorIdValue = document.createElement("vendorId");
			viewPriceDetails.appendChild(vendorIdValue);
			vendorIdValue.appendChild(document.createTextNode(vendorId));

			Element fromDate = document.createElement("fromDate");
			viewPriceDetails.appendChild(fromDate);
			fromDate.appendChild(document.createTextNode(fromDateValue));

			Element toDate = document.createElement("toDate");
			viewPriceDetails.appendChild(toDate);
			toDate.appendChild(document.createTextNode(toDateValue));

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
	 * Aynsc task class for loading order details
	 */

	public class loadOrderDetails extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			progressBar.setMessage("Loading Details....");
			progressBar.setIndeterminate(false);
			progressBar.setCancelable(false);
			progressBar.show();
		}

		@Override
		protected String doInBackground(String... params) {

			if (fromDateValue.equalsIgnoreCase("")
					&& toDateValue.equalsIgnoreCase("")) {
				fromDateValue = "All";
				toDateValue = "All";
			}

			requestArg = serializeCommodityDetails(fromDateValue, toDateValue,
					"" + vendorId);

			try {
				result = executeMultiPartRequest();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String success) {
			super.onPostExecute(success);

			progressBar.dismiss();

			if (result != null) {

				progressBar.dismiss();

				/** Parse the response */
				XmlParser mXmlParser = new XmlParser();
				Document doc = mXmlParser.getDomElement(result);

				commodityList = new ArrayList<OrderHistoryDetails>();

				NodeList nodelist = doc.getElementsByTagName("view");

				for (int i = 0; i < nodelist.getLength(); i++) {

					Element element = (Element) nodelist.item(i);
					String orderId = mXmlParser.getValue(element, "orderId");
					String orderDate = mXmlParser
							.getValue(element, "orderDate");
					String mobileNo = mXmlParser.getValue(element, "mobileNo");
					String customerName = mXmlParser.getValue(element,
							"customerName");
					String Status = mXmlParser.getValue(element, "status");
					OrderHistoryDetails ri = new OrderHistoryDetails();

					ri.setDate(orderDate);
					ri.setMobileNUmber(mobileNo);
					ri.setName(customerName);
					ri.setOrderId(orderId);
					ri.setStatus(Status);
					commodityList.add(ri);

				}

				if (commodityList.size() == 0) {
					noDataLayout.setVisibility(View.VISIBLE);
					mlistView.setVisibility(View.GONE);
				} else {
					noDataLayout.setVisibility(View.GONE);
					mlistView.setVisibility(View.VISIBLE);
				}

				mlistView.invalidateViews();
				OrderDetailsAdapter mAdapter1 = new OrderDetailsAdapter(
						OrderHistory.this, R.layout.row_order_details,
						commodityList);
				mlistView.setAdapter(mAdapter1);
				mlistView.setTextFilterEnabled(true);
				mAdapter1.callWebService(new NotifyListener() {

					@Override
					public void taskCompleted() {
						new loadOrderDetails().execute();
					}

					@Override
					public void DoTaskCompleted(String commodityId,
							String vendorId, String priceStatus) {
						// TODO Auto-generated method stub
					}

					@Override
					public void isSearchActive(String active) {
						// TODO Auto-generated method stub

					}
				});
			} else {

				Toast.makeText(getApplicationContext(), "Session Expired",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * http connection set up and called web service method
	 */
	public String executeMultiPartRequest() throws Exception {

		String strResponse = null;
		/*HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(getString(R.string.Order_history));
		httppost.addHeader("Accept", "application/xml");
		httppost.addHeader("Content-Type", "application/xml");

		try {

			StringEntity entity = new StringEntity(requestArg, "UTF-8");
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

			//System.out.println("the getting url is---"+requestXML.getUrl());
                /* forming th java.net.URL object */
			URL url = new URL(getString(R.string.Order_history));
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
			wr.writeBytes(requestArg);
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
				//System.out.println("the getting url is- iokkkkkkk--"+requestXML.getUrl());
				//BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				StringBuilder stringBuilder = new StringBuilder();
				while ((line=br.readLine()) != null) {
					stringBuilder.append(line + '\n');
				}

				strResponse = stringBuilder.toString();

			} }finally{
			urlConnection.disconnect();
		}

		System.out.println("the getting response is---"+strResponse);
		return strResponse;
	}

	/**
	 * check for internet connection
	 */
	public boolean checkInternetConnection() {
		boolean connected = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED
				|| connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			connected = true;
		} else
			connected = false;

		if (connected)
			noInternetConnection.setVisibility(View.GONE);
		else
			noInternetConnection.setVisibility(View.VISIBLE);

		return connected;
	}
	
	 @Override
	public void onBackPressed() {
		 
		 Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			 
			OrderHistory.this.finish();

	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			System.out.println("homeee");
			 Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				 
				OrderHistory.this.finish();
			return true;

		default:
			break;
		}
		return true;
	}

}
