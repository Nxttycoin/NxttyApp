package com.nxtapp.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.FriendRequest;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class FriendRequestAdapter extends BaseAdapter
{

    Context mContext;
    private ArrayList<FriendRequest> FriendReqList = new ArrayList<FriendRequest>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public FriendRequestAdapter(Context context, ArrayList<FriendRequest> friendReqList2)
    {
        this.mContext = context;
        this.FriendReqList = friendReqList2;
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
        return FriendReqList.size();
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
    public View getView(int position, View convertView, ViewGroup parent)
    {

        try{



            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.friendrequestlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_friendname);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_friendstatus);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_friendrequestlist_item);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(FriendReqList.get(position).getUserName());
            holder.txt_userStatus.setText(FriendReqList.get(position).getUserStatus());

            if(FriendReqList.get(position).getAvatarImage()==null){

            }else{

                imageLoader.displayImage(FriendReqList.get(position).getAvatarImage(), holder.ivImage, circularOptions);
            }
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
    }

}
