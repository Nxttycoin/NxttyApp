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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextapp.tasks.CallPostWebseviceTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.classes.Invites;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.main.R;

public class InvitesAdapter extends BaseAdapter
{
    Context context;
    private ArrayList<Invites> InvitesList = new ArrayList<Invites>();
    private ImageLoader imageLoader;
    private DisplayImageOptions circularOptions;

    public InvitesAdapter(Context context, ArrayList<Invites> invites)
    {
        this.context = context;
        this.InvitesList = invites;
        imageLoader = ImageLoader.getInstance();

        circularOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
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
        ImageView ivImage;
        Button btnAccept, btnReject;
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
                convertView = mInflater.inflate(R.layout.inviteslist_item, parent, false);
                holder = new ViewHolder();
                holder.txt_userName = (TextView) convertView.findViewById(R.id.textview_invitename);
                holder.txt_userStatus = (TextView) convertView.findViewById(R.id.textview_invitestatus);
                holder.ivImage = (ImageView) convertView.findViewById(R.id.imageview_inviteslist_item);
                holder.btnAccept = (Button) convertView.findViewById(R.id.button_accept);
                holder.btnReject = (Button) convertView.findViewById(R.id.button_reject);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_userName.setText(InvitesList.get(position).getUserName());
            holder.txt_userStatus.setText(InvitesList.get(position).getUserStatus());

            if(InvitesList.get(position).getAvatarImage()!=null)
                imageLoader.displayImage(InvitesList.get(position).getAvatarImage(), holder.ivImage, circularOptions);

            holder.btnAccept.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(context.getResources().getString(R.string.sure_add) + " " + InvitesList.get(position).getUserName() + " ?");
                    alertDialog.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (InvitesList.size() > position)
                            {
                                WebCall("1", position);
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
            });
            holder.btnReject.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    // bala code
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Are you sure to reject invitation from " + InvitesList.get(position).getUserName() + " ?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (InvitesList.size() > position)
                            {
                                WebCall("3", position);
                            }
                            dialog.cancel();

                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

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
        return InvitesList.size();
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

    private void WebCall(String val, final int pos)
    {

        try
        {
            if (InvitesList.size() > pos)
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("subscriberID", Constants.NxtAcId));
                nameValuePairs.add(new BasicNameValuePair("contactID", InvitesList.get(pos).getUserId()));
                nameValuePairs.add(new BasicNameValuePair("status", val));
                nameValuePairs.add(new BasicNameValuePair("key", Constants.ParamKey));

                CallPostWebseviceTask task = new CallPostWebseviceTask(context, true, "update_friendship", nameValuePairs)
                {
                    @Override
                    protected void onPostExecute(String result)
                    {
                        // TODO Auto-generated method stub
                        super.onPostExecute(result);

                        try
                        {
                            if (InvitesList.size() > pos)
                            {

                                InvitesList.remove(pos);
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
            DebugReportOnLocat.e(e);
        }
    }

}