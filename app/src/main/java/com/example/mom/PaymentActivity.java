package com.example.mom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mom.Module.Invoice;
import com.example.mom.databinding.ActivityPaymentBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;

import static com.example.mom.DefineVars.*;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    SimpleDateFormat time_format;
    Calendar calendar;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding         = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        time_format     = new SimpleDateFormat("kk:mm:ss");
        calendar        = Calendar.getInstance();
        executor = ContextCompat.getMainExecutor(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent payment_intent = getIntent();
        Invoice payment =  (Invoice) payment_intent.getSerializableExtra(PAYMENT_INFO);
        calendar.setTimeInMillis(payment.getTime());
        binding.paymentBillID.setText(payment.getBillID());
        binding.paymentBillId.setText(payment.getBillID());
        binding.billDuedate.setText(listMonth[calendar.get(Calendar.MONTH)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
        binding.billTime.setText(time_format.format(payment.getTime()));
        binding.billTotal.setText(String.valueOf(payment.getTotal()));
        binding.paymentAmount.setText(payment.getAmount()+" "+payment.getUnit());
        binding.invoiceIcon.setOnClickListener(v -> onBackPressed());
        biometricPrompt = new BiometricPrompt(PaymentActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Payment")
                .setSubtitle("You can use password instead!")
                .setNegativeButtonText("Use password")
                .build();
        binding.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentInvoice();
            }
        });
    }

    private void PaymentInvoice() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                //App can use
                Toast.makeText(getApplicationContext(), "CAN USE FINGERPRINT", Toast.LENGTH_LONG).show();
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                break;
        }
    }
}