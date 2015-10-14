package com.rainbowagri.liveprice;


import com.rainbowagri.liveprice.ChangePwdFragment.PwdChangedListener;
import com.rainbowagri.liveprice.ForgotPasswordFragment.OnOTCSendSuccessListner;
import com.rainbowagri.liveprice.LoginFragment.OnSkipClickListener;
import com.rainbowagri.liveprice.VerifyOTCFragment.InitiateChangePasswordListener;
import com.rainbowagri.profilepage.ProfilePageActivity;
 
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class UserAuthMainActivity extends Activity implements 
							LoginFragment.OnForgotPasswordClickListener,
							OnSkipClickListener,
							OnOTCSendSuccessListner, 
							InitiateChangePasswordListener, 
							PwdChangedListener{
	
	public static final String APP_NAME = "LivePrice"; //Should be manually configured for each app
	public static final String TAG = "UserAuthActivity";
	public final static String USER_MOB = "userMob";
	public static boolean isInternetConnected = false;
	public static Typeface gothamBookTf, gothamMediumTf;
	Fragment mFragment = null;
	FragmentManager mFragmentManager;
	String fragmentTag = null;
	
	/**
	 * Request code for SignUp Activity
	 */
	static final int SIGNUP_REQUEST = 1;
	static final int UPDATE_PROFILE = 2;
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		System.out.println("the button is hhe");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);

		}
		return true;
	}*/
	
	
	@Override
	public void onBackPressed() {
		System.out.println("the back repssed");
	    if (getFragmentManager().getBackStackEntryCount() > 0) {
	         getFragmentManager().popBackStack();
	         getFragmentManager().beginTransaction().commit();
	    }
	    else {
	        super.onBackPressed();
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ua_activity_login);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new LoginFragment()).commit();
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		//Add custom font from Assets
		gothamBookTf = Typeface.createFromAsset(getAssets(), "fonts/Gotham-Book_0.otf");
		gothamMediumTf = Typeface.createFromAsset(getAssets(), "fonts/Gotham-Medium_0.otf");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ua_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		mFragmentManager = getFragmentManager();
		
		if(item.getItemId() == R.id.action_user_policy){
			//Navigate to User Policies fragment
			mFragment = new UserPoliciesFragment();
			fragmentTag = mFragment.getClass().getName();
			if (mFragment != null) {
				FragmentTransaction mFragmentTransaction = mFragmentManager
						.beginTransaction();
				mFragmentTransaction.replace(R.id.container, mFragment,
						fragmentTag);
				mFragmentManager.popBackStack(fragmentTag,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				mFragmentTransaction.addToBackStack(fragmentTag).commit();	
			}
		}else if(item.getItemId() == R.id.action_sign_up){
			//Navigate to Sign Up 
			Intent signupIntent = new Intent(this, ProfilePageActivity.class);
			Bundle extras = new Bundle();
			extras.putString("appName", APP_NAME + ":signup");
			signupIntent.putExtras(extras);
			startActivityForResult(signupIntent, SIGNUP_REQUEST);
		}	
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		/** Register for CONNECTIVITY_ACTION broadcasts */
		registerReceiver(NetworkStatusReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Unregister the broadcast receiver for Internet connectivity
		unregisterReceiver(NetworkStatusReceiver);
	}
	
	@Override
	protected void onActivityResult ( int requestCode, int resultCode, Intent data ){
		/**
		 * Check for SignUp request's response
		 */
		if(requestCode == SIGNUP_REQUEST){
			/**
			 * Make sure the request was successful
			 */
			if(resultCode == RESULT_OK){
				Toast.makeText(this, "Sign up success", Toast.LENGTH_LONG).show();
			}
		}
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
				Toast noInternetToast = Toast.makeText(getApplicationContext(),
						"No internet connection", Toast.LENGTH_LONG);
				noInternetToast.show();
			}
		}
	};

	@Override
	public void onPasswordForgot(String UserMob) {
		/** User forgot the password and they want to reset it */
		
		Log.i(TAG, "onPasswordForgot for User Mobile: " +UserMob);
		mFragment = new ForgotPasswordFragment();
		fragmentTag = mFragment.getClass().getName();
		if (mFragment != null) {
			
			/** Bundle and pass the entered mobile number from LoginFragment to Forgot Password Fragment */
			Bundle args = new Bundle();
			args.putString(ForgotPasswordFragment.USER_MOB, UserMob);
			mFragment.setArguments(args);
			
			mFragmentManager = getFragmentManager();
			FragmentTransaction mFragmentTransaction = mFragmentManager
					.beginTransaction();
			mFragmentTransaction.replace(R.id.container, mFragment,
					fragmentTag);
			mFragmentManager.popBackStack(fragmentTag,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mFragmentTransaction.addToBackStack(fragmentTag).commit();
		}	
	}

	@Override
	public void onUserAuthSkip() {
		// TODO Auto-generated method stub
		Log.i(TAG, "User request to skip login and proceed to app");
		Toast.makeText(this, "Login skipped", Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void OTCSentTo(String mobNumb, String reqArg) {
		Toast.makeText(this, "OTC send success", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		Log.i(TAG, " OTC sent to user mobile " +mobNumb);
		mFragment = new VerifyOTCFragment();
		fragmentTag = mFragment.getClass().getName();
		if (mFragment != null) {
			
			/** Bundle and pass the entered mobile number from LoginFragment to Forgot Password Fragment */
			Bundle args = new Bundle();
			args.putString(ForgotPasswordFragment.USER_MOB, mobNumb);
			args.putString("resendOTC", reqArg);
			mFragment.setArguments(args);
			
			mFragmentManager = getFragmentManager();
			FragmentTransaction mFragmentTransaction = mFragmentManager
					.beginTransaction();
			mFragmentTransaction.replace(R.id.container, mFragment);
			mFragmentManager.popBackStack(null,//null -> as it should move to LoginFragment when back button pressed in ChangePassword Fragment
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mFragmentTransaction.addToBackStack(fragmentTag);
			mFragmentTransaction.commit();
		}			
	}

	@Override
	public void changePwdFor(String userMob) {
		// TODO Auto-generated method stub
		Log.i(TAG, " OTC sent to user mobile " +userMob);
		mFragment = new ChangePwdFragment();
		fragmentTag = mFragment.getClass().getName();
		if (mFragment != null) {
			
			/** Bundle and pass the entered mobile number from LoginFragment to Forgot Password Fragment */
			Bundle args = new Bundle();
			args.putString(ForgotPasswordFragment.USER_MOB, userMob);
			mFragment.setArguments(args);
			
			mFragmentManager = getFragmentManager();
			FragmentTransaction mFragmentTransaction = mFragmentManager
					.beginTransaction();
			mFragmentTransaction.replace(R.id.container, mFragment,
					fragmentTag);
			mFragmentManager.popBackStack(null,			//null -> as it should move to LoginFragment when back button pressed in ChangePassword Fragment
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mFragmentTransaction.addToBackStack(fragmentTag).commit();
		}			
	}

	public static void showNoInternetToast(Context context){
		Toast.makeText(context,"No internet connection", 
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void loginAfterPwdChanged(String userMob) {
		// TODO Auto-generated method stub
		mFragment = new LoginFragment();
		fragmentTag = mFragment.getClass().getName();
		if (mFragment != null) {
			
			/** Bundle and pass the entered mobile number from LoginFragment to Forgot Password Fragment */
			Bundle args = new Bundle();
			args.putString(USER_MOB, userMob);
			mFragment.setArguments(args);
			
			mFragmentManager = getFragmentManager();
			FragmentTransaction mFragmentTransaction = mFragmentManager
					.beginTransaction();
			mFragmentTransaction.replace(R.id.container, mFragment,
					fragmentTag);
			mFragmentManager.popBackStack(fragmentTag,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mFragmentTransaction.addToBackStack(fragmentTag);
			mFragmentTransaction.commit();
		}
	}
	/**
	 * Custom logo based on App Name
	 */
	public static void setLogo(ImageView logo){
		if (APP_NAME.equalsIgnoreCase("RainbowAgri")) {
			
			logo.setImageResource(R.drawable.ua_logo_rainbow);
			
		} else if (APP_NAME.equalsIgnoreCase("Broadcaster")) {
			
			logo.setImageResource(R.drawable.ua_logo_broadcaster);
			
		} else if (APP_NAME.equalsIgnoreCase("LivePrice")) {
			
			logo.setImageResource(R.drawable.ua_logo_liveprice);
			
		} else {
			
			logo.setImageResource(R.drawable.ua_ic_launcher);

		}
	}
}
