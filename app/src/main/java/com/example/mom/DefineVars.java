package com.example.mom;

import java.util.ArrayList;

public class DefineVars {
    /*
        ================== Doc ==================
        Material Design             : https://material.io/components/text-fields/android#using-text-fields
        Login with phone number     : https://firebase.google.com/docs/auth/android/phone-auth
        Tablayout MDC               : https://www.youtube.com/watch?v=eXK4VMI9XLI (MDC: Material Design Components)
        Phone authen example        : https://www.youtube.com/watch?v=W8eGh6vKKR8
        Conver phone number         : https://github.com/google/libphonenumber
        Navigation Drawer MDC       : https://material.io/components/navigation-drawer/android#bottom-navigation-drawer
        Glide                       : https://github.com/bumptech/glide
        Manager User Fbase          : https://firebase.google.com/docs/auth/android/manage-users
        Databinding                 : https://developer.android.com/topic/libraries/data-binding/expressions
        Query firestore             : https://firebase.google.com/docs/firestore/query-data/order-limit-data#java

        ================== Note =================
        platform-tools              : %LOCALAPPDATA%\Android\Sdk\platform-tools

    */

    public static final int LOADING_TIME    = 5000;     //ms
    public static final int RC_SIGN_IN      = 123;      //random code
    public static final long TIMEOUT        = 60L;      //After <TIMEOUT> senconds, OTP will be invoked
    public static final String[] listMonth  = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    //============== FireStore Collections ===============
    public static final String USERS        = "users";
    public static final String BILLS        = "bills";
    public static final String MOM_BILL     = "mom";

    //============== Intent ==============
    public static final String PAYMENT_INFO = "payment_info";
}
