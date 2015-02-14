package com.nxtapp.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import com.nxtapp.classes.TipModel;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class TipListAdaptor extends BaseAdapter
{
    private Context context;
    private ArrayList<TipModel> arrlstTip;
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public TipListAdaptor(Context context, ArrayList<TipModel> arrlstTip)
    {

        this.context = context;
        this.arrlstTip = arrlstTip;
        imageLoader = ImageLoader.getInstance();

        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return BitmapUtility.maskingRoundBitmap(bitmap);
                    }
                })
                .showImageOnFail(R.drawable.loading)
                .build();
    }

    @Override
    public int getCount()
    {
        return arrlstTip.size();
    }

    @Override
    public Object getItem(int position)
    {
        return arrlstTip.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        try
        {

            ViewHolder holder = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.wallet_tip_row_item, parent, false);
                holder = new ViewHolder();

                holder.image = (ImageView) convertView.findViewById(R.id.iv_ProfileImageChatUser);
                holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
                holder.txtTipValue = (TextView) convertView.findViewById(R.id.txtTipValue);
                holder.txtTipto = (TextView) convertView.findViewById(R.id.txtTipto);
                holder.txtTimeStamp = (TextView) convertView.findViewById(R.id.txtTipTimestamp);

                convertView.setTag(holder);

            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtName.setText(arrlstTip.get(position).getName());
            if(arrlstTip.get(position).getImagePath()!=null){

                imageLoader.displayImage(arrlstTip.get(position).getImagePath(), holder.image, circularOptions);

            }else{

                holder.image.setImageResource(R.drawable.logo_app_icon);

            }

            holder.txtTimeStamp.setText(Diff(arrlstTip.get(position).getTipTimestamp()));

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        return convertView;
    }

    private class ViewHolder
    {
        ImageView image;
        TextView txtName;
        TextView txtTipValue;
        TextView txtTipto, txtTimeStamp;
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
                return Long.toString(Hrs) + " " + context.getString(R.string.hrs_ago);
            }
        }
        else
        {
            return Long.toString(diffMinutes) + " " + context.getString(R.string.min_ago);
        }

    }
}
