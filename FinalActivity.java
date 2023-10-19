package com.razani.techchooser;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinalActivity extends BaseActivity {

    private DatabaseReference mRootRef;//,nameRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> details;
    private String orderDetails = "";
    private TextView priceView, infoView;

    private Toolbar mToolbar;
    private RadioButton mLaptop, mDesktop;
    //    private BigDecimal fee;
//    private PaymentConfirmation confirm;
//    private static PayPalConfiguration config = new PayPalConfiguration()
//
//            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
//            // or live (ENVIRONMENT_PRODUCTION)
//            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
//
//            .clientId("AY5Vf5brumQHS5PK9wDgCTN7N21xX_kVYOilz6YcctEX-_XUZypwHov4ZgvA3w8ULnd5FVqlAOcrnsn0");
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        details = getIntent().getStringArrayListExtra("details");
        init();
//        Intent intent = new Intent(this, PayPalService.class);
//
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        //startService(intent);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Last Step!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7718702161034029/7590278508");

        //fee=BigDecimal.valueOf((Integer.valueOf(details.get(3))-(Integer.valueOf(details.get(3))%500))/100);
        // fee = BigDecimal.valueOf((Integer.valueOf(details.get(3))) * 1.15 / 100);
        // fee = fee.setScale(2, RoundingMode.HALF_DOWN);

        //float fee2= (float) (Float.parseFloat(fee.toString())-0.01);
        //fee= BigDecimal.valueOf(fee2);
        //  priceView.setText("$" + String.valueOf(fee));
        infoView.setText(orderDetails);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                sendOrder(false);
            }
        });


    }

    private void init() {
        for (int i = 0; i < details.size(); i++) {
            if (i == 0) {
                orderDetails += "Name: ";
            } else if (i == 1) {
                orderDetails += "Purpose: ";
            } else if (i == 2) {
                orderDetails += "Level: ";
            } else if (i == 3) {
                orderDetails += "Budget: $";
            } else if (i == 4) {
                orderDetails += "Apps used : ";
            } else if (i == 5) {
                orderDetails += "Considerations: ";
            }
            orderDetails += details.get(i);
            if (!(i == details.size() - 1)) {
                orderDetails += "\n";
            }

        }
        infoView = findViewById(R.id.info_tv);
        infoView.setMovementMethod(new ScrollingMovementMethod());
        mLaptop = findViewById(R.id.laptopButton);
        mDesktop = findViewById(R.id.desktopButton);
        mToolbar = findViewById(R.id.final_page_toolbar);

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void sendOrder(boolean donate) {


        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //nameRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        String type = "";
        if (mDesktop.isChecked()) {
            type = "Desktop";
        }
        if (mLaptop.isChecked()) {
            type = "Laptop";
        }
        Map orderMap = new HashMap();
        String order_ref = "Orders/";
        String user_ref = "Users/" + mAuth.getUid() + "/" + "orders";
        orderMap.put("name", details.get(0));
        orderMap.put("counselorID", "");
        orderMap.put("order_time", ServerValue.TIMESTAMP);
        orderMap.put("userID", mAuth.getUid());
        orderMap.put("status", "request");
        orderMap.put("order_details", "Type: " + type + "\n" + orderDetails);
//        if (donate) {
//            try {
//                try {
//                    orderMap.put("confirm", new ObjectMapper().readValue(confirm.toJSONObject().toString(4), HashMap.class));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        DatabaseReference order_push = mRootRef.child("Orders").push();
        DatabaseReference user_order_push = mRootRef.child(user_ref).push();
        String push_id = order_push.getKey();
        Map orderUserMap = new HashMap();
        Map order = new HashMap();
        order.put("orderID", push_id);
        orderUserMap.put(user_ref + "/" + user_order_push.getKey(), order);
        // Map request= new HashMap();
        //  request.put("Request/"+ user_order_push.getKey(), order);
        orderUserMap.put(order_ref + "/" + push_id, orderMap);
        orderUserMap.put("Requests/" + user_order_push.getKey(), order);
        mRootRef.updateChildren(orderUserMap);
        Map chatMap = new HashMap();
        chatMap.put("seen", true);
        mRootRef.child("Chats").child(push_id).child(push_id).setValue(chatMap);
        // startActivity(new Intent(FinalActivity.this, MainActivity.class));
        finish();
    }

//    private void paypalOpener(BigDecimal fee) {
//        PayPalPayment payment = new PayPalPayment(fee, "USD", "Donate Fee",
//                PayPalPayment.PAYMENT_INTENT_SALE);
//
//        Intent intent = new Intent(this, PaymentActivity.class);
//
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
//
//        startActivityForResult(intent, 0);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) {
//            if (resultCode == Activity.RESULT_OK) {
//                confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                sendOrder(true);
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("paymentExample", "The user canceled.");
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
//                Toast.makeText(FinalActivity.this, "invalid Payment", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    public void onPayPalStart(View view) {
//        if (isNetworkAvailable()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            final EditText input = new EditText(FinalActivity.this);
//            input.setInputType(InputType.TYPE_CLASS_NUMBER);
//            builder.setView(input);
//            builder.setTitle("Donation");
//            builder.setMessage("Please enter the amount you want to donate in USD($), It will help us to provide better services.");
//            // builder.setMessage("During the consultation, if you are dissatisfied, you can get your money back( after reducing the transfer fee). You can NOT get your money after application closes, but always have access to the consultation history. If we don't receive a response from you for 2 days your application will be closed( after send final answer) ");
//            builder.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    fee = BigDecimal.valueOf(Float.parseFloat(input.getText().toString()));
//                    paypalOpener(fee);
//                    dialog.cancel();
//
//                }
//            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
//
//        }
//    }

    public void onSetOrder1(View view) {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        else{
            sendOrder(false);
        }

        //sendOrder(false);
    }
}
