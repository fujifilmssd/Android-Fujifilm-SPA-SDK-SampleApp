# Fujifilm SPA Android SDK Tutorial
 
## Introduction
This project is a sample application to demonstrate how to integrate with the Fujifilm SPA Android SDK.  
 
This document provides a tutorial to use the Fujifilm SPA Android SDK in your Android application.
 
### What is it?
The Fujifilm Smart Publishing API SDK is a library that you can include in your existing Android application to enable photo product output through Fujifilm.  This provides you with a new revenue stream while providing a new valuable service to your application users.
 
The Fujifilm SPA SDK gives you access to over 50 popular photo gift products and allows you to control the availability and pricing of each product through our web portal. 
 
Please visit the Fujifilm Developer Network portal to sign-up and obtain an API key, set product pricing, and configure your application.  The portal is available at http://www.fujifilmapi.com/.
 
### What’s new?
There are many new items in this release:

+ Completely re-designed GUI
+ Product categorization to help organize the available product set
+ Over 30 new products including items within popular categories such as Home Décor and mobile device cases
+ A native Android SDK in addition to our iOS SDK so that you can provide your users with a common experience in either operating system (Android versions 4.0 and above)
+ Updated support for Android versions 6 and above
 
## Requirements
+ App minimum SDK 16 or above
+ Developers using the Fujifilm SPA Android SDK need to sign up for an account on Fujifilm Developer Network (http://fujifilmapi.com), create an application, obtain an API Key, and setup catalog products and pricing.
 
## Integration Instructions
To add Fujifilm SPA SDK to your project, you may install it via Gradle or add it manually.
 
### Android Manifest - Permissions
Update your app’s `AndroidManifest.xml` to add the following permissions:
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

### Android Studio - Using Gradle
 
To install the library in Android Studio using Gradle, you can declare it as dependency in your app's `build.gradle` file.
 
```
dependencies {
    compile('com.fujifilmssd:fujifilm.spa.sdk:1.7.34')
}
```
Include the following repositories in your root project’s `build.gradle` file under the  `allprojects` section:
```
allprojects {
    repositories {
        jcenter()
        maven { url  "http://dl.bintray.com/fujifilmssd/maven" }
        maven { url "https://jitpack.io" }
    }
}
```
Change your minimum SDK version to 16 or above in your gradle file:
```
 minSdkVersion 16
```
You can now force Android Studio to sync with your configuration by selecting `Tools -> Android -> Sync Project with Gradle Files`
 
Skip to [Implementation Instructions](#implementation-instructions) section below
 
### Android Studio - Manual Installation
 
##### Skip this section if you are using Gradle!
 
1. Download the [library](https://github.com/fujifilmssd/Android-Fujifilm-SPA-SDK-SampleApp/blob/master/fujifilm.spa.sdk/fujifilm.spa.sdk.aar).
2. In Android Studio, Click `File -> New -> New Module`
3. Select `Import .JAR or .AAR Package` and click `Next`. Then link to the path of the library downloaded in Step 1 and click `Finish`.
4. Set your app to be dependent on the FujifilmSPASDK library. Right click on your app in the project browser and click `Open Module Settings`.
5. In the Project Structure window make sure your app is selected in the modules area to the left, then click on the Dependencies tab. Click the plus sign in the Dependencies window, then click `Module dependency`. Select the FujifilmSPASDK library in the Choose Modules window and press `OK`.
6. Include Additional Dependencies.
    * Add the following dependencies to your app’s app `build.gradle` file. These should be included above the `:fujifilm.spa.sdk` project dependency:
    
            compile('com.afollestad.material-dialogs:core:0.8.6.2') {
                transitive = true
            }
            compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
            compile ('com.braintreepayments.api:braintree:2.+')
 
    * Include the following in your project’s `build.gradle` file allprojects repositories:
 
            allprojects {
                repositories {
                    jcenter()
                    maven { url  "http://dl.bintray.com/fujifilmssd/maven" }
                    maven { url "https://jitpack.io" }
                }
            }
 
Skip to [Implementation Instructions](#implementation-instructions) section below
 
#### Eclipse with ADT
 
##### Skip this section if you are using Android Studio!
 
1. Download .JAR, and move it to project's lib directory.
2. Right click on your project in the package explorer, then click properties.
3. Click apply, then click “Java Build Path” on the left. In the libraries tab, press “Add Jars” and direct to the FujifilmSPASDK.jar file.
4. Go to the Order and Export tab, and in the list presented, make sure FujifilmSPASDK is checked.
5. Update your app’s Android Manifest to add the following permissions and activity:
 
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 
    <application
        <activity
            android:name="com.example.me.FujifilmSPASDK"
            android:screenOrientation="portrait"
            android:label="FujifilmSPASDK" >
        </activity>
    </application>
</manifest>
```
 
Clean and build your project.
 
## Implementation Instructions
 
### Fujifilm SPA SDK Usage
 
See complete example [here](#full-example).
 
Import the following classes in your activity:
 
```java
import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.FujifilmSPA;
 
//Add the following classes, if not already imported
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.content.Intent;
import android.widget.Toast;
```
 
Create and store a FujifilmSPA Object using the following code:
 
```java
FujifilmSPA fujifilmSPA = FujifilmSPA.getInstance();
```
 
Call `checkout` and pass in all required parameters to start. This will create a new child application where the checkout process will commence.
```java
fujifilmSPA.checkout(Activity startingActivity, int requestCode, String apiKey, SdkEnvironment environment, String userID, boolean shouldRetainUserInfo, ArrayList<FFImage> images, String promoCode, LaunchPage launchPage, Map<String, Serializable> extraOptions);
```
 
#### Parameters
 
 | Name | Type | Description |
 | ------- | ------| -------------- |
|`startingActivity`|`Activity`|The parent activity that is starting the Fujifilm SPA SDK. This will be used to provide information about the order when the Fujifilm SPA SDK (child app) finishes.  
|`requestCode`|`int`|A user-defined request code to handle response messages. An integer that will be sent back in 'OnActivityResult' when the FujifilmSPASDKActivity finishes  
|`apiKey`|`String`|Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com. This apiKey is environment specific  
|`environment`|`FujifilmSPA.SdkEnvironment`|Sets the environment to use. The apiKey must match your app’s environment set on http://fujifilmapi.com.  
|`userId`|`String`|Optional parameter. This can be used to link a user with an order. May be null if no UserId is desired. MaxLength = 50 alphanumeric characters  
|`shouldRetainUserInfo`|`boolean`|Save user information (address, phone number, email) for when the app is used a 2nd time.  
 |`images`|`ArrayList<FFImage>`|ArrayList of FFImage objects. May be null if no images passed in is desired. FFImage can be a local image (id, path) or public url (https://). Supported image types are jpeg/png. A maximum of 100 images can be sent in a given Checkout process. If more than 100 images are sent, only the first 100 will be processed.  
|`promoCode`|`String`|Optional parameter to add a promo code to the order. May be null if no passed in promo code is desired. Contact us through http://fujifilmapi.com for usage and support.  
|`




ge`|`FujifilmSPA.LaunchPage`|The page that the SDK should launch when initialized. Valid values are (Home, Cart), defaults to Home  
|`extraOptions`|`Map<String, Serializable>`| A map with several accepted key/value pairs. All key/value pairs are optional; `extraOptions` may be empty or `null` if no options are desired. See section "[Extra Initialization Options](#extra-initialization-options)" for more information.

#### Extra Initialization Options (Optional) 
The following are currently accepted key/value pairs for the `extraOptions` initialization parameter:
 
 | Name | Type | Description |
 | ------- | ------| -------------- |
|`FujifilmSPA.EXTRA_ADD_MORE_PHOTOS_DISABLED`|`boolean`|By default this is set to `false`. To disable the "Add More Photos" feature set this to value `true`. If `false` (or omitted), the user will be able to add more photos from his or her local Photos gallery on the Compose screen and the Prints screen.  
|`FujifilmSPA.EXTRA_LAUNCH_LINK`|`String`|Specifies which page the user is first presented with when launching the SDK. You can send the user to the cart, a category, or to a product details screen. To send the user to the cart, set the value to `Cart`. To send the user to a category use the follow pattern: `mailorder/CATEGORY_NAME`. Make sure to change the `CATEGORY_NAME` to the name of the category, for example, `mailorder/WallArt`. To send the user to a product details screen use the following pattern: `mailorder/CATEGORY_NAME/PRODUCT_NAME`. Make sure to change `CATEGORY_NAME` to the name of the category and the `PRODUCT_NAME` to the name of the product, for example, `mailorder/canvas/11x14gallerywrappedcanvas`.  
|`FujifilmSPA.EXTRA_HAS_PRERENDERED_ITEMS`|`PreRenderedOrder`|See section "[Providing Pre-rendered Products](#providing-pre-rendered-products)" for more information  

#### Providing Pre-rendered Products (Optional)
In order to include pre-rendered products with your order, you may pass an instance of the `PreRenderedOrder` class into the `extraOptions` parameter. This class contains a list of products (instance of `Line` class) to be added to the order. Each `Line` contains a product code field which corresponds to the product code found on http://fujifilmapi.com, as well as a list of `Page` objects. Each `Page` object contains a list of `Asset` objects, each of which contains an asset type (only "image" is currently accepted) as well as a url to the Hi-Res image to be printed.
The following is an example function showing how to create an order with a pre-rendered Stationery Card:

```java
import com.fujifilm.libs.spa.Asset;
import com.fujifilm.libs.spa.Line;
import com.fujifilm.libs.spa.Page;
import com.fujifilm.libs.spa.PreRenderedOrder;

private PreRenderedOrder createPrerenderedOrderWithStationeryCard() {

    //create an image Asset for the front of the card
    Asset assetFront = new Asset("image", "http://test.com/prerenderedCardFront.jpg");
    Asset[] frontAssets = {assetFront};
    
    //create an image Asset for the back of the card
    Asset assetBack = new Asset("image", "http://test.com/prerenderedCardBack.jpg");
    Asset[] backAssets = {assetBack};
    
    //create a Page for the front of the card. Set the appropriate assets
    Page pageFront = new Page();
    pageFront.setAssets(frontAssets);
    
    //create a Page for the back of the card. Set the appropriate assets
    Page pageBack = new Page();
    pageBack.setAssets(backAssets);
    
    Page[] pages = {pageFront, pageBack};
    
    //create a Line which represents the product
    Line line = new Line();
    
    //set the product code to that of the desired product
    line.setProductCode("PRGift;4121");
    
    //add the front and back pages to the Line
    line.setPages(pages);
    
    Line[] lines = {line};
    
    //Create a new order containing the Line
    PreRenderedOrder preRenderedOrder = new PreRenderedOrder();
    preRenderedOrder.setLines(lines);
    
    return preRenderedOrder;
}
```

#### Finish Fujifilm SPA SDK
 
When the Fujifilm SPA SDK is finished, it will return to the parent app. You can check the result in `onActivityResult()`. The requestCode for the result will be the same as the code that was passed in when the checkout method was called.
 
```java
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
    super.onActivityResult(requestCode, resultCode, data);
 
    if(requestCode == FujifilmSPASDK_INTENT){
        //FujifilmSPASDK_INTENT is the requestCode passed in when checkout was called.
        //If the user successfully completes an order, the resultCode will be RESULT_OK
        if(resultCode == RESULT_OK){
            //user successfully completes an order.
            //Toast.makeText(this.getApplicationContext(),data.getStringExtra(FujifilmS//PA.EXTRA_STATUS), Toast.LENGTH_LONG).show();
        }
        //If the user cancels the order or the SDK fails, the resultCode will be RESULT_CANCELED
        if(resultCode == RESULT_CANCELED){
            //Toast.makeText(this.getApplicationContext(),data.getStringExtra(FujifilmS//PA.EXTRA_STATUS),Toast.LENGTH_LONG).show();
        }
        if (data.hasExtra(FujifilmSPA.EXTRA_PROMO_ERROR)) {
            //Toast.makeText(this.getApplicationContext(),String.valueOf(data.getSerializableExtra(FujifilmSPA.EXTRA_PROMO_ERROR)), Toast.LENGTH_LONG).show();
        }
 
    }
}
```
 
In the case of a successful purchase, the result code of the response will be `RESULT_OK`. For any other case (either due to a user cancel or an error), the result code will be `RESULT_CANCELED`. If the result is `RESULT_CANCELED`, the `EXTRA_STATUS` field will define the reason for a cancellation. This can be used to check whether the user cancelled the order, or what error caused the SDK to close.
 
The status code will be one of the following values:
 
|`EXTRA_STATUS` | Meaning |
| ----- | ------ |
|0  | Fatal Error         |
|1  | No Images Uploaded  |
|2  | No Internet         |
|3  | Invalid API Key     |
|4  | User Cancelled      |
|5  | No Valid Images     |
|6  | Time Out            |
|7  | Order Complete      |
|8  | Upload Failed       |
|9  | User ID Invalid Format|
|10  | Promo Code Invalid Format|
 
In addition, a promotion code that is in a valid format but is otherwise invalid will still allow the SDK to run. In this case, the intent will include a field `EXTRA_PROMO_ERROR`, which will explain why the promotion is invalid.
Possible values are as follows:
 
| `EXTRA_PROMO_ERROR` | Meaning |
| ---------- | ------- |
| 0  |Promotion Expired            |
| 1  |Promotion Not Yet Activated  |
| 2  |Invalid Discount             |
| 3  |Promotion Disabled           |
| 4  |Promotion Does Not Exist     |
| 5  |Fatal (default)              |
 
#### Receiving analytic events (Optional)
If you're interested in seeing what behavior your users are taking in our SDK, we provide a way for any you to listen to events from us when users take certain actions. To receive these events, implement an instance of `BroadcastManager` and  register it to receive events with the `FujifilmSPA.Analytics.EVENT_ACTION_FILTER` action. Make sure to register the `LocalBroadcastManager` before starting the SDK and to un-register the handler in `onActivityResult` from the SDK.

Each event attribute will be provided in the received `Intent` with the keys and value types as defined below. Below is an example of how data is received in the broadcast and how to register it.

##### Example Code

Setting up the `BroadcastReceiver` and an example of how to handle the Item Purchased event:

```Java
private BroadcastReceiver mSDKAnalyticsReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(FujifilmSPA.Analytics.EVENT_ACTION_FILTER)) {
            handleSDKAnalytics(intent);
        }
    }
};

private void handleSDKAnalytics(Intent intent) {
    String event = intent.getStringExtra(FujifilmSPA.Analytics.EVENT);
    if (event == null) {
        return;
    }
    switch (event) {
        case FujifilmSPA.Analytics.ACTION_ITEM_PURCHASED:
            String purchasedProductName = intent.getStringExtra(FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE);
            String purchasedProductCode = intent.getStringExtra(FujifilmSPA.Analytics.EXTRA_PRODUCT_ID);
            int purchasedQuantity = intent.getIntExtra(FujifilmSPA.Analytics.EXTRA_QTY_OF_ITEM_PURCHASED, 0);
            double purchasedUnitPrice = intent.getDoubleExtra(FujifilmSPA.Analytics.EXTRA_PRODUCT_UNIT_PRICE, 0);

            Log.d(TAG, "Received Item Purchased event");
            break;
        default:
            break;
    }
}
```

Just before starting the SDK, register the `BroadcastReceiver` using `LocalBroadcastManager`:

```java

// Call this before calling FujifilmSPA.checkout
private void registerFujifilmSDKAnalyticsReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(FujifilmSPA.Analytics.EVENT_ACTION_FILTER);
    
    LocalBroadcastManager.getInstance(this).registerReceiver(mSDKAnalyticsReceiver, filter);
}
```

When the SDK finishes, unregister the `BroadcastReceiver`:

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SDK_REQUEST_CODE) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSDKAnalyticsReceiver);
        // anything else you want to handle here
    }
}

```

##### Events

|Event Name|Event Value|Description|
|-|-|-|
|Exit|`FujifilmSPA.Analytics.ACTION_EXIT`|Sent when the user exits the SDK|
|Print Edited|`FujifilmSPA.Analytics.ACTION_PRINT_EDITED`|Sent when the user edits a print product|
|Product Edited|`FujifilmSPA.Analytics.ACTION_PRODUCT_EDITED`|Sent when the user edits any non-print product|
|Continue Shopping|`FujifilmSPA.Analytics.ACTION_CONTINUE_SHOPPING`|Sent when the user tapps "Continue Shopping" from the cart page and the thank you page|
|Item Added to Cart|`FujifilmSPA.Analytics.ACTION_ITEM_ADDED_TO_CART`|Sent when a product is added to cart from either the compose page or the prints page|
|Item Composed|`FujifilmSPA.Analytics.ACTION_ITEM_COMPOSED`|Sent when the user goes to the compose page to edit a product|
|Item Details Viewed|`FujifilmSPA.Analytics.ACTION_ITEM_DETAILS_VIEWED`|Sent when the user taps navigates to the product details page|
|Item Purchased|`FujifilmSPA.Analytics.ACTION_ITEM_PURCHASED`|Sent for every item when the user successfully checks out|
|Order Complete|`FujifilmSPA.Analytics.ACTION_ORDER_COMPLETED`|Sent when the user successfully checks out|
|Checkout Started|`FujifilmSPA.Analytics.ACTION_CHECKOUT_STARTED`|Sent when the user taps "Continue to Checkout" from the cart page|
|Store Searched|`FujifilmSPA.Analytics.ACTION_STORE_SEARCHED`|Sent when the user searches for a store on the store list page|
|Item Removed from Cart|`FujifilmSPA.Analytics.ACTION_PRODUCT_REMOVED`|Sent when a product is removed from the cart, either due to an error or when the user deletes the product themselves|
|Store Favorited|`FujifilmSPA.Analytics.ACTION_STORE_FAVORITED`|Sent when the user favorites a store on the store search page or on the first step of checkout|

##### Exit Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_NUMBER_OF_ITEMS_PURCHASED`|`int`|Number of items purchased|
|`FujifilmSPA.Analytics.EXTRA_EXIT_POINT`|`String`|Page the user left on|
|`FujifilmSPA.Analytics.EXTRA_PROMO_CODE`|`String`|Comma separated list of promo codes applied |
|`FujifilmSPA.Analytics.EXTRA_EXIT_METHOD`|`String`|Done Button/Back Button/Cancel Button|
|`FujifilmSPA.Analytics.EXTRA_DELIVERY_TYPE`|`String`|Retail Pickup/Mail Order |
|`FujifilmSPA.Analytics.EXTRA_PICKUP_LOCATION`|`String`|If this is a Mail Order order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|
|`FujifilmSPA.Analytics.EXTRA_ADDRESS_VALIDATION_ERRORS`|`int`|number of address validation errors that have occurred during the session|


##### Print Edited Event Attributes
No event attributes

##### Product Edited Event Attributes
No event attributes

##### Continue Shopping Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_CONTINUE_SHOPPING_SCREEN`|`String`|The page in which the user tapped Continue Shopping|

##### Item Added to Cart Shopping Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_ITEMS_ADDED_TO_CART_SUCCESS`|string|Whether add to cart succeeded or failed|
|`FujifilmSPA.Analytics.EXTRA_ITEMS_ADDED_TO_CART_DURATION`|integer|The amount of time it took to add to cart |
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE`|string|Name of the product added to cart|
|`FujifilmSPA.Analytics.EXTRA_DELIVERY_TYPE`|string|Retail Pickup/Mail Order|
|`FujifilmSPA.Analytics.EXTRA_PICKUP_LOCATION`|string|If this is a Mail Order order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_ID`|string|Product code of the product added to cart|

##### Item Composed Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE`|`String`|Name of the product composed|
|`FujifilmSPA.Analytics.EXTRA_SOURCE`|`String`|Where the user came from. One of: Cart Page, Order Prints Page, Product List|
|`FujifilmSPA.Analytics.EXTRA_DELIVERY_TYPE`|`String`|Retail Pickup/Mail Order|
|`FujifilmSPA.Analytics.EXTRA_PICKUP_LOCATION`|`String`|If this is a Mail Order order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|

##### Item Details Viewed Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE`|`String`|Name of the product the user viewed|
|`FujifilmSPA.Analytics.EXTRA_SOURCE`|`String`|Where the user viewed the product from. Currently only "Product List"|
|`FujifilmSPA.Analytics.EXTRA_DELIVERY_TYPE`|`String`|Retail Pickup/Mail Order|
|`FujifilmSPA.Analytics.EXTRA_PICKUP_LOCATION`|`String`|If this is a Mail Order order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|


##### Item Purchased Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE`|`String`|Name of the product the user purchased|
|`FujifilmSPA.Analytics.EXTRA_QTY_OF_ITEM_PURCHASED`|`int`|Quantity of the line|
|`FujifilmSPA.Analytics.EXTRA_DELIVERY_TYPE`|`String`|Retail Pickup/Mail Order|
|`FujifilmSPA.Analytics.EXTRA_PICKUP_LOCATION`|`String`|If this is a Mail Order order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_ID`|`String`|Product code of the item purchased|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_UNIT_PRICE`|`double`|Unit price of the product|

##### Order Complete Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_NUMBER_OF_ITEMS_PURCHASED`|`int`|Number of lines in the order|
|`FujifilmSPA.Analytics.EXTRA_NUMBER_OF_DISTINCT_ITEMS`|`int`|Number of distinct products in the order|
|`FujifilmSPA.Analytics.EXTRA_ORDER_PAYMENT_TYPE`|`String`| Name of the payment method used by the user|
|`FujifilmSPA.Analytics.EXTRA_ORDER_CURRENCY_TYPE`|`String`|ISO 4217 currency code for the type of currency used. Currently only USD.|
|`FujifilmSPA.Analytics.EXTRA_ORDER_SUBTOTAL`|`double`|Sub total of the order|
|`FujifilmSPA.Analytics.EXTRA_ORDER_TAX`|`double`|Tax the user was charged for|
|`FujifilmSPA.Analytics.EXTRA_ORDER_SHIPPING`|`double`|Total cost of shipping for the user|
|`FujifilmSPA.Analytics.EXTRA_ORDER_DISCOUNT`|`double`|Amount taken off the order due to discounts|
|`FujifilmSPA.Analytics.EXTRA_ORDER_TOTAL`|`double`|Total amount of the order|
|`FujifilmSPA.Analytics.EXTRA_ORDER_RETAILER`|`String`|If this is Mail Order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|
|`FujifilmSPA.Analytics.EXTRA_ORDER_SERVICE_TYPE`|`String`|Mail Order/Retail Pickup|
|`FujifilmSPA.Analytics.EXTRA_ORDER_DELIVERY_METHOD`|`String`|The delivery level the user selected: Standard/Expidited/Rush. If this is a store pickup order, this is "Pay in Store".|
|`FujifilmSPA.Analytics.EXTRA_STORE_NUMBER`|`String`|The store number of the store the user selected, if the order is store pickup.|


##### Checkout Started Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_STORE_NUMBER`|`String`|The store number of the store the user selected, if the order is store pickup.|
|`FujifilmSPA.Analytics.EXTRA_NUMBER_OF_ITEMS_PURCHASED`|`int`|Number of lines in the order|
|`FujifilmSPA.Analytics.EXTRA_NUMBER_OF_DISTINCT_ITEMS`|`int`|Number of distinct products in the order|
|`FujifilmSPA.Analytics.EXTRA_ORDER_CURRENCY_TYPE`|`String`|ISO 4217 currency code for the type of currency used. Currently only USD.|
|`FujifilmSPA.Analytics.EXTRA_ORDER_SUBTOTAL`|`double`|Sub total of the order|
|`FujifilmSPA.Analytics.EXTRA_IS_PRESERVED_CART`|`boolean`|If this is a new order or a preserved cart|
|`FujifilmSPA.Analytics.EXTRA_ORDER_RETAILER`|`String`|If is is Mail Order, N/A. Otherwise, it is the name of the retailer the user selected (Walmart, Sam's Club, etc.)|
|`FujifilmSPA.Analytics.EXTRA_ORDER_SERVICE_TYPE`|`String`|Mail Order/Retail Pickup|


##### Store Searched Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_STORE_SEARCH_LATITUDE`|`double`|The latitude of the user when they tap "Find near me" on the store search page. NOTE: This value may not be specified if the user did not use the "Find near me" feature.|
|`FujifilmSPA.Analytics.EXTA_STOER_SEARCH_LONGITUDE`|`double`|The longitude of the user when they tap "Find near me" on the store search page. NOTE: This value may not be specified if the user did not use the "Find near me" feature.|
|`FujifilmSPA.Analytics.EXTRA_STORE_SEARCH_ZIP_CODE`|`String`|The zip code the user entered on the store search page|
|`FujifilmSPA.Analytics.EXTRA_STORE_SEARCH_RADIUS`|`int`|The radius the user had selected|
|`FujifilmSPA.Analytics.EXTRA_STORE_SEARCH_RESULT_COUT`|`int`|The number of stores found based on the users search criteria|


##### Item Removed from Cart Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_CODE`|`String`|The name of the product removed from the users cart|
|`FujifilmSPA.Analytics.EXTRA_PRODUCT_ID`|`String`|The product code of the product removed from the users cart|

##### Store Favorited Event Attributes
|Event Attribute|Data Type|Description|
|-|-|-|
|`FujifilmSPA.Analytics.EXTRA_FAVORITED_STORE_NUMBER`|`String`|The store number of the store that the user favorited|

 
#### ProGuard Rules for app
```
-keepclassmembers enum * { *; }
-keepclassmembers class com.fujifilmssd.FujifilmSPASDKActivity.** {*;}
-keep public class com.fujifilmssd.FujifilmSPASDKActivity.**
-keepnames class io.card.payment.** {*;}
-keep public class io.card.payment.** {*;}
-dontwarn io.card.payment.CardIOActivity
-dontwarn io.card.payment.CreditCard
```

#### Full Example
```java
import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.FujifilmSPA;
 
//Add the following classes, if not already imported
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.content.Intent;
import android.widget.Toast;
 
public class MainActivity extends AppCompatActivity {
 
    private static final int FujifilmSPASDK_INTENT = 333; //user defined request code for SPA
    private ArrayList<FFImage> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FujifilmSPA fujifilmSPA = FujifilmSPA.getInstance();
 
        //Create Array of images
        images = new ArrayList<>();
 
        //Add public FFImage with public URL
        try {
            URL myPublicImageURL = new URL("https://webservices.fujifilmesys.com/venus/imagebank/fujifilmCamera.jpg");
            images.add(new FFImage(myPublicImageURL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
 
        //Add public FFImage with local image
        //images.add(new FFImage(image.imageId, image.path)); //local image
 
        //Call checkout which takes the user into Fujifilm's order flow
        fujifilmSPA.checkout(MainActivity.this, FujifilmSPASDK_INTENT, "5cb79d2191874aca879e2c9ed7d5747c", FujifilmSPA.SdkEnvironment.Preview, null, true, images, "", FujifilmSPA.LaunchPage.Home, null);
    }
 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        if (requestCode == FujifilmSPASDK_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_CODE), Toast.LENGTH_LONG).show();
            }
            //If a child app fails for any reason, the parent app will receive RESULT_CANCELED
            if (resultCode == RESULT_CANCELED && data != null) {
                int statusCode = (int)data.getSerializableExtra(FujifilmSPA.EXTRA_STATUS_CODE);
 
                Toast.makeText(this.getApplicationContext(), data.getStringExtra(FujifilmSPA.EXTRA_STATUS_MESSAGE), Toast.LENGTH_LONG).show();
            }
            if (data.hasExtra(FujifilmSPA.EXTRA_PROMO_ERROR)) {
                Toast.makeText(this.getApplicationContext(),String.valueOf(data.getSerializableExtra(FujifilmSPA.EXTRA_PROMO_ERROR)), Toast.LENGTH_LONG).show();
            }
        }
    }
}
```
 
## Additional notes and debugging help
 
The following are some notes to help with integrating with **Fujifilm SPA Android SDK**.
 
### Use Requirements:
+ The phone must have internet access to begin the SDK, and must retain access throughout the checkout process. If the connection is lost during the checkout process, an alert will be shown notifying the user that an internet connection is required.
+ A maximum of 100 images can be sent to the SDK in a given checkout process. If more than 100 images are sent, only the first 100 will be processed.
+ Only jpeg/png files are supported.
+ The maximum size of a single file is 20MB for local images, no file size restriction for public urls
 
### Errors that prevent the SDK from Starting
+ 0 valid images
+ No internet access
+ Invalid APIKey. Ensure the APIKey you are using matches the environment string you are passing in (Stage, Preview, Production)
+ Permissions not granted
 
As these errors prevent the Fujifilm SPA SDK activity from ever being created, `onActivityResult` will not be called. Instead, a Log message will appear in the android console explaining why the SDK was unable to start.
 
### Errors that will cancel the SDK
+ Loss of network or internet access before all images have finished uploading
+ All images fail to upload / no remaining images to checkout with
 
These errors will return control back to the parent app and supply a status code in `onActivityResult`. In addition, depending on the nature of the error, there may be a Log message in the Android console with more information on the cause for the error.
 
### Errors that will prevent a specific picture from uploading or being processed
+ An image is over 20MB
+ An image is of an unsupported file format
Image file is corrupt or is uploaded unsuccessfully, making it corrupt
 
These Errors will not cancel the SDK, and as such, they will not directly return an error result in `onActivityResult`. Instead, they will Log an error with more information about the specific error that caused an image to be removed. If enough images are removed such that 0 images are remaining then the SDK will be terminated.
 
### Feedback
We’re very interested in your feedback!  If you run into any trouble, have a suggestion, or want to let us know what worked well send us an email to contact@fujifilmapi.com or use the web form at https://www.fujifilmapi.com/contact-us.
 
### License
IMPORTANT - PLEASE READ THE FOLLOWING TERMS AND CONDITIONS CAREFULLY BEFORE USING THE FOLLOWING COMPUTER CODE (THE “CODE”). USE OF THE CODE IS AT YOUR OWN RISK. THE CODE IS PROVIDED “AS IS”, WITH ANY AND ALL FAULTS, DEFECTS AND ERRORS, AND WITHOUT ANY WARRANTY OF ANY KIND. FUJIFILM DISCLAIMS ALL WARRANTIES, WHETHER EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, ALL IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT, WITH RESPECT TO THE CODE OR DEFECTS IN OPERATION OR ANY PARTICULAR APPLICATION OR USE OF THE CODE. FUJIFILM DOES NOT WARRANT THAT THE CODE WILL MEET YOUR REQUIREMENTS OR EXPECTATIONS, THAT THE CODE WILL WORK ON ANY HARDWARE, OPERATING SYSTEM OR WITH ANY SOFTWARE, THAT THE OPERATION OF THE CODE WILL BE UNINTERRUPTED, FREE OF HARMFUL COMPONENTS OR ERROR-FREE, OR THAT ANY KNOWN OR DISCOVERED ERRORS WILL BE CORRECTED.
FUJIFILM SHALL NOT BE LIABLE TO YOU OR ANY THIRD PARTY FOR ANY LOSS OF PROFIT, LOSS OF DATA, COMPUTER FAILURE OR MALFUNCTION, INTERRUPTION OF BUSINESS, OR OTHER DAMAGE ARISING OUT OF OR RELATING TO THE CODE, INCLUDING, WITHOUT LIMITATION, EXEMPLARY, PUNITIVE, SPECIAL, STATUTORY, DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, TORT OR COVER DAMAGES, WHETHER IN CONTRACT, TORT OR OTHERWISE, INCLUDING, WITHOUT LIMITATION, DAMAGES RESULTING FROM THE USE OR INABILITY TO USE THE CODE, EVEN IF FUJIFILM HAS BEEN ADVISED OR AWARE OF THE POSSIBILITY OF SUCH DAMAGES.
