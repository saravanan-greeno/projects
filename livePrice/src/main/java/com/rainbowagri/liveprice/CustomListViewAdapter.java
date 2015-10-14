/**
 * Rainbow LivePrice Copyright (C) 2014,
 *  Greeno Tech Solutions Pvt. Ltd.
 * 
 * @author G1019 SARAVANAN
 * @version 1.0, Rel 1, 23 sep 2014
 */
package com.rainbowagri.liveprice;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbowagri.data.BitmapManager;
import com.rainbowagri.data.Connectivity;
import com.rainbowagri.data.RowItem;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class CustomListViewAdapter extends BaseAdapter implements Filterable {

	Context context;
	ArrayList<String> mListItem;
	RowItem rowTem;

	private Typeface typeface;
	String rupeeSymbol = "";
	private NotifyListener listener;
	 
	private FriendFilter friendFilter;

	ArrayList<RowItem> commodityDetails = new ArrayList<RowItem>();
	private ArrayList<RowItem> filteredList = new ArrayList<RowItem>();
	private LayoutInflater inflater;

	/**
	 * Constructor
	 */
	public CustomListViewAdapter(Context context, int resourceId,
			ArrayList<RowItem> dataItems) {
		super();

		this.context = context;
		this.commodityDetails.addAll(dataItems);
		this.filteredList = dataItems;

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getFilter();
		rupeeSymbol = context.getString(R.string.rupee_symbol);
		callAsynchronousTask();

		BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.sellloader));
	}

	public void setActivateListner(NotifyListener listener) {
		this.listener = listener;
	}

	/**
	 * private view holder class
	 */

	private class ViewHolder {

		TextView commodityName;
		TextView varietyName;
		TextView date;
		TextView price;
		TextView id;
		TextView unit;
        TextView mainCategory;
		TextView unitTagVALue;
		ImageView commodityImage;
		TextView discountPrice;
		TextView quantity;
		TextView discountInPercentage;
		ImageView offer;
		ImageView priceHideOrShow;

	}

	@Override
	public int getCount() {
		return filteredList.size();
	}

	@Override
	public RowItem getItem(int position) {
		return filteredList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addData(RowItem data) {
		this.commodityDetails.add(data);
	}

	/**
	 * view for all the fragments list view
	 */

	public ArrayList<RowItem> getProductItems() {
		return this.filteredList;
	}

	/**
	 * view for all the fragments list view
	 */
	public void removeProduct(int position) {

		filteredList.remove(position);
		commodityDetails.remove(position);
		Collections.sort(filteredList, convertStringToDateAndCompare);
		notifyDataSetChanged();
	}

	/**
	 * remove old product from list
	 */
	public void removeOldProduct(String commoditId) {

		try {
			for (int i = 0; i < filteredList.size(); i++) {
				if (filteredList.get(i).getId().equalsIgnoreCase(commoditId)) {
					filteredList.remove(i);
					commodityDetails.remove(i);
				}
			}
			Collections.sort(filteredList, convertStringToDateAndCompare);
			notifyDataSetChanged();

		} catch (Exception e) {
		}
	}

	/**
	 * List view preparation
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		final RowItem rowItem = (RowItem) getItem(position);

		convertView = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.commodity_details_row,
					parent, false);

			holder = new ViewHolder();
			holder.commodityName = (TextView) convertView
					.findViewById(R.id.commodityname);
			holder.varietyName = (TextView) convertView
					.findViewById(R.id.varietyname);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.unit = (TextView) convertView.findViewById(R.id.unit);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.mainCategory = (TextView) convertView.findViewById(R.id.mainCategory);
			holder.unitTagVALue = (TextView) convertView
					.findViewById(R.id.unitTypeTagValue);
			holder.offer = (ImageView) convertView
					.findViewById(R.id.imageoffer);
			holder.priceHideOrShow = (ImageView) convertView
					.findViewById(R.id.imageHideShow);
			holder.commodityImage = (ImageView) convertView
					.findViewById(R.id.list_commodity_image);
			holder.discountPrice = (TextView) convertView
					.findViewById(R.id.discountPrice);
			holder.quantity = (TextView) convertView
					.findViewById(R.id.quantity);
			holder.discountInPercentage = (TextView) convertView
					.findViewById(R.id.discountInPercentage);

			typeface = Typeface.createFromAsset(context.getAssets(),
					"fonts/Sanchez-Regular_0.ttf");
			holder.commodityName.setTypeface(typeface);
			holder.varietyName.setTypeface(typeface);
			holder.date.setTypeface(typeface);
			holder.price.setTypeface(typeface);
			holder.unit.setTypeface(typeface);
			holder.id.setTypeface(typeface);
			holder.discountPrice.setTypeface(typeface);
			holder.quantity.setTypeface(typeface);
			holder.discountInPercentage.setTypeface(typeface);
		 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/*try
		{
			if (Build.VERSION.SDK_INT >= 16) {

				userId.setBackground(customBg);
				password.setBackground(customBg);

			} else {

				userId.setBackgroundDrawable(customBg);
				password.setBackgroundDrawable(customBg);
			}
		}
		catch (Exception e) {

			Toast.makeText(getActivity(), "Something wrong..", Toast.LENGTH_LONG).show();


		}*/
		if (rowItem.getStatusPrice() != null
				&& rowItem.getStatusPrice().equalsIgnoreCase("Hide")) {
		/*	holder.priceHideOrShow.setBackground(context.getResources()
					.getDrawable(R.drawable.hide2));*/


			if (Build.VERSION.SDK_INT >= 16) {
				holder.priceHideOrShow.setBackground(context.getResources()
						.getDrawable(R.drawable.hide2));
			} else {

				holder.priceHideOrShow.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.hide2));
			 }


		} else {
			/*holder.priceHideOrShow.setBackground(context.getResources()
					.getDrawable(R.drawable.show2));*/
			if (Build.VERSION.SDK_INT >= 16) {
				holder.priceHideOrShow.setBackground(context.getResources()
						.getDrawable(R.drawable.show2));
			} else {

				holder.priceHideOrShow.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.show2));
			}
		}

		holder.unitTagVALue.setText(rowItem.getUnitTag());
		holder.commodityName.setText(rowItem.getCommodityName());
		holder.varietyName.setText(rowItem.getVarietyName());
		holder.date.setText(rowItem.getDate());
		holder.mainCategory.setText(rowItem.getMainCategory());
		holder.price.setText(rupeeSymbol + rowItem.getPrice());
		holder.unit.setText(rowItem.getUnit());
		holder.id.setText(rowItem.getId());
		holder.discountPrice.setText(rupeeSymbol + rowItem.getDiscountPrice());
		holder.discountInPercentage.setText(rowItem.getDiscountInPercentage()
				+ " %");

		if (!rowItem.getDiscountInPercentage().equalsIgnoreCase("No discount")) {

			holder.price.setPaintFlags(holder.price.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			holder.offer.setVisibility(View.VISIBLE);
			holder.discountInPercentage.setVisibility(View.VISIBLE);
			holder.price.setVisibility(View.VISIBLE);

		} else {
			holder.price.setPaintFlags(0);
			holder.offer.setVisibility(View.GONE);
			holder.price.setVisibility(View.GONE);
			holder.discountInPercentage.setVisibility(View.GONE);
		}

		holder.commodityImage.setTag(rowItem.getImageURL());
		BitmapManager.INSTANCE.loadBitmap(rowItem.getImageURL(),
				holder.commodityImage, 200, 200);

		holder.priceHideOrShow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					if (rowItem.getStatusPrice().equalsIgnoreCase("Hide")) {

						listener.DoTaskCompleted(rowItem.getId().toString(),
								CommodityDetailsActivity.VENDOR_ID, "Show");
					} else {

						listener.DoTaskCompleted(rowItem.getId().toString(),
								CommodityDetailsActivity.VENDOR_ID, "Hide");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		holder.commodityImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (holder.commodityImage != null) {
					BitmapDrawable drawable = (BitmapDrawable) holder.commodityImage
							.getDrawable();
					Bitmap bitmap = drawable.getBitmap();

					maximizeImage(bitmap);
				}
			}
		});

		return convertView;
	}

	/**
	 * Update the hide or show button
	 */
	public void UpdateHideOrShow(String commoditId, String status) {

		try {

			for (int i = 0; i < filteredList.size(); i++) {
				if (filteredList.get(i).getId().equalsIgnoreCase(commoditId)) {
					filteredList.get(i).setStatusPrice(status);
				}
				if (commodityDetails.get(i).getId()
						.equalsIgnoreCase(commoditId)) {
					commodityDetails.get(i).setStatusPrice(status);

				}
			}

			Collections.sort(filteredList, convertStringToDateAndCompare);
			notifyDataSetChanged();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Showing alert dialog for hide or show option
	 */
	public void AlertDialogWindow(final String pStatus,
			final String commoditIds, final String vendorIds) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		builder.setTitle("Discard cart");
		if (pStatus.equalsIgnoreCase("Hide")) {
			builder.setMessage("Are you sure you want to hide this commodity price to Consumer?");
		} else {
			builder.setMessage("Are you sure you want to show this commodity price to Consumer?");
		}

		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (pStatus.equalsIgnoreCase("Hide")) {

				} else {

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

	/**
	 * Common method for showing toast message
	 */
	public void displayToastMessage(String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public Filter getFilter() {
		if (friendFilter == null) {
			friendFilter = new FriendFilter();
		}

		return friendFilter;
	}

	/**
	 * Custom filter for friend list Filter content in friend list according to
	 * the search text
	 */
	private class FriendFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<RowItem> tempList = new ArrayList<RowItem>();

				// search content in friend list
				for (RowItem user : commodityDetails) {
					if (user.getCommodityName().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getVarietyName().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getPrice().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getDate().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else if (user.getDate().toLowerCase()
							.contains(constraint.toString().toLowerCase())) {
						tempList.add(user);
					} else {
					}
				}

				filterResults.count = tempList.size();
				filterResults.values = tempList;
			} else {
				filterResults.count = commodityDetails.size();
				filterResults.values = commodityDetails;
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
			Collections.sort(filteredList, convertStringToDateAndCompare);
			notifyDataSetChanged();
		}
	}

	/**
	 * resize the bitmap image
	 */
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		// matrix.preRotate(90);
		matrix.preScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	public void callAsynchronousTask() {

		if (!Connectivity.isConnectedFast(context)) {

			Toast.makeText(
					context,
					"Your network connection may be slow.Please check your connection",
					Toast.LENGTH_LONG).show();
		}

	}

	private void maximizeImage(Bitmap bitmap) {
		try {
			Dialog bitmapDialog = new Dialog(context);
			bitmapDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			bitmapDialog.setContentView(R.layout.image_layout);
			ImageView image = (ImageView) bitmapDialog
					.findViewById(R.id.thumbnail_IMAGEVIEW);
			image.setImageBitmap(bitmap);
			bitmapDialog.show();

		} catch (Exception e) {
			// TODO: handle exception
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
		}
	};
}
