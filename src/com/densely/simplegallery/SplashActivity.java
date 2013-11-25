package com.densely.simplegallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class SplashActivity extends Activity implements Runnable{

    Animation splashAnim = null;
    private static final int DELAY = 3000;
    ImageView ivSplash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash);
        ivSplash = (ImageView) findViewById(R.id.ivSplash);
        ivSplash.startAnimation(splashAnim);

        Handler handler = new Handler();
        handler.postDelayed(this, DELAY);

    }


    @Override
    public void run() {

        ivSplash.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }




}
