/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Sella & Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;


public interface NotifyListener {

	public void taskCompleted();

	public void DoTaskCompleted(String commodityId, String vendorId,
			String priceStatus);

	public void isSearchActive(String active);
	 
}
