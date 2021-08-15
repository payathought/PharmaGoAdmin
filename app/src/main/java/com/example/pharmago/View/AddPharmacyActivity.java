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
import com.example.pharmago.Model.PharmacyModel;
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

public class AddPharmacyActivity extends AppCompatActivity {
    Button btn_cancel, btn_add;
    EditText et_pharmacyName,et_pharmacyAddress;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    Intent intent;
    String id = "";
    Toolbar toolbar;
    TextView txtUserToolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pharmacy);

        intent = getIntent();
        id = intent.getStringExtra("pharmacy_id");

        btn_add = findViewById(R.id.btn_add);
        btn_cancel = findViewById(R.id.btn_cancel);
        et_pharmacyName = findViewById(R.id.et_pharmacyName);
        et_pharmacyAddress = findViewById(R.id.et_pharmacyAddress);

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
            db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getId().equals(id)){

                                        PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                        et_pharmacyName.setText(pharmacyModel.getPharmacy_name());
                                        et_pharmacyAddress.setText(pharmacyModel.getPharmacy_address());

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
                startActivity(new Intent(AddPharmacyActivity.this, HomeActivity.class));
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
                    if(!id.equals("")){
                        PharmacyModel pharmacyModel = new PharmacyModel();
                        pharmacyModel.setPharmacy_name(et_pharmacyName.getText().toString().trim());
                        pharmacyModel.setPharmacy_address(et_pharmacyAddress.getText().toString().trim());
                        pharmacyModel.setPharmacy_id(id);
                        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                                .document(id)
                                .set(pharmacyModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(AddPharmacyActivity.this,HomeActivity.class));
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
                                            Log.d("TAG", "onComplete: " + task.getResult().getId());
                                            progressDialog.dismiss();
                                            startActivity(new Intent(AddPharmacyActivity.this, HomeActivity.class));

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
