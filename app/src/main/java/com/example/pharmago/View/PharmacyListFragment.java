package com.example.pharmago.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmago.Adapter.MedicineListAdapter;
import com.example.pharmago.Adapter.PharmacyListAdapter;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.example.pharmago.View.Dialog.AddMedicineDialog;
import com.example.pharmago.View.Dialog.AddPharmacyDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PharmacyListFragment extends Fragment {
    View view;
    private ArrayList<PharmacyModel> mPharmacyModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_pharmacyList;
    Button btn_addPharmacy;
    ProgressDialog progressDialog;
    ImageView iv_empty;
    TextView tv_empty,textView;
    ConstraintLayout parent_layout;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;

    private static final String TAG = "PharmacyListFragment";

    public PharmacyListFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pharmacy_list, container, false);

        btn_addPharmacy = view.findViewById(R.id.btn_addPharmacy);
        rv_pharmacyList = view.findViewById(R.id.rv_pharmacyList);
        tv_empty = view.findViewById(R.id.tv_empty);
        iv_empty = view.findViewById(R.id.iv_empty);
        parent_layout = view.findViewById(R.id.parent_layout);
        textView = view.findViewById(R.id.textView);
        rv_pharmacyList.setHasFixedSize(true);
        rv_pharmacyList.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        mPharmacyModel = new ArrayList<>();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mPharmacyModel.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {

                            PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                            Log.d(TAG, "onComplete: " + pharmacyModel.getUser_id());
                            Log.d(TAG, "onComplete: " + firebaseUser.getUid());
                            if(pharmacyModel.getUser_id().equals(firebaseUser.getUid())){
                                pharmacyModel.setPharmacy_id(document.getId());
                                mPharmacyModel.add(pharmacyModel);
                            }



                        }
                        if(mPharmacyModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.VISIBLE);
                            rv_pharmacyList.setVisibility(View.GONE);
                            btn_addPharmacy.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.VISIBLE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.GONE);
                            rv_pharmacyList.setVisibility(View.VISIBLE);
                            btn_addPharmacy.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                        PharmacyListAdapter pharmacyListAdapter = new PharmacyListAdapter(getContext(), mPharmacyModel);
                        rv_pharmacyList.setAdapter(pharmacyListAdapter);
                    }
                });

        btn_addPharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getContext(), AddPharmacyActivity.class);
//                i.putExtra("pharmacy_id", "");
//                startActivity(i);
                AddPharmacyDialog dialog = new AddPharmacyDialog(null);
                dialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "PharmaGo");

            }
        });
        return view;
    }
}