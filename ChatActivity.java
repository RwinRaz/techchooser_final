package com.razani.techchooser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razani.techchooser.adapters.MessageAdapter;
import com.razani.techchooser.entity.Messages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends BaseActivity {

    private String mChatUserID, mOrderName;
    private RelativeLayout mLayout;
    private Toolbar mChatToolBar;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserId, mOrderID;
    private ImageButton mSendBtn;
    private EditText mChatMessage;
    private RecyclerView mMessagesList;
    private LinearLayoutManager layoutManager;
    private List<Messages> listOfAll = new ArrayList<>();
    private MessageAdapter mAdaptor;
    private BigDecimal fee;
    private PaymentConfirmation confirm;
    private Button mDonate;
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)

            .clientId("AWgvrD_MrKn3MSXw1Sb76FNCWLhniuEyaGvo6uJzNGS2uSvBbkXqoa4yn4m131lz8AkShNH8M1Ucr3RV");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mOrderID = getIntent().getStringExtra("order_id");
        init();
        mChatUserID = getIntent().getStringExtra("counselorID");
        //setSeen();

        setSupportActionBar(mChatToolBar);
        mOrderName = getIntent().getStringExtra("name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mOrderName);
        mMessagesList.setAdapter(mAdaptor);
        loadMessages();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootRef.child("Orders").child(mOrderID).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().contains("finished")) {
                    mLayout.setVisibility(View.GONE);
                    mDonate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
        setSeen();
    }
    private void paypalOpener(BigDecimal fee) {
        PayPalPayment payment = new PayPalPayment(fee, "USD", "Donate Fee",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }
    public void onPayPalStart(View view) {
        if (isNetworkAvailable()) {
            Intent intent = new Intent(this, PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startService(intent);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText input = new EditText(ChatActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setTitle("Donation");
            builder.setMessage("Please enter the amount you want to donate in USD($), It will help us to provide better services.");
            // builder.setMessage("During the consultation, if you are dissatisfied, you can get your money back( after reducing the transfer fee). You can NOT get your money after application closes, but always have access to the consultation history. If we don't receive a response from you for 2 days your application will be closed( after send final answer) ");
            builder.setPositiveButton("Donate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fee = BigDecimal.valueOf(Float.parseFloat(input.getText().toString()));
                    paypalOpener(fee);
                    dialog.cancel();

                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                //confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
              //  sendOrder(true);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(ChatActivity.this, "invalid Payment", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadMessages() {

        mRootRef.child("Messages").child(mOrderID).child(mChatUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                listOfAll.add(message);
                mAdaptor.notifyDataSetChanged();
                mMessagesList.smoothScrollToPosition(mMessagesList.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        mLayout = findViewById(R.id.bottom);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mChatToolBar = findViewById(R.id.chat_app_bar);
        mChatMessage = findViewById(R.id.chat_message_et);
        mSendBtn = findViewById(R.id.chat_send_btn);
       mDonate= findViewById(R.id.paypal_donate_btn);
        mAdaptor = new MessageAdapter(listOfAll, mOrderID);
        mMessagesList = findViewById(R.id.chat_message_list);
        layoutManager = new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(layoutManager);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        if (isNetworkAvailable()) {
            String message = mChatMessage.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                Map messageMap = new HashMap();
                String current_user_ref = "Messages/" + mOrderID + "/" + mChatUserID;

                DatabaseReference user_message_push = mRootRef.child("Messages").child(mOrderID).child(mChatUserID).push();
                String push_id = user_message_push.getKey();
                messageMap.put("message", message);

                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", mOrderID);
                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                mRootRef.updateChildren(messageUserMap);
                mRootRef.child("Chats").child(mOrderID).child(mChatUserID).child("seen").setValue(false);
                mChatMessage.setText("");
                mChatMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);

            }
        }
    }

    private void setSeen() {
        mRootRef.child("Chats").child(mOrderID).child(mOrderID).child("seen").setValue(true);
    }
}

