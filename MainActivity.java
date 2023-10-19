package com.razani.techchooser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razani.techchooser.entity.Order;
import com.razani.techchooser.entity.OrderID;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RecyclerView mOrdersList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef, mCounselorRef, mOrderRef, mRefChat;
    private FirebaseRecyclerAdapter adapter;
    private Toolbar mToolbar;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }
        else
        {
            mPref = getSharedPreferences("mPref", MODE_PRIVATE);

            boolean firstStart = mPref.getBoolean("firstOrder", true);
           // Toast.makeText(MainActivity.this,String.valueOf(firstStart),Toast.LENGTH_LONG).show();

            if (firstStart)
            {
                LinearLayout linearLayout=findViewById(R.id.firstorderlayout);
                linearLayout.setVisibility(View.VISIBLE);
                mPref.edit().putBoolean("firstOrder",false).apply();


            }

        }
        mToolbar = findViewById(R.id.main_page_toolbar);
        mOrdersList = findViewById(R.id.main_orders_recycler);
        mOrdersList.setHasFixedSize(true);
        mOrdersList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("   TechChooser");

    }

    @Override
    public void onStart() {
        super.onStart();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");

            mRefChat = FirebaseDatabase.getInstance().getReference().child("Chats");
            mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("orders");
            mCounselorRef = FirebaseDatabase.getInstance().getReference().child("Users");
            mOrderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
            FirebaseRecyclerOptions<OrderID> options = new FirebaseRecyclerOptions.Builder<OrderID>().setQuery(mUsersRef, OrderID.class).build();
            adapter = new FirebaseRecyclerAdapter<OrderID, OrdersViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final OrdersViewHolder holder, int position, @NonNull final OrderID model) {
                    // Bind the Chat object to the ChatHolder
                    //...

                    mOrderRef.child(model.getOrderID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Order order=new Order();
                            order.setName(dataSnapshot.child("name").getValue().toString());
                            order.setCounselorID(dataSnapshot.child("counselorID").getValue().toString());
                            order.setTime(Long.parseLong(dataSnapshot.child("order_time").getValue().toString()));
                            String format = simpleDateFormat.format(order.getTime());
                            holder.time.setText(format);
                            if (!order.getCounselorID().isEmpty()) {
                                mCounselorRef.child(order.getCounselorID()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        holder.counselorName.setText("Counselor's Name: " + dataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            holder.name.setText("Order Name: " + order.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    mRefChat.child(model.getOrderID()).child(model.getOrderID()).child("seen").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue().toString().equals("false")) {
                                holder.orderStatus.setText("Status: New Messages Are Here");

                            } else {
                                mOrderRef.child(model.getOrderID()).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Log.e("LIR",dataSnapshot.getValue().toString());
                                        if (dataSnapshot.getValue().toString().equals("request")) {
                                            holder.orderStatus.setText("Status: Wait for us to find a counselor, you can click here to view the counselor's updates.");
                                        } else {
                                            holder.orderStatus.setText("Status: " + dataSnapshot.getValue().toString());
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                    // final String order_id = model.getOrderID();

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!holder.counselorName.getText().toString().isEmpty()) {
                                mOrderRef.child(model.getOrderID()).child("counselorID").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("order_id", model.getOrderID()).putExtra("name", holder.name.getText().toString().replace("Order Name:","")).putExtra("counselorID", dataSnapshot.getValue().toString()));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Please wait, we are finding a counselor for your order.It usualy takes 2 days.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                @Override
                public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.single_order_layout, parent, false);

                    return new OrdersViewHolder(view);
                }

            };
            mOrdersList.setAdapter(adapter);
            adapter.startListening();


        }

    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout linearLayout=findViewById(R.id.firstorderlayout);
        linearLayout.setVisibility(View.GONE);
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView name, orderStatus, counselorName, time;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = mView.findViewById(R.id.order_single_name);
            orderStatus = mView.findViewById(R.id.order_single_status);
            counselorName = mView.findViewById(R.id.order_single_counselor);
            time = mView.findViewById(R.id.order_single_time);
        }
    }

    public void startOrder(View view) {


                if (isNetworkAvailable()) {
                    final ArrayList<String> details = new ArrayList<String>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Order Name");
                    builder.setMessage("Set a name for this order to remember it.");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input.getText().toString().isEmpty()) {
                                input.setText("An Order");
                            }
                            details.add(input.getText().toString());
                            startActivity(new Intent(MainActivity.this, PurposeActivity.class).putExtra("details", details));

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_logout_btn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                break;
            case R.id.main_setting_btn:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                break;
            case R.id.main_about_us_btn:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}