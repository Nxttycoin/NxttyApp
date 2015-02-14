package com.nxtapp.adapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.CommentModel;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.main.R;

@SuppressLint({ "InflateParams", "SimpleDateFormat", "ShowToast" })
public class CommentAdapter extends BaseAdapter
{

    Context mContext;
    private ArrayList<CommentModel> CommentList = new ArrayList<CommentModel>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public static  LinearLayout wrapper_newsfeedlist;

    OnEditcommentPost editPost;
    public CommentAdapter(Context context, ArrayList<CommentModel> CommentList)
    {
        this.mContext = context;
        editPost = (OnEditcommentPost) context;
        this.CommentList = CommentList;
        imageLoader = ImageLoader.getInstance();

        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtility.maskingRoundBitmap(bitmap);
                    }
                })
                .build();
    }

    @Override
    public int getCount()
    {
        return CommentList.size();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.commentlist_item, parent, false);
            holder = new ViewHolder();
            holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_Commentname);
            holder.txt_CommetnTime = (TextView) convertView.findViewById(R.id.textview_Commenttime);
            holder.txtComment = (TextView) convertView.findViewById(R.id.textview_Commnetdescripltion);
            holder.txtComment.setMovementMethod(LinkMovementMethod.getInstance());

            holder.ln_Editfeed=(LinearLayout)convertView.findViewById(R.id.ln_Editfeed);

            holder.wrapper_newsfeedlist=(LinearLayout)convertView.findViewById(R.id.wrapper_newsfeedlist);


            holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_Commentlist_item);
            holder.lnDelete = (LinearLayout) convertView.findViewById(R.id.ln_delete_comment);
            holder.lnTip = (LinearLayout) convertView.findViewById(R.id.ln_tip_comment);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ln_Editfeed.setTag(convertView);

        holder.txt_userName.setText(CommentList.get(position).getName());
        holder.txt_CommetnTime.setText(Diff(CommentList.get(position).getTimestamp()));

        String decryptMsg = StringEscapeUtils.unescapeJava(CommentList.get(position).getBody());

        DebugReportOnLocat.e("OpenChat", "decrypt msg escape=>" + decryptMsg);
        DebugReportOnLocat.ln(" Comment ID >> "+CommentList.get(position).getCommentID());

        holder.txtComment.setText(decryptMsg);

        // DebugReportOnLocat.e("######", "  Comment Image :  " +
        // CommentList.get(position).getImage());

        // Log.i("######", "  Comment ID :  " +
        // CommentList.get(position).getId());


        if(CommentList.get(position).getImage()==null){

        }else{

            imageLoader.displayImage(CommentList.get(position).getImage(), holder.ivImage, circularOptions);
        }




        if (CommentList.get(position).getId() != null)
            if (CommentList.get(position).getId().equalsIgnoreCase(Constants.NxtAcId))
            {
                holder.lnDelete.setVisibility(View.VISIBLE);

                holder.ln_Editfeed.setVisibility(View.VISIBLE);

                holder.lnTip.setVisibility(View.GONE);
            }
            else
            {
                holder.ln_Editfeed.setVisibility(View.GONE);

                holder.lnDelete.setVisibility(View.GONE);

                holder.lnTip.setVisibility(View.VISIBLE);
            }


        holder.wrapper_newsfeedlist.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

                editPost.OnEditClick(position,"","",false);
            }
        });



        holder.ln_Editfeed.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                //wrapper_newsfeedlist


                if(wrapper_newsfeedlist!=null)wrapper_newsfeedlist.setBackgroundColor(Color.WHITE);


                View parent = (View)v.getTag();


                wrapper_newsfeedlist=(LinearLayout)parent.findViewById(R.id.wrapper_newsfeedlist);

                wrapper_newsfeedlist.setBackgroundColor(Color.LTGRAY);

                String decryptMsg = StringEscapeUtils.unescapeJava(CommentList.get(position).getBody());

                editPost.OnEditClick(position, decryptMsg,CommentList.get(position).getCommentID(),true);
                //String msg,String commentID
                // TODO Auto-generated method stub

            }
        });
        holder.lnDelete.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setCancelable(false);
                alertDialog.setMessage(mContext.getResources().getString(R.string.sure_delete_comment));
                alertDialog.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                        if (CommentList.size() != position && CommentList.size() > position)
                        {
                            new DeleteCommentAsync(CommentList.get(position).getCommentID(), position).execute();

                        }

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
        });
        holder.lnTip.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // new CommentPostAsync(position).execute();
                if (Network.isNetworkConnected(mContext))
                {

                    new GetAsync_FirstCommentTip(position).execute();
                    new CommentPostAsync(position).execute();
                }
                else
                {
                    Toast.makeText(mContext, "Please check the network connection", Toast.LENGTH_SHORT);
                }

            }
        });

        return convertView;
    }

    private class ViewHolder
    {
        TextView txt_userName, txt_CommetnTime, txtComment;
        ImageView ivImage;
        LinearLayout lnDelete, lnTip,ln_Editfeed,wrapper_newsfeedlist;
    }

    private String Diff(String timestamp)
    {
        long currentTime = System.currentTimeMillis() / 1000;
        long PrevTime = Long.parseLong(timestamp) / 1000;
        long diff = currentTime - PrevTime;
        long diffMinutes = diff / 60;
        if (diffMinutes > 60)
        {
            long Hrs = diffMinutes / 60;
            if (Hrs > 24)
            {
                return new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(PrevTime * 1000);
            }
            else
            {
                return Long.toString(Hrs) + " " + mContext.getResources().getString(R.string.hrs_ago);
            }
        }
        else
        {
            return Long.toString(diffMinutes) + " " + mContext.getResources().getString(R.string.min_ago);
        }
    }

    public class DeleteCommentAsync extends AsyncTask<Void, String, Void>
    {

        String CommentID;
        int pos;
        String responce = null;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            pDialog.show();

        }

        public DeleteCommentAsync(String CommentID, int position)
        {
            this.CommentID = CommentID;
            this.pos = position;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            DebugReportOnLocat.e("DeleteCommentAsync", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);
            // String respStr = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
            HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/delete_comment");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            try
            {
                nameValuePairs.add(new BasicNameValuePair("commentID", CommentID));
                nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                responce = EntityUtils.toString(response.getEntity());
                DebugReportOnLocat.e("##############", "  responce from post -->  " + responce);

            } catch (ClientProtocolException e)
            {
                DebugReportOnLocat.e(e);

            } catch (IOException e)
            {
                DebugReportOnLocat.e(e);
            } catch (IndexOutOfBoundsException e)
            {

                DebugReportOnLocat.e(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (pDialog != null)
                pDialog.dismiss();

            if (responce != null)
            {
                try
                {
                    JSONObject jobj = new JSONObject(responce);
                    if (jobj.optString("status").equalsIgnoreCase("true"))
                    {

                        if (CommentList.size() != pos && CommentList.size() > pos)
                        {

                            CommentList.remove(pos);
                            GroupDetailActivity.ComingFromCreatePost = true;
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

    public class CommentPostAsync extends AsyncTask<Void, String, Void>
    {

        ProgressDialog pDialog;
        String respStr = null;
        int pos;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            try
            {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
                pDialog.show();

            } catch (Exception e)
            {

                DebugReportOnLocat.e(e);
            }

        }

        public CommentPostAsync(int pos)
        {
            this.pos = pos;
        }

        @Override
        protected Void doInBackground(Void... params)
        {

            if (CommentList.size() > pos)
            {

                Log.e("#########", " Post ID :  " + CommentList.get(pos).getCommentID());
                Log.e("#########", " Demo Req for TIP: Another   " + CommentList.get(pos).getId());

                HttpClient httpclient = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/tip_post");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                try
                {
                    nameValuePairs.add(new BasicNameValuePair("tipBy", Constants.NxtAcId));
                    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                    nameValuePairs.add(new BasicNameValuePair("postID", CommentList.get(pos).getCommentID()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    respStr = EntityUtils.toString(response.getEntity());

                    Log.e("##############", "  responce from post -->  " + respStr);

                    // {"status":true,"errorDetail":null}

                } catch (ClientProtocolException e)
                {

                    DebugReportOnLocat.e(e);

                } catch (IOException e)
                {

                    DebugReportOnLocat.e(e);

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
                if (pDialog != null)
                    pDialog.dismiss();
            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

            if (respStr != null)
            {
                try
                {
                    JSONObject jobj = new JSONObject(respStr);
                    if (jobj.optString("status").equalsIgnoreCase("false"))
                    {
                        AlertUtility.showToast(mContext, mContext.getResources().getString(R.string.already_tipped_comment));
                    }
                    else
                    {
                        AlertUtility.showToast(mContext, mContext.getResources().getString(R.string.already_tipped_comment_success));
                    }

                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }
            }
        }

    }

    public class GetAsync_FirstCommentTip extends AsyncTask<Void, String, Void>
    {

        int pos;

        public GetAsync_FirstCommentTip(int pos)
        {
            this.pos = pos;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {

            if (CommentList.size() > pos)
            {
                String respStr = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                HttpPost httppost = new HttpPost(Constants.TipUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                try
                {
                    nameValuePairs.add(new BasicNameValuePair("recipient", CommentList.get(pos).getId()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    respStr = EntityUtils.toString(response.getEntity());

                    Log.e("##############", "  responce from post -->  " + respStr);

                } catch (ClientProtocolException e)
                {
                    DebugReportOnLocat.e(e);

                } catch (IOException e)
                {
                    DebugReportOnLocat.e(e);

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }

    }



    public interface OnEditcommentPost
    {
        public void OnEditClick(int position,String msg,String commentID,boolean isEdit);
    }

}
