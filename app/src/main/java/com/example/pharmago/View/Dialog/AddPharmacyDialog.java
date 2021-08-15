package com.example.pharmago.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.example.pharmago.View.AddPharmacyActivity;
import com.example.pharmago.View.HomeActivity;
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

public class AddPharmacyDialog extends AppCompatDialogFragment {
    Button btn_cancel, btn_add;
    EditText et_pharmacyName,et_pharmacyAddress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PharmacyModel pharmacyModel;

    private static final String TAG = "AddMedicineDialog";
    String pharmacy_id;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_pharmacy_to_list_dialog,null);
        btn_add = view.findViewById(R.id.btn_add);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        et_pharmacyName = view.findViewById(R.id.et_pharmacyName);
        et_pharmacyAddress = view.findViewById(R.id.et_pharmacyAddress);

        FirebaseAuth mFbAuth = FirebaseAuth.getInstance();
        builder.setView(view);


        if(pharmacyModel !=null){

            btn_add.setText("Update");
            db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(pharmacyModel.getPharmacy_id())){

                                        PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                        et_pharmacyName.setText(pharmacyModel.getPharmacy_name());
                                        et_pharmacyAddress.setText(pharmacyModel.getPharmacy_address());

                                    }
                                }
                            }


                        }
                    });
        }else
        {
            btn_add.setText("Add");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_pharmacyName.getText().toString().trim().isEmpty()){
                    et_pharmacyName.setError("This Field is required");
                }else if (et_pharmacyAddress.getText().toString().trim().isEmpty()){
                    et_pharmacyAddress.setError("This Field is required");

                }else{
                    if(pharmacyModel != null){
                        PharmacyModel phar = new PharmacyModel();
                        phar.setPharmacy_name(et_pharmacyName.getText().toString().trim());
                        phar.setPharmacy_address(et_pharmacyAddress.getText().toString().trim());
                        phar.setPharmacy_id(pharmacyModel.getPharmacy_id());
                        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                .document(phar.getPharmacy_id())
                                .set(phar)
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
                        PharmacyModel pharmacyModel = new PharmacyModel();
                        pharmacyModel.setPharmacy_name(et_pharmacyName.getText().toString().trim());
                        pharmacyModel.setPharmacy_address(et_pharmacyAddress.getText().toString().trim());

                        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                .add(pharmacyModel)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toasty.info(getActivity(),
                                                    "Added Successfully",Toast.LENGTH_LONG)
                                                    .show();
                                            dismiss();


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

    public AddPharmacyDialog(PharmacyModel pharmacyModel) {
    this.pharmacyModel = pharmacyModel;
    }
    public AddPharmacyDialog() {

    }
}
