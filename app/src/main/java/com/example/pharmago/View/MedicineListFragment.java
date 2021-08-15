package com.example.pharmago.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pharmago.Adapter.MedicineListAdapter;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.R;
import com.example.pharmago.View.AddMedicineActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MedicineListFragment extends Fragment {
    private View view;
    private ArrayList<MedicineModel> mMedecineModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_medecineList;
    Button btn_addMedecine;
    ProgressDialog progressDialog;
    private static final String TAG = "MedicineListFragment";
    public MedicineListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_medecine_list, container, false);
        btn_addMedecine = view.findViewById(R.id.btn_addMedecine);
        rv_medecineList = view.findViewById(R.id.rv_medecineList);

        rv_medecineList.setHasFixedSize(true);
        rv_medecineList.setLayoutManager(new LinearLayoutManager(getContext()));


        user = FirebaseAuth.getInstance().getCurrentUser();

        mMedecineModel = new ArrayList<>();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);


        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mMedecineModel.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            MedicineModel medicineModel = new MedicineModel();
                            medicineModel.setMedicine_id(document.getId());
                            medicineModel.setMedecine_name(document.get("medecine_name").toString());
                            medicineModel.setMedecine_price(document.get("medecine_price").toString());
                            mMedecineModel.add(medicineModel);
                            Log.d(TAG, "onComplete: " + medicineModel.getMedecine_name());

                        }
                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getContext(), mMedecineModel);
                        rv_medecineList.setAdapter(medicineListAdapter);
                    }
                });

        btn_addMedecine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddPharmacyActivity.class);
                i.putExtra("medicine_id", "");
                startActivity(i);

            }
        });


        return view;
    }



}