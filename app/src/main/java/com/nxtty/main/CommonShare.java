package com.nxtty.main;

import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.nxtapp.adapter.ShareListAdapter;
import com.nxtapp.classes.Item;
import com.nxtapp.utils.Constants;
import com.nxtapp.utils.DebugReportOnLocat;
import com.nxtty.callback.ResultCallback;
import com.nxtty.http.AsyncHttpRequest;
import com.nxtty.http.HttpUri;
import com.nxtty.http.JSONBody;

public class CommonShare {

	

	private static Item[] items;

	
	Context mcontext;

	public void share(Context mContext) {
	    
	    
		this.mcontext = mContext;
		
		final PackageManager pkgMgr = mContext.getPackageManager();

		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("text/plain");

		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentActivities(sendIntent, 0);

		items = new Item[list.size()];

		
		int i = 0;
		for (ResolveInfo rInfo : list) {

			Drawable d = rInfo.activityInfo.applicationInfo.loadIcon(pkgMgr);
			Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" Bitmap W " + bitmap.getWidth() + " H "
					+ bitmap.getHeight());
			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(pkgMgr + rInfo.activityInfo.name + " ;;;; "
					+ " pkg " + rInfo.activityInfo.packageName + " :::: "
					+ rInfo.activityInfo.toString());

			items[i] = new Item(rInfo.activityInfo.applicationInfo.loadLabel(
					pkgMgr).toString(), bitmap, rInfo.activityInfo.name,
					rInfo.activityInfo.packageName);

			i++;
		}

		/*System.out
				.println("==================================================");
		System.out
				.println("==================================================");
		System.out
				.println("==================================================");*/
		ShareListAdapter adapter = new ShareListAdapter(mContext, items);

		new AlertDialog.Builder(mContext).setTitle(" Nxtty app share using...")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						try {
						    
						    String code= codeGeneration();
						    
						    addReffralcode(code);

							if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln("**pressed item "
									+ items[item].shareActivityName.trim());

							
							
							
							/*if (items[item].PackageName.trim().startsWith(
									"com.facebook.katana")) {

								
							
							

								
								redirectFaceBookPost(mcontext);//Just commented
								

							} else*/
							if (items[item].PackageName.trim().startsWith(
									"com.twitter.android")) {

								CommonShare.sendPlainTwiterShare( item,code,
										mcontext);

								
								if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" twiter activity ");

							} else if (items[item].PackageName.trim()
									.startsWith("com.google.android.gm") ||items[item].PackageName.trim()
									.startsWith("com.htc.android.mail") || items[item].PackageName.trim()
									.startsWith("com.yahoo.mobile.client.android.mail") ||  items[item].PackageName.trim()
									.contains("mail")) {

								CommonShare.sendMail( item,code, mcontext);

							
							} else {
								
								
								if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" Without options ");
								if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" Without options ");
								if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" Without options ");

								try {


									
									String body = new String();
							
									body="It's FREE and Nxtty is a messenger which allows you to "
										+ "send encrypted messages. Referral code: "+code +"  Please "
											+ "find download link http://nxtty.com/app";
									

									//String st = body;

									String bodyData = body.trim();/*.substring(
											0, Math.min(body.trim().length(), 140));
*/
									if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" bodyData "
											+ bodyData);

									String activity = items[item].shareActivityName.trim();
									if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" Activity name "+activity);
									  Intent sendIntent = new Intent(Intent.ACTION_SEND, null);
									  sendIntent.addCategory(Intent.CATEGORY_DEFAULT); 
									  
									  sendIntent.setType("text/plain");
									 // sendIntent.setFlags(0x3000000);
									  
									  sendIntent.setClassName(
											items[item].PackageName.trim(),	activity);

									
									  sendIntent.putExtra(
											Intent.EXTRA_TEXT,bodyData.trim() );
									
								/*	shareIntent1.putExtra(
											Intent.EXTRA_TEXT, "Hello test");*/

									mcontext.startActivity(sendIntent);

								

								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).show();

	}

	final static Intent emailIntent = new Intent(
			android.content.Intent.ACTION_SEND);



	public static boolean sendMail( int position,String code,
			Context mContext) {

		if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" send mail ");
		if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" send mail ");
		if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" send mail ");
		
		try {


		    //http://nxtty.com/app
		    
		//    + "<br>Follow us on Twitter: <a href='https://twitter.com/@cryptomessenger'>@cryptomessenger</a>"
		//	+ "<br>Follow us on Facebook: <a href='https://www.facebook.com/nxtty.official'>Nxtty</a>"
			StringBuilder Body = new StringBuilder();

			String Subject = "Download Nxtty...";

			String body = "<html><body><p align='Justify'>";
			body = body
					+ "It's FREE and Nxtty is a crypto messenger which allows you to send encrypted messages to one or more users at a time.  "
					+ "Please find download link http://nxtty.com/app "
					+ ""
					+ " </p>"
					+"<p align='Justify'> Referral code: <strong>"+code +"</strong></p>"
					    
					+ "<p align='Justify'>"
					+ "<b>CRYPTOMESSENGER</b> - All messages are encrypted from app to app, so your messages are safe and secure."
					+ "<br><b>MESSAGE DESTRUCTION</b> - Automatic & manual message delete options provide you more ways to remove messages."
					+ "<br><b>PRIVACY PROTECTION</b> - Nxtty does not use your phone contact list or social media connections."
					+ "<br><b>OPEN CHAT</b> - Chat room style messaging open to anyone where you can post images, comments, "
					+ "links while discussing great topics."
					+ "<br><b>TIPPING</b>- Send Tips of Nxttycoins instead of likes, post great content and get Tipped in the "
					+ "Nxttycoin crypto currency."
					+ ""
					+ "<br>"
					+ "<br>"
					+ "<b>Special Features</b><br>"
					+ "<br>- App to App calling free of charge within the Nxtty crypto messenger."
					+ "<br>- Discover friends in any city."
					+ "<br>- Tipping is fun and simple. Just hit the red button on any post that you like."
					+ "<br>- Create your own profile and begin posting!"
					+ "<br><br>Email us via: support@nxtty.com"
					+ "<br>Follow us on Twitter: twitter.com/@cryptomessenger"
					+ "<br>Follow us on Facebook: www.facebook.com/nxtty.official"
					+ "<br>Visit us at: <a href='www.nxtty.com'>nxtty.com</a>"
					+ "</p>"
					+ "</body></html>";
			
			Body.append(body + "<br>");
			Body.append("<br>");

			Body.append("<br>");

			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln("HTML format mail");
			Intent shareIntent = new Intent(Intent.ACTION_SEND);

			shareIntent.setClassName(items[position].PackageName,
					items[position].shareActivityName);
			shareIntent.setType("text/html");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
			shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
			mContext.startActivity(shareIntent);

			return false;
		

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static boolean sendPlainTwiterShare(int position,String code,Context mContext) {

		try {
			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" sendPlainTwiterShare ");	
			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" sendPlainTwiterShare ");
			if (DebugReportOnLocat.dbg)DebugReportOnLocat.ln(" sendPlainTwiterShare ");
			

				emailIntent.setType("text/plain");

				
				String st ="It's FREE and Nxtty is a messenger which allows you to send encrypted messages. Referral code: "+code +" Download Link http://nxtty.com/app";
				
			

				String bodyData = st.trim().substring(0,Math.min(st.trim().length(), 140));

				Intent shareIntent = new Intent(Intent.ACTION_SEND);

				String activity = items[position].shareActivityName.trim();
				shareIntent.setType("text/plain");
				shareIntent.setClassName(items[position].PackageName, activity);
				shareIntent.putExtra(Intent.EXTRA_TEXT, bodyData);

				mContext.startActivity(shareIntent);

				return false;
		

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	
	
	
	 private void addReffralcode(String code)
	    {

		AsyncHttpRequest req = new AsyncHttpRequest(HttpUri.ADD_REFERRALS);
		req.setBody(JSONBody.addReferral(Constants.NxtAcId, code, Constants.ParamKey));
		req.setUserInfo(HttpUri.ADD_REFERRALS);
		DebugReportOnLocat.ln("url >>>" + HttpUri.ADD_REFERRALS);
		// final CategoryManager cManager = new CategoryManager(this);
		//new AsyncHttpClient()
		
		new com.nxtty.http.AsyncHttpClient().executeJSONObject(mcontext, req, new ResultCallback()
		{

		    @Override
		    public void onCompleted(Exception e, String responseString, String methodInfo)
		    {

			System.out.println(" responseString" + responseString);

			// responseString{"status":false,"errorDetail":"Invalid or epxire code!"}

		    }

		});

	    }

	    String codeGeneration()
	    {

		StringBuilder activationCode = new StringBuilder();
		try
		{

		    Random random = new Random();

		    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		    int Low = 0;
		    int High = 9;
		    Integer R = random.nextInt(High - Low) + Low;

		    if (Constants.AliasName != null)
		    {

			if (Constants.AliasName.length() > 1)
			{

			    activationCode.append(Constants.AliasName.charAt(0));// 1
			}
			else
			{

			    int character = (int) (Math.random() * 26);

			    String s = alphabet.substring(character, character + 1);
			    activationCode.append(s);
			}

		    }

		    R = random.nextInt(High - Low) + Low;

		    activationCode.append(R.toString());// 2

		    if (Constants.AliasName != null)
		    {

			if (Constants.AliasName.length() > 1)
			    activationCode.append(Constants.AliasName.charAt(Constants.AliasName.trim().length() - 1));// 3
		    }

		    R = random.nextInt(High - Low) + Low;

		    activationCode.append(R.toString().trim());// 4

		    R = random.nextInt(High - Low) + Low;

		    activationCode.append(R.toString());// 5

		    R = random.nextInt(High - Low) + Low;

		    activationCode.append(R.toString().trim());// 6

		} catch (Exception e)
		{
		    e.printStackTrace();
		}

		return activationCode.toString();
	    }
}




