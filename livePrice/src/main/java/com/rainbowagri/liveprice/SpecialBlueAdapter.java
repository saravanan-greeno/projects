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

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class SpecialBlueAdapter extends SimpleAdapter {
	private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

	public SpecialBlueAdapter(Context context,
			ArrayList<HashMap<String, Object>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		TextView textView = (TextView) view.findViewById(R.id.categoryNameText);
		if (textView != null)
			;
		else
			textView = (TextView) view.findViewById(R.id.simpleText);
		Typeface face = Typeface.createFromAsset(parent.getContext()
				.getAssets(), "fonts/Sanchez-Regular_0.ttf");
		textView.setTypeface(face);
		int colorPos = position % colors.length;
		// view.setBackgroundResource(background_image[colorPos]);
		return view;
	}
}
