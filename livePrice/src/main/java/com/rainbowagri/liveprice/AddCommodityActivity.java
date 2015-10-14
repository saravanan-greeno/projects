/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>,
 */

package com.rainbowagri.liveprice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.data.AddCommodityData;
import com.rainbowagri.data.BitmapManager;
import com.rainbowagri.data.GenericAction;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.model.CatagoryFactory;
import com.rainbowagri.profilepage.ProfilePageActivity;
import com.rainbowagri.servercommunicator.AsyncTaskCompleteListener;
import com.rainbowagri.servercommunicator.AsynchronousTaskToFetchData;
import com.rainbowagri.servercommunicator.XMLCreator;
import com.rainbowagri.servercommunicator.XMLRequestAndResponseData;
import com.rainbowagri.servercommunicator.XmlParser;


public class AddCommodityActivity extends Activity {

	LinearLayout categoryLayout, addCommodityLayout, nameLayout,
			priceAndUnitLayout, buttonLayout, priceLayout, unitLayout;
	ScrollView addCommodityLayoutScroll;
	TextView categoryText, layoutHeadingText, unitText, minimumquantity;
	EditText priceText, quantityText, discountText;// ,unitText;
	ImageView categoryDownArrow, commodityPicture;
	Button saveButton, resetButton, commInfoButtons;
	 CharSequence[] categoryItemsPassValue={};
	AutoCompleteTextView nameText;
	String categoryTextString = "", nameTextString = "", priceTextString = "",
			unitTextString = "", minimumQuantityStr = "",
			coordinatorUserId = "", lastUpdatedDate = "0000-00-00 00:00:00",
			mSDCardPath = "", quantityTextStr = "1", discountTextStr = "0",
			minimumQuantityValue, minimumQuantityUnit, commodityInfo = "",spinnerValue,spinnerValueTemp="",
	        maincategoryString="";
	private int m_intSpinnerInitiCount = 0;
	private static final int NO_OF_EVENTS = 1;
	List categories = new ArrayList();
	public static String mainCategories;

	long commodityRefId = 0, coordinatorRefId = 0;
	int minimumValue, availableQuantity;
	CatagoryFactory catagoryFactory;
	AddCommodityData addCommodityData = new AddCommodityData();
	boolean isInternetConnected = false, isTookPhoto = false;
	private final int CATEGORY_FLAG = 1;
	private final int UNIT_FLAG = 2;
	private final int RESET_FLAG = 3;
	private final int CAMERA_PIC_REQUEST = 4;
	private final int SELECT_FILE = 5;
	private final int REQUEST_CAMERA = 6;
	private OnUpdateListener listener;
	Spinner spinnervalue;
    ProfilePageActivity profileActivity;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_commodity);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.add_commodity);
		UploadCrashReport.startService(this);
*/
		
		GenericAction.trackerSetup("Add Commodity", "seller");
		
		addCommodityLayoutScroll = (ScrollView) findViewById(R.id.addCommodityLayoutScroll);
		addCommodityLayout = (LinearLayout) findViewById(R.id.addCommodityLayout);
		nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
		priceAndUnitLayout = (LinearLayout) findViewById(R.id.priceAndUnitLayout);
		categoryLayout = (LinearLayout) findViewById(R.id.categoryLayout);
		buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		spinnervalue = (Spinner) findViewById(R.id.spinnermaincategory);
		unitLayout = (LinearLayout) findViewById(R.id.unitLayout);
		categoryText = (TextView) findViewById(R.id.categoryText);
		layoutHeadingText = (TextView) findViewById(R.id.layoutHeadingText);
		nameText = (AutoCompleteTextView) findViewById(R.id.nameText);
		priceText = (EditText) findViewById(R.id.priceText);
		unitText = (TextView) findViewById(R.id.unitText);
		quantityText = (EditText) findViewById(R.id.quantityText);
		discountText = (EditText) findViewById(R.id.discountText);
		categoryDownArrow = (ImageView) findViewById(R.id.categoryDownArrow);
		commodityPicture = (ImageView) findViewById(R.id.commodityPicture);
		saveButton = (Button) findViewById(R.id.saveButton);
		resetButton = (Button) findViewById(R.id.resetButton);
		commInfoButtons = (Button) findViewById(R.id.info);
		catagoryFactory = new CatagoryFactory(this);
		spinnervalue.setFocusable(true);
		spinnervalue.setSelected(true);
		discountText.setRight(CAMERA_PIC_REQUEST);

		String name = "Category";
		String commodityName = "Commodity Name";
		String units = "Units";
		String price = "Price";
		String Availablqty = "Available qty";
		String colored = "*";

		SpannableStringBuilder categoryField = new SpannableStringBuilder();
		categoryField.append(name);
		int start = categoryField.length();
		categoryField.append(colored);
		int end = categoryField.length();

		SpannableStringBuilder commodityNameField = new SpannableStringBuilder();
		commodityNameField.append(commodityName);
		int start1 = commodityNameField.length();
		commodityNameField.append(colored);
		int end1 = commodityNameField.length();

		SpannableStringBuilder unitsField = new SpannableStringBuilder();
		unitsField.append(units);
		int start2 = unitsField.length();
		unitsField.append(colored);
		int end2 = unitsField.length();

		SpannableStringBuilder priceField = new SpannableStringBuilder();
		priceField.append(price);
		int start3 = priceField.length();
		priceField.append(colored);
		int end3 = priceField.length();
		
		final SpannableStringBuilder availableQty = new SpannableStringBuilder();
		availableQty.append(Availablqty);
		int start6 = availableQty.length();
		availableQty.append(colored);
		int end6 = availableQty.length();

		categoryField.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		categoryText.setHint(categoryField);

		commodityNameField.setSpan(new ForegroundColorSpan(Color.RED), start1,
				end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		nameText.setHint(commodityNameField);

		unitsField.setSpan(new ForegroundColorSpan(Color.RED), start2, end2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		unitText.setHint(unitsField);

		priceField.setSpan(new ForegroundColorSpan(Color.RED), start3, end3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		priceText.setHint(priceField);
		
		availableQty.setSpan(new ForegroundColorSpan(Color.RED), start6, end6,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		quantityText.setHint(availableQty);
		 
		setTypeface();
		createFolderInSDCard();
		/*
		 * Set Width at Runtime
		 */
		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();

		addCommodityLayout.getLayoutParams().width = screenWidth - 30;
		nameLayout.getLayoutParams().width = screenWidth - 80;
		priceAndUnitLayout.getLayoutParams().width = screenWidth - 80;
		categoryLayout.getLayoutParams().width = screenWidth - 80;
		buttonLayout.getLayoutParams().width = screenWidth - 80;

		nameText.setWidth(screenWidth - 80);
		categoryText.setWidth((screenWidth - 80) - 70);

		SharedPreferences mPreferences = this.getSharedPreferences(
				getString(R.string.shared_references_name), MODE_PRIVATE);
		coordinatorUserId = mPreferences.getString(
				getString(R.string.mobile_number), null);
		coordinatorRefId = mPreferences.getInt(
				getString(R.string.coordinator_ref_id), 0);
		 Bundle bundle = getIntent().getExtras();
	//	 mainCategories = CommodityDetailsActivity.mainCategory;
		 profileActivity=new ProfilePageActivity();
		 
		 if(bundle!=null)
		 {
			  mPreferences = this.getSharedPreferences("RainbowAgriLivePrice",
						MODE_PRIVATE);
				coordinatorRefId = mPreferences.getInt("coordinatorRefId", 0);
				mainCategories = mPreferences.getString("mainCategoryAllvalues", ""); 
			
		 }
		 else
		 { 
			 mPreferences = this.getSharedPreferences("RainbowAgriLivePrice",
						MODE_PRIVATE);
				coordinatorRefId = mPreferences.getInt("coordinatorRefId", 0);
				mainCategories = mPreferences.getString("mainCategory", "");
			 
		 }
		
		 
		 String[] temp;
		 String delimiter = "//";
		 temp = mainCategories.split(delimiter);
		 for (int i = 0; i < temp.length; i++) {
		 //System.out.println("the splitted +++="+temp[i]);
			 categories.add(temp[i]);
		 }
		 if(categories.get(0).toString().equals(""))
			 categories.remove(0);
		    ArrayAdapter dataAdapter = new ArrayAdapter (this, android.R.layout.simple_spinner_item, categories);
		   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	       spinnervalue.setAdapter(dataAdapter);
		 
		 if (bundle != null) {
			  
			layoutHeadingText.setText("Edit Commodity");
			commodityRefId = Integer.parseInt(bundle.getString("refId"));
			categoryTextString = bundle.getString("category");
			nameTextString = bundle.getString("commodityName");
			 priceTextString = bundle.getString("price");
			unitTextString = bundle.getString("units");
			maincategoryString = bundle.getString("mainCategory");
			lastUpdatedDate = bundle.getString("lastUpdatedDate");
			minimumQuantityStr = bundle.getString("minQty");
			quantityTextStr = bundle.getString("quantity");
			discountTextStr = bundle.getString("discount");
			commodityInfo = bundle.getString("commInfo");
			nameText.setText(nameTextString);
			priceTextString = priceTextString.replace(".00", "");
			priceText.setText(priceTextString);

			unitText.setText(unitTextString);
			categoryText.setText(categoryTextString);
		 
            spinnervalue.setSelection(getIndex(spinnervalue, maincategoryString));
            
        	if(quantityTextStr.equals("") ||quantityTextStr.equals(" ") )
    			quantityText.setHint(availableQty);
        	else
        		quantityText.setText(quantityTextStr);
			if (discountTextStr.equalsIgnoreCase("No discount"))
				discountText.setText("");
			else
				discountText.setText(discountTextStr);

			saveButton.setText("Save");
			File outFile = new File(mSDCardPath + "/" + "temp.jpg");
			if (outFile.exists()) {

				Drawable d = Drawable.createFromPath(mSDCardPath + "/"
						+ "temp.jpg");
				commodityPicture.setImageDrawable(d);

				Bitmap bitmap = BitmapFactory.decodeFile(outFile
						.getAbsolutePath());
				bitmap = getResizedBitmap(bitmap, 100, 100, 0);
				commodityPicture.setImageBitmap(bitmap);
			} else
				System.out.println("== File not exists");
		}
		
		else
		
		{
			File tempFile = new File(mSDCardPath, "temp.jpg");
			if (tempFile.exists())
				tempFile.delete();
		}

		
		
		 
		
		
		spinnervalue.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			 
				return false;
			}
		});

		spinnervalue.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				 
				  if (m_intSpinnerInitiCount < NO_OF_EVENTS) {
                      m_intSpinnerInitiCount++;
                      spinnerValue = adapterView.getItemAtPosition(
  							position).toString();
                      spinnerValueTemp=spinnerValue;
                   
                  } else {    
                	   
                	  spinnerValue = adapterView.getItemAtPosition(
  							position).toString();
                	  categoryText.setText("");
                	 
  				    }   
				  }

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		priceText.setRawInputType(Configuration.KEYBOARD_12KEY);
		priceText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.length() > 0 && str.startsWith(" ")) {

					priceText.setText("");
				} else if (str.length() > 0 && str.startsWith(".")) {

					priceText.setText("");
				} else if (str.length() > 0 && str.startsWith("0")) {

					priceText.setText("");
				}

			}
		});
		categoryLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(spinnervalue.equals(""))
				{
					Toast.makeText(AddCommodityActivity.this,
							"Please choose Main Category", Toast.LENGTH_LONG).show();
				}
				else
				{
					Bundle bundle = new Bundle();
	                bundle.putString("mainCategory", spinnerValue.trim());
					Intent moveToNextScreen = new Intent(AddCommodityActivity.this,
							CategoryActivity.class);
					moveToNextScreen.putExtras(bundle);
					AddCommodityActivity.this.startActivityForResult(
							moveToNextScreen, CATEGORY_FLAG);
					unitText.setText("");
				}
			 }
		});

		commodityPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createFolderInSDCard();
				if (Build.MODEL.toLowerCase().contains("spice")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(mSDCardPath + "/temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else
					selectImage();

			}
		});

		quantityText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.length() > 0 && str.startsWith(" ")) {

					quantityText.setText("");
					quantityText.setHint(availableQty);
				} else if (str.length() > 0 && str.startsWith(".")) {

					quantityText.setText("");
				} else if (str.length() > 0 && str.startsWith("0")) {

					quantityText.setText("");
					quantityText.setHint(availableQty);
				}

			}
		});

		discountText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.length() > 0 && str.startsWith(" ")) {

					discountText.setText("");
				} else if (str.length() > 0 && str.startsWith("0")) {

					discountText.setText("");
				} else if (str.length() > 0 && str.startsWith(".")) {

					discountText.setText("");
				}

			}
		});

		commInfoButtons.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commodityInfoDialog();

			}
		});
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				categoryTextString = categoryText.getText().toString();
				nameTextString = nameText.getText().toString();
				priceTextString = priceText.getText().toString();
				unitTextString = unitText.getText().toString();
				quantityTextStr = quantityText.getText().toString();
				discountTextStr = discountText.getText().toString();
				
				double discountRate = 0.0;
				try {
					discountRate = Double.parseDouble(discountTextStr);
				} catch (Exception e) {
					// TODO: handle exception
				}

				if (nameTextString.equals("")) {
					nameText.setError("Commodity name is required.");
				} else if (categoryTextString.equals("")) {
					nameText.setError(null);
					categoryText.setError("Catagory is required.");
				} else if (priceTextString.equals("")
						|| priceTextString.equals("0.00")
						|| priceTextString.equals("0")) {
					nameText.setError(null);
					categoryText.setError(null);
					priceText.setError("Price is required.");
				}
                else if (unitTextString.equals("")) {
					nameText.setError(null);
					categoryText.setError(null);
					priceText.setError(null);
					unitText.setError("Unit is required.");
				}

				else if (quantityTextStr.equals("")) {
					nameText.setError(null);
					categoryText.setError(null);
					priceText.setError(null);
					unitText.setError(null);
                    quantityText.setError("Available quantity is required.");
				}

				else if (discountRate >= 100 || discountRate >= 100.00) {
					nameText.setError(null);
					categoryText.setError(null);
					priceText.setError(null);
					unitText.setError(null);
					quantityText.setError(null);
					discountText.setError("Discount should not be exceed 99");
				}

				else {
					// Web Service to save
					nameText.setError(null);
					categoryText.setError(null);
					priceText.setError(null);
					unitText.setError(null);
					quantityText.setError(null);
					discountText.setError(null);
					createFolderInSDCard();
					quantityTextStr = (quantityTextStr.equals("")) ? " "
							: quantityTextStr;

					if (unitTextString.equalsIgnoreCase("Bag")
							|| unitTextString.equalsIgnoreCase("Dozen")
							|| unitTextString.equalsIgnoreCase("Roll")
							|| unitTextString.equalsIgnoreCase("Bundle")
							|| unitTextString.equalsIgnoreCase("Kg")
							|| unitTextString.equalsIgnoreCase("Box")
							|| unitTextString.equalsIgnoreCase("Packet")
							|| unitTextString.equalsIgnoreCase("Piece")
							|| unitTextString.equalsIgnoreCase("Quintal")
							|| unitTextString.equalsIgnoreCase("Ton")
							|| unitTextString.equalsIgnoreCase("Bottle")
							|| unitTextString.equalsIgnoreCase("Litre")) {
						minimumQuantityValue = "1";
						minimumQuantityUnit = unitTextString;
					} else {
						minimumQuantityValue = unitTextString.substring(0,
								unitTextString.indexOf(" "));
						minimumQuantityUnit = unitTextString
								.substring(unitTextString.lastIndexOf(" ") + 1);

					}
					addCommodityData.setCategory(categoryTextString);
					addCommodityData.setCommodityName(nameTextString);
					addCommodityData.setPrice(priceTextString);
					addCommodityData.setUnits(minimumQuantityUnit);
					addCommodityData.setLastUpdatedDate(lastUpdatedDate);
					addCommodityData.setCommodityRefId(commodityRefId);
					addCommodityData.setCoordinatorRefId(coordinatorRefId);
					addCommodityData.setQuantity(quantityTextStr);
					addCommodityData.setDiscounts(discountTextStr);
					addCommodityData.setMinimumQuantity(minimumQuantityValue);
					addCommodityData.setMinQtyUnit(minimumQuantityUnit);
					addCommodityData.setMainCategory(spinnerValue);
					
					if (commodityInfo.equalsIgnoreCase("")) {
						addCommodityData.setCommodityInfo("none");
					} else {
						addCommodityData.setCommodityInfo(commodityInfo);
					}

					try {
						if (isTookPhoto) {
							uploadCommodityPicture();
							if (commodityRefId != 0) {
								BitmapManager.INSTANCE.clearImageCache();
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					saveCommodityDetails();

				}
			}
		});

		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				nameTextString = nameText.getText().toString();
				priceTextString = priceText.getText().toString();
				unitTextString = unitText.getText().toString();
				categoryTextString = categoryText.getText().toString();
				quantityTextStr = quantityText.getText().toString();
				discountTextStr = discountText.getText().toString();
				if (categoryTextString.equals("")
						&& nameTextString.equals("")
						&& (priceTextString.equals("0.00") || priceTextString
								.equals("")) && unitTextString.equals("")
						&& quantityTextStr.equals("")
						&& discountTextStr.equals(""))
					;
				else {

					final Dialog dialog = new Dialog(AddCommodityActivity.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog_window);
					// set values for custom dialog components - text, image and
					// button
					TextView alertMessage = (TextView) dialog
							.findViewById(R.id.alertMessage);
					Button yesButton = (Button) dialog
							.findViewById(R.id.yesButton);
					Button noButton = (Button) dialog
							.findViewById(R.id.noButton);
					Typeface face = Typeface.createFromAsset(getAssets(),
							"fonts/Sanchez-Regular_0.ttf");
					alertMessage.setTypeface(face);
					yesButton.setTypeface(face);
					noButton.setTypeface(face);
					alertMessage.setText("Are you sure to reset all values?");
					dialog.show();
					// if decline button is clicked, close the custom dialog
					yesButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Close dialog
							resetFields();
							dialog.dismiss();
						}
					});
					noButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Close dialog
							dialog.dismiss();
						}
					});
				}
			}
		});

		unitText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				categoryTextString = categoryText.getText().toString();
				if (categoryTextString.equals("")) {
					Toast.makeText(AddCommodityActivity.this,
							"Please choose Category", Toast.LENGTH_LONG).show();
				} else {

					Bundle bundle = new Bundle();
					bundle.putString("unit", categoryTextString);
					Intent moveToNextScreen = new Intent(
							AddCommodityActivity.this,
							ListViewPopupActivity.class);
					moveToNextScreen.putExtras(bundle);
					AddCommodityActivity.this.startActivityForResult(
							moveToNextScreen, UNIT_FLAG);
				}

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CATEGORY_FLAG) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				categoryTextString = bundle.getString("categoryName");
				categoryText.setText(categoryTextString);
			}
		} else if (requestCode == UNIT_FLAG) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				unitTextString = bundle.getString("simpleText");
				if (unitTextString.equalsIgnoreCase("Bag")
						|| unitTextString.equalsIgnoreCase("Dozen")
						|| unitTextString.equalsIgnoreCase("Roll")
						|| unitTextString.equalsIgnoreCase("Bundle")
						|| unitTextString.equalsIgnoreCase("Kg")
						|| unitTextString.equalsIgnoreCase("Box")
						|| unitTextString.equalsIgnoreCase("Packet")
						|| unitTextString.equalsIgnoreCase("Piece")
						|| unitTextString.equalsIgnoreCase("Quintal")
						|| unitTextString.equalsIgnoreCase("Ton")
						|| unitTextString.equalsIgnoreCase("Bottle")
						|| unitTextString.equalsIgnoreCase("Litre")) {
					minimumQuantityValue = "1";
					minimumQuantityUnit = unitTextString;
					unitText.setText(unitTextString);
				}

				else {

					minimumQuantityValue = unitTextString.substring(0,
							unitTextString.indexOf(" "));
					minimumQuantityUnit = unitTextString
							.substring(unitTextString.lastIndexOf(" ") + 1);
					unitText.setText(unitTextString);

				}

			}
		} else if (requestCode == RESET_FLAG) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					String isYesClicked = bundle.getString("isYesClicked");
					if (isYesClicked.equalsIgnoreCase("Yes"))
						resetFields();
					else
						;
				}

			}
		}

		else if (requestCode == SELECT_FILE && resultCode == RESULT_OK)

		{
			Uri selectedImageUri = data.getData();
			String tempPath = getPath(selectedImageUri,
					AddCommodityActivity.this);
			if (tempPath != null && new File(tempPath).exists()) {
				copyFile(new File(tempPath),
						new File(mSDCardPath + "/temp.jpg"));
				compressImage(mSDCardPath + "/temp.jpg");
				Bitmap thumbnail = decodeFile(new File(mSDCardPath
						+ "/temp.jpg"));
				if (thumbnail != null) {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

					Matrix matrix = new Matrix();
					matrix.postRotate(0);
					Bitmap bmp = Bitmap.createBitmap(thumbnail, 0, 0,
							thumbnail.getWidth(), thumbnail.getHeight(),
							matrix, true);
					commodityPicture.setImageBitmap(bmp);
					commodityPicture.setScaleType(ScaleType.FIT_XY);
					isTookPhoto = true;

				}
			}
		} else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			File file = new File(mSDCardPath + "/temp.jpg");
			compressImage(mSDCardPath + "/temp.jpg");

			int rotate = 0;
			try {
				File imageFile = new File(file.getAbsolutePath());
				ExifInterface exif = new ExifInterface(
						imageFile.getAbsolutePath());
				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);

			Bitmap thumbnail = decodeFile(file);
			if (thumbnail != null) {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

				Bitmap bmp = Bitmap.createBitmap(thumbnail, 0, 0,
						thumbnail.getWidth(), thumbnail.getHeight(), matrix,
						true);

				commodityPicture.setImageBitmap(bmp);
				commodityPicture.setScaleType(ScaleType.FIT_XY);
				isTookPhoto = true;

			}

		}
	}

	/**
	 * when reset button clicked
	 */
	public void resetFields() {
		nameText.setError(null);
		priceText.setError(null);
		unitText.setError(null);
		categoryText.setError(null);
		quantityText.setError(null);
		discountText.setError(null);
		categoryText.setText("");
		nameText.setText("");
		priceText.setText("");
		unitText.setText("");
		quantityText.setText("");
		discountText.setText("");
		commodityPicture.setImageResource(0);
		commodityPicture.setImageResource(R.drawable.uploadsan);
		saveButton.setText("Save");
		nameText.requestFocus();
		priceText.setHint("Price");
		lastUpdatedDate = "0000-00-00 00:00:00";
	}

	// @SuppressWarnings("deprecation")
	public void showAlertBox() {
		LinearLayout layout = new LinearLayout(this);
		LinearLayout layoutSub = new LinearLayout(this);

		TextView textViewHeading = new TextView(this);
		TextView textViewContent = new TextView(this);
		Button yesButton = new Button(this);
		Button noButton = new Button(this);

		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		//
		int alertBoxWidth = (screenWidth * 1) / 3;
		int alertBoxHeight = (screenHeight * 1) / 3;

		layout.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		layout.setOrientation(LinearLayout.VERTICAL);

		textViewHeading.setText(" ALERT ");
		textViewHeading.setPadding(5, 10, 5, 20);
		textViewHeading.setTextColor(Color.parseColor("#FFFFFF"));
		textViewHeading.setBackgroundColor(Color.parseColor("#007fff"));

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (alertBoxHeight * 1) / 4);// 50);
		params.gravity = Gravity.CENTER;

		textViewHeading.setLayoutParams(params);
		textViewHeading.setTextSize(16);
		textViewHeading.setGravity(Gravity.CENTER_VERTICAL);

		LinearLayout.LayoutParams paramsSub = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (alertBoxHeight * 2) / 4);// 100);
		paramsSub.gravity = Gravity.CENTER;
		textViewContent.setLayoutParams(paramsSub);
		textViewContent
				.setText("Looks like something went wrong, Sorry for the inconvenience. We will analyze the issue.");
		textViewContent.setPadding(5, 10, 5, 20);
		textViewContent.setTextColor(Color.parseColor("#000000"));
		textViewContent.setTextSize(14);
		textViewContent.setGravity(Gravity.CENTER);

		LinearLayout.LayoutParams paramsLayoutSub = new LinearLayout.LayoutParams(
				alertBoxWidth, (alertBoxHeight * 1) / 4);//
		paramsLayoutSub.gravity = Gravity.CENTER_HORIZONTAL;
		layoutSub.setLayoutParams(paramsLayoutSub);
		layoutSub.setOrientation(LinearLayout.HORIZONTAL);
		layoutSub.setBackgroundColor(Color.parseColor("#FFFFFF"));

		LinearLayout.LayoutParams paramsOkButtonCrash = new LinearLayout.LayoutParams(
				alertBoxWidth, alertBoxHeight);
		paramsOkButtonCrash.weight = 1.0f;
		paramsOkButtonCrash.gravity = Gravity.CENTER;
		paramsOkButtonCrash.bottomMargin = 5;
		paramsOkButtonCrash.rightMargin = 15;
		yesButton.setLayoutParams(paramsOkButtonCrash);
		yesButton.setText(" Yes ");
		yesButton.setBackgroundColor(Color.parseColor("#007FFF"));

		LinearLayout.LayoutParams paramsCancelButtonCrash = new LinearLayout.LayoutParams(
				70, 40);
		paramsCancelButtonCrash.weight = 1.0f;
		paramsCancelButtonCrash.gravity = Gravity.CENTER;
		paramsCancelButtonCrash.bottomMargin = 5;
		paramsCancelButtonCrash.rightMargin = 15;
		noButton.setLayoutParams(paramsCancelButtonCrash);
		noButton.setText(" Cancel ");
		noButton.setBackgroundColor(Color.parseColor("#007FFF"));

		layout.addView(textViewHeading);
		layout.addView(textViewContent);

		layoutSub.setOrientation(LinearLayout.HORIZONTAL);
		layoutSub.addView(yesButton);
		layoutSub.addView(noButton);
		layout.addView(layoutSub);

		setContentView(layout);
		/** If user clicks OK, Send details to Server */
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		/** If user clicks CANCEL, close the application */
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	/**
	 * Inform user instantly when the INTERNET connectivity losses.
	 */
	private BroadcastReceiver NetworkStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			isInternetConnected = networkInfo != null
					&& networkInfo.isConnectedOrConnecting();

			if (!isInternetConnected) {

				displayToastMessage("No internet connection");
			}
		}
	};

	/**
	 * When "Save" button clicked
	 */
	private void saveCommodityDetails() {

		if (checkInternetConnection()) {

			XMLCreator xmlCreator = new XMLCreator();
			String requestAddCommodity = xmlCreator
					.serializeAddCommodity(addCommodityData);
			System.out.println("the creting xml is---"+requestAddCommodity);
			XMLRequestAndResponseData createRequest = new XMLRequestAndResponseData();
			createRequest.setRequestXMLdata(requestAddCommodity);
			createRequest.setTitle("AddCommodity");
			createRequest.setUrl(getString(R.string.add_commodity));
			String params = "";
			AsynchronousTaskToFetchData task = new AsynchronousTaskToFetchData(
					this, createRequest);
			task.setFetchMyData(new AsyncTaskCompleteListener<XMLRequestAndResponseData>() {

				@Override
				public void onTaskComplete(XMLRequestAndResponseData result) {

					try {

						XmlParser mXmlParser = new XmlParser();
						Document doc = mXmlParser.getDomElement(result
								.getResult());
						NodeList nodelist = doc
								.getElementsByTagName("responseSavePriceDetails");
						Element element = (Element) nodelist.item(0);
						String status = mXmlParser.getValue(element, "status");

						// For valid vendor; Move to next screen
						if (status.equalsIgnoreCase("Exists")) {
							displayToastMessage("Commodity already Exists");

						} else if (status.equalsIgnoreCase("success")) {
							GenericAction.loadingMore = false;
							String totalCount = mXmlParser.getValue(element,
									"totalCount");
							CommodityDetailsActivity.totalCount = totalCount;
							String uniqueId = mXmlParser
									.getValue(element, "id");
							String name = mXmlParser.getValue(element, "Name");
							String category = mXmlParser.getValue(element,
									"category");
							String commInfo = mXmlParser.getValue(element,
									"description");

							try {
								name = GenericAction.convertHexaToString(name);
								category = GenericAction
										.convertHexaToString(category);
								commInfo = GenericAction
										.convertHexaToString(commInfo);

							} catch (Exception e) {
								// TODO: handle exception
							}
							String date = mXmlParser.getValue(element, "Date");

							if (date.length() > 19) {
								date = date.substring(0, date.length() - 2);

							}

							String price = mXmlParser
									.getValue(element, "Price");
							date = dateFormat(date);
							String QuantityandUnits = mXmlParser.getValue(
									element, "unit");

							String discountInPercentage = mXmlParser.getValue(
									element, "discount");

							String discountPrice = mXmlParser.getValue(element,
									"discountPrice");
							String quantity = mXmlParser.getValue(element,
									"quantity");
							String minQty = mXmlParser.getValue(element,
									"minQty");
							QuantityandUnits = minQty + " " + QuantityandUnits;

							String unitTag = mXmlParser.getValue(element,
									"minUnit");

							String imageURL = mXmlParser.getValue(element,
									"commodityImageUrl");

							String statusPrice = mXmlParser.getValue(element,
									"priceStatus");
							
							String mainCategory = mXmlParser.getValue(element,
									"mainCategory");

							RowItem dataItems = new RowItem();
							dataItems.setCommodityName(name);
							dataItems.setStatusPrice(statusPrice);
							dataItems.setVarietyName(category);
							dataItems.setDate(date);
							dataItems.setPrice(price);
							dataItems.setUnit(QuantityandUnits);
							dataItems.setId(uniqueId);
							dataItems
									.setDiscountInPercentage(discountInPercentage);
							dataItems.setDiscountPrice(discountPrice + "/");
							dataItems.setQuantity(quantity);
							dataItems.setMinimumQuantity(minQty);
							dataItems.setUnitTag(unitTag);
							dataItems.setImageURL(imageURL.replace(" ", "%20"));
							dataItems.setCommodityInfo(commInfo);
							dataItems.setMainCategory(mainCategory); 

							if (commodityRefId == 0) {

								GenericAction.saveData = dataItems;
								CommodityDetailsActivity.IsObjectEdited = "Save";
								displayToastMessage("Commodity details has been saved successfully.");
								
								
								Product product = new Product()
							    .setName("New category")
							    .setPrice(40.00);

							ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
							    .setTransactionId(uniqueId);

							// Add the transaction data to the event.
							HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
							    .setCategory("In-My Store")
							    .setAction("Purchase")
							    .addProduct(product)
							    .setProductAction(productAction);

							// Send the transaction data with the event.
							MyApplications.tracker().send(builder.build());
								
								
								
							} else {

								GenericAction.editData = dataItems;
								CommodityDetailsActivity.IsObjectEdited = "Edit";
								displayToastMessage("Commodity details has been updated successfully.");
							}
							resetFields();

							Intent intent = new Intent();
							setResult(RESULT_OK, intent);
							hideSoftKeyboard();
							AddCommodityActivity.this.finish();

						}
						// For Invalid vendor; display message
						else {

							displayToastMessage(getString(R.string.response_failure));
						}

					} catch (Exception e) {
						if (checkInternetConnection()) {

							displayToastMessage(getString(R.string.session_expired));
						} else {

							displayToastMessage("No internet connection");
						}

					}
				}
			});
			task.execute(params);
		} else {

			displayToastMessage("No internet connection");
		}
	}

	/**
	 * Method for internet connection check up
	 */
	public boolean checkInternetConnection() {
		boolean connected = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED
				|| connectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			connected = true;
		} else
			connected = false;
		return connected;
	}

	/**
	 * common method for showing toast message
	 */
	public void displayToastMessage(String msg) {
		Toast toast = Toast.makeText(AddCommodityActivity.this, msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void openFolder() {

	}

	/**
	 * create folder in sd card for storing images in it
	 */
	public void createFolderInSDCard() {
		File file0 = new File(Environment.getExternalStorageDirectory()
				+ "/Rainbow");

		File file1 = new File(Environment.getExternalStorageDirectory()
				+ "/Rainbow/LivePrice");

		if (!file0.exists()) {
			if (file0.mkdir()) {

			}
		}

		if (!file1.exists()) {
			if (file1.mkdir()) {

			}
		}

		mSDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Rainbow/LivePrice/";
	}

	/**
	 * Upload the commodity picture to server
	 */
	public void uploadCommodityPicture() throws Exception {
		
		System.out.println("the image laoding");
		final File tempFile = new File(mSDCardPath, "temp.jpg");
		if (tempFile.exists()) {
			if (checkInternetConnection()) {

				Thread background = new Thread(new Runnable() {

					public void run() {
						try {
							File commodityFile = new File(mSDCardPath,
									nameTextString + "_" + coordinatorRefId
											+ ".jpg");
							tempFile.renameTo(commodityFile);
							executeMultiPartImageRequest(commodityFile);
						} catch (Throwable t) {
							Log.i("Animation", "Thread  exception " + t);
						}
					}

					private void threadMsg(String msg) {

						if (!msg.equals(null) && !msg.equals("")) {
							Message msgObj = handler.obtainMessage();
							Bundle b = new Bundle();
							b.putString("message", msg);
							msgObj.setData(b);
							handler.sendMessage(msgObj);
						}
					}

					private final Handler handler = new Handler() {
						public void handleMessage(Message msg) {
						}
					};

				});
				// Start Thread
				background.start();

			}
		}

	}

	/**
	 * setting http connection
	 */
	public String executeMultiPartImageRequest(File file) throws Exception {
		String strResponse = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(
				getString(R.string.save_commodity_picture));

		try {
			String imageName = GenericAction
					.convertStringToHexadecimal(nameTextString);
			MultipartEntity multiPartEntity = new MultipartEntity();
			multiPartEntity.addPart("coordinatorRefId", new StringBody(""
					+ coordinatorRefId));
			multiPartEntity.addPart("commodityName", new StringBody(imageName));
			multiPartEntity.addPart("commodityPictureImage", new StringBody(
					file.getName()));
			FileBody fileBody = new FileBody(file, "application/octect-stream");
			multiPartEntity.addPart("attachment", fileBody);

			// Set to request body
			postRequest.setEntity(multiPartEntity);
			// Send request
			HttpResponse response = client.execute(postRequest);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			if (response != null) {
				try {
					strResponse = responseHandler.handleResponse(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.e("WCFTEST", "WCFTEST ********** Responseesd" + strResponse);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strResponse;
	}

	/**
	 * decode the file
	 */
	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/**
	 * Set Type face
	 */
	public void setTypeface() {
		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/Sanchez-Regular_0.ttf");
		layoutHeadingText.setTypeface(face);
		nameText.setTypeface(face);
		priceText.setTypeface(face);
		unitText.setTypeface(face);
		categoryText.setTypeface(face);
		quantityText.setTypeface(face);
		discountText.setTypeface(face);
		saveButton.setTypeface(face);
		resetButton.setTypeface(face);
	}

	/**
	 * Set the compressed bitmap image size
	 */
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth,
			int rotate) {

		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.preRotate(rotate);
		matrix.preScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;
	}

	/**
	 * get the discount rate
	 */
	public double getDiscounRate() {
		double discountRate = 0.0;
		if (!discountTextStr.equals("")) {
			if (discountTextStr.indexOf(" ") != -1) {
				discountTextStr = discountTextStr.substring(0,
						discountTextStr.indexOf(" "));
			}
			if (discountTextStr.indexOf("%") != -1) {
				if (discountTextStr.indexOf("%") != 0) {
					discountTextStr = discountTextStr.substring(0,
							discountTextStr.indexOf("%"));

				} else {
					discountTextStr = discountTextStr.substring(0,
							discountTextStr.indexOf("%") + 1);
				}
			}
			if (!discountTextStr.equals(""))
				discountRate = Double.parseDouble(discountTextStr);
		}
		return discountRate;
	}

	/**
	 * compress the image from original image size
	 */
	public void compressImage() {
		int MAX_IMAGE_SIZE = 70 * 1024;
		Bitmap bmpPic = BitmapFactory.decodeFile(mSDCardPath + "temp.jpg");
		if (bmpPic != null) {
			if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
				BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
				bmpOptions.inSampleSize = 1;
				while ((bmpPic.getWidth() >= 1024)
						&& (bmpPic.getHeight() >= 1024)) {
					bmpOptions.inSampleSize++;
					bmpPic = BitmapFactory.decodeFile(mSDCardPath + "temp.jpg",
							bmpOptions);
				}
				Log.d("compressImage", "Resize: " + bmpOptions.inSampleSize);
			}

			int compressQuality = 104;
			int streamLength = MAX_IMAGE_SIZE;
			while (streamLength >= MAX_IMAGE_SIZE) {
				ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
				compressQuality -= 5;
				Log.d("compressImage", "Quality: " + compressQuality);
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality,
						bmpStream);
				byte[] bmpPicByteArray = bmpStream.toByteArray();
				streamLength = bmpPicByteArray.length;
				Log.d("compressImage", "Size: " + streamLength);
			}
			try {
				FileOutputStream bmpFile = new FileOutputStream(mSDCardPath
						+ "temp.jpg");
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality,
						bmpFile);
				bmpFile.flush();
				bmpFile.close();
			} catch (Exception e) {
				Log.e("compressImage", "Error on saving file");
			}
		}

	}

	/**
	 * get the minimum value for sorting process
	 */
	public int getMinimumValue() {
		if (!minimumQuantityStr.equals("")) {
			minimumValue = Integer.parseInt(minimumQuantityStr);
		}

		return minimumValue;
	}

	/**
	 * get the quantity value for getting available quantity
	 */
	public int getQuandityValue() {
		if (!quantityTextStr.equals("")) {
			availableQuantity = Integer.parseInt(quantityTextStr);
		}
		return availableQuantity;
	}

	/**
	 * take photo for commodity image
	 */
	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Browse Photo" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddCommodityActivity.this);
		builder.setTitle(null);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(mSDCardPath + "/temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Browse Photo")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	/**
	 * copy image file from location
	 */
	public void copyFile(File sourceLocation, File targetLocation) {
		if (sourceLocation.exists()) {

			try {

				InputStream in;
				OutputStream out;

				in = new FileInputStream(sourceLocation);
				out = new FileOutputStream(targetLocation);
				byte[] buf = new byte[1024];
				int len;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
				out.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * compress the image from original image size
	 */
	public void compressImage(String path) {
		int MAX_IMAGE_SIZE = 50 * 1024;
		File f = new File(path);
		Bitmap bmpPic = null;
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		bmpOptions.inSampleSize = 1;
		try {
			if (f != null) {
				bmpPic = BitmapFactory.decodeFile(path, bmpOptions);
			}
		} catch (OutOfMemoryError e) {
			System.gc();
			try {
				if (f != null) {
					bmpPic = BitmapFactory.decodeFile(path, bmpOptions);

				}
			} catch (OutOfMemoryError e1) {

			}
		}
		if (bmpPic != null) {
			if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {

				while ((bmpPic.getWidth() >= 1024)
						&& (bmpPic.getHeight() >= 1024)) {
					bmpOptions.inSampleSize++;
					bmpPic = BitmapFactory.decodeFile(path, bmpOptions);
				}
			}
			int compressQuality = 104;
			int streamLength = MAX_IMAGE_SIZE;
			while (streamLength >= MAX_IMAGE_SIZE) {
				ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
				compressQuality -= 5;
				Log.d("compressImage", "Quality: " + compressQuality);
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality,
						bmpStream);
				byte[] bmpPicByteArray = bmpStream.toByteArray();
				streamLength = bmpPicByteArray.length;
				Log.d("compressImage", "Size: " + streamLength);
			}

			try {
				FileOutputStream bmpFile = new FileOutputStream(path);
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality,
						bmpFile);
				bmpFile.flush();
				bmpFile.close();
			} catch (Exception e) {
				Log.e("compressImage", "Error on saving file");
			}
		}
	}

	/**
	 * get path for selecting data
	 */

	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Button for viewing commodity details info
	 */
	private void commodityInfoDialog() {
		final Dialog commInfoDialog = new Dialog(AddCommodityActivity.this);
		commInfoDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		commInfoDialog.setTitle("Information");
		commInfoDialog.setCanceledOnTouchOutside(false);

		commInfoDialog.setContentView(R.layout.commodity_info);
		EditText info = (EditText) commInfoDialog
				.findViewById(R.id.commodityInfo);
		Button infoButton = (Button) commInfoDialog
				.findViewById(R.id.comInfoButton);

		if (commodityInfo.equalsIgnoreCase("none")) {
			info.setText("");
		} else {
			info.setText(commodityInfo);
		}
		commodityInfo = info.getText().toString();
		info.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable editText) {
				commodityInfo = editText.toString();

			}
		});

		infoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				commInfoDialog.dismiss();

			}
		});

		commInfoDialog.show();
	}

	public interface OnUpdateListener {
		void saveCommodity(RowItem data);
        void editedCommodity(RowItem data);

	}

	public void setListener(OnUpdateListener listener) {
		this.listener = listener;
	}

	/**
	 * setting date format
	 */
	public String dateFormat(String date) throws ParseException {
		String strCurrentDate = date;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date newDate = format.parse(strCurrentDate);

		format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String datesFormats = format.format(newDate);
		return datesFormats;
	}

	/**
	 * hide the keypad
	 */

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
		}
	}
	
	/**
	 * get the selected index value in spinner
	 */
	 private int getIndex(Spinner spinner, String myString)
	 {
	  int index = 0;

	  for (int i=0;i<spinner.getCount();i++){
	   if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
	    index = i;
	    break;
	   }
	  }
	  return index;
	 } 
}
