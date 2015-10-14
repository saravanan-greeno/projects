/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 10 september 2014
 */
package com.rainbowagri.data;

public class AddCommodityData {

	private String commodityName;
	private String mainCategory;
	private String price;
	private String units;
	private String category;
	private String lastUpdatedDate;
	private long coordinatorRefId;
	private long commodityRefId;
	private String minimumQuantity;
	private String quantity;
	private String discounts;
	private String minQtyUnit;
	private String commodityInfo;

	public String getCommodityInfo() {
		return commodityInfo;
	}

	public void setCommodityInfo(String commodityInfo) {
		this.commodityInfo = commodityInfo;
	}

	
	
	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public long getCoordinatorRefId() {
		return coordinatorRefId;
	}

	public void setCoordinatorRefId(long coordinatorRefId) {
		this.coordinatorRefId = coordinatorRefId;
	}

	public long getCommodityRefId() {
		return commodityRefId;
	}

	public void setCommodityRefId(long commodityRefId) {
		this.commodityRefId = commodityRefId;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getDiscounts() {
		return discounts;
	}

	public void setDiscounts(String discounts) {
		this.discounts = discounts;
	}

	public String getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(String minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public String getMinQtyUnit() {
		return minQtyUnit;
	}

	public void setMinQtyUnit(String minQtyUnit) {
		this.minQtyUnit = minQtyUnit;
	}

}
