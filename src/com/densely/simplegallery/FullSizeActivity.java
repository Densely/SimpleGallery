package com.densely.simplegallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import touch.TouchActivity;
import util.FileUtils;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FullSizeActivity extends TouchActivity {
	
	private static final int EXIT = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    static  String DIRECTORY = "/storage/sdcard0/Pictures/";
    private static String DATA_DIRECTORY = "/storage/sdcard0/Pictures";
    private static String DATA_FILE = "/storage/sdcard0/imagelist.dat";
    View.OnTouchListener gestureListener;
    List<String> ImageList;
    private GestureDetector gestureDetector;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    private int currentView = 0;
    private int currentIndex = 99999;
    private int maxIndex = 0;
    private float mMinZoomScale = 1;
    SharedPreferences indexPrefs;
    private static boolean transitionFromGridView;

    public static void setTransitionFromGridView(){
        transitionFromGridView = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        DIRECTORY = i.getStringExtra("Path") + "/";
        Log.d("RRR123", DIRECTORY);
        DATA_DIRECTORY = i.getStringExtra("Path") + "/";
        Log.d("RRR123", DATA_DIRECTORY);
        DATA_FILE = i.getStringExtra("Path") + "/imagelist.dat";

        if(transitionFromGridView){
            currentIndex = i.getIntExtra("IndexImage", 0);
            transitionFromGridView = false;

            indexPrefs = getSharedPreferences("currentIndex",
                    MODE_PRIVATE);
            SharedPreferences.Editor indexEditor = indexPrefs.edit();
            indexEditor.putInt("currentIndex", currentIndex);
            indexEditor.commit();
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fullsize);
        ImageView iv = (ImageView) findViewById(R.id.zero);

        File data_directory = new File(DATA_DIRECTORY);
        /*ImageList = FindFiles();
        FileUtils savedata1 = new FileUtils();
        SystemClock.sleep(100);
        savedata1.saveArray(DATA_FILE, ImageList);*/

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

        maxIndex = ImageList.size() - 1;

        Log.d("Image #", " "+ImageList.size());

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        Drawable d = Drawable.createFromPath(ImageList.get(currentIndex));

        iv.setImageDrawable(d);
        resetImage(iv, d);
        System.gc();

        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
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
                //quit();

                Intent i = new Intent(getApplicationContext(),
                       ShareActivity.class);

                i.putExtra("Path", ImageList.get(currentIndex));

                startActivity(i);

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



    @Override
    public void resetImage(ImageView iv, Drawable draw) {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        int orientation = 0;
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
            orientation = 0;
        else
            orientation = 1;

        matrix = new Matrix();
        matrix.setTranslate(1f, 1f);
        float scale = 1;

        mMinZoomScale = 1;
        if (orientation == 0) {

            scale = (float) getWindowManager().getDefaultDisplay().getWidth() / (float) draw.getIntrinsicWidth();
            mMinZoomScale = scale;
            matrix.postScale(scale, scale);

            iv.setImageMatrix(matrix);
        } else if (orientation == 1) {
            scale = (float) getWindowManager().getDefaultDisplay().getHeight() / (float) draw.getIntrinsicHeight();
            mMinZoomScale = scale;
            matrix.postScale(scale, scale);

            iv.setImageMatrix(matrix);
        }


        float transX = (float) getWindowManager().getDefaultDisplay().getWidth() / 2
                - (float) (draw.getIntrinsicWidth() * scale) / 2;

        float transY = (float)
                getWindowManager().getDefaultDisplay().getHeight() / 2
                - (float) (draw.getIntrinsicHeight() * scale) / 2;
        matrix.postTranslate(transX, transY);
        iv.setImageMatrix(matrix);
    }

    @Override
    public float getMinZoomScale() {
        return mMinZoomScale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent rawEvent) {
        if (gestureDetector.onTouchEvent(rawEvent))
            return true;


        ImageView view = (ImageView) findViewById(R.id.zero);
        switch (currentView) {
            case 0:
                view = (ImageView) findViewById(R.id.zero);
                break;
            case 1:
                view = (ImageView) findViewById(R.id.one);
                break;
            case 2:
                view = (ImageView) findViewById(R.id.two);
                break;
        }
        onTouchEvented(view, rawEvent);

        return true;
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

    class MyGestureDetector extends SimpleOnGestureListener {


        @Override
        public boolean onDoubleTap(final MotionEvent e) {

            ImageView view = (ImageView) findViewById(R.id.zero);

            switch (currentView) {

                case 0:
                    view = (ImageView) findViewById(R.id.zero);
                    break;
                case 1:
                    view = (ImageView) findViewById(R.id.one);
                    break;
                case 2:
                    view = (ImageView) findViewById(R.id.two);
                    break;
            }

            resetImage(view, view.getDrawable());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);

                    if (currentIndex == maxIndex) {
                        currentIndex = 0;
                    } else {
                        currentIndex = currentIndex + 1;
                    }
                    ImageView iv;
                    Drawable d = Drawable.createFromPath(ImageList
                            .get(currentIndex));

                    if (currentView == 0) {
                        currentView = 1;
                        iv = (ImageView) findViewById(R.id.one);

                        iv.setImageDrawable(d);


                        System.gc();
                    } else if (currentView == 1) {
                        currentView = 2;
                        iv = (ImageView) findViewById(R.id.two);

                        iv.setImageDrawable(d);
                        System.gc();
                    } else {
                        currentView = 0;
                        iv = (ImageView) findViewById(R.id.zero);

                        iv.setImageDrawable(d);
                        System.gc();
                    }
                    resetImage(iv, d);
                    Log.v("ImageViewFlipper", "Current View: " + currentView);
                    viewFlipper.showNext();

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);


                    if (currentIndex == 0) {
                        currentIndex = maxIndex;
                    } else {
                        currentIndex = currentIndex - 1;
                    }
                    ImageView iv;
                    Drawable d = Drawable.createFromPath(ImageList
                            .get(currentIndex));
                    if (currentView == 0) {
                        currentView = 2;
                        iv = (ImageView) findViewById(R.id.two);
                        iv.setImageDrawable(d);
                        System.gc();
                    } else if (currentView == 2) {
                        currentView = 1;
                        iv = (ImageView) findViewById(R.id.one);
                        iv.setImageDrawable(d);
                        System.gc();
                    } else {
                        currentView = 0;
                        iv = (ImageView) findViewById(R.id.zero);
                        iv.setImageDrawable(d);
                        System.gc();
                    }
                    resetImage(iv, d);
                    Log.v("ImageViewFlipper", "Current View: " + currentView);
                    viewFlipper.showPrevious();
                    return true;
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }

}
