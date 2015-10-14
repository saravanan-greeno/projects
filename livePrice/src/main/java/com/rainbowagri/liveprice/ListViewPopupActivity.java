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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;


public class ListViewPopupActivity extends Activity {

	SpecialBlueAdapter specialBlueAdapter;
	ListView listview;
	String unitType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listview_popup);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.listview_popup);
		UploadCrashReport.startService(this);*/
		listview = (ListView) findViewById(R.id.listview);

		Bundle bundle = getIntent().getExtras();

		String[] values = null;

		if (bundle != null) {
			unitType = bundle.getString("unit");

			if (unitType.equals("Fruits") || unitType.equals("Vegetables")
					|| unitType.equals("Grocery and Staples")
					|| unitType.equals("Others")) {
				values = new String[] { "Kg", "0.5 Kg", "0.25 Kg", "100 Grams",
						"Packet", "Quintal", "Ton", "Bottle", "Litre", "Piece",
						"Box", "Bag", "Bundle", "Dozen" };
			} else if (unitType.equals("Dry fruits/Nuts")) {
				values = new String[] { "Kg", "100 Grams", "200 Grams",
						"0.5 Kg", "Packet", "Piece", "Box" };
			} else if (unitType.equals("Beverages/Drinks")) {
				values = new String[] { "Litre", "Bottle", "Packet", "Piece",
						"Box" };
			} else if (unitType.equals("Oil/Ghee")) {
				values = new String[] { "Litre", "100 ml", "200 ml", "500 ml",
						"Packet", "Piece" };
			} else {
				values = new String[] { "Kg", "0.5 Kg", "0.25 Kg", "100 Grams",
						"Packet", "Quintal", "Ton", "Bottle", "Litre", "Piece",
						"Box", "Bag", "Bundle", "Dozen", "Roll" };
			}

		}

		ArrayList stringArrayList = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("simpleText", values[i]);
			stringArrayList.add(hashMap);
		}
		reloadListView(stringArrayList);

		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView simpleText = (TextView) parent.getChildAt(
						position - listview.getFirstVisiblePosition())
						.findViewById(R.id.simpleText);
				String simpleTextString = simpleText.getText().toString();
				returnText(simpleTextString);
			}
		});

	}

	/**
	 * Reload the list view
	 */

	public void reloadListView(
			ArrayList<HashMap<String, Object>> categoryListTemp) {
		String from[] = { "simpleText" };
		int to[] = { R.id.simpleText };
		specialBlueAdapter = new SpecialBlueAdapter(this, categoryListTemp,
				R.layout.row_listview_popup, from, to);
		listview.setAdapter(specialBlueAdapter);
	}

	/**
	 * Return the selected text to add commodity page
	 */
	public void returnText(String simpleTextString) {
		Bundle b = new Bundle();
		b.putString("simpleText", simpleTextString);
		Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
}
