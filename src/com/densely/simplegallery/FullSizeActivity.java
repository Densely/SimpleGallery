package com.densely.simplegallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FullSizeActivity extends Activity {
	
	TextView tvFileName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullsize);
				
		Intent i = getIntent();

		// Selected image id
		String imageway = i.getExtras().getString("Path");
		

		ImageView ivImgZoom = (ImageView) findViewById(R.id.ivImgZoom);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bmfull = BitmapFactory.decodeFile(imageway, options);
		
		ivImgZoom.setImageBitmap(bmfull); 
		
		tvFileName = (TextView) findViewById(R.id.tvFileName);
		tvFileName.setText(imageway);
		tvFileName.setMovementMethod(new ScrollingMovementMethod());
		
		OnClickListener oclivImgZoom = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		};

		ivImgZoom.setOnClickListener(oclivImgZoom);

	}
}
