package com.example.mom;

import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Random;

public class DefineVars {
    /*
        ================== Doc ==================
        Material Design                 : https://material.io/components/text-fields/android#using-text-fields
        Login with phone number         : https://firebase.google.com/docs/auth/android/phone-auth
        Tablayout MDC                   : https://www.youtube.com/watch?v=eXK4VMI9XLI (MDC: Material Design Components)
        Phone authen example            : https://www.youtube.com/watch?v=W8eGh6vKKR8
        Conver phone number             : https://github.com/google/libphonenumber
        Navigation Drawer MDC           : https://material.io/components/navigation-drawer/android#bottom-navigation-drawer
        Glide                           : https://github.com/bumptech/glide
        Manager User Fbase              : https://firebase.google.com/docs/auth/android/manage-users
        Databinding                     : https://developer.android.com/topic/libraries/data-binding/expressions
        Query/Getting firestore         : https://firebase.google.com/docs/firestore/query-data/order-limit-data#java
        Biometric Authentication        : https://developer.android.com/training/sign-in/biometric-auth
        LineChart (String xAxis)        : https://stackoverflow.com/questions/45320457/how-to-set-string-value-of-xaxis-in-mpandroidchart

        ================== Note =================
        platform-tools                  : %LOCALAPPDATA%\Android\Sdk\platform-tools
        zXing beep crash fix            : https://github.com/zxing/zxing/issues/775
        Compare List Object by fields   : https://www.codebyamir.com/blog/sort-list-of-objects-by-field-java
        GridView                        : https://openplanning.net/10473/android-gridview

    */

    public static final int LOADING_TIME        = 3500;     //ms
    public static final int RC_SIGN_IN          = 123;      //random code
    public static final long TIMEOUT            = 60L;      //After <TIMEOUT> senconds, OTP will be invoked
    public static final String[] listMonth      = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    //============== FireStore Collections ===============
    public static final String USERS            = "users";
    public static final String PAYMENT_EVENTS   = "payment_events";
    public static final String GROUP_USERS      = "groups";
    public static final String EARNING_CATE     = "earnings_categories";
    public static final String CONSUMING_CATE   = "consuming_categories";

    //============== Intent ==============
    public static final String PAYMENT_INFO     = "payment_info";
    public static final String MOM_BILL         = "mom";
    public static final String DETAIL_PAYMENTS  = "detail_info";

    //============== Gen Bill ID ==============
    private static Random g                     = new Random();

    public static String GenBillID(String pre) {
        String x = pre;
        for (int i=0; i<3; i++) {
            x += "-"+ g.nextInt(900);
        }
        return x;
    }

}
