/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 10 september 2014
 */
package com.rainbowagri.data;


public class OrderHistoryDetails {

	private String OrderId;
	private String Date;
	private String Status;
	private String MobileNUmber;
	private String Name;
	private String ClikedValue;

	public String getClikedValue() {
		return ClikedValue;
	}

	public void setClikedValue(String clikedValue) {
		ClikedValue = clikedValue;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMobileNUmber() {
		return MobileNUmber;
	}

	public void setMobileNUmber(String mobileNUmber) {
		MobileNUmber = mobileNUmber;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

}
