package com.example.pharmago.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.MyOrderItemsModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewMyOrderListAdapter extends RecyclerView.Adapter<ViewMyOrderListAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<MyOrderItemsModel> mMyOrderItemsModel = new ArrayList();
    private static final String TAG = "MedicineListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ViewMyOrderListAdapter(Context mContext, ArrayList<MyOrderItemsModel> mMyOrderItemsModel) {
        this.mContext = mContext;
        this.mMyOrderItemsModel = mMyOrderItemsModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item_list, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyOrderItemsModel cartModel = mMyOrderItemsModel.get(position);

        db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getId().equals(cartModel.getMedicine_id())){
                                    Log.d(TAG, "onComplete: on DB inside if");
                                    MedicineModel medicineModel = document.toObject(MedicineModel.class);

                                    holder.tv_medName.setText(medicineModel.getDescription());
                                    holder.tv_price.setText("₱"+cartModel.getMedecine_price());

                                    holder.tv_pharmaName.setText(medicineModel.getMedecine_name());
                                    holder.tv_quantity.setText(cartModel.getQuantity());



                                }
                            }
                        }


                    }
                });








    }

    @Override
    public int getItemCount() {
        return mMyOrderItemsModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ConstraintLayout parent_layout;
        TextView tv_pharmaName,tv_price,tv_medName,tv_quantity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_pharmaName = itemView.findViewById(R.id.tv_pharmaName);
            tv_medName = itemView.findViewById(R.id.tv_medName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);

        }
    }


}
