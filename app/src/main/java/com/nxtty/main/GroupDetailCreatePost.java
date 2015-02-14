package com.nxtty.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aniways.Aniways;
import com.flurry.android.FlurryAgent;
import com.nextapp.imageloader.ImageLoader_Nxtty;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nxtapp.fragment.GroupDetailActivity;
import com.nxtapp.utils.AlertUtility;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.CropperImageActivity;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtapp.utils.Network;
import com.nxtapp.utils.Utils;
import com.nxtapp.utils.Utils.STORAGE;

public class GroupDetailCreatePost extends Activity
{

    ImageView imgAttachName;
    private String imagePath;
    boolean isEdit = false;
    String Title = "", Body = "", PostID = "", attachedImage = "";
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private static final int PICTURE_RESULT = 0;
    private static final int SELECT_PICTURE = 1;
    private static final int ACTION_REQUEST_CROP = 0x1002;
    private static final int EDIT_PICTURE = 0x88888;
    private String GroupID;
    private File profileImage;
    private ImageLoader_Nxtty imageloader_nxtty;
    ImageView ivCamera, ivGallery;

    TextView txtcancelTop, txtPost;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	Aniways.init(GroupDetailCreatePost.this);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.create_post);
	mContext=this;
	
	imagePath = null;
	FlurryAgent.setLogEnabled(true);
	// FlurryAgent.setReportLocation(true);
	FlurryAgent.setUserId("nxtmobileappco@gmail.com");
	FlurryAgent.setLogLevel(2);
	FlurryAgent.onStartSession(getApplicationContext(), Constants.FlurryKey);
	TextView txt_title_create_post = (TextView) findViewById(R.id.txt_title_create_post);
	// final EditText editTitle = (EditText)
	// findViewById(R.id.edit_createPost);
	final EditText editDetail = (EditText) findViewById(R.id.edit_CreateDetail);
	// LinearLayout lnChooseFile = (LinearLayout)
	// findViewById(R.id.ln_create_chooseFile);
	TextView txtPost = (TextView) findViewById(R.id.txt_create_post);
	imgAttachName = (ImageView) findViewById(R.id.img_create_attach);

	// ivSend = (ImageView) findViewById(R.id.ivSend);
	ivCamera = (ImageView) findViewById(R.id.ivCamera);
	ivGallery = (ImageView) findViewById(R.id.ivGallery);

	// txtcancelBottom = (TextView) findViewById(R.id.txt_cancelBottom);
	txtcancelTop = (TextView) findViewById(R.id.txt_create_cancel);
	txtPost = (TextView) findViewById(R.id.txtPost);

	imageLoader = ImageLoader.getInstance();
	options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).showImageOnLoading(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading).showImageOnFail(R.drawable.loading).build();

	if (getIntent().getExtras() != null)
	{
	    GroupID = getIntent().getExtras().getString("gID");
	    isEdit = getIntent().getExtras().getBoolean("IsEdit");

	}

	ivCamera.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		camera(false);
	    }
	});

	ivGallery.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		gallery(false);
	    }
	});

	if (isEdit)
	{

	    if (getIntent().getExtras() != null)
	    {
		Title = getIntent().getExtras().getString("Title");
		Body = getIntent().getExtras().getString("Body");
		attachedImage = getIntent().getExtras().getString("AttachedImage");
		PostID = getIntent().getExtras().getString("PostID");
	    }

	    txt_title_create_post.setText(R.string.edit_post);
	    // editTitle.setText(Html.fromHtml(Title));
	    // editTitle.setText(Title);

	    // String decryptTitle = StringEscapeUtils.unescapeJava(Title);
	    // editTitle.setText(decryptTitle);

	    // txt_title_create_post.setText(R.string.edit_post);

	    String decryptMsg = StringEscapeUtils.unescapeJava(Body);
	    Log.e("OpenChat", "decrypt msg escape=>" + decryptMsg);
	    // editDetail.setText(Html.fromHtml(decryptMsg));

	    editDetail.setText(decryptMsg);

	    // editDetail.setText(Body);
	    if (attachedImage != null && !attachedImage.equalsIgnoreCase("null"))
	    {
		Log.e("OpenChat", "attachedImage=>" + attachedImage);
		imgAttachName.setVisibility(View.VISIBLE);
		imageLoader.displayImage(attachedImage, imgAttachName, options);
	    }
	    else
	    {
		imgAttachName.setVisibility(View.INVISIBLE);
	    }

	}
	else
	{
	    txt_title_create_post.setText(R.string.create_post);
	}

	// txtcancelBottom.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// finish();
	// }
	// });
	txtcancelTop.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		finish();
	    }
	});
	// lnChooseFile.setOnClickListener(new OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	//
	// if (isEdit)
	// {
	// Toast t = Toast.makeText(GroupDetailCreatePost.this,
	// R.string.cant_edit_image, Toast.LENGTH_SHORT);
	// t.show();
	// }
	// else
	// {
	// openUploadoption(false);
	// }
	// }
	// });

	txtPost.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View v)
	    {
		final String Message = Aniways.encodeMessage(editDetail.getText());
		Log.e("################", "  Message : " + Message);

		if ((Message.trim().length() > 0) || imagePath != null)
		{
		    if (isEdit)
		    {
			if (Network.isNetworkConnected(getApplicationContext()))
			{

			    // txtcancelBottom.setEnabled(false);
			    txtcancelTop.setEnabled(false);

			    new SendPostEdited("", Message, PostID, imagePath).execute();

			}
			else
			{
			    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));
			}

		    }
		    else
		    {

			if (Network.isNetworkConnected(getApplicationContext()))
			{
			    new SendPost("", Message, imagePath).execute();

			}
			else
			{
			    AlertUtility.showToast(getApplicationContext(), getString(R.string.networkIssues));

			}

		    }

		}
		// else if (editTitle.getText().toString().trim().length() == 0)
		// {
		// // Bala Code
		// AlertUtility.showToast(getApplicationContext(),
		// getString(R.string.enter_title));
		//
		// // Toast t = Toast.makeText(GroupDetailActivity.this,
		// // R.string.enter_title, Toast.LENGTH_SHORT);
		// // t.show();
		// }
		else if (Message.trim().length() == 0)
		{
		    AlertUtility.showToast(getApplicationContext(), getString(R.string.enter_details));
		}

	    }
	});

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void openUploadoption(Boolean isEdit)
    {

	final Dialog dialog = new Dialog(this);
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
		    camera(false);
		}
		else if (arg2 == 1)
		{
		    gallery(false);
		}
	    }
	});
	dialog.show();

    }

    public void camera(Boolean isEdit2)
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

    public void gallery(Boolean isEdit2)
    {

	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

	if (isEdit2)
	{

	    startActivityForResult(i, EDIT_PICTURE);

	}
	else
	{

	    startActivityForResult(i, SELECT_PICTURE);
	}

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);

	if (requestCode == SELECT_PICTURE)
	{

	    if (resultCode == Activity.RESULT_OK)
	    {/*
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
		    } catch (OutOfMemoryError e1)
		    {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			AlertUtility.showToast(getApplicationContext(), "Image size is too high.  Please select diffrent image");
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
	    */
		
	    
	    /////////////////New code for gnanaoly
		
		try
		{
		    Uri mImageUri = data.getData();
		    STORAGE available_storage = Utils.getStorageWithFreeSpace(mContext);
		    File folder = new File(Utils.getRootPath(mContext, available_storage));
		    if (!folder.isDirectory())
		    {
			folder.mkdir();
		    }
		    File imageFile = new File(Utils.getImagePath(mContext, available_storage, true));
		    if (!imageFile.exists())
		    {
			imageFile.createNewFile();
		    }

		    if (imageFile.exists())
		    {
			Utils.getImagePathFromURI(mContext, mImageUri, imageFile);

			imagePath = imageFile.getPath();
			startActivityForResult(new Intent(mContext, CropperImageActivity.class).putExtra("Path", imagePath), ACTION_REQUEST_CROP);
		    }
		    else
		    {
			Toast.makeText(mContext, getResources().getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
		    }

		} catch (Exception e)
		{
		    Toast.makeText(mContext, getResources().getString(R.string.image_invalid), Toast.LENGTH_LONG).show();
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
		
		
	/*	if (imagePath != null && !imagePath.equals(""))
		{
		    profileImage = new File(imagePath);
			imageDisplay();
		}*/
		
		try
		{
		    startActivityForResult(new Intent(mContext, CropperImageActivity.class).putExtra("Path", imagePath), ACTION_REQUEST_CROP);

		} catch (Exception e)
		{
		}
		
		
	    }

	

	}else if (requestCode == ACTION_REQUEST_CROP)
	{
	 
	    if (resultCode == Activity.RESULT_OK){
		try
		{
		    imagePath = data.getStringExtra("picture_path");
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		
		
	    }
	
	
	 if (imagePath != null && !imagePath.equals(""))
		{
		    profileImage = new File(imagePath);
			imageDisplay();
		}
	
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

		pDialog = new ProgressDialog(GroupDetailCreatePost.this);
		pDialog.setMessage(getResources().getString(R.string.please_wait));
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setCancelable(false);
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
		reqEntity.addPart("key", new StringBody(Constants.ParamKey));
		String unicodeTitle = convertToUnicodeEscaped(Title);

		reqEntity.addPart("title", new StringBody(unicodeTitle));

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

	    } catch (RuntimeException e)
	    {

		DebugReportOnLocat.e(e);

	    } catch (ClientProtocolException e)
	    {

		DebugReportOnLocat.e(e);

	    } catch (IOException e)
	    {
		DebugReportOnLocat.e(e);
	    }

	    return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{

	    try
	    {
		super.onPostExecute(result);

		try
		{

		    if (pDialog != null)
		    {
			if (pDialog.isShowing())
			    pDialog.dismiss();
			pDialog = null;
		    }

		} catch (Exception e)
		{
		    DebugReportOnLocat.e(e);
		}
		// G commented
		// GroupDetailActivity.GROUPDETAILS_LIST.clear();

		// GroupDetailActivity.Offset = 0;
		GroupDetailActivity.isDataCompleted = false;
		// new GetAsyncGroupDetail().execute();
		// GroupPost();
		GroupDetailActivity.ComingFromCreatePost = true;

		Intent data = new Intent();
		setResult(Activity.RESULT_OK, data);

		finish();

	    } catch (IllegalArgumentException e)
	    {
		DebugReportOnLocat.e(e);
	    } catch (Exception e)
	    {
		DebugReportOnLocat.e(e);
	    }
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
		pDialog = new ProgressDialog(GroupDetailCreatePost.this);
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
		reqEntity.addPart("key", new StringBody(Constants.ParamKey));
		// bala code
		String unicodeTitle = convertToUnicodeEscaped(Title);
		reqEntity.addPart("title", new StringBody(unicodeTitle));

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

	    if (pDialog != null)
	    {
		pDialog.dismiss();
		pDialog = null;
	    }
	    // G Commneted
	    // GroupDetailActivity.GROUPDETAILS_LIST.clear();

	    // GroupDetailActivity.Offset = 0;
	    GroupDetailActivity.isDataCompleted = false;
	    // new GetAsyncGroupDetail().execute();
	    // GroupPost();
	    GroupDetailActivity.ComingFromCreatePost = true;
	    finish();
	}

    }
}
