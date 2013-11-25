package com.densely.simplegallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import touch.TouchActivity;
import util.FileUtils;



import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FullSizeActivity extends TouchActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    DisplayImageOptions options;
    ViewPager pager;


	private static final int ABOUT = 0;
    final int DIALOG = 1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    View.OnTouchListener gestureListener;
    private GestureDetector gestureDetector;
    private int currentView = 0;
    private int currentIndex = 99999;
    private int maxIndex = 0;
    private float mMinZoomScale = 1;
    SharedPreferences indexPrefs;
    private static boolean transitionFromGridView;
    protected ImageLoader imageLoader;
    String[] sImageList;

    public static void setTransitionFromGridView(){
        transitionFromGridView = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        sImageList = bundle.getStringArray("ImageList");
        currentView = bundle.getInt("IndexImage");
        Log.d("#####", String.valueOf(currentView));


        Log.d("Fatal", "661");

        imageLoader = ImageLoader.getInstance();



        Log.d("Fatal", "662");
        if(transitionFromGridView){
            currentView = bundle.getInt("IndexImage");
            transitionFromGridView = false;
            Log.d("#####", String.valueOf(currentView));
            indexPrefs = getSharedPreferences("currentIndex",
                    MODE_PRIVATE);
            SharedPreferences.Editor indexEditor = indexPrefs.edit();
            indexEditor.putInt("currentIndex", currentView);
            indexEditor.commit();
        }
        Log.d("Fatal", "663");
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        Log.d("Fatal", "664");
        setContentView(R.layout.ac_image_pager);


        //ImageView iv = (ImageView) findViewById(R.id.zero);

        if (sImageList == null) {
            quit();
        }

        Log.d("Fatal", "666");

        SharedPreferences indexPrefs = getSharedPreferences("currentIndex", MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentView = indexPrefs.getInt("currentIndex", 0);

        }

        Log.d("Fatal", "667");
        maxIndex = sImageList.length - 1;

        Log.d("Image #", " "+sImageList.length);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        initImageLoader(getApplicationContext());


        Log.d("Fatal", "668");
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter(sImageList));
        pager.setCurrentItem(currentView);

        Log.d("Fatal", "669");



        //viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        //slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        //slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        //slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        //slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        //viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        //viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        //Drawable d = Drawable.createFromPath(ImageList.get(currentIndex));

        //iv.setImageDrawable(d);
        //resetImage(iv, d);
        //System.gc();

        /*gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };*/
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapter(String[] images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(FullSizeActivity.this, message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        int NONE = Menu.NONE;
        menu.add(NONE, ABOUT, NONE, "About");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ABOUT:
                showDialog(DIALOG);


        }

        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);

        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", currentView);
        indexEditor.commit();
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentView = indexPrefs.getInt("currentIndex", 0);
        }
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

    /*@Override
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
    }*/

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














    /*class MyGestureDetector extends SimpleOnGestureListener {


        *//*@Override
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
        }*//*

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
        *//*    try {
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
            }*//*
            return false;
        }

    }*/

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
