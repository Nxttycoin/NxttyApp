package com.nxtapp.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nxtapp.classes.GroupModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.main.R;

public class GroupUnsubScribeAdapter extends BaseAdapter
{
    Context context;
    private ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    ProgressDialog mProgressDialog;
    int  pcount;
    
    
    
    public GroupUnsubScribeAdapter(Context context, ArrayList<GroupModel> discoverList2)
    {

	this.context = context;
	this.GroupList = discoverList2;
    }

    private class ViewHolder
    {
	TextView txt_Title;
	LinearLayout lnUnSubScribe;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
	
	try{
	    
	
	ViewHolder holder = null;
	LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	if (convertView == null)
	{
	    convertView = mInflater.inflate(R.layout.unsubscribelist_item, parent, false);
	    holder = new ViewHolder();
	    holder.txt_Title = (TextView) convertView.findViewById(R.id.txt_unsubscribeGroupName);
	    holder.lnUnSubScribe = (LinearLayout) convertView.findViewById(R.id.ln_UnSubScribe_btn);

	    convertView.setTag(holder);
	}
	else
	{
	    holder = (ViewHolder) convertView.getTag();
	}

	holder.txt_Title.setText(GroupList.get(position).getTitle());
	
	holder.lnUnSubScribe.setOnTouchListener(new OnTouchListener()
	{
	    @Override
	    public boolean onTouch(View v, MotionEvent e)
	    {
		// True means the event is ignored by the overlayed views
		pcount = e.getPointerCount();
		
		
		try
		{
		    DebugReportOnLocat.ln("touch count>>" + pcount);
			if (pcount == 1)
			{
			    
			    if(Network.isNetworkConnected(context)){
				
				
				 AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
				    alertDialog.setCancelable(false);
				    alertDialog.setMessage(context.getResources().getString(R.string.sure_unsubscribe) + " " + GroupList.get(position).getTitle() + " group?");
				    alertDialog.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
				    {
					public void onClick(DialogInterface dialog, int which)
					{
					    if (GroupList.size() > position)
					    {
						
						try
						{

							
							if(Network.isNetworkConnected(context)){
							    
							    new DoInBackUnsubScribe(position).execute();
							    
							}else{
							    
							    AlertUtility.showToast(context, context.getString(R.string.networkIssues));
							}
							
						    
						    
						} catch (Exception e2)
						{
						    // TODO: handle exception
						}
					    }
					    dialog.cancel();
					}
				    });
				    alertDialog.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener()
				    {
					public void onClick(DialogInterface dialog, int which)
					{
					    dialog.cancel();
					}
				    });
				    alertDialog.show();
				    
				}else{
				    
				    AlertUtility.showToast(context, context.getString(R.string.networkIssues));
				}
				
			    }
			   
			
		} catch (Exception e2)
		{
		    // TODO: handle exception
		}

		return e.getPointerCount() > 1 ? true : false;
	    }
	});
	 
//	holder.lnUnSubScribe.setOnClickListener(new OnClickListener()
//	{
//	 
//	    @Override
//	    public void onClick(View v)
//	    {
//		 
//		
//	    }
//	});

	
	    
	} catch (Exception e)
	{
	   DebugReportOnLocat.e(e);
	}
	
	
	return convertView;
    }

    @Override
    public int getCount()
    {
	// TODO Auto-generated method stub
	return GroupList.size();
    }

    @Override
    public Object getItem(int position)
    {
	// TODO Auto-generated method stub
	return position;
    }

    @Override
    public long getItemId(int position)
    {
	// TODO Auto-generated method stub
	return position;
    }

    public class DoInBackUnsubScribe extends AsyncTask<Void, String, Void>
    {

	int pos;
	ProgressDialog pDialog;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {

		    pDialog = new ProgressDialog(context);
		    pDialog.setCancelable(false);
		    pDialog.setMessage(context.getString(R.string.please_wait));
		    pDialog.show();
		    
	    } catch (Exception e)
	    {
		 DebugReportOnLocat.e(e);
	    }
	}

	public DoInBackUnsubScribe(int pos)
	{
	    this.pos = pos;
	}

	@Override
	protected Void doInBackground(Void... params)
	{
	    
	    if(GroupList.size()>pos){
		
		    String respStr = null;

		  

		    String url=Constants.baseUrl_Group + "groups/delete_member";
		    
		    DebugReportOnLocat.e("##############", "  url -->  " + url);
		    DebugReportOnLocat.e("##############", "  nxtID -->  " + Constants.NxtAcId);
		    DebugReportOnLocat.e("##############", "  groupID-->  " + GroupList.get(pos).getId());
		    DebugReportOnLocat.e("##############", "  key -->  " + Constants.ParamKey);
		    
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
		    
		    HttpPost httppost = new HttpPost(url);
		    try
		    {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
			nameValuePairs.add(new BasicNameValuePair("groupID", GroupList.get(pos).getId()));
			nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			respStr = EntityUtils.toString(response.getEntity());

			 DebugReportOnLocat.e("##############", "  responce fromd delete_member -->  " + respStr);

		    } catch (ClientProtocolException e)
		    {
		    } catch (IOException e)
		    {
		    }

		  
		
	    }
	    
	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);

	    try
	    {
		if(pDialog!=null)
		  pDialog.dismiss();
		  
	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
		
	    };
	    
	    if(GroupList.size()>pos){
		
		GroupList.remove(pos);
		 notifyDataSetChanged();
	    }
	    
	   
	}
    }
  
}
