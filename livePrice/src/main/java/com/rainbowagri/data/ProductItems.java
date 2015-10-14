/**
 * 
 * Rainbow live price consumer
 * Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
 */

/**
 * @author SELLATHURAI & SARAVANAN
 * @version 1.0, Rel 3, 1 Nov 2014
 */
package com.rainbowagri.data;



import android.graphics.Bitmap;

public class ProductItems {
	private String commodityName;
	private String commodityPrice;
	private String discountPercent;
	private String discountPrice;
	private String tempQty;
	private String category;
	private int commodityId;;
	private String totalPrice;
	private String quantity;
	private String imagePath;
	private String count;
	private String title;
	private Bitmap image;
	private String unit;
	private String status;
	private String priceStatus;
	private String minuminQty;
	private String minuminUnit;

	public String getMinuminQty() {
		return minuminQty;
	}

	public void setMinuminQty(String minuminQty) {
		this.minuminQty = minuminQty;
	}

	public String getMinuminUnit() {
		return minuminUnit;
	}

	public void setMinuminUnit(String minuminUnit) {
		this.minuminUnit = minuminUnit;
	}

	public String getPriceStatus() {
		return priceStatus;
	}

	public void setPriceStatus(String priceStatus) {
		this.priceStatus = priceStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(int commodityId) {
		this.commodityId = commodityId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(String discountPercent) {
		this.discountPercent = discountPercent;
	}

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getTempQty() {
		return tempQty;
	}

	public void setTempQty(String tempQty) {
		this.tempQty = tempQty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityPrice() {
		return commodityPrice;
	}

	public void setCommodityPrice(String commodityPrice) {
		this.commodityPrice = commodityPrice;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
