package com.example.pharmago.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmago.MainActivity;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.R;
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

public class AddMedicineActivity extends AppCompatActivity {
    private static final String TAG = "AddMedicineActivity";
    Button btn_cancel, btn_add;
    EditText et_medicineName,et_medicinePrice,et_quantity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    Intent intent;
    String id = "";
    String pharmacy_id = "";
    Toolbar toolbar;
    TextView txtUserToolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        intent = getIntent();
        id = intent.getStringExtra("medicine_id");
        pharmacy_id = intent.getStringExtra("pharmacy_id");



        btn_add = findViewById(R.id.btn_add);
        btn_cancel = findViewById(R.id.btn_cancel);
        et_medicineName = findViewById(R.id.et_medicineName);
        et_medicinePrice = findViewById(R.id.et_medicinePrice);
        et_quantity = findViewById(R.id.et_quantity);

        toolbar  = findViewById(R.id.toolBar);
        txtUserToolbar  = findViewById(R.id.txtUserToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        sharedpreferences = getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);

        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        if(!id.equals("")){

            btn_add.setText("Update");
            progressDialog.show();
            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(id)){

                                        MedicineModel med = document.toObject(MedicineModel.class);
                                        et_medicineName.setText(med.getMedecine_name());
                                        et_medicinePrice.setText(med.getMedecine_price());
                                        et_quantity.setText(med.getMedicine_quantity());

                                    }
                                }
                            }
                            progressDialog.dismiss();

                        }
                    });
        }else
        {
            btn_add.setText("Add");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddMedicineActivity.this, HomeActivity.class));
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_medicineName.getText().toString().trim().isEmpty()){
                    et_medicineName.setError("This Field is required");
                }else if (et_medicinePrice.getText().toString().trim().isEmpty()){
                    et_medicinePrice.setError("This Field is required");

                }else{
                    if(!id.equals("")){
                        MedicineModel medicineModel = new MedicineModel();
                        medicineModel.setMedecine_name(et_medicineName.getText().toString().trim());
                        medicineModel.setMedecine_price(et_medicinePrice.getText().toString().trim());
                        medicineModel.setMedicine_id(id);
                        medicineModel.setPharmacy_id(pharmacy_id);
                        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                                .document(id)
                                .set(medicineModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent i = new Intent(AddMedicineActivity.this, MedicineListActivity.class);
                                        i.putExtra("pharmacy_id", pharmacy_id);
                                        startActivity(i);
                                    }
                                });


                    }else{
                        MedicineModel medicineModel = new MedicineModel();
                        medicineModel.setMedecine_name(et_medicineName.getText().toString().trim());
                        medicineModel.setMedecine_price(et_medicinePrice.getText().toString().trim());
                        medicineModel.setPharmacy_id(pharmacy_id);
                        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                                .add(medicineModel)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "onComplete: " + task.getResult().getId());
                                            progressDialog.dismiss();

                                            Intent i = new Intent(AddMedicineActivity.this, MedicineListActivity.class);
                                            i.putExtra("pharmacy_id", pharmacy_id);
                                            startActivity(i);

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.d("Exception", "onFailure: " + e);
                            }
                        });

                    }


                }


            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Log.d("Logout", "logout: " + editor);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toasty.info(getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }
    }
}