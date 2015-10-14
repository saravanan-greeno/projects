package com.rainbowagri.profilepage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.transition.Visibility;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
  
//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
import com.rainbowagri.ServerCommuicator.AsyncTaskCompleteListener;
import com.rainbowagri.ServerCommuicator.Configuration;
import com.rainbowagri.ServerCommuicator.ConnectionDetector;
import com.rainbowagri.ServerCommuicator.GenericProcess;
import com.rainbowagri.ServerCommuicator.ServerCommunicator;
import com.rainbowagri.ServerCommuicator.TaskToFetchDetailsProfile;
import com.rainbowagri.ServerCommuicator.XMLCreatorProfile;
import com.rainbowagri.ServerCommuicator.XmlParserProfile;
import com.rainbowagri.data.ProfilePageData;
import com.rainbowagri.data.TaskToFetchData;
 
 
 

@SuppressLint("NewApi")
public class ProfilePageActivity extends Activity implements LocationListener{

	private double latituteField=0.0;
	private double longitudeField=0.0;
	ProgressDialog progressBar;
	 boolean connected = false;
	// String[] items ;
	private LocationManager locationManager;
	private String provider;
	ConnectionDetector connect;
	 ArrayAdapter<String> mAdapter;
	ArrayList mSelectedItems;
	CharSequence[] categoryItems={};
	public static CharSequence[] categoryItemsPass={};
	CharSequence[] checkedCategoryItems={};
    List<CharSequence> categoryList = new ArrayList<CharSequence>();
    List<CharSequence> selectedCategoryList = new ArrayList<CharSequence>();
    private AutoCompleteTextView choseLocation;
    
    CharSequence[] districtNames={"All","Chennai","Tiruvallur","Kanchipuram","Vellore","Tiruvannamalai","Villupuram","Krishnagiri",
    		"Dharmapuri","Salem","Perambalur","Ariyalur","Cuddalore","Nagapattinam","Tiruvarur","Thajavore","Trichy","Namakkal",
    		"Erode","karur","Pudukkottai","Sivaganga","Madurai","Ramanadhapuram","Viridhunagar","Dindigul","Tutukodi","Tirunelveli",
    		"Theni","Tiruppur","Coimbatore","Nilgiris","Kanniyakumari"};
   
    boolean[] checked_District = new boolean[33];
    boolean checked_state[]={}; 
	boolean categorychecked_state[]={}; 
	boolean isChecked= true;
	RelativeLayout rl;
	// The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	ScrollView sclist;
	private Context myContext;
	boolean isGPSEnabled = false, isNetworkEnabled = false, canGetLocation = false, hasGps=false, isTookPhoto=false, locationChangeSettingFlag =  false;
	Location location; 
	PackageManager pm;
	TaskToFetchDetailsProfile task;
	private AlertDialog myalertDialog=null;
	ScrollView topScrollLayout;
	EditText mobileNumber, firstName, lastName, dob, pincode,  description, shopName, addressEditText, city, district,minimumOrder;
	TextView headingTextAddProfile,chooseCategory;

	ImageView profilePicture;
	String display_checked_categories = "";
	String display_checked_districts = "",profileTypeStr,selectDistrict;
	Button saveButtonPro, locationButton,addButton;
	String mobileNumberStr = "", firstNameStr = "", lastNameStr = "", dobStr = "", pincodeStr = "",  descriptionStr = "", shopNameStr="";
	String appName = "",   addressStr = "",cityStr="",geoLocationStr="", districtStr;
	String coordinatorUniqueId = "0", profileId = "0", mSDCardPath = "",minimumOrderStr="",selectedCategoryStr="",id="",category="",chooseLocationStr="";
	JSONArray categories = null;
    
	File sourceFile, destinationFile;
	ProfilePageData profilePageData ;
	ApplicationInformation applicationInformation = new ApplicationInformation();
	ArrayList<HashMap<String, Object>>  profileViewList  = new ArrayList<HashMap<String, Object>>();
	ListView mListView;
	private PincodeAdapter mAdapter1;
	public int signUpFlag = 0;
	private final int CAMERA_PIC_REQUEST = 1;
	private final int SETTING_LOCATION_REQUEST = 2;
	private final int REQUEST_CAMERA = 3;
	private final int SELECT_FILE = 4;
	String[] items ;
	private RadioButton radioDeliveryButton,chosePrfileType;
	RadioGroup rg,profileType;
	RadioButton farmerRadio,sellerRadio,fpoRadio,districtRadio,pincodeRadio;
	List<String> itemValues = new ArrayList<String>();
	int totalselectedValues=0;
	int totalSelectedDistricts=0;
	GenericProcess gp;
	RelativeLayout listView;
	/* int selectedId = rg.getCheckedRadioButtonId();
	 radioDeliveryButton = (RadioButton)findViewById(selectedId); 
	 
	 if(radioDeliveryButton.getText().toString().equalsIgnoreCase("District"))*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		myContext = this;
		setContentView(R.layout.profile_page);
	/*	Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.profile_page);
		UploadCrashReport.startService(this);*/
		connect = new ConnectionDetector(getApplicationContext());
		mobileNumber = (EditText)findViewById(R.id.mobileNumber);
		firstName = (EditText)findViewById(R.id.firstName);
		sclist=(ScrollView)findViewById(R.id.scrollLayout);
		lastName = (EditText)findViewById(R.id.lastName);
		shopName = (EditText)findViewById(R.id.shopName);
		pincode = (EditText)findViewById(R.id.pincode);
		addressEditText = (EditText)findViewById(R.id.addressEditText);
		minimumOrder = (EditText)findViewById(R.id.minOrder);
		city = (EditText)findViewById(R.id.city);
		district = (EditText)findViewById(R.id.district);
		description = (EditText)findViewById(R.id.description);
		profilePicture = (ImageView)findViewById(R.id.profilePicture);
		headingTextAddProfile = (TextView)findViewById(R.id.headingTextAddProfile);
		chooseCategory = (TextView)findViewById(R.id.categories);
		progressBar = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
	    progressBar.setCancelable(true);
		saveButtonPro = (Button)findViewById(R.id.saveButtonPro);
		addButton = (Button)findViewById(R.id.AddPincode);
		choseLocation = (AutoCompleteTextView) findViewById(R.id.choselocation); 
		locationButton  =(Button)findViewById(R.id.locationButton);
	    rl= (RelativeLayout)findViewById(R.id.headingAddProfile);
	    listView= (RelativeLayout)findViewById(R.id.listLayout);
		profilePageData = new ProfilePageData();
		mListView = (ListView)findViewById(R.id.list);
		gp=new GenericProcess();
		    rg = (RadioGroup) findViewById(R.id.radioDelivery);
		    profileType = (RadioGroup) findViewById(R.id.radioProfile);
		   
		    farmerRadio=(RadioButton)findViewById(R.id.radioFarmer);
		    sellerRadio=(RadioButton)findViewById(R.id.radioSeller);
		    fpoRadio=(RadioButton)findViewById(R.id.radioFpo);
		    districtRadio=(RadioButton)findViewById(R.id.radioDistrict);
		    pincodeRadio=(RadioButton)findViewById(R.id.radioPincode);
		    
	//	setListViewHeightBasedOnChildren(mListView);
		/// Initial Setup
		try {
			createSDCardFolder();
//			mandatoryField();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	Typeface myfont = Typeface.createFromAsset(getAssets(),
		          "fonts/Gotham-Book_0.otf");

		mobileNumber.setTypeface(myfont);
		firstName.setTypeface(myfont);
		lastName.setTypeface(myfont);
		shopName.setTypeface(myfont);
		pincode.setTypeface(myfont);
		addressEditText.setTypeface(myfont);
		
		city.setTypeface(myfont);
		district.setTypeface(myfont);
		description.setTypeface(myfont);
		saveButtonPro.setTypeface(myfont);
		headingTextAddProfile.setTypeface(myfont);

		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			 
			System.out.println("bundle is threre");
			profileViewList = (ArrayList<HashMap<String, Object>>) bundle.getSerializable("profileViewList");
			if(profileViewList!=null)   // Update
			{
				if(profileViewList.size() !=0)
				{
					coordinatorUniqueId = ""+profileViewList.get(0).get("coordinatorUniqueId");
					appName = ""+profileViewList.get(0).get("appName");
					profileId = ""+profileViewList.get(0).get("profileId");
					mobileNumberStr = ""+profileViewList.get(0).get("mobileNumber");
					firstNameStr = ""+profileViewList.get(0).get("firstName");
					lastNameStr = ""+profileViewList.get(0).get("lastName");
					shopNameStr = ""+profileViewList.get(0).get("shopName");
					pincodeStr = ""+profileViewList.get(0).get("pincode"); 
					descriptionStr = ""+profileViewList.get(0).get("description");
					
					minimumOrderStr=""+profileViewList.get(0).get("minimumOrder");
					addressStr = ""+profileViewList.get(0).get("address");
					cityStr = ""+profileViewList.get(0).get("city");
					districtStr =  ""+profileViewList.get(0).get("district");
					geoLocationStr = ""+profileViewList.get(0).get("geoLocation");
					category = ""+profileViewList.get(0).get("categoryList");
					chooseLocationStr=""+profileViewList.get(0).get("deliveryLocation");
					profileTypeStr=""+profileViewList.get(0).get("profileType");
					selectDistrict=""+profileViewList.get(0).get("deliveryDistrict");
					//System.out.println("the getting distrcit value is---"+selectDistrict);
				//	System.out.println("the getting profileTypeStr value is---"+profileTypeStr);
					if(profileTypeStr.equalsIgnoreCase("farmer"))
					{
						farmerRadio.setChecked(true);
					}
					else if(profileTypeStr.equalsIgnoreCase("Retailer"))
					{
					 sellerRadio.setChecked(true);
					}
					else
					{
						fpoRadio.setChecked(true);
					}
					
					if(selectDistrict.equalsIgnoreCase("none"))
					{
					pincodeRadio.setChecked(true);
					addButton.setVisibility(View.VISIBLE);
					listView.setVisibility(View.VISIBLE);
				//	System.out.println("the gettig chooseLocationStr is"+chooseLocationStr);
				//	 System.out.println("the gettig category is"+category);
					 String splitValue = "//";
				    items= chooseLocationStr.split(splitValue);
				      for (int i = 0; i < items.length; i++) {
					   itemValues.add(items[i]);
					 }
					items = new String[itemValues.size()];
					items = itemValues.toArray(items);
					// System.out.println("the gettig value from activity is"+items.toString());
					 
					mAdapter1 = new PincodeAdapter(ProfilePageActivity.this,
							R.layout.pincode_textview, new ArrayList<String>(Arrays.asList(items)));
					mListView.setAdapter(mAdapter1);
					}
					else
					{   districtRadio.setChecked(true);
					    addButton.setVisibility(View.GONE);
					    listView.setVisibility(View.GONE);
						 String[] temp;
						 String delimiter = "//";
						  temp = selectDistrict.split(delimiter);
						  
						  if(selectDistrict.equalsIgnoreCase("All"))
						  {
							  for(int k=0;k<districtNames.length;k++){
							     
									checked_District[k]=true;
									totalSelectedDistricts++;
									//System.out.println("totalselectedValues++++"+totalSelectedDistricts);
								   
							 } 
							  choseLocation.setHint("All Districts");
						  }
						  else
						  {
					//	 System.out.println("the getting temp name is++++"+temp.length);
						// int value=0;
						 for (int i = 0; i < temp.length; i++) 
						 {
							   for(int j=0;j<districtNames.length;j++){
						    //	System.out.println("temp[i].toString() "+temp[i].toString());
						    	//System.out.println("districtNames[j].toString() "+districtNames[j].toString());
							if(temp[i].toString().equalsIgnoreCase(districtNames[j].toString()))
							{
							//	System.out.println("compare category"+temp.toString());
								checked_District[j]=true;
								totalSelectedDistricts++;
							//	System.out.println("totalselectedValues++++"+totalSelectedDistricts);
							  }
						 }
						}
						 choseLocation.setHint(totalSelectedDistricts+" District(s)");	
						  }
					}
					
		            if(geoLocationStr!= null && !geoLocationStr.equalsIgnoreCase("null") && !geoLocationStr.equals(""))
					{
						if(geoLocationStr.indexOf(":") != -1)
						{	
							latituteField = Double.parseDouble(geoLocationStr.substring(0, geoLocationStr.indexOf(":")));
							longitudeField = Double.parseDouble(geoLocationStr.substring(geoLocationStr.indexOf(":")+1));
						}	
					}
					
					File profilePictureFile = new  File(mSDCardPath+"/temp.jpg");

					if(profilePictureFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(profilePictureFile.getAbsolutePath());
					    if(myBitmap!=null)
					    	profilePicture.setImageBitmap(myBitmap);
                      }
					else
					{
						profilePicture.setImageResource(R.drawable.take_photo_profile);
					}
					
					mobileNumber.setKeyListener(null); 
					
					mobileNumber.setBackgroundResource(R.drawable.rounded_edittext_dim);
					mobileNumber.setText(mobileNumberStr);
					firstName.setText(firstNameStr);
					lastName.setText(lastNameStr);
					pincode.setText(pincodeStr);
					  try
					    {
						    String name = connect.convertHexaToString(shopNameStr);
						    String descriptionValue = connect.convertHexaToString(descriptionStr);
						    String addressText= connect.convertHexaToString(addressStr);
						    String cityValue= connect.convertHexaToString(cityStr);
						    String districtString= connect.convertHexaToString(districtStr);
						  
						    shopName.setText(name);
							description.setText(descriptionValue);
							addressEditText.setText(addressText);
							city.setText(cityValue);
							district.setText(districtString);
							minimumOrder.setText(minimumOrderStr);
							
						   /* shopName.setText(shopNameStr);
							description.setText(descriptionStr);
							addressEditText.setText(addressStr);
							city.setText(cityStr);
							district.setText(districtStr); */
					   
					    }
					    
					    catch (Exception e) {
							// TODO: handle exception
						}
					
		
				}
			}   // list is null
			else
			{
				deleteTempFile();
				appName   = bundle.getString("appName");
				
				// has to fix gps location issuses
				getLocation();
				if(appName.indexOf(":") == -1)    // $- Add Profile from existing application
				{
					mobileNumberStr = bundle.getString("mobileNumber");
					coordinatorUniqueId = bundle.getString("coordinatorUniqueId");	
					mobileNumber.setKeyListener(null); 
					mobileNumber.setBackgroundResource(R.drawable.rounded_edittext_dim);
					mobileNumber.setText(mobileNumberStr);
				}
				else     /// $- Sign up
				{
					if(appName.substring(appName.indexOf(":")+1) !=null )
					{
						signUpFlag = 1;
						// Enable Password field
					 }
					appName = appName.substring(0, appName.indexOf(":"));
				}
			
			}
			mandatoryField();	
			if(appName.equals("LivePrice"))
			{
				firstName.setVisibility(View.GONE);
				lastName.setVisibility(View.GONE);
				shopName.setVisibility(View.VISIBLE);
				//description.setHint("Return Policy");
				String name = "Return Policy";
				String colored = " *";   
				SpannableStringBuilder descriptionField = new SpannableStringBuilder();
				descriptionField.append(name);
				int start = descriptionField.length();
				descriptionField.append(colored);
				int end = descriptionField.length();
				
				descriptionField.setSpan(new ForegroundColorSpan(Color.rgb(153, 153, 153)), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				description.setHint(descriptionField);
				
				 if (connect.isConnectingToInternet()) {
					 GetCategoryDetails details = new GetCategoryDetails(this);
					 details.execute();
	               
	             } 
				 else {

	            	 connect.displayToast(getApplicationContext().getResources().getString(R.string.no_internet_msg));
	                 ProfilePageActivity.this.finish();
	             }
				 
				 
				 
				 
			}
			else //if(appName.equals("LivePrice"))
			{
				firstName.setVisibility(View.VISIBLE);
				lastName.setVisibility(View.VISIBLE);
				shopName.setVisibility(View.GONE);
			}
		
		}
		else
		{
		    System.out.println("nooo bundleeee");
			profileId = "0";
			coordinatorUniqueId = "0";
			deleteTempFile();
		}
		 String[] categoryItem=convertFileToArray();
		 
	//	 System.out.println("the categroiss are ==="+categoryItem.toString());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,categoryItem);
		choseLocation.setAdapter(adapter);  
		
		pm = this.getPackageManager();
		hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
		if(hasGps);
		else
			locationButton.setVisibility(View.GONE);
	
		
		locationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!locationChangeSettingFlag)
				{
					locationChangeSettingFlag = true;
					locationButton.setBackgroundResource(R.drawable.on);
					getLocation();
					try {
						setAddress();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					/* String descriptionValue = connect.convertHexaToString(descriptionStr);
				    String addressText= connect.convertHexaToString(addressStr);
				    String cityValue= connect.convertHexaToString(cityStr);
				    String districtString= connect.convertHexaToString(districtStr);*/
					locationChangeSettingFlag = false;
					locationButton.setBackgroundResource(R.drawable.off);
					if(!addressStr.isEmpty())
					{
					    String addressText= connect.convertHexaToString(addressStr);
					    String cityValue= connect.convertHexaToString(cityStr);
					    String districtString= connect.convertHexaToString(districtStr);
					addressEditText.setText(addressText);
			    	city.setText(cityValue);
			    	district.setText(districtString);
			    	pincode.setText(pincodeStr);
					}
					else
					{
						addressEditText.setText("");	
						city.setText("");
				    	district.setText("");
				    	pincode.setText("");
					}
				}

			}
		});
		
	 
	addButton.setOnClickListener(new OnClickListener() {
			boolean flag= false;
			@Override
			public void onClick(View arg0)
			{
				String pinValue=choseLocation.getText().toString();
			//	System.out.println("the pin value is----"+pinValue);
				
				
				if(pinValue.equalsIgnoreCase(""))
				{
					Toast.makeText(getApplicationContext(), "Enter Pincode", Toast.LENGTH_LONG).show();
				}
				else if(choseLocation.getText().toString().length() != 6) {
					 Toast.makeText(getApplicationContext(), "Enter Proper Pincode", Toast.LENGTH_LONG).show();
				}
				 else
				{
			 	if(itemValues.size()!=0)
			 {
			 		//System.out.println("itemValues.get(i).toString() values is----"+itemValues.size());
					//  System.out.println("the comparepinValue values is----"+pinValue);
			 		
			 		if(itemValues.contains(pinValue))
			 		{
			 			
			 			
			 			Toast.makeText(getApplicationContext(), "This pin number is already added", Toast.LENGTH_LONG).show();	
			 		}
			 		else
			 		{
			 		//	System.out.println("the else part values is----"+pinValue);
			 			itemValues.add(pinValue);
			 			
			 		}
			     }
			else
				{
				     itemValues.add(pinValue);
				}
			  }
				items = new String[itemValues.size()];
			//	System.out.println("the contains values is size----"+items.length);
				items = itemValues.toArray(items);
			//	System.out.println("the contains values after size----"+items.length);
			//	System.out.println("the items values in converted----"+items.toString());
				/*mAdapter = new ArrayAdapter<String>(getApplicationContext(),
						   android.R.layout.simple_list_item_1,
			                android.R.id.text1,
		                new ArrayList<String>(Arrays.asList(items)));
		        setListAdapter(mAdapter);*/
		      
				mAdapter1 = new PincodeAdapter(ProfilePageActivity.this,
						R.layout.pincode_textview, new ArrayList<String>(Arrays.asList(items)));
				mListView.setAdapter(mAdapter1);
				//System.out.println("the lsit size is----"+itemValues.size());
			//	System.out.println("the lsit values is----"+itemValues);
			}
		});
	
	 SwipeDismissListViewTouchListener touchListener =
             new SwipeDismissListViewTouchListener(
                     mListView,
                     new SwipeDismissListViewTouchListener.DismissCallbacks() {
                         @Override
                         public boolean canDismiss(int position) {
                             return true;
                         }

                         @Override
                         public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                             for (int position : reverseSortedPositions) {
                            	  String deletedItem = mAdapter1.getItem(position).toString();
                            	  
                            	  System.out.println("the deleted items are==="+deletedItem); 
                            		  itemValues.remove(deletedItem);
                            	      mAdapter1.remove(mAdapter1.getItem(position));
                            }
                             mAdapter1.notifyDataSetChanged();
                         }
                     });
	 
	 
	     mListView.setOnTouchListener(touchListener);
	     mListView.setOnScrollListener(touchListener.makeScrollListener());
	     choseLocation.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                	
                	 int selectedId = rg.getCheckedRadioButtonId();
                	 radioDeliveryButton = (RadioButton)findViewById(selectedId); 
                	 
                	 if(radioDeliveryButton.getText().toString().equalsIgnoreCase("District"))
                	 {
                		 choseLocation.setText("");
                		 AlertDialog.Builder builder1=new AlertDialog.Builder(ProfilePageActivity.this)
     					.setTitle("Choose District")
     					.setMultiChoiceItems(districtNames, checked_District, new DialogInterface.OnMultiChoiceClickListener() {
     					 
     					@Override
     					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
     						 checked_District[which]=isChecked;
     					    if(which==0)
     					    {
     					    	if(checked_District[which]==isChecked)
     					    	{
     					    		AlertDialog dialog1 = (AlertDialog) dialog;
     			                    ListView v = dialog1.getListView();
     			                    int i = 0;
     			                    while(i < districtNames.length) {
     			                        v.setItemChecked(i, isChecked);
     			                       checked_District[i]=isChecked;
     			                        i++;
     			                      
     			                    }}
     					    	 else
     					    	{}
     					    }
     					    else
     					    {
     					    	if(!checked_District[0]==isChecked)
     					    	{
     					    		 checked_District[0] = false;
     					            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
     					    	}
     					    	else
     					    	{}
     					      }
     					}
     					 })
     					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
     					 
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						 totalSelectedDistricts=0;
     						display_checked_districts="";
     						// display_checked_categories = "";
     						if(checked_District[0]==true)
     						{
     							display_checked_districts = "All";
     							 choseLocation.setHint("All Districts");
     						//	 System.out.println("display_checked_districts+display_checked_districts"+display_checked_districts);
     						}
     						else
     						{
     						//	display_checked_categories = "";
     					 for(int i=0;i<districtNames.length;i++){
     					 	if(checked_District[i]==true){
     						totalSelectedDistricts++;
     						display_checked_districts =display_checked_districts+"//"+districtNames[i];
     	           //      System.out.println("display_checked_districts values are--------"+display_checked_districts);
     					}
     					if(totalSelectedDistricts==33)
     					  choseLocation.setHint("All Districts");
     					else
     					 choseLocation.setHint(totalSelectedDistricts+" District(s)");
     				           } 
     						}
     				//	System.out.println("when ok clciekd+++"+totalselectedValues);
     				  //   Toast.makeText(getApplicationContext(), "The selected values is"+display_checked_categories, Toast.LENGTH_LONG).show();
     				  //   chooseCategory.setText(totalselectedValues+" categories");	
     				  }
     					});
     					  
     					AlertDialog alertDialog = builder1.create();
     					alertDialog.show();	 
                	 }
                	 else
                	 {
                		 choseLocation.setHint(""); 
                	 }
                	 
                }
                    
                return false;

            }
        });  
	    choseLocation.addTextChangedListener(new TextWatcher() {
			 boolean valid;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				  valid = false;
				  choseLocation.setError(null);	
			}
			 @Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			 @Override
			public void afterTextChanged(Editable s) {
				 String pin = s.toString();
	                if (choseLocation.getText().toString().length() == 6) {
	                    try {
	                     
	                        BufferedReader reader = new BufferedReader(
	                                new InputStreamReader(getResources().getAssets().open("pincode.txt")));
	                        String line;
	                        while ((line = reader.readLine()) != null) {
	                         
	                            if (line.equalsIgnoreCase(pin)) {
                                 valid = true;
	                                break;
	                            }
	                        }
                    } catch (Exception e) {
	                        e.getLocalizedMessage();
	                    }
	                    if (!valid) {
	                     	choseLocation.setError(getString(R.string.invalid_data));
	                    	choseLocation.requestFocus();
	                    }
	                }
			}
		});
		   rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	    @Override
	    	           public void onCheckedChanged(RadioGroup group, int checkedId) {
	    	                if(checkedId == R.id.radioDistrict) {
	    	                	addButton.setVisibility(View.GONE);
	    					    listView.setVisibility(View.GONE);
	    					    choseLocation.setText("");
	    					    choseLocation.setHint(totalSelectedDistricts+" District(s)");
                                Toast.makeText(getApplicationContext(), "chosen:District",
	    	                     Toast.LENGTH_SHORT).show();
	    	                 } else if(checkedId == R.id.radioPincode) {
	    	                	    addButton.setVisibility(View.VISIBLE);
	    						    listView.setVisibility(View.VISIBLE);
                                       Toast.makeText(getApplicationContext(), "chosen:Pincode",
	    	                              Toast.LENGTH_SHORT).show();
                                       choseLocation.setHint("");
	                              } else {
	    	                     /* Toast.makeText(getApplicationContext(), "choice: Vibration",
	    	                      Toast.LENGTH_SHORT).show();*/
	    	                       }
	    	                     }
	    	                 });

		  pincode.addTextChangedListener(new TextWatcher() {
	            boolean valid;

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	            }

	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	                valid = false;
	                pincode.setError(null);
	            }

	            @Override
	            public void afterTextChanged(Editable s) {

	                String pin = s.toString();
	                if (pincode.getText().toString().length() == 6) {
	                    try {
	                    	
	                    	//System.out.println("into tryy block");
	                        BufferedReader reader = new BufferedReader(
	                                new InputStreamReader(getResources().getAssets().open("pincode.txt")));
	                        String line;
	                        while ((line = reader.readLine()) != null) {
	                        	//System.out.println("into whilee block"+line);
	                        	
	                            if (line.equalsIgnoreCase(pin)) {
                                    valid = true;
	                                break;
	                            }
	                        }
                       } catch (Exception e) {
	                        e.getLocalizedMessage();
	                    }
	                    if (!valid) {
	                    	//System.out.println("invalid pinnn value");
	                        pincode.setError(getString(R.string.invalid_data));
	                        pincode.requestFocus();
	                    }
	                }

	            }
	        });
		 
     	saveButtonPro.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getUserEnteredValue();

				if(!(validationAllFields()))
				{
					if(profilePageData.getCoordinatorUniqueId().equalsIgnoreCase("0")){
						/**
						 * SignUp
						 */
						doSignup();
					}else{
						/**
						 * Update profile
						 */
					//	doSignup();
						saveProfileDetails();
					}
					
				}
				
			}
		});
		
		
		profilePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {


				if(Build.MODEL.toLowerCase().contains("spice"))
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(mSDCardPath+"/temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				}
				else
					selectImage();
				
//				selectImage();
			
			}
		});

		  chooseCategory.setOnClickListener(new OnClickListener() {
	    	  
				@Override
				public void onClick(View arg0) {
					 
					AlertDialog.Builder builder1=new AlertDialog.Builder(ProfilePageActivity.this)
					.setTitle("Choose a Category")
					.setMultiChoiceItems(categoryItems, checked_state, new DialogInterface.OnMultiChoiceClickListener() {
					 
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					
						
					checked_state[which]=isChecked;
					 
					  }
					})
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						totalselectedValues=0;
						display_checked_categories="";
					for(int i=0;i<categoryItems.length;i++){
					if(checked_state[i]==true){
						totalselectedValues++;
						display_checked_categories=display_checked_categories+"//"+categoryItems[i];
	                 
					}
				}
					
				//	System.out.println("when ok clciekd+++"+totalselectedValues);
				  //   Toast.makeText(getApplicationContext(), "The selected values is"+display_checked_categories, Toast.LENGTH_LONG).show();
				     chooseCategory.setText(totalselectedValues+" categories");	
				  }
					});
					  
					AlertDialog alertDialog = builder1.create();
					alertDialog.show();			 

				}
			});
	}
	  class GetCategoryDetails extends AsyncTask<String, Void, String> {
	        String reqParams;

	        public GetCategoryDetails(Context applicationContext) {
	        }

	        @Override
	        protected void onPreExecute() {
                System.out.println("getting herere");
	            super.onPreExecute();
	            progressBar.setMessage("Please Wait....");
	            progressBar.setIndeterminate(false);
	            progressBar.setCancelable(false);
	            progressBar.show(); 
	        }

	        @Override
	        protected String doInBackground(String... params) {
	            String response = null;

	            try {

	                ServerCommunicator serverCommunicator = new ServerCommunicator();
	              
	                String requestURL = Configuration.GET_CATEGORIES;
	                
	                System.out.println("the getting url is++"+requestURL);
	                try {
	                    // 3. build jsonObject
	                    JSONObject jsonObject = new JSONObject();
	                    jsonObject.accumulate("category", "All");

	                    // 4. convert JSONObject to JSON to String
	                    reqParams = jsonObject.toString();
	                    Log.d("Login request params:", reqParams);

	                } catch (JSONException e) {
	                    e.getLocalizedMessage();
	                }
	                response = serverCommunicator.postJSONData(reqParams, requestURL);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	            return response;
	        }

	        @Override
	        protected void onPostExecute(String response) {
	            super.onPostExecute(response);
	           progressBar.dismiss();
	            if (response != null) {
	                try {
	                	
	                	 System.out.println("responseresponse iss--"+response);
	                	processCategoryResponse(response);
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }
	            else
	            {
	            	displayToast("Please try later..");
	            }
	        }
	    }
	 
	 
	 
	 public void processCategoryResponse(String response) throws JSONException {
	       // StringRelatedStuff stringRelatedStuff = new StringRelatedStuff();
	        JSONObject jsonObject = new JSONObject(response);

	      //  fpoNameStr = stringRelatedStuff.convertHexaToString(jsonObject.get("name").toString());
	        
	      
	        categories = jsonObject.getJSONArray("category");
	        System.out.println("the size of the list is-----"+categories.length());
	        categoryList.clear();
	        for (int i = 0; i < categories.length(); i++) {
                 
	        	JSONObject c = categories.getJSONObject(i);
	           
                 String idValue = c.getString("id");
	             String categoryValue = c.getString("category");
	          
	             categoryList.add(categoryValue);
	           //  categoryItems = new CharSequence[categoryList.size()];
	             categoryItems = categoryList.toArray(new CharSequence[categoryList.size()]);
	             System.out.println("the size of the categories is-----"+categoryItems.length);
	        //     System.out.println("the categories are here-----"+categoryItems.toString());
	             
	        } 
 
	        categoryItemsPass = categoryItems;
            String mainCategoryalue="";
   		 for(int i=0;i<categoryItemsPass.length;i++){
   			 mainCategoryalue=mainCategoryalue+"//"+categoryItemsPass[i].toString();
   		  }
   		 SharedPreferences mypref = getApplication().getSharedPreferences("RainbowAgriLivePrice",
			MODE_PRIVATE);
		SharedPreferences.Editor prefsEditr = mypref.edit();
		prefsEditr.putString("mainCategoryAllvalues",mainCategoryalue);
		prefsEditr.commit(); 
            
            
            
	        
	        checked_state=new boolean[categoryItems.length];
	     
	        
	        
	        if(!category.isEmpty())
			{
	        	
	        	// System.out.println("the gtotalselectedValues++++"+totalselectedValues);
			 String[] temp;
			 String delimiter = "//";
			// System.out.println("the getting temp name is++++"+category);
			 temp = category.split(delimiter);
		//	 System.out.println("the getting temp name is++++"+temp.length);
			// int value=0;
			 for (int i = 0; i < temp.length; i++) 
			 {
				 
				// System.out.println("categoryItems.length "+categoryItems.length);
				 
			    for(int j=0;j<categoryItems.length;j++){
			    //	System.out.println("temp[i].toString() "+temp[i].toString());
			    //	System.out.println("categoryItems[j].toString() "+categoryItems[j].toString());
				if(temp[i].toString().equalsIgnoreCase(categoryItems[j].toString()))
				{
				//	System.out.println("compare category"+temp.toString());
					checked_state[j]=true;
					totalselectedValues++;
					//System.out.println("totalselectedValues++++"+totalselectedValues);
				  }
			 }
			}
			 chooseCategory.setText(totalselectedValues+" categories");	
		   }
	      } 
	 

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("Test Profile", "onStart was called");
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("Test Profile", "onResume was called");

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("Test Profile", "onPause was called");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("Test Profile", "onStop was called");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("Test Profile", "onRestart was called");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Test Profile", "onDestroy was called");
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
 
			if(requestCode == SETTING_LOCATION_REQUEST && resultCode == RESULT_OK)
			{
				isGPSEnabled = locationManager
			            .isProviderEnabled(LocationManager.GPS_PROVIDER);
						if(!applicationInformation.isInternetConnection(myContext))
						{
							displayToast("No internet Connection.");
						}
						else if(isGPSEnabled)
						{
							getLocation();
							try {
								setAddress();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							displayToast("GPS is not enabled.So could not able to get the address.");
							locationButton.setBackgroundResource(R.drawable.off);
							locationChangeSettingFlag = false;
						}

			}
			
			else if(requestCode == SELECT_FILE && resultCode == RESULT_OK)
				
			{	Uri selectedImageUri = data.getData();
					 
	                String tempPath = getPath(selectedImageUri, ProfilePageActivity.this);
	                
//	                File copyFromGalleryToTempFile  = new File(mSDCardPath+"/temp.jpg");
	                
	                if(tempPath != null && new File(tempPath).exists() )
	                {
	                    copyFile(new File(tempPath), new File(mSDCardPath+"/temp.jpg"));
//	                  new File(tempPath).renameTo(copyFromGalleryToTempFile);
		                  //System.out.println("=== tempPath =="+tempPath);
//		                  Bitmap bm;
//		                  BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
//		                  btmapOptions.inSampleSize = 1;
//		                  bm = BitmapFactory.decodeFile(mSDCardPath+"/temp.jpg", btmapOptions);
		                  compressImage(mSDCardPath+"/temp.jpg");
		  				Bitmap thumbnail = decodeFile(new File(mSDCardPath+"/temp.jpg"));
		  				if(thumbnail != null)
		  				{
		  					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		  					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		
		  					Matrix matrix = new Matrix();
		  			        matrix.postRotate(0);
		  					Bitmap bmp = Bitmap.createBitmap(thumbnail, 0, 0,
		  							thumbnail.getWidth(), thumbnail.getHeight(), matrix,
		  							true);
		
		  					profilePicture.setImageBitmap(bmp);
		  					profilePicture.setScaleType(ScaleType.FIT_XY);
		  					isTookPhoto = true;
		
		  				}
		                  
        }
 
			}
			else if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK)
			{
 
				
				File file = new File(mSDCardPath+"/temp.jpg");
				compressImage(mSDCardPath+"/temp.jpg");

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
				if(thumbnail != null)
				{
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

					Bitmap bmp = Bitmap.createBitmap(thumbnail, 0, 0,
							thumbnail.getWidth(), thumbnail.getHeight(), matrix,
							true);

					profilePicture.setImageBitmap(bmp);
					profilePicture.setScaleType(ScaleType.FIT_XY);
					isTookPhoto = true;

				}

			}

	}

	
	

	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 50;

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
	
	
	
	
	
	
	
	public void drawLayout()
	{
		Display display = getWindowManager().getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		int rowWidth = screenWidth;
		
		LinearLayout topHeadLinearLayout = new LinearLayout(this);
		LayoutParams topHeadLinearLayoutparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	
		topHeadLinearLayout.setLayoutParams(topHeadLinearLayoutparams);
		topHeadLinearLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout topSubLinearLayout = new LinearLayout(this);
		LayoutParams topSubLinearLayoutparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		topSubLinearLayout.setLayoutParams(topSubLinearLayoutparams);
		topSubLinearLayout.setOrientation(LinearLayout.VERTICAL);
		topSubLinearLayout.setBackgroundColor(0x22FF0000);


		mobileNumber = new EditText(this);
		firstName = new EditText(this);
		lastName = new EditText(this);
		dob = new EditText(this);
		pincode = new EditText(this);
		description = new EditText(this);
		profilePicture = new ImageView(this);
		saveButtonPro = new Button(this);
		
		LinearLayout nameImageSplitLayoutHead = new LinearLayout(this);
		LayoutParams nameImageSplitLayoutparams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		nameImageSplitLayoutHead.setLayoutParams(nameImageSplitLayoutparams2);
		nameImageSplitLayoutHead.setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		LinearLayout nameImageSplitLayout1 = new LinearLayout(this);
		LayoutParams nameImageSplitLayoutparams1 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		nameImageSplitLayout1.setLayoutParams(nameImageSplitLayoutparams1);
		nameImageSplitLayout1.setOrientation(LinearLayout.VERTICAL);
		nameImageSplitLayout1.setWeightSum(1);
		
		
	
		
		mobileNumber.setWidth(rowWidth-200);
		mobileNumber.setHeight(100);
		mobileNumber.setHint("Mobile Number *");

		firstName.setWidth(rowWidth-200);
		firstName.setHeight(100);
		firstName.setHint("First Name *");
		
		profilePicture.setLayoutParams(new LinearLayout.LayoutParams
				(200,200,1));
		profilePicture.setBackgroundResource(R.drawable.propic);
		
		lastName.setWidth(100);
		lastName.setHeight(100);
		lastName.setHint("Last Name ");
		
		dob.setWidth(100);
		dob.setHeight(100);
		dob.setHint("Date of Birth");
		
		pincode.setWidth(100);
		pincode.setHeight(100);
		pincode.setHint("Pincode");
		
		description.setWidth(100);
		description.setHeight(100);
		description.setHint("Description");
		
		saveButtonPro.setWidth(100);
		saveButtonPro.setHeight(100);
		saveButtonPro.setText("Save");
		saveButtonPro.setBackgroundColor(0x66FF0000);

		nameImageSplitLayout1.addView(mobileNumber);
		nameImageSplitLayout1.addView(firstName);
		nameImageSplitLayoutHead.addView(nameImageSplitLayout1);
		nameImageSplitLayoutHead.addView(profilePicture);
		topSubLinearLayout.addView(nameImageSplitLayoutHead);

		topSubLinearLayout.addView(lastName);
		topSubLinearLayout.addView(dob);
		topSubLinearLayout.addView(pincode);
		topSubLinearLayout.addView(description);
		topSubLinearLayout.addView(saveButtonPro);
		topHeadLinearLayout.addView(topSubLinearLayout);
		
		topScrollLayout.addView(topHeadLinearLayout);
		
	}
	
	public void mandatoryField()
	{
		firstName.setHint("First name *");
		lastName.setHint("Last name");
		shopName.setHint("Shop name *");
		//System.out.println("========= App Name ====="+appName);
		if(appName.equals("LivePrice"))
		{
			pincode.setHint("Pincode *");
			city.setHint("City *");
			district.setHint("District *");
			addressEditText.setHint("Address *");
			description.setHint("Description *");
			
		}
		else 
		{
			pincode.setHint("Pincode");
			city.setHint("City");
			district.setHint("District");
			addressEditText.setHint("Address");
			description.setHint("Description");
		}
		if(coordinatorUniqueId.equals("0"))
		{
			pincode.setHint("Pincode *");
		}
		
	}
	public void getUserEnteredValue()
	{
		mobileNumberStr = mobileNumber.getText().toString();
		firstNameStr = firstName.getText().toString();
		lastNameStr = lastName.getText().toString();
		shopNameStr = shopName.getText().toString();
 
		pincodeStr = pincode.getText().toString();
		descriptionStr = description.getText().toString();
		addressStr = addressEditText.getText().toString();
		cityStr = city.getText().toString();
		districtStr = district.getText().toString();
		selectedCategoryStr=chooseCategory.getText().toString();
		minimumOrderStr=minimumOrder.getText().toString();
	}
	
	public boolean validationAllFields()
	{
		boolean returnValue = false;
		 
		if(firstNameStr.equals("") && !appName.equals("LivePrice"))
		{
			displayToast("First Name is required.");
			returnValue = true;
		}
		else if(shopNameStr.equals("") && appName.equals("LivePrice"))
		{
			displayToast("Shop Name is required.");
			returnValue = true;
		}
				
		else if(mobileNumberStr.equals(""))
		{	
			displayToast("Mobile Number is required.");
			returnValue = true;
		}
		else if(mobileNumberStr.length()> 0 && mobileNumberStr.length()<10)
		{	
			displayToast("Please enter valid mobile number.");
			returnValue = true;
		}
		
		else if(pincodeStr.length()> 0 && pincodeStr.length()<6 )
		{
			displayToast("Please enter valid pincode number.");
			returnValue = true;
		}
		else if(pincodeStr.length() == 0 && coordinatorUniqueId.equals("0"))
		{
			displayToast("Pincode is required.");
			returnValue = true;
		}
		
		 
		if(!returnValue && appName.equals("LivePrice"))
		{
			 
			shopNameStr = shopName.getText().toString();
			pincodeStr = pincode.getText().toString();
			descriptionStr = description.getText().toString();
			
			addressStr = addressEditText.getText().toString();
			cityStr = city.getText().toString();
			districtStr = district.getText().toString();
			selectedCategoryStr=chooseCategory.getText().toString();
			 int selectedId = rg.getCheckedRadioButtonId();
        	 radioDeliveryButton = (RadioButton)findViewById(selectedId); 
	         minimumOrderStr=minimumOrder.getText().toString();
			 
			if(pincodeStr.equals(""))
			{
				displayToast("Pincode is required.");
				returnValue = true;
			}
			else if(cityStr.equals(""))
			{
				displayToast("City is required.");
				returnValue = true;
			}
			else if(districtStr.equals(""))
			{
				displayToast("District is required.");
				returnValue = true;
			}
			else if(addressStr.equals(""))
			{
				displayToast("Address is required.");
				returnValue = true;
			}
			else if(descriptionStr.equals(""))
			{
				displayToast("Return policy is required.");
				returnValue = true;
			}
			else if(!isTookPhoto && profileId.equals("0"))
			{
				displayToast("Please add the picture.");
				returnValue = true;
			}
			else if(minimumOrderStr.equals(""))
			{
				displayToast("Minimum Purchase value is required.");
				returnValue = true;
			}
			
			 else if(totalselectedValues==0)
			{
				displayToast("Choose atleast one category.");
				returnValue = true;
			}
			
			
			
			else if(itemValues.size() == 0 && totalSelectedDistricts==0)
			{
				displayToast("Choose atleast one delivery location.(District/pincode)");
				returnValue = true;
			}
			
			
			else if(radioDeliveryButton.getText().toString().equalsIgnoreCase("District"))
			{  
		         System.out.println("its in district validation");
				 for(int i=0;i<districtNames.length;i++){
					 	if(checked_District[i]==true){
						totalSelectedDistricts++;
					 	}
				 }
				 
				  if(totalSelectedDistricts==0)
					{   displayToast("Choose atleast one delivery district");
						returnValue = true;
					} 
			 }
			 else if(radioDeliveryButton.getText().toString().equalsIgnoreCase("Pincode"))
			{	 System.out.println("its in pincde validation");     
			
			      if(itemValues.size()==0)
				{    displayToast("Choose atleast one delivery pincode location..");
					returnValue = true;
				}
			}
			
		}
		if(!returnValue) 
		{
			profilePageData = new ProfilePageData();
			profilePageData.setProfileId(profileId);
			profilePageData.setCoordinatorUniqueId(coordinatorUniqueId);
			profilePageData.setAppName(appName);
			
			profilePageData.setMobileNumber(mobileNumberStr);
			profilePageData.setFirstName(firstNameStr);
			profilePageData.setLastName(lastNameStr);
			profilePageData.setPincode(pincodeStr);
			profilePageData.setShopName(shopNameStr);
			profilePageData.setDescription(descriptionStr);
			profilePageData.setMinimumOrder(minimumOrderStr);
			
			 int selectedIdForProfileType= profileType.getCheckedRadioButtonId();
			 chosePrfileType = (RadioButton)findViewById(selectedIdForProfileType); 
			 
			 profilePageData.setProfileType( chosePrfileType.getText().toString());
			  
       	   int selectedId = rg.getCheckedRadioButtonId();
       	    radioDeliveryButton = (RadioButton)findViewById(selectedId); 
       	   if(radioDeliveryButton.getText().toString().equalsIgnoreCase("District"))
       	   {
       		 totalSelectedDistricts=0;
       		display_checked_districts="";
       		 for(int i=0;i<districtNames.length;i++){
 			 	if(checked_District[i]==true){
 				totalSelectedDistricts++;
 				display_checked_districts =display_checked_districts+"//"+districtNames[i];
                }
       		 }
       		   System.out.println("its in while getting selecting district");
       		   profilePageData.setDeliveryDistrict(display_checked_districts);
       		   profilePageData.setDeliveryLocation("");
       		   
       		/*totalSelectedDistricts=0;
       		 for(int i=0;i<districtNames.length;i++){
				 	if(checked_District[i]==true){
					totalSelectedDistricts++;
					display_checked_districts =display_checked_districts+"//"+districtNames[i];
               System.out.println("display_checked_districts"+display_checked_districts);
				}
				if(totalSelectedDistricts==33)
				  choseLocation.setHint("All Districts");
				else
				 choseLocation.setHint(totalSelectedDistricts+" District(s)");
			           }*/
       		   
       	   }
       	   else
       	   {
       		   
       		 //  System.out.println("the value for items list is---"+itemValues.size());
       		 chooseLocationStr="";
			for(int i=0;i<itemValues.size();i++)
			{
				chooseLocationStr=chooseLocationStr+"//"+itemValues.get(i).toString();
			}
			//System.out.println("the formed value is==="+chooseLocationStr);
			String finalChoseLocationValue = chooseLocationStr.substring(2);
			//System.out.println("the final formed value is==="+finalChoseLocationValue);
			profilePageData.setDeliveryLocation(finalChoseLocationValue);
			profilePageData.setDeliveryDistrict("");
       	   }
			
			
			
			if(!display_checked_categories.isEmpty())
			{
				String addSlashAtEndOfCategories = display_checked_categories+"//";
			//	System.out.println("the addSlashAtEndOfCategories value is==="+addSlashAtEndOfCategories);
				String finalString = addSlashAtEndOfCategories.substring(2);
			//	System.out.println("the addSlashAtEndOfCategories finalString is==="+finalString);
				profilePageData.setSelectedCategories(finalString);
			}
			else
			{
			  profilePageData.setSelectedCategories(category);
			}
			
			
			profilePageData.setAddress(addressStr);
			
			profilePageData.setCity(cityStr);
			profilePageData.setDistrict(districtStr);
			profilePageData.setGeoLocation(latituteField+":"+longitudeField);
			
			returnValue = false;
		} 
		return returnValue;
	}
	

	public void displayToast(String msg)
	{
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	 
	private void saveProfileDetails(){

		if (applicationInformation.isInternetConnection(myContext)) {
            XMLCreatorProfile xmlCreator = new XMLCreatorProfile();
			String requestAddProfile = xmlCreator.serializeSignUpArgs(profilePageData);
		//	System.out.println("the received value is---"+requestAddProfile);
			
			TaskToFetchData createRequest=new TaskToFetchData();
			createRequest.setUrl(Configuration.SIGN_UP);
				
			createRequest.setRequestXML(requestAddProfile);
			createRequest.setRequestInfo("Add Profile");
	 	 	 
			String params = "";
			TaskToFetchDetailsProfile task=new TaskToFetchDetailsProfile(this, createRequest, "text");
			task.setFetchMyData(new AsyncTaskCompleteListener<TaskToFetchData>() {
				
				@Override
				public void onTaskCompleteForDownloadAudio(TaskToFetchData result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTaskComplete(TaskToFetchData result) {
					
					try {
						if(result != null)
						{ 
							XmlParserProfile mXmlParser = new XmlParserProfile();
							Document doc = mXmlParser.getDomElement(result.getResponseXML());
							NodeList nodelist = doc.getElementsByTagName("resSignup");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");
						    
						     if (status.equalsIgnoreCase("success")) {	
						 		 
						 		 String mainCategory = mXmlParser.getValue(element, "mainCategory");
						 		  SharedPreferences mypref = getApplication().getSharedPreferences("RainbowAgriLivePrice",
										MODE_PRIVATE);
						 		SharedPreferences.Editor prefsEditr = mypref.edit();
						 		prefsEditr.putString("mainCategory",mainCategory);
						 		prefsEditr.commit(); 
						 		
						 		if(profileId.equals("0"))
						 			displayToast("Profile details has been added successfully.");
						 		else
						 			displayToast("Profile details has been updated successfully.");
                                
						 		
						 		
						 		sourceFile = new File(mSDCardPath+"/temp.jpg");
						 		destinationFile = new File(mSDCardPath+"/"+mXmlParser.getValue(element, "profileId")+
										".jpg");
								
						 		sourceFile.renameTo(destinationFile);
								
						 		if(destinationFile.exists() && isTookPhoto)
						 		{
						 			saveProfileImageDetails(mXmlParser.getValue(element, "profileId"));
						 		}
						 			
						 		else
						 		{
						 			Bundle b = new Bundle();
						 			b.putString("shopName", shopNameStr);
						 			
						 			Intent intent = new Intent();
						 			intent.putExtras(b);
							 		setResult(Activity.RESULT_OK, intent);
							 		finish();
						 		}
						 			
	
						 	 } 
						 	 else if(status.equalsIgnoreCase("pincode")) {		 
						 		displayToast("Please enter valid pincode.");
						 	 }
						 	 
						 	 //For Invalid vendor; display message 
						 	 else{
						 		displayToast("Failed to update");

						 	 }
					}
					else
					{
						displayToast("No result");
					}	
					} catch (Exception e) {
						if(applicationInformation.isInternetConnection(myContext)){
							displayToast("Session Expired");
						}else{

							displayToast("No internet connection");
						}
						
					}
				}
			});
			 task.execute(params);
		} else {
			displayToast("No internet connection");
		}
		
	}
	
	
	private void saveProfileImageDetails(String profileIdTemp){
		
		if (applicationInformation.isInternetConnection(myContext)) {

			XMLCreatorProfile xmlCreator = new XMLCreatorProfile();
			String requestAddProfile = profileIdTemp; 
			TaskToFetchData createRequest=new TaskToFetchData();;
			createRequest.setRequestXML(requestAddProfile);
			createRequest.setRequestInfo("Image Upload");
			createRequest.setUrl(Configuration.UPLOAD_IMAGE_PROFILE);
			String params = "";
			TaskToFetchDetailsProfile task=new TaskToFetchDetailsProfile(this, createRequest, "imageUpload");
			task.setFetchMyData(new AsyncTaskCompleteListener<TaskToFetchData>() {
				
				@Override
				public void onTaskCompleteForDownloadAudio(TaskToFetchData result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTaskComplete(TaskToFetchData result) {
					
					try {
					
						//Parse the response XML string to know the status
						XmlParserProfile mXmlParser = new XmlParserProfile();
						Document doc = mXmlParser.getDomElement(result.getResponseXML());
						NodeList nodelist = doc.getElementsByTagName("responseSaveProfileImage");
						Element element = (Element) nodelist.item(0);
					    String status = mXmlParser.getValue(element, "status");

					 	 if (status != "success" ) {	
					 		destinationFile.delete();
					 		if(profileId.equals("0"))
					 		{	
					 			displayToast("Profile picture has been uploaded successfully.");
					 			Toast.makeText(getApplicationContext(), "Thank you for registering. Your password to login will be delivered by SMS very shortly.", Toast.LENGTH_LONG).show();
					 		}	
					 		else
					 		{
					 			displayToast("Profile picture has been updated successfully.");
					 		}

				 			

					 		//Move back to Login
					 		Bundle b = new Bundle();
				 			b.putString("shopName", shopNameStr);
				 			
				 			Intent intent = new Intent();
				 			intent.putExtras(b);
					 		setResult(Activity.RESULT_OK, intent);
					 		finish();
					 		
					 	 } 
					 	
					 	 //For Invalid vendor; display message 
					 	 else{
					 		displayToast("Failed to update");
					 	 }
						
					} catch (Exception e) {

						if(applicationInformation.isInternetConnection(myContext)){
							displayToast("Session Expired");

						}else{

							displayToast("No internet connection");
						}
						
					}
				}
			});
			 task.execute(params);
		} else {

			displayToast("No internet connection");
		}	
	}
	
    public void createSDCardFolder() throws IOException
    {
		File file0 = new File(Environment.getExternalStorageDirectory()	+ "/Rainbow");

		File file1 = new File(Environment.getExternalStorageDirectory()	+ "/Rainbow/Profile");

		if (!file0.exists()) {
			if (file0.mkdir()) {

			}
		}

		if (!file1.exists()) {
			if (file1.mkdir()) {

			}
		}
		else
		{
			File test = new File(mSDCardPath+"/temp.jpg");
			if(test.exists())
			{
//				File nomediaFile = new File(mSDCardPath+"/.nomedia");
//				if(nomediaFile.exists())
//					nomediaFile.delete();
//				test.renameTo(nomediaFile);
				test.delete();
			}
			
		}

    	mSDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Rainbow/Profile";
    	
    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) myContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	if(hasGps)
            		if(applicationInformation.isInternetConnection(myContext))
            			showSettingsAlert();
            		else
            		{	
            			locationButton.setBackgroundResource(R.drawable.off);
						locationChangeSettingFlag = false;
            			displayToast("No internet connection.");
            		}	
            } else {

                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                            locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latituteField = location.getLatitude();
                            longitudeField = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                            	latituteField = location.getLatitude();
                            	longitudeField = location.getLongitude();
                            }
                        }
                    }
                }
                else
                {

                	if(hasGps)
                		if(applicationInformation.isInternetConnection(myContext))
                			showSettingsAlert();
                		else
                		{	
                			displayToast("No internet connection.");                

                			locationButton.setBackgroundResource(R.drawable.off);
							locationChangeSettingFlag = false;
                		}	
           		}
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
    public void showSettingsAlert(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfilePageActivity.this);
    	
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                ProfilePageActivity.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTING_LOCATION_REQUEST);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            	locationButton.setBackgroundResource(R.drawable.off);
				locationChangeSettingFlag = false;
            	dialog.cancel();
            }
        });
        alertDialog.show();    // Showing Alert Message
        
    }   
    public void setAddress() throws IOException
    {
    	
        	Geocoder geocoder;
        	List<Address> addresses =null;

        	if(applicationInformation.isInternetConnection(myContext))
        	{
	        	while(true)
	        	{
	            	ProgressDialog progressDialog = new ProgressDialog(ProfilePageActivity.this);
	            	progressDialog.setMessage("Please wait...");

	            	geocoder = new Geocoder(this, Locale.getDefault());
	        		addresses = geocoder.getFromLocation(latituteField, longitudeField, 1);
	        		if(addresses!=null)
	        		{

	        			break;
	        		}
	        			
	        	}
        	
        	}
        	else
        		displayToast("No internet connection.");
        
        	for(int j=0; (addresses!=null && j<addresses.size()); j++)
        	{
            	String city1 = addresses.get(j).getAddressLine(1);
            	String pincode1 = ""+addresses.get(j).getPostalCode();
            	String district1 = "";

            	
            	String addressTemp = "";
            	int maxAddressLine = addresses.get(j).getMaxAddressLineIndex();
            	for(int i=0; i<maxAddressLine; i++)
            	{
            		addressTemp = addressTemp+addresses.get(j).getAddressLine(i)+"\n";
            	}
            	
            	district1 = (maxAddressLine >= 1 && addresses.get(j).getAddressLine(maxAddressLine-1).indexOf(", Tamil") != -1) ?  addresses.get(j).getAddressLine(maxAddressLine-1).substring(0, addresses.get(j).getAddressLine(maxAddressLine-1).indexOf(", Tamil")):"";
            	city1 = (maxAddressLine-2 >= 0) ?  ""+addresses.get(j).getAddressLine(maxAddressLine-2) : city1;
            	if(city1!=null)
            	{
            		if(city1.lastIndexOf(",") != -1)
            		{
            			city1 = city1.substring(city1.lastIndexOf(",")+1);
            			if(!(city1.trim().equals("")))
            				city1 =city1.trim();
            		}
            	}

            	if(pincode1!=null && !pincode1.equals("null") && pincode1.contains("\\d+") )
            	{           		
            		pincode.setText(""+pincode1);
            	}
            	else
            	{
            		String pickPincode = null;

            		if(addressTemp!=null && addressTemp.lastIndexOf(" ") != -1)
            		{
            			pickPincode = addressTemp.substring(addressTemp.lastIndexOf(" ")+1);
            			pickPincode = (pickPincode.length()>6) ? pickPincode.substring(0,6) : pickPincode;

            			if (pickPincode.contains("[a-zA-Z]+") == false && pickPincode.length() == 6)
            			{
            				pincode.setText(""+pickPincode);
            			}
            			
            		}
            	}

            	addressEditText.setText(addressTemp);
            	city.setText(city1);
            	
            	district.setText(district1);
        	}
    }

    
    public void deleteTempFile()
    {
    	sourceFile = new File(mSDCardPath+"/temp.jpg");
    	sourceFile.delete();
    }
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 latituteField = (double) (location.getLatitude());
		 longitudeField = (double) (location.getLongitude());

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		Log.d("Profile page", "2 GPS is DISABLED");
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Log.d("Profile page", "1 GPS is ENABLED");
	}




	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		Log.d("Profile page", "1 GPS status CHANGED");
	}
	
	
	
	/**
	 * Sign up
	 */
	private void doSignup(){
		if (applicationInformation.isInternetConnection(myContext)) {

			XMLCreatorProfile xmlCreator = new XMLCreatorProfile();
			String requestAddProfile =xmlCreator.serializeSignUpArgs(profilePageData);
			System.out.println("the received value is---"+requestAddProfile);
			
			TaskToFetchData createRequest=new TaskToFetchData();
			createRequest.setUrl(Configuration.SIGN_UP);
				
			createRequest.setRequestXML(requestAddProfile);
			createRequest.setRequestInfo("Add Profile");
			
			String params = "";
			task=new TaskToFetchDetailsProfile(this, createRequest, "text");
			task.setFetchMyData(new AsyncTaskCompleteListener<TaskToFetchData>() {
				
				@Override
				public void onTaskCompleteForDownloadAudio(TaskToFetchData result) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onTaskComplete(TaskToFetchData result) {
					// TODO Auto-generated method stub
					try {
						if(result != null){
							//Parse the response XML string to know the status
							XmlParserProfile mXmlParser = new XmlParserProfile();
							Document doc = mXmlParser.getDomElement(result.getResponseXML());
							NodeList nodelist = doc.getElementsByTagName("resSignup");
							Element element = (Element) nodelist.item(0);
						    String status = mXmlParser.getValue(element, "status");
						    
						    if (status.equalsIgnoreCase("success")) {
						    	
						    	sourceFile = new File(mSDCardPath+"/temp.jpg");
						 		destinationFile = new File(mSDCardPath+"/"+mXmlParser.getValue(element, "profileId")+
										".jpg");
								
						 		sourceFile.renameTo(destinationFile);
								
						 		if(destinationFile.exists())
						 		{
						 			
						 			saveProfileImageDetails(mXmlParser.getValue(element, "profileId"));
						 		}else
						 		{
						 			

						 			Toast.makeText(getApplicationContext(), "Thank you for registering. Your password to login will be delivered by SMS very shortly.", Toast.LENGTH_LONG).show();
						 			//Move back to Login
							 		Intent intent = new Intent();
							 		setResult(Activity.RESULT_OK, intent);
							 		finish();
						 		}
								
							} else if(status.equalsIgnoreCase("pincode")) {		 
						 		displayToast("Please enter valid pincode.");
						 	 } else if(status.equalsIgnoreCase("existing")){
						 		displayToast("This mobile number already registered. Use Forgot Password to reset your password if you missed it");
						 	 }
						 	 //For Invalid vendor; display message 
						 	 else{
						 		displayToast("Failed to update");

						 	 }
							
						}
					} catch (Exception e) {
						// TODO: handle exception
						if(applicationInformation.isInternetConnection(myContext)){
							displayToast("Session Expired");
						}else{

							displayToast("No internet connection");
						}
					}
				}
			});
			task.execute(params);
		}else{
			displayToast("No internet connection");
		}
	}
 
	
	public void compressImage(String path)
	{
		int MAX_IMAGE_SIZE = 50 * 1024; // max final file size
		File f = new File(path);
		Bitmap bmpPic = null;
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		
		bmpOptions.inDither=false;                    
		bmpOptions.inSampleSize = 2;                   
		bmpOptions.inPurgeable=true;                 
		bmpOptions.inInputShareable=true;             
		bmpOptions.inTempStorage=new byte[16 * 1024]; 
		try
		{ 
			if(f != null)
			{	 bmpPic = BitmapFactory.decodeFile(path,bmpOptions);
//				System.out.println("f is not null.="+(f.length()/1024));
//				displayToast("111.  f is not null.="+(f.length()/1024));
			}
		}
		catch(OutOfMemoryError e)
		{
//			System.out.println("== In catch block == OutOfMemoryError==============");
			System.gc();
			try
			{
				if(f != null)
				{	 bmpPic = BitmapFactory.decodeFile(path, bmpOptions);
//					System.out.println("f is not null.="+(f.length()/1024));
					
//					displayToast("222.  f is not null.="+(f.length()/1024));
				}
			}
			catch(OutOfMemoryError e1)
			{
//				System.out.println("f is not null.="+(f.length()/1024));
				
//				displayToast("333.  f is not null.="+(f.length()/1024));
			}
		}
	
//		System.out.println("== bmpPic =="+bmpPic);
		if(bmpPic !=null)
		{
			if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
			    
			    while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
			        bmpOptions.inSampleSize++;
			        bmpPic = BitmapFactory.decodeFile(path, bmpOptions);
			    }
			}
			int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
			int streamLength = MAX_IMAGE_SIZE;
			while (streamLength >= MAX_IMAGE_SIZE) {
			    ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
			    compressQuality -= 5;
			    Log.d("compressImage", "Quality: " + compressQuality);
			    bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
			    byte[] bmpPicByteArray = bmpStream.toByteArray();
			    streamLength = bmpPicByteArray.length;
			    Log.d("compressImage", "Size: " + streamLength);
			}
			
			try {

				FileOutputStream bmpFile = new FileOutputStream(path);
			    bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
			    bmpFile.flush();
			    bmpFile.close();
			} catch (Exception e) {
			    Log.e("compressImage", "Error on saving file");
			}
		}
	}
	
	
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	
	
	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Browse Photo"};//,
		//		"Cancel" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageActivity.this);
//		builder.setTitle("Add Photo!");
		builder.setTitle(null);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(mSDCardPath+"/temp.jpg");
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
	
	
	public void copyFile(File sourceLocation, File targetLocation)
	{
//    	File sourceLocation = files[position-1].getAbsoluteFile();
//		File sourceLocation = new File(sourceLocation);
    	if(sourceLocation.exists()){
             
			try {
				
                InputStream in;// = new FileInputStream(sourceLocation);
                OutputStream out;

				in = new FileInputStream(sourceLocation);
				out = new FileOutputStream(targetLocation);
				
				
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                 
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                 
                in.close();
                out.close();
                out.flush();

			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
	}	
	
	 public String[] convertFileToArray()
	 {
		 String[] arr= null;
		   List<String> items= new ArrayList<String>();

		    try 
		    { 
		       BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getAssets().open("pincode.txt")));
		        String str_line; 

		        while ((str_line = reader.readLine()) != null) 
		        { 
		            str_line = str_line.trim(); 
		            if ((str_line.length()!=0))  
		            { 
		               items.add(str_line);
		            } 
		        }

		        arr = (String[])items.toArray(new String[items.size()]);
		     //   System.out.println("the collected text file list is==="+arr.length);
		     //   System.out.println("the collected list is==="+arr.toString());
		    }
		    catch (Exception e) {
				
		    	e.printStackTrace();
				
			}
			return arr;
	 }
	 public static void setListViewHeightBasedOnChildren(ListView listView) {
		    ListAdapter listAdapter = listView.getAdapter();
		    if (listAdapter == null)
		        return;

		    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		    int totalHeight = 0;
		    View view = null;
		    for (int i = 0; i < listAdapter.getCount(); i++) {
		        view = listAdapter.getView(i, view, listView);
		        if (i == 0)
		            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

		        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
		        totalHeight += view.getMeasuredHeight();
		    }
		    ViewGroup.LayoutParams params = listView.getLayoutParams();
		    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		    listView.setLayoutParams(params);
		    listView.requestLayout();
		} 
	
	
//- See more at: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample/#sthash.Yrh50P9V.dpuf
}
