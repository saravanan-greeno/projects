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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.rainbowagri.data.RowItem;


public class CommodityPriceDetailsAdapter extends BaseAdapter implements
		Filterable {

	Context context;
	ArrayList<String> mListItem;
	RowItem rowTem;
	String rupeeSymbol = "";
	Boolean update = false;
	private NotifyListener listener;
	String strValue;

	private filterCommodities commodityFiler;
	private ArrayList<RowItem> originalList;
	private ArrayList<RowItem> filteredList;
	private LayoutInflater inflater;
	ArrayList<String> tempPriceArray = new ArrayList<String>();
	int k = 0;

	/**
	 * Constructor
	 */
	public CommodityPriceDetailsAdapter(Context context, int resourceId,
			ArrayList<RowItem> commodityList) {
		super();

		this.context = context;
		this.originalList = commodityList;
		this.filteredList = commodityList;
		for (int i = 0; i < originalList.size(); i++) {
			tempPriceArray.add(originalList.get(i).getPrice());
		}

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getFilter();
		rupeeSymbol = context.getString(R.string.rupee_symbol);
		notifyDataSetChanged();

	}

	public void setActivateListner(NotifyListener listener) {
		this.listener = listener;
	}

	private class ViewHolder {

		TextView commodityName;
		EditText price;
		TextView id;
		TextView unitValue;
		public TextWatcher textWatcher;
		int ref;

	}

	@Override
	public int getCount() {
		return filteredList.size();
	}

	public List<RowItem> getRowItems() {
		return this.filteredList;
	}

	@Override
	public int getViewTypeCount() {
		return getCount();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return filteredList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int i = position;
		final ViewHolder holder;

		final RowItem rowItem = (RowItem) getItem(position);
		if (convertView == null) {

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.category_list_edit,
					parent, false);
			holder = new ViewHolder();
			holder.commodityName = (TextView) convertView
					.findViewById(R.id.commodityName);
			holder.id = (TextView) convertView.findViewById(R.id.vendorId);
			holder.unitValue = (TextView) convertView
					.findViewById(R.id.unitValue);
			holder.price = (EditText) convertView
					.findViewById(R.id.priceValue1);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		holder.commodityName.setText(rowItem.getCommodityName());
		holder.price.setText(rowItem.getPrice());
		holder.unitValue.setText(rowItem.getQuantity());
		holder.id.setText(rowItem.getId());
		holder.ref = position;

		holder.price.setId(position);

		holder.price.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {

					final int position = view.getId();
					final EditText Caption = (EditText) view;

					if (Caption.getText().toString().isEmpty()) {
						Caption.setText(rowItem.getPrice());
						filteredList.get(position).setPrice(strValue);
						notifyDataSetChanged();
					} else {
						filteredList.get(position).setPrice(
								Caption.getText().toString());
					}
				} else {

					final EditText Caption = (EditText) view;
					strValue = Caption.getText().toString();
				}

			}
		});

		holder.price.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.length() > 0 && str.startsWith("0")) {

					holder.price.setText("");
				} else if (str.length() > 0 && str.startsWith(".")) {

					holder.price.setText("");
				} else if (str.length() == 0) {

				} else {
					holder.price.setSelection(s.toString().length());
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		return convertView;
	}

	@Override
	public Filter getFilter() {
		if (commodityFiler == null) {
			commodityFiler = new filterCommodities();
		}

		return commodityFiler;
	}

	/**
	 * Class for search content in list view using search manager
	 */

	private class filterCommodities extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<RowItem> tempList = new ArrayList<RowItem>();

				for (RowItem user : originalList) {
					if (user.getCommodityName().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getPrice().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					}

					else if (user.getQuantity().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					}

					else {

					}
				}

				filterResults.count = tempList.size();
				filterResults.values = tempList;
			} else {
				filterResults.count = originalList.size();
				filterResults.values = originalList;
			}

			return filterResults;
		}

		/**
		 * Notify about filtered list to ui
		 * 
		 * @param constraint
		 *            text
		 * @param results
		 *            filtered result
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredList = (ArrayList<RowItem>) results.values;
			if (filteredList.size() == 0) {
				listener.isSearchActive("Yes");

			} else if (filteredList.size() > 0) {
				listener.isSearchActive("No");

			}
			notifyDataSetChanged();
		}
	}

	/**
	 * Get the complete list
	 */
	public ArrayList<RowItem> getValueList() {
		return filteredList;
	}

}