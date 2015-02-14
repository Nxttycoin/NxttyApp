package com.nxtapp.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;

public class UniversityAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable
{
    private String TAG = "UniversityAutoCompleteAdapter";
    private ArrayList<String> listOfUniversity;
    //private Context mContext;

    public UniversityAutoCompleteAdapter(Context context, int resource)
    {
	super(context, resource);
	//this.mContext = context;
    }

    @Override
    public int getCount()
    {
	// TODO Auto-generated method stub
	return listOfUniversity.size();
    }

    @Override
    public String getItem(int position)
    {
	// TODO Auto-generated method stub
	return listOfUniversity.get(position);
    }

    @Override
    public Filter getFilter()
    {

	Filter filter = new Filter()
	{

	    @Override
	    protected FilterResults performFiltering(CharSequence constraint)
	    {
		// TODO Auto-generated method stub

		FilterResults filterResults = new FilterResults();
		if (constraint != null)
		{
		    DebugReportOnLocat.e(TAG, "letter=>" + constraint.toString());
		    // Retrieve the autocomplete results.
		    try
		    {
			
			fetchUniversityList(constraint.toString());
		    } catch (ClientProtocolException e)
		    {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
		    } catch (IOException e)
		    {
			// TODO Auto-generated catch block
			 DebugReportOnLocat.e(e);
		    }

		    // Assign the data to the FilterResults
		    filterResults.values = listOfUniversity;
		    filterResults.count = listOfUniversity.size();
		}
		return filterResults;
	    }

	    @Override
	    protected void publishResults(CharSequence constraint, FilterResults results)
	    {

		if (results != null && results.count > 0)
		{
		    notifyDataSetChanged();
		}
		else
		{
		    notifyDataSetInvalidated();
		}

	    }

	};

	return filter;
    }

    JSONArray jsonArray = null;

//    private JSONArray callWSSearchUniversities(String typeText)
//    {
//
//	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//	nameValuePairs.add(new BasicNameValuePair("query", typeText));
//	nameValuePairs.add(new BasicNameValuePair("keyword", "false"));
//	CallPostWebseviceTask task = new CallPostWebseviceTask(context, false, "search_universities", nameValuePairs)
//	{
//	    @Override
//	    protected void onPostExecute(String result)
//	    {
//		// TODO Auto-generated method stub
//		super.onPostExecute(result);
//		 DebugReportOnLocat.e("callWSSearchUniversities", "result=>" + result);
//
//		try
//		{
//		    jsonArray = new JSONArray(result);
//		} catch (JSONException e)
//		{
//		    // TODO Auto-generated catch block
//		    e.printStackTrace();
//		}
//
//	    }
//	};
//	task.execute();
//	return jsonArray;
//    }

    private ArrayList<String> fetchUniversityList(String letter) throws ClientProtocolException, IOException
    {

	HttpParams myParams = new BasicHttpParams();
	HttpConnectionParams.setConnectionTimeout(myParams, 10000);
	HttpConnectionParams.setSoTimeout(myParams, 10000);
	DefaultHttpClient httpClient = new DefaultHttpClient(myParams);
	//ResponseHandler<String> res = new BasicResponseHandler();
	HttpPost httpPost = new HttpPost(Constants.BASE_SUBSCRIBER_URL + "search_universities");

	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("query", letter));
	nameValuePairs.add(new BasicNameValuePair("keyword", "false"));

	if (nameValuePairs != null)
	{
	    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	}

	HttpResponse response = httpClient.execute(httpPost);
	String result = EntityUtils.toString(response.getEntity());
	
//	 DebugReportOnLocat.e(TAG, "result=>"+result);
	listOfUniversity = new ArrayList<String>();
	try
	{
	    JSONArray jsonArray = new JSONArray(result);
	    for (int i = 0; i < jsonArray.length(); i++)
	    {
//		  DebugReportOnLocat.e("callWSSearchUniversities", "university=>"
//		 + jsonArray.getString(i));
		String uni = jsonArray.getString(i);
		listOfUniversity.add(uni);
	    }
	} catch (JSONException e)
	{
	    // TODO Auto-generated catch block
	    DebugReportOnLocat.e(e);
	}

	return listOfUniversity;
    }

}
