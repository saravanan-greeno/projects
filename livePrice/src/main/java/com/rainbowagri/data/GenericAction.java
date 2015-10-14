/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by sella.R <sella.ragavan@greeno.in>, 02 nov 2014
 */
package com.rainbowagri.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.analytics.HitBuilders;
import com.rainbowagri.liveprice.MyApplications;


public class GenericAction {

	/**
	 * Converting String to Hexa value
	 * 
	 * @param context
	 * @return
	 */

	public static String convertStringToHexadecimal(String hexaString) {

		String convertingString = "";
		for (int i = 0; i < hexaString.length(); i++) {
			convertingString = convertingString
					+ String.format("%04x", (int) hexaString.charAt(i));
		}

		return convertingString;

	}
	
	public  static  void trackerSetup(String screenName,String dimentionValue) {

		MyApplications.tracker().setScreenName(screenName);

        // Send the custom dimension value with a screen view.
        // Note that the value only needs to be sent once.
	MyApplications.tracker().send(new HitBuilders.ScreenViewBuilder()
            .setCustomDimension(1, dimentionValue)
            .build()
        );

	}

	/**
	 * Convert Hexadecimal To String value
	 * 
	 * @param context
	 * @return
	 */
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

	// Converting decimal format from double value
	public static String convertDecimalFormat(double amount) {

		Double totalAmounts = Double.valueOf(amount);
		DecimalFormat dec = new DecimalFormat();
		dec.setMinimumFractionDigits(2);
		String credits = dec.format(totalAmounts);
		return credits;

	}

	// Converting String to double value
	public static Double stringToDouble(String x) {
		if (x != null)
			return Double.parseDouble(x);

		return null;
	}

	// converting double to string
	public static String doubleToString(Double y) {
		if (y != null)
			return String.valueOf(y);

		return null;
	}

	public static boolean loadingMore = false;
	public static List<String> productId = new ArrayList<String>();
	public static RowItem saveData = new RowItem();
	public static String commodityId = "";
	public static RowItem editData = new RowItem();
	public static List<String> commodityImageURL = new ArrayList<String>();
	public static List<String> removeImageURL = new ArrayList<String>();
}
