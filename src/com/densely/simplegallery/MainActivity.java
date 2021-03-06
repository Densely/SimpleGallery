package com.densely.simplegallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;


import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import util.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {


    private static final int EXIT = 0;
    public static String buffway;
    static String DIRECTORY;
    private static String DATA_DIRECTORY;
    private static String DATA_FILE;
    final int REQUEST_CODE_WAY = 1;
    final int DIALOG = 1;
    SharedPreferences prefsFirstLaunch = null;




    String mybuffway;
    Button btnBrowse;
    TextView tvWay;
    ImageAdapter myImageAdapter;
    List<String> ImageList;
    ImageView ivBackGround;
    int widthScreen;
    String[] sImageList = new String[1];
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent i = new Intent(getApplicationContext(), FullSizeActivity.class);
            transitionOnFullcreen();
            i.putExtra("Path", buffway);
            i.putExtra("IndexImage", position);
            startActivity(i);

        }
    };

    private int currentIndex = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        prefsFirstLaunch = getSharedPreferences("firstrun", MODE_PRIVATE);
        btnBrowse = (Button) findViewById(R.id.btnBrowse);
        btnBrowse.setOnClickListener(this);

        tvWay = (TextView) findViewById(R.id.tvWay);
        ivBackGround = (ImageView) findViewById(R.id.ivBackGround);
        GridView gridview = (GridView) findViewById(R.id.gridview);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        initImageLoader(getApplicationContext());
        if (DIRECTORY != null) {
            tvWay.setText(DIRECTORY);
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        try {
            display.getSize(size);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            size.x = display.getWidth();
            size.y = display.getHeight();
        }

        widthScreen = size.x;

        setFirstImageBackGround();
        gridview.setOnItemClickListener(myOnItemClickListener);

    }
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
    public void transitionOnFullcreen() {
        FullSizeActivity.setTransitionFromGridView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = "
                + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_WAY:


                    this.mybuffway = buffway;

                    DIRECTORY = buffway + "/";
                    DATA_DIRECTORY = buffway + "/";
                    DATA_FILE = buffway + "/imagelist.dat";

                    currentIndex = 0;

                    File data_directory = new File(DATA_DIRECTORY);

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

                        }
                    }

                    if (ImageList == null) {
                        quit();
                    }
                    SharedPreferences indexPrefs = getSharedPreferences("currentIndex", MODE_PRIVATE);

                    if (indexPrefs.contains("currentIndex")) {
                        currentIndex = indexPrefs.getInt("currentIndex", 0);
                    }

                    if (ImageList.size() == 0) {
                        setNoImageBackGround();
                    } else {
                        deleteBackGround();

                    }

                    sImageList = new String[ImageList.size()];
                    sImageList = ImageList.toArray(sImageList);
                    int i = 0;
                    for(String s : sImageList){
                        sImageList[i] = "file://" + s;
                        i++;
                        Log.d("Files666", s);
                    }


                    this.workIt();

                    break;
            }

        }
    }

    public void workIt(){
        this.tvWay.setText(DIRECTORY);



        GridView gridview = (GridView) findViewById(R.id.gridview);

        gridview.setAdapter(new ImageAdapter());





        //myImageAdapter.setSize((int) (widthScreen/3.18));

        //gridview.setAdapter(myImageAdapter);

        /*Toast.makeText(getApplicationContext(), DIRECTORY, Toast.LENGTH_LONG).show();

        for (String aImageList : ImageList) {
            Log.d("File Name", aImageList);
            myImageAdapter.add(aImageList);

        }*/
    }

    public class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sImageList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            imageLoader.displayImage(sImageList[position], imageView, options);

            return imageView;
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

        SharedPreferences indexPrefs = getSharedPreferences("currentIndex", MODE_PRIVATE);
        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", currentIndex);
        indexEditor.commit();
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex", MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentIndex = indexPrefs.getInt("currentIndex", 0);
        }
        if (prefsFirstLaunch.getBoolean("firstrun", true)) {
            showDialog(DIALOG);
            prefsFirstLaunch.edit().putBoolean("firstrun", false).commit();
        }

    }

    private List<String> FindFiles() {
        List<String> tFileList = new ArrayList<String>();
        Resources resources = getResources();


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

    protected void quit() {
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

        Intent intent;
        intent = new Intent(this, FolderWayTaker.class);
        startActivityForResult(intent, REQUEST_CODE_WAY);
    }

    public void setFirstImageBackGround() {

        ivBackGround.setImageResource(R.drawable.ic_splash_bg_first);
        ivBackGround.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout llMain = (LinearLayout)findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.rgb(206, 206, 206));
        LinearLayout llDevide = (LinearLayout)findViewById(R.id.llDevide);
        llDevide.setBackgroundColor(Color.BLACK);
    }

    public void setNoImageBackGround() {

        ivBackGround.setImageResource(R.drawable.ic_splash_bg_no_image);
        ivBackGround.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout llMain = (LinearLayout)findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.rgb(206, 206, 206));;
        LinearLayout llDevide = (LinearLayout)findViewById(R.id.llDevide);
        llDevide.setBackgroundColor(Color.BLACK);
    }

    public void deleteBackGround() {

        int width = 0;
        int height = 0;
        LinearLayout.LayoutParams parms3 = new LinearLayout.LayoutParams(width,height);
        ivBackGround.setLayoutParams(parms3);
        LinearLayout llMain = (LinearLayout)findViewById(R.id.llMain);
        llMain.setBackgroundColor(R.color.redBlack);
        LinearLayout llDevide = (LinearLayout)findViewById(R.id.llDevide);
        llDevide.setBackgroundColor(Color.rgb(206, 206, 206));
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("About");

        LinearLayout view = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.about, null);

        adb.setView(view);

        return adb.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {

            TextView tvAbout = (TextView) dialog.getWindow().findViewById(
                    R.id.tvAbout);
            TextView tvAbout1 = (TextView) dialog.getWindow().findViewById(
                    R.id.tvAbout1);
            TextView tvAbout2 = (TextView) dialog.getWindow().findViewById(
                    R.id.tvAbout2);
            TextView tvAbout3 = (TextView) dialog.getWindow().findViewById(
                    R.id.tvAbout3);
            TextView tvAbout4 = (TextView) dialog.getWindow().findViewById(
                    R.id.tvAbout4);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "fonts/arial.ttf");
            tvAbout.setTypeface(face);
            tvAbout1.setTypeface(face);
            tvAbout2.setTypeface(face);
            tvAbout3.setTypeface(face);
            tvAbout4.setTypeface(face);
        }
    }


}