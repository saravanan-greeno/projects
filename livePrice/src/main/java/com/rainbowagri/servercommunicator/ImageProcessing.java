/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 20 November 2014
 */
package com.rainbowagri.servercommunicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;


public class ImageProcessing {

	/**
	 * Save the image locally
	 **/

	public boolean saveImageLocally(String mSDCardPath, String fileName,
			Bitmap bitmap) {

		File file = new File(mSDCardPath, fileName);
		if (bitmap != null) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			return true;
		}

		return false;
	}
}
