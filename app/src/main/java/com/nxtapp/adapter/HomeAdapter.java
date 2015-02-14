package com.nxtapp.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nxtapp.classes.ChatModel;
import com.nxtapp.classes.UserModel;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

@SuppressLint("SimpleDateFormat")
public class HomeAdapter extends BaseAdapter
{

    Context mContext;
    private ArrayList<UserModel> HomeScreenList = new ArrayList<UserModel>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public HomeAdapter(Context context, ArrayList<UserModel> friendReqList2)
    {
        this.mContext = context;
        this.HomeScreenList = friendReqList2;
        imageLoader = ImageLoader.getInstance();
        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .build();
    }

    @Override
    public int getCount()
    {
        return HomeScreenList.size();
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

        try{


            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.homescreenlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_Homescreenname);
                holder.tv_lastmsg = (TextView) convertView.findViewById(R.id.tv_lastmsg);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_Homescreen_item);
                holder.txtTimestamp = (TextView) convertView.findViewById(R.id.txt_homescreenTimestamp);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(HomeScreenList.get(position).getNameAlias());
            holder.txtTimestamp.setText(Diff(HomeScreenList.get(position).getLastMessage().getCreatedDate()));

            ChatModel lastMsg = HomeScreenList.get(position).getLastMessage();
            if (lastMsg != null && lastMsg.getContent().equalsIgnoreCase("Text"))
            {
                holder.tv_lastmsg.setText(lastMsg.getBody().trim());
            }
            else
            {
                holder.tv_lastmsg.setText("");
            }

            if(HomeScreenList.get(position).getAvatar()!=null){
                imageLoader.displayImage(HomeScreenList.get(position).getAvatar(), holder.ivImage, circularOptions);
            }else{

                holder.ivImage.setImageResource(R.drawable.logo);
            }




        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }


        return convertView;
    }

    private class ViewHolder
    {
        TextView txt_userName, tv_lastmsg, txtTimestamp;
        ImageView ivImage;
    }

    private String Diff(String timestamp)
    {

        //String Time = null;

        long currentTime = System.currentTimeMillis() / 1000;
        long PrevTime = Long.parseLong(timestamp) / 1000;
        long diff = currentTime - PrevTime;
        long diffMinutes = diff / 60;
        if (diffMinutes > 60)
        {
            long Hrs = diffMinutes / 60;
            if (Hrs > 24)
            {
                return new SimpleDateFormat("dd-MMM-yyyy").format(PrevTime * 1000);
            }
            else
            {
                return new SimpleDateFormat("hh:mm a").format(PrevTime * 1000);
            }
        }
        else
        {
            return new SimpleDateFormat("hh:mm a").format(PrevTime * 1000);
        }
    }
}
