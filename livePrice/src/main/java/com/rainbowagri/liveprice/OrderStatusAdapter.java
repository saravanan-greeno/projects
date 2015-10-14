/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */

package com.rainbowagri.liveprice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rainbowagri.data.ProductItems;


public class OrderStatusAdapter extends ArrayAdapter<ProductItems> {

	Context context;
	int resourceId;
	List productsItems = new ArrayList<ProductItems>();

	/**
	 * constructor
	 */
	public OrderStatusAdapter(Context context, int resourceId,
			List<ProductItems> items) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
		productsItems = items;
	}

	/** private view holder class */
	private class ViewHolder {

		TextView commodityName;
		TextView price;

	}

	/**
	 * private view holder class view for showing the news in list view
	 * */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ProductItems productItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(resourceId, null);
			holder = new ViewHolder();
			holder.commodityName = (TextView) convertView
					.findViewById(R.id.commodityname);
			holder.price = (TextView) convertView.findViewById(R.id.price1);
			Typeface font = Typeface.createFromAsset(context.getAssets(),
					"fonts/Sanchez-Regular_0.ttf");
			holder.commodityName.setTypeface(font);
			holder.price.setTypeface(font);
			convertView.setTag(holder);

		} else
			holder = (ViewHolder) convertView.getTag();

		holder.commodityName.setText(productItem.getCommodityName());

		String price = productItem.getCommodityPrice();
		String quantity = productItem.getQuantity();
		String unit = productItem.getUnit();
		holder.price.setText(price.toString().trim() + "/" + quantity
				+ unit.toString().trim());

		return convertView;
	}

}
