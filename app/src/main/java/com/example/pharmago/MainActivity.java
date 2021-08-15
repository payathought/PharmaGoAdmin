package com.example.pharmago;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmago.FunctionMethod.FunctionMethod;
import com.example.pharmago.Model.UserModel;
import com.example.pharmago.View.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    UserModel user = new UserModel();
    Button btn_login,btn_signup;
    EditText et_password, et_emailAddress;
    ProgressDialog progressDialog;
    TextView tv_forgoPassword,tv_incorrect;
    FunctionMethod functionMethod = new FunctionMethod();
    String username;
    String password;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    private static final String TAG = "MainActivity";

    TextToSpeech t1;


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    t1.setLanguage(Locale.US);
//                }
//            }
//        });

        sharedpreferences = getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);
        if (!sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }


        btn_login = findViewById(R.id.btn_login);
        btn_signup  = findViewById(R.id.btn_signup);
        et_password  = findViewById(R.id.et_password);
        et_emailAddress  = findViewById(R.id.et_emailAddress);
        tv_forgoPassword  = findViewById(R.id.tv_forgoPassword);
        tv_incorrect  = findViewById(R.id.tv_incorrect);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();




        btn_login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onClick(View v)
            {

                if (functionMethod.haveNetworkConnected(MainActivity.this))
                {
                    progressDialog.show();

                    if (et_emailAddress.getText().toString().trim().isEmpty()) {
                        et_emailAddress.setError("Required Field");
                        progressDialog.dismiss();
                    }else if (et_password.getText().toString().trim().isEmpty()) {
                        et_password.setError("Required Field");
                        progressDialog.dismiss();

                    } else {
                        username = et_emailAddress.getText().toString().trim();
                        password = et_password.getText().toString().trim();
                        mFirebaseAuth = FirebaseAuth.getInstance();

                        mFirebaseAuth.signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            firebaseUser = mFirebaseAuth.getCurrentUser();

                                            if (firebaseUser.isEmailVerified()) {
                                                CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_ADMIN_INFORMATION));
                                                Query userInfoQuery = userinfo.whereEqualTo("user_id", firebaseUser.getUid());
                                                userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        if (!querySnapshot.isEmpty()) {

                                                            user.setFirst_name(querySnapshot.getDocuments().get(0).getData().get("firstname").toString());
                                                            user.setLast_name(String.valueOf(querySnapshot.getDocuments().get(0).getData().get("lastname")));
                                                            user.setUser_name(String.valueOf(querySnapshot.getDocuments().get(0).getData().get("username")));
                                                            user.setPhone_number(String.valueOf(querySnapshot.getDocuments().get(0).getData().get("phonenumber")));
                                                            user.setUser_id(String.valueOf(querySnapshot.getDocuments().get(0).getData().get("user_id")));
                                                            editor = sharedpreferences.edit();
                                                            editor.putString(getString(R.string.FIRSTNAME), user.getFirst_name());
                                                            editor.putString(getString(R.string.LASTNAME), user.getLast_name());
                                                            editor.putString(getString(R.string.USERNAME), user.getUser_name());
                                                            editor.putString(getString(R.string.PHONENUMBER), user.getPhone_number());
                                                            editor.putString(getString(R.string.USER_ID), user.getUser_id());
                                                            editor.apply();
                                                            Log.d(TAG, "onSuccess: " + firebaseUser.getUid());
                                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                            Toasty.success(getApplicationContext(), "Successfully login", Toast.LENGTH_LONG).show();
                                                            progressDialog.dismiss();

                                                        } else {
                                                            progressDialog.dismiss();
                                                            tv_incorrect.setVisibility(View.VISIBLE);
//                                                            Toasty.error(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                            } else {

                                                FirebaseAuth.getInstance().signOut();
                                                Toasty.error(MainActivity.this, "Please, Verify Your Email First", Toast.LENGTH_LONG).show();
//                                                t1.speak("Please, Verify Your Email First", TextToSpeech.QUEUE_FLUSH, null);
                                                progressDialog.dismiss();


                                            }
                                        } else {
                                            progressDialog.dismiss();
                                            tv_incorrect.setVisibility(View.VISIBLE);
//                                            Toasty.error(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }

                }else{
//                    t1.speak("Please, Check Internet Connection", TextToSpeech.QUEUE_FLUSH, null);
                    Toasty.error(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
//
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        tv_forgoPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordDialog dialog = new ForgotPasswordDialog();
                dialog.show(getSupportFragmentManager(), "PharmaGo");

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}