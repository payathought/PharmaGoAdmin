package com.example.pharmago.View;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmago.Adapter.MedicineListAdapter;
import com.example.pharmago.Adapter.OrderListAdpater;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.MyOrderModel;
import com.example.pharmago.Model.OrderModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class OrderListFragment extends Fragment {
    private View view;
    private ArrayList<MyOrderModel> mOrderModel;
    private static final String TAG = "OrderListFragment";
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_orderList;
    ProgressDialog progressDialog;
    ImageView iv_empty;
    TextView tv_empty;
    ConstraintLayout parent_layout;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    public OrderListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_list, container, false);

        rv_orderList = view.findViewById(R.id.rv_orderList);
        rv_orderList.setHasFixedSize(true);
        rv_orderList.setLayoutManager(new LinearLayoutManager(getContext()));

        tv_empty = view.findViewById(R.id.tv_empty);
        iv_empty = view.findViewById(R.id.iv_empty);
        parent_layout = view.findViewById(R.id.parent_layout);

        user = FirebaseAuth.getInstance().getCurrentUser();




        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String pharmaId = document.getId();
                                PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                if(pharmacyModel.getUser_id().equals(firebaseUser.getUid())){
                                    db.collection(getString(R.string.COLLECTION_MY_ORDERLIST)).
                                            addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                    mOrderModel = new ArrayList<>();
                                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                                                        MyOrderModel orderModel = document.toObject(MyOrderModel.class);

                                                        Log.d(TAG, "onEvent: " + orderModel.getStatus());
                                                        orderModel.setMyOrder_id(document.getId());
                                                        if(!(document.get("status").toString().equals("cancel")) && pharmaId.equals(orderModel.getPharmacy_id())){
                                                            mOrderModel.add(orderModel);


                                                        }

                                                        if(mOrderModel.size() == 0){
                                                            iv_empty.setVisibility(View.VISIBLE);
                                                            rv_orderList.setVisibility(View.GONE);
                                                            tv_empty.setVisibility(View.VISIBLE);
                                                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                                                        }else {
                                                            iv_empty.setVisibility(View.GONE);
                                                            rv_orderList.setVisibility(View.VISIBLE);
                                                            tv_empty.setVisibility(View.GONE);
                                                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                                        }

                                                        Log.d(TAG, "onEvent: " + mOrderModel.size());
                                                        OrderListAdpater myOrderListAdapter = new OrderListAdpater(getContext(), mOrderModel);
                                                        rv_orderList.setAdapter(myOrderListAdapter);
                                                    }

                                                }
                                            });

                                }else{
                                    iv_empty.setVisibility(View.VISIBLE);
                                    rv_orderList.setVisibility(View.GONE);
                                    tv_empty.setVisibility(View.VISIBLE);
                                    parent_layout.setBackgroundColor(Color.parseColor("#255265"));
                                }

                            }
                        }


                    }
                });



        return view;
    }
}