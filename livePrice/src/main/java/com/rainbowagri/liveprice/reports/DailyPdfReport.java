/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
   * @author Sathish
 * @version v1.0
 * @date 08-oct-2014
 */
package com.rainbowagri.liveprice.reports;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.util.Log;

import com.rainbowagri.data.RowItem;
 
@TargetApi(Build.VERSION_CODES.KITKAT)
public class DailyPdfReport extends PdfDocument {

	private static final String TAG = "Daily PDF Report";

	Page page;
	PageInfo pageInfo;

	// Define page info for ISO - A4/8.4"x11" paper
	// units are in points (1/72 of an inch)
	private static final int PAGE_WIDTH = 605;
	private static final int PAGE_HEIGHT = 792;

	// Left margin; say 2cm -> 2*28.3 = 56.6 => 57points
	private static final int LEFT_MARGIN = 57;

	// Top margin; say 1cm -> 1*28.3 => 28points
	private static final int TOP_MARGIN = 28;

	// Right margin; say 1cm
	private static final int RIGHT_MARGIN = 28;

	// Bottom margin; say 1cm
	private static final int BOTTOM_MARGIN = 28;

	// Define various font sizes
	private static final int TITLE_SIZE = 20;
	private static final int CONTENT_SIZE = 11;
	private static final int COPYRIGHT_SIZE = 7;

	// Define text padding
	private static final int PADDING = 3;
	private static final int LINE_SPACING = 5;

	// Drawing area
	private static final float LEFT = PAGE_WIDTH - (PAGE_WIDTH - LEFT_MARGIN);
	private static final float TOP = PAGE_HEIGHT - (PAGE_HEIGHT - TOP_MARGIN);
	private static final float RIGHT = PAGE_WIDTH - RIGHT_MARGIN;
	private static final float BOTTOM = PAGE_HEIGHT - BOTTOM_MARGIN;

	// Table properties
	private static final int MAX_COMM_IN_PAGE = 35; // calculated based on
													// available area to draw
													// content

	Rect mBounds = new Rect((int) LEFT, (int) TOP, (int) RIGHT, (int) BOTTOM);
	// cursor - Represents the position current draw will start
	Point cursor = new Point((int) LEFT, (int) TOP);

	private String shopName = null;
	private int totalPages = 1;
	private ArrayList<RowItem> commodityList = null;

	// Table header title
	private String tableHeader[] = { "S.No", "Commodity", "Price" };
	private String copyRight = "Powered by RainbowAgri - Rainbow_Sell";

	/**
	 * 
	 */
	public DailyPdfReport(ArrayList<RowItem> data, String shopName) {

		Log.i(TAG, "Started drawing content");

		// Check for null data
		if (data != null && shopName != null) {
			this.shopName = shopName;
			this.commodityList = data;
		} else {
			this.close();
			Log.i(TAG, "No data received to process");
		}
	}

	public DailyPdfReport createPdf() {

		int dataSize = commodityList.size();
		// For the given font type and size, the drawable area can hold 35 lines
		// of text
		if (dataSize % 35 == 0) {
			totalPages = dataSize / 35;
		} else {
			totalPages = (dataSize / 35) + 1;
		}
		Log.i(TAG, "No. of pages in this document = " + totalPages);
		pageInfo = new PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, totalPages)
				.create();
		Log.i(TAG, "Document size = " + this.getPages().size());

		// temp array list to hold 35 commodities for one page
		ArrayList<RowItem> tempData = new ArrayList<RowItem>();

		for (int pageCount = 0; pageCount < totalPages; pageCount++) {
			Page page = this.startPage(pageInfo);
			drawTitle(page);
			// Let's give 50 point space between title and content in first page
			cursor.y = cursor.y + 50;
			// Split data to be drawn based on page and send it to draw content
			// method
			for (int i = 0; i < MAX_COMM_IN_PAGE
					&& (i + pageCount * MAX_COMM_IN_PAGE) < commodityList
							.size(); i++) {
				tempData.add(commodityList
						.get(i + pageCount * MAX_COMM_IN_PAGE));
			}
			drawPageContent(page, tempData, pageCount);
			drawFooter(page, pageCount + 1);
			this.finishPage(page);
			tempData.clear();
			newPage();
		}
		return this;
	}

	/**
	 * Draws title: shop_name,report generated date and day
	 * 
	 * @param page
	 */
	private void drawTitle(Page page) {

		Canvas canvas = page.getCanvas();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(TITLE_SIZE);
		TextSize titleSize = new TextSize();
		titleSize = calculateTextBounds(paint, shopName);
		canvas.drawText(shopName, mBounds.centerX()
				- (titleSize.getWidth() / 2f),
				TOP + PADDING + titleSize.getHeight(), paint);
		// update cursor's y position
		cursor.y = cursor.y + PADDING + titleSize.getHeight() + LINE_SPACING;
		paint.setTextSize(CONTENT_SIZE);
		TextSize dateSize = new TextSize();
		dateSize = calculateTextBounds(paint, getDate());
		// update cursor's x position
		cursor.x = mBounds.right - dateSize.width - PADDING;
		canvas.drawText(getDate(), cursor.x, cursor.y, paint);

		// update cursor's y position for further drawing
		cursor.y = cursor.y + dateSize.getHeight() + LINE_SPACING;
		TextSize daySize = new TextSize();
		daySize = calculateTextBounds(paint, getDay());
		// update cursor's x position
		cursor.x = mBounds.right - daySize.width - PADDING;
		canvas.drawText(getDay(), cursor.x, cursor.y, paint);
		// update cursor's Y position
		cursor.y = cursor.y + daySize.getHeight() + LINE_SPACING;
	}

	/**
	 * Draws the commodity list with price in table format
	 */
	private void drawPageContent(Page page, ArrayList<RowItem> tableData,
			int pageNumb) {

		Canvas canvas = page.getCanvas();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(CONTENT_SIZE);
		drawTableHeader(canvas);
		TextSize mTextSize = new TextSize();

		cursor.y = cursor.y + 10;

		for (int i = 0; i < tableData.size(); i++) {
			if (cursor.y < mBounds.bottom) {

				mTextSize = calculateTextBounds(paint, "" + (i + 1));
				newLine();
				cursor.y = cursor.y + mTextSize.height + LINE_SPACING + PADDING;
				canvas.drawText("" + ((i + 1) + (pageNumb * MAX_COMM_IN_PAGE)),
						cursor.x, cursor.y, paint);

				// update cursor x position
				cursor.x = cursor.x + 100;
				canvas.drawText(tableData.get(i).getCommodityName(), cursor.x,
						cursor.y, paint);

				// update cursor position
				cursor.x = cursor.x + 300;
				canvas.drawText("Rs. " + tableData.get(i).getPrice() + " / "
						+ commodityList.get(i).getUnit(), cursor.x, cursor.y,
						paint);
			}
		}
	}

	/**
	 * Measures and draws content table header like below for the given canvas
	 * ________________________________________________ | | | | | S.No. |
	 * Commodity | Price | |_______|____________________________|___________|
	 */
	private void drawTableHeader(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(CONTENT_SIZE);
		TextSize mTextSize = new TextSize();
		// update cursor position
		newLine();
		// cursor.y = cursor;

		mTextSize = calculateTextBounds(paint, tableHeader[0]);
		canvas.drawText(tableHeader[0], cursor.x, cursor.y + PADDING, paint);
		// Give horizontal space
		cursor.x = cursor.x + 100;
		canvas.drawText(tableHeader[1], cursor.x, cursor.y + PADDING, paint);
		// Give horizontal space
		cursor.x = cursor.x + 300;
		canvas.drawText(tableHeader[2], cursor.x, cursor.y + PADDING, paint);
	}

	private void drawFooter(Page page, int pageNumb) {
		Canvas canvas = page.getCanvas();
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(CONTENT_SIZE);

		String pageStr = "Page " + pageNumb + " of " + totalPages;
		TextSize mTextSize = new TextSize();
		mTextSize = calculateTextBounds(paint, pageStr);
		canvas.drawText(pageStr, mBounds.centerX()
				- (mTextSize.getWidth() / 2f),
				BOTTOM + PADDING + mTextSize.getHeight(), paint);

		paint.setTextSize(COPYRIGHT_SIZE);
		mTextSize = calculateTextBounds(paint, copyRight);

		canvas.drawText(copyRight, mBounds.right - mTextSize.width,
				mBounds.bottom + 2 * mTextSize.height, paint);
	}

	/**
	 * Calculates the required drawable width and height area for the given
	 * paint and string
	 * 
	 * @param mTextPaint
	 * @param mText
	 * @return
	 */
	private TextSize calculateTextBounds(Paint mTextPaint, String mText) {

		TextSize mTextSize = new TextSize(); // Our calculated text bounds
		Rect textBounds = new Rect();
		mTextPaint.getTextBounds(mText, 0, mText.length(), textBounds);
		mTextSize.setWidth((int) mTextPaint.measureText(mText)); // Use
																	// measureText
																	// to
																	// calculate
																	// width
		mTextSize.setHeight(textBounds.height()); // Use height from
													// getTextBounds()
		return mTextSize;
	}

	/**
	 * Returns the system date in DD - MM - YYYY format
	 * 
	 * @return formatted_system_date
	 */
	private String getDate() {
		Calendar cal = Calendar.getInstance();
		String date = "" + cal.get(Calendar.DATE) + " - "
				+ String.valueOf(cal.get(Calendar.MONTH) + 1) + " - "
				+ cal.get(Calendar.YEAR);
		return date;
	}

	/**
	 * Returns system's day
	 * 
	 * @return
	 */
	private String getDay() {
		Calendar cal = Calendar.getInstance();
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			return "Sunday";
		case 2:
			return "Monday";
		case 3:
			return "Tuesday";
		case 4:
			return "Wednesday";
		case 5:
			return "Thursday";
		case 6:
			return "Friday";
		case 7:
			return "Saturday";
		default:
			return null;
		}
	}

	/**
	 * Starts a new line, brings the cursor to the left corner of drawable area.
	 */
	private void newLine() {
		cursor.x = mBounds.left + PADDING;
	}

	/**
	 * Starts a new page, resets the cursor position.
	 */
	private void newPage() {
		cursor.x = (int) LEFT;
		cursor.y = (int) TOP;
	}

	/**
	 * Static class to define Text size parameters
	 * 
	 * @author SATHISH
	 * 
	 */
	private static class TextSize {
		int height;
		int width;

		public void setHeight(int height) {
			this.height = height;
		}

		public int getHeight() {
			return height;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getWidth() {
			return width;
		}
	}
}
