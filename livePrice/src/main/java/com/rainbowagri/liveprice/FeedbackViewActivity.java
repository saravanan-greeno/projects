/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mirko.android.datetimepicker.date.DatePickerDialog;
import mirko.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnCloseListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.ServerCommunicator;
import com.rainbowagri.data.FeedbackData;
import com.rainbowagri.data.GenericAction;
import com.rainbowagri.servercommunicator.XMLCreator;

public class FeedbackViewActivity extends Activity implements OnQueryTextListener { 

	private TextView toDate;
	private TextView fromDate;

	private int year;
	private int month;
	private int day;
	 ListView mListview;
	 MenuItem searchItem;
		private SearchView mSearchView;
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
	private ArrayList<FeedbackData> feedbackList;
	ImageView proceed;
	FeedbackDetailsAdapter mAdapter1;
	static boolean isInternetConnected = false;
	private final Calendar mCalendar = Calendar.getInstance();
	public static String ORDER_ID = "orderId";
	JSONArray feedbacksDetails = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_view);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.feedback_view);
		UploadCrashReport.startService(this);*/
		
		GenericAction.trackerSetup("Feedback View", "seller");
		 fromDate = (TextView) findViewById(R.id.fromDate);
		toDate = (TextView) findViewById(R.id.todate);
		noInternetConnection = (LinearLayout) findViewById(R.id.noInternetConnection);
		noDataLayout = (LinearLayout) findViewById(R.id.noDataLayout);
		tabToLoad = (Button) findViewById(R.id.tabToLoad);
		proceed = (ImageView) findViewById(R.id.proceed);
		
	//	imgAvatar.setImageResource(R.drawable.okay2);
		xmlCreator = new XMLCreator();
		progressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
		progressBar.setCancelable(true);
		//getActionBar().setTitle("Order History");
		mListview = (ListView)findViewById(R.id.listdetails);
		SharedPreferences mPreferences = this.getSharedPreferences(
				getString(R.string.shared_references_name), MODE_PRIVATE);
		vendorId = mPreferences.getInt(getString(R.string.coordinator_ref_id),
				0);
		mListview.setTextFilterEnabled(true);
		fromDateValue = fromDate.getText().toString();
		toDateValue = toDate.getText().toString(); 
		
		
		MyApplications.tracker().setScreenName("feedback screen");

		MyApplications.tracker().send(
				new HitBuilders.EventBuilder().setCategory("goal")
						.setAction("click").setLabel("feedback").build());
		MyApplications.tracker().send(new HitBuilders.ScreenViewBuilder().build());
		
		 
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
	 	fetchfeedbackDetails();

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

		mListview.setOnItemClickListener(new OnItemClickListener() {
           @Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				System.out.println("the clicked items");
				FeedbackData items = (FeedbackData) mListview
						.getItemAtPosition(position);

				String comment= items.getComment();
				System.out.println("the clicked items are "+comment);
				Bundle bundle = new Bundle();
                bundle.putString("details",comment);
				Intent intent = new Intent(FeedbackViewActivity.this,
						FeedbackDetailViewActivity.class);
				intent.putExtras(bundle);
				FeedbackViewActivity.this.startActivity(intent);
			}
		});
		
		
		

		proceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fromDate.getText().toString().equals("") && toDate.getText().toString().equals("")) {
					fetchfeedbackDetails();
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
						 new loadfeedbacks().execute();
						} else {

							Toast.makeText(getApplicationContext(),
									"No internet connection here",
									Toast.LENGTH_LONG).show();
						}

					} else if (date1.compareTo(date2) == 0) {

						if (isInternetConnected) {
							new loadfeedbacks().execute();
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

	public void fetchfeedbackDetails() {
		if (isInternetConnected) {
			new loadfeedbacks().execute();
		} else {

			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}
	}

	 
	/**
	 * Aynsc task class for loading order details
	 */

	public class loadfeedbacks extends AsyncTask<String, Void, String> {
		String reqParams;
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
			 
				
/*
			requestArg = serializeCommodityDetails(fromDateValue, toDateValue,
					"" + vendorId);*/

			try {
				//result = executeMultiPartRequest();
				System.out.println("the date from ==="+fromDateValue);
				System.out.println("the date from ==="+toDateValue);
				 ServerCommunicator serverCommunicator = new ServerCommunicator();
				 
	                String requestURL =getString(R.string.feedback_view);
	                try {
	                    // 3. build jsonObject
	                    JSONObject jsonObject = new JSONObject();
	                    jsonObject.accumulate("vendorId",vendorId);
	                    jsonObject.accumulate("fromDate", fromDateValue);
	                    jsonObject.accumulate("toDate", toDateValue);

	                    // 4. convert JSONObject to JSON to String
	                    reqParams = jsonObject.toString();
	                    Log.d("Login request params:", reqParams);

	                } catch (JSONException e) {
	                    e.getLocalizedMessage();
	                }
	                result = serverCommunicator.postJSONData(reqParams, requestURL);
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
				 try {
	                	
                	 System.out.println("responseresponse iss--"+result);
                	processCategoryResponse(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
				 
				 
			} else {

				Toast.makeText(getApplicationContext(), "Session Expired",
						Toast.LENGTH_LONG).show();
			}
		}
		
		 public void processCategoryResponse(String response) throws JSONException {
		       // StringRelatedStuff stringRelatedStuff = new StringRelatedStuff();
		        JSONObject jsonObject = new JSONObject(response);
		        System.out.println("when parsing-");
		      //  fpoNameStr = stringRelatedStuff.convertHexaToString(jsonObject.get("name").toString());
		        
		      
		         feedbacksDetails = jsonObject.getJSONArray("feedback");
		         
		         if(feedbacksDetails.length()==0)
		         {
		        	 System.out.println("the sizeof the list is---");
						noDataLayout.setVisibility(View.VISIBLE);
						mListview.setVisibility(View.GONE);
					} 
		         else {
					System.out.println("not zeroo---");
					noDataLayout.setVisibility(View.GONE);
				   mListview.setVisibility(View.VISIBLE);
					 
		           feedbackList = new ArrayList<FeedbackData>();
		         // categoryList.clear();
		            for (int i = 0; i < feedbacksDetails.length(); i++) {
		        	 System.out.println("into for loops---");
		        	JSONObject c = feedbacksDetails.getJSONObject(i);
		        	  FeedbackData fb=new FeedbackData();

		        	    String commentDetails = GenericAction.convertHexaToString(c.getString("comment"));
			           //  String commentDetails = c.getString("comment");
			             String commentType = GenericAction.convertHexaToString(c.getString("commentType"));
			             String idValue = c.getString("id");
			             String mobileNumber = c.getString("mobile");
			             String commenterName = GenericAction.convertHexaToString(c.getString("name"));
			          
			             fb.setComment(commentDetails);
			             fb.setCommenterName(commenterName);
			             fb.setFeedbackId(idValue);
			             fb.setFeedbackType(commentType);
			             fb.setMobileNumber(mobileNumber);
		        	  
		            /* String commentDetails = c.getString("comment");
		             String commentType = c.getString("commentType");
		             String idValue = c.getString("id");
		             String mobileNumber = c.getString("mobile");
		             String commenterName = c.getString("name");
		          
		             fb.setComment(commentDetails);
		             fb.setCommenterName(commenterName);
		             fb.setFeedbackId(idValue);
		             fb.setFeedbackType(mobileNumber);
		             fb.setMobileNumber(commenterName);*/
		            
		             feedbackList.add(fb);
		             
		             if (feedbackList.size() == 0) {
		            	 
							noDataLayout.setVisibility(View.VISIBLE);
							mListview.setVisibility(View.GONE);
						} else {
							 
							noDataLayout.setVisibility(View.GONE);
							mListview.setVisibility(View.VISIBLE);
						}

		              mListview.invalidateViews();
						  mAdapter1= new FeedbackDetailsAdapter(
								FeedbackViewActivity.this, R.layout.feedback_row_view,
								feedbackList);
						mListview.setAdapter(mAdapter1);
						mListview.setTextFilterEnabled(true);
						
						
						mAdapter1.setActivateListner(new NotifyListener() {

							@Override
							public void taskCompleted() {
								// TODO Auto-generated method stub
				               }

							@Override
							public void DoTaskCompleted(String commodityId,
									String vendorId, String priceStatus) {
							}

							@Override
							public void isSearchActive(String active) {
								if (active.isEmpty()) 
								{
									Bundle bundle = new Bundle();
					                bundle.putString("details","No Feedback Given");
									Intent intent = new Intent(FeedbackViewActivity.this,
											FeedbackDetailViewActivity.class);
									intent.putExtras(bundle);
									FeedbackViewActivity.this.startActivity(intent); 
								} 
								else
								{
									Bundle bundle = new Bundle();
					                bundle.putString("details",active);
									Intent intent = new Intent(FeedbackViewActivity.this,
											FeedbackDetailViewActivity.class);
									intent.putExtras(bundle);
									FeedbackViewActivity.this.startActivity(intent); 
								}

							}
						});	
				      } 
					}
               } 
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

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feedback_view, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
		mSearchView.setIconifiedByDefault(true);
		MenuItem menuItem = menu.findItem(R.id.search);
		mSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		mSearchView.setOnQueryTextListener(this);

		int searchPlateId = mSearchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlate = mSearchView.findViewById(searchPlateId);
		if (searchPlate != null) {

			int searchTextId = searchPlate.getContext().getResources()
					.getIdentifier("android:id/search_src_text", null, null);
			TextView searchText = (TextView) searchPlate
					.findViewById(searchTextId);
			if (searchText != null) {
				searchText.setTextColor(Color.BLACK);
				searchText.setHintTextColor(Color.WHITE);

			}
		}

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			menuItem.setOnActionExpandListener(new OnActionExpandListener() {

				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					FeedbackDetailsAdapter ca = (FeedbackDetailsAdapter) mListview
							.getAdapter();

					if (ca != null) {
						ca.getFilter().filter("");
						return true;
					}

					return true;

				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {
					return true;
				}
			});
		} else {
			mSearchView.setOnCloseListener(new OnCloseListener() {

				@Override
				public boolean onClose() {
					// TODO Auto-generated method stub
					return true;
				}
			});
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		FeedbackDetailsAdapter ca = (FeedbackDetailsAdapter) mListview
				.getAdapter();

		if (ca != null) {
			ca.getFilter().filter(newText);
			return true;
		}

		return false;
	}

}

