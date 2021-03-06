package com.example.mom.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mom.MainActivity;
import com.example.mom.R;
import com.example.mom.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.mom.DefineVars.RC_SIGN_IN;

public class AccLoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LinearLayout acclogin;
    private TextInputLayout email, password;
    private AppCompatButton login_btn;
    private ImageView google, phone;
    private ProgressDialog progressDialog;
    boolean isError = false;
    private FirebaseAuth auth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_login);
        auth            = FirebaseAuth.getInstance();
        gso             = new GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
        gsoClient       = GoogleSignIn.getClient(this, gso);
        acclogin        = binding.acclogin;
        email           = binding.loginEmail;
        password        = binding.loginPass;
        login_btn       = binding.loginBtn;
        google          = binding.loginGoogle;
        phone           = binding.loginPhone;
        progressDialog  = new ProgressDialog(this);
        email.setBoxStrokeColor(Color.parseColor("#FFFFFF"));
        password.setBoxStrokeColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.createAcc.setOnClickListener(v -> startActivity(new Intent(AccLoginActivity.this, CreateNewUserActivity.class)));
        login_btn.setOnClickListener(v -> LoginWithAccount());
        google.setOnClickListener(v -> LoginWithGoogleClient());
        phone.setOnClickListener(v -> startActivity(new Intent(AccLoginActivity.this, PhoneLoginActivity.class)));
    }

    private void LoginWithGoogleClient() {
        Intent signInIntent = gsoClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            acclogin.setVisibility(View.GONE);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE LOGIN:", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLE LOGIN", "Google sign failed", e);
                Toast.makeText(getApplicationContext(), "Google login failed!", Toast.LENGTH_LONG).show();
                acclogin.setVisibility(View.VISIBLE);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
//                        Log.d("GOOGLE LOGIN", "signInWithCredential:success");
                    FirebaseUser user = auth.getCurrentUser();
                    //Start intent here
                    StartMainScreen();
                } else {
                    // If sign in fails, display a message to the user.
//                        Log.w("GOOGLE LOGIN", "signInWithCredential:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Signin with credential failed!", Toast.LENGTH_LONG).show();
                    acclogin.setVisibility(View.VISIBLE);
                }
            });
    }


    private void LoginWithAccount() {
        isError = false;
        //Validation input fields
        String email_txt = email.getEditText().getText().toString();
        String pass_txt  = password.getEditText().getText().toString();
        if (email_txt.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email_txt).matches()) {
            isError = true;
            email.setError(getString(R.string.email_err));
        } else {
            email.setError(null);
        }
        if (pass_txt.isEmpty() || pass_txt.length() < 6) {
            isError = true;
            password.setError(getString(R.string.pass_err));
        } else {
            password.setError(null);
        }
        if (isError) return;
        //Start login here
        progressDialog.setMessage("Waiting...");
        progressDialog.show();
        auth.signInWithEmailAndPassword(email_txt, pass_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.hide();
                if (task.isSuccessful()) {
                    StartMainScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "Account is not existed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void StartMainScreen() {
        //if login successful will go to here
        startActivity(new Intent(AccLoginActivity.this, MainActivity.class));
        finish();
    }
}