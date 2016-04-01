package com.fujifilm.sample.spa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fujifilm.libs.spa.FujifilmSPA;
import com.fujifilm.libs.spa.FFImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rnavinchand on 12/11/15.
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 */
public class MainActivity extends AppCompatActivity implements Picker.PickListener {

    private static final int FujifilmSPASDK_INTENT = 333; //user defined request code for SPA
    private ArrayList<FFImage> images;

    private String apiKey;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private Context mContext = this;
    //RNAV: You only use lower 8 bits for a requestCode, i.e 0 - 255 in decimal
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 255;
    private View mCarouselBackGround;
    private TextView mImageCountTextView;

    private static String TAG = "fujifilm.spa.sdk.sample";

    private Picker mPicker;


    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            updateImageCount(position + 1);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            Log.d(TAG, "Finishing Activity as it should not be top of stack, the last viewed activity is!");
            finish();
            return;

        }

        setContentView(R.layout.activity_main);
        images = new ArrayList<>();

        mPicker = new Picker.Builder(this, this, R.style.MIP_theme)
                .disableCaptureImageFromCamera()
                .setDoneFabIconTintColor(Color.WHITE)
                .build();

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        mCarouselBackGround = findViewById(R.id.carousel_background);
        mImageCountTextView = (TextView) findViewById(R.id.txtview_image_count);
        initImageLoader(this);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_info:
                Toast.makeText(mContext, BuildConfig.VERSION_NAME + "-" + com.fujifilm.libs.spa.BuildConfig.FLAVOR, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mViewPager.addOnPageChangeListener(mPageChangeListener);

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void placeOrder(View v) {
        if (images.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), getString(R.string.no_image_txt), Toast.LENGTH_SHORT).show();
            return;
        }

        EditText edit = (EditText) findViewById(R.id.apiKey);

        apiKey = edit.getText().toString();

        if (apiKey.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), getString(R.string.no_apikey_txt), Toast.LENGTH_SHORT).show();
            return;
        }

        EditText userIdedit = (EditText) findViewById(R.id.user_id);

        String userId = userIdedit.getText().toString();


        RadioGroup rg = (RadioGroup) findViewById(R.id.toggle);
        String environmentRadioValue = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        /*
         ---------------------------------------------------------------------------------------
         Creates a FujifilmSPA instance that handles the order checkout process.
         ---------------------------------------------------------------------------------------

         - Go to http://www.fujifilmapi.com to register for an apiKey.
         - Ensure you have the right apiKey for the right environment.

         @param apiKey: Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com
         @param isLive: A bool indicating which environment your app runs in.  Must match your app’s environment set on http://fujifilmapi.com.
         @param userid: Optional parameter. This can be used to link a user with an order. MaxLength = 50 alphanumeric characters
         @param images: ArrayList of image local path strings or public urls (http://). If using local paths, they can be either relative or absolute paths. Supported image types are jpeg. A maximum of 50 images can be sent in a given Checkout process. If more than 50 images are sent, only the first 50 will be processed.
         *---------------------------------------------------------------------------------------

        */
        //Get Fujifilm SPA SDK singleton class instance
        FujifilmSPA fujifilm = FujifilmSPA.getInstance();
        //call checkout which takes the user into Fujifilm's print products order flow
        fujifilm.checkout(this, FujifilmSPASDK_INTENT, apiKey, environmentRadioValue.equals("LIVE"), userId, images);
    }


    //When called open image picker
    public void selectImages(View v) {
        // Here, thisActivity is the current activity
        //storage permission did not exist in api level 15
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_STORAGE);
            }
            return;
        } else {
            pickImage();
        }

    }

    public void pickImage() {
        mPicker.startActivity();
    }

    public void addURL(View v) {
        new MaterialDialog.Builder(this)
                .title(R.string.add_url_title)
                .input(getString(R.string.url_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input != null && input.length() > 0) {
                            URL url = null;
                            try {
                                url = new URL(input.toString());
                                images.add(new FFImage(url));
                                updateImageCarousel();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .positiveText(R.string.add_txt)
                .negativeText(R.string.cancel_txt)
                .show();
    }

    public void clearImages(View v) {
        if (images.size() > 0) {
            new MaterialDialog.Builder(this)
                    .content(R.string.clear_all_txt)
                    .positiveText(R.string.yes_text)
                    .negativeText(R.string.cancel_txt)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            images.clear();
                            ImageLoader.getInstance().clearMemoryCache();
                            updateImageCarousel();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this.getApplicationContext(), getString(R.string.no_images_clear_txt), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();

                } else {
                    Toast.makeText(this.getApplicationContext(), getString(R.string.permission_denied_storage_msg), Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    //called after we return from another intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FujifilmSPASDK_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_CODE), Toast.LENGTH_LONG).show();
            }

            //If a child app fails for any reason, the parent app will receive RESULT_CANCELLED
            if (resultCode == RESULT_CANCELED && data != null) {


                FujifilmSDKErrorCode errorCode = FujifilmSDKErrorCode.getErrorFromInt((int) data.getSerializableExtra(FujifilmSPA.EXTRA_STATUS_CODE));

                if (errorCode == FujifilmSDKErrorCode.INVALID_API_KEY) {
                    new MaterialDialog.Builder(this)
                            .content(getString(R.string.invalid_apikey_msg))
                            .positiveText(R.string.ok_txt)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_MESSAGE), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        EditText edit = (EditText) findViewById(R.id.apiKey);
        apiKey = edit.getText().toString();

        savedInstanceState.putSerializable("IMAGES", images);
        savedInstanceState.putString("APIKEY", apiKey);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        EditText edit = (EditText) findViewById(R.id.apiKey);
        edit.setText(savedInstanceState.getString("APIKEY"));
        if (images.isEmpty()) {
            images = (ArrayList<FFImage>) savedInstanceState.getSerializable("IMAGES");
            updateImageCarousel();
            updateImageCount(images.size());
        }
    }

    @Override
    public void onPickedSuccessfully(final ArrayList<ImageEntry> imagePaths) {
        for (ImageEntry image : imagePaths) {
            images.add(new FFImage(image.path));
        }
        updateImageCarousel();
    }

    @Override
    public void onCancel() {
        //User canceled the pick activity
    }


    private void updateImageCarousel() {
        ImageCarouselAdapter adapter = new ImageCarouselAdapter(mContext, images);
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        hideOrShowImageCarouselBackground();
    }

    private void hideOrShowImageCarouselBackground() {
        if (images.size() == 0) {
            mCarouselBackGround.setVisibility(View.VISIBLE);
        } else {
            mCarouselBackGround.setVisibility(View.GONE);
            updateImageCount(1);
        }
    }

    private void updateImageCount(int count) {
        String imageSuffix = images.size() > 1 ? getString(R.string.images_suffix) : getString(R.string.image_suffix);
        String text = String.format("%d/%d %s", count, images.size(), imageSuffix);
        mImageCountTextView.setText(text);
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config;
        DisplayImageOptions optionsList;


        optionsList = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_placeholder)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY)
                .defaultDisplayImageOptions(optionsList)
                .denyCacheImageMultipleSizesInMemory()
                .threadPoolSize(3)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }
}
