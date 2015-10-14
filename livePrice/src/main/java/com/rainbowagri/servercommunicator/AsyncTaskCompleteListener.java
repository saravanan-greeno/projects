/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 20 November 2014
 */
package com.rainbowagri.servercommunicator;



@SuppressWarnings("hiding")
public interface AsyncTaskCompleteListener<XMLRequestAndResponseData> {
	public void onTaskComplete(XMLRequestAndResponseData result);

}
