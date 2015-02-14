package com.nxtapp.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.GroupDetailModel;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.CommentActivity;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.lib.imagezoom.ImageViewTouch;
import com.nxtty.lib.imagezoom.ImageViewTouch.OnBitmapLongClickListener;
import com.nxtty.lib.imagezoom.ImageViewTouchBase;
import com.nxtty.main.R;

public class GroupDetailAdapter extends BaseAdapter
{

    Activity mContext;
    public ArrayList<GroupDetailModel> GroupDetailList = new ArrayList<GroupDetailModel>();
    ImageLoader imageLoader;
    DisplayImageOptions options, circularDisplayOptions;
    String groupID;
    OnEditPost editPost;
    int pos;
    String From = "Detail";

    public GroupDetailAdapter(Activity context, ArrayList<GroupDetailModel> gROUPDETAILS_LIST, String groupID)
    {
        this.mContext = context;
        editPost = (OnEditPost) context;
        this.groupID = groupID;
        this.GroupDetailList = gROUPDETAILS_LIST;
        imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .showImageOnFail(R.drawable.loading_white)
                .build();

        circularDisplayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtility.maskingRoundBitmap(bitmap);
                    }
                })
                .showImageOnFail(R.drawable.loading_white)
                .build();
    }

    public GroupDetailAdapter(Activity context, ArrayList<GroupDetailModel> gROUPDETAILS_LIST, String groupID, String From)
    {
        this.mContext = context;
        // editPost = (OnEditPost) context;
        this.groupID = groupID;
        this.GroupDetailList = gROUPDETAILS_LIST;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Bitmap result = BitmapUtility.maskingRoundBitmap(bitmap);
                        return result;
                    }
                })
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .showImageOnFail(R.drawable.loading_white).build();

        circularDisplayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtility.maskingRoundBitmap(bitmap);
                    }
                })
                .showImageOnFail(R.drawable.loading_white)
                .build();
        From = "Search";
    }

    @Override
    public int getCount()
    {
        return GroupDetailList.size();
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

        try
        {

            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.groupdetail_list_row, parent, false);
                holder = new ViewHolder();
                holder.txtName = (TextView) convertView.findViewById(R.id.textview_groupfeedname);
                holder.txtTime = (TextView) convertView.findViewById(R.id.textview_groupfeedtime);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.textview_groupfeedtitle);
                holder.txtBody = (TextView) convertView.findViewById(R.id.textview_groupfeedbody);

                holder.txtComments1 = (TextView) convertView.findViewById(R.id.txt_seeCommets1);
                holder.txtComments2 = (TextView) convertView.findViewById(R.id.txt_seeCommets2);
                holder.txtComments3 = (TextView) convertView.findViewById(R.id.txt_seeCommets3);

                holder.txtComcount1 = (TextView) convertView.findViewById(R.id.txt_seeCommetsCounter1);
                holder.txtComcount2 = (TextView) convertView.findViewById(R.id.txt_seeCommetsCounter2);
                holder.txtComcount3 = (TextView) convertView.findViewById(R.id.txt_seeCommetsCounter3);

                holder.txtTipCount = (TextView) convertView.findViewById(R.id.txt_tipCount);

                holder.txtBody.setMovementMethod(LinkMovementMethod.getInstance());

                holder.lnUser = (LinearLayout) convertView.findViewById(R.id.ln_UserLayout);
                holder.lnOwner = (LinearLayout) convertView.findViewById(R.id.ln_OwnerLayout);

                holder.ivMain = (ImageView) convertView.findViewById(R.id.iv_groupfeed_mainImage);
                holder.ivProfile = (ImageView) convertView.findViewById(R.id.iv_groupfeed_profImage);
                holder.ivCountIcon = (ImageView) convertView.findViewById(R.id.ivCountIcon);

                holder.lnReportPost = (LinearLayout) convertView.findViewById(R.id.ln_reportPost);
                holder.lnReportUser = (LinearLayout) convertView.findViewById(R.id.ln_reportuser);
                holder.lnEdit = (LinearLayout) convertView.findViewById(R.id.ln_Editfeed);
                holder.lnDelete = (LinearLayout) convertView.findViewById(R.id.ln_Deletefeed);

                holder.lnTip = (LinearLayout) convertView.findViewById(R.id.ln_tip);

                // holder.lnHide = (LinearLayout)
                // convertView.findViewById(R.id.ln_Hidefeed);

                holder.lnHidePost = (LinearLayout) convertView.findViewById(R.id.ln_NewsroomLayout);

                holder.lnSeeComments1 = (LinearLayout) convertView.findViewById(R.id.ln_SeeComments1);
                holder.lnSeeComments2 = (LinearLayout) convertView.findViewById(R.id.ln_SeeComments2);
                holder.lnSeeComments3 = (LinearLayout) convertView.findViewById(R.id.ln_SeeComments3);
                // holder.wvGif = (WebView)
                // convertView.findViewById(R.id.wvGif);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtName.setText(GroupDetailList.get(position).getNameAlias());
            holder.txtTime.setText(Diff(GroupDetailList.get(position).getCreated()));
            String decryptMsg = StringEscapeUtils.unescapeJava(GroupDetailList.get(position).getBody());

            // holder.txtBody.setText(GroupDetailList.get(position).getBody());


            try
            {

                if(GroupDetailList.get(position).getTitle().length()==0){

                    holder.txtTitle.setVisibility(View.GONE);
                }else{
                    holder.txtTitle.setVisibility(View.VISIBLE);
                    String decryptTitle = StringEscapeUtils.unescapeJava(GroupDetailList.get(position).getTitle());
                    // Log.e("OpenChat", "decrypt msg escape=>" + decryptMsg);



                    holder.txtTitle.setText(Html.fromHtml(decryptTitle));


                }




            } catch (Exception e)
            {
                // TODO: handle exception
            }



            try
            {

                if(decryptMsg.length()==0){

                    holder.txtBody.setText(decryptMsg);

                    holder.txtBody.setVisibility(View.GONE);

                }else{

                    holder.txtBody.setVisibility(View.VISIBLE);
                    holder.txtBody.setText(decryptMsg);
                }



            } catch (Exception e)
            {
                // TODO: handle exception
            }


            // holder.txtComments1.setText("Comments " +
            // GroupDetailList.get(position).getCommentCount());
            // holder.txtComments2.setText("Comments " +
            // GroupDetailList.get(position).getCommentCount());
            // holder.txtComments3.setText("Comments " +
            // GroupDetailList.get(position).getCommentCount());

            holder.txtComments1.setText(mContext.getResources().getString(R.string.comments));
            holder.txtComments2.setText(mContext.getResources().getString(R.string.comments));
            holder.txtComments3.setText(mContext.getResources().getString(R.string.comments));

            holder.txtComcount1.setText(GroupDetailList.get(position).getCommentCount());
            holder.txtComcount2.setText(GroupDetailList.get(position).getCommentCount());
            holder.txtComcount3.setText(GroupDetailList.get(position).getCommentCount());

            if (GroupDetailList.get(position).isOwner())
            {
                holder.lnOwner.setVisibility(View.VISIBLE);
                holder.lnUser.setVisibility(View.GONE);
                holder.lnHidePost.setVisibility(View.GONE);
                holder.lnTip.setVisibility(View.VISIBLE);
                holder.txtTipCount.setVisibility(View.VISIBLE);
                holder.ivCountIcon.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.lnOwner.setVisibility(View.GONE);
                holder.lnTip.setVisibility(View.VISIBLE);
                holder.txtTipCount.setVisibility(View.VISIBLE);
                holder.ivCountIcon.setVisibility(View.VISIBLE);

                if (GroupDetailList.get(position).getNameAlias().equalsIgnoreCase("NEWSROOM"))
                {
                    holder.lnHidePost.setVisibility(View.VISIBLE);
                    holder.lnUser.setVisibility(View.GONE);
                    holder.lnTip.setVisibility(View.GONE);
                    holder.txtTipCount.setVisibility(View.GONE);
                    holder.ivCountIcon.setVisibility(View.GONE);
                }
                else
                {
                    holder.lnUser.setVisibility(View.VISIBLE);
                    holder.lnHidePost.setVisibility(View.GONE);

                    if (!GroupDetailList.get(position).getTipCount().trim().equals("0"))
                        holder.txtTipCount.setText(GroupDetailList.get(position).getTipCount());

                }
            }

            if (GroupDetailList.get(position).getImage() != null && GroupDetailList.get(position).getImage().length() > 5)
            {

                imageLoader.displayImage(GroupDetailList.get(position).getImagePath(), holder.ivMain, options);
                // holder.wvGif.getSettings().setBuiltInZoomControls(true);
                // holder.wvGif.loadUrl(GroupDetailList.get(position).getImage());
                holder.ivMain.setVisibility(View.VISIBLE);

            }
            else
            {

                //R.drawable.loading_white

                holder.ivMain.setImageResource(R.drawable.loading_white);
                holder.ivMain.setVisibility(View.GONE);
            }

            holder.ivMain.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e("############", "  Clicked  ");
                    // GroupDetailList.get(position).getImagePath()
                    openDialogPinchZoom(GroupDetailList.get(position).getImagePath());

                }
            });

            // code commented on oct 07 from downloaded source code(oct 06)
            // if (GroupDetailList.get(position).getAvatar() != null &&
            // GroupDetailList.get(position).getAvatar().length() > 5
            // && !GroupDetailList.get(position).getAvatar().contains("null"))
            // {
            // Log.e("OpenChat", "avtar=>" +
            // GroupDetailList.get(position).getAvatar());

            if(GroupDetailList.get(position).getAvatar()!=null){

                imageLoader.displayImage(GroupDetailList.get(position).getAvatar(), holder.ivProfile, circularDisplayOptions);

            }else{

                holder.ivProfile.setImageResource(R.drawable.loading_white);
            }


            // holder.ivProfile.setVisibility(View.VISIBLE);

            // }
            // else
            // {
            // // holder.ivProfile.setVisibility(View.INVISIBLE);
            // }

            holder.lnReportPost.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setMessage(mContext.getString(R.string.sure_report));
                    alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("reportedBy", Constants.NxtAcId));
                            nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                            nameValuePairs.add(new BasicNameValuePair("postID", GroupDetailList.get(position).getId()));
                            new GetAsyncPublicChat("report_post", nameValuePairs, GroupDetailList.get(position).getId(), position).execute();
                        }
                    });
                    alertDialog.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            });
            holder.lnReportUser.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setMessage(mContext.getString(R.string.sure_report_user));
                    alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                            nameValuePairs.add(new BasicNameValuePair("reportedBy", Constants.NxtAcId));
                            nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                            nameValuePairs.add(new BasicNameValuePair("reportedTo", GroupDetailList.get(position).getNxtAccountId()));
                            nameValuePairs.add(new BasicNameValuePair("groupID", groupID));
                            new GetAsyncPublicChat("report_member", nameValuePairs, GroupDetailList.get(position).getId(), position).execute();

                        }
                    });
                    alertDialog.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }
            });
            holder.lnEdit.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    if (!From.equalsIgnoreCase("Search"))
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                        alertDialog.setMessage(mContext.getString(R.string.sure_edit));
                        alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();

                                try
                                {

                                    if (GroupDetailList.size() != position && GroupDetailList.size() > position)
                                    {

                                        editPost.OnEditClick(position, GroupDetailList.get(position).getTitle(), GroupDetailList.get(position).getBody(),
                                                GroupDetailList.get(position).getId(), GroupDetailList.get(position).getImage());
                                    }

                                } catch (Exception e)
                                {
                                    DebugReportOnLocat.e(e);
                                }

                            }
                        });
                        alertDialog.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {

                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                }
            });
            holder.lnDelete.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(mContext.getString(R.string.sure_delete_post));
                    alertDialog.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();

                            if (Network.isNetworkConnected(mContext))
                            {

                                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                                try
                                {

                                    if (GroupDetailList.size() > position)
                                    {

                                        nameValuePairs.add(new BasicNameValuePair("postID", GroupDetailList.get(position).getId()));
                                        nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                                        pos = position;
                                        new GetAsyncPublicChat("delete_post", nameValuePairs).execute();

                                    }

                                } catch (Exception e)
                                {
                                    DebugReportOnLocat.e(e);
                                }
                            }
                            else
                            {
                                AlertUtility.showToast(mContext, mContext.getString(R.string.networkIssues));
                            }
                        }
                    });
                    alertDialog.setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }
            });

            // holder.lnHide.setOnClickListener(new OnClickListener()
            // {
            //
            // @Override
            // public void onClick(View v)
            // {
            // AlertDialog.Builder alertDialog = new
            // AlertDialog.Builder(mContext);
            // alertDialog.setCancelable(false);
            // alertDialog.setMessage(mContext.getString(R.string.sure_hide_post));
            // alertDialog.setPositiveButton(mContext.getString(R.string.yes),
            // new DialogInterface.OnClickListener()
            // {
            // public void onClick(DialogInterface dialog, int which)
            // {
            // dialog.cancel();
            //
            //
            // try
            // {
            // if( GroupDetailList.size()>pos){
            //
            // GroupDetailList.remove(pos);
            // }
            //
            // notifyDataSetChanged();
            //
            //
            // } catch (Exception e)
            // {
            // DebugReportOnLocat.e(e);
            // }
            // }
            // });
            // alertDialog.setNegativeButton(mContext.getString(R.string.no),
            // new DialogInterface.OnClickListener()
            // {
            // public void onClick(DialogInterface dialog, int which)
            // {
            //
            // dialog.cancel();
            // }
            // });
            // alertDialog.show();
            //
            // }
            // });
            holder.lnSeeComments1.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pos = position;
                    onClickofComment();
                }
            });
            holder.lnSeeComments2.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pos = position;
                    onClickofComment();
                }
            });
            holder.lnSeeComments3.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pos = position;
                    onClickofComment();
                }
            });
            holder.lnTip.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    pos = position;

                    new OnTipClickFirst().execute();
                    new GetAsync().execute();
                }
            });

            holder.ivProfile.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    pos = position;
                    Intent intent = new Intent();
                    intent.putExtra("comingFrom", "openChat");
                    intent.putExtra("NameAlias", GroupDetailList.get(position).getNameAlias());
                    mContext.setResult(Activity.RESULT_OK, intent);
                    mContext.finish();
                }
            });
            // /commented by bala
	    /*
	     * holder.txtName.setOnClickListener(new OnClickListener() {
	     * 
	     * @Override public void onClick(View v) { pos = position; Intent
	     * intent = new Intent(); intent.putExtra("comingFrom", "openChat");
	     * intent.putExtra("NameAlias",
	     * GroupDetailList.get(position).getNameAlias());
	     * mContext.setResult(Activity.RESULT_OK, intent);
	     * mContext.finish();
	     * 
	     * } });
	     * 
	     * holder.txtTitle.setOnClickListener(new OnClickListener() {
	     * 
	     * @Override public void onClick(View v) { if
	     * (GroupDetailList.get(position).getUrl() != null &&
	     * !GroupDetailList.get(position).getUrl().equalsIgnoreCase("") &&
	     * !GroupDetailList.get(position).getUrl().equalsIgnoreCase("null"))
	     * { String url = GroupDetailList.get(position).getUrl();
	     * Log.e("GroupDetailAdapetr", ""); Intent i = new
	     * Intent(Intent.ACTION_VIEW); i.setData(Uri.parse(url));
	     * mContext.startActivity(i); } } });
	     * holder.txtBody.setOnClickListener(new OnClickListener() {
	     * 
	     * @Override public void onClick(View v) {
	     * 
	     * if (GroupDetailList.get(position).getUrl() != null &&
	     * !GroupDetailList.get(position).getUrl().equalsIgnoreCase("") &&
	     * !GroupDetailList.get(position).getUrl().equalsIgnoreCase("null"))
	     * { String url = GroupDetailList.get(position).getUrl(); Intent i =
	     * new Intent(Intent.ACTION_VIEW); i.setData(Uri.parse(url));
	     * mContext.startActivity(i); } else { pos = position;
	     * onClickofComment(); }
	     * 
	     * } });
	     */

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        return convertView;
    }

    private class ViewHolder
    {
        TextView txtName, txtTime, txtBody, txtTitle, txtComments1, txtComments2, txtComments3, txtComcount1, txtComcount2, txtComcount3, txtTipCount;
        LinearLayout lnUser, lnOwner, lnReportPost, lnReportUser, lnEdit, lnDelete, lnHide, lnHidePost, lnSeeComments1, lnSeeComments2, lnSeeComments3, lnTip;
        ImageView  ivMain, ivCountIcon,ivProfile;
        // WebView wvGif;
    }

    private String deletePost(int position)
    {
        String respStr = null;
        if (Network.isNetworkConnected(mContext))
        {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            try
            {

                if (GroupDetailList.size() > position)
                {

                    nameValuePairs.add(new BasicNameValuePair("postID", GroupDetailList.get(position).getId()));
                    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                    pos = position;

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                    HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/delete_post");
                    try
                    {

                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        respStr = EntityUtils.toString(response.getEntity());
                        Log.e("##############", "delete message -->  " + respStr);
                        try
                        {
                            JSONObject jobj = new JSONObject(respStr);
                            String Message = jobj.optString("errorDetail");
                        } catch (JSONException e)
                        {
                            DebugReportOnLocat.e(e);
                        }

                    } catch (ClientProtocolException e)
                    {
                    } catch (IOException e)
                    {
                    }

                }

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }
        }
        else
        {
            mContext.runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {
                    AlertUtility.showToast(mContext, mContext.getString(R.string.networkIssues));
                }
            });
        }
        return respStr;
    }

    public class GetAsyncPublicChat extends AsyncTask<Void, String, String>
    {

        ProgressDialog pDialog;
        String WS, Key, Val;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String Message = "", GroupDetailListId = "";
        int positionOfItem;

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

        public GetAsyncPublicChat(String From, List<NameValuePair> Param)
        {
            this.WS = From;
            this.nameValuePairs = Param;

        }

        public GetAsyncPublicChat(String From, List<NameValuePair> nameValuePairs2, String id, int position)
        {
            // TODO Auto-generated constructor stub
            this.WS = From;
            this.nameValuePairs = nameValuePairs2;
            this.GroupDetailListId = id;
            this.positionOfItem = position;
        }

        @Override
        protected String doInBackground(Void... params)
        {

            String respStr = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
            HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/" + WS);
            try
            {

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                respStr = EntityUtils.toString(response.getEntity());
                Log.e("##############", "  responce from post -->  " + respStr);
                try
                {
                    JSONObject jobj = new JSONObject(respStr);
                    Message = jobj.optString("errorDetail");
                } catch (JSONException e)
                {
                    DebugReportOnLocat.e(e);
                }

            } catch (ClientProtocolException e)
            {
            } catch (IOException e)
            {
            }

            if (!WS.equalsIgnoreCase("delete_post"))
            {
                respStr = deletePost(positionOfItem);
            }

            return respStr;
        }

        @Override
        protected void onPostExecute(String result)
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

            if (Message.length() > 5)
            {
                Toast.makeText(mContext, Message, Toast.LENGTH_LONG).show();
            }
            else
            {
                // Toast.makeText(mContext, "Success",
                // Toast.LENGTH_LONG).show();
                // if (WS.equalsIgnoreCase("delete_post"))
                // {
                try
                {
                    if ((result != null) && (!result.equalsIgnoreCase("null")))
                    {

                        if (result.length() > 0)
                        {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("status").equalsIgnoreCase("true"))
                            {
                                if (GroupDetailList.size() > pos)
                                {

                                    GroupDetailList.remove(pos);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e)
                {

                    DebugReportOnLocat.e(e);
                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);
                }
                // }
            }
        }

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
            DebugReportOnLocat.ln(Hrs + " Hrs, ");
            if (Hrs > 24)
            {

                return new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(PrevTime * 1000);
            }
            else
            {
                return Long.toString(Hrs) + " " + mContext.getString(R.string.hrs_ago);
            }
        }
        else
        {
            return Long.toString(diffMinutes) + " " + mContext.getString(R.string.min_ago);
        }

    }

    protected void showShareLinkDialog(final String link)
    {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);
        ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
        lv.setAdapter(new ArrayAdapter(mContext, R.layout.row, mContext.getResources().getStringArray(R.array.share_link_option)));
        lv.setOnItemClickListener(new OnItemClickListener()
        {

            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (position == 0)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mContext.startActivity(browserIntent);
                }
                else if (position == 1)
                {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, link);
                    mContext.startActivity(Intent.createChooser(share, mContext.getResources().getString(R.string.share_with_frnds)));
                }
                else if (position == 2)
                {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB)
                    {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(link);
                    }
                    else
                    {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("link", link);
                        clipboard.setPrimaryClip(clip);
                    }

                    AlertUtility.showToast(mContext, "Copied");
                }
            }
        });
        dialog.show();
    }

    public class GetAsync extends AsyncTask<Void, String, Void>
    {

        ProgressDialog pDialog;
        String respStr = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            try
            {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
                pDialog.setCancelable(false);
                pDialog.show();

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

        }

        @Override
        protected Void doInBackground(Void... params)
        {

            if (GroupDetailList.size() > pos)
            {

                Log.e("#########", " Post ID :  " + GroupDetailList.get(pos).getId());
                Log.e("#########", " Demo Req for TIP: Another   " + GroupDetailList.get(pos).getNxtAccountId());

                HttpClient httpclient = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/tip_post");
                // HttpPost httppost = new HttpPost(Constants.baseUrl_Group +
                // "groups/is_post_tipped");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                try
                {
                    nameValuePairs.add(new BasicNameValuePair("tipBy", Constants.NxtAcId));
                    nameValuePairs.add(new BasicNameValuePair("postID", GroupDetailList.get(pos).getId()));
                    nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    respStr = EntityUtils.toString(response.getEntity());

                    Log.e("##############", "  responce from post -->  " + respStr);

                    // {"status":true,"errorDetail":null}

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
                    if (jobj.optString("status").equalsIgnoreCase("false"))
                    {
                        AlertUtility.showToast(mContext, mContext.getResources().getString(R.string.already_tipped));
                    }
                    else
                    {
                        AlertUtility.showToast(mContext, mContext.getResources().getString(R.string.already_tipped_success));
                    }

                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }
            }
        }

    }

    public class GetAsync_First extends AsyncTask<Void, String, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params)
        {

            try
            {

                if (GroupDetailList.size() > pos)
                {

                    Log.e("#########", " Demo Req for TIP User  :  " + Constants.NxtAcId);
                    Log.e("#########", " Demo Req for TIP: Another   " + GroupDetailList.get(pos).getNxtAccountId());

                    String respStr = null;

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                    HttpPost httppost = new HttpPost(Constants.TipUrl);

                    // HttpPost httppost = new
                    // HttpPost("http://nxtty.com/1nxttycoinsender.php");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                    try
                    {
                        nameValuePairs.add(new BasicNameValuePair("recipient", GroupDetailList.get(pos).getNxtAccountId()));
                        nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        respStr = EntityUtils.toString(response.getEntity());

                        Log.e("##############", "  responce from post -->  " + respStr);

                    } catch (IndexOutOfBoundsException e)
                    {
                        DebugReportOnLocat.e(e);
                    } catch (ClientProtocolException e)
                    {
                        DebugReportOnLocat.e(e);
                    } catch (IOException e)
                    {
                        DebugReportOnLocat.e(e);
                    }

                }

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }

    }

    private void onClickofComment()
    {

        try
        {
            if (GroupDetailList.size() > pos)
            {

                mContext.startActivity(new Intent(mContext, CommentActivity.class).putExtra("PostID", GroupDetailList.get(pos).getId()));
                CommentActivity.GroupId = GroupDetailList.get(pos).getId();
                CommentActivity.GroupName = "Support";
            }

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);

        }

    }

    public interface OnEditPost
    {
        public void OnEditClick(int pos, String Title, String body, String PostID, String attachedImage);
    }

    public class OnTipClickFirst extends AsyncTask<Void, String, Void>
    {

        // ProgressDialog pDialog;
        String respStr = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // pDialog = new ProgressDialog(mContext);
            // pDialog.setMessage(mContext.getResources().getString(R.string.please_wait));
            // pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params)
        {

            try
            {

                if (GroupDetailList.size() > pos)
                {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
                    HttpPost httppost = new HttpPost("http://www.nxtty.com/1nxttycoinsender.php");
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    try
                    {
                        nameValuePairs.add(new BasicNameValuePair("recipient", GroupDetailList.get(pos).getNxtAccountId()));
                        nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);
                        respStr = EntityUtils.toString(response.getEntity());

                        // Log.e(" Responce  ", "  : " + respStr);

                    } catch (ClientProtocolException e)
                    {
                    } catch (IOException e)
                    {
                    }
                }

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // try
            // {
            // if(pDialog!=null)
            // pDialog.dismiss();
            //
            // } catch (Exception e)
            // {
            // DebugReportOnLocat.e(e);
            //
            // };

        }

    }

    public void openDialogPinchZoom(final String link)
    {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.imagedialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);

        //private ImageViewTouch imageView;
        //ImageView tImageView = (ImageView) dialog.findViewById(R.id.iv_largeImage);
        ImageViewTouch tImageView = (ImageViewTouch) dialog.findViewById(R.id.iv_largeImage);
        tImageView.clear();

        tImageView.onBitmapLongClick(new OnBitmapLongClickListener()
        {

            @Override
            public void onBitmapLongClick(Bitmap bitmap)
            {
                try
                {


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setMessage(R.string.save_image_sdcard).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            new AsyncTask<Void, Void, Void>()
                            {
                                @Override
                                protected Void doInBackground(Void... params)
                                {
                                    SaveImage(link);
                                    return null;
                                }
                            }.execute();

                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    //return false;



                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);
                }

            }
        });
	

	/*dialog.findViewById(R.id.iv_largeImage).setOnLongClickListener(new OnLongClickListener()
	{

	    @Override
	    public boolean onLongClick(View v)
	    {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		alertDialogBuilder.setMessage(R.string.save_image_sdcard).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{
		    public void onClick(DialogInterface dialog, int id)
		    {
			new AsyncTask<Void, Void, Void>()
			{
			    @Override
			    protected Void doInBackground(Void... params)
			    {
				SaveImage(link);
				return null;
			    }
			}.execute();

		    }
		}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{
		    public void onClick(DialogInterface dialog, int id)
		    {

		    }
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

		return false;
	    }
	});*/


        imageLoader.displayImage(link, tImageView, options, new SimpleImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {

                try
                {
                    ((ImageViewTouchBase) view).setImageBitmapReset(loadedImage, true);


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    private void SaveImage(String link)
    {
        File filepath = Environment.getExternalStorageDirectory();
        OutputStream output;
        Bitmap bitmap = getBitmapFromURL(link);
        File dir = new File(filepath.getAbsolutePath() + "/Nxtty Images/");
        dir.mkdirs();
        File file = new File(dir, System.currentTimeMillis() + "nxtty.png");
        try
        {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromURL(String src)
    {
        try
        {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e)
        {
            // Log exception
            return null;
        }
    }

}
