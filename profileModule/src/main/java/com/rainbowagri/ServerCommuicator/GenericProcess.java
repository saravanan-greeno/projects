package com.rainbowagri.ServerCommuicator;

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
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class GenericProcess {
	
	Context context;
	 public String[] convertFileToArray(Context context)
	 {
		 String[] arr= null;
		   List<String> items= new ArrayList<String>();

		    try 
		    { 
		       BufferedReader reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("pincode.txt")));
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
		        System.out.println("the collected text file list is==="+arr.length);
		        System.out.println("the collected list is==="+arr.toString());
		    }
		    catch (Exception e) {
				
		    	e.printStackTrace();
				
			}
			return arr;
	 }
	 
	 
		public void copyFile(File sourceLocation, File targetLocation)
		{
//	    	File sourceLocation = files[position-1].getAbsoluteFile();
//			File sourceLocation = new File(sourceLocation);
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
		
		
		public String getPath(Uri uri, Activity activity) {
	        String[] projection = { MediaColumns.DATA };
	        Cursor cursor = activity
	                .managedQuery(uri, projection, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
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
//					System.out.println("f is not null.="+(f.length()/1024));
//					displayToast("111.  f is not null.="+(f.length()/1024));
				}
			}
			catch(OutOfMemoryError e)
			{
//				System.out.println("== In catch block == OutOfMemoryError==============");
				System.gc();
				try
				{
					if(f != null)
					{	 bmpPic = BitmapFactory.decodeFile(path, bmpOptions);
//						System.out.println("f is not null.="+(f.length()/1024));
						
//						displayToast("222.  f is not null.="+(f.length()/1024));
					}
				}
				catch(OutOfMemoryError e1)
				{
//					System.out.println("f is not null.="+(f.length()/1024));
					
//					displayToast("333.  f is not null.="+(f.length()/1024));
				}
			}
		
//			System.out.println("== bmpPic =="+bmpPic);
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
		
		
		public void displayToast(String msg)
		{
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		

		public Bitmap decodeFile(File f) {
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
}
