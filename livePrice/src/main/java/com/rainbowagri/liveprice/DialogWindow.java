/**
 * Rainbow Sell
 * Copyright (C) 2014, Greeno Tech Solutions Pvt. Ltd.
 * * @author SELLATHURAI & SARAVANAN
 * @version 1.0, Rel 2
 */
package com.rainbowagri.liveprice;
 

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DialogWindow extends Dialog implements
		android.view.View.OnClickListener {

	Button noButton, yesButton;
	TextView alertMessage;
	String fromPage = "";

	public DialogWindow(Context context, String fromPagePrevious) {
		super(context);
		this.fromPage = fromPagePrevious;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_window);
		alertMessage = (TextView) findViewById(R.id.alertMessage);
		yesButton = (Button) findViewById(R.id.yesButton);
		noButton = (Button) findViewById(R.id.noButton);

		if (fromPagePrevious.equals("reset")) {
			alertMessage.setText("Are you sure to reset?");
			yesButton.setText("Yes");
			noButton.setText("No");
		}

	}

	@Override
	public void onClick(View v) {

		if (v == yesButton) {

		} else if (v == noButton) {
			dismiss();
		}

	}

}
