/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowagri.data.FeedbackData;
import com.rainbowagri.data.OrderHistoryDetails;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.liveprice.OrderHistory.loadOrderDetails;
import com.rainbowagri.servercommunicator.XMLCreator;

public class FeedbackDetailsAdapter extends BaseAdapter implements Filterable {

	Context context;
	 
	ArrayList<String> mListItem;
	 
	 
	XMLCreator xmlCreator;
	private Typeface typeface;
	private NotifyListener listener;
	private FilterClass filterclass;

	private ArrayList<FeedbackData> originalList;
	private ArrayList<FeedbackData> filteredList;
	private LayoutInflater inflater;
	 

	public FeedbackDetailsAdapter(Context context, int resourceId,
			ArrayList<FeedbackData> feedbackList) {
		super();
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.originalList = feedbackList;
		this.filteredList = feedbackList;

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getFilter();
		 notifyDataSetChanged();

	}
	public void setActivateListner(NotifyListener listener) {
		this.listener = listener;
	}
	/**
	 * private view holder class
	 */

	private class ViewHolder {

		TextView feedbackId;
		TextView feedback;
		//TextView mobileNumber;
		//TextView name;
		Button view;
		TextView type;
		//public int position;
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
	public int getCount() {
		return filteredList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * view for all the fragments list view
	 */

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
	 

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.feedback_row_view, parent,
					false);

			holder = new ViewHolder();
		//	holder.position = position;
		
			holder.feedback = (TextView) convertView
					.findViewById(R.id.fedbackdetails);
			holder.feedbackId = (TextView) convertView
					.findViewById(R.id.feedbackId);
			holder.type = (TextView) convertView
					.findViewById(R.id.feedbackType);
			
			holder.view = (Button) convertView
					.findViewById(R.id.viewer);
			
			holder.view.setFocusable(false);
			//holder.mobileNumber = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		typeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/Sanchez-Regular_0.ttf");

		 final FeedbackData rowItem = (FeedbackData) getItem(position);

		holder.feedback.setTypeface(typeface);
		holder.feedbackId.setTypeface(typeface);
		holder.type.setTypeface(typeface);
		
//System.out.println("the setting commet is===="+rowItem.getComment());

		holder.feedback.setText(rowItem.getComment());
		holder.feedbackId.setText(rowItem.getFeedbackId());
		holder.type.setText("Type: "+rowItem.getFeedbackType()); 
	 
		 holder.view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				//final int position = view.getId();
				String ViewComment=holder.feedback.getText().toString();
				System.out.println("the passign value"+ViewComment);
				listener.isSearchActive(ViewComment);
				
			}
		}); 
		 
		return convertView;
	}
 

	 
	@Override
	public Filter getFilter() {
		if (filterclass == null) {
			filterclass = new FilterClass();
		}

		return filterclass;
	}

	/**
	 * Keep reference to children view to avoid unnecessary calls
	 */

	/**
	 * Custom filter for friend list Filter content in friend list according to
	 * the search text
	 */
	private class FilterClass extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<FeedbackData> tempList = new ArrayList<FeedbackData>();

				// search content in friend list
				for (FeedbackData user : originalList) {
					if (user.getComment().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getCommenterName().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getMobileNumber().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getFeedbackType().toLowerCase()
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
			filteredList = (ArrayList<FeedbackData>) results.values;
			notifyDataSetChanged();
		}
	}

	 
}
