package com.rainbowagri.profilepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity {

	Button addNewProfileId_Broadcaster, addNewProfileId_LivePrice,  addNewProfileId_Others, viewProfileId_Broadcaster,  viewProfileId_LivePrice, viewProfileId_Others ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.main);

		 addNewProfileId_Broadcaster = (Button)findViewById(R.id.addNewProfileId_Broadcaster); 
		 addNewProfileId_LivePrice = (Button)findViewById(R.id.addNewProfileId_LivePrice);  
		 addNewProfileId_Others = (Button)findViewById(R.id.addNewProfileId_Others); 
		 viewProfileId_Broadcaster = (Button)findViewById(R.id.viewProfileId_Broadcaster);  
		 viewProfileId_LivePrice = (Button)findViewById(R.id.viewProfileId_LivePrice);
		 viewProfileId_Others = (Button)findViewById(R.id.viewProfileId_Others);
		 
		 addNewProfileId_Broadcaster.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageActivity.class);
				 moveToNextScreen.putExtra("coordinatorUniqueId", "33");
				 moveToNextScreen.putExtra("appName", "Broadcaster");
				 moveToNextScreen.putExtra("mobileNumber", "9865782625");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();
//				 LoginActivity.this.startActivityForResult(moveToNextScreen, 1);
				
			}
		});
		 
		 addNewProfileId_LivePrice.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageActivity.class);
				 moveToNextScreen.putExtra("coordinatorUniqueId", "3");
				 moveToNextScreen.putExtra("appName", "LivePrice");
				 moveToNextScreen.putExtra("mobileNumber", "9865782625");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();
//				 LoginActivity.this.startActivityForResult(moveToNextScreen, 1);
				
			}
		});
		 addNewProfileId_Others.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageActivity.class);
				 moveToNextScreen.putExtra("appName", "LivePrice:Signup");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();
			}
		});
		 viewProfileId_Broadcaster.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageViewActivity.class);
				 moveToNextScreen.putExtra("coordinatorUniqueId", "33");
				 moveToNextScreen.putExtra("appName", "Broadcaster");
				 moveToNextScreen.putExtra("mobileNumber", "9865782625");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();
	
			}
		});
		 viewProfileId_LivePrice.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageViewActivity.class);
				 moveToNextScreen.putExtra("coordinatorUniqueId", "3");
				 moveToNextScreen.putExtra("appName", "LivePrice");
				 moveToNextScreen.putExtra("mobileNumber", "9865782625");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();
				
			}
		});
		 viewProfileId_Others.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				 Intent moveToNextScreen = new Intent(LoginActivity.this, ProfilePageViewActivity.class);
				 moveToNextScreen.putExtra("coordinatorUniqueId", "3");
				 moveToNextScreen.putExtra("appName", "CarryBag");
				 LoginActivity.this.startActivity(moveToNextScreen);
				 LoginActivity.this.finish();

			}
		});

	}
	
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
				case 1:
				if (resultCode == RESULT_OK) {

				}
			}
			
		}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("Profile-MainActivity", "onStart was called");
	}
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("Profile-MainActivity", "onResume was called");
//	    // Get the Camera instance as the activity achieves full user focus
//	    if (mCamera == null) {
//	        initializeCamera(); // Local method to handle camera init
//	    }
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("Profile-MainActivity", "onPause was called");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("Profile-MainActivity", "onStop was called");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("Profile-MainActivity", "onRestart was called");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Profile-MainActivity", "onDestroy was called");
	}
}
