package com.nxtapp.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextapp.tasks.CallPostWebseviceTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.Chat2Activity;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class ContactAdapter extends BaseAdapter
{

    Context mContext;
    private ArrayList<FriendRequest> ContactList = new ArrayList<FriendRequest>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public ContactAdapter(Context context, ArrayList<FriendRequest> friendReqList2)
    {
        this.mContext = context;
        this.ContactList = friendReqList2;
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
        return ContactList.size();
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
                convertView = mInflater.inflate(R.layout.contactlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_friendname);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_friendstatus);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_friendrequestlist_item);
                holder.lnBlock = (LinearLayout) convertView.findViewById(R.id.ln_block);
                holder.lnLayout = (LinearLayout) convertView.findViewById(R.id.ln_contact_mainLayout);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(ContactList.get(position).getUserName());
            String unicodedMsg = ContactList.get(position).getUserStatus();
            String status = StringEscapeUtils.unescapeJava(unicodedMsg);
            // Log.e("SettingFragment", "status=>" + status);
            if (status != null && !status.equalsIgnoreCase("null"))
            {
                holder.txt_userStatus.setText(status);
            }

            if(ContactList.get(position).getAvatarImage()==null){

                holder.ivImage.setImageResource(R.drawable.loading);

            }else{

                imageLoader.displayImage(ContactList.get(position).getAvatarImage(), holder.ivImage, circularOptions);
            }

            holder.lnBlock.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (ContactList.size() != position && ContactList.size() > position)
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage(mContext.getResources().getString(R.string.sure_block) + " " + ContactList.get(position).getUserName() + " ?");
                        alertDialog.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (ContactList.size() > position)
                                {
                                    WebCall(position);
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

                }
            });

            holder.lnLayout.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub

                    try
                    {

                        if (ContactList.size() > position)
                        {

                            mContext.startActivity(new Intent(mContext, Chat2Activity.class).putExtra("contactID", ContactList.get(position).getUserId())
                                    .putExtra("Name", ContactList.get(position).getUserName()).putExtra("Avtar", ContactList.get(position).getAvatarImage()));

			    /*
			     * mContext.startActivity(new Intent(mContext,
			     * Chat2Activity.class).putExtra("contactID",
			     * ContactList
			     * .get(position).getUserId()).putExtra("Name",
			     * ContactList.get(position).getUserName()));
			     */}

                    } catch (Exception e)
                    {
                        DebugReportOnLocat.e(e);
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
        TextView txt_userName, txt_userStatus;
        ImageView ivImage;

        LinearLayout lnLayout, lnBlock;
    }

    private void WebCall(final int pos)
    {

        if (ContactList.size() > pos)
        {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
            nameValuePairs.add(new BasicNameValuePair("contactID", ContactList.get(pos).getUserId()));
            nameValuePairs.add(new BasicNameValuePair("status", "2"));
            nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

            CallPostWebseviceTask task = new CallPostWebseviceTask(mContext, true, "update_friendship", nameValuePairs)
            {
                @Override
                protected void onPostExecute(String result)
                {
                    // TODO Auto-generated method stub
                    super.onPostExecute(result);

                    try
                    {

                        if (ContactList.size() > pos)
                        {

                            ContactList.remove(pos);
                            notifyDataSetChanged();
                        }

                    } catch (IndexOutOfBoundsException e)
                    {
                        DebugReportOnLocat.e(e);
                    } catch (Exception e)
                    {
                        DebugReportOnLocat.e(e);
                    }

                }
            };

            task.execute();

        }

    }

}
