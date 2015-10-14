/**
 * Rainbow Sell
 * * @author SELLATHURAI
 * @version 1.0, Rel 2
 * Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
 */
package com.rainbowagri.ServerCommuicator;

/**
 
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

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
			connected = true;
		} else
			connected = false;

		return connected;
	}

	public void displayToast(String msg) {
        Toast toast = Toast.makeText(_context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
	}
	
	public static String convertStringToHexadecimal(String hexaString) {

		String convertingString = "";
		for (int i = 0; i < hexaString.length(); i++) {
			convertingString = convertingString
					+ String.format("%04x", (int) hexaString.charAt(i));
		}

		return convertingString;

	}

	/**
	 * Convert Hexadecimal To String value
	 * 
	 * @param context
	 * @return
	 */
	public   String convertHexaToString(String getString) {

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
