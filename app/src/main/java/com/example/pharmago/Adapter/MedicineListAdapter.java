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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmago.View.AddMedicineActivity;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.R;
import com.example.pharmago.View.Dialog.AddMedicineDialog;
import com.example.pharmago.View.MedicineListActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<MedicineModel>  mMedecineModel = new ArrayList();
    private static final String TAG = "MedecineAdpater";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MedicineListAdapter(Context context, ArrayList<MedicineModel> mMedecineModel) {
        this.mContext = context;
        this.mMedecineModel = mMedecineModel;

    }

    @NonNull
    @Override
    public MedicineListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_medecine_list_item, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        mMedecineModel.forEach(item -> Log.d(TAG, "onBindViewHolder: " + item.getMedecine_name()));
        MedicineModel medicineModel = mMedecineModel.get(position);
        holder.tv_medecineName.setText(medicineModel.getMedecine_name());
        holder.tv_price.setText("₱"+medicineModel.getMedecine_price());

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicineModel medicineModel = mMedecineModel.get(position);
//                Intent i = new Intent(mContext, AddMedicineActivity.class);
//                i.putExtra("medicine_id", medicineModel.getMedicine_id());
//                mContext.startActivity(i);
                AddMedicineDialog dialog = new AddMedicineDialog(medicineModel,medicineModel.getPharmacy_id());

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
                                MedicineModel medicineModel = mMedecineModel.get(position);
                                db.collection(mContext.getString(R.string.COLLECTION_MEDICINELIST))
                                        .document(medicineModel.getMedicine_id())
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
        return mMedecineModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout parent_layout;
        TextView tv_medecineName,tv_price;
        Button btn_edit,btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_medecineName = itemView.findViewById(R.id.tv_medecineName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);




        }
    }



}