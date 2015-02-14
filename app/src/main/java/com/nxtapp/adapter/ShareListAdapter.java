package com.nxtapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxtapp.classes.Item;
import com.nxtty.main.R;
import com.nxtty.main.R.id;
import com.nxtty.main.R.layout;

public class ShareListAdapter  extends BaseAdapter{

	Context mContext;

	private LayoutInflater mInflater;
	private int listsize = 0;
	Item[] items;
	public ShareListAdapter(Context c, Item[] items) {

		mInflater = LayoutInflater.from(c);
		this.listsize = items.length;
		this.items=items;
		this.mContext = c;

	}

	public int getCount() {
		return listsize;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {

		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ListData listData = new ListData();

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.list_row, null);

			listData.label = (TextView) convertView.findViewById(R.id.label);
			listData.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(listData);

		} else {

			listData = (ListData) convertView.getTag();
		}

		try{
			
			listData.label.setText(items[position].text);
			
			listData.icon.setImageBitmap(items[position].icon);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;

	}
	
	
	static class ListData {

		TextView label;
		ImageView icon;
		

	}



}
