/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.data.GenericAction;
import com.rainbowagri.data.ProductItems;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;


public class OrderStatusActivity extends Activity {

	private TextView totalAmt, status;

	private ImageView statusImage;
	private int year;
	private int month;
	private int day;
	ListView mlistView;
	String totalAmount, statusProcess;
	static final int DATE_PICKER_ID_FROMDATE = 1111;
	static final int DATE_PICKER_ID_TODATE = 2222;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order_status_main);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.order_status_main);
		UploadCrashReport.startService(this);*/

		totalAmt = (TextView) findViewById(R.id.amount);
		status = (TextView) findViewById(R.id.status);
		statusImage = (ImageView) findViewById(R.id.statusimage);

		mlistView = (ListView) findViewById(R.id.listView1);
		DoTheWebserviceTask();
	}

	/**
	 * Async task class for loading track order status
	 */
	private void DoTheWebserviceTask() {

		XMLCreator xmlCreater = new XMLCreator();
		String reqXML = xmlCreater.fetchOrderStatus(OrderHistory.ORDER_ID);
		InitiateAsyncTask(reqXML);

	}

	/**
	 * Http connection setup
	 */
	private void InitiateAsyncTask(String xmlReq) {
		XMLRequestAndResponseData reguest = new XMLRequestAndResponseData();
		reguest.setRequestXMLdata(xmlReq);
		reguest.setUrl(getString(R.string.orderTrack));

		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				this, reguest);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {
				parseResponseData(result);

			}
		});

		task.execute("");

	}

	/**
	 * Parsing the response from server
	 */
	private void parseResponseData(XMLRequestAndResponseData result) {
		try {

			XmlParser parser = new XmlParser();
			Document doc = parser.getDomElement(result.getResult());
			NodeList nodeList = doc.getElementsByTagName("view");
			ArrayList<ProductItems> itemsOfProduct = new ArrayList<ProductItems>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				ProductItems data = new ProductItems();

				Element element = (Element) nodeList.item(i);
				try {
					data.setCommodityName(GenericAction
							.convertHexaToString(parser.getValue(element,
									"commodityName")));
				} catch (Exception e) {
					data.setCommodityName(parser.getValue(element,
							"commodityName"));
				}

				data.setCommodityPrice(parser.getValue(element, "actualPrice"));
				data.setUnit(parser.getValue(element, "unit"));
				data.setQuantity(parser.getValue(element, "quantity"));
				data.setTotalPrice(parser.getValue(element, "totalPrice"));
				totalAmount = parser.getValue(element, "totalPrice");
				data.setStatus(parser.getValue(element, "status"));
				statusProcess = parser.getValue(element, "status");
				itemsOfProduct.add(data);
			}
			OrderStatusAdapter mAdapter = new OrderStatusAdapter(
					getApplicationContext(), R.layout.order_status_row,
					itemsOfProduct);
			mlistView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			final String rupee = getResources()
					.getString(R.string.rupee_symbol);
			totalAmt.setText("Order Total :" + " " + rupee + totalAmount);

			if (statusProcess.equalsIgnoreCase("Delivered")) {
				statusImage.setImageDrawable(getResources().getDrawable(
						R.drawable.delivered));
				status.setText("Status : Delivered");
			} else if (statusProcess.equalsIgnoreCase("Pending")) {
				statusImage.setImageDrawable(getResources().getDrawable(
						R.drawable.pending_status));
				status.setText("Status : In progress");
			} else {
				statusImage.setImageDrawable(getResources().getDrawable(
						R.drawable.cancelled3));
				status.setText("Status : Cancelled");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			finish();
		}

	}

	/**
	 * Toast message showing
	 */

	public void toastMessage(String alert) {
		Toast toast = Toast.makeText(getApplicationContext(), alert,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
