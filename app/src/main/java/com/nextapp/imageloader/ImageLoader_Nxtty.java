package com.nextapp.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nxtapp.utils.DebugReportOnLocat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

public class ImageLoader_Nxtty {
    
    MemoryCache memoryCache=new MemoryCache();
    static FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
    
    public ImageLoader_Nxtty(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
//    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
//	displayImage(uri, new ImageViewAware(imageView), options, null, null);
//}
   // final int stub_id=R.drawable.stub;
    public void DisplayImage(String url,Activity activity, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
          	try{
          		//bitmap=newgetRoundedCornerBitmap(bitmap,10);
        		imageView.setImageBitmap(bitmap);
        	}
          	catch (OutOfMemoryError e) {
				// TODO: handle exception
				
				//System.gc();
				System.runFinalization();
				Runtime.getRuntime().gc();
				System.gc();
				 DebugReportOnLocat.e(e);
				//bitmap=newgetRoundedCornerBitmap(bitmap,10);
				imageView.setImageBitmap(bitmap);
				DebugReportOnLocat.ln("set bitmap worked");
			}
        }
        else
        {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }
    
    public void DisplayImage(String url,Context context, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
          	try{
          		//bitmap=newgetRoundedCornerBitmap(bitmap,10);
        		imageView.setImageBitmap(bitmap);
        	}
          	catch (OutOfMemoryError e) {
				// TODO: handle exception
				
				//System.gc();
				System.runFinalization();
				Runtime.getRuntime().gc();
				System.gc();
				 DebugReportOnLocat.e(e);
				//bitmap=newgetRoundedCornerBitmap(bitmap,10);
				imageView.setImageBitmap(bitmap);
				DebugReportOnLocat.ln("set bitmap worked");
			}
        }
        else
        {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }
    
    
   /* public void DisplayMMSImage(String url,Activity activity, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
          	try{
          		//bitmap=newgetRoundedCornerBitmap(bitmap,10);         		
        		imageView.setImageBitmap(bitmap);
        	}
          	catch (OutOfMemoryError e) {
				// TODO: handle exception
				
				//System.gc();
				System.runFinalization();
				Runtime.getRuntime().gc();
				System.gc();
				 DebugReportOnLocat.e(e);
				//bitmap=newgetRoundedCornerBitmap(bitmap,10);
				imageView.setImageBitmap(bitmap);
				DebugReportOnLocat.ln("set bitmap worked");
			}
        }
        else
        {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }
    }*/
    
   
    
    public Bitmap DisplayImage1(String url,Activity activity, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        /*if(bitmap!=null){
          	try{
        		imageView.setImageBitmap(bitmap);
        	}
          	catch (OutOfMemoryError e) {
				// TODO: handle exception
				
				//System.gc();
				System.runFinalization();
				Runtime.getRuntime().gc();
				System.gc();
				 DebugReportOnLocat.e(e);
				imageView.setImageBitmap(bitmap);
				DebugReportOnLocat.ln("set bitmap worked");
			}
        }
        else
        {
            queuePhoto(url, imageView);
            //imageView.setImageResource(stub_id);
        }*/
        return bitmap;
    }
        
    private void queuePhoto(String url, ImageView imageView)
    {
    	
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private static Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            //final int REQUIRED_SIZE=300;
            //final int REQUIRED_SIZE=400;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            //return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            try{
    			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    			}
    			catch (OutOfMemoryError e) {
    				// TODO: handle exception
    				 DebugReportOnLocat.e(e);
    				//clearCache();
    				
    				DebugReportOnLocat.ln("bitmap creating success");
    				//System.gc();
    				System.runFinalization();
    				Runtime.getRuntime().gc();
    				System.gc();
    				//decodeFile(f);
    			}
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                //photoToLoad.imageView.setImageBitmap(bitmap);
            	try{
            		photoToLoad.imageView.setImageBitmap(bitmap);
    				}
    				catch (OutOfMemoryError e) {
    					// TODO: handle exception
    					//System.gc();
    					System.runFinalization();
    					Runtime.getRuntime().gc();
    					System.gc();
    					 DebugReportOnLocat.e(e);
    					photoToLoad.imageView.setImageBitmap(bitmap);
    					DebugReportOnLocat.ln("here also bitmap setting worked");
    					
    				}
            }
            //else
               // photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    @Override
    protected void finalize()  {
    	// TODO Auto-generated method stub
    	try {
			
		//	System.runFinalization();
		//	 System.runFinalizersOnExit(true);
			 super.finalize();
			 DebugReportOnLocat.ln("finalize is being called in without loader");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
		}
    }
    
    public static Bitmap newgetRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    } 
}
