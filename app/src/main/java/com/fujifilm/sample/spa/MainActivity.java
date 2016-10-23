package com.fujifilm.sample.spa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.FujifilmSPA;
import com.fujifilm.libs.spa.models.FFImageType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 */
public class MainActivity extends AppCompatActivity implements Picker.PickListener {

    private static final int SPA_INTENT = 333; //user defined request code for SPA
    private ArrayList<FFImage> images;

    private String apiKey;
    //private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private Context mContext = this;
    //You only use lower 8 bits for a requestCode, i.e 0 - 255 in decimal
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 255;
    private View mCarouselBackGround;
    private TextView mImageCountTextView;

    private LinearLayout mImageContainer;
    private HorizontalScrollView mImageScroller;
    private ArrayList<ImageView> imageViews;
    private ArrayList<Boolean> imageIsVisible;
    private int lastCenterImageIndex = 0;
    private CountDownTimer scrollUpdateTimer;
    private boolean isScrollListenerAdded = false;

    private static String TAG = "fujifilm.spa.sdk.sample";

    private String landingPage;
    private String catalog;

    private Picker mPicker;

    private boolean onMainPage;
    private android.support.v7.app.ActionBar actionBar;

    public String userID;

    private Toast mToast;

    private String promoCode;

    private String launchLink;

    private final String APIKEY = "APIKEY";
    private final String IMAGES = "IMAGES";
    private final String USER_ID = "USER_ID";
    private final String PROMO_CODE = "PROMO_CODE";

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
        imageViews = new ArrayList<>();
        imageIsVisible = new ArrayList<>();

        mPicker = new Picker.Builder(this, this, R.style.MIP_theme)
                .disableCaptureImageFromCamera()
                .setDoneFabIconTintColor(Color.WHITE)
                .build();

        landingPage = "";
        catalog = "";

        onMainPage = true;
        actionBar = getSupportActionBar();


        //mViewPager = (ViewPager) findViewById(R.id.pager);
        //mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        //mCarouselBackGround = findViewById(R.id.carousel_background);
        //mImageCountTextView = (TextView) findViewById(R.id.txtview_image_count);

        mImageContainer = (LinearLayout) findViewById(R.id.image_container);
        mImageScroller = (HorizontalScrollView) findViewById(R.id.image_scroller);


        Log.d(TAG, "ViewTreeOvserver: " + mImageScroller.getViewTreeObserver().toString());
        mImageScroller.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isScrollListenerAdded) {
                    isScrollListenerAdded = true;
                    mImageScroller.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                        @Override
                        public void onScrollChanged() {
                            if (scrollUpdateTimer == null) {
                                scrollUpdateTimer = new CountDownTimer(250, 250) {

                                    @Override
                                    public void onFinish() {
                                        Log.d(TAG, "onFinish updateImageViews");

                                        onImagesUpdated(false);
                                    }

                                    @Override
                                    public void onTick(long interval) {
                                        Log.d(TAG, "onTick updateImageViews");
                                    }
                                };

                            } else {
                                scrollUpdateTimer.cancel();
                                scrollUpdateTimer.start();
                            }

                        }
                    });
                }
            }
        });


        initImageLoader(this);
        makeActionOverflowMenuShown();

    }

    /**
     * Force show overflow menu on devices that have hw menu button
     * http://stackoverflow.com/a/11438245
     */
    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public void onBackPressed() {
        if(onMainPage) {
            moveTaskToBack(true);
        }
        else {
            actionBar.setTitle("Fujifilm SDK");
            setContentView(R.layout.activity_main);
            onMainPage = true;
        }
    }
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //mViewPager.addOnPageChangeListener(mPageChangeListener);

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        //mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }


    public void placeOrder(View v) {
        if (images.isEmpty()) {
            cancelToast();
            mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.no_image_txt), Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        EditText apikeyEditText = (EditText) findViewById(R.id.apiKey);

        apiKey = apikeyEditText.getText().toString().trim();


        EditText userIDEditText = (EditText) findViewById(R.id.user_id);

        userID = userIDEditText.getText().toString().trim();


        EditText promoCodeEditText = (EditText) findViewById(R.id.promo_code);

        promoCode = promoCodeEditText.getText().toString().trim();

        EditText launchLinkEditText = (EditText) findViewById(R.id.deep_link);

        launchLink = launchLinkEditText.getText().toString().trim();

        String landingPageHash;
        switch (landingPage)
        {
            case "Cart":
                landingPageHash = "#cart";
                break;
            case "Product":
                landingPageHash = "#products";
                break;
            case "Landing Page (Default)":
            default:
                landingPageHash = "#servicetype";
                break;
        }

        String catalog2;
        switch (catalog)
        {
            case "Costco":
                catalog2 = "Costco";
                break;
            case "Rite Aid":
                catalog2 = "RiteAid";
                break;
            case "Sam\'s Club":
                catalog2 = "SamsClub";
                break;
            case "Walmart":
                catalog2 = "Walmart";
                break;
            case "Mail Order":
            default:
                catalog2 = "MailOrder";
                break;
        }

        cancelToast();
        mToast = Toast.makeText(this.getApplicationContext(), landingPageHash + " " + catalog2, Toast.LENGTH_SHORT);
        mToast.show();

        if (apiKey.isEmpty()) {
            cancelToast();
            mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.no_apikey_txt), Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        SwitchCompat retainSwitch = (SwitchCompat) findViewById(R.id.retain_user_info_switch);
        RadioGroup rg = (RadioGroup) findViewById(R.id.toggle);
        String environmentRadioValue = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        //Get Fujifilm SPA SDK singleton class instance
        FujifilmSPA fujifilm = FujifilmSPA.getInstance();
        FujifilmSPA.SdkEnvironment sdkEnvironment;
        if (environmentRadioValue.equals("LIVE")) {
            sdkEnvironment = FujifilmSPA.SdkEnvironment.Production;
        }
        else {
            sdkEnvironment = FujifilmSPA.SdkEnvironment.Preview;
        }

        Map<String, Serializable> extraOptions = null;
        if (launchLink != null && !launchLink.isEmpty()) {
            extraOptions = new HashMap<>();
            extraOptions.put(FujifilmSPA.EXTRA_LAUNCH_LINK, launchLink);
        }

        //call checkout which takes the user into Fujifilm's print products order flow
        fujifilm .checkout(MainActivity.this, SPA_INTENT, apiKey, sdkEnvironment, userID, retainSwitch.isChecked(), images, promoCode, FujifilmSPA.LaunchPage.Home, extraOptions);
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
                                addImageToScroller();

                                //updateImageCarousel();
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
                            imageViews.clear();
                            imageIsVisible.clear();
                            ImageLoader.getInstance().clearMemoryCache();
                            mImageContainer.removeAllViews();
                            //updateImageCarousel();
                        }
                    })
                    .show();
        } else {
            cancelToast();
            mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.no_images_clear_txt), Toast.LENGTH_LONG);
            mToast.show();
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
                    cancelToast();
                    mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.permission_denied_storage_msg), Toast.LENGTH_SHORT);
                    mToast.show();
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

        if (requestCode == SPA_INTENT) {
            if (data.hasExtra(FujifilmSPA.EXTRA_PROMO_ERROR)) {
                cancelToast();
                mToast = Toast.makeText(this.getApplicationContext(), FujifilmSDKPromoError.getErrorFromCode((int)data.getSerializableExtra(FujifilmSPA.EXTRA_PROMO_ERROR)).getDescription(), Toast.LENGTH_LONG);
                mToast.show();
            }
            if (resultCode == RESULT_OK) {
                cancelToast();
                mToast = Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_CODE), Toast.LENGTH_LONG);
                mToast.show();
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
                    cancelToast();
                    mToast = Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_MESSAGE), Toast.LENGTH_LONG);
                    mToast.show();
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
        if(edit != null) {
            apiKey = edit.getText().toString();
            savedInstanceState.putString(APIKEY, apiKey);
        }

        EditText userId = (EditText) findViewById(R.id.user_id);
        if (userId != null) {
            savedInstanceState.putString(USER_ID, userId.getText().toString());
        }

        EditText promoCode = (EditText) findViewById(R.id.promo_code);
        if (promoCode != null) {
            savedInstanceState.putString(PROMO_CODE, promoCode.getText().toString());
        }

        savedInstanceState.putSerializable(IMAGES, images);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        EditText edit = (EditText) findViewById(R.id.apiKey);
        edit.setText(savedInstanceState.getString(APIKEY));

        EditText userId = (EditText) findViewById(R.id.user_id);
        userId.setText(savedInstanceState.getString(USER_ID));

        EditText promoCode = (EditText) findViewById(R.id.promo_code);
        promoCode.setText(savedInstanceState.getString(PROMO_CODE));

        if (images.isEmpty()) {
            images = (ArrayList<FFImage>) savedInstanceState.getSerializable(IMAGES);
            //updateImageCarousel();
            updateImageCount(images.size());
        }
    }

    @Override
    public void onPickedSuccessfully(final ArrayList<ImageEntry> imagePaths) {
        for (ImageEntry image : imagePaths) {
            //Creating Fujifilm image objects, you could can use any of the following constructors below
            //images.add(new FFImage(image.path));

            images.add(new FFImage(image.imageId, image.path));
            addImageToScroller();
        }
        //updateImageCarousel();
    }

    @Override
    public void onCancel() {
        //User canceled the pick activity
    }
    private void hideOrShowImageCarouselBackground() {
        if (images.size() == 0) {
            //mCarouselBackGround.setVisibility(View.VISIBLE);
        } else {
            //mCarouselBackGround.setVisibility(View.GONE);
            updateImageCount(1);
        }
    }

    private void updateImageCount(int count) {
        String imageSuffix = images.size() > 1 ? getString(R.string.images_suffix) : getString(R.string.image_suffix);
        String text = String.format("%d/%d %s", count, images.size(), imageSuffix);
        //mImageCountTextView.setText(text);
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

    private void addImageToScroller(){

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.drawable.img_placeholder);
        LinearLayout parentParent = (LinearLayout) (mImageContainer.getParent().getParent());
        int parentParentHeight = parentParent.getHeight();
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int margin = (int)(parentParentHeight * 0.02);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(parentParentHeight, parentParentHeight);
        layout.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(layout);

        imageViews.add(imageView);
        imageIsVisible.add(false);
        mImageContainer.addView(imageView);

        onImagesUpdated(true);
    }

    private void onImagesUpdated(Boolean forceUpdate){
        int scrollX = mImageScroller.getScrollX(); //for horizontalScrollView
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int scrollCenter = scrollX + width / 2;
        int centerImageIndex = scrollCenter / (int) (mImageScroller.getHeight() * 1.04);
        if(centerImageIndex != lastCenterImageIndex || forceUpdate)
        {
            lastCenterImageIndex = centerImageIndex;
            for(int i = 0; i < imageViews.size(); i++)
            {
                ImageView imageView = imageViews.get(i);
                if (i - centerImageIndex < 4 && i - centerImageIndex > -4) {
                    if( !imageIsVisible.get(i)) {
                        FFImage image = images.get(i);

                        /*
                        if (image.getImageType() == FFImageType.URL) {
                            new ImageLoadTask(image.url.toString(), imageView, parentParentHeight, true).execute();
                        } else {
                            new ImageLoadTask(image.path, imageView, parentParentHeight, false).execute();
                        }
                        */

                        if (image.getImageType() == FFImageType.URL) {
                            ImageLoader.getInstance().displayImage(image.url.toString(), imageView);
                        } else {
                            ImageLoader.getInstance().displayImage(String.format("file://%s", image.path), imageView);
                        }

                        imageIsVisible.set(i, true);
                    }
                }
                else {
                    imageView.setImageResource(R.drawable.img_placeholder);
                    imageIsVisible.set(i, false);
                }
            }
        }
    }

}