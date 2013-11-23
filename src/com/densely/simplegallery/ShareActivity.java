package com.densely.simplegallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.facebook.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Densely
 * Date: 23.11.13
 * Time: 1:03
 * To change this template use File | Settings | File Templates.
 */
public class ShareActivity extends Activity implements View.OnClickListener{

    private static String PATH;
    ImageView ivMyImage;
    Button btnShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ivMyImage = (ImageView) findViewById(R.id.ivMyImage);
        btnShare = (Button) findViewById(R.id.btnShare);

        Intent i = getIntent();
        PATH = i.getStringExtra("Path");


        Drawable d = Drawable.createFromPath(PATH);


        ivMyImage.setImageDrawable(d);

        btnShare.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        publishStory();
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            Log.d("qqq423056", state.toString());
            if (exception != null)
                Log.d("qqq423056", exception.toString());
            if (state == SessionState.OPENED || state == SessionState.OPENED_TOKEN_UPDATED)
                updateView();
        }
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            publishStory();
        }
    }

    private void publishStory() {
        Session session = Session.getActiveSession();

        if (session != null){

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            /*if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }*/

            byte[] data = null;

            Bitmap bi = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/11/test.jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();


            Bundle postParams = new Bundle();
            postParams.putString("description", "Тут должен быть комментарий пользователя");
            postParams.putByteArray("picture", data);

            Request.Callback callback= new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("TAG",
                                "JSON error " + e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        // Toast.makeText(getActivity().getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(getActivity().getApplicationContext(), postId, Toast.LENGTH_LONG).show();
                    }
                }
            };

            Request request = new Request(session, "me/photos", postParams, HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            Log.d("qqq423056", "YO555");
            task.execute();
        }

    }


}
