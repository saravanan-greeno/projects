/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */
package com.rainbowagri.liveprice;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbowagri.data.GenericAction;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;


public class ManagePrice extends DialogFragment {

	LinearLayout editProces, delelteProcess;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.manage_prices, null);
		builder.setTitle("Manage Price");

		builder.setView(view);

		editProces = (LinearLayout) view.findViewById(R.id.changeLayout);
		delelteProcess = (LinearLayout) view.findViewById(R.id.deleteLayout);

		GenericAction.trackerSetup("Edit Or Delete", "seller");
		
		/**
		 * changing user details functionality
		 */

		editProces.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					editProces.setBackgroundColor(Color.GRAY);
					delelteProcess.setBackgroundColor(Color.rgb(224, 235, 202));

					Bundle getArgument = getArguments();
					Bundle bundle = new Bundle();
					String priceStr = getArgument.getString("price");
					if (priceStr.endsWith("/"))
						priceStr = priceStr.substring(0, priceStr.length() - 1);
					bundle.putString("refId", getArgument.getString("refId"));
					bundle.putString("commodityName",
							getArgument.getString("commodityName"));
					bundle.putString("units", getArgument.getString("units"));
					bundle.putString("category",
							getArgument.getString("category"));
					bundle.putString("lastUpdatedDate",
							getArgument.getString("lastUpdatedDate"));
					bundle.putString("price", priceStr);

					Intent intent = new Intent(getActivity(),
							AddCommodityActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);

					dismiss();

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		/**
		 * Deleting user functionality
		 */
		delelteProcess.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editProces.setBackgroundColor(Color.rgb(224, 235, 202));
				delelteProcess.setBackgroundColor(Color.GRAY);

				webServiceTaskForDelete();

			}
		});
		Dialog dialognew = builder.create();
		return dialognew;

	}

	/**
	 * Method for deleting the commodity
	 */
	private void webServiceTaskForDelete() {
		XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();

		Bundle getArgument = getArguments();
		String vendorId = getArgument.getString("refId");
		String requestArg = null;
		createRequest.setRequestXMLdata(requestArg);

		createRequest.setUrl(getString(R.string.delete_commodity));
		String params = "";
		AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
				getActivity(), createRequest);
		task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

			@Override
			public void onTaskComplete(XMLRequestAndResponseData result) {

				try {
					// Parse the response XML string to know the status

					XmlParser mXmlParser = new XmlParser();
					Document doc = mXmlParser.getDomElement(result.getResult());
					NodeList nodelist = doc
							.getElementsByTagName("responseDeletePrice");
					Element element = (Element) nodelist.item(0);
					String status = mXmlParser.getValue(element, "status");
					if (status.equalsIgnoreCase("success")) {

						resultMessage("Commodity detail has been deleted successfully");

						Intent intent = new Intent(getActivity(),
								CommodityDetailsActivity.class);
						startActivity(intent);
						dismiss();
					}

				} catch (Exception e) {

					resultMessage("Delete failed please try again later.");
					dismiss();
					e.printStackTrace();
				}

			}
		});
		task.execute(params);
	}

	public void resultMessage(String messageText) {

		Toast toast = Toast.makeText(getActivity(), messageText,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}