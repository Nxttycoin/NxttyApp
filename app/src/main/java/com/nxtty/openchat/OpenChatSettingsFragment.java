package com.nxtty.openchat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nextapp.imageloader.ImageLoader_Nxtty;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nxtapp.adapter.GroupUnsubScribeAdapter;
import com.nxtapp.classes.GroupModel;
import com.nxtapp.fragment.BitmapUtility;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.CropperImageActivity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.ExpandableHeightListView;
import com.nxtapp.utils.Network;
import com.nxtapp.utils.Utils;
import com.nxtapp.utils.Utils.STORAGE;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.R;

public class OpenChatSettingsFragment extends Fragment
{

    private String TAG = "GroupSettingsActivity";
    private static final int ACTION_REQUEST_CAMERA = 1001;
    private static final int ACTION_REQUEST_CROP = 1002;
    private static final int ACTION_REQUEST_GALLERY = 1003;
    private String tempImagePath, imagePath = "";
    ImageView ivProfile, ivBack, ivNotiToggle;// , ivNotiReaction, ivnotiTips;
    Button btnSaveChanges;
    ExpandableHeightListView lvUnsubScribe;
    String ImagePathFromServer, DeviceIDFrom = "";
    private ImageLoader imageLoader;
    String profileImagePath;
    private TextView txt_username;
    ImageLoader_Nxtty imageloader_nxtty;
    File profileImage;

    private DisplayImageOptions options,circularOptions;
    ProgressDialog mProgressDialog;
    ArrayList<GroupModel> GroupList = new ArrayList<GroupModel>();
    private SharedPreferences mNotiPref;

    // Context context;


    private static final Field sChildFragmentManagerField;
    private static final String LOGTAG = "GCheck";

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.e(LOGTAG, "Error getting mChildFragmentManager field", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Log.e(LOGTAG, "Error setting mChildFragmentManager field", e);
            }
        }
    }

    public static Fragment newInstance(Context context)
    {
        OpenChatSettingsFragment f = new OpenChatSettingsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.group_settings, null, false);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .build();

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

        ivProfile = (ImageView) view.findViewById(R.id.ivProfilePicGroup);
        ivNotiToggle = (ImageView) view.findViewById(R.id.ivToggleNotificationGroup);
        btnSaveChanges = (Button) view.findViewById(R.id.button_savechangesGroup);
        lvUnsubScribe = (ExpandableHeightListView) view.findViewById(R.id.lv_unsubscribeGroup);
        lvUnsubScribe.setExpanded(true);

        mNotiPref = getActivity().getSharedPreferences("notify_settings", 0);
        ivNotiToggle.setSelected(mNotiPref.getBoolean("notify_enabled", true));

        txt_username = (TextView) view.findViewById(R.id.txt_username);

        SharedPreferences prefs = getActivity().getSharedPreferences("Avtar", 0);
        Constants.SameAvtarforGroup = prefs.getBoolean("checked", false);
        Constants.SameAvtarImagePath = prefs.getString("ImagePath", "");

        new GetGroupUserDetailAsync().execute();

        if (Network.isNetworkConnected(getActivity()))
        {
            PublicChatGroup();
        }
        else
        {
            AlertUtility.showToast(getActivity(), getString(R.string.networkIssues));
        }

        btnSaveChanges.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try
                {

                    new SaveChangesAsync().execute();

                } catch (Exception e)
                {

                    DebugReportOnLocat.e(e);
                }

            }
        });

        ivProfile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openUploadoption();
            }
        });

        ivNotiToggle.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ivNotiToggle.setSelected(!ivNotiToggle.isSelected());

            }
        });

        return view;
    }

    private void openUploadoption()
    {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);
        ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
        lv.setAdapter(new ArrayAdapter(getActivity(), R.layout.row, getResources().getStringArray(R.array.uploadpicoption)));
        lv.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (arg2 == 0)
                {
                    initCamera();
                }
                else if (arg2 == 1)
                {

                    initGallery();
                }
            }
        });
        dialog.show();

    }

    @SuppressWarnings("static-access")
    public void imageDisplay()
    {

        Bitmap bm = imageloader_nxtty.decodeFile(profileImage);

        DebugReportOnLocat.ln("Bitmap Value ===============> : " + bm);

        if (bm != null)
        {
            Matrix matrix = new Matrix();
            float rotation = GroupDetailActivity.rotationForImage(imagePath);
            if (rotation != 0f)
            {
                matrix.preRotate(rotation);
            }
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

            if (bm != null)
            {

                ivProfile.setImageBitmap(bm);
                ivProfile.setVisibility(View.VISIBLE);
            }
            else
            {
                ivProfile.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            Toast.makeText(getActivity(), "Pick Images Only", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("NewApi")
    public static float rotationForImage(String imagePath)
    {
        try
        {
            ExifInterface exif = new ExifInterface(imagePath);
            int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            return rotation;
        } catch (IOException e)
        {
            // Log.e(TAG, "Error checking exif", e);
        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation)
    {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    // @Override
    // public void onBackPressed()
    // {
    // // TODO Auto-generated method stub
    // super.onBackPressed();
    //
    // Intent intentCloseHome = new Intent("CLOSE_ALL");
    // sendBroadcast(intentCloseHome);
    //
    // }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case ACTION_REQUEST_GALLERY:
                    try
                    {
                        Uri mImageUri = data.getData();
                        STORAGE available_storage = Utils.getStorageWithFreeSpace(getActivity());
                        File folder = new File(Utils.getRootPath(getActivity(), available_storage));
                        if (!folder.isDirectory())
                        {
                            folder.mkdir();
                        }
                        File imageFile = new File(Utils.getImagePath(getActivity(), available_storage, true));
                        if (!imageFile.exists())
                        {
                            imageFile.createNewFile();
                        }

                        if (imageFile.exists())
                        {
                            Utils.getImagePathFromURI(getActivity(), mImageUri, imageFile);

                            profileImagePath = imageFile.getPath();
                            startActivityForResult(new Intent(getActivity(), CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), getResources().getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e)
                    {
                        Toast.makeText(getActivity(), getResources().getString(R.string.image_invalid), Toast.LENGTH_LONG).show();
                        DebugReportOnLocat.e(e);
                    }

                    break;
                case ACTION_REQUEST_CAMERA:
                    try
                    {
                        startActivityForResult(new Intent(getActivity(), CropperImageActivity.class).putExtra("Path", profileImagePath), ACTION_REQUEST_CROP);

                    } catch (Exception e)
                    {
                    }

                    break;
                case ACTION_REQUEST_CROP:
                    imagePath = data.getStringExtra("picture_path");
                    if (imagePath != null && !imagePath.equals(""))
                    {
                        ivProfile.requestFocus();
                        ivProfile.setImageURI(Uri.parse(imagePath));
                    }
                    break;
            }
        }
        else if (requestCode == ACTION_REQUEST_CROP)
        {
            imagePath = tempImagePath;
            if (imagePath != null && !imagePath.equals(""))
            {
                ivProfile.requestFocus();
                ivProfile.setImageURI(Uri.parse(imagePath));
            }
        }
    }

    public class SaveChangesAsync extends AsyncTask<Void, String, String>
    {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            // pDialog = new ProgressDialog(getActivity());
            // pDialog.setMessage(getResources().getString(R.string.please_wait));
            // pDialog.show();

            try
            {
                // G code
                if (pDialog != null)
                {

                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    pDialog = null;
                }

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.show();

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

        }

        @Override
        protected String doInBackground(Void... params)
        {

            DebugReportOnLocat.e(TAG, " NxtAcId  :  " + Constants.NxtAcId);
            DebugReportOnLocat.e(TAG, " RegistationKeyGCM  :  " + Constants.RegistationKeyGCM);
            DebugReportOnLocat.e(TAG, " imagePath  :  " + imagePath);

            String url = Constants.baseUrl_Group + "subscriber/update_subscriber_settings";
            DebugReportOnLocat.e(TAG, " imagePath  :  " + imagePath);

            String respStr = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
            HttpPost httppost = new HttpPost(url);
            try
            {
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
                reqEntity.addPart("key", new StringBody(Constants.ParamKey));
                if (imagePath != null)
                {

                    if (imagePath.trim().length() > 2)
                    {

                        // DebugReportOnLocat.e("Inside If =>", " Inside This");
                        // entityBuilder.addPart("file", new FileBody(new
                        // File(imagePath)));

                        reqEntity.addPart("file", new FileBody(new File(imagePath)));

                    }

                    else if (Constants.SameAvtarforGroup && Constants.SameAvtarImagePath.length() > 5)
                    {
                        reqEntity.addPart("file", new FileBody(new File(Constants.SameAvtarImagePath)));
                    }

                }

                if (ivNotiToggle.isSelected())
                {

                    mNotiPref.edit().putBoolean("notify_enabled", ivNotiToggle.isSelected()).commit();
                    // AlertUtility.showToast(getApplicationContext(),
                    // getResources().getString(R.string.update_success));

                    reqEntity.addPart("deviceID", new StringBody(Constants.RegistationKeyGCM));
                }
                else
                {
                    reqEntity.addPart("deviceID", new StringBody(""));
                }
                reqEntity.addPart("deviceType", new StringBody("Android"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                respStr = EntityUtils.toString(response.getEntity());
                try
                {
                    // G code
                    if (respStr != null)
                    {

                        JSONObject jo = new JSONObject(respStr);
                        if (jo.getBoolean(Constants.STATUS_WKEY))
                        {
                            mNotiPref.edit().putBoolean("notify_enabled", ivNotiToggle.isSelected()).commit();
                        }
                    }

                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);

                }

                DebugReportOnLocat.e(TAG, "  responce from Save CHanges  -->  " + respStr);

            } catch (ClientProtocolException e)
            {

                DebugReportOnLocat.e(e);

            } catch (IOException e)
            {
                DebugReportOnLocat.e(e);
            }
            return respStr;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            DebugReportOnLocat.ln(" result >> " + result);

            if (result != null)
            {
                if (result.length() > 0)
                {
                    try
                    {
                        JSONObject jsonObject = new JSONObject(result);

                        boolean status = jsonObject.getBoolean("status");
                        if (status)
                        {
                            Toast t = Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT);
                            t.show();
                        }
                        else
                        {
                            String errorDetail = jsonObject.getString("errorDetail");
                            Toast t = Toast.makeText(getActivity(), errorDetail, Toast.LENGTH_SHORT);
                            t.show();
                        }

                    } catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        DebugReportOnLocat.e(e);
                    }

                }
            }
        }
    }

    public class GetGroupUserDetailAsync extends AsyncTask<Void, String, Void>
    {

        ProgressDialog pDialog;
        String nameAlias = "";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            try
            {

                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage(getResources().getString(R.string.please_wait));
                pDialog.show();

            } catch (Exception e)
            {
                DebugReportOnLocat.e(e);
            }

        }

        @Override
        protected Void doInBackground(Void... params)
        {

            String respStr = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
            HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "subscriber/get_subscriber");
            try
            {
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
                reqEntity.addPart("key", new StringBody(Constants.ParamKey));
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                respStr = EntityUtils.toString(response.getEntity());

                DebugReportOnLocat.e("##############", "  responce from Save CHanges  -->  " + respStr);

                try
                {
                    JSONObject jobj = new JSONObject(respStr);
                    ImagePathFromServer = jobj.optString("avatar");
                    DeviceIDFrom = jobj.optString("deviceID");

                    nameAlias = jobj.optString("nameAlias");

                } catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    DebugReportOnLocat.e(e);
                }

            } catch (ClientProtocolException e)
            {
            } catch (IOException e)
            {
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

            } catch (Exception err)
            {
                DebugReportOnLocat.e(err);

            }
            ;

            if (nameAlias != null && !nameAlias.equalsIgnoreCase("null"))
            {
                txt_username.setText(nameAlias);
            }

            if (ImagePathFromServer != null && ImagePathFromServer.length() > 5)
            {
                imageLoader.displayImage(Constants.baseUrl_ImagesGroup + ImagePathFromServer, ivProfile, circularOptions);

            }
            else if (Constants.SameAvtarforGroup && Constants.SameAvtarImagePath.length() > 5)
            {
                File imgFile = new File(Constants.SameAvtarImagePath);

                if (imgFile.exists())
                {

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivProfile.setImageBitmap(myBitmap);

                }

            }

            if (DeviceIDFrom != null && DeviceIDFrom.length() > 5)
            {
                ivNotiToggle.setSelected(true);
            }

        }
    }

    void PublicChatGroup()
    {

        showProgress("Please Wait...");

        AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_USER_GROUPS);
        req.setBody(JSONBody.publicChat(Constants.NxtAcId));
        req.setUserInfo(HttpUri.GROUPS_USER_GROUPS);
        // final CategoryManager cManager = new CategoryManager(this);
        new AsyncHttpClient().executeJSONObject(getActivity(), req, new ResultCallback()
        {

            @Override
            public void onCompleted(Exception e, String responseString, String methodInfo)
            {

                PublicChat(responseString);

                GroupUnsubScribeAdapter adapter = new GroupUnsubScribeAdapter(getActivity(), GroupList);
                lvUnsubScribe.setAdapter(adapter);

            }

            private void PublicChat(String respStr)
            {
                try
                {
                    GroupList.clear();
                    JSONArray jArray = new JSONArray(respStr);
                    GroupModel model;
                    for (int i = 0; i < jArray.length(); i++)
                    {

                        JSONObject jobj = jArray.getJSONObject(i);
                        model = new GroupModel();
                        model.setId(jobj.optString("id"));
                        model.setTitle(jobj.optString("title"));
                        model.setDescription(jobj.optString("description"));
                        model.setCategory(jobj.optString("category"));
                        model.setAvatar(jobj.optString("avatar"));
                        model.setCreated(jobj.optString("created"));
                        model.setModified(jobj.optString("modified"));
                        model.setPosts(jobj.optString("posts"));
                        model.setMembers(jobj.optString("members"));

                        model.setOwner(true);

                        GroupList.add(model);

                    }

                } catch (JSONException e)
                {
                    DebugReportOnLocat.e(e);
                } finally
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            dismissProgress();

                            if (GroupList.size() == 0)
                            {

                                //
                            }

                        }
                    });
                }

            }
        });

    }

    /*
     * public class GetAsyncPublicChatGroups extends AsyncTask<Void, String,
     * Void> {
     * 
     * ProgressDialog pDialog;
     * 
     * @Override protected void onPreExecute() { super.onPreExecute();
     * 
     * pDialog = new ProgressDialog(getActivity());
     * pDialog.setMessage(getResources().getString(R.string.please_wait));
     * pDialog.show();
     * 
     * }
     * 
     * @Override protected Void doInBackground(Void... params) {
     * 
     * DebugReportOnLocat.e("#########", " FR Constants.NxtAcId  :  " +
     * Constants.NxtAcId);
     * 
     * String respStr = null;
     * 
     * HttpClient httpclient = new DefaultHttpClient();
     * HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000);
     * HttpPost httppost = new HttpPost(Constants.baseUrl_Group +
     * "groups/user_groups");
     * 
     * DebugReportOnLocat.e("##############", "  Constants.NxtAcId -->  " +
     * Constants.NxtAcId);
     * 
     * List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
     * 
     * try { nameValuePairs.add(new BasicNameValuePair("nxtID",
     * Constants.NxtAcId));
     * 
     * httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
     * HttpResponse response = httpclient.execute(httppost); respStr =
     * EntityUtils.toString(response.getEntity());
     * 
     * DebugReportOnLocat.e("##############", "  responce from post -->  " +
     * respStr);
     * 
     * } catch (ClientProtocolException e) { } catch (IOException e) { }
     * 
     * try { GroupList.clear(); JSONArray jArray = new JSONArray(respStr);
     * GroupModel model; for (int i = 0; i < jArray.length(); i++) {
     * 
     * JSONObject jobj = jArray.getJSONObject(i); model = new GroupModel();
     * model.setId(jobj.optString("id"));
     * model.setTitle(jobj.optString("title"));
     * model.setDescription(jobj.optString("description"));
     * model.setCategory(jobj.optString("category"));
     * model.setAvatar(jobj.optString("avatar"));
     * model.setCreated(jobj.optString("created"));
     * model.setModified(jobj.optString("modified"));
     * model.setPosts(jobj.optString("posts"));
     * model.setMembers(jobj.optString("members"));
     * 
     * model.setOwner(true);
     * 
     * GroupList.add(model);
     * 
     * }
     * 
     * } catch (JSONException e) { DebugReportOnLocat.e(e); }
     * 
     * return null; }
     * 
     * @Override protected void onPostExecute(Void result) {
     * super.onPostExecute(result);
     * 
     * pDialog.dismiss(); GroupUnsubScribeAdapter adapter = new
     * GroupUnsubScribeAdapter(getActivity(), GroupList);
     * lvUnsubScribe.setAdapter(adapter);
     * 
     * }
     * 
     * }
     */
    protected void showProgress(String msg)
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(getActivity(), "", msg);
    }

    protected void dismissProgress()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initGallery()
    {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_REQUEST_GALLERY);
    }

    private void initCamera()
    {

        STORAGE availableStorage = Utils.getStorageWithFreeSpace(getActivity());
        String rootPath = Utils.getRootPath(getActivity(), availableStorage);

        File folder = new File(rootPath);

        if (!folder.isDirectory())
        {
            folder.mkdir();
        }

        File fileCamera = new File(Utils.getImagePath(getActivity(), availableStorage, true));
        profileImagePath = fileCamera.getPath();
        Log.d("log_tag", "uri: " + profileImagePath);

        if (!fileCamera.exists())
            try
            {
                fileCamera.createNewFile();
            } catch (IOException e)
            {
                DebugReportOnLocat.e(e);
            }

        Uri mImageUri = Uri.fromFile(fileCamera);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, ACTION_REQUEST_CAMERA);
    }

}
