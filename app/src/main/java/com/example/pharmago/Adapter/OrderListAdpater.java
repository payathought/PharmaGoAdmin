package com.example.pharmago.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmago.Model.OrderModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderListAdpater extends RecyclerView.Adapter<OrderListAdpater.ViewHolder>  {
    private Context mContext;
    ArrayList<OrderModel> mOrderModel = new ArrayList();
    private static final String TAG = "OrderListAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderListAdpater(Context mContext, ArrayList<OrderModel> mOrderModel) {
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

        OrderModel orderModel = mOrderModel.get(position);

        db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(orderModel.getPharmacy_id())){

                                    PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                    holder.tv_medName.setText(orderModel.getMedecine_name());
                                    holder.tv_price.setText("₱"+orderModel.getMedecine_price());
                                    holder.tv_driverStatus.setText(orderModel.getStatus().toUpperCase());
                                    holder.tv_quantity.setText(orderModel.getQuantity().toUpperCase());
                                    holder.tv_pharmaName.setText(pharmacyModel.getPharmacy_name());
                                    Log.d(TAG, "onComplete: " + orderModel.getPaymant_method());
                                    if(orderModel.getPaymant_method().equals("cod")){
                                        holder.tv_payment_method.setText("COD");

                                    }else {
                                        holder.tv_payment_method.setText("Paid");

                                    }



                                }
                            }
                        }


                    }
                });



        if((orderModel.getStatus().equals("pending"))){
            holder.tv_driverStatus.setVisibility(View.GONE);
            holder.btn_olAccept.setVisibility(View.VISIBLE);
            holder.btn_olCancel.setVisibility(View.VISIBLE);
        }else {
            holder.tv_driverStatus.setVisibility(View.VISIBLE);
            holder.btn_olAccept.setVisibility(View.GONE);
            holder.btn_olCancel.setVisibility(View.GONE);
        }

        holder.btn_olAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderModel order = mOrderModel.get(position);
                order.setStatus("accepted");
                db.collection(mContext.getString(R.string.COLLECTION_ORDERLIST))
                        .document(order.getOrder_id())
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                notifyDataSetChanged();
                            }
                        });
            }
        });
        holder.btn_olCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderModel order = mOrderModel.get(position);
                order.setStatus("cancel");
                db.collection(mContext.getString(R.string.COLLECTION_ORDERLIST))
                        .document(order.getOrder_id())
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

            }
        });



    }

    @Override
    public int getItemCount() {
        return mOrderModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_price,tv_driverStatus,tv_payment_method,tv_medName,tv_quantity;
        Button btn_olAccept,btn_olCancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_driverStatus = itemView.findViewById(R.id.tv_driverStatus);
            btn_olAccept = itemView.findViewById(R.id.btn_olAccept);
            btn_olCancel = itemView.findViewById(R.id.btn_olCancel);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);





        }
    }


}
