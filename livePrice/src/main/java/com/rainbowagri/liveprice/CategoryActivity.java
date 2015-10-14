/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.data.GenericAction;
import com.rainbowagri.model.CatagoryFactory;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;


public class CategoryActivity extends Activity {

	ListView categoryListView;
	EditText searchCategoryTextId;
	Button addButton;
	TextView categoryListHeadText;
	RelativeLayout searchRelativeLayout;
	String searchCategoryText = "",mainCategory;
	SpecialBlueAdapter specialBlueAdapter;
	CatagoryFactory catagoryFactory;
	ArrayList<HashMap<String, Object>> categoryList;
	long coordinatorRefId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_name_list);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.category_name_list);
		UploadCrashReport.startService(this);*/
		GenericAction.trackerSetup("category list", "seller");
	 
		categoryListView = (ListView) findViewById(R.id.categoryListView);
		 
		categoryListHeadText = (TextView) findViewById(R.id.categoryListHeadText);
 
		catagoryFactory = new CatagoryFactory(this);
		categoryList = new ArrayList<HashMap<String, Object>>();

		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/Sanchez-Regular_0.ttf");
	 
		categoryListHeadText.setTypeface(face);

		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		categoryListHeadText.setWidth(screenWidth - 110);
		SharedPreferences mPreferences = this.getSharedPreferences(
				getString(R.string.shared_references_name), MODE_PRIVATE);
		coordinatorRefId = mPreferences.getInt(
				getString(R.string.coordinator_ref_id), 0);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mainCategory = bundle.getString("mainCategory");
			System.out.println("the received category is===="+mainCategory);
	 
		}
		 ArrayList<HashMap<String, Object>> categoryNames = catagoryFactory
				.getCategoryName(mainCategory.trim(),"" + coordinatorRefId);
		 
		for (int i = 0; i < categoryNames.size(); i++) {
			Object category = categoryNames.get(i).get("CATEGORY_NAME");

			try {

				category = GenericAction.convertHexaToString(category
						.toString());

			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("CATEGORY_NAME", category);
			categoryList.add(map);

		}
		 
		reloadListView(categoryList);
 
		categoryListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LinearLayout rowLinearLayout = (LinearLayout) parent
						.getChildAt(
								position
										- categoryListView
												.getFirstVisiblePosition())
						.findViewById(R.id.rowLinearLayout);
				TextView searchCategory = (TextView) parent.getChildAt(
						position - categoryListView.getFirstVisiblePosition())
						.findViewById(R.id.categoryNameText);
				searchCategoryText = searchCategory.getText().toString();
				rowLinearLayout
						.setBackgroundColor(R.drawable.rounded_edittextonpress);
				returnCategory();
			}
		});

	 
	}
 
	/**
	 * Check the Internet connection
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
	 * Common method for validation process
	 */
	public void displayToastMessage(String msg) {
		Toast toast = Toast.makeText(CategoryActivity.this, msg,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * Updating the list view
	 */
	 public void reloadListView(
			ArrayList<HashMap<String, Object>> categoryListTemp) {
		String from[] = { CatagoryFactory.COL_CATEGORY_NAME };
		int to[] = { R.id.categoryNameText };
		specialBlueAdapter = new SpecialBlueAdapter(this, categoryListTemp,
				R.layout.row_category, from, to);
		categoryListView.setAdapter(specialBlueAdapter);
	}
 
	/**
	 * Return the category after selecting one to commodity page
	 */
	public void returnCategory() {
		Bundle b = new Bundle();
		b.putString("categoryName", searchCategoryText);
		Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
 
}
