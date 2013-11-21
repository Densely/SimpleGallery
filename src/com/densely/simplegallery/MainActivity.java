package com.densely.simplegallery;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {


	public static String buffway;          // в эту переменную кладёт значение FolderWayPicker
	String mybuffway;

	final int REQUEST_CODE_WAY = 1;

	Button btnBrowse;
	TextView tvWay;

	ImageAdapter myImageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnBrowse = (Button) findViewById(R.id.btnBrowse);
		btnBrowse.setOnClickListener(this);

		tvWay = (TextView) findViewById(R.id.tvWay);

		GridView gridview = (GridView) findViewById(R.id.gridview);

		if (mybuffway != null) {
			tvWay.setText(mybuffway);
		} else {

		}

		gridview.setOnItemClickListener(myOnItemClickListener);
	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			String prompt = (String) parent.getItemAtPosition(position);

			Intent i = new Intent(getApplicationContext(),
					FullSizeActivity.class);

			i.putExtra("Path", buffway);
			startActivity(i);

			Log.d("myTag", prompt);

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = "
				+ resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_WAY:

				Log.d("myLogs", buffway);
				this.mybuffway = buffway;

				this.workIt(mybuffway);
				break;

			}

		} else {
			Log.d("myLogs", "jhg");
			Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
		}
	}

	public void workIt(String wayfolder) {
		this.tvWay.setText(mybuffway);

		myImageAdapter = new ImageAdapter(this);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(myImageAdapter);

		String targetPath = wayfolder + "/";

        Log.d("AEAE", wayfolder);

		Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG)
				.show();
		File targetDirector = new File(targetPath);

		File[] files = targetDirector.listFiles();
		for (File file : files) {
			myImageAdapter.add(file.getAbsolutePath());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		intent = new Intent(this, FolderWayTaker.class);
		startActivityForResult(intent, REQUEST_CODE_WAY);
	}
}