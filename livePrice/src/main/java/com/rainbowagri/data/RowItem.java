/**
 * Rainbow LivePrice Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
 * 
 * @author G1019 SARAVANAN
 * @version 1.0, Rel 1, 23-09 2014
 */
package com.rainbowagri.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rainbowagri.liveprice.CustomListViewAdapter;



public class RowItem {
	private String commodityName;
	private String mainCategory;
	private String varietyName;
	private String date;
	private String price;
	private String unit;
	private String id;
	private boolean selected;
	private String minimumQuantity;
	private String discountInPercentage;
	private String discountPrice;
	private String quantity;
	private String imageURL;
	private String statusPrice;
	private Bitmap commodityImage;
	private String unitTag;
	private Context context;

	private String imgUrl;

	private Bitmap image;

	private CustomListViewAdapter listAdapter;

	private String commodityInfo;

	public String getCommodityInfo() {
		return commodityInfo;
	}

	public void setCommodityInfo(String commodityInfo) {
		this.commodityInfo = commodityInfo;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public CustomListViewAdapter getListAdapter() {
		return listAdapter;
	}

	public void setListAdapter(CustomListViewAdapter listAdapter) {
		this.listAdapter = listAdapter;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Class is for getting the values in text views in list view for entire
	 * apps list view
	 * 
	 * @param mLine1
	 * @param mLine2
	 * @param mLine3
	 */
	public RowItem(String mLine1, String mLine2, String mLine3, String mLine4,
			String unit, String id) {
		this.commodityName = mLine1;
		this.varietyName = mLine2;
		this.date = mLine3;
		this.price = mLine4;
		this.unit = unit;
		this.id = id;
	}

	public String getStatusPrice() {
		return statusPrice;
	}

	public void setStatusPrice(String statusPrice) {
		this.statusPrice = statusPrice;
	}

	public RowItem() {
		super();

	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getVarietyName() {
		return varietyName;
	}

	public void setVarietyName(String varietyName) {
		this.varietyName = varietyName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return commodityName + "\n" + varietyName + "\n" + date + "\n" + price
				+ "\n" + unit;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getDiscountInPercentage() {
		return discountInPercentage;
	}

	public void setDiscountInPercentage(String discountInPercentage) {
		this.discountInPercentage = discountInPercentage;
	}

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Bitmap getCommodityImage() {
		return commodityImage;
	}

	public void setCommodityImage(Bitmap commodityImage) {
		this.commodityImage = commodityImage;
	}

	public String getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(String minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public String getUnitTag() {
		return unitTag;
	}

	public void setUnitTag(String unitTag) {
		this.unitTag = unitTag;
	}

	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void loadImage(CustomListViewAdapter adapter, Context conext) {
		// HOLD A REFERENCE TO THE ADAPTER
		this.context = conext;

		// callAsynchronousTask();

		this.listAdapter = adapter;
		if (imageURL != null && !imageURL.equals("")) {
			new ImageLoadTask().execute(imageURL);
		}
	}

	// ASYNC TASK TO AVOID CHOKING UP UI THREAD
	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {

		protected void onPreExecute() {
			Log.i("ImageLoadTask", "Loading image...");
		}

		// PARAM[0] IS IMG URL
		protected Bitmap doInBackground(String... param) {
			Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
			try {
				Bitmap b = downloadUrl(param[0]);
				return b;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		protected void onProgressUpdate(String... progress) {
			// NO OP
		}

		protected void onPostExecute(Bitmap ret) {
			if (ret != null) {
				Log.i("ImageLoadTask", "Successfully loaded " + commodityName
						+ " image");
				commodityImage = ret;
				if (listAdapter != null) {
					// WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
					listAdapter.notifyDataSetChanged();
				}
			} else {
				Log.e("ImageLoadTask", "Failed to load " + commodityName
						+ " image");
			}
		}
	}
	/*URL url = new URL("http://myurl.com");
	HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	connection.setReadTimeout(10000);
	connection.setConnectTimeout(15000);
	connection.setRequestMethod("POST");
	connection.setDoInput(true);
	connection.setDoOutput(true);
	Uri.Builder builder = new Uri.Builder()
			.appendQueryParameter("key1", valu1)
			.appendQueryParameter("key2", value2);
	String query = builder.build().getEncodedQuery();
	OutputStream os = connection.getOutputStream();
	BufferedWriter writer = new BufferedWriter(
			new OutputStreamWriter(os, "UTF-8"));
	writer.write(query);
	writer.flush();
	writer.close();
	os.close();
	connection.connect();*/
	/*static Bitmap downloadBitmap(String url) {

		Bitmap tempBitmap = null;
		Bitmap bitmaps = null;
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					tempBitmap = BitmapFactory.decodeStream(inputStream);

					bitmaps = Bitmap
							.createScaledBitmap(tempBitmap,
									tempBitmap.getWidth(),
									tempBitmap.getHeight(), true);
					if (tempBitmap != bitmaps) {
						tempBitmap.recycle();
					}
					return bitmaps;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();

		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
*/

	private Bitmap downloadUrl(String strUrl) throws IOException{
		Bitmap bitmap=null;
		InputStream iStream = null;
		try{
			URL url = new URL(strUrl);
			/** Creating an http connection to communcate with url */
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			/** Connecting to url */
			urlConnection.connect();

			/** Reading data from url */
			iStream = urlConnection.getInputStream();

			/** Creating a bitmap from the stream returned from the url */
			bitmap = BitmapFactory.decodeStream(iStream);

		}catch(Exception e){
			Log.d("Exception  ", e.toString());
		}finally{
			iStream.close();
		}
		return bitmap;
	}
	public static Bitmap getBitmapFromURL(String src) {
		try {
			Bitmap tempBitmap = null;
			Bitmap bitmaps = null;
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			// // Bitmap myBitmap = BitmapFactory.decodeStream(input);
			//
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = 1;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inTempStorage = new byte[24 * 1024];

			// Download Image from URL
			// InputStream inputs = new java.net.URL(src).openStream();
			// Decode Bitmap
			// tempBitmap = BitmapFactory.decodeStream(inputs);

			tempBitmap = BitmapFactory.decodeStream(input, null, opts);

			// bitmap = Bitmap.createScaledBitmap(tempBitmap,100,100, true);
			bitmaps = Bitmap.createScaledBitmap(tempBitmap,
					tempBitmap.getWidth(), tempBitmap.getHeight(), true);
			if (tempBitmap != bitmaps) {
				tempBitmap.recycle();
			}
			input.close();

			return bitmaps;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {

						if (!Connectivity.isConnectedFast(context)) {

							Toast.makeText(
									context,
									"Your network connection seems to be very slow.Please check your connection",
									Toast.LENGTH_LONG).show();
						}

					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 60000); // execute in every 50000
														// ms
	}

}
