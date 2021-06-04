package com.fujifilm.sample.spa;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.models.FFImageType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;

/**
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 */
public class ImageCarouselAdapter extends PagerAdapter {
    private ArrayList<FFImage> images;
    private Context mContext;
    private static final String TAG = "fujifilm.spa.sdk.sample";

    public ImageCarouselAdapter(Context context, ArrayList<FFImage> images) {
        this.images = images;
        this.mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FFImage image = images.get(position);
        ImageSize targetSize = new ImageSize(512, 384);
        if (image.getImageType() == FFImageType.URL) {
            ImageLoader.getInstance().displayImage(image.url.toString(), imageView, targetSize);
        } else {
            ImageLoader.getInstance().displayImage(String.format("file://%s", image.path), imageView, targetSize);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView view = (ImageView) object;
        container.removeView(view);
        view = null;
    }

    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==((ImageView)object);
    }

}