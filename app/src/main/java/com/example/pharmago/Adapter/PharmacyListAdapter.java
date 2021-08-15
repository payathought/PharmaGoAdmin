package com.example.pharmago.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.example.pharmago.View.AddMedicineActivity;
import com.example.pharmago.View.AddPharmacyActivity;
import com.example.pharmago.View.Dialog.AddPharmacyDialog;
import com.example.pharmago.View.MedicineListActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PharmacyListAdapter extends RecyclerView.Adapter<PharmacyListAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<PharmacyModel> mPharmacyModel = new ArrayList();
    private static final String TAG = "PharmacyListAdapter";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PharmacyListAdapter(Context context, ArrayList<PharmacyModel> mPharmacyModel) {
        this.mContext = context;
        this.mPharmacyModel = mPharmacyModel;

    }

    @NonNull
    @Override
    public PharmacyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pharmacy_list_item, parent,false);
        PharmacyListAdapter.ViewHolder holder = new PharmacyListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyListAdapter.ViewHolder holder, int position) {

        PharmacyModel pharmacyModel = mPharmacyModel.get(position);
        holder.tv_pharmacyName.setText(pharmacyModel.getPharmacy_name());

        holder.cv_cotainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PharmacyModel pharmacyModel = mPharmacyModel.get(position);
                Intent i = new Intent(mContext, MedicineListActivity.class);
                i.putExtra("pharmacy_id", pharmacyModel.getPharmacy_id());
                Log.d(TAG, "onClick: " + pharmacyModel.getPharmacy_id());
                mContext.startActivity(i);
            }
        });
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PharmacyModel pharmacyModel = mPharmacyModel.get(position);
//                Intent i = new Intent(mContext, AddPharmacyActivity.class);
//                i.putExtra("pharmacy_id", pharmacyModel.getPharmacy_id());
//                mContext.startActivity(i);
                AddPharmacyDialog dialog = new AddPharmacyDialog(pharmacyModel);
                dialog.show(  ((AppCompatActivity) mContext).getSupportFragmentManager(), "PharmaGo");

            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Are you sure you want to delete this data?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PharmacyModel pharmacyModel = mPharmacyModel.get(position);
                                db.collection(mContext.getString(R.string.COLLECTION_PHARMACYLIST))
                                        .document(pharmacyModel.getPharmacy_id())
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toasty.info(mContext,
                                                "Data has been deleted.", Toast.LENGTH_LONG)
                                                .show();
                                        notifyDataSetChanged();
                                    }
                                });
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();


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
        return mPharmacyModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_pharmacyName;
        Button btn_edit,btn_delete;
        CardView cv_cotainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_cotainer  = itemView.findViewById(R.id.cv_cotainer);
            tv_pharmacyName = itemView.findViewById(R.id.tv_pharmacyName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);




        }
    }



}