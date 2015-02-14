package com.nxtty.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.nxtapp.fragment.Chat2Activity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.lib.imagezoom.ImageViewTouch;

public class ImageViewDisplay extends Activity {

	private ImageViewTouch imageView;
	//protected ImageLoader imageLoader = ImageLoader.getInstance();

	//DisplayImageOptions options;
	String imageUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.display_image);
		//getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		Bundle bundle = getIntent().getExtras();
		imageUrl = bundle.getString(Chat2Activity.Extra.IMAGES,"");

		DebugReportOnLocat.ln(" imageUrl "+imageUrl);
		/*options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();*/

		imageView = (ImageViewTouch) findViewById(R.id.imgDisplay);

		imageView.clear();

		/*if (imageUrl.trim().length() < 4) {
			
			imageView.setImageBitmap(Review.bitmap);
			imageView.setImageBitmapReset(Review.bitmap, true);

		} else {*/
		    
		    try
		    {
			if (imageUrl.trim().length() > 4) {
			    
			    Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
				// imageView.setImageBitmap(bitmap);
				 
				 imageView.setImageBitmap(bitmap);
					imageView.setImageBitmapReset(bitmap, true);
					
				 
			}
			
			
			    
			    
		    } catch (OutOfMemoryError e)
		    {
			e.printStackTrace();
		    }catch (Exception e)
		    {
			e.printStackTrace();
			
		    }
		    /*
			imageLoader.loadImage(imageUrl, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, 	View view,
						Bitmap loadedImage) {
					
					imageView.setImageBitmap(loadedImage);
					imageView.setImageBitmapReset(loadedImage, true);
					
					// Do whatever you want with Bitmap
				}
			});*/

			

		//}


	}

}
