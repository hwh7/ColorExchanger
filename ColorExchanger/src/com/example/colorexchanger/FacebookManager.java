package com.example.colorexchanger;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookActivity;
import com.facebook.FacebookException;
import com.facebook.GraphUser;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookManager extends FacebookActivity {
	private Facebook facebook;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facebook = new Facebook(getResources().getString(R.string.app_id));
		this.openSession();
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		// user has either logged in or not ...
		if (state.isOpened()) {
			// publishStory();
			// make request to the /me API
//			Request request = Request.newUploadPhotoRequest(this.getSession(), imageView.
//					new Request.GraphUserCallback() {
//						// callback after Graph API response with user object
//						@Override
//						public void onCompleted(GraphUser user,
//								Response response) {
//							if (user != null) {
//								Log.d("hwh", "facebook logged in");
//							}
//						}
//					});
//			Request.executeBatchAsync(request);

			// Bundle params = new Bundle();
			// params.putString("message",
			// "Learn how to make your Android apps social");
			// facebook.dialog(this, "apprequests", params, new DialogListener()
			// {
			// @Override
			// public void onComplete(Bundle values) {
			// final String requestId = values.getString("request");
			// if (requestId != null) {
			// Toast.makeText(
			// FacebookManager.this.getApplicationContext(),
			// "Request sent", Toast.LENGTH_SHORT).show();
			// } else {
			// Toast.makeText(
			// FacebookManager.this.getApplicationContext(),
			// "Request cancelled", Toast.LENGTH_SHORT).show();
			// }
			// }
			//
			// @Override
			// public void onFacebookError(FacebookError error) {
			// }
			//
			// @Override
			// public void onCancel() {
			// Toast.makeText(FacebookManager.this.getApplicationContext(),
			// "Request cancelled", Toast.LENGTH_SHORT).show();
			// }
			//
			// @Override
			// public void onError(DialogError e) {
			// // TODO Auto-generated method stub
			//
			// }
			// });
		}
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {

			// Check for publish permissions
			// List<String> permissions = session.getPermissions();
			// if (!isSubsetOf(PERMISSIONS, permissions)) {
			// pendingPublishReauthorization = true;
			// Session.ReauthorizeRequest reauthRequest = new
			// Session.ReauthorizeRequest(
			// this, PERMISSIONS).setRequestCode(REAUTH_ACTIVITY_CODE);
			// session.reauthorizeForPublish(reauthRequest);
			// return;
			// }

			Bundle postParams = new Bundle();
			postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption",
					"Build great social apps and get more installs.");
			postParams
					.putString(
							"description",
							"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			postParams.putString("link",
					"https://developers.facebook.com/android");
			postParams
					.putString("picture",
							"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i("hwh", "JSON error " + e.getMessage());
					}
					FacebookException error = response.getError();
					if (error != null) {
						Toast.makeText(
								FacebookManager.this.getApplicationContext(),
								error.getMessage(), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								FacebookManager.this.getApplicationContext(),
								postId, Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}
}
