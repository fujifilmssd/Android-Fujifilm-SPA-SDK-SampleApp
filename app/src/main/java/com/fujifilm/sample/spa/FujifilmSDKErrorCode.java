package com.fujifilm.sample.spa;

import android.app.Activity;

/**
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 *
 * This is a sample enum containing possible error messages returned by SPA Mobile Web. This may need to
 * be updated as more error codes are added.
 */
public enum FujifilmSDKErrorCode {
    FATAL_ERROR("FATAL_ERROR", 0),
    NO_IMAGES_UPLOADED("NO_IMAGES_UPLOADED", 1),
    NO_INTERNET("NO_INTERNET", 2),
    INVALID_API_KEY("INVALID_API_KEY", 3),
    USER_CANCELED("USER_CANCELED", 4),
    NO_VALID_IMAGES("NO_VALID_IMAGES", 5),
    TIME_OUT("TIME_OUT",6),
    ORDER_COMPLETE("ORDER COMPLETE",7),
    UPLOAD_FAILED("UPLOAD_FAILED",8),
    USERID_INVALID_FORMAT("USERID_INVALID_FORMAT", 9),
    PROMOCODE_INVALID_FORMAT("PROMOCODE_INVALID_FORMAT", 10);

    private String stringValue;
    private int intValue;

    FujifilmSDKErrorCode(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }
    public static FujifilmSDKErrorCode getErrorFromInt(int code) {
        return FujifilmSDKErrorCode.values()[code];
    }
    public int getValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
    public static String GetMessage(FujifilmSDKErrorCode code, Activity activity) {
        return GetMessage(code, activity, "");
    }
    public static String GetMessage(FujifilmSDKErrorCode code, Activity activity, String extraMessage) {
        String msg = activity.getString(com.fujifilm.libs.spa.R.string.fatal_error_msg);

        switch (code) {
            case FATAL_ERROR:
                msg = activity.getString(R.string.fatal_error_msg);
                break;
            case NO_IMAGES_UPLOADED:
                msg = activity.getString(R.string.no_images_uploaded_msg);
                break;
            case NO_INTERNET:
                msg = activity.getString(R.string.no_internet_msg);
                break;
            case INVALID_API_KEY:
                msg = activity.getString(R.string.invalid_api_key_msg);
                break;
            case USER_CANCELED:
                msg = activity.getString(R.string.user_canceled_msg);
                break;
            case NO_VALID_IMAGES:
                msg = activity.getString(R.string.no_valid_images_msg);
                break;
            case TIME_OUT:
                msg = activity.getString(R.string.time_out_msg);
                break;
            case ORDER_COMPLETE:
                msg = activity.getString(R.string.order_complete, extraMessage);
                break;
            case UPLOAD_FAILED:
                msg = activity.getString(R.string.upload_failed);
                break;
            case USERID_INVALID_FORMAT:
                msg = activity.getString(R.string.userid_invalid_format);
                break;
            case PROMOCODE_INVALID_FORMAT:
                msg = activity.getString(R.string.promocode_invalid_format);
                break;
        }
        return msg;
    }
}
