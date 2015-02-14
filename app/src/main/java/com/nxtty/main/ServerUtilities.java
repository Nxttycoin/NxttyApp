/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nxtty.main;

import java.util.Random;

import com.nxtapp.utils.DebugReportOnLocat;

import android.content.Context;
import android.util.Log;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {
	static String TAG = "ServerUtilities";
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	
	static boolean register(final Context context, final String regId,
			String userId) {

		boolean success=false;
		Log.i(TAG,
				"================Inside Register Method==============================");
		Log.i(TAG, "registering device (regId = " + regId + ")");
		Log.i(TAG, "Registering device (UserId = " + userId + ")");
//		String url = "http://flight-mate.co.uk/web_services/user_action.php?action=device_reg&iUserId="
//				+ userId
//				+ "&deviceRegistrationID="
//				+ regId
//				+ "&device_type=android";
//		if (RegisterActivity.connect(url)) {
//
//			try {
//				JSONObject j_obj = new JSONObject(RegisterActivity.result);
//
//				String msg = j_obj.getString("msg");
//				if (msg.equalsIgnoreCase("Success")) {
//					success=true;
//					Log.i(TAG, "Registartion Successfully Done in WS");
//					LoginActivity.registered = true;
//					
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				 DebugReportOnLocat.e(e);
//
//			}
//
//		} else {
//
//		}
		return success;
	}

}
