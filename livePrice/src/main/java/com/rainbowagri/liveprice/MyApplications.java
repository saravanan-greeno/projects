
/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.lang.reflect.Field;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewConfiguration;

public class MyApplications extends Application {
	private static Context mContext;
	public static Boolean status = true;
	 private static GoogleAnalytics analytics;
	/**
     * The default app tracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		 analytics = GoogleAnalytics.getInstance(this);
		 
	      //  analytics = GoogleAnalytics.getInstance(this);

	        // TODO: Replace the tracker-id with your app one from https://www.google.com/analytics/web/
	        tracker = analytics.newTracker("UA-65520509-2"); 

	        // Provide unhandled exceptions reports. Do that first after creating the tracker
	        tracker.enableExceptionReporting(true);

	        // Enable Remarketing, Demographics & Interests reports
	        // https://developers.google.com/analytics/devguides/collection/android/display-features
	        tracker.enableAdvertisingIdCollection(true);

	        // Enable automatic activity tracking for your app
	        tracker.enableAutoActivityTracking(true);
		
		/**
		 * Storing the shop name across application
		 */
		SharedPreferences mPreferences = this.getSharedPreferences(
				CommodityDetailsActivity.SHOP_SHARED_PREF_NAME, MODE_PRIVATE);
		if (mPreferences.getBoolean(CommodityDetailsActivity.SHOP_STATUS, true)) {
			SharedPreferences.Editor activeEdit = mPreferences.edit();
			activeEdit.putBoolean(CommodityDetailsActivity.SHOP_STATUS, false);
			activeEdit.putString(CommodityDetailsActivity.STATUS, "Yes");
			activeEdit.commit();

		}

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}

	}
}
