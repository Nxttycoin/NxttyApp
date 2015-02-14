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

public class LeaderBoardAdaptor extends BaseAdapter
{
    private Context context;
    private ArrayList<TipModel> arrlstTip;
    private ImageLoader imageLoader;
    private DisplayImageOptions  circularOptions;

    public LeaderBoardAdaptor(Context context, ArrayList<TipModel> arrlstTip)
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

            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

            TipModel tipModel=arrlstTip.get(position);

            holder.txtName.setText(tipModel.getName());

            if(tipModel.getImagePath()==null){

            }else{

                imageLoader.displayImage(tipModel.getImagePath(), holder.image, circularOptions);
            }


            holder.txtTimeStamp.setVisibility(View.GONE);
            holder.txtTipto.setVisibility(View.GONE);
            holder.txtTipValue.setText(tipModel.getTipCount() + " " + context.getResources().getString(R.string.tips));

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

}
