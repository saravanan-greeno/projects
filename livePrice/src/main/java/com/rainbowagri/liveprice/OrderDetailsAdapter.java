/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Saravanan <saravanan.s@greeno.in>,  
 */
package com.rainbowagri.liveprice;


import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbowagri.data.OrderHistoryDetails;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.liveprice.OrderHistory.loadOrderDetails;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;

public class OrderDetailsAdapter extends BaseAdapter implements Filterable {

	Context context;
	OrderHistory od;
	ArrayList<String> mListItem;
	RowItem rowTem;
	private NotifyListener listener;
	XMLCreator xmlCreator;
	private Typeface typeface;
	String rupeeSymbol = "", orderIdValue, statusValue, result, requestArg;
	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;
	ProgressDialog progressBar;
	private OrderHistory activity;
	private FriendFilter friendFilter;

	private ArrayList<OrderHistoryDetails> friendList;
	private ArrayList<OrderHistoryDetails> filteredList;
	private LayoutInflater inflater;
	loadOrderDetails ld;

	public OrderDetailsAdapter(Context context, int resourceId,
			ArrayList<OrderHistoryDetails> friendList) {
		super();

		this.context = context;
		this.friendList = friendList;
		this.filteredList = friendList;

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getFilter();
		rupeeSymbol = context.getString(R.string.rupee_symbol);

	}

	public void callWebService(NotifyListener listener) {
		this.listener = listener;
	}

	/**
	 * private view holder class
	 */

	private class ViewHolder {

		TextView orderId;
		TextView orderDate;
		TextView mobileNumber;
		TextView customerName;
		ImageView status;
		ImageView cancel_status;
		ImageView delivered_status;
		TextView statusValue;
		public int position;
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
		activity = new OrderHistory();

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.row_order_details, parent,
					false);

			holder = new ViewHolder();
			holder.position = position;
			holder.orderId = (TextView) convertView.findViewById(R.id.orderId);
			holder.customerName = (TextView) convertView
					.findViewById(R.id.name);
			holder.orderDate = (TextView) convertView.findViewById(R.id.date);
			holder.mobileNumber = (TextView) convertView
					.findViewById(R.id.mobilenumber);
			holder.status = (ImageView) convertView.findViewById(R.id.status);
			holder.delivered_status = (ImageView) convertView
					.findViewById(R.id.delivered_status1);
			holder.cancel_status = (ImageView) convertView
					.findViewById(R.id.cancel_order1);
			holder.statusValue = (TextView) convertView
					.findViewById(R.id.StatusVAlue);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		typeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/Sanchez-Regular_0.ttf");

		final OrderHistoryDetails rowItem = (OrderHistoryDetails) getItem(position);

		holder.orderId.setTypeface(typeface);
		holder.customerName.setTypeface(typeface);
		holder.orderDate.setTypeface(typeface);
		holder.mobileNumber.setTypeface(typeface);

		holder.orderId.setText(rowItem.getOrderId());
		holder.customerName.setText(rowItem.getName());
		holder.orderDate.setText(rowItem.getDate());
		holder.mobileNumber.setText(rowItem.getMobileNUmber());
		holder.statusValue.setText(rowItem.getStatus());
		if (rowItem.getStatus().equalsIgnoreCase("Delivered")) {
			holder.status.setVisibility(View.VISIBLE);
			holder.status.setImageResource(R.drawable.delivered_png);
			holder.delivered_status.setVisibility(View.GONE);
			holder.cancel_status.setVisibility(View.GONE);
			holder.status.setTag("Delivered");
		} else if (rowItem.getStatus().equalsIgnoreCase("Pending")) {
			holder.delivered_status.setVisibility(View.VISIBLE);
			holder.cancel_status.setVisibility(View.VISIBLE);
			holder.cancel_status.setImageResource(R.drawable.cancel);
			holder.delivered_status.setImageResource(R.drawable.accept);
			holder.status.setTag("Pending");
			holder.status.setVisibility(View.GONE);

		} else if (rowItem.getStatus().equalsIgnoreCase("Cancel")) {
			holder.status.setVisibility(View.VISIBLE);
			holder.status.setImageResource(R.drawable.cancelled_png);
			holder.delivered_status.setVisibility(View.GONE);
			holder.cancel_status.setVisibility(View.GONE);
			holder.status.setTag("Cancel");
		}

		holder.cancel_status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				orderIdValue = filteredList.get(position).getOrderId();
				if (rowItem.getStatus().equalsIgnoreCase("Pending")) {
					statusValue = "Cancel";
					alertForOrderStatus();
				}
			}
		});
		holder.delivered_status.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				orderIdValue = filteredList.get(position).getOrderId();
				if (rowItem.getStatus().equalsIgnoreCase("Pending")) {
					statusValue = "Delivered";
					alertForOrderStatus();
				}
			}
		});
		return convertView;
	}

	private void Alert() {
		listener.taskCompleted();

	}

	/**
	 * Alert for showing cancel order or delivered order
	 */
	public void alertForOrderStatus() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);

		if (statusValue.equalsIgnoreCase("Cancel")) {
			builder.setTitle("Cancel");

		} else {
			builder.setTitle("Delivered");

		}
		builder.setMessage("Are you sure you want to Proceed?");
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doWebServiceTaskToChangeDeliverStatus();
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

	@Override
	public Filter getFilter() {
		if (friendFilter == null) {
			friendFilter = new FriendFilter();
		}

		return friendFilter;
	}

	/**
	 * Keep reference to children view to avoid unnecessary calls
	 */

	/**
	 * Custom filter for friend list Filter content in friend list according to
	 * the search text
	 */
	private class FriendFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<OrderHistoryDetails> tempList = new ArrayList<OrderHistoryDetails>();

				// search content in friend list
				for (OrderHistoryDetails user : friendList) {
					if (user.getName().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getDate().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getMobileNUmber().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getOrderId().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getStatus().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else {
					}
				}

				filterResults.count = tempList.size();
				filterResults.values = tempList;
			} else {
				filterResults.count = friendList.size();
				filterResults.values = friendList;
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
			filteredList = (ArrayList<OrderHistoryDetails>) results.values;
			notifyDataSetChanged();
		}
	}

	// To call web-service task to update order status
	private void doWebServiceTaskToChangeDeliverStatus() {
		XMLCreator creator = new XMLCreator();
		XMLRequestAndResponseData request = new XMLRequestAndResponseData();
		System.out.println("yes status " + statusValue);
		String reqXml = creator
				.serializeOrderStatuss(orderIdValue, statusValue);
		System.out.println("yes reqXml status " + reqXml);
		request.setRequestXMLdata(reqXml);
		request.setUrl(context.getResources().getString(R.string.Order_Status));
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				context, request);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {
				parseResponseXml(result);

			}
		});
		task.execute("");

	}

	// Parse XML response data
	private void parseResponseXml(XMLRequestAndResponseData result) {
		try {
			XmlParser mXmlParser = new XmlParser();
			Document doc = mXmlParser.getDomElement(result.getResult());
			NodeList nodelist = doc.getElementsByTagName("responseOrderStatus");
			Element element = (Element) nodelist.item(0);
			String status = mXmlParser.getValue(element, "status");
			if (status.equalsIgnoreCase("Success")) {
				listener.taskCompleted();
			} else if (status.equalsIgnoreCase("Cancel")) {
				listener.taskCompleted();
			} else if (status.equalsIgnoreCase("Delivered")) {
				listener.taskCompleted();
			} else if (status.equalsIgnoreCase("failure")) {
				Toast.makeText(context,
						"Failed to submit.Please try after some time",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Toast.makeText(context, "Failed.Please try after some time",
					Toast.LENGTH_SHORT).show();
		}

	}
}
