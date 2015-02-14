package com.nxtapp.fragment;



import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.Dashboard;

public class BaseListFragment extends ListFragment {
	private Activity activity;
	//private View view , customHeaderView;
	private Dashboard parent;
	private static final String TAG = BaseListFragment.class.toString();
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
		setParent(getSAMainActivity());
	}
	
	
	 private static final Field sChildFragmentManagerField;
	  	private static final String LOGTAG = "GCheck";

	  	static {
	  		Field f = null;
	  		try {
	  			f = Fragment.class.getDeclaredField("mChildFragmentManager");
	  			f.setAccessible(true);
	  		} catch (NoSuchFieldException e) {
	  			Log.e(LOGTAG, "Error getting mChildFragmentManager field", e);
	  		}
	  		sChildFragmentManagerField = f;
	  	}

	  	@Override
	  	public void onDetach() {
	  		super.onDetach();

	  		if (sChildFragmentManagerField != null) {
	  			try {
	  				sChildFragmentManagerField.set(this, null);
	  			} catch (Exception e) {
	  				Log.e(LOGTAG, "Error setting mChildFragmentManager field", e);
	  			}
	  		}
	  	}
	  	
	  	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * Helper method
	 */
	public Dashboard getSAMainActivity() {
		try {
			return (Dashboard) activity;
		} catch (Exception ex) {
		    DebugReportOnLocat.e(TAG, "(SamyMainActivity) getSherlockActivity();", ex);
			return null;
		}
	}
	

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Dashboard parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public Dashboard getParent() {
		return parent;
	}


}
