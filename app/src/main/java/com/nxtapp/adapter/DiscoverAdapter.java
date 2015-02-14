package com.nxtapp.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextapp.tasks.CallPostWebseviceTask;
import com.nextapp.tasks.DecryptImageLoad;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class DiscoverAdapter extends BaseAdapter
{
    Context context;
    private ArrayList<FriendRequest> DiscoverList = new ArrayList<FriendRequest>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public DiscoverAdapter(FragmentActivity activity, ArrayList<FriendRequest> discoverList2)
    {

        this.context = activity;
        this.DiscoverList = discoverList2;

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

    private class ViewHolder
    {
        TextView txt_userName, txt_userStatus;
        ImageView ivProfile;
        LinearLayout lnInvite;
        // Button btnAdd;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        try
        {

            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.discoverlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_name);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_status);
                holder.ivProfile = (ImageView) convertView.findViewById(R.id.imageview_discoverlist_item);
                holder.lnInvite = (LinearLayout) convertView.findViewById(R.id.ln_Discover_Invite);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(DiscoverList.get(position).getUserName());
            holder.txt_userStatus.setText(DiscoverList.get(position).getUserStatus());

            //Avatar decrypt code commented now
			/*DecryptImageLoad decryptAsynk=new DecryptImageLoad(context,DiscoverList.get(position).getAvatarImage(),holder.ivProfile,true);
			
			    decryptAsynk.execute();*/


            if(DiscoverList.get(position).getAvatarImage()==null){

            }else{

                imageLoader.displayImage(DiscoverList.get(position).getAvatarImage(), holder.ivProfile, circularOptions);
            }

            holder.lnInvite.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    if (DiscoverList.size() != position && DiscoverList.size() > position)
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage(context.getResources().getString(R.string.sure_add) + " " + DiscoverList.get(position).getUserName() + "?");
                        alertDialog.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {

                                if (DiscoverList.size() != position && DiscoverList.size() > position)
                                {

                                    WebCall(position);
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
                    }

                }
            });

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        return convertView;
    }

    @Override
    public int getCount()
    {

        return DiscoverList.size();
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

    private void WebCall(final int pos)
    {

        try
        {

            DebugReportOnLocat.e("DiscoverAdapter", " FR Constants.NxtAcId  :  " + Constants.NxtAcId);

            if (DiscoverList.size() > pos)
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("nxtID", Constants.NxtAcId));
                nameValuePairs.add(new BasicNameValuePair("contacts", DiscoverList.get(pos).getUserId()));
                nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

                DebugReportOnLocat.ln("nxtID-->>"+Constants.NxtAcId);
                DebugReportOnLocat.ln("contacts-->>"+DiscoverList.get(pos).getUserId());
                DebugReportOnLocat.ln("key-->>"+Constants.ParamKey);

                CallPostWebseviceTask task = new CallPostWebseviceTask(context, true, "addcontacts", nameValuePairs)
                {
                    @Override
                    protected void onPostExecute(String result)
                    {

                        super.onPostExecute(result);

                        try
                        {

                            if (DiscoverList.size() > pos)
                            {

                                DiscoverList.remove(pos);
                                notifyDataSetChanged();
                            }

                        } catch (Exception e)
                        {
                            DebugReportOnLocat.e(e);
                        }
                    }
                };

                task.execute();
            }

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }
    }
}
