package com.example.pharmago.View.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.util.LogTime;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.R;
import com.example.pharmago.SignUp;
import com.example.pharmago.View.AddMedicineActivity;
import com.example.pharmago.View.HomeActivity;
import com.example.pharmago.View.MedicineListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class AddMedicineDialog extends AppCompatDialogFragment {
    MedicineModel medicineModel;
    Button btn_cancel, btn_add;
    EditText et_medicineName,et_medicinePrice,et_quantity,et_description;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddMedicineDialog";
    String pharmacy_id;

    RadioButton rb_medicine, rb_vitamins,rb_supplements;
    RadioGroup radio_group;
    TextView tv_rbRequired;

    String category;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_medecine_to_list_dialog,null);
        btn_add = view.findViewById(R.id.btn_add);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        et_medicineName = view.findViewById(R.id.et_medicineName);
        et_medicinePrice = view.findViewById(R.id.et_medicinePrice);
        et_quantity = view.findViewById(R.id.et_quantity);
        et_description = view.findViewById(R.id.et_description);
        rb_medicine = view.findViewById(R.id.rb_medicine);
        rb_vitamins = view.findViewById(R.id.rb_vitamins);
        rb_supplements = view.findViewById(R.id.rb_supplements);
        radio_group = view.findViewById(R.id.radio_group);
        tv_rbRequired = view.findViewById(R.id.tv_rbRequired);

        FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
        builder.setView(view);



        if(medicineModel != null){

            btn_add.setText("Update");

            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(medicineModel.getMedicine_id())){

                                        MedicineModel med = document.toObject(MedicineModel.class);
                                        et_medicineName.setText(med.getMedecine_name());
                                        et_medicinePrice.setText(med.getMedecine_price());
                                        et_quantity.setText(String.valueOf(med.getMedicine_quantity()));
                                        et_description.setText(String.valueOf(med.getDescription()));

                                        category = med.getCategory();

                                        if (category.equals("Medicine")){
                                            rb_medicine.setChecked(true);
                                        }else if (med.getCategory().equals("Vitamins")){
                                            rb_vitamins.setChecked(true);
                                        }else{
                                            rb_supplements.setChecked(true);
                                        }

                                    }
                                }
                            }


                        }
                    });


        }else
        {

            btn_add.setText("Add");
        }

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rb_medicine:
                        category = rb_medicine.getText().toString();
                        tv_rbRequired.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vitamins:

                        category = rb_vitamins.getText().toString();
                        tv_rbRequired.setVisibility(View.GONE);
                        break;
                    case R.id.rb_supplements:

                        category = rb_supplements.getText().toString();
                        tv_rbRequired.setVisibility(View.GONE);
                        break;

                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_medicineName.getText().toString().trim().isEmpty()){
                    et_medicineName.setError("This Field is required");
                }else if (et_medicinePrice.getText().toString().trim().isEmpty()){
                    et_medicinePrice.setError("This Field is required");

                }else if (et_quantity.getText().toString().trim().isEmpty()){
                    et_quantity.setError("This Field is required");

                }else if (et_description.getText().toString().trim().isEmpty()){
                    et_description.setError("This Field is required");

                }
                else{
                    if(medicineModel != null){

                        medicineModel.setMedecine_name(et_medicineName.getText().toString().trim());
                        medicineModel.setMedecine_price(et_medicinePrice.getText().toString().trim());
                        medicineModel.setMedicine_id(medicineModel.getMedicine_id());
                        medicineModel.setPharmacy_id(pharmacy_id);
                        medicineModel.setMedicine_quantity(Integer.parseInt(et_quantity.getText().toString()));
                        medicineModel.setDescription((et_description.getText().toString()));
                        medicineModel.setCategory(category);
                        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                                .document(medicineModel.getMedicine_id())
                                .set(medicineModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toasty.info(getActivity(),
                                                "Updated Successfully",Toast.LENGTH_LONG)
                                                .show();
                                        dismiss();
                                    }
                                });


                    }else{
                        medicineModel = new MedicineModel();
                        medicineModel.setMedecine_name(et_medicineName.getText().toString().trim());
                        medicineModel.setMedecine_price(et_medicinePrice.getText().toString().trim());
                        medicineModel.setPharmacy_id(pharmacy_id);
                        medicineModel.setDescription((et_description.getText().toString()));
                        medicineModel.setMedicine_quantity(Integer.parseInt(et_quantity.getText().toString()));
                        medicineModel.setCategory(category);
                        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                                .add(medicineModel)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "onComplete: " + task.getResult().getId());
                                            medicineModel.setMedicine_id(task.getResult().getId());
                                            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                                                    .document(medicineModel.getMedicine_id())
                                                    .set(medicineModel)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toasty.info(getActivity(),
                                                                    "Added Successfully",Toast.LENGTH_LONG)
                                                                    .show();
                                                            dismiss();
                                                        }
                                                    });

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dismiss();
                                Log.d("Exception", "onFailure: " + e);
                            }
                        });

                    }


                }


            }
        });


        return builder.create();

    }

    public AddMedicineDialog(MedicineModel medicineModel, String pharmacy_id) {
        this.medicineModel = medicineModel;
        this.pharmacy_id = pharmacy_id;
    }
    public AddMedicineDialog() {

    }
}
