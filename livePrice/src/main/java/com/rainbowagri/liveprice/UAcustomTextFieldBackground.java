package com.rainbowagri.liveprice;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class UAcustomTextFieldBackground extends GradientDrawable {

	public UAcustomTextFieldBackground() {
		// TODO Auto-generated constructor stub
		setShape(RECTANGLE);
		setCornerRadius(2);
		setStroke(1, Color.parseColor("#25aae1"));
		setColor(Color.TRANSPARENT);
	}

	public UAcustomTextFieldBackground(Orientation orientation, int[] colors) {
		super(orientation, colors);
		// TODO Auto-generated constructor stub
	}

}
