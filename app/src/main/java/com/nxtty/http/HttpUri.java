package com.nxtty.http;

import com.nxtapp.utils.Constants;

public class HttpUri {
    
    
 // Liver server service URL
 
 	public static String SUBSCRIBER_CONVERSATION=Constants.baseUrl_new + "subscriber/conversation";
 	public static String SUBSCRIBER_UNREAD_MESSAGE=Constants.baseUrl_new + "subscriber/unread_messages";
 	public static String ADD_GROUP=Constants.baseUrl_Group + Constants.ADD_GROUP;
 	
 	public static String APPROVE_REFERRALS=Constants.baseUrl_new+"referrals/approve";
 	public static String VALIDATE_REFERRALS=Constants.baseUrl_new+"referrals/validate";
	public static String IS_DUPLICATE_DEVICE_ID=Constants.baseUrl_new+"subscriber/is_duplicate_device_id";
 	
 	public static String ADD_REFERRALS=Constants.baseUrl_new+"referrals/add";
 	
 	
	public static String SUBSCRIBER_CONTACTS=Constants.baseUrl_new + "subscriber/contacts";
	public static String SUBSCRIBER_PROSPECTIVE_CONTACTS=Constants.baseUrl_new + "subscriber/prospective_contacts";
	public static String SUBSCRIBER_PENDING_REQUESTS=Constants.baseUrl_new + "subscriber/pending_requests";
	public static String GROUPS_FETCH_POST=Constants.baseUrl_Group + "groups/fetch_posts";
	public static String GROUPS_USER_GROUPS=Constants.baseUrl_Group + "groups/user_groups";
	public static String SUBSCRIBER_ACTIVE_CONVERSATION=Constants.baseUrl_new + "subscriber/active_conversations";
	public static String SUBSCRIBER_CONFIRM_PENDING_REQUESTS=Constants.baseUrl_new + "subscriber/confirm_pending_requests";
	public static String SUBSCRIBER_ACTIVITIES=Constants.baseUrl_new+ "subscriber/activities";
	
	public static String GROUPS_CHAT_KEY=Constants.baseUrl_Group + "groups/" + "user_groups";
	
	public static String GROUPS_GET_SUBSCRIBER_POST_TIPS=Constants.baseUrl_Group + "groups/get_subscriber_posts_tips";
	public static String GROUPS_LEADERBOARD = Constants.baseUrl_Group + "groups/top_subscribers_with_tips";
	
}
