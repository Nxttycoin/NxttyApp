package com.nxtapp.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
	
	
	try
	{

	        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
	        mChildOfContent = content.getChildAt(0);
	        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	        	
	        	try
			{
			    
	        	    possiblyResizeChildOfContent();
	        	    
			}catch (IllegalStateException e)
	        	{
				    DebugReportOnLocat.e(e);
				}
				catch (Exception e)
				{
				    DebugReportOnLocat.e(e);
				}
	               
	            }
	        });
	        
	        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
	    
	    
	} catch (IllegalStateException e)
	{
		    DebugReportOnLocat.e(e);
		}
		catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}
		
    }

    private void possiblyResizeChildOfContent() {
	
	try
	{
	    
	    int usableHeightNow = computeUsableHeight();
	        if (usableHeightNow != usableHeightPrevious) {
	            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
	            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
	            if (heightDifference > (usableHeightSansKeyboard/4)) {
	                // keyboard probably just became visible
	                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
	            } else {
	                // keyboard probably just became hidden
	                frameLayoutParams.height = usableHeightSansKeyboard;
	            }
	            mChildOfContent.requestLayout();
	            usableHeightPrevious = usableHeightNow;
	        }
	        
	    
	} catch (IllegalStateException e)
	{
		    DebugReportOnLocat.e(e);
		}
		catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}
       
    }

    private int computeUsableHeight() {
	
	try
	{
	    
	    Rect r = new Rect();
	        mChildOfContent.getWindowVisibleDisplayFrame(r);
	        return (r.bottom - r.top);
	        
	    
	}  catch (IllegalStateException e)
	{
		    DebugReportOnLocat.e(e);
		}
		catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}
	
	return 0;
       
    }

}

