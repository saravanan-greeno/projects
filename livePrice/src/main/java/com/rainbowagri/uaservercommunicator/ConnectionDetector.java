/**
 * Rainbow Broadcaster
 * Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
 */
package com.rainbowagri.uaservercommunicator;

/**
 * @author SELLATHURAI
 * @version 1.0, Rel 2, 31 Mar2014
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;

public class ConnectionDetector extends Activity {

	private Context _context;
	boolean connected = false;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	/**
	 * Detecting network availability thorough out application
	 */
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED
				|| connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			// we are connected to a network
			connected = true;
		} else
			connected = false;

		return connected;
	}

	/**
	 * Alert view process thorough out application
	 */
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);

		alertDialog.setMessage(message);


		alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
			}
		});

		alertDialog.show();
		Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		button.setTextColor(Color.WHITE);
//		button.setBackgroundResource(R.drawable.button_press);

	}
	
	public static String convertHexaToString(String getString) {

		StringBuilder stringBuilder = new StringBuilder();
		String tempData = "";
		for (int j = 0; j < getString.length(); j += 4) {
			tempData = getString.substring(j, j + 4);
			stringBuilder.append((char) Integer.parseInt(tempData, 16));
		}
		String convertedString = stringBuilder.toString();

		return convertedString;

	}

}
