package com.fujifilm.sample.spa;

/**
 * Copyright (c) 2016 FUJIFILM North America Corp. All rights reserved.
 *
 * This is a sample enum containing possible Promo Code error messages returned by SPA Mobile Web. This may need to
 * be updated as more error codes are added.
 */
public enum FujifilmSDKPromoError {
    EXPIRED("Promotion Expired", 0),
    NOT_ACTIVATED("Promotion Not Yet Activated", 1),
    INVALID_DISCOUNT("Invalid Discount", 2),
    DISABLED("Promotion Disabled", 3),
    DOES_NOT_EXIST("Promotion Does Not Exist", 4),
    FATAL("Fatal", 5);

    private final String description;
    private final int code;

    FujifilmSDKPromoError(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public static FujifilmSDKPromoError getErrorFromCode(int c) {
        FujifilmSDKPromoError[] values = FujifilmSDKPromoError.values();
        return (c < 0 || c >= values.length ? FATAL : values[c]);
    }
}
