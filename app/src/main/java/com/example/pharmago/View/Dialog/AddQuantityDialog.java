package com.example.pharmago.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.example.pharmago.View.MedicineListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class AddQuantityDialog extends AppCompatDialogFragment {
    Button btn_okay;
    TextView tv_note;
    String medicineName,pharmacyId;
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_quantity_dialog,null);

        btn_okay = view.findViewById(R.id.btn_okay);
        tv_note = view.findViewById(R.id.tv_note);

        builder.setView(view);

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pharmacyId.isEmpty()){
                    Intent i = new Intent(getContext(), MedicineListActivity.class);
                    i.putExtra("pharmacy_id", pharmacyId);
                    startActivity(i);
                }
            }
        });
        tv_note.setText("The Medicine " + medicineName + "'s quantity is less than the quantity of the order; before accepting the order replenish first the stock or cancel the order and send the reason why to the customer.");
        return builder.create();

    }

    public AddQuantityDialog() {
    }
    public AddQuantityDialog(String medicineName, String pharmacyId) {
        this.pharmacyId = pharmacyId;
        this.medicineName = medicineName;
    }
}
