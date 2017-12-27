package com.fujifilm.sample.spa;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.ffimagepicker.FFAlbumsViewActivity;
import com.example.ffimagepicker.models.ImageSource;
import com.fujifilm.libs.spa.Asset;
import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.FujifilmSPA;
import com.fujifilm.libs.spa.Line;
import com.fujifilm.libs.spa.Page;
import com.fujifilm.libs.spa.PreRenderedOrder;
import com.fujifilm.libs.spa.models.FFImageType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.viewpagerindicator.CirclePageIndicator;

import net.yazeed44.imagepicker.util.Picker;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rnavinchand on 12/11/15.
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 */
public class MainActivity extends AppCompatActivity {

    private static final int SPA_INTENT = 333; //user defined request code for SPA
    private ArrayList<FFImage> images;

    private String apiKey;
    //private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private Context mContext = this;
    //RNAV: You only use lower 8 bits for a requestCode, i.e 0 - 255 in decimal
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

    private Spinner prerenderedSpinner;
    private GridView prerenderedGridView;
    private GridView prerenderedGridViewButtons;

    private ArrayList<String> prerenderedSpinnerList;
    private ArrayAdapter<String> prerenderedSpinnerAdapter;

    private ArrayList<String> prerenderedGridViewNamesList;
    private ArrayList<String> prerenderedGridViewList;
    private ArrayAdapter<String> prerenderedGridViewAdapter;

    private ArrayList<String> prerenderedGridViewButtonsList;
    private ArrayAdapter<String> prerenderedGridViewButtonsAdapter;

    private ViewGroup.LayoutParams prerenderedGridViewParams;
    private ViewGroup.LayoutParams prerenderedGridViewButtonsParams;

    private DisplayMetrics displayMetrics;

    private boolean requestingPermissions = false;

    private final String APIKEY = "APIKEY";
    private final String IMAGES = "IMAGES";
    private final String USER_ID = "USER_ID";
    private final String PROMO_CODE = "PROMO_CODE";
    private final String CUSTOM_DEV_URL = "CUSTOM_DEV_URL";
    private final String PRERENDERED_LIST_FIRST_STRING = "<Select Item>";


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


        landingPage = "";
        catalog = "";

        onMainPage = true;
        actionBar = getSupportActionBar();

        setPrerenderedElements();

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

    //RNAV: Prevents activity from being finished. Going back to the app calls onResume on existing activity in the activity stack
    @Override
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
                cancelToast();
                mToast = Toast.makeText(mContext, BuildConfig.VERSION_NAME + "-" + com.fujifilm.libs.spa.BuildConfig.FLAVOR, Toast.LENGTH_LONG);
                mToast.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (!requestingPermissions) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mImagePickerBroadcastReceiver);
        }
        requestingPermissions = false;
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
        Map<String, Serializable> extraOptions = null;
        SwitchCompat enableAddMorePhotosSwitch = (SwitchCompat) findViewById(R.id.enable_add_more_photos_switch);

        if (enableAddMorePhotosSwitch != null && !enableAddMorePhotosSwitch.isChecked()) {
            if (extraOptions == null) {
                extraOptions = new HashMap<>();
            }
            extraOptions.put(FujifilmSPA.EXTRA_ADD_MORE_PHOTOS_DISABLED, true);
            if (images.isEmpty()) {
                cancelToast();
                mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.no_image_txt), Toast.LENGTH_SHORT);
                mToast.show();
                return;
            }
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

        if (apiKey.isEmpty()) {
            cancelToast();
            mToast = Toast.makeText(this.getApplicationContext(), getString(R.string.no_apikey_txt), Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }

        SwitchCompat retainSwitch = (SwitchCompat) findViewById(R.id.retain_user_info_switch);
        RadioGroup rg = (RadioGroup) findViewById(R.id.toggle);

        //Get Fujifilm SPA SDK singleton class instance
        FujifilmSPA fujifilm = FujifilmSPA.getInstance();
        FujifilmSPA.SdkEnvironment sdkEnvironment;

        if (rg.getCheckedRadioButtonId() == R.id.preview) {
            sdkEnvironment = FujifilmSPA.SdkEnvironment.Preview;
        }
        else {
            sdkEnvironment = FujifilmSPA.SdkEnvironment.Production;
        }


        if (launchLink != null && !launchLink.isEmpty()) {
            if (extraOptions == null) {
                extraOptions = new HashMap<>();
            }
            extraOptions.put(FujifilmSPA.EXTRA_LAUNCH_LINK, launchLink);
        }

        if(prerenderedGridViewList.size() > 0){
            if (extraOptions == null) {
                extraOptions = new HashMap<>();
            }
            extraOptions.put(FujifilmSPA.EXTRA_PRERENDERED_ORDER, createPreRenderedOrderFromArrayList(prerenderedGridViewList));
        }

        //call checkout which takes the user into Fujifilm's print products order flow
        fujifilm.checkout(this, SPA_INTENT, apiKey, sdkEnvironment, userID, retainSwitch.isChecked(), images, promoCode, FujifilmSPA.LaunchPage.Home, extraOptions);
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
    private BroadcastReceiver mImagePickerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(FFAlbumsViewActivity.ACTION_PHOTOS_SELECTED)) {
                HashMap<String, ImageSource> images = (HashMap<String, ImageSource>)intent.getSerializableExtra(FFAlbumsViewActivity.EXTRA_SELECTED_IMAGES);

                if (images == null) {
                    return;
                }
                ArrayList<FFImage> outgoingImages = new ArrayList<>();
                for(String image : images.keySet()) {
                    ImageSource source = images.get(image);
                    if(source != ImageSource.UNDEFINED) {
                        if(source == ImageSource.PRETAG || source == ImageSource.MULTIPLE) {
                            FFImage ffImage = new FFImage(image);
                            outgoingImages.add(ffImage);
                        }
                        if(source == ImageSource.LOCAL || source == ImageSource.MULTIPLE) {
                            FFImage ffImage = new FFImage(image);
                            outgoingImages.add(ffImage);
                        }
                    }
                }
                onFinishedPickingImages(outgoingImages);
            }
        }
    };
    public void pickImage() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FFAlbumsViewActivity.ACTION_PHOTOS_SELECTED);

        LocalBroadcastManager.getInstance(this).registerReceiver(mImagePickerBroadcastReceiver, filter);
        Intent intent = new Intent(this, FFAlbumsViewActivity.class);
        intent.putExtra("fromTaggedImageActivity", false);
        intent.putExtra("fromSdkAddPhotos", true);
//            intent.putExtra(FFAlbumsViewActivity.EXTRA_SELECTED_IMAGES, usedImageIdentifiers);
        intent.putExtra(FFAlbumsViewActivity.EXTRA_MAX_IMAGE_COUNT, 100);
        intent.putExtra(FFAlbumsViewActivity.EXTRA_ALWAYS_ENABLE_DONE_BUTTON, true);
        startActivity(intent);
//        mPicker.startActivity();
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
        requestingPermissions = true;
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


    public void onFinishedPickingImages(ArrayList<FFImage> selectedimages) {
        for (FFImage image : selectedimages) {
            images.add(image);
            addImageToScroller();
        }
    }

    private void setPrerenderedElements(){
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final String[] prerenderedProductsFromStrings = getResources().getStringArray(R.array.dropdown_prerendered);
        final String[] prerenderedProductNamesFromStrings = getResources().getStringArray(R.array.dropdown_prerendered_names);
        int prerenderedProductsCount = prerenderedProductNamesFromStrings.length;
        // int prerenderedProductsCount = prerenderedProductsFromStrings.length;
        String[] prerenderedProducts = new String[prerenderedProductsCount + 1];
        prerenderedProducts[0] = PRERENDERED_LIST_FIRST_STRING;
        System.arraycopy(prerenderedProductNamesFromStrings, 0, prerenderedProducts, 1, prerenderedProductsCount);
        //System.arraycopy(prerenderedProductsFromStrings, 0, prerenderedProducts, 1, prerenderedProductsCount);

        prerenderedSpinner = (Spinner)findViewById(R.id.dropdown_prerendered);
        prerenderedSpinnerList = new ArrayList<String>(Arrays.asList(prerenderedProducts));
        prerenderedSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prerenderedSpinnerList);
        prerenderedSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prerenderedSpinner.setAdapter(prerenderedSpinnerAdapter);
        prerenderedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                if(pos == 0){
                    return;
                }

                String spinnerSelectionName = (String) prerenderedProductNamesFromStrings[pos - 1];
                String spinnerSelectionCode = (String) prerenderedProductsFromStrings[pos - 1];

                /*if(spinnerSelection == PRERENDERED_LIST_FIRST_STRING){
                    return;
                }*/

                prerenderedGridViewNamesList.add(prerenderedGridViewNamesList.size(), spinnerSelectionName);
                prerenderedGridViewList.add(prerenderedGridViewList.size(), spinnerSelectionCode);
                prerenderedGridViewButtonsList.add(prerenderedGridViewButtonsList.size(), "X");
                prerenderedGridViewAdapter.notifyDataSetChanged();
                prerenderedGridViewButtonsAdapter.notifyDataSetChanged();
                resizeGridView(prerenderedGridView, prerenderedGridViewParams, prerenderedGridViewList.size());
                resizeGridView(prerenderedGridViewButtons, prerenderedGridViewButtonsParams, prerenderedGridViewButtonsList.size());
                parent.setSelection(0);
            }

            public void onNothingSelected(AdapterView<?> parent){}
        });

        prerenderedGridView = (GridView)findViewById(R.id.gridView_prerendered);
        prerenderedGridViewParams = prerenderedGridView.getLayoutParams();
        prerenderedGridViewList = new ArrayList<String>();
        prerenderedGridViewNamesList = new ArrayList<String>();
        prerenderedGridViewAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, prerenderedGridViewNamesList);
        prerenderedGridView.setAdapter(prerenderedGridViewAdapter);

        prerenderedGridViewButtons = (GridView)findViewById(R.id.gridView_prerendered_buttons);
        prerenderedGridViewButtonsParams = prerenderedGridViewButtons.getLayoutParams();
        prerenderedGridViewButtonsList = new ArrayList<String>();
        prerenderedGridViewButtonsAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, prerenderedGridViewButtonsList);
        prerenderedGridViewButtons.setAdapter(prerenderedGridViewButtonsAdapter);
        prerenderedGridViewButtons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                prerenderedGridViewNamesList.remove(pos);
                prerenderedGridViewList.remove(pos);
                prerenderedGridViewButtonsList.remove(pos);
                prerenderedGridViewAdapter.notifyDataSetChanged();
                prerenderedGridViewButtonsAdapter.notifyDataSetChanged();
                resizeGridView(prerenderedGridView, prerenderedGridViewParams, prerenderedGridViewList.size());
                resizeGridView(prerenderedGridViewButtons, prerenderedGridViewButtonsParams, prerenderedGridViewButtonsList.size());
            }
        });
    }

    private PreRenderedOrder createPreRenderedOrderFromArrayList(ArrayList<String> items){
        int itemsCount = items.size();
        Line[] lines = new Line[itemsCount];
        String[] allPrerenderedItems = getResources().getStringArray(R.array.prerendered_array);
        String[] prerenderedUrlFragments = getResources().getStringArray(R.array.prerendered_url_fragments);
        for (int i = 0; i < itemsCount; i++){
            String item = items.get(i);
            for(int j = 0; j < allPrerenderedItems.length; j++) {
                String prerenderedItem = allPrerenderedItems[j];
                if(item.equalsIgnoreCase(prerenderedItem)){
                    lines[i] = createPreRenderedOrderLine(prerenderedUrlFragments[j], item, new int[]{1,1,2,12}[j]);
                    break;
                }
            }
        }

        PreRenderedOrder preRenderedOrder = new PreRenderedOrder();
        preRenderedOrder.setLines(lines);

        return preRenderedOrder;
    }

    private Line createPreRenderedOrderLine(String assetHiResIn, String productCodeIn, int numAssets){
        String assetHiResUrl;
        Asset asset;
        Asset[] assets = new Asset[numAssets];
        for(int pageIndex = 0; pageIndex < numAssets; pageIndex++){
            assetHiResUrl = "https://stage.webservices.fujifilmesys.com/imagebank/spaprerendered" + assetHiResIn + pageIndex + ".jpg";
            asset = new Asset("image", assetHiResUrl);
            assets[pageIndex] = asset;
        }

        Page page = new Page();
        page.setAssets(assets);
        Page[] pages = {page};

        Line line = new Line();
        line.setProductCode(productCodeIn);
        line.setPages(pages);

        return line;
    }
    private void resizeGridView(GridView gridView, ViewGroup.LayoutParams layoutParams, int listSize){
        int increment = convertDpToPx(48);
        layoutParams.height = increment * listSize;
        gridView.setLayoutParams(layoutParams);
    }

    private int convertDpToPx(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    //private void updateImageCarousel() {
    //    ImageCarouselAdapter adapter = new ImageCarouselAdapter(mContext, images);
    //    mViewPager.setAdapter(adapter);
    //    //mIndicator.setViewPager(mViewPager);
    //    hideOrShowImageCarouselBackground();
    //}

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

    public void goToLaunchOptions(View view){
        actionBar.setTitle("Launch Options");
        setContentView(R.layout.launch_options);
        onMainPage = false;

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        Spinner landingPageSpinner = (Spinner)findViewById(R.id.dropdown_pages);
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.dropdown_pages, R.layout.spinner_item);
        //landingPageSpinner.setAdapter(adapter);
        for(int i = 0; i < landingPageSpinner.getCount(); i++)
        {
            if (landingPageSpinner.getItemAtPosition(i).toString().equals(landingPage))
            {
                landingPageSpinner.setSelection(i);
                break;
            }
        }


        landingPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> spinner, View view, int i, long l) {
                if (spinner.getItemAtPosition(i).toString().equals("Product"))
                {
                    findViewById(R.id.catalogLabel).setVisibility(View.VISIBLE);
                    findViewById(R.id.dropdown_catalogs).setVisibility(View.VISIBLE);
                    findViewById(R.id.productLabel).setVisibility(View.VISIBLE);
                    findViewById(R.id.dropdown_products).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.catalogLabel).setVisibility(View.GONE);
                    findViewById(R.id.dropdown_catalogs).setVisibility(View.GONE);
                    findViewById(R.id.productLabel).setVisibility(View.GONE);
                    findViewById(R.id.dropdown_products).setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        Spinner catalogSpinner = (Spinner)findViewById(R.id.dropdown_catalogs);
        for(int i = 0; i < catalogSpinner.getCount(); i++)
        {
            if (catalogSpinner.getItemAtPosition(i).toString().equals(catalog))
            {
                catalogSpinner.setSelection(i);
                break;
            }
        }
    }

    public void goToMain(View view){
        Spinner landingPageSpinner = (Spinner)findViewById(R.id.dropdown_pages);
        landingPage = landingPageSpinner.getSelectedItem().toString();

        Spinner catalogSpinner = (Spinner)findViewById(R.id.dropdown_catalogs);
        catalog = catalogSpinner.getSelectedItem().toString();

        actionBar.setTitle("Fujifilm SDK");
        setContentView(R.layout.activity_main);
        onMainPage = true;
    }

}
