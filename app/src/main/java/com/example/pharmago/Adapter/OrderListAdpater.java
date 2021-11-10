package com.example.pharmago.Adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmago.FunctionMethod.FunctionMethod;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.MyOrderItemsModel;
import com.example.pharmago.Model.MyOrderModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.Model.SignUpModel;
import com.example.pharmago.R;
import com.example.pharmago.View.Dialog.AddQuantityDialog;
import com.example.pharmago.View.Dialog.ViewUserInfoDialog;
import com.example.pharmago.View.Dialog.ViewUserInfoInToolBarDialog;
import com.example.pharmago.View.ViewOrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class OrderListAdpater extends RecyclerView.Adapter<OrderListAdpater.ViewHolder>  {
    private Context mContext;
    ArrayList<MyOrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "OrderListAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    public OrderListAdpater(Context mContext, ArrayList<MyOrderModel> mOrderModel) {
        this.mContext = mContext;
        this.mOrderModel = mOrderModel;
    }

    @NonNull
    @Override
    public OrderListAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_list_item, parent,false);
        OrderListAdpater.ViewHolder holder = new OrderListAdpater.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdpater.ViewHolder holder, int position) {

        MyOrderModel orderModel = mOrderModel.get(position);

        db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(orderModel.getPharmacy_id())){

                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                    SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
                                    holder.tv_medName.setText("Date Ordered: " + formatter.format(orderModel.getDateOrdered()));

                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());
                                    Log.d(TAG, "onComplete: " + orderModel.getPayment_method());
                                    if(orderModel.getPayment_method().equals("cod")){
                                        holder.tv_payment_method.setText("Payment Method: COD");

                                    }else {
                                        holder.tv_payment_method.setText("Payment Method: Credit/Debit (Paid)");

                                    }
                                    if(orderModel.getDriver_status().equals("pending")){

                                        holder.tv_driverStatus.setText("Order Status: " + orderModel.getStatus().toUpperCase());

                                    }else {
                                        if(orderModel.getStatus().equals("done")){
                                            holder.tv_driverStatus.setText("Order Status: " + orderModel.getStatus().toUpperCase());
                                        }else {
                                            holder.tv_driverStatus.setText("Order Status: Accepted By The driver");
                                            holder.tv_driverStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                        }
                                    }



                                }
                            }
                        }


                    }
                });



        if((orderModel.getStatus().equals("pending"))){
//            holder.tv_driverStatus.setVisibility(View.GONE);
            holder.btn_olAccept.setVisibility(View.VISIBLE);
            holder.btn_olCancel.setVisibility(View.VISIBLE);
        }else {
//            holder.tv_driverStatus.setVisibility(View.VISIBLE);
            holder.btn_olAccept.setVisibility(View.GONE);
            holder.btn_olCancel.setVisibility(View.GONE);
        }

        holder.btn_olAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyOrderModel order = mOrderModel.get(position);

                order.setStatus("accepted");
                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST))
                        .document(order.getMyOrder_id())
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyDataSetChanged();
                            }
                        });
                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST_ITEMS))
                        .whereEqualTo("myOrder_id",order.getMyOrder_id())
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                Log.d(TAG, "onEvent: " + queryDocumentSnapshots.size());

                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                                        MyOrderItemsModel orderItems = document.toObject(MyOrderItemsModel.class);
                                        db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
                                                .whereEqualTo("medicine_id",orderItems.getMedicine_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){

                                                                for (DocumentSnapshot document : task.getResult()){
                                                                    MedicineModel medicineModel = document.toObject(MedicineModel.class);
                                                                    int medQty = medicineModel.getMedicine_quantity();
                                                                    int orderQty = Integer.parseInt(orderItems.getQuantity());
                                                                    int newQty = medQty-orderQty;
                                                                    Log.d(TAG, "compare: medQTy" + medQty + " orderQty: " +orderQty);
                                                                    medicineModel.setMedicine_quantity(newQty);
                                                                    db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
                                                                            .document(medicineModel.getMedicine_id())
                                                                            .set(medicineModel)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Log.d(TAG, "onSuccess: ");
                                                                                }
                                                                            });
//                                                                    if(medQty < orderQty){
//                                                                        AddQuantityDialog dialog = new AddQuantityDialog(medicineModel.getMedecine_name(),medicineModel.getPharmacy_id());
//                                                                        dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
//
//                                                                    }else {
//
//                                                                    }
                                                                }


                                                        }

                                                    }
                                                });


                                    }


                            }
                        });





//                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST_ITEMS))
//                        .whereEqualTo("myOrder_id",order.getMyOrder_id())
//                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                Log.d(TAG, "onEvent: " + queryDocumentSnapshots.size());
//                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
//                                    MyOrderItemsModel orderItems = document.toObject(MyOrderItemsModel.class);
//                                    db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
//                                            .whereEqualTo("medicine_id",orderItems.getMedicine_id())
//                                            .get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull  Task<QuerySnapshot> task) {
//                                                    if (task.isSuccessful()){
//                                                        for (DocumentSnapshot document : task.getResult()){
//                                                            MedicineModel medicineModel = document.toObject(MedicineModel.class);
//                                                            int medQty = medicineModel.getMedicine_quantity();
//                                                            int orderQty = Integer.parseInt(orderItems.getQuantity());
//                                                            int newQty = medQty-orderQty;
//                                                            Log.d(TAG, "compare: medQTy" + medQty + " orderQty: " +orderQty);
//                                                            if(medQty < orderQty){
//
//                                                                Log.d(TAG, "medQty is less than qty");
//                                                                AddQuantityDialog dialog = new AddQuantityDialog(medicineModel.getMedecine_name(),medicineModel.getPharmacy_id());
//                                                                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
//
//                                                            }else {
//                                                                medicineModel.setMedicine_quantity(newQty);
//                                                                db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
//                                                                        .document(medicineModel.getMedicine_id())
//                                                                        .set(medicineModel)
//                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                            @Override
//                                                                            public void onSuccess(Void unused) {
//                                                                                Log.d(TAG, "onSuccess: ");
//                                                                            }
//                                                                        });
//                                                            }
//                                                        }
//                                                    }
//
//                                                }
//                                            });
////                                    if(isGreaterthan){
////                                        db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
////                                                .whereEqualTo("medicine_id",orderItems.getMedicine_id())
////                                                .get()
////                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                                    @Override
////                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                                        if (task.isSuccessful()){
////                                                            Log.d(TAG, "task: " + task.getResult().size());
////                                                            for (DocumentSnapshot doc : task.getResult()){
////
////                                                                MedicineModel medicineModel = doc.toObject(MedicineModel.class);
////                                                                int medQty = medicineModel.getMedicine_quantity();
////                                                                int orderQty = Integer.parseInt(orderItems.getQuantity());
////                                                                int newQty = medQty-orderQty;
////                                                                medicineModel.setMedicine_quantity(newQty);
////                                                                Log.d(TAG, "compare: medQTy" + medQty + " orderQty: " +orderQty);
////                                                                Log.d(TAG, "newQty: " + newQty);
////
////
////                                                            }
////                                                        }
////
////                                                    }
////                                                });
////                                    }
//
//
////                                    db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
////                                            .whereEqualTo("medicine_id",orderItems.getMedicine_id())
////                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
////                                                @Override
////                                                public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable  FirebaseFirestoreException e) {
////                                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
////                                                        MedicineModel medicineModel = document.toObject(MedicineModel.class);
////                                                        int medQty = medicineModel.getMedicine_quantity();
////                                                        int orderQty = Integer.parseInt(orderItems.getQuantity());
////                                                        Log.d(TAG, "compare: medQTy" + medQty + " orderQty: " +orderQty);
////                                                        if(medQty < orderQty){
////                                                            Log.d(TAG, "medQty is less than qty");
////                                                            AddQuantityDialog dialog = new AddQuantityDialog(medicineModel.getMedecine_name(),medicineModel.getPharmacy_id());
////                                                            dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
////                                                            break;
////                                                        }
////                                                    }
////                                                }
////                                            });
//
//
//
//                                }
//
//
//                            }
//                        });
//                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST_ITEMS))
//                        .whereEqualTo("myOrder_id",order.getMyOrder_id())
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()){
//                                    for (DocumentSnapshot document : task.getResult()){
//                                            MyOrderItemsModel orderItems = document.toObject(MyOrderItemsModel.class);
//
//
//                                    }
//                                }
//                            }
//                        });




            }
        });
        holder.btn_olCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyOrderModel order = mOrderModel.get(position);
                order.setStatus("cancel");
                db.collection(mContext.getString(R.string.COLLECTION_MY_ORDERLIST))
                        .document(order.getMyOrder_id())
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyDataSetChanged();
                            }
                        });
            }
        });



        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderModel.getDriver_id().isEmpty()){
                    Intent i = new Intent(mContext, ViewOrderActivity.class);
                    i.putExtra("order_id", orderModel.getMyOrder_id());
                    mContext.startActivity(i);
                }else {
                    if(orderModel.getStatus().equals("done")){
                        Intent i = new Intent(mContext, ViewOrderActivity.class);
                        i.putExtra("order_id", orderModel.getMyOrder_id());
                        mContext.startActivity(i);
                    }else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setMessage("What do you want to do? ");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "View Driver Profile",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        ViewUserInfoDialog viewUserInfoDialog = new ViewUserInfoDialog(orderModel.getDriver_id());
                                        viewUserInfoDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");
                                    }
                                });

                        builder1.setNegativeButton(
                                "View Order",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                        Intent i = new Intent(mContext, ViewOrderActivity.class);
                                        i.putExtra("order_id", orderModel.getMyOrder_id());
                                        mContext.startActivity(i);
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }

                }

            }
        });
        holder.btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    MyOrderModel order = mOrderModel.get(position);

                    db.collection(mContext.getString(R.string.COLLECTION_USER_INFORMATION))
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                    {
                                        SignUpModel userModel = document.toObject(SignUpModel.class);
                                        if(order.getUser_id().equals(userModel.getUser_id())) {


                                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setData(Uri.parse("smsto:" + userModel.getPhonenumber())); // This ensures only SMS apps respond
                                            intent.putExtra("sms_body", "Good day, " + userModel.getFirstname());
                                            mContext.startActivity(intent);
                                        }
                                    }
                                }
                            });
                }else{
                    FunctionMethod functionMethod = new FunctionMethod();

                    functionMethod.callPermission(mContext);
                }
            }
        });


    }



    @Override
    public int getItemCount() {
        return mOrderModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_driverStatus,tv_payment_method,tv_medName;
        Button btn_olAccept,btn_olCancel;
        ImageButton btn_sms;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            btn_olAccept = itemView.findViewById(R.id.btn_olAccept);
            btn_olCancel = itemView.findViewById(R.id.btn_olCancel);
            btn_sms = itemView.findViewById(R.id.btn_sms);






        }
    }


}
