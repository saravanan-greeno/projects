/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Saravanan <saravanan.s@greeno.in>,  
 */
package com.rainbowagri.liveprice;


import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	ArrayList<String> imageList = new ArrayList<String>();
	int width = 220, height = 220;

	public ImageAdapter(Context c) {
		context = c;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		if (screenWidth != 0 && screenHeight != 0) {
			width = (screenWidth / 3) - 16;
		}
	}

	void add(String path) {
		imageList.add(path);
	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(width, width));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
			imageView.setBackgroundResource(R.drawable.take_photo_profile);
		} else {
			imageView = (ImageView) convertView;
		}

		if (position == 0) {
			imageView.setImageResource(R.drawable.take_photo_profile);
		} else {
			Bitmap bm = decodeSampledBitmapFromUri(imageList.get(position),
					width, width);
			imageView.setImageBitmap(bm);
		}

		return imageView;
	}

	/**
	 * Method for decoding bitmap image from URL
	 */
	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);
		return bm;
	}

	/**
	 * Method for calculating size of bitmap
	 */
	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
