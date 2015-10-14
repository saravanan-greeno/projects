/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */

package com.rainbowagri.liveprice;

import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.data.GenericAction;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.model.CatagoryFactory;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;


public class EditCommodityPriceActivity extends Activity implements
		SearchView.OnQueryTextListener {
	CatagoryFactory catagoryFactory;
	long coordinatorRefId = 0;
	Spinner spinnervalue;
	CommodityPriceDetailsAdapter mAdapter1;
	String[] categoryList;
	MenuItem searchItem;
	private SearchView mSearchView;
	private ArrayList<RowItem> commodityList;
	String defaultSort = "Choose Category", spinnerValues, result, requestArg,
			categoryTextString, name;
	ArrayList<String> commodities = new ArrayList<String>();
	ArrayList<String> duplicateRemovalCategories = new ArrayList<String>();
	ArrayList<String> uniqueIds = new ArrayList<String>();
	ArrayList<String> categories = new ArrayList<String>();
	static boolean isInternetConnected = false;
	ArrayList<RowItem> mRowItems;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
	LinearLayout noInternetConnection, noDataLayout;
	ProgressDialog progressBar;
	ListView mListView;
	Button tabToLoad;
	Dialog dialog;

	TextView title;
	public static String VENDOR_ID = "vendorId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_commodity_price);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.edit_commodity_price);
		UploadCrashReport.startService(this);*/
		GenericAction.trackerSetup("Edit Price", "seller");
		mListView = (ListView) findViewById(R.id.liste);
		spinnervalue = (Spinner) findViewById(R.id.spinnercategory);
		catagoryFactory = new CatagoryFactory(this);
		noInternetConnection = (LinearLayout) findViewById(R.id.noInternetConnection);
		noDataLayout = (LinearLayout) findViewById(R.id.noDataLayout);
		tabToLoad = (Button) findViewById(R.id.tabToLoad);
		title = (TextView) findViewById(R.id.title);
		progressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressBar.setCancelable(true);

		mListView.setItemsCanFocus(true);

		Intent i = getIntent();
		String rupeeSymbol = getApplicationContext().getString(
				R.string.rupee_symbol);

		String amt = "amount in " + rupeeSymbol;
		title.setText(amt);
		if (i != null) {
			commodities = i.getStringArrayListExtra("Commodities");
			uniqueIds = i.getStringArrayListExtra("uniqueIds");
			categories = i.getStringArrayListExtra("categories");

			if (categories.size() != 0) {

			}
		}
		if (categories.size() != 0) {

			categories.remove(0);
			HashSet<String> listToSet = new HashSet<String>(categories);
			duplicateRemovalCategories = new ArrayList<String>(listToSet);

			spinnerCustomAdapter dataAdapter = new spinnerCustomAdapter(
					EditCommodityPriceActivity.this,
					R.layout.shopdetails_spinner_item,
					duplicateRemovalCategories);
			dataAdapter
					.setDropDownViewResource(R.layout.shopdetails_spinner_checkitem);
			spinnervalue.setAdapter(dataAdapter);
			dataAdapter.setNotifyOnChange(true);
		} else {

		}

		spinnervalue.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		SharedPreferences mPreferences = this.getSharedPreferences(
				getString(R.string.shared_references_name), MODE_PRIVATE);
		coordinatorRefId = mPreferences.getInt(
				getString(R.string.coordinator_ref_id), 0);

		mPreferences = this.getSharedPreferences("RainbowAgriLivePrice",
				MODE_PRIVATE);
		coordinatorRefId = mPreferences.getInt("coordinatorRefId", 0);

		EditCommodityPriceActivity.VENDOR_ID = "" + coordinatorRefId;
		isInternetConnected = checkInternetConnection();
		if (isInternetConnected) {

			spinnervalue.setSelection(0);
			webServiceTaskForViewCommodity("" + coordinatorRefId,
					duplicateRemovalCategories.get(0));

		} else {
			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}

		spinnervalue.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (spinnerValues.equalsIgnoreCase("All")) {

				} else {
					if (isInternetConnected) {
						webServiceTaskForViewCommodity("" + coordinatorRefId,
								spinnerValues);
					} else {
						Toast.makeText(getApplicationContext(),
								"No internet connection here",
								Toast.LENGTH_LONG).show();
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		tabToLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isInternetConnected = checkInternetConnection();
				if (isInternetConnected) {

				} else {

					Toast.makeText(getApplicationContext(),
							"No internet connection here", Toast.LENGTH_LONG)
							.show();
				}

			}
		});
		
		
		
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_price, menu);

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
					CommodityPriceDetailsAdapter ca = (CommodityPriceDetailsAdapter) mListView
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

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			System.out.println("th back coicked");
			finish();
			return true;
		}

		else if (id == R.id.Doneedit) {
			isInternetConnected = checkInternetConnection();
			if (isInternetConnected) {

				getWindow().getDecorView().clearFocus();
				webServiceTaskForEditCommodity();

			} else {
				Toast.makeText(getApplicationContext(),
						"No internet connection here", Toast.LENGTH_LONG)
						.show();
			}

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * class for spinner creation process
	 */
	public class spinnerCustomAdapter extends ArrayAdapter<String> {
		boolean isFirstTime = true;
		String firstElement;
		OnItemSelectedListener listener;

		public spinnerCustomAdapter(Context context, int textViewResourceId,
				ArrayList<String> filterData) {
			super(context, textViewResourceId, filterData);
		}

		public void setOnItemSelectedListener(OnItemSelectedListener listener) {
			this.listener = listener;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			notifyDataSetChanged();
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View row;
			LayoutInflater inflater = getLayoutInflater();
			row = inflater.inflate(R.layout.shopdetails_spinner_checkitem,
					parent, false);
			TextView label = (TextView) row.findViewById(R.id.text1);
			label.setText(categories.get(position).toString());
			spinnerValues = label.getText().toString();
			return row;
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			View view;
			LayoutInflater inflater = getLayoutInflater();
			view = inflater.inflate(R.layout.shopdetails_spinner_item, parent,
					false);
			TextView label = (TextView) view.findViewById(R.id.text1);
			label.setText(categories.get(position).toString());
			spinnerValues = label.getText().toString();
			return view;
		}

	}

	/**
	 * Method for internet connection check up
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
		return connected;
	}

	/**
	 * Method for view commodity process
	 */
	private void webServiceTaskForViewCommodity(String vendorId,
			String categoryName) {
		XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();

		XMLCreator xmlCreator = new XMLCreator();

		String vendorid = vendorId;
		String requestArg = xmlCreator.serializeViewCommodityPriceForEdit(
				vendorid, categoryName);

		createRequest.setRequestXMLdata(requestArg);

		createRequest.setUrl(getString(R.string.view_edit_price_commodity));
		String params = "";
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, createRequest);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {

				try {
					if (result != null) {

						progressBar.dismiss();

						/** Parse the response */
						XmlParser mXmlParser = new XmlParser();
						Document doc = mXmlParser.getDomElement(result
								.getResult());
						commodityList = new ArrayList<RowItem>();
						NodeList nodelist = doc
								.getElementsByTagName("Commodity");

						for (int i = 0; i < nodelist.getLength(); i++) {

							Element element = (Element) nodelist.item(i);
							String uniqueId = element.getAttribute("id");
							String CommodityName = mXmlParser.getValue(element,
									"commodityName");
							String Price = mXmlParser.getValue(element,
									"commodityPrice");
							String Quantity = mXmlParser.getValue(element,
									"quantity");

							try {
								name = GenericAction
										.convertHexaToString(CommodityName);

							} catch (Exception e) {
								// TODO: handle exception
							}
							RowItem ri = new RowItem();
							ri.setCommodityName(name);
							ri.setPrice(Price);
							ri.setId(uniqueId);
							ri.setQuantity(Quantity);
							commodityList.add(ri);

						}

						if (commodityList.size() == 0) {
							noDataLayout.setVisibility(View.VISIBLE);
							mListView.setVisibility(View.GONE);
						} else {
							noDataLayout.setVisibility(View.GONE);
							mListView.setVisibility(View.VISIBLE);
						}

						mListView.invalidateViews();
						mAdapter1 = new CommodityPriceDetailsAdapter(
								EditCommodityPriceActivity.this
										.getApplicationContext(),
								R.layout.category_list_edit, commodityList);
						mListView.setAdapter(mAdapter1);
						mListView.setItemsCanFocus(true);
						mListView.setTextFilterEnabled(true);

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
								if (active.equalsIgnoreCase("Yes")) {
									mListView.setVisibility(View.GONE);
									noDataLayout.setVisibility(View.VISIBLE);

								} else if (active.equalsIgnoreCase("No")) {
									mListView.setVisibility(View.VISIBLE);
									noDataLayout.setVisibility(View.GONE);

								}

							}
						});
					} else {
						Toast.makeText(getApplicationContext(),
								"Session Expired", Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {

					//displayToastMessage("Please try again later.");
					e.printStackTrace();
				}

			}
		});
		task.execute(params);
	}

	/**
	 * Method for Edit commodity process
	 */
	private void webServiceTaskForEditCommodity() {
		XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();

		String xmlRequest = XmlRequest();
		createRequest.setRequestXMLdata(xmlRequest);

		createRequest.setUrl(getString(R.string.edit_submit_price_commodity));
		String params = "";
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, createRequest);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {

				try {
					if (result != null) {

						XmlParser mXmlParser = new XmlParser();
						Document doc = mXmlParser.getDomElement(result
								.getResult());
						NodeList nodelist = doc
								.getElementsByTagName("ResponseCommodityEditPrice");

						Element element = (Element) nodelist.item(0);
						String status = mXmlParser.getValue(element, "status");

						if (status.equalsIgnoreCase("success")) {
							displayToastMessage("Price details have been successfully edited");

							Intent intent = new Intent();
							setResult(RESULT_OK, intent);
							EditCommodityPriceActivity.this.finish();

						} else if (status.equalsIgnoreCase("failure")) {
							displayToastMessage("Could not proceed .Please try again later.");
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Session Expired", Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {

					displayToastMessage("Could not proceed .Please try again later.");
					e.printStackTrace();
				}

			}
		});
		task.execute(params);
	}

	/**
	 * Method for display toast messages
	 */
	public void displayToastMessage(String msg) {
		Toast toast = Toast.makeText(EditCommodityPriceActivity.this, msg,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public boolean onQueryTextChange(String newText) {/* $ */
		CommodityPriceDetailsAdapter ca = (CommodityPriceDetailsAdapter) mListView
				.getAdapter();

		if (ca != null) {
			ca.getFilter().filter(newText);
			return true;
		}

		return false;

	}

	@Override
	public boolean onQueryTextSubmit(String query) {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
		return false;
	}

	/**
	 * Method for creating xml request
	 */
	public String XmlRequest() {
		String result = null;
		try {

			CommodityPriceDetailsAdapter adapter = (CommodityPriceDetailsAdapter) mListView
					.getAdapter();
			ArrayList<RowItem> commodites = (ArrayList<RowItem>) adapter
					.getRowItems();
			ArrayList<RowItem> productDetails = new ArrayList<RowItem>();
			for (int i = 0; i < commodites.size(); i++) {
				RowItem data = new RowItem();
				data.setPrice(commodites.get(i).getPrice());
				data.setId(commodites.get(i).getId());
				productDetails.add(data);
			}
			XMLCreator xmlCreator = new XMLCreator();
			result = xmlCreator.serializePriceEditForCommodities(
					productDetails, VENDOR_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	@Override
	public void onBackPressed() {
		 
		 Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			 
			EditCommodityPriceActivity.this.finish();

	} 
	
}
