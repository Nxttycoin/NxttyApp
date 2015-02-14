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
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nxtapp.classes.GroupModel;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.KeyboardUtility;
import com.nxtty.main.Dashboard;
import com.nxtty.main.R;

public class PublicChatAdapter extends BaseAdapter
{

    Activity mContext;
    private ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    int pcount;

    public PublicChatAdapter(Activity context, ArrayList<GroupModel> groupList)
    {
	this.mContext = context;
	this.GroupList = groupList;
    }

    @Override
    public int getCount()
    {
	return GroupList.size();
    }

    @Override
    public Object getItem(int position)
    {
	return position;
    }

    @Override
    public long getItemId(int position)
    {
	return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

	try
	{

	    ViewHolder holder = null;
	    KeyboardUtility.hideKeypad(mContext);
	    LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    if (convertView == null)
	    {
		convertView = mInflater.inflate(R.layout.public_chat_listitem, parent, false);
		holder = new ViewHolder();
		holder.txt_groupName = (TextView) convertView.findViewById(R.id.textview_groupName);
		holder.txt_groupdetail = (TextView) convertView.findViewById(R.id.textview_GroupDetail);
		holder.lnMain = (LinearLayout) convertView.findViewById(R.id.ln_groupMain);

		convertView.setTag(holder);
	    }
	    else
	    {
		holder = (ViewHolder) convertView.getTag();
	    }

	    holder.txt_groupName.setText(GroupList.get(position).getTitle());
	    holder.txt_groupdetail.setText(GroupList.get(position).getDescription());
	    holder.txt_groupName.getParent().requestDisallowInterceptTouchEvent(true);
	    // holder.lnMain.getParent().requestDisallowInterceptTouchEvent(true);
	    holder.lnMain.setOnTouchListener(new OnTouchListener()
	    {

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
		    // TODO Auto-generated method stub // True means the event
		    // is ignored by the overlayed views
		    // Point pt = new Point( (int)event.getX(),
		    // (int)event.getY() );
		    // if (event.getAction() == MotionEvent.ACTION_DOWN) {
		    //
		    // } else if (event.getAction() == MotionEvent.ACTION_MOVE)
		    // {
		    // //do stuff, drag the image or whatever
		    //
		    // } else if (event.getAction() == MotionEvent.ACTION_UP) {
		    //
		    // }

		    pcount = event.getPointerCount();
		    DebugReportOnLocat.ln("touch count>>" + pcount);

		    return event.getPointerCount() > 1 ? true : false;
		}
	    });

	    holder.lnMain.setOnClickListener(new OnClickListener()
	    {

		@Override
		public void onClick(View v)
		{
		    if (pcount == 1)
		    {
			if (GroupList.get(position).isOwner())
			{

			    Log.e("#######", "  ID " + GroupList.get(position).getId() + "   " + GroupList.get(position).getTitle());

			    Intent intent = new Intent(mContext, GroupDetailActivity.class);
			    intent.putExtra("gID", GroupList.get(position).getId()).putExtra("Name", GroupList.get(position).getTitle());
			    // CommentActivity.GroupId=
			    // GroupList.get(position).getId();
			    // CommentActivity.GroupName=
			    // GroupList.get(position).getTitle();
			    mContext.startActivityForResult(intent, Dashboard.SWITCH_FRAGMENT);

			}
			else
			{
			    if (GroupList.size() > position)
			    {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
				alertDialog.setCancelable(false);
				alertDialog.setMessage(mContext.getResources().getString(R.string.sure_subscribe) + " " + GroupList.get(position).getTitle() + " ?");
				alertDialog.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
				{
				    public void onClick(DialogInterface dialog, int which)
				    {

					new JoinAsync(position).execute();

					dialog.cancel();
				    }
				});
				alertDialog.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener()
				{
				    public void onClick(DialogInterface dialog, int which)
				    {
					dialog.cancel();
				    }
				});
				alertDialog.show();
			    }

			}
		    }
		}
	    });

	} catch (Exception e)
	{
	    DebugReportOnLocat.e(e);
	}

	return convertView;
    }

    private class ViewHolder
    {
	TextView txt_groupName, txt_groupdetail;
	LinearLayout lnMain;
	TextView txt_NoofUser;
    }

    public class JoinAsync extends AsyncTask<Void, String, Void>
    {

	ProgressDialog pDialog;
	String respStr = null;
	int pos;

	public JoinAsync(int position)
	{
	    this.pos = position;
	}

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();

	    try
	    {

		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage(mContext.getString(R.string.please_wait));
		pDialog.show();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }

	}

	@Override
	protected Void doInBackground(Void... params)
	{

	    HttpClient httpclient = new DefaultHttpClient();
	    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
	    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/join");

	    DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " + Constants.NxtAcId);

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    try
	    {

		if (GroupList.size() > pos)
		{

		    nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
		    nameValuePairs.add(new BasicNameValuePair("groupID", GroupList.get(pos).getId()));
		    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    respStr = EntityUtils.toString(response.getEntity());

		}

		DebugReportOnLocat.e("##############", "  responce from post subscribe -->  " + respStr);

	    } catch (ClientProtocolException e)
	    {
	    } catch (IOException e)
	    {
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    super.onPostExecute(result);

	    try
	    {
		if (pDialog != null)
		    pDialog.dismiss();

	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);

	    }
	    ;
	    if (respStr != null)
	    {
		try
		{
		    JSONObject jobj = new JSONObject(respStr);
		    if (jobj.optString("status").equalsIgnoreCase("true"))
		    {

			if (GroupList.size() > pos)
			{

			    GroupList.remove(pos);
			    notifyDataSetChanged();
			}

		    }
		} catch (JSONException e)
		{
		    DebugReportOnLocat.e(e);
		} catch (IndexOutOfBoundsException e)
		{
		    DebugReportOnLocat.e(e);
		}
	    }

	}

    }

    // interface CallBackPublicnterface
    // {
    // public void CallBackMethod();
    //
    // }

}
