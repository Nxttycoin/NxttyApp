package com.nxtapp.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.NewsFeed;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

@SuppressLint("SimpleDateFormat")
public class NewsFeedAdapter extends BaseAdapter
{
    Context context;
    private ArrayList<NewsFeed> NewsFeedList = new ArrayList<NewsFeed>();
    ImageLoader imageLoader;
    DisplayImageOptions options,circularOptions;

    public NewsFeedAdapter(Context context, ArrayList<NewsFeed> newsfeed)
    {
        this.context = context;
        this.NewsFeedList = newsfeed;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .showImageOnFail(R.drawable.loading_white)
                .build();

        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.loading_white)
                .showImageForEmptyUri(R.drawable.loading_white)
                .showImageOnFail(R.drawable.loading_white)
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
        TextView txt_time, txt_userName, txt_userStatus;
        ImageView profImage, NewsFeedImage;

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        try{


            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.newsfeedlist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_newsfeedname);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_newsfeedstatus);
                holder.txt_time = (TextView) convertView.findViewById(R.id.textview_newsfeedtime);
                holder.profImage = (ImageView) convertView.findViewById(R.id.imageview_newsfeedlist_item);
                holder.NewsFeedImage = (ImageView) convertView.findViewById(R.id.iv_newsfeedstatus);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(NewsFeedList.get(position).getUserDetail().get(0).getNameAlias());
            holder.txt_userStatus.setText(NewsFeedList.get(position).getUserDetail().get(0).getStatus());

            holder.txt_time.setText(Diff(NewsFeedList.get(position).getCreated()));

            if(NewsFeedList.get(position).getUserDetail().get(0).getAvatar()!=null)
                imageLoader.displayImage(NewsFeedList.get(position).getUserDetail().get(0).getAvatar(), holder.profImage, circularOptions);

            if(NewsFeedList.get(position).getImage()!=null)
                imageLoader.displayImage(NewsFeedList.get(position).getImage(), holder.NewsFeedImage, options);


        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }


        return convertView;
    }

    @Override
    public int getCount()
    {

        return NewsFeedList.size();
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

    private String Diff(String timestamp)
    {

        //String Time = null;

        long currentTime = System.currentTimeMillis() / 1000;
        long PrevTime = Long.parseLong(timestamp) / 1000;

        long diff = currentTime - PrevTime;

        long diffMinutes = diff / 60;

        DebugReportOnLocat.e("############", "   diffMinutes    " + diffMinutes);

        DebugReportOnLocat.ln(diffMinutes + " minutes, ");
        if (diffMinutes > 60)
        {
            long Hrs = diffMinutes / 60;

            DebugReportOnLocat.e("############", "   Hrs    " + Hrs);

            DebugReportOnLocat.ln(Hrs + " Hrs, ");
            if (Hrs > 24)
            {

                return new SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(PrevTime * 1000);

            }
            else
            {
                return Long.toString(Hrs) + " "+context.getResources().getString(R.string.hrs_ago);
            }

        }
        else
        {
            return Long.toString(diffMinutes) + " "+context.getResources().getString(R.string.min_ago);
        }

        // return Time;

    }

}
