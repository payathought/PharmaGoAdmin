package com.example.pharmago.View.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.example.pharmago.Model.SignUpModel;
import com.example.pharmago.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewUserInfoInToolBarDialog extends AppCompatDialogFragment {
    FirebaseFirestore db;
    TextView tv_name,tv_phonenumber,tv_email,tv_username;
    Button btn_ok;
    String user_id = "";
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;

    private static final String TAG = "ViewUserInfoDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_view_user_info_dialog,null);
        tv_name = view.findViewById(R.id.tv_name);

        tv_phonenumber = view.findViewById(R.id.tv_phonenumber);
        tv_email = view.findViewById(R.id.tv_email);
        tv_username = view.findViewById(R.id.tv_username);
        btn_ok = view.findViewById(R.id.btn_ok);
        db = FirebaseFirestore.getInstance();


        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        builder.setView(view);
        CollectionReference userinfo = db.collection(getString(R.string.COLLECTION_ADMIN_INFORMATION));
        Query userInfoQuery = userinfo.whereEqualTo("user_id", user_id);
        userInfoQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {

                    SignUpModel signUpModel = querySnapshot.getDocuments().get(0).toObject(SignUpModel.class);

                    tv_name.setText(signUpModel.getFirstname() + " " + signUpModel.getLastname());
                    tv_phonenumber.setText(signUpModel.getPhonenumber());
                    tv_username.setText(signUpModel.getUsername());
                    tv_email.setText(firebaseUser.getEmail());



                }
            }
        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });





        return builder.create();

    }

    public ViewUserInfoInToolBarDialog(String user_id) {
        this.user_id = user_id;
    }
}
