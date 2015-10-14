/**
* @author Ramya.D
* @Project Crash Report
* @Date 21.08.2014
* Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
*/

package com.rainbowagri.profilepage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class ApplicationInformation {

	final int CONN_WAIT_TIME = 40000;
	final int CONN_DATA_WAIT_TIME = 40000;

	/* This method return the Application Name ;
	 * Input - Context */
	public String getApplicationName(Context myContext) throws NameNotFoundException{//,ApplicationInfo info){
		
		ApplicationInfo applicationInfo = myContext.getPackageManager()
			    .getPackageInfo(myContext.getPackageName(), 0).applicationInfo;
        PackageManager p = myContext.getPackageManager();
        String label = p.getApplicationLabel(applicationInfo).toString();
        return label;
    }
	public boolean isInternetConnection(Context context) {
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
/*	public boolean isWifiConnected(Context context)
	{
		boolean wifi = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(mWifi.isConnected())
			wifi = true;
		return wifi;
	}
	
	
	public static String getSystemConnectivityStatus(Context context)
	{
		String connection = "No Connection";
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkActive = cm.getActiveNetworkInfo();
		
		if(networkActive != null)
		{
			if(networkActive.getType() == ConnectivityManager.TYPE_WIFI)
				connection = "Wifi";
			if(networkActive.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				if(connection.equals("No Connection"))
					connection = "Mobile Data";
				else
					connection = connection+":"+ "Mobile Data";
			}
				
		}
	
		return connection;
	}
*/
}
