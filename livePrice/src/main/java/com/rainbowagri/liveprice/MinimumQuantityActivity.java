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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;


public class MinimumQuantityActivity extends Activity {

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
		ArrayList stringArrayList = new ArrayList<String>();
		String[] values = null;

		if (bundle != null) {
			unitType = bundle.getString("unit");

			if (unitType.equals("Grams")) {
				values = new String[] { "50 Grams", "100 Grams", "150 Grams",
						"250 Grams", "500 Grams" };
			} else if (unitType.equals("Kg")) {
				values = new String[] { "250 Grams", "500 Grams", "1 Kg",
						"2 Kgs" };
			} else if (unitType.equals("Piece")) {
				values = new String[] { "1 Piece", "2 Pieces", "3 Pieces",
						"4 Pieces" };
			} else if (unitType.equals("Litre")) {
				values = new String[] { "250 ML", "500 ML", "1 Litre" };
			} else if (unitType.equals("Packet")) {
				values = new String[] { "1 Packet", "2 Packets" };
			} else if (unitType.equals("Quintal")) {
				values = new String[] { "1 Quintal", "2 Quintals" };
			} else if (unitType.equals("Ton")) {
				values = new String[] { "1 Ton", "2 Tons" };
			} else if (unitType.equals("Millilitres")) {
				values = new String[] { "50 ML", "100 ML", "200 ML", "300 ML",
						"400 ML", "500 ML" };
			} else if (unitType.equals("Bundle")) {
				values = new String[] { "1 Bundle", "2 Bundles", "3 Bundles",
						"4 Bundles", "5 Bundles" };
			} else if (unitType.equals("Dozen")) {
				values = new String[] { "1 Dozen", "2 Dozens" };
			} else if (unitType.equals("Item")) {
				values = new String[] { "1 Item", "2 Items", "3 Items",
						"4 Items", "5 Items" };
			}

		}

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
