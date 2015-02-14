package com.nxtty.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Config;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressWarnings("deprecation")
public class Application extends android.app.Application
{

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings({ "unused" })
    @Override
    public void onCreate()
    {
	if (Config.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
	{
	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
	    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
	}

	super.onCreate();

	initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context)
    {
	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
	// Initialize ImageLoader with configuration.
	ImageLoader.getInstance().init(config);
    }
}
