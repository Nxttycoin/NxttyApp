package com.nxtty.openchat;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nxtty.main.R;

public class OpenChatMainFragment extends Fragment /*implements OnClickListener*/
{

    ViewPager pagerOpenChat;
    ViewPagerAdapterOpenChat pagerAdapter;
    LinearLayout lnMainList, lnAdd, lnSearch, lnSettings;
    LinearLayout lnFirstFooter, lnSecondFooter, lnThirdFooter, lnFourthFooter;

    
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	View view = inflater.inflate(R.layout.openchat_mainfragment, null, false);
	pagerOpenChat = (ViewPager) view.findViewById(R.id.pagerOpenChat);
	pagerAdapter = new ViewPagerAdapterOpenChat(getFragmentManager());
	pagerOpenChat.setAdapter(pagerAdapter);
	pagerOpenChat.setCurrentItem(0);

	lnMainList = (LinearLayout) view.findViewById(R.id.lnMainList);
	lnMainList.setOnClickListener(new OnClickListener()
	{
	    
	    @Override
	    public void onClick(View v)
	    {
		 lnFirstFooter.setVisibility(View.VISIBLE);
		    lnSecondFooter.setVisibility(View.INVISIBLE);
		    lnThirdFooter.setVisibility(View.INVISIBLE);
		    lnFourthFooter.setVisibility(View.INVISIBLE);
		    pagerOpenChat.setCurrentItem(0);
		
	    }
	});
	
	lnAdd = (LinearLayout) view.findViewById(R.id.lnAdd);
	lnAdd.setOnClickListener(new OnClickListener()
	{
	    
	    @Override
	    public void onClick(View v)
	    {
		  lnFirstFooter.setVisibility(View.INVISIBLE);
		    lnSecondFooter.setVisibility(View.VISIBLE);
		    lnThirdFooter.setVisibility(View.INVISIBLE);
		    lnFourthFooter.setVisibility(View.INVISIBLE);
		    pagerOpenChat.setCurrentItem(1);
		
	    }
	});
	lnSearch = (LinearLayout) view.findViewById(R.id.lnSearch);
	lnSearch.setOnClickListener(new OnClickListener()
	{
	    
	    @Override
	    public void onClick(View v)
	    {
		 lnFirstFooter.setVisibility(View.INVISIBLE);
		    lnSecondFooter.setVisibility(View.INVISIBLE);
		    lnThirdFooter.setVisibility(View.VISIBLE);
		    lnFourthFooter.setVisibility(View.INVISIBLE);
		    pagerOpenChat.setCurrentItem(2);
		
	    }
	});
	lnSettings = (LinearLayout) view.findViewById(R.id.lnSettings);
	lnSettings.setOnClickListener(new OnClickListener()
	{
	    
	    @Override
	    public void onClick(View v)
	    {
		 lnFirstFooter.setVisibility(View.INVISIBLE);
		    lnSecondFooter.setVisibility(View.INVISIBLE);
		    lnThirdFooter.setVisibility(View.INVISIBLE);
		    lnFourthFooter.setVisibility(View.VISIBLE);
		    pagerOpenChat.setCurrentItem(3);
		
	    }
	});

	lnFirstFooter = (LinearLayout) view.findViewById(R.id.lnFirstFooter);
	lnFirstFooter.setVisibility(View.VISIBLE);
	lnSecondFooter = (LinearLayout) view.findViewById(R.id.lnSecondFooter);
	lnSecondFooter.setVisibility(View.INVISIBLE);
	lnThirdFooter = (LinearLayout) view.findViewById(R.id.lnThirdFooter);
	lnThirdFooter.setVisibility(View.INVISIBLE);
	lnFourthFooter = (LinearLayout) view.findViewById(R.id.lnFourthFooter);
	lnFourthFooter.setVisibility(View.INVISIBLE);

	return view;
    }
    
    

    public class ViewPagerAdapterOpenChat extends FragmentStatePagerAdapter
    {

	public ViewPagerAdapterOpenChat(FragmentManager fm)
	{
	    super(fm);
	    // TODO Auto-generated constructor stub
	}

	@Override
	public int getCount()
	{
	    return 4;
	}

	@Override
	public Fragment getItem(int arg0)
	{
	    switch (arg0)
	    {
	    case 0:
		return OpenChatListFragment.newInstance(getActivity());
	    case 1:
		return OpenChatAddFragment.newInstance(getActivity());
	    case 2:
		return OpenChatSearchFragment.newInstance(getActivity());
	    case 3:
		return OpenChatSettingsFragment.newInstance(getActivity());
	    default:
		//This code for handling the default
		return  OpenChatListFragment.newInstance(getActivity());
	    }
	}

    }

    public static OpenChatMainFragment newInstance()
    {
	OpenChatMainFragment newHome = new OpenChatMainFragment();
	return newHome;
    }

   /* @Override
    public void onClick(View v)
    {
	switch (v.getId())
	{
	case R.id.lnMainList:
	    lnFirstFooter.setVisibility(View.VISIBLE);
	    lnSecondFooter.setVisibility(View.INVISIBLE);
	    lnThirdFooter.setVisibility(View.INVISIBLE);
	    lnFourthFooter.setVisibility(View.INVISIBLE);
	    pagerOpenChat.setCurrentItem(0);
	    break;
	case R.id.lnAdd:
	    lnFirstFooter.setVisibility(View.INVISIBLE);
	    lnSecondFooter.setVisibility(View.VISIBLE);
	    lnThirdFooter.setVisibility(View.INVISIBLE);
	    lnFourthFooter.setVisibility(View.INVISIBLE);
	    pagerOpenChat.setCurrentItem(1);
	    break;
	case R.id.lnSearch:
	    lnFirstFooter.setVisibility(View.INVISIBLE);
	    lnSecondFooter.setVisibility(View.INVISIBLE);
	    lnThirdFooter.setVisibility(View.VISIBLE);
	    lnFourthFooter.setVisibility(View.INVISIBLE);
	    pagerOpenChat.setCurrentItem(2);
	    break;
	case R.id.lnSettings:
	    lnFirstFooter.setVisibility(View.INVISIBLE);
	    lnSecondFooter.setVisibility(View.INVISIBLE);
	    lnThirdFooter.setVisibility(View.INVISIBLE);
	    lnFourthFooter.setVisibility(View.VISIBLE);
	    pagerOpenChat.setCurrentItem(3);
	    break;

	default:
	    break;
	}
    }*/

}
