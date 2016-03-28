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
+ A native Android SDK in addition to our Android SDK so that you can provide your users with a common experience in either operating system (Android versions 4.0 and above)
+ Updated support for Android versions 7 and above

## Requirements
+ App minimum SDK 15 and above
+ Developers using the Fujifilm SPA Android SDK need to sign up for an account on Fujifilm Developer Network (http://fujifilmapi.com), create an application, obtain an Api Key, and setup catalog products and pricing.

## Integration Instructions

### Step 1: Get API Key
Sign up for an account on Fujifilm Developer Network (http://fujifilmapi.com), create an application, obtain an API Key, and setup catalog products and pricing.

### Step 2: Include Fujifilm SPA SDK
To add Fujifilm SPA SDK to your project, you may add it manually, or you may install via Gradle. 

#### Android Studio

##### Android Studio / Gradle
1. Include jcenter() and maven { url "https://jitpack.io" } in your project level build.gradle file:

   ```Java 
   allprojects {  
       repositories {  
           jcenter()  
           maven { url "https://jitpack.io" }  
       }  
   }
   ```
2. To install the library in Android Studio using Gradle, you can declare it as dependency in your build.gradle file:  

   ```Java 
   dependencies {
       compile 'com.fujifilmssd:fujifilm.spa.sdk:1.0.2'
   }
   ```

3. You can now force Android Studio to sync with your configuration by selecting *Tools -> Android -> Sync Project with Gradle Files*

This should download the dependency. 

##### Android Studio Manual Installation
1. Download the [library](https://github.com/fujifilmssd/Android-Fujifilm-SPA-SDK-SampleApp/blob/master/fujifilm.spa.sdk/fujifilm.spa.sdk.aar).
2. In Android Studio, Click File -> New -> New Module
3. Select “Import .JAR or .AAR Package” and click “Next”. Then link to the path of the library downloaded in Step 1 and click “Finish”. 
4. Set your app to be dependent on the FujifilmSPASDK library. Right click on your app in the project browser and click Open Module Settings.
5. In the Project Structure window make sure your app is selected in the modules area to the left, then click on the Dependencies tab. Click the plus sign in the Dependencies window, then click “Module dependency”. Select the FujifilmSPASDK library in the Choose Modules window and press “OK”.
6. Include the following dependencies to your project’s app build.gradle file: 
     * These should be included above the ‘:fujifilm.spa.sdk’ project dependency.

        ```  
         compile('com.github.afollestad.material-dialogs:core:0.8.5.1@aar') {
             transitive = true
         }  
         compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'  
         ```
7. Include the following in your project’s build.gradle file allprojects repositories:

   ```Java 
   allprojects {  
       repositories {  
           jcenter()  
           maven { url "https://jitpack.io" }  
       }  
   }
   ```

##### Eclipse with ADT
1. Download .JAR, and move it to project's lib directory.  
2. Right click on your project in the package explorer, then click properties.  
3. Click apply, then click “Java Build Path” on the left. In the libraries tab, press “Add Jars” and direct to the FujifilmSPASDK.jar file.  
4. Go to the Order and Export tab, and in the list presented, make sure FujifilmSPASDK is checked.  
5. Update your app’s Android Manifest to add the following permissions and activity:

```Java
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

### Step 3: Integrate with SDK

1. Import the following classes in your activity:

   ```Java
   import com.fujifilm.libs.spa.FFImage;
   import com.fujifilm.libs.spa.FujifilmSPA;
   
   //Import the following classes if not already imported
   import java.net.URL;
   import java.util.ArrayList;
   import java.net.MalformedURLException;
   import android.content.Intent;
   import android.widget.Toast;
   ```

2. Set your Fujifilm SPA SDK static constants in your MainActivity
   ```Java 
   //user defined request code for Fujifilm SPA SDK
   private static final int FujifilmSPASDK_INTENT = 333;

   //Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com
   private static final String FujifilmSPASDK_APIKey = "5cb79d2191874aca879e2c9ed7d5747c";

   //A bool indicating which environment your app runs in.  Must match your app’s (apiKey) environment set on http://fujifilmapi.com.
   private static final Boolean FujifilmSPASDK_IsLive = false;
   ```
3. Create a FujifilmSPA Object using the following code:  

   ```Java
   FujifilmSPA fujifilmSPA = FujifilmSPA.getInstance();
   ```

4. Create an ArrayList of images to pass to the SDK. The ArrayList must be an array of FFImage objects. 
    ```Java
   //FFImage can be a local image (id, path) or public url (https://). Supported image types are jpeg. A maximum of 50 images can be sent in a given Checkout process. If more than 50 images are sent, only the first 50 will be processed.
   ArrayList<FFImage> images = new ArrayList<>();
   
   //Add a FFImage with public URL
   try {
      URL myPublicImageURL = new URL("https://pixabay.com/static/uploads/photo/2015/09/05/21/08/fujifilm-925350_960_720.jpg");
      images.add(new FFImage(myPublicImageURL));
   }catch (MalformedURLException e) {
      e.printStackTrace();
   }
        
   //Add public FFImage with local image
   //images.add(new FFImage(image.imageId, image.path)); //local image
   ```
5. Set an optional userId.
   ```Java
   //Optional parameter. This can be used to link a user with an order. MaxLength = 50 alphanumeric characters
   String userId = null;
   ```

6. Call checkout and pass in all required parameters to start (userId is optional). This will create a new child application where the checkout process will commence.
   ```Java
   fujifilmSPA.checkout(this, FujifilmSPASDK_INTENT, FujifilmSPASDK_APIKey, FujifilmSPASDK_IsLive, userId, images);
   ```

    ##### fujifilmSPA.checkout Parameters
    
    *  **startingActivity**: (Activity) - The parent activity that is starting the Fujifilm SPA SDK. This will be used to provide information about the order when the Fujifilm SPA SDK (child app) finishes  
    *  **requestCode**: (int) - A user-defined request code to handle response messages  
    *  **apiKey**:  (String) - Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com  
    *  **isLive**: (boolean) - Boolean value that indicates which environment your app runs in, must match your app’s environment set on http://fujifilmapi.com.  
    *  **userId**: (String) - Optional parameter. This can be used to link a user with an order. MaxLength = 50 alphanumeric characters  
    *  **images**: (ArrayList<FFImage>) - ArrayList of FFImage objects. FFImage can be a local image (id, path) or public url (https://). Supported image types are jpeg. A maximum of 50 images can be sent in a given Checkout process. If more than 50 images are sent, only the first 50 will be processed.

    The FFImage class has several constructors:
    *  Local image: images.add(new FFImage("https://someURLtoPublicImage.jpg"))
    *  Public url: images.add(new FFImage(myLocalImage.imageId, myLocalImage.path))
  

4. When the Fujifilm SPA SDK is finished, it will return to the parent app. You can check the result in onActivityResult(). The requestCode for the result will be the same as the code that was passed in when the checkout method was called.  

    ```Java
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
        }
    }
    ```

    In the case of a successful purchase, the result code of the response will be RESULT_OK. for any other case (either through a user cancel or an error), the result code will be RESULT_CANCELED. If the result is RESULT_CANCELED, the EXTRA_STATUS field will define the reason for a cancellation, which can be used to check whether the user cancelled the order, or what error caused the SDK to close (such as no selected images being usable).  
    
    The status code will be one of the following values:
    
    *  Fatal Error         = 0  
    *  No Images Uploaded  = 1  
    *  No Internet         = 2  
    *  Invalid API Key     = 3  
    *  User Cancelled      = 4  
    *  No Valid Images     = 5  
    *  Time Out            = 6  
    *  Order Complete      = 7  
    *  Upload Failed       = 8  

#### Full Example

```Java
import com.fujifilm.libs.spa.FFImage;
import com.fujifilm.libs.spa.FujifilmSPA;

//Import the following classes if not already imported
import java.net.URL;
import java.util.ArrayList;
import java.net.MalformedURLException;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //user defined request code for Fujifilm SPA SDK
    private static final int FujifilmSPASDK_INTENT = 333;

    //Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com
    private static final String FujifilmSPASDK_APIKey = "5cb79d2191874aca879e2c9ed7d5747c";

    //A bool indicating which environment your app runs in.  Must match your app’s environment set on http://fujifilmapi.com.
    private static final Boolean FujifilmSPASDK_IsLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        ----------------------------------------------------------------------------------------------
        Create a FujifilmSPA instance and call checkout with apiKey, isLive, userId (optional), images
        ----------------------------------------------------------------------------------------------

        - Go to http://www.fujifilmapi.com to register for an apiKey.
        - Ensure you have the right apiKey for the right environment.

        @param apiKey: Fujifilm SPA apiKey you receive when you create your app at http://fujifilmapi.com
        @param isLive: A bool indicating which environment your app runs in.  Must match your app’s environment set on http://fujifilmapi.com.
        @param userId: Optional parameter. This can be used to link a user with an order. MaxLength = 50 alphanumeric characters
        @param images: ArrayList of FFImage objects. FFImage can be a local image (id, path) or public url (https://). Supported image types are jpeg. A maximum of 50 images can be sent in a given Checkout process. If more than 50 images are sent, only the first 50 will be processed.

        The FFImage class has several constructors:
            Local image: images.add(new FFImage("https://someURLtoPublicImage.jpg"))
            Public url: images.add(new FFImage(myLocalImage.imageId, myLocalImage.path))
        *---------------------------------------------------------------------------------------

        */
        //Get Fujifilm SPA SDK singleton class instance
        FujifilmSPA fujifilmSPA = FujifilmSPA.getInstance();

        //Create ArrayList of FFImage objects. FFImage can be a local image (id, path) or public url (https://). Supported image types are jpeg. A maximum of 50 images can be sent in a given Checkout process. If more than 50 images are sent, only the first 50 will be processed.
        ArrayList<FFImage> images = new ArrayList<>();

        //Add a FFImage with public URL
        try {
            URL myPublicImageURL = new URL("https://pixabay.com/static/uploads/photo/2015/09/05/21/08/fujifilm-925350_960_720.jpg");
            images.add(new FFImage(myPublicImageURL));
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Add public FFImage with local image
        //images.add(new FFImage(image.imageId, image.path)); //local image

        //Optional parameter. This can be used to link a user with an order. MaxLength = 50 alphanumeric characters
        String userId = null;

        //Call checkout which takes the user into Fujifilm's order flow
        fujifilmSPA.checkout(this, FujifilmSPASDK_INTENT, FujifilmSPASDK_APIKey, FujifilmSPASDK_IsLive, userId, images);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
        }
    }
}
```

## Additional notes and debugging help

The following are some notes to help with integrating with **Fujifilm SPA Android SDK**. 

### Use Requirements:
+ The phone must have internet access to begin the SDK, and must retain access throughout the checkout process. If the connection is lost during the checkout process, an alert will be shown notifying the user that an internet connection is required
+ A maximum of 50 images can be sent to the SDK in a given checkout process. If more than 50 images are sent, only the first 50 will be processed
+ Only .jpg files are supported
+ The maximum size of a single file is 20MB

### Errors that prevent the SDK from Starting
+ 0 Images to be uploaded
+ No internet access
+ Invalid APIKey. Ensure the APIKey you are using matches the environment string you are passing in (“test’, “live”)
+ Permissions not granted  

As these errors prevent the Fujifilm SPA SDK activity from ever being created, onActivityResult will not be called. Instead, a Log message will appear in the android console explaining why the SDK was unable to start.

### Errors that will cancel the SDK
+ Loss of network or internet access before all images have finished uploading
+ All images fail to upload / no remaining images to checkout with

These errors will return control back to the parent app and supply a status code in onActivityResult. In addition, depending on the nature of the error there may be a Log message in the Android console with more information on the cause for the error.

### Errors that will prevent a specific picture from uploading or being processed
+ An image is over 20MB
+ An image is of an unsupported file format
Image file is corrupt or is uploaded unsuccessfully, making it corrupt

These Errors will not cancel the SDK, and as such, they will not directly return an error result in onActivityResult. Instead, they will Log an error with more information about the specific error that caused an image to be removed. If enough images are removed such that 0 images are remaining then the SDK will be terminated.

### Feedback
We’re very interested in your feedback!  If you run into any trouble, have a suggestion, or want to let us know what worked well send us an email to contact@fujifilmapi.com or use the web form at https://www.fujifilmapi.com/contact-us. 

### License
IMPORTANT - PLEASE READ THE FOLLOWING TERMS AND CONDITIONS CAREFULLY BEFORE USING THE FOLLOWING COMPUTER CODE (THE “CODE”). USE OF THE CODE IS AT YOUR OWN RISK. THE CODE IS PROVIDED “AS IS”, WITH ANY AND ALL FAULTS, DEFECTS AND ERRORS, AND WITHOUT ANY WARRANTY OF ANY KIND. FUJIFILM DISCLAIMS ALL WARRANTIES, WHETHER EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION, ALL IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT, WITH RESPECT TO THE CODE OR DEFECTS IN OPERATION OR ANY PARTICULAR APPLICATION OR USE OF THE CODE. FUJIFILM DOES NOT WARRANT THAT THE CODE WILL MEET YOUR REQUIREMENTS OR EXPECTATIONS, THAT THE CODE WILL WORK ON ANY HARDWARE, OPERATING SYSTEM OR WITH ANY SOFTWARE, THAT THE OPERATION OF THE CODE WILL BE UNINTERRUPTED, FREE OF HARMFUL COMPONENTS OR ERROR-FREE, OR THAT ANY KNOWN OR DISCOVERED ERRORS WILL BE CORRECTED.
FUJIFILM SHALL NOT BE LIABLE TO YOU OR ANY THIRD PARTY FOR ANY LOSS OF PROFIT, LOSS OF DATA, COMPUTER FAILURE OR MALFUNCTION, INTERRUPTION OF BUSINESS, OR OTHER DAMAGE ARISING OUT OF OR RELATING TO THE CODE, INCLUDING, WITHOUT LIMITATION, EXEMPLARY, PUNITIVE, SPECIAL, STATUTORY, DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, TORT OR COVER DAMAGES, WHETHER IN CONTRACT, TORT OR OTHERWISE, INCLUDING, WITHOUT LIMITATION, DAMAGES RESULTING FROM THE USE OR INABILITY TO USE THE CODE, EVEN IF FUJIFILM HAS BEEN ADVISED OR AWARE OF THE POSSIBILITY OF SUCH DAMAGES.
