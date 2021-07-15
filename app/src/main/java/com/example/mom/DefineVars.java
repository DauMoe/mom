package com.example.mom;

public class DefineVars {
    /*
        ================== Doc ==================
        Material Design             : https://material.io/components/text-fields/android#using-text-fields
        Login with phone number     : https://firebase.google.com/docs/auth/android/phone-auth

        ================== Note =================
        Signout Fbase               : FirebaseAuth.getInstance().signOut();
    */

    public static final int LOADING_TIME    = 5000;     //ms
    public static final int RC_SIGN_IN      = 123;      //random code
    public static final long TIMEOUT        = 60L;      //After <TIMEOUT> senconds, OTP will be invoked
}
