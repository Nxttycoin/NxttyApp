package com.nxtapp.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
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
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aniways.Aniways;
import com.nextapp.imageloader.ImageLoader_Nxtty;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nxtapp.adapter.GroupDetailAdapter;
import com.nxtapp.adapter.GroupDetailAdapter.OnEditPost;
import com.nxtapp.classes.GroupDetailModel;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpClient;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;
import com.nxtty.main.GroupDetailCreatePost;
import com.nxtty.main.LoadMoreListView;
import com.nxtty.main.LoadMoreListView.OnLoadMoreListener;
import com.nxtty.main.R;

public class GroupDetailActivity extends Activity implements OnEditPost
{

    String GroupID, Name;
    ImageView ivBack, ivCreatePost;
    TextView txtHead;
    LoadMoreListView lvGroupDetail;
    public int Offset = 0;
    int EditPos = 0;

    String Title = "", Body = "", PostID = "", attachedImage = "";

    public static boolean isDataCompleted = false;
    ImageView imgAttachName;
    GroupDetailAdapter groupDetailAdapter;
    public static ArrayList<GroupDetailModel> GROUPDETAILS_LIST = new ArrayList<GroupDetailModel>();

    // private String _path;// , selectedImagePath = "";
    // private Uri mCapturedImageURI = null, mImageCaptureUri_samsung = null;
    // private String tempImagePath;
    boolean isEdit = false;
    TextView txtTop;
    LinearLayout lnTopScroll;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    static ProgressDialog mProgressDialog;
    String Key, imagePath = "";
    ImageLoader_Nxtty imageloader_nxtty;
    File profileImage;
    private static int PICTURE_RESULT = 0;
    private static int SELECT_PICTURE = 1;
    BroadcastReceiver broadcastReceiver;

    public static boolean ComingFromCreatePost = false;

    @Override
    protected void onResume()
    {
        super.onResume();
        // startActivity(new Intent(getApplicationContext(),
        // GroupDetailActivity.class).putExtra("gID", "6").putExtra("Name",
        // "Support"));
        //

        if (ComingFromCreatePost)
        {
            Log.e("GroupDetailActivity", "onResume");
            GROUPDETAILS_LIST.clear();
            Offset = 0;
            GroupPost();
            ComingFromCreatePost = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        Aniways.init(this);

        GROUPDETAILS_LIST = new ArrayList<GroupDetailModel>();
        Offset = 0;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.groupdetail);
        ComingFromCreatePost = false;
        initActivity();
    }

    private void initActivity()
    {

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading).showImageOnFail(R.drawable.loading).build();

        try
        {
            GroupID = getIntent().getExtras().getString("gID");
            Name = getIntent().getExtras().getString("Name","  ");

            DebugReportOnLocat.ln(" GroupID " + GroupID + " Name " + Name);

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }

        lnTopScroll = (LinearLayout) findViewById(R.id.ln_Top_scroll_layout);
        lnTopScroll.setVisibility(View.GONE);

        txtHead = (TextView) findViewById(R.id.txt_groupName);
        Name = Name.toLowerCase();

        try
        {
            String Name1 = Character.toUpperCase(Name.charAt(0)) + Name.substring(1);

            txtHead.setText(Name1);

        } catch (Exception e)
        {
            DebugReportOnLocat.e(e);
        }



        ivCreatePost = (ImageView) findViewById(R.id.iv_head_CreatePost);
        ivCreatePost.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isEdit = false;
                // openDialogCreatePost();

                Intent intent = new Intent(GroupDetailActivity.this, GroupDetailCreatePost.class);
                intent.putExtra("IsEdit", isEdit);
                intent.putExtra("gID", GroupID);
                startActivity(intent);
            }
        });

        ivBack = (ImageView) findViewById(R.id.iv_groupBack);
        ivBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();

            }
        });

        lvGroupDetail = (LoadMoreListView) findViewById(R.id.lv_loadMore_GroupDetail);
        lvGroupDetail.setOnLoadMoreListener(new OnLoadMoreListener()
        {

            @Override
            public void onLoadMore()
            {

                if (!isDataCompleted)
                {
                    Offset = Offset + 25;
                    // new GetAsyncGroupDetail().execute();
                    GroupPost();
                }
                else
                {
                    lvGroupDetail.onLoadMoreComplete();
                }

            }
        });

        lvGroupDetail.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (firstVisibleItem > 3)
                {
                    lnTopScroll.setVisibility(View.VISIBLE);
                }
                else
                {
                    lnTopScroll.setVisibility(View.GONE);
                }
            }
        });

        txtTop = (TextView) findViewById(R.id.txt_top_chat);
        txtTop.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                DebugReportOnLocat.e("###########", " TOp CLicked  ");

                lvGroupDetail.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        lvGroupDetail.setSelection(0);
                    }
                });
            }
        });
        // new GetAsyncGroupDetail().execute();
        if (Network.isNetworkConnected(getApplicationContext()))
        {
            GroupPost();
        }
        else
        {
            AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("COMMENT_UPDATE");
        broadcastReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent)
            {

                if (Network.isNetworkConnected(getApplicationContext()))
                {
                    GroupPost();
                }
                else
                {
                    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
                }

            }
        };

        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    public void GroupPost()
    {

        if(Offset==0)
            showProgress("Please Wait...");

        AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.GROUPS_FETCH_POST);
        req.setBody(JSONBody.groupPost(Constants.NxtAcId, GroupID, "0", "", Offset));
        req.setUserInfo(HttpUri.GROUPS_FETCH_POST);
        // final CategoryManager cManager = new CategoryManager(this);
        new AsyncHttpClient().executeJSONObject(getApplicationContext(), req, new ResultCallback()
        {

            @Override
            public void onCompleted(Exception e, String responseString, String methodInfo)
            {

                PublicChat(responseString);
                // if(CommentActivity.onBackUp==1){
                // groupDetailAdapter.notifyDataSetChanged();
                // CommentActivity.onBackUp=0;
                // }

                // DebugReportOnLocat.ln(" >>> GroupID + "+GroupID);
                // if (Offset < 25)
                // {
                // groupDetailAdapter = new
                // GroupDetailAdapter(GroupDetailActivity.this,
                // GROUPDETAILS_LIST, GroupID);
                // lvGroupDetail.setAdapter(groupDetailAdapter);
                // }
                // else
                // {
                // groupDetailAdapter.notifyDataSetChanged();
                // }
                //
                // lvGroupDetail.onLoadMoreComplete();
            }

            private void PublicChat(String respStr)
            {
                if (respStr.length() < 10)
                {
                    isDataCompleted = true;
                }

                try
                {
                    Log.e("GroupDetailActivity",
                            "respStr for comment=>"+respStr);
                    JSONArray jArray = new JSONArray(respStr);
                    GroupDetailModel model;
                    for (int i = 0; i < jArray.length(); i++)
                    {

                        JSONObject jobj = jArray.getJSONObject(i);
                        model = new GroupDetailModel();
                        model.setId(jobj.optString("id"));
                        model.setTitle(jobj.optString("title"));
                        model.setBody(jobj.optString("body"));
                        model.setUrl(jobj.optString("url"));
                        model.setSource(jobj.optString("source"));
                        model.setTipCount(jobj.optString("tipCount"));
                        if (jobj.optString("image").length() > 5)
                        {
                            model.setImage(Constants.baseUrl_ImagesGroup + jobj.optString("image"));
                        }
                        else
                        {
                            model.setImage("null");
                        }
                        model.setSpamCount(jobj.optString("spamCount"));
                        model.setDeleted(jobj.optString("deleted"));
                        model.setCreated(jobj.optString("created"));
                        model.setModified(jobj.optString("modified"));

                        model.setCommentCount(jobj.optString("commentCount"));

                        JSONObject jobj1 = jobj.getJSONObject("owner");

                        model.setNxtAccountId(jobj1.optString("nxtAccountId"));
                        model.setNameAlias(jobj1.optString("nameAlias"));

                        if( jobj1.optString("avatar").equals("null")){

                            model.setAvatar(null);

                        }else{
                            model.setAvatar(Constants.baseUrl_ImagesGroup + jobj1.optString("avatar"));
                        }


                        model.setRole(jobj1.optString("role"));
                        model.setBlocked(jobj1.optString("blocked"));
                        model.setSpamCount_owner(jobj1.optString("spamCount"));
                        model.setCreated_owner(jobj1.optString("created"));
                        model.setBlockedDate(jobj1.optString("blockedDate"));

                        if (jobj1.optString("nxtAccountId").equalsIgnoreCase(Constants.NxtAcId))
                        {
                            model.setOwner(true);
                        }
                        else
                        {
                            model.setOwner(false);
                        }

                        if (jobj.optString("image").length() > 5)
                        {

                            model.setImagePath(Constants.baseUrl_ImagesGroup + jobj.optString("image"));

                        }
                        else
                        {
                            model.setImagePath("null");
                        }
                        // Constants.groupDetailModel = new GroupDetailModel();
                        // Constants.groupDetailModel = model;
                        GROUPDETAILS_LIST.add(model);
                        try
                        {
                            DebugReportOnLocat.ln(" >>> GroupID + " + GroupID);
                            if (Offset < 25)
                            {
                                groupDetailAdapter = new GroupDetailAdapter(GroupDetailActivity.this, GROUPDETAILS_LIST, GroupID);
                                lvGroupDetail.setAdapter(groupDetailAdapter);
                            }
                            else
                            {
                                groupDetailAdapter.notifyDataSetChanged();
                            }

                            lvGroupDetail.onLoadMoreComplete();

                        } catch (Exception e)
                        {
                            DebugReportOnLocat.e(e);
                        }

                        // try
                        // {
                        // if(groupDetailAdapter!=null){
                        //
                        // groupDetailAdapter.notifyDataSetChanged();
                        // }
                        //
                        //
                        // } catch (Exception e)
                        // {
                        //  DebugReportOnLocat.e(e);
                        // }

                    }

                } catch (JSONException e)
                {
                    DebugReportOnLocat.e(e);
                    dismissProgress(getApplicationContext());
                } finally
                {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            dismissProgress(getApplicationContext());

                        }
                    });
                }

            }
        });

    }

//    @SuppressWarnings("deprecation")
//    public void openDialogCreatePost()
//    {
//
//	imagePath = null;
//
//	final Dialog dialog = new Dialog(GroupDetailActivity.this);
//	dialog.setCanceledOnTouchOutside(false);
//	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//	dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//	dialog.setContentView(R.layout.create_post);
//	dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//	TextView txt_title_create_post = (TextView) dialog.findViewById(R.id.txt_title_create_post);
//	final EditText editTitle = (EditText) dialog.findViewById(R.id.edit_createPost);
//	final EditText editDetail = (EditText) dialog.findViewById(R.id.edit_CreateDetail);
//	LinearLayout lnChooseFile = (LinearLayout) dialog.findViewById(R.id.ln_create_chooseFile);
//	TextView txtPost = (TextView) dialog.findViewById(R.id.txt_create_post);
//	imgAttachName = (ImageView) dialog.findViewById(R.id.img_create_attach);
//
//	TextView txtcancelBottom = (TextView) dialog.findViewById(R.id.txt_cancelBottom);
//	TextView txtcancelTop = (TextView) dialog.findViewById(R.id.txt_create_cancel);
//
//	if (isEdit)
//	{
//	    txt_title_create_post.setText(R.string.edit_post);
//	    // editTitle.setText(Html.fromHtml(Title));
//	    
//	    String decryptTitle = StringEscapeUtils.unescapeJava(Title);
//	    
//	    editTitle.setText(decryptTitle);
//	    
//	    String decryptMsg = StringEscapeUtils.unescapeJava(Body);
//	    Log.e("OpenChat", "decrypt msg escape=>" + decryptMsg);
//	    // editDetail.setText(Html.fromHtml(decryptMsg));
//	    editDetail.setText(decryptMsg);
//	    // editDetail.setText(Body);
//	    if (attachedImage != null && !attachedImage.equalsIgnoreCase("null"))
//	    {
//		Log.e("OpenChat", "attachedImage=>" + attachedImage);
//		imgAttachName.setVisibility(View.VISIBLE);
//		imageLoader.displayImage(attachedImage, imgAttachName, options);
//	    }
//	    else
//	    {
//		imgAttachName.setVisibility(View.INVISIBLE);
//	    }
//
//	}
//	else
//	{
//	    txt_title_create_post.setText(R.string.create_post);
//	}
//
//	txtcancelBottom.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		dialog.cancel();
//	    }
//	});
//	txtcancelTop.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		dialog.cancel();
//	    }
//	});
//	lnChooseFile.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		// if(isEdit&&imagePath==null){
//		// openUploadoption();
//		// }
//		if (isEdit)
//		{
//		    Toast t = Toast.makeText(GroupDetailActivity.this, R.string.cant_edit_image, Toast.LENGTH_SHORT);
//		    t.show();
//		}
//		else
//		{
//		    openUploadoption();
//		}
//	    }
//	});
//
//	txtPost.setOnClickListener(new OnClickListener()
//	{
//	    @Override
//	    public void onClick(View v)
//	    {
//		final String Message = Aniways.encodeMessage(editDetail.getText());
//		Log.e("################", "  Message : " + Message);
//
//		if (editTitle.getText().toString().trim().length() > 0 && Message.trim().length() > 0)
//		{
//		    if (isEdit)
//		    {
//			if (Network.isNetworkConnected(getApplicationContext()))
//			{
//			    new SendPostEdited(editTitle.getText().toString(), Message, PostID, imagePath).execute();
//			    dialog.cancel();
//
//			}
//			else
//			{
//			    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
//			}
//
//		    }
//		    else
//		    {
//
//			if (Network.isNetworkConnected(getApplicationContext()))
//			{
//			    new SendPost(editTitle.getText().toString(), Message, imagePath).execute();
//			    dialog.cancel();
//
//			}
//			else
//			{
//			    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
//
//			}
//
//		    }
//
//		}
//		else if (editTitle.getText().toString().trim().length() == 0)
//		{
//		    // Bala Code
//		    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_title));
//
//		    // Toast t = Toast.makeText(GroupDetailActivity.this,
//		    // R.string.enter_title, Toast.LENGTH_SHORT);
//		    // t.show();
//		}
//		else if (Message.trim().length() == 0)
//		{
//		    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_details));
//		}
//
//	    }
//	});
//
//	dialog.show();
//
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void openUploadoption()
    {

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_dialog);
        ListView lv = (ListView) dialog.findViewById(R.id.dialog_list);
        lv.setAdapter(new ArrayAdapter(this, R.layout.row, getResources().getStringArray(R.array.uploadpicoption)));
        lv.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (arg2 == 0)
                {
                    camera();
                }
                else if (arg2 == 1)
                {
                    gallery();
                }
            }
        });
        dialog.show();

    }

    public void camera()
    {

        File f = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/MyTempImage.jpg");
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        // end new code

        try
        {
            camera.putExtra("return-data", true);
            startActivityForResult(camera, PICTURE_RESULT);
            // startActivityForResult(camera, 1);

        } catch (ActivityNotFoundException e)
        {
            DebugReportOnLocat.e(e);
        }
        // end previous code

    }

    public void gallery()
    {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);

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

                imgAttachName.setImageBitmap(bm);
                imgAttachName.setVisibility(View.VISIBLE);
            }
            else
            {
                imgAttachName.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Pick Images Only", Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE)
        {

            if (resultCode == Activity.RESULT_OK)
            {

                try
                {

                    Uri selectedImageUri = data.getData();
                    DebugReportOnLocat.ln("selectedImageUri getData>>>>>" + selectedImageUri);
                    // OI FILE Manager

                    String filemanagerstring = selectedImageUri.getPath();
                    DebugReportOnLocat.ln("selectedImageUri filestring>>>>>" + filemanagerstring);
                    // MEDIA GALLERY

                    String selectedImagePath = getPath(selectedImageUri);
                    DebugReportOnLocat.ln("selectedImageUri getPath>>>>>" + selectedImagePath);
                    imagePath = selectedImagePath;
                    if (selectedImagePath != null && !selectedImagePath.equals(""))
                    {
                        // String[] name = tempImagePath.split("/");
                        // if (txtAttachName != null)
                        // txtAttachName.setText(name[name.length - 1]);

                        Uri uri = data.getData();
                        Bitmap bitmap = null;
                        try
                        {
                            if (uri != null)
                            {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            }
                        } catch (FileNotFoundException e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        if (bitmap != null)
                        {
                            imgAttachName.setVisibility(View.VISIBLE);
                            imgAttachName.setImageBitmap(bitmap);
                        }
                        else
                        {
                            imgAttachName.setVisibility(View.INVISIBLE);
                        }

                    }
                    if (selectedImagePath != null)
                    {

                        imgAttachName.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        imgAttachName.setVisibility(View.GONE);
                    }

                } catch (Exception e)
                {
                    DebugReportOnLocat.e(e);
                }
            }

            // case PICTURE_RESULT :
        }
        else if (requestCode == PICTURE_RESULT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                imagePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/MyTempImage.jpg";
                profileImage = new File(imagePath);
                imageDisplay();
            }

	    /*
	     * switch (requestCode) { case 1: if (data != null) {
	     * 
	     * 
	     * 
	     * tempImagePath =
	     * StaticUtility.getRealPathFromURI(GroupDetailActivity.this,
	     * data.getData()); Log.i("path is ", tempImagePath); } else {
	     * AlertUtility.showToast(GroupDetailActivity.this,
	     * getString(R.string.err_unknown)); } break;
	     * 
	     * case 2: if (data != null) { tempImagePath =
	     * StaticUtility.getRealPathFromURI(GroupDetailActivity.this,
	     * data.getData()); } else {
	     * AlertUtility.showToast(GroupDetailActivity.this,
	     * getString(R.string.err_unknown)); }
	     * 
	     * break;
	     * 
	     * }
	     */
	    /*
	     * if (tempImagePath != null && !tempImagePath.equals("")) { //
	     * String[] name = tempImagePath.split("/"); // if (txtAttachName !=
	     * null) // txtAttachName.setText(name[name.length - 1]);
	     * 
	     * Uri uri = data.getData(); Bitmap bitmap = null; try { if (uri !=
	     * null) { bitmap =
	     * MediaStore.Images.Media.getBitmap(this.getContentResolver(),
	     * uri); } } catch (FileNotFoundException e1) { // TODO
	     * Auto-generated catch block e1.printStackTrace(); } catch
	     * (IOException e1) { // TODO Auto-generated catch block
	     * e1.printStackTrace(); } if (bitmap != null) {
	     * imgAttachName.setVisibility(View.VISIBLE);
	     * imgAttachName.setImageBitmap(bitmap); } else {
	     * imgAttachName.setVisibility(View.INVISIBLE); }
	     * 
	     * }
	     */

        }

    }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data)
    // {
    //
    // super.onActivityResult(requestCode, resultCode, data);
    //
    // if (requestCode == 1)
    // {
    //
    // try
    // {
    // Log.i("TAG", "inside Samsung Phones");
    // String[] projection = { MediaStore.Images.Thumbnails._ID, // The
    // // columns
    // // we
    // // want
    // MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.KIND,
    // MediaStore.Images.Thumbnails.DATA };
    // String selection = MediaStore.Images.Thumbnails.KIND + "=" + // Select
    // // only
    // // mini's
    // MediaStore.Images.Thumbnails.MINI_KIND;
    //
    // String sort = MediaStore.Images.Thumbnails._ID + " DESC";
    //
    // Cursor myCursor =
    // this.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
    // projection, selection, null, sort);
    //
    // long imageId = 0l;
    // long thumbnailImageId = 0l;
    // String thumbnailPath = "";
    //
    // try
    // {
    // myCursor.moveToFirst();
    // imageId =
    // myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
    // thumbnailImageId =
    // myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
    // thumbnailPath =
    // myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
    // } finally
    // {
    // // myCursor.close();
    // }
    //
    // String[] largeFileProjection = { MediaStore.Images.ImageColumns._ID,
    // MediaStore.Images.ImageColumns.DATA };
    //
    // String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
    // myCursor =
    // this.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    // largeFileProjection, null, null, largeFileSort);
    // String largeImagePath = "";
    //
    // try
    // {
    // myCursor.moveToFirst();
    //
    // largeImagePath =
    // myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
    // mImageCaptureUri_samsung = Uri.fromFile(new File(largeImagePath));
    // mCapturedImageURI = null;
    // } finally
    // {
    // // myCursor.close();
    // }
    //
    // } catch (Exception e)
    // {
    // mImageCaptureUri_samsung = null;
    // Log.i("TAG", "inside catch Samsung Phones exception " + e.toString());
    //
    // }
    //
    // if (mImageCaptureUri_samsung != null)
    // {
    // selectedImagePath = mImageCaptureUri_samsung.getPath();
    // }
    // else
    // {
    // selectedImagePath = getPath(mCapturedImageURI);
    // }
    // DebugReportOnLocat.e("Image path", selectedImagePath);
    //
    // }
    // else if (requestCode == 2)
    // {
    //
    // Uri selectedImageUri = data.getData();
    //
    // selectedImagePath = getPath(selectedImageUri);
    // DebugReportOnLocat.e("Image path sec ", selectedImagePath);
    //
    // }
    //
    // String[] name = selectedImagePath.split("/");
    // txtAttachName.setText(name[name.length - 1]);
    //
    // }

    public static Bitmap decodeFile(String path, boolean falg)
    {
        int orientation;
        try
        {
            if (path == null)
            {
                return null;
            }
            Bitmap bitmap = null;
            Bitmap bm = null;
            if (falg)
            {
                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = 4;
                bm = BitmapFactory.decodeFile(path, o2);
                bitmap = bm;
            }
            else
            {
                try
                {
                    // Decode image size
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, o);
                    // The new size we want to scale to
                    // final int REQUIRED_SIZE = 70;
                    // Find the correct scale value. It should be the power of
                    // 2.
                    int scale = 1;
                    if (o.outWidth >= 1200 || o.outHeight >= 1200)
                    {
                        scale = 4;
                    }
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    o.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(path, o2);
                    bitmap = bm;
                } catch (FileNotFoundException e)
                {
                }
            }

            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180))
            {
                m.postRotate(180);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            {
                m.postRotate(90);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            {
                m.postRotate(270);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e)
        {
            return null;
        }
    }

    public String getPath(Uri uri)
    {

        String StringPath;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null)
        {

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            StringPath = cursor.getString(column_index);
        }
        else
        {
            StringPath = null;
        }

        if (StringPath == null)
        {
            StringPath = uri.getPath();
        }
        else
        {
            return StringPath;
        }

        if (StringPath == null)
        {
            return null;
        }
        else
        {
            return StringPath;
        }
    }

    public class SendPost extends AsyncTask<Void, String, Void>
    {

        ProgressDialog pDialog;
        String Title, Message, path;

        public SendPost(String Title, String Message, String path)
        {
            this.Message = Message;
            this.Title = Title;
            this.path = path;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            try
            {
                pDialog = new ProgressDialog(GroupDetailActivity.this);
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
            HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/create_post");

            DebugReportOnLocat.e("##############", "  INSIDE POST  -->  ");

            MultipartEntity reqEntity = new MultipartEntity();
            try
            {

                reqEntity.addPart("nxtID", new StringBody(Constants.NxtAcId));
                reqEntity.addPart("groupID", new StringBody(GroupID));
                reqEntity.addPart("title", new StringBody(Title));
                reqEntity.addPart("key", new StringBody(Constants.ParamKey));
                // byte[] b = Message.getBytes("UTF-8");
                // String temp = new String(b, "US-ASCII");

                String unicodeString = convertToUnicodeEscaped(Message);

                reqEntity.addPart("message", new StringBody(unicodeString));

                // reqEntity.addPart("message", new StringBody(Message));

                DebugReportOnLocat.e("openchat", "nxtID--> " + Constants.NxtAcId);
                DebugReportOnLocat.e("openchat", "groupID--> " + GroupID);
                DebugReportOnLocat.e("openchat", "title--> " + Title);
                DebugReportOnLocat.e("openchat", "message--> " + unicodeString);

                if (path != null && !path.equals(""))
                {
                    FileBody bin = new FileBody(new File(path));
                    reqEntity.addPart("file", bin);
                }
                else
                {
                    // reqEntity.addPart("file", new StringBody("null"));
                }
                httppost.setEntity(reqEntity);

                HttpResponse response = httpclient.execute(httppost);
                respStr = EntityUtils.toString(response.getEntity());

                DebugReportOnLocat.e("##############", "  responce Create POST  -->  " + respStr);

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
            GROUPDETAILS_LIST.clear();
            Offset = 0;
            isDataCompleted = false;
            // new GetAsyncGroupDetail().execute();
            GroupPost();
            dismissProgress(getApplicationContext());
        }
    }

    private String unicodeEscaped(char ch)
    {
        if (ch < 0x10)
        {
            return "\\u000" + Integer.toHexString(ch);
        }
        else if (ch < 0x100)
        {
            return "\\u00" + Integer.toHexString(ch);
        }
        else if (ch < 0x1000)
        {
            return "\\u0" + Integer.toHexString(ch);
        }
        return "\\u" + Integer.toHexString(ch);
    }

    private String convertToUnicodeEscaped(String str)
    {
        StringBuilder sbUnicodeBuilder = new StringBuilder();

        char[] chArr = str.toCharArray();
        for (char mChar : chArr)
        {
            sbUnicodeBuilder.append(unicodeEscaped(mChar));
        }

        return sbUnicodeBuilder.toString();
    }

    public class SendPostEdited extends AsyncTask<Void, String, Void>
    {

        ProgressDialog pDialog;
        String Title, Message, path, PostID;

        public SendPostEdited(String Title, String Message, String postID, String path)
        {
            this.Message = Message;
            this.Title = Title;
            this.PostID = postID;
            this.path = path;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            try
            {

                pDialog = new ProgressDialog(GroupDetailActivity.this);
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
            HttpPost httppost = new HttpPost(Constants.baseUrl_Group + "groups/update_post");

            try
            {

                MultipartEntity reqEntity = new MultipartEntity();
                // reqEntity.addPart("nxtID", new
                // StringBody(Constants.NxtAcId));
                // reqEntity.addPart("groupID", new StringBody(GroupID));
                reqEntity.addPart("postID", new StringBody(PostID));
                reqEntity.addPart("title", new StringBody(Title));
                reqEntity.addPart("key", new StringBody(Constants.ParamKey));
                String unicodeString = convertToUnicodeEscaped(Message);

                reqEntity.addPart("message", new StringBody(unicodeString));

                // reqEntity.addPart("message", new StringBody(Message));

                if (path != null && !path.equals(""))
                {
                    DebugReportOnLocat.e("##########", " file  include: " + path);
                    FileBody bin = new FileBody(new File(path));
                    reqEntity.addPart("file", bin);
                }
                else
                {
                    DebugReportOnLocat.e("##########", " file not include : ");
                    // reqEntity.addPart("file", new StringBody("null"));
                }

                // DebugReportOnLocat.e("##########", " Path  : " + path);
                //
                // if (path != null && !path.equals(""))
                // {
                //
                // FileBody bin = new FileBody(new File(path));
                // reqEntity.addPart("file", bin);
                // }
                // else
                // {
                // // reqEntity.addPart("file", new StringBody("null"));
                // }
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                respStr = EntityUtils.toString(response.getEntity());

                DebugReportOnLocat.e("##############", "  responce from conversation  -->  " + respStr);
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
            GROUPDETAILS_LIST.clear();
            Offset = 0;
            isDataCompleted = false;
            // new GetAsyncGroupDetail().execute();
            GroupPost();
            dismissProgress(getApplicationContext());

        }

    }

    @Override
    public void OnEditClick(int pos, String Title, String body, String PostID, String attachedImage)
    {
        DebugReportOnLocat.e("#############", "   position :  " + pos);

        EditPos = pos;
        isEdit = true;
        this.Title = Title;
        this.Body = body;
        this.PostID = PostID;
        this.attachedImage = attachedImage;
        // openDialogCreatePost();

        Intent intent = new Intent(GroupDetailActivity.this, GroupDetailCreatePost.class);
        intent.putExtra("IsEdit", isEdit);
        intent.putExtra("gID", GroupID);
        intent.putExtra("Title", Title);
        intent.putExtra("Body", Body);
        intent.putExtra("AttachedImage", attachedImage);
        intent.putExtra("PostID", PostID);
        startActivity(intent);

    }

    protected void showProgress(String msg)
    {
        try
        {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                dismissProgress(getApplicationContext());

            mProgressDialog = ProgressDialog.show(this, "", msg);

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }

    public static void dismissProgress(Context context)
    {
        try
        {
            if (mProgressDialog != null)
            {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

        } catch (Exception e)
        {
            // TODO: handle exception
        }

    }
}
