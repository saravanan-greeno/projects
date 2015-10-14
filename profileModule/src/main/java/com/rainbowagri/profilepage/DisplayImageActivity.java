package com.rainbowagri.profilepage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

//import com.greeno.crashreport.ExceptionHandler;
//import com.greeno.crashreport.UploadCrashReport;
public class DisplayImageActivity extends Activity {  
	private ImageAdapter imageAdapter;  
	private final int CAMERA_PIC_REQUEST = 1;
	@Override public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		/*Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_grid); 
		UploadCrashReport.startService(this);*/
		GridView gridview = (GridView) findViewById(R.id.gridview); 
		imageAdapter = new ImageAdapter(this); 
		gridview.setAdapter(imageAdapter);  
  
		String targetPath = "";
		
		
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		System.out.println("=== path =="+ path.getAbsolutePath());
	    if (path.exists()) {
	        File test1 = new File(path, "100MEDIA");
	        if (test1.exists()) {
	            path = test1;
	        } else {
	          
	            File test2 = new File(path, "Camera");
	            if (test2.exists()) {
	            	
	                path = test2;
	                System.out.println("== test2 =="+path.getAbsolutePath());
	            } else {
	            	File test3 = new File(path, "100ANDRO");
	                if (!test3.exists()) {
	                    test3.mkdirs();
	                }
	                path = test3;
	            }
	        }
	    } else {
	        path = new File(path, "Camera");
	        path.mkdirs();
	    }
		
//	    path= new File(Environment.get, "Camera");
	    targetPath = path.getAbsolutePath();
	    System.out.println("=== targetPath =="+targetPath);
		
		File targetDirector = new File(targetPath);  
		final File[] files = targetDirector.listFiles(); 

		int i=0;
		for (File file : files){ 
			if(i==0)
			{
				imageAdapter.add(file.getAbsolutePath());
			}
			imageAdapter.add(file.getAbsolutePath()); 
			i++;
		} 
		
		if(files.length == 0)
		{
			String imageFileName = "temp.jpg";

			File targetLocation = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Rainbow/Profile/",
					imageFileName);
			Uri outputFileUri = Uri.fromFile(targetLocation);
			Intent i1 = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			i1.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(i1, CAMERA_PIC_REQUEST);
		}
	 
	  gridview.setOnItemClickListener(new OnItemClickListener() { 
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 

	            String imageFileName = "temp.jpg";

				File targetLocation = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/Rainbow/Profile/",
						imageFileName);
	            if(position == 0)
	            {
	            	

					Uri outputFileUri = Uri.fromFile(targetLocation);
					Intent i = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
					startActivityForResult(i, CAMERA_PIC_REQUEST);					
	            }
	            else
	            {
	            	File sourceLocation = files[position-1].getAbsoluteFile();

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
	         
						Intent intent = new Intent();
					    setResult(RESULT_OK, intent);
					    finish();
					
	            	}
	            }
	        } 
	   });
	} 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK)
		{
			Intent intent = new Intent();
		    setResult(RESULT_OK, intent);
		    finish();
		}
	}
}