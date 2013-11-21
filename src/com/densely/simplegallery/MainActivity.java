package com.densely.simplegallery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import util.FileUtils;

public class MainActivity extends Activity implements OnClickListener {


	public static String buffway;          // в эту переменную кладёт значение FolderWayPicker
	String mybuffway;

	final int REQUEST_CODE_WAY = 1;

	Button btnBrowse;
	TextView tvWay;

	ImageAdapter myImageAdapter;

    List<String> ImageList;
    private int currentIndex = 0;
    private int maxIndex = 0;
    private float mMinZoomScale = 1;
    SharedPreferences indexPrefs;
    private static final int EXIT = 0;
    static  String DIRECTORY = "/storage/sdcard0/Pictures/";
    private static String DATA_DIRECTORY = "/storage/sdcard0/Pictures";
    private static String DATA_FILE = "/storage/sdcard0/imagelist.dat";


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

			String indexImage = (String) parent.getItemAtPosition(position);

			Intent i = new Intent(getApplicationContext(),
					FullSizeActivity.class);
            transitionOnFullcreen();
            i.putExtra("Path", buffway);
            i.putExtra ("IndexImage", position);
			startActivity(i);

			Log.d("myTag454545", indexImage + position);

		}
	};

    public void transitionOnFullcreen(){
        FullSizeActivity.setTransitionFromGridView();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = "
				+ resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_WAY:

				Log.d("myLogs", buffway);
				this.mybuffway = buffway;

                DIRECTORY = buffway + "/";
                Log.d("RRR123", DIRECTORY);
                DATA_DIRECTORY = buffway + "/";
                Log.d("RRR123", DATA_DIRECTORY);
                DATA_FILE = buffway + "/imagelist.dat";

                currentIndex = 0;

                File data_directory = new File(DATA_DIRECTORY);
                ImageList = FindFiles();
                FileUtils savedata1 = new FileUtils();
                SystemClock.sleep(100);
                savedata1.saveArray(DATA_FILE, ImageList);

                if (!data_directory.exists()) {
                    if (data_directory.mkdir()) {
                        FileUtils savedata = new FileUtils();

                        SystemClock.sleep(100);
                        ImageList = FindFiles();
                        savedata.saveArray(DATA_FILE, ImageList);

                    } else {
                        ImageList = FindFiles();
                    }

                } else {
                    File data_file = new File(DATA_FILE);
                    if (!data_file.exists()) {
                        FileUtils savedata = new FileUtils();

                        SystemClock.sleep(100);
                        ImageList = FindFiles();
                        savedata.saveArray(DATA_FILE, ImageList);
                    } else {
                        FileUtils readdata = new FileUtils();

                        ImageList = readdata.loadArray(DATA_FILE);
                        Log.d("qqq423056", "YO");
                    }
                }
                Log.d("qqq423056", "YO1");
                if (ImageList == null) {
                    quit();
                }
                SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                        MODE_PRIVATE);
                if (indexPrefs.contains("currentIndex")) {
                    currentIndex = indexPrefs.getInt("currentIndex", 0);
                }

				this.workIt(mybuffway);



				break;

			}

		} else {
			Log.d("myLogs", "jhg");
			Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
		}
	}



	public void workIt(String wayfolder) {
		this.tvWay.setText(DIRECTORY);

		myImageAdapter = new ImageAdapter(this);


		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(myImageAdapter);

		String targetPath = wayfolder + "/";

        Log.d("AEAE", wayfolder);

		Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG)
				.show();





		File targetDirector = new File(targetPath);




		File[] files = targetDirector.listFiles();





        for (int i=0; i<ImageList.size(); i++) {

            myImageAdapter.add(ImageList.get(i));
		}

















	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        int NONE = Menu.NONE;
        menu.add(NONE, EXIT, NONE, "Exit");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EXIT:
                quit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onPause() {
        super.onPause();

        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);

        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", currentIndex);
        indexEditor.commit();
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentIndex = indexPrefs.getInt("currentIndex", 0);
        }
    }

    private List<String> FindFiles() {
        List<String> tFileList = new ArrayList<String>();
        Resources resources = getResources();

        // array of valid image file extensions
        String[] imageTypes = resources.getStringArray(R.array.image);
        FilenameFilter[] filter = new FilenameFilter[imageTypes.length];

        int i = 0;
        for (final String type : imageTypes) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {

                    return name.endsWith("." + type);
                }
            };

            i++;
        }

        FileUtils fileUtils = new FileUtils();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(
                new File(DIRECTORY), filter, -1);
        for (File f : allMatchingFiles) {
            tFileList.add(f.getAbsolutePath());
            Log.d("Finded files", f.getAbsolutePath().toString());
        }
        return tFileList;
    }

    public void quit() {
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);

        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", 0);
        indexEditor.commit();


        finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		intent = new Intent(this, FolderWayTaker.class);
		startActivityForResult(intent, REQUEST_CODE_WAY);
	}
}