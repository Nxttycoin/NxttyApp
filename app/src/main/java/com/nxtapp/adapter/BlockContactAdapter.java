package com.nxtapp.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class BlockContactAdapter extends BaseAdapter
{

    Context mContext;
    private ArrayList<FriendRequest> blockContactList = new ArrayList<FriendRequest>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOption;

    public BlockContactAdapter(Context context, ArrayList<FriendRequest> blockContactList)
    {
        this.mContext = context;
        this.blockContactList = blockContactList;
        imageLoader = ImageLoader.getInstance();

        circularOption =new DisplayImageOptions.Builder()
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
        return blockContactList.size();
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
                convertView = mInflater.inflate(R.layout.block_contactlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_friendname);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_friendstatus);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_friendrequestlist_item);
                holder.btnDelete = (Button) convertView.findViewById(R.id.button_Delete);
                holder.btnUnblock = (Button) convertView.findViewById(R.id.button_unBlock);
                holder.lnLayout = (LinearLayout) convertView.findViewById(R.id.ln_contact_mainLayout);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(blockContactList.get(position).getUserName());
            holder.txt_userStatus.setText(blockContactList.get(position).getUserStatus());


            if(!blockContactList.get(position).getAvatarImage().isEmpty()){
                imageLoader.displayImage(blockContactList.get(position).getAvatarImage(), holder.ivImage, circularOption);
            }


            holder.btnUnblock.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    AlertUtility.showConfirmDialog(mContext, mContext.getResources().getString(R.string.sure_unblock) + " " + blockContactList.get(position).getUserName() + " ? ",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (blockContactList.size() > position)
                                    {
                                        callWSUpdateFriendshipContact(position, 1);
                                    }
                                    dialog.cancel();

                                }
                            });

                }
            });

            holder.btnDelete.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    AlertUtility.showConfirmDialog(mContext, mContext.getResources().getString(R.string.sure_delete) + " " + blockContactList.get(position).getUserName() + " ? ",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (blockContactList.size() > position)
                                    {
                                        callWSUpdateFriendshipContact(position, 3);
                                    }
                                    dialog.cancel();

                                }
                            });

                }
            });
	    /*
	     * holder.lnLayout.setOnClickListener(new OnClickListener() {
	     * 
	     * @Override public void onClick(View v) { // TODO Auto-generated
	     * method stub
	     * 
	     * mContext.startActivity(new Intent(mContext,
	     * Chat2Activity.class).putExtra("contactID",
	     * blockContactList.get(position).getUserId()).putExtra("Name",
	     * blockContactList.get(position).getUserName())); } });
	     */

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
        Button btnDelete, btnUnblock;
        LinearLayout lnLayout;
    }

    private void callWSUpdateFriendshipContact(final int pos, int staus)
    {

        try
        {

            if (blockContactList.size() > pos)
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
                nameValuePairs.add(new BasicNameValuePair("contactID", blockContactList.get(pos).getUserId()));
                nameValuePairs.add(new BasicNameValuePair("status", staus + ""));
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

                            if (blockContactList.size() > pos)
                            {

                                blockContactList.remove(pos);
                            }

                            notifyDataSetChanged();

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
            // TODO: handle exception
        }
    }

}
