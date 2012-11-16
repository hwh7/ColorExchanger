package com.example.colorexchanger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MainActivity extends FacebookActivity {

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
			Log.d("hwh", "hwh: error in loading opencv");
		} else {
		}
	}

	private static final int PICTURE_SELECTED = 0;
	private static final int PICTURE_TAKEN = 1;

	private Facebook facebook;
	ImageView imageView;
	Bitmap exchangingImg;
	Uri exchangingImgUri;
	int exchangingRGB = 0xffffff;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button shareViaMessageButton = (Button) findViewById(R.id.button1);
		shareViaMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND);

				shareIntent.setType("*/*");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "R:"
						+ ((exchangingRGB & 0xFF0000) >>> 16) + ", G: "
						+ ((exchangingRGB & 0x00FF00) >>> 8) + ", B: "
						+ (exchangingRGB & 0x0000FF));
				// shareIntent.setType("image/*");
				shareIntent.putExtra(Intent.EXTRA_STREAM, exchangingImgUri);
				startActivity(shareIntent);
			}
		});

		ImageButton shareViaFbButton = (ImageButton) findViewById(R.id.imageButton2);
		shareViaFbButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publishStory();
				// Intent shareIntent = new Intent(Intent.ACTION_SEND);
				//
				// shareIntent.setType("image/*");
				// Uri uri = Uri
				// .parse("content://media/external/images/media/63650");
				// shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
				// startActivity(shareIntent);

				// Intent intent = new Intent(MainActivity.this,
				// FacebookManager.class); // 두번째 액티비티를 실행하기 위한 인텐트
				// startActivity(intent); // 두번째 액티비티를 실행합니다.

			}
		});

		final ImageButton colorButton = (ImageButton) findViewById(R.id.imageButton1);
		colorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String items[] = { "Import from Gallery",
						"Take a Picture" };
				new AlertDialog.Builder(MainActivity.this)
						.setIcon(R.drawable.ic_launcher).setTitle("Share")
						.setItems(items, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0) {
									Uri uri = Uri
											.parse("content://media/external/images/media");
									Intent intent = new Intent(
											Intent.ACTION_VIEW, uri);
									intent.setAction(Intent.ACTION_GET_CONTENT);
									intent.setType("image/*");
									startActivityForResult(intent,
											PICTURE_SELECTED);
								} else if (item == 1) {
									Intent intent = new Intent(
											android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

									intent.putExtra(
											android.provider.MediaStore.EXTRA_OUTPUT,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
													+ "/xxx");
									startActivityForResult(intent,
											PICTURE_TAKEN);
								}
							}
						}).show();
			}
		});

		EditText editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setText("255");

		EditText editText2 = (EditText) findViewById(R.id.editText2);
		editText2.setText("255");

		EditText editText3 = (EditText) findViewById(R.id.editText3);
		editText3.setText("255");

		imageView = (ImageView) findViewById(R.id.imageView1);
		Uri uri = Uri.parse("content://media/external/images/media/63650");
		exchangingImgUri = uri;

		ContentResolver cr = getContentResolver();
		InputStream in = null;
		try {
			in = cr.openInputStream(uri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 8;
		exchangingImg = BitmapFactory.decodeStream(in, null, options);
		imageView.setImageBitmap(exchangingImg);

		imageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Mat mat = new Mat();
				Utils.bitmapToMat(exchangingImg, mat);
				// try {
				// mat = Utils.loadResource(getApplicationContext(),
				// R.drawable.lee);
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

				Mat mat2 = new Mat(mat.width(), mat.height(), mat.type());
				Imgproc.Sobel(mat, mat2, mat.depth(), 1, 1);
				Bitmap bitmap = Bitmap.createBitmap(mat2.cols(), mat2.rows(),
						Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(mat2, bitmap);
				imageView.setImageBitmap(bitmap);
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null)
			return;

		try {
			switch (requestCode) {
			case PICTURE_SELECTED:
				if (!intent.getData().equals(null)) {
					Uri uri = intent.getData();
					exchangingImgUri = uri;
					exchangingImg = Images.Media.getBitmap(
							getContentResolver(), uri);
					imageView.setImageBitmap(exchangingImg);
				}
				break;
			case PICTURE_TAKEN:
				Uri uri = intent.getData();// content uri of photo in media
				exchangingImgUri = uri;
				imageView.setImageBitmap(exchangingImg);
				break;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		// user has either logged in or not ...
		if (state.isOpened()) {
			// publishStory();
			// make request to the /me API
			Request request = Request.newUploadPhotoRequest(this.getSession(),
					exchangingImg, new Request.Callback() {

						@Override
						public void onCompleted(Response response) {
							// TODO Auto-generated method stub
						}
					});
			Request.executeBatchAsync(request);

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

	@SuppressWarnings("deprecation")
	private void publishStory() {
		facebook = new Facebook(getResources().getString(R.string.app_id));
		this.openSession();

		final Session session = Session.getActiveSession();
		// Log.d("hwh", "token : " + session.getAccessToken());
		postToFbWall(session);
		// if (session != null) {
		// facebook.authorize(this, new String[] { "publish_stream" },
		// new DialogListener() {
		//
		// @Override
		// public void onFacebookError(FacebookError e) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onError(DialogError dialogError) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onComplete(Bundle values) {
		// postToFbWall(session);
		// }
		//
		// @Override
		// public void onCancel() {
		// // TODO Auto-generated method stub
		// }
		// });
		// }

	}

	public void postToFbWall(Session session) {
		Bundle params = new Bundle();

		params.putString(Facebook.TOKEN, session.getAccessToken());
		params.putString("message", "R:" + ((exchangingRGB & 0xFF0000) >>> 16)
				+ ", G: " + ((exchangingRGB & 0x00FF00) >>> 8) + ", B: "
				+ (exchangingRGB & 0x0000FF));

		// // The byte array is the data of a picture.
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// exchangingImg.compress(CompressFormat.JPEG, 100, stream);
		// byte[] byteArray = stream.toByteArray();
		// params.putByteArray("picture", byteArray);

		facebook.dialog(this, "stream.publish", params,
				new PostDialogListener());
	}

	public class PostDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			Log.d("hwh", "fb upload complete");
		}

		@Override
		public void onFacebookError(FacebookError e) {
		}

		@Override
		public void onError(DialogError e) {
		}

		@Override
		public void onCancel() {
		}

	}
}
