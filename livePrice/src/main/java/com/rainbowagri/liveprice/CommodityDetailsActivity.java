/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.service.MarketService;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.greeno.Util.XmlCreator;
//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.Configuration;
import com.rainbowagri.ServerCommuicator.ServerCommunicator;
import com.rainbowagri.data.Connectivity;

import com.rainbowagri.data.GenericAction;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.data.Vendor;

import com.rainbowagri.liveprice.AddCommodityActivity.OnUpdateListener;
import com.rainbowagri.liveprice.reports.DailyPdfReport;
import com.rainbowagri.model.CatagoryFactory;

import com.rainbowagri.profilepage.ProfilePageViewActivity;

import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.ImageProcessing;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class CommodityDetailsActivity extends Activity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	private ActionBar actionBar;
	private SearchView mSearchView;

	private ArrayList<RowItem> commodityList = new ArrayList<RowItem>();
	private ArrayList<RowItem> tempCommodityList = new ArrayList<RowItem>();
	ImageView picture;
	public static final String COMMODITYNAME = "commodityname";
	public String mSDCardPath = "";
	public static final String VARIETY = "variety";
	public static final String DATE = "date";
	public static final String PRICE = "price";
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
	String result, requestArg, categoryTextString, spinnerValueForCategory,
			profileImages = "", editedPosition, searchText = "",
			filterText = "", exactPrice, shopName, profileId,
			shopStatusActive = "No", defaultTextForCategorySpinner = "All",
			defaultTextForSpinnerAtDisplay = "Filter by";
	String varietyName;
	public static String mainCategory;
	ArrayList<RowItem> mRowItems;
	ProgressDialog progressBar;
	LinearLayout noInternetConnection, noDataLayout;
	Button tabToLoad;
	int visibleCount;
	Typeface currency;
	MenuItem searchItem;

	private CustomListViewAdapter mAdapter;
	private final int CATEGORY_SELECTION = 1;
	private final int ADD_COMMODITY = 2;
	private final int VIEW_PROFILE = 3;
	private final int EDIT_COMMODITY_PRICE = 4;
	private final int EDIT_PRICE = 6;
	private final int ORDER_HISTORY = 10;
	ImageView profileImageView;
	CatagoryFactory catagoryFactory;

	TextView title, currencyText, countOfCommodity, mainCategoryText;

	public static String totalCount = "totalCount";
	long coordinatorRefId = 0;
	int commodityId = 0;
	SharedPreferences mPreferences;
	Button priceHideOrShow;
	static boolean isInternetConnected = false;
	Dialog dialog;
	ListView mListView;
	Spinner spinnervalue;
	Toast toast;
	public static String IsObjectEdited = "SaveOrEdit";
	ImageProcessing imageProcessing = new ImageProcessing();
	public static String VENDOR_ID = "vendorId";
	ArrayList<HashMap<String, Object>> groupArrayList = new ArrayList<HashMap<String, Object>>();

	ArrayList<String> commodities = new ArrayList<String>();
	ArrayList<String> uniqueIds = new ArrayList<String>();
	ArrayList<String> categories = new ArrayList<String>();
	ArrayList<String> defaultCategories = new ArrayList<String>();
	ArrayList<String> duplicates = new ArrayList<String>();
	private Boolean exit = false;
	private Boolean filterExit = false;
	Bitmap profileImageBitmap = null;
	public static String SHOP_STATUS = "shopstatus";
	private Menu menu;
	public static String SHOP_SHARED_PREF_NAME = "active";
	public static final String STATUS = "IsActive";
	int listViewPostion = 0;
	int pageNumber = 1;
	String[] SortFilter;
	String[] SortData, OrganicGroceries, Grocery, FruitsVegetables,
			CattlePoultry, DairyProducts, Seed, NurserySaplings,
			farmingProducts,machinary,handicraft,others,terraceGardening;

	@Override
	public void onBackPressed() {
		if (exit) {
			finish(); // finish activity
		} else {
			Toast.makeText(this, "Press Back again to Exit.",
					Toast.LENGTH_SHORT).show();
			exit = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					exit = false;
				}
			}, 3 * 1000);

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.commodity_view_layout);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.commodity_view_layout);
		UploadCrashReport.startService(this);*/
		// Tracker.
		// myap.trackPageView("/HomeScreen");
	//	MyApp myapp = new MyApp();
		
	//	int a = 10/0;
	//	System.out.println(""+a);
		
		MyApplications.tracker().setScreenName("Commodity screen");

	        // Send the custom dimension value with a screen view.
	        // Note that the value only needs to be sent once.
		MyApplications.tracker().send(new HitBuilders.ScreenViewBuilder()
	            .setCustomDimension(1, "premiumUser")
	            .build()
	        );
		//GoogleAnalytics.getInstance(CommodityDetailsActivity.this.getBaseContext()).dispatchLocalHits();
		
		 
		mListView = (ListView) findViewById(R.id.list);
		noInternetConnection = (LinearLayout) findViewById(R.id.noInternetConnection);
		noDataLayout = (LinearLayout) findViewById(R.id.noDataLayout);
		tabToLoad = (Button) findViewById(R.id.tabToLoad);
		countOfCommodity = (TextView) findViewById(R.id.count);
		mainCategoryText = (TextView) findViewById(R.id.mainCategory);
		progressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		progressBar.setCancelable(true);
		spinnervalue = (Spinner) findViewById(R.id.spinnercategory);
		catagoryFactory = new CatagoryFactory(this);
		catagoryFactory.onCreate();


		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{

			String vendor = bundle.getString("userId");

			SharedPreferences mPreferences = this.getSharedPreferences(
					"RainbowAgriLivePrice", MODE_PRIVATE);
			SharedPreferences.Editor mEditor = mPreferences.edit();
			mEditor.putInt("coordinatorRefId", Integer.parseInt(vendor));
			mEditor.putString("coordinatorUserId",bundle.getString("mobile"));
			mEditor.putString("mobilenumber", bundle.getString("mobile"));

			mEditor.putString("shopName",
					bundle.getString("shopName"));
			mEditor.putString("profileId",
					bundle.getString("profileId"));

			mEditor.putString("mainCategory",
					bundle.getString("mainCategory"));
            mEditor.commit();
		 }


		isInternetConnected = checkInternetConnection();
		if (isInternetConnected) {
			CheckVersion details = new CheckVersion(this);
			details.execute();

		} else {
			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}

		ApplicationInfo applicationInfo;
		try {
			  applicationInfo = getApplicationContext().getPackageManager()
				    .getPackageInfo(getApplicationContext().getPackageName(), 0).applicationInfo;
	        PackageManager p = getApplicationContext().getPackageManager();
	        String label = p.getApplicationLabel(applicationInfo).toString();
	        System.out.println("the getting name of the appp==="+label);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		
		createFolderInSDCard();
		ActionBar actionBar = getActionBar();
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#f4e4d4")));

		totalCount = "0";

		countOfCommodity.setText("Items :" + 0 + " Of " + totalCount);
		Connectivity.getNetworkInfo(getApplicationContext());

		mPreferences = this.getSharedPreferences("RainbowAgriLivePrice",
				MODE_PRIVATE);
		coordinatorRefId = mPreferences.getInt("coordinatorRefId", 0);
		shopName = mPreferences.getString("shopName", "");
		profileId = mPreferences.getString("profileId", "");
		mainCategory = mPreferences.getString("mainCategory", "");

		System.out.println("the selected main categories are ----"
				+ mainCategory);

		String[] temp;
		String delimiter = "//";
		temp = mainCategory.split(delimiter);
		for (int i = 0; i < temp.length; i++) {

		}

		coordinatorRefId = mPreferences.getInt("coordinatorRefId", 0);

		CommodityDetailsActivity.VENDOR_ID = "" + coordinatorRefId;

		actionBar.setTitle(shopName);

		isInternetConnected = checkInternetConnection();

		countOfCommodity.setVisibility(View.VISIBLE);
		mAdapter = new CustomListViewAdapter(CommodityDetailsActivity.this,
				R.layout.commodity_details_row, commodityList);
		mListView.setAdapter(mAdapter);
		spinnervalue.setFocusable(true);
		spinnervalue.setSelected(true);
		// addCategory();
		System.out.println("the BEFORE IF ");
		int count = catagoryFactory.getRecordsCount();
		System.out.println("getRecordsCount" + count);
		if (count == 0) {

			System.out.println("the count 000");
			saveDefaultCategory();
		}
		System.out.println("the AFTER IF ");
		// TaskToFetchCommodities();

		mListView.setOnScrollListener(new OnScrollListener() {
			int currentScrollState = 0, currentFirstVisibleItem = 0,
					currentVisibleItemCount = 0, lastItem = 0, totalItem = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				this.currentScrollState = scrollState;
				this.isScrollCompleted(mListView.getLastVisiblePosition());

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				this.currentFirstVisibleItem = firstVisibleItem;
				this.currentVisibleItemCount = visibleItemCount;
				this.lastItem = firstVisibleItem + visibleItemCount;
				this.totalItem = totalItemCount;

				visibleCount = this.lastItem;
				countOfCommodity.setVisibility(View.VISIBLE);
				countOfCommodity.setText("" + visibleCount + " Of "
						+ totalCount);

			}

			private void isScrollCompleted(int pos) {
				if (lastItem == totalItem
						&& this.currentScrollState == SCROLL_STATE_IDLE) {
					// *** In this way I detect if there's been a scroll which
					// has completed ***//*
					// *** do the work for load more date! ***//*
					if (!GenericAction.loadingMore) {
						GenericAction.loadingMore = true;
						listViewPostion = pos;

						// fetchSearchResult();

						TaskToFetchCommodities();

					}

				}

			}

		});
		spinnervalue.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				filterExit = true;
				if (categories.size() == 0) {
					addCategory();
				}
				return false;
			}
		});

		spinnervalue.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (filterExit) {
					String spinnerValue = adapterView.getItemAtPosition(
							position).toString();
					System.out.println("the selected spinner value is===="
							+ spinnerValue);

					filterExit = false;
					filterText = spinnerValue;
					pageNumber = 1;
					commodityList.clear();
					tempCommodityList.clear();
					mAdapter.notifyDataSetChanged();
					totalCount = "0";
					countOfCommodity.setText("" + 0 + " Of " + 0);
					TaskToFetchCommodities();

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
					TaskToFetchCommodities();

				} else {
					Toast.makeText(getApplicationContext(),
							"No internet connection here", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				dialog = new Dialog(CommodityDetailsActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

				dialog.setContentView(R.layout.manage_prices);
				// dialog.setTitle("Manage Price");

				isInternetConnected = checkInternetConnection();
				final ImageView commodityImageView = (ImageView) parent
						.getChildAt(
								position - mListView.getFirstVisiblePosition())
						.findViewById(R.id.list_commodity_image);
				final LinearLayout editProces = (LinearLayout) dialog
						.findViewById(R.id.changeLayout);
				final LinearLayout delelteProcess = (LinearLayout) dialog
						.findViewById(R.id.deleteLayout);
				Typeface face = Typeface.createFromAsset(getAssets(),
						"fonts/Sanchez-Regular_0.ttf");
				final TextView change = (TextView) dialog
						.findViewById(R.id.change);
				final TextView delete = (TextView) dialog
						.findViewById(R.id.delete);
				change.setTypeface(face);
				delete.setTypeface(face);

				RowItem rowItems = (RowItem) mListView
						.getItemAtPosition(position);
				Bitmap comBitmap = rowItems.getCommodityImage();

				dialog.show();

				/**
				 * Edit details functionality
				 */

				editProces.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {

							if (isInternetConnected) {
								
							 
								MyApplications.tracker().enableAdvertisingIdCollection(true);
								
								
								
								
								editProces.setBackgroundColor(Color.GRAY);
								delelteProcess.setBackgroundColor(Color.rgb(
										224, 235, 202));

								RowItem item = (RowItem) mListView
										.getItemAtPosition(position);
								String commodityName = item.getCommodityName()
										.toString();
								String varietyNamee = item.getVarietyName()
										.toString();
								varietyName = varietyNamee;
								System.out.println("the passing category is==="
										+ varietyNamee);
								String mainCategoryName = item
										.getMainCategory().toString();
								String date = item.getDate().toString();
								String priceValue = item.getPrice().toString();
								String quantityandUnits = item.getUnit()
										.toString();
								String vendorId = item.getId().toString();

								GenericAction.commodityId = vendorId;
								String minQty = item.getMinimumQuantity()
										.toString();
								String unitTagValue = item.getUnitTag()
										.toString();

								String quantity = item.getQuantity().toString();

								System.out
										.println("the getting getDiscountInPercentage"
												+ item.getDiscountInPercentage()
														.toString());

								String discount = item
										.getDiscountInPercentage().toString();
								String commodityInformation = item
										.getCommodityInfo().toString();

								BitmapDrawable drawable = (BitmapDrawable) commodityImageView
										.getDrawable();
								Bitmap bitmap = drawable.getBitmap();

								if (bitmap != null) {
									boolean returnValue = imageProcessing
											.saveImageLocally(mSDCardPath,
													"temp.jpg", bitmap);

								} else

								if (priceValue.endsWith("/"))
									priceValue = priceValue.substring(0,
											priceValue.length() - 1);

								Bundle bundle = new Bundle();

								bundle.putString("refId", vendorId);
								bundle.putString("commodityName", commodityName);
								bundle.putString("units", quantityandUnits);
								bundle.putString("category", varietyNamee);
								bundle.putString("lastUpdatedDate", date);
								bundle.putString("price", priceValue);
								bundle.putString("mainCategory",
										mainCategoryName);
								bundle.putString("quantity", quantity);
								bundle.putString("discount", discount);
								bundle.putString("minQty", minQty);
								bundle.putString("minUnit", unitTagValue);
								bundle.putString("commInfo",
										commodityInformation);
								GenericAction.editData = null;

								Intent intent = new Intent(
										getApplicationContext(),
										AddCommodityActivity.class);
								intent.putExtras(bundle);
								CommodityDetailsActivity.this
										.startActivityForResult(intent,
												EDIT_COMMODITY_PRICE);

								dialog.dismiss();
							} else {
								displayToastMessage("No internet connection");
								dialog.dismiss();
							}

						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});

				/**
				 * Deleting commodity functionality
				 */
				delelteProcess.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						editProces.setBackgroundColor(Color.rgb(224, 235, 202));
						delelteProcess.setBackgroundColor(Color.GRAY);

						if (isInternetConnected) {

							RowItem item = (RowItem) mListView
									.getItemAtPosition(position);
							String commodityName = item.getCommodityName()
									.toString();
							String vendorId = item.getId().toString();
							String categoryName = item.getVarietyName()
									.toString();

							GenericAction.commodityId = vendorId;
							webServiceTaskForDelete(""
									+ item.getId().toString(), commodityName,
									categoryName, position);
						} else {
							displayToastMessage("No internet connection");
							dialog.dismiss();
						}

					}
				});

				/*********************************************************/

			}
		});

	}

	private void doTheTaskToFetchData() {

		if (isInternetConnected) {

		} else {

			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.commodity_details, menu);
		MenuItem menuItem1 = menu.findItem(R.id.active);

		SharedPreferences mPreferences = this.getSharedPreferences(
				CommodityDetailsActivity.SHOP_SHARED_PREF_NAME, MODE_PRIVATE);

		String status = mPreferences.getString(CommodityDetailsActivity.STATUS,
				null);

		if (status.equalsIgnoreCase("Yes")) {
			menuItem1.setTitle("Deactivate Shop");
			// menuItem1.setIcon(getResources().getDrawable(R.drawable.loader));
		}

		else {
			menuItem1.setTitle("Activate Shop");
			// menuItem1.setIcon(getResources().getDrawable(R.drawable.loader));
		}
		// updateMenuTitles();
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();

		MenuItem menuItem = menu.findItem(R.id.search);
		mSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		mSearchView.setOnQueryTextListener(this);

		int searchPlateId = mSearchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlate = mSearchView.findViewById(searchPlateId);
		if (searchPlate != null) {

			// searchPlate.setBackgroundColor(Color.DKGRAY);
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

					// IfSearhIsCleared();
					searchText = "";
					mSearchView.clearFocus();
					IntiateTaskWithFreshData();
					return true;

					// return true;
				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {

					// TODO Auto-generated method stub
					return true;
				}
			});
		} else {
			mSearchView.setOnCloseListener(new OnCloseListener() {

				@Override
				public boolean onClose() {

					System.out.println("closssss");
					// TODO Auto-generated method stub
					return true;
				}
			});
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		/**
		 * Customize menu items based on user login status
		 */
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT) {

			menu.findItem(R.id.exportpdf).setVisible(true);

		} else {

			menu.findItem(R.id.exportpdf).setVisible(false);

		}
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {/* $ */
		searchText = newText;
		if (searchText.length() == 0) {
			IfSearhIsCleared();
		}

		return false;

	}

	@Override
	public boolean onQueryTextSubmit(String query) {

		searchText = query;
		onQueryTextSubmit();

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.addcommodity) {

			if (!filterText.equalsIgnoreCase("All")
					|| !filterText.equalsIgnoreCase("Filter By")) {
				filterText = "";
				mSearchView.clearFocus();
				searchText = "";
				spinnervalue.setSelection(0);
				IntiateTaskWithFreshData();
			}

			mSearchView.clearFocus();
			searchText = "";

			GenericAction.saveData = null;
			Intent intent = new Intent(getApplicationContext(),
					AddCommodityActivity.class);
			startActivityForResult(intent, ADD_COMMODITY);
			return true;
		}

		else if (id == R.id.search) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(),
					InputMethodManager.RESULT_UNCHANGED_SHOWN);
			return true;

		}

		/*
		 * else if (id == R.id.filter) { Intent intent = new
		 * Intent(getApplicationContext(), CategoryActivity.class);
		 * intent.putExtra("filter", "true"); startActivityForResult(intent,
		 * CATEGORY_SELECTION); }
		 */

		else if (id == R.id.Orderhistory) {
			Intent intent = new Intent(getApplicationContext(),
					OrderHistory.class);
			startActivityForResult(intent, ORDER_HISTORY);

			// asda
			// CommodityDetailsActivity.this.finish();
		}

		else if (id == R.id.EditPrice) {
			if (categories.size() == 0) {
				displayToastMessage("Please add at least a single commodity to proceed.");
			} else {
				Intent intent = new Intent(getApplicationContext(),
						EditCommodityPriceActivity.class);
				intent.putStringArrayListExtra("Commodities", commodities);
				intent.putStringArrayListExtra("uniqueIds", uniqueIds);
				intent.putStringArrayListExtra("categories", duplicates);
				startActivityForResult(intent, EDIT_PRICE);
			}

		} else if (id == R.id.active) {
			SharedPreferences mPreferences = this.getSharedPreferences(
					CommodityDetailsActivity.SHOP_SHARED_PREF_NAME,
					MODE_PRIVATE);
			String status = mPreferences.getString(
					CommodityDetailsActivity.STATUS, null);

			if (status.equalsIgnoreCase("Yes")) {

				alertShopActiveOrInactiov("No", "" + coordinatorRefId);

			}

			else {

				alertShopActiveOrInactiov("Yes", "" + coordinatorRefId);
			}
		} else if (id == R.id.logout) {
                      logout();
		/*	SharedPreferences settings = this.getSharedPreferences(
					"RainbowAgriLivePrice", Context.MODE_PRIVATE);
			settings.edit().clear().commit();
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			CommodityDetailsActivity.this.startActivity(intent);
			CommodityDetailsActivity.this.finish();
			catagoryFactory.deleteTable();
            finish();*/
		} else if (id == R.id.Refresh) {
			// MyApp mm=new MyApp();
			MyApplications.tracker().setScreenName("Commodity screen");

			MyApplications.tracker().send(
					new HitBuilders.EventBuilder().setCategory("Menu")
							.setAction("click").setLabel("Refresh").build());
			MyApplications.tracker().send(new HitBuilders.ScreenViewBuilder().build());
			/*MyApplications.tracker().send(new HitBuilders.ScreenViewBuilder()
			  .set("&cd", "Commodity screen")
			  .build()
			);*/
			
			
			/*
			 * GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			 * Tracker tracker = analytics.newTracker("UA-65520509-2"); // Send
			 * hits to tracker id UA-XXXX-Y
			 * 
			 * // All subsequent hits will be send with screen name =
			 * "main screen" tracker.setScreenName("Commodity screen");
			 * 
			 * tracker.send(new HitBuilders.EventBuilder() .setCategory("Menu")
			 * .setAction("click") .setLabel("Refresh") .build());
			 */

			isInternetConnected = checkInternetConnection();
			if (isInternetConnected) {
				addCategory();
				// addCategory();
				// IntiateTaskWithFreshData();
				TaskToFetchCommodities();

			} else {
				Toast.makeText(getApplicationContext(),
						"No internet connection here", Toast.LENGTH_LONG)
						.show();
			}

		} else if (id == R.id.exportpdf) {

			try {
				if (commodityList != null) {
					DailyPdfReport mDailyReport = new DailyPdfReport(
							commodityList, shopName);
					mDailyReport = mDailyReport.createPdf();
					// Save pdf document
					if (isExternalStorageWritable()) {
						File file = new File(
								Environment.getExternalStorageDirectory()
										+ "/Rainbow_Sell.pdf");
						try {
							FileOutputStream os = new FileOutputStream(file);
							mDailyReport.writeTo(os);
							Log.i("PDF", "Done -- "
									+ file.getAbsolutePath().toString());
							mDailyReport.close();
							os.close();
						} catch (FileNotFoundException e) {
							Log.i("PDF", "File not found");
							e.printStackTrace();
						} catch (IOException e) {
							Log.i("PDF", "IO exception");
							e.printStackTrace();
						}

						// Update UI
						Toast.makeText(this, "Pdf created", Toast.LENGTH_LONG)
								.show();
						Intent target = new Intent(Intent.ACTION_VIEW);
						target.setDataAndType(Uri.fromFile(file),
								"application/pdf");
						target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

						Intent intent = Intent.createChooser(target,
								"Open File");
						try {
							startActivity(intent);
						} catch (ActivityNotFoundException e) {
							// Instruct the user to install a PDF reader here,
							// or something
						}
					} else {
						Toast.makeText(this, "SD card not found",
								Toast.LENGTH_LONG).show();
					}
				} else {
					// TODO Inform user about null data
					Toast.makeText(this,
							"Pdf creation aborted due to empty data",
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				Toast.makeText(this, "Not supported", Toast.LENGTH_LONG).show();
			}

		}

		else if (id == R.id.myprofile) {
			if (checkInternetConnection()) {
				Intent mIntent1 = new Intent(CommodityDetailsActivity.this,
						ProfilePageViewActivity.class);
				mIntent1.putExtra("coordinatorUniqueId", "" + coordinatorRefId);
				mIntent1.putExtra("appName", "LivePrice");
				startActivityForResult(mIntent1, VIEW_PROFILE);
			} else {
				displayToastMessage("No internet connection.");
			}
		}

		else if (id == R.id.feedback) {
			
			
			
			Intent intent = new Intent(getApplicationContext(),
					FeedbackViewActivity.class);
			CommodityDetailsActivity.this.startActivity(intent);

		}

		/*
		 * else if (id == R.id.viewtable) { Intent intent = new
		 * Intent(getApplicationContext(), AndroidDatabaseManager.class);
		 * CommodityDetailsActivity.this.startActivity(intent);
		 * 
		 * }
		 */

		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		GenericAction.loadingMore = true;

		if (requestCode == CATEGORY_SELECTION) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				categoryTextString = bundle.getString("categoryName");

				ArrayList<RowItem> searchList = new ArrayList<RowItem>();
				if (categoryTextString.equalsIgnoreCase("All")) {
					searchList = commodityList;
				} else {
					int i = 0;

					if (commodityList != null) {
						for (RowItem mRowItem : commodityList) {

							String varietyName = mRowItem.getVarietyName();

							if ((varietyName.toLowerCase())
									.equals(categoryTextString.toLowerCase())) {

								searchList.add(commodityList.get(i));
							} else {
							}
							i++;
						}
					} else {

					}
				}

				if (searchList != null) {
					if (searchList.size() == 0) {
						noDataLayout.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					} else {
						noDataLayout.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
					}
					CustomListViewAdapter mAdapter1 = new CustomListViewAdapter(
							CommodityDetailsActivity.this,
							R.layout.commodity_details_row, searchList);
					mListView.setAdapter(mAdapter1);
					mAdapter1.setActivateListner(new NotifyListener() {

						@Override
						public void taskCompleted() {
							// TODO Auto-generated method stub
							doTheTaskToFetchData();
						}

						@Override
						public void DoTaskCompleted(String commodityId,
								String vendorId, String priceStatus) {
							alertDialogWindow(priceStatus, commodityId,
									vendorId);

						}

						@Override
						public void isSearchActive(String active) {
							// TODO Auto-generated method stub

						}
					});

				} else {
					displayToastMessage("No data to filter ");
					if (searchList.size() == 0) {
						noDataLayout.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					} else {
						noDataLayout.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
					}
				}

			}
		} else if (requestCode == ADD_COMMODITY) {
			if (resultCode == RESULT_OK) {

				isInternetConnected = checkInternetConnection();
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				if (isInternetConnected) {

					performSearch();
					hideSoftKeyboard();
					updateNewlyAddedCommodityDetails(GenericAction.saveData);

				} else {
					Toast.makeText(getApplicationContext(),
							"No internet connection here", Toast.LENGTH_LONG)
							.show();
				}
			}
		}

		else if (requestCode == ORDER_HISTORY) {
			if (resultCode == RESULT_OK) {
				System.out.println("when click back button");
				GenericAction.loadingMore = true;
				// spinnervalue.setSelection(0);
				IntiateTaskWithFreshData();

			}
		} else if (requestCode == EDIT_PRICE) {
			if (resultCode == RESULT_OK) {
				// filterText = "";
System.out.println("it comes ghere");
				mSearchView.clearFocus();
				// searchText = "";
				// spinnervalue.setSelection(0);
				IntiateTaskWithFreshData();

			}
		} else if (requestCode == EDIT_COMMODITY_PRICE) {
			if (resultCode == RESULT_OK) {

				try {
					isInternetConnected = checkInternetConnection();
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,
							0);
					if (isInternetConnected) {

						UpdateDataForEditCommodityPrice();

					} else {

						Toast.makeText(getApplicationContext(),
								"No internet connection here",
								Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}

		if (requestCode == VIEW_PROFILE && resultCode == RESULT_OK) {
			IntiateTaskWithFreshData();
			// Update Shop Name
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				ActionBar actionBar = getActionBar();
				shopName = bundle.getString("shopName");
				
				//String name = GenericAction.convertHexaToString(shopName);
				addCategory();
				System.out.println("the receiving tiel is ==" + shopName);
				// title.setText(shopName);
				actionBar.setTitle(shopName);
				SharedPreferences mPreferences = getApplication()
						.getSharedPreferences("RainbowAgriLivePrice",
								MODE_PRIVATE);
				SharedPreferences.Editor mEditor = mPreferences.edit();
				mEditor.putString("shopName", shopName);
				mEditor.commit();

			}

		}

	}

	private void TaskToFetchCommodities() {
		if (isInternetConnected) {

			if (filterText.equalsIgnoreCase("All")
					|| filterText.equalsIgnoreCase("Filter By")) {
				filterText = "";
			}
			Vendor vendor = new Vendor();
			vendor.setVendorId("" + coordinatorRefId);
			XMLCreator xmlReatror = new XMLCreator();
			System.out.println("Page no tag" + pageNumber);
			requestArg = xmlReatror
					.serializeCommodityDetailsForSearch("" + coordinatorRefId,
							"" + pageNumber, searchText, filterText);
			System.out.println("the request requestArg file is===="
					+ requestArg);
			XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();

			createRequest.setUrl(getString(R.string.view_commodity));
			createRequest.setRequestXMLdata(requestArg);
			AsynchronousTaskToFetchData taskToFetch = new AsynchronousTaskToFetchData(
					CommodityDetailsActivity.this, createRequest);
			taskToFetch
					.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

						@Override
						public void onTaskComplete(
								XMLRequestAndResponseData result) {
							try {
								parserCommodityXmlData(result);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});
			taskToFetch.execute("");

		} else {

			GenericAction.loadingMore = false;
			Toast.makeText(getApplicationContext(),
					"No internet connection here", Toast.LENGTH_LONG).show();
		}

	}

	public class spinnerCustomAdapter extends ArrayAdapter<String> {
		boolean isFirstTime;
		String firstElement;

		public spinnerCustomAdapter(Context context, int textViewResourceId,
				String[] filterData, String value) {
			super(context, textViewResourceId, filterData);
			this.isFirstTime = true;
			setDefaultText(value);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			if (isFirstTime) {
				SortFilter[0] = firstElement;
				isFirstTime = false;
			}
			View row;
			LayoutInflater inflater = getLayoutInflater();
			row = inflater.inflate(R.layout.shopdetails_spinner_checkitem,
					parent, false);
			TextView label = (TextView) row.findViewById(R.id.text1);
			label.setText(SortFilter[position]);
			spinnerValueForCategory = label.getText().toString();
			return row;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			View view;
			LayoutInflater inflater = getLayoutInflater();
			view = inflater.inflate(R.layout.shopdetails_spinner_checkitem,
					parent, false);
			TextView label = (TextView) view.findViewById(R.id.text1);
			label.setText(SortFilter[position]);

			spinnerValueForCategory = label.getText().toString();
			System.out.println("the spinner value in category=="
					+ spinnerValueForCategory);
			return view;
		}

		public void setDefaultText(String defaultText) {

			this.firstElement = SortFilter[0];
			SortFilter[0] = defaultText;
		}
	}

	private void parserCommodityXmlData(XMLRequestAndResponseData result) {

		/** Parse the response */
		try {

			XmlParser mXmlParser = new XmlParser();
			Document doc = mXmlParser.getDomElement(result.getResult());

			NodeList nodelist = doc.getElementsByTagName("view");
			NodeList nodelist1 = doc
					.getElementsByTagName("responseViewPriceDetails");

			if (nodelist.getLength() == 0) {
				displayToastMessage("No more product(s) found");
			}

			else {

				pageNumber = pageNumber + 1;

				System.out.println("the page number is-----" + pageNumber);

				Element element1 = (Element) nodelist1.item(0);
				String totalCounts = mXmlParser
						.getValue(element1, "totalCount");

				totalCount = totalCounts;

				for (int i = 0; i < nodelist.getLength(); i++) {
					Element element = (Element) nodelist.item(i);

					String uniqueId = element.getAttribute("id");

					String name = mXmlParser.getValue(element, "Name");
					String category = mXmlParser.getValue(element, "category");
					String mainCategoryStr = mXmlParser.getValue(element,
							"mainCategory");
					String commInfo = mXmlParser.getValue(element,
							"description");
					uniqueIds.add(uniqueId);

					try {
						name = GenericAction.convertHexaToString(name);
						category = GenericAction.convertHexaToString(category);
						commInfo = GenericAction.convertHexaToString(commInfo);
						mainCategoryStr = GenericAction
								.convertHexaToString(mainCategoryStr);
						commodities.add(name);

					} catch (Exception e) {
						// TODO: handle exception
					}
					categories.add(category);

					String date = mXmlParser.getValue(element, "Date");

					if (date.length() > 19) {
						date = date.substring(0, date.length() - 2);

					}

					String price = mXmlParser.getValue(element, "Price");
					// exactPrice=price;
					// QuantityandUnits
					String priceValue = price + "/";
					String QuantityandUnits = mXmlParser.getValue(element,
							"unit");

					String discountInPercentage = mXmlParser.getValue(element,
							"discount");
					String discountPrice = mXmlParser.getValue(element,
							"discountPrice");
					String quantity = mXmlParser.getValue(element, "quantity");
					String minQty = mXmlParser.getValue(element, "minQty");
					QuantityandUnits = minQty + " " + QuantityandUnits;

					String unitTag = mXmlParser.getValue(element, "minUnit");

					String imageURL = mXmlParser.getValue(element,
							"commodityImageUrl");

					String statusPrice = mXmlParser.getValue(element,
							"priceStatus");
					// imageURL.replace("rainbowagri.com",
					// "192.168.1.134:8080");

					RowItem dataItems = new RowItem();
					dataItems.setCommodityName(name);
					dataItems.setMainCategory(mainCategoryStr);
					dataItems.setStatusPrice(statusPrice);
					dataItems.setVarietyName(category);
					dataItems.setDate(date);
					dataItems.setPrice(price);
					dataItems.setUnit(QuantityandUnits);
					dataItems.setId(uniqueId);
					dataItems.setDiscountInPercentage(discountInPercentage);
					dataItems.setDiscountPrice(discountPrice + "/");
					dataItems.setQuantity(quantity);
					dataItems.setMinimumQuantity(minQty);
					dataItems.setUnitTag(unitTag);
					dataItems.setImageURL(imageURL.replace(" ", "%20"));
					dataItems.setCommodityInfo(commInfo);

					commodityList.add(dataItems);
					if (searchText.length() == 0) {
						tempCommodityList.add(dataItems);
					}

					mAdapter.addData(dataItems);

					// catagoryFactory.onSave(category, "" + coordinatorRefId);
				}
				Collections.sort(commodityList, convertStringToDateAndCompare);
				mListView.setAdapter(mAdapter);

				mListView.setSelectionFromTop(listViewPostion, 0);
				GenericAction.loadingMore = false;
				countOfCommodity.setText("" + visibleCount + " Of "
						+ totalCount);

				if (commodityList.size() == 0) {
					noDataLayout.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
				} else {
					noDataLayout.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
				}

				mAdapter.setActivateListner(new NotifyListener() {

					@Override
					public void taskCompleted() {
						// TODO Auto-generated method stub
						TaskToFetchCommodities();
					}

					@Override
					public void DoTaskCompleted(String commodityId,
							String vendorId, String priceStatus) {
						alertDialogWindow(priceStatus, commodityId, vendorId);

					}

					@Override
					public void isSearchActive(String active) {
						// TODO Auto-generated method stub

					}

				});
			}
		} catch (Exception e) {
			displayToastMessage("Couldn't able to fetch data from server.Please try after some time");
		}

		// loadCategory();

	}

	private void loadCategory(String val) {

		HashSet<String> listToSet = new HashSet<String>(categories);
		duplicates = new ArrayList<String>(listToSet);
		duplicates.add(0, defaultTextForCategorySpinner);
		SortFilter = duplicates.toArray(new String[0]);

		spinnerCustomAdapter dataAdapter = new spinnerCustomAdapter(
				CommodityDetailsActivity.this,
				R.layout.shopdetails_spinner_checkitem, SortFilter,
				defaultTextForSpinnerAtDisplay);
		dataAdapter
				.setDropDownViewResource(R.layout.commoditydetails_spinner_checkitem);
		spinnervalue.setAdapter(dataAdapter);
		spinnervalue.setSelection(getIndex(spinnervalue, val));
	}

	/** Webservice task to fetch category from server **/
	private void addCategory() {

		mPreferences = this.getSharedPreferences("RainbowAgriLivePrice",
				MODE_PRIVATE);

		mainCategory = mPreferences.getString("mainCategory", "");

		XMLRequestAndResponseData items = new XMLRequestAndResponseData();
		XMLCreator xmlCreator = new XMLCreator();
		String reqeustXML = xmlCreator.createCategoryXML(VENDOR_ID,
				mainCategory); // no need to consider main category now to fetch
								// all the data
		items.setRequestXMLdata(reqeustXML);
		// items.setUrl(getString(R.string.delete_commodity));
		items.setUrl(getString(R.string.Get_Sub_Categories));
		AsynchronousTaskToFetchData asyTask = new AsynchronousTaskToFetchData(
				this, items);

		asyTask.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {
				parseCategoryData(result);

			}
		});

		asyTask.execute("");
	}

	/** Fetch category for particular shop **/
	private void parseCategoryData(XMLRequestAndResponseData result) {
		try {

			categories.clear();
			duplicates.clear();
			System.out.println("Result is99" + result.getResult());
			XmlParser parser = new XmlParser();
			Document doc = parser.getDomElement(result.getResult());
			NodeList nodelist = doc.getElementsByTagName("commodityList");

			for (int i = 0; i < nodelist.getLength(); i++) {

				Element element = (Element) nodelist.item(i);

				String category = "" + parser.getValue(element, "categoryName");

				try {
					category = GenericAction.convertHexaToString(category);

					System.out.println("the getting categoru listsda =="
							+ category);
					categories.add(category);
				} catch (Exception e) {
					categories.add(category);
				}

			}

			HashSet<String> listToSet = new HashSet<String>(categories);
			duplicates = new ArrayList<String>(listToSet);
			duplicates.add(0, defaultTextForCategorySpinner);
			SortFilter = duplicates.toArray(new String[0]);

			spinnerCustomAdapter dataAdapter = new spinnerCustomAdapter(
					CommodityDetailsActivity.this,
					R.layout.shopdetails_spinner_checkitem, SortFilter,
					defaultTextForSpinnerAtDisplay);
			dataAdapter
					.setDropDownViewResource(R.layout.commoditydetails_spinner_checkitem);
			spinnervalue.setAdapter(dataAdapter);
		} catch (Exception e) {
			displayToastMessage("Could not fetch category .Please try again");
		}

	}

	public void alertDialogWindow(final String pStatus,
			final String commoditIds, final String vendorIds) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);

		if (pStatus.equalsIgnoreCase("Hide")) {
			builder.setTitle("Hide Commodity");
			builder.setMessage("Are you sure you want to hide this commodity to Consumer?");
		} else {
			builder.setTitle("Show Commodity");
			builder.setMessage("Are you sure you want to show this commodity to Consumer?");
		}

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (pStatus.equalsIgnoreCase("Hide")) {

					// listener.DoTaskCompleted(commoditIds,
					// CommodityDetailsActivity.VENDOR_ID, "Hide");
					webserviceTaskToHideAndShowPrice(commoditIds,
							CommodityDetailsActivity.VENDOR_ID, "Hide");

				} else {
					webserviceTaskToHideAndShowPrice(commoditIds,
							CommodityDetailsActivity.VENDOR_ID, "Show");

				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	public boolean onClose() {

		mAdapter.notifyDataSetChanged();

		return false;
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

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

	private void webServiceTaskForDelete(String commodityRefId, String comName,
			String categoryName, final int position) {
		XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();

		XMLCreator xmlCreator = new XMLCreator();

		String comid = commodityRefId;// getArgument.getString("refId");
		String requestArg = xmlCreator.serializeForDeleteCommodity(comid, ""
				+ coordinatorRefId, comName, categoryName);

		createRequest.setRequestXMLdata(requestArg);

		createRequest.setUrl(getString(R.string.delete_commodity));
		String params = "";
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, createRequest);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {

				try {
					// Parse the response XML string to know the status

					XmlParser mXmlParser = new XmlParser();
					Document doc = mXmlParser.getDomElement(result.getResult());
					NodeList nodelist = doc
							.getElementsByTagName("responseDeletePrice");
					Element element = (Element) nodelist.item(0);
					String status = mXmlParser.getValue(element, "status");

					if (status.equalsIgnoreCase("success")) {
						String totalCounts = mXmlParser.getValue(element,
								"totalCount");

						String categoryCount = mXmlParser.getValue(element,
								"categoryCount");
						if (categoryCount.equalsIgnoreCase("0")) {
							addCategory();

						}
						commodityList.clear();
						mAdapter.notifyDataSetChanged();
						totalCount = "0";
						// mAdapter.notifyDataSetChanged();
						// removeOldProduct(GenericAction.commodityId);

						/*
						 * countOfCommodity.setText("" + mAdapter.getCount() +
						 * " Of " + totalCount);
						 */
						displayToastMessage("Commodity detail has been deleted successfully");
						dialog.dismiss();
						// pageNumber=1;
						IntiateTaskWithFreshData();

						// totalCount = totalCounts;
						// mAdapter.removeOldProduct(GenericAction.commodityId);
						// totalCount = "" + commodityList.size();
						// countOfCommodity.setText("" + visibleCount + " Of "
						// + totalCount);
						//

						// mListView.invalidateViews();
						GenericAction.loadingMore = false;

					} else {
						displayToastMessage("Delete failed please try again later.");
						dialog.dismiss();
					}

				} catch (Exception e) {

					displayToastMessage("Delete failed please try again later.");
					// dismiss();
					e.printStackTrace();
				}

			}
		});
		task.execute(params);
	}

	public void displayToastMessage(String msg) {
		Toast toast = Toast.makeText(CommodityDetailsActivity.this, msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * Checks if external storage is available for read and write
	 */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public void createFolderInSDCard() {
		File file0 = new File(Environment.getExternalStorageDirectory()
				+ "/Rainbow");

		File file1 = new File(Environment.getExternalStorageDirectory()
				+ "/Rainbow/LivePrice");

		if (!file0.exists()) {
			if (file0.mkdir()) {

			}
		}

		if (!file1.exists()) {
			if (file1.mkdir()) {

			}
		}

		mSDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Rainbow/LivePrice/";
	}

	public void webserviceTaskToHideAndShowPrice(final String commId,
			String vendorId, final String content) {
		XMLRequestAndResponseData data = new XMLRequestAndResponseData();
		XMLCreator xmlCreator = new XMLCreator();
		String request = xmlCreator.serializePriceTODO(commId, vendorId,
				content);
		data.setRequestXMLdata(request);

		data.setUrl(getString(R.string.Price_Hide));

		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, data);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {

				try {
					XmlParser mXmlParser = new XmlParser();
					Document doc = mXmlParser.getDomElement(result.getResult());
					NodeList nodelist = doc
							.getElementsByTagName("responseHidePrice");

					Element element = (Element) nodelist.item(0);

					String status = mXmlParser.getValue(element, "status");

					if (status.equalsIgnoreCase("success")) {
						if (content.equalsIgnoreCase("Hide")) {
							displayToastMessage("This commodity details has been hidden from consumer");
						} else {
							displayToastMessage("This commodity details will be visible to consumer");
						}
						mAdapter.UpdateHideOrShow(commId, content);
						mAdapter.notifyDataSetChanged();
						// TaskToFetchCommodities();
					} else {

					}

				} catch (Exception e) {
					displayToastMessage("Failed.Please try after some time");
				}

			}
		});
		task.execute("");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public void alertShopActiveOrInactiov(String pStatus, final String vendorIds) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		shopStatusActive = pStatus;

		if (pStatus.equalsIgnoreCase("No")) {
			builder.setTitle("Deactivate");
			builder.setMessage("Are you sure you want to INVISIBLE your shop to Consumer?");
		} else {
			builder.setTitle("Activate");
			builder.setMessage("Are you sure you want to VISIBLE your shop to Consumer?");
		}

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (shopStatusActive.equalsIgnoreCase("No")) {

					WebseriviceTaskForShopActive("No");

				} else {

					WebseriviceTaskForShopActive("Yes");
				}
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// Shop activate or deactivate
	private void WebseriviceTaskForShopActive(final String status) {
		shopStatusActive = status;
		XMLRequestAndResponseData request = new XMLRequestAndResponseData();
		XMLCreator xmlCreator = new XMLCreator();
		request.setRequestXMLdata(xmlCreator.serializeShopActiveOrInactive(
				status, "" + coordinatorRefId));
		request.setUrl(getString(R.string.Shop_Active));
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, request);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {
				// TODO Auto-generated method stub
				parseShopActiveXml(result, shopStatusActive);
			}
		});
		task.execute("");

	}

	private void parseShopActiveXml(XMLRequestAndResponseData data,
			String active) {
		try {
			XmlParser mXmlParser = new XmlParser();
			Document doc = mXmlParser.getDomElement(data.getResult());
			NodeList nodelist = doc.getElementsByTagName("resActive");

			Element element = (Element) nodelist.item(0);

			String status = mXmlParser.getValue(element, "status");

			if (status.equalsIgnoreCase("success")) {

				if (shopStatusActive.equalsIgnoreCase("No")) {
					displayToastMessage("Your shop has been INVISIBLE to user");
				} else {
					displayToastMessage("Your shop has been VISIBLE to user");
				}

				SharedPreferences shopActive = getSharedPreferences(
						CommodityDetailsActivity.SHOP_SHARED_PREF_NAME,
						MODE_PRIVATE);
				SharedPreferences.Editor activeEdit = shopActive.edit();
				activeEdit.putString(CommodityDetailsActivity.STATUS, active);
				activeEdit.putBoolean(CommodityDetailsActivity.SHOP_STATUS,
						false);
				activeEdit.commit();

				invalidateOptionsMenu();

			}

		} catch (Exception e) {
			displayToastMessage("Failed.Please try after some time");
		}

	}

	private void saveDefaultCategory() {

		OrganicGroceries = new String[] { "Millets", "Vegetables", "Fruits",
				"Healthy drinks", "Oil", "Flour","Rice","Spices", "Others"};
		for (int i = 0; i < OrganicGroceries.length; i++) {
			catagoryFactory.onSave(OrganicGroceries[i], "" + coordinatorRefId,
					"Organic Groceries");
		}
		Grocery = new String[] { "Grocery & staples", "Oil", "Drinks", "Egg","Rice","Spices","Dall",
				"Others" };
		for (int i = 0; i < Grocery.length; i++) {
			catagoryFactory
					.onSave(Grocery[i], "" + coordinatorRefId, "Grocery");
		}
		FruitsVegetables = new String[] { "Vegetables", "Fruits", "Others" };
		for (int i = 0; i < FruitsVegetables.length; i++) {
			catagoryFactory.onSave(FruitsVegetables[i], "" + coordinatorRefId,
					"Fruits and Vegetables");
		}
		CattlePoultry = new String[] { "Cow", "Goat", "Country chicken",
				"Broiler chicken", "Egg", "Others" };
		for (int i = 0; i < CattlePoultry.length; i++) {
			catagoryFactory.onSave(CattlePoultry[i], "" + coordinatorRefId,
					"Cattle");
		}
		Seed = new String[] { "Seed", "Vegetables seed", "Fruits seed",
				"Dry grass seed", "Cereals seed", "Flower seed", "Others" };
		for (int i = 0; i < Seed.length; i++) {
			catagoryFactory.onSave(Seed[i], "" + coordinatorRefId, "Seed");
		}
		DairyProducts = new String[] { "Milk", "Butter", "Ghee", "Others" };
		for (int i = 0; i < DairyProducts.length; i++) {
			catagoryFactory.onSave(DairyProducts[i], "" + coordinatorRefId,
					"Dairy Products");
		}
		NurserySaplings = new String[] { "Nursery and Saplings", "Dry nursery",
				"wet nursery", "Transplant nursery", "seedling nursery",
				"Others" };
		for (int i = 0; i < NurserySaplings.length; i++) {
			catagoryFactory.onSave(NurserySaplings[i], "" + coordinatorRefId,
					"Nursery and Saplings");
		}
		
		handicraft = new String[] { "Metal craft", "glay craft",
				" Weaving & Dyeing craft", "Bamboo craft", "Stone craft","carpets","wood craft", 
                "Embroidery craft","Rock craft","Others"};
		for (int i = 0; i < handicraft.length; i++) {
			catagoryFactory.onSave(handicraft[i], "" + coordinatorRefId,
					"Handicraft");
		}
		
		machinary = new String[] { "Tracker", "Cultivator",
				"Planter", "Tanker & Terragator", "Irrigation","Harvester","Others"};
		for (int i = 0; i < machinary.length; i++) {
			catagoryFactory.onSave(machinary[i], "" + coordinatorRefId,
					"Machinery");
		}
		
		others = new String[] {"others"};
		for (int i = 0; i < others.length; i++) {
			catagoryFactory.onSave(others[i], "" + coordinatorRefId,
					"Others");
		}
		
		terraceGardening = new String[] {"terrace gardening kit","others"};
		for (int i = 0; i < terraceGardening.length; i++) {
			catagoryFactory.onSave(terraceGardening[i], "" + coordinatorRefId,
					"Terrace Gardening");
		}
		
	}

	/**
	 * Date wise descending order
	 */
	static final Comparator<RowItem> convertStringToDateAndCompare = new Comparator<RowItem>() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm:ss");

		public int compare(RowItem value1, RowItem value2) {
			Date date1 = null;
			Date date2 = null;
			try {

				date1 = dateFormat.parse(value1.getDate());
				date2 = dateFormat.parse(value2.getDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return (date1.getTime() > date2.getTime() ? -1 : 1); // descending
			// return (date1.getTime() > date2.getTime() ? 1 : -1); //ascending
		}
	};

	/**
	 * Convert string to date
	 */
	public static String getFormattedDateFromTimestamp(
			long timestampInMilliSeconds) {
		Date date = new Date();
		date.setTime(timestampInMilliSeconds);
		String formattedDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm")
				.format(date);
		return formattedDate;

	}

	public void updateNewlyAddedCommodityDetails(RowItem items) {

		categories.add(items.getVarietyName());
		loadCategory("");

		IntiateTaskWithFreshData();

	}

	private void performSearch() {
		mSearchView.clearFocus();
		InputMethodManager in = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

	}

	// ** Hides the soft keyboard

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}

	}

	public String dateFormat(String date) throws ParseException {
		String strCurrentDate = date;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date newDate = format.parse(strCurrentDate);

		format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String datesFormats = format.format(newDate);
		return datesFormats;
	}

	private void IntiateTaskWithFreshData() {

		hideSoftKeyboard();
		// spinnervalue.setSelection(0);
		commodityList.clear();
		tempCommodityList.clear();
		mAdapter = new CustomListViewAdapter(CommodityDetailsActivity.this,
				R.layout.commodity_details_row, commodityList);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		pageNumber = 1;
		TaskToFetchCommodities();
	}

	private void IfSearhIsCleared() {
		commodityList.clear();
		commodityList.addAll(tempCommodityList);
		totalCount = "" + commodityList.size();
		countOfCommodity.setText("" + visibleCount + " Of "
				+ commodityList.size());
		mAdapter.notifyDataSetChanged();
	}

	private void onQueryTextSubmit() {
		pageNumber = 1;
		commodityList.clear();
		mAdapter.notifyDataSetChanged();
		countOfCommodity.setText("" + visibleCount + " Of " + totalCount);
		TaskToFetchCommodities();
	}

	private void UpdateDataForEditCommodityPrice() {
		hideSoftKeyboard();
		GenericAction.loadingMore = false;

		mAdapter.removeOldProduct(GenericAction.commodityId);
		removeOldProduct(GenericAction.commodityId);
		// spinnervalue.setSelection(getIndex(spinnervalue,
		// maincategoryString));
		mListView.invalidateViews();
		mAdapter.notifyDataSetChanged();
		updateEditedCommodityDetails(GenericAction.editData);
	}

	// Remove product from cart
	public void removeOldProduct(String commoditId) {

		try {
			System.out.println("Remove product b size "
					+ tempCommodityList.size());
			System.out.println("Remove product " + commoditId);
			for (int i = 0; i < tempCommodityList.size(); i++) {
				if (tempCommodityList.get(i).getId()
						.equalsIgnoreCase(commoditId)) {
					System.out.println("Remove product id fil" + commoditId);
					tempCommodityList.remove(i);

				}

			}

		} catch (Exception e) {
			System.out.println("Remove product" + e.getMessage());
		}
	}

	public void updateEditedCommodityDetails(RowItem items) {

		categories.add(items.getVarietyName()); // removed by saravanan as we
												// have default predefined
												// categories now
		loadCategory(varietyName); // removed by saravanan as we have default
									// predefined categories now

		// categories.add(items.getVarietyName()); // removed by saravanan as we
		// have default predefined categories now

		// loadCategory(); // removed by saravanan as we have default predefined
		// categories now
		// catagoryFactory.onSave(items.getVarietyName(), "" +
		// coordinatorRefId);
		IntiateTaskWithFreshData();

	}

	class CheckVersion extends AsyncTask<String, Void, String> {
		String reqParams;

		public CheckVersion(Context applicationContext) {
		}
		

		@Override
		protected void onPreExecute() {
			progressBar.setMessage("please wait checking..");
			progressBar.setIndeterminate(false);
			progressBar.setCancelable(false);
			progressBar.show();
			
			
			MyApplications.tracker().setScreenName("Commodity screen");
			long time1 = System.currentTimeMillis();
			long time2 = System.currentTimeMillis();
			long timingValue = time2 - time1;
			         // Build and send timing.
		MyApplications.tracker().send(new HitBuilders.TimingBuilder()
			            .setCategory("Load")
			            .setValue(timingValue)
			            .setVariable("variibleName")
			            .setLabel("setLAbel")
			            .build());
			
			// System.out.println("getting herere");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String response = null;

			try {

				ServerCommunicator serverCommunicator = new ServerCommunicator();

				//URL requestURL = new URL(getString(R.string.check_version));

				 String requestURL = getString(R.string.check_version);
				PackageInfo pInfo = null;
				try {
					pInfo = getApplicationContext()
							.getPackageManager()
							.getPackageInfo(
									getApplicationContext().getPackageName(), 0);
				} catch (NameNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String intVersion = pInfo.versionName;
				int intVersionCode = pInfo.versionCode;
				;
				String versioncode = "" + intVersion;

				try {

					JSONObject jsonObject = new JSONObject();
					jsonObject.accumulate("appName", "Sell");
					jsonObject.accumulate("currentVersion", versioncode);

					reqParams = jsonObject.toString();
					Log.d("Login request params:", reqParams);

				} catch (JSONException e) {
					e.getLocalizedMessage();
				}
				response = serverCommunicator.postJSONData(reqParams,
						requestURL);
				System.out.println("th res;response---"+response);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Network Error..",
						Toast.LENGTH_SHORT).show();
			}

			return response;
		}

		@Override
		protected void onPostExecute(String response) {

			System.out.println("th res;ons---"+response);
			super.onPostExecute(response);
			progressBar.dismiss();
			if (response != null) {
				try {

					GoogleAnalytics.getInstance(CommodityDetailsActivity.this.getBaseContext()).dispatchLocalHits();
					
					JSONObject jObject = new JSONObject(response);
					String newVersion = jObject.getString("status");
					if (newVersion.equalsIgnoreCase("success")) {
						addCategory();
						TaskToFetchCommodities();
					} else if (newVersion.equalsIgnoreCase("failure")) {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								CommodityDetailsActivity.this);
						alertDialog.setCancelable(false);
						alertDialog.setTitle("Update Require");
						alertDialog
								.setMessage("Please update latest version Sell App.");
						// alertDialog.setIcon(R.drawable.delete);
						alertDialog.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										 logout();
										// Toast.makeText(getApplicationContext(),
										// "You clicked on YES",
										// Toast.LENGTH_SHORT).show();
										final String appPackageName = getPackageName();
										try {
											startActivity(new Intent(
													Intent.ACTION_VIEW,
													Uri.parse("market://details?id="
															+ appPackageName)));
										} catch (android.content.ActivityNotFoundException anfe) {
											startActivity(new Intent(
													Intent.ACTION_VIEW,
													Uri.parse("http://play.google.com/store/apps/details?id="
															+ appPackageName)));
											
											
										}
									}
								});
						alertDialog.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Toast.makeText(getApplicationContext(),
										// "You clicked on NO",
										// Toast.LENGTH_SHORT).show();
										dialog.cancel();

										finish();
									}
								});
						alertDialog.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					finish();
				}
			}
		}
	}
  public void logout()
  {
	  SharedPreferences settings = this.getSharedPreferences(
				"RainbowAgriLivePrice", Context.MODE_PRIVATE);
		settings.edit().clear().commit();
		Intent intent = new Intent(getApplicationContext(),
				UserAuthMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		CommodityDetailsActivity.this.startActivity(intent);
		CommodityDetailsActivity.this.finish();
		catagoryFactory.deleteTable();
      finish();  
  }
	private int getIndex(Spinner spinner, String myString) {
		int index = 0;

		for (int i = 0; i < spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).toString()
					.equalsIgnoreCase(myString)) {
				index = i;
				break;
			}
		}
		return index;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}


}
