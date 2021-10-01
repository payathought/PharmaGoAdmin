package com.example.pharmago.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmago.Adapter.ViewMyOrderListAdapter;
import com.example.pharmago.Model.MyOrderItemsModel;
import com.example.pharmago.Model.MyOrderModel;
import com.example.pharmago.Model.UserSignUpModel;
import com.example.pharmago.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewOrderActivity extends AppCompatActivity {
    private ArrayList<MyOrderItemsModel> mMyOrderItemsModel;

    Intent intent;
    String id = "";
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    TextView tv_total,tv_payment_method,tv_name,tv_number;
    CardView cv_total;
    ImageView iv_empty;
    ConstraintLayout parent_layout;
    int total= 0;
    int codTotal= 0;
    private static final String TAG = "ViewOrderActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        intent = getIntent();
        id = intent.getStringExtra("order_id");
        rv_orderList = findViewById(R.id.rv_orderList);
        tv_total = findViewById(R.id.tv_total);
        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        cv_total = findViewById(R.id.cv_total);
        tv_payment_method = findViewById(R.id.tv_payment_method);

        iv_empty = findViewById(R.id.iv_empty);
        parent_layout = findViewById(R.id.parent_layout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(ViewOrderActivity.this);
        progressDialog.setTitle("Pharma Go");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        mMyOrderItemsModel = new ArrayList<>();

        rv_orderList.setHasFixedSize(true);
        rv_orderList.setLayoutManager(new LinearLayoutManager(ViewOrderActivity.this));

        db.collection(getString(R.string.COLLECTION_MY_ORDERLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){

                            if(document.getId().equals(id)){

                                MyOrderModel myOrderModel = document.toObject(MyOrderModel.class);

                                CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_USER_INFORMATION));
                                Query userInfoQuery = userinfo.whereEqualTo("user_id", myOrderModel.getUser_id());
                                userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        if (!querySnapshot.isEmpty()) {

                                            UserSignUpModel userModel = querySnapshot.getDocuments().get(0).toObject(UserSignUpModel.class);
                                            tv_name.setText(userModel.getFirstname() + " " + userModel.getLastname());
                                            tv_number.setText(userModel.getPhonenumber());




                                        }
                                    }
                                });
                                Log.d(TAG, "payment: " + myOrderModel.getPayment_method());
                                if(myOrderModel.getPayment_method().equals("cod")){
                                    tv_payment_method.setText(myOrderModel.getPayment_method().toUpperCase());
                                }else{
                                    tv_payment_method.setText("Debit/Credit Card");
                                }

                                Log.d(TAG, "onEvent: " + myOrderModel.getPayment_method());
                            }



                        }

                    }
                });
        db.collection(getString(R.string.COLLECTION_MY_ORDERLIST_ITEMS))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mMyOrderItemsModel.clear();
                        total= 0;
                        codTotal= 0;
                        Log.d(TAG, "onEvent: pasok sa db");
                        if(mMyOrderItemsModel.size() == 0){
                            cv_total.setVisibility(View.GONE);
                        }else {
                            cv_total.setVisibility(View.VISIBLE);
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            MyOrderItemsModel orderItems = document.toObject(MyOrderItemsModel.class);



                            Log.d(TAG, "onEvent: " + document.getId());
//
                            if(id.equals(orderItems.getMyOrder_id())){
                                mMyOrderItemsModel.add(orderItems);
                                codTotal += Integer.parseInt(orderItems.getMedecine_price());
                                tv_total.setText("₱" + (codTotal+100));

                                Log.d(TAG, "onEvent: " + codTotal);

                            }

                            if(mMyOrderItemsModel.size() == 0){
                                cv_total.setVisibility(View.GONE);
                            }else {
                                cv_total.setVisibility(View.VISIBLE);
                            }

                        }


                        if(mMyOrderItemsModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            rv_orderList.setVisibility(View.GONE);
                            cv_total.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            rv_orderList.setVisibility(View.VISIBLE);
                            cv_total.setVisibility(View.VISIBLE);
                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }

                        ViewMyOrderListAdapter viewMyOrderListAdapter = new ViewMyOrderListAdapter( ViewOrderActivity.this, mMyOrderItemsModel);
                        rv_orderList.setAdapter(viewMyOrderListAdapter);


                    }
                });
    }
}