package com.example.pharmago.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmago.Adapter.MedicineListAdapter;
import com.example.pharmago.MainActivity;
import com.example.pharmago.Model.MedicineModel;
import com.example.pharmago.Model.PharmacyModel;
import com.example.pharmago.R;
import com.example.pharmago.View.AddMedicineActivity;
import com.example.pharmago.View.Dialog.AddMedicineDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class MedicineListFragment extends Fragment {
    public MedicineListFragment() {
        // Required empty public constructor
    }

    private ArrayList<MedicineModel> mMedecineModel;

    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_medecineList;
    Button btn_addMedecine;
    ProgressDialog progressDialog;
    TextView tv_medicineName;
    Intent intent;
    String id = "";
    Toolbar toolbar;
    TextView txtUserToolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    ImageView iv_empty;
    TextView tv_empty,tv_medicine_lb,tv_vit_lb,tv_sup_lb;
    ConstraintLayout parent_layout;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;

    CardView cv_side,cv_medicine,cv_vitamins,cv_supplements;

    private static final String TAG = "MedicineListFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medicine_list, container, false);
        setHasOptionsMenu(true);

        btn_addMedecine = view.findViewById(R.id.btn_addMedecine);
        rv_medecineList = view.findViewById(R.id.rv_medecineList);
        tv_medicineName = view.findViewById(R.id.tv_medicineName);
        cv_side = view.findViewById(R.id.cv_side);
        cv_medicine = view.findViewById(R.id.cv_medicine);
        cv_vitamins = view.findViewById(R.id.cv_vitamins);
        cv_supplements = view.findViewById(R.id.cv_supplements);

        tv_medicine_lb = view.findViewById(R.id.tv_medicine_lb);
        tv_sup_lb = view.findViewById(R.id.tv_sup_lb);
        tv_vit_lb = view.findViewById(R.id.tv_vit_lb);

        rv_medecineList.setHasFixedSize(true);
        rv_medecineList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        toolbar  = view.findViewById(R.id.toolBar);
        txtUserToolbar  = view.findViewById(R.id.txtUserToolbar);

        tv_empty = view.findViewById(R.id.tv_empty);
        iv_empty = view.findViewById(R.id.iv_empty);
        parent_layout = view.findViewById(R.id.parent_layout);

        sharedpreferences = getActivity().getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        mMedecineModel = new ArrayList<>();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Pharma Go");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        progressDialog.show();
        db.collection(getString(R.string.COLLECTION_PHARMACYLIST))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PharmacyModel pharmacyModel = document.toObject(PharmacyModel.class);
                                if(pharmacyModel.getUser_id().equals(firebaseUser.getUid())){
                                    tv_medicineName.setText(pharmacyModel.getPharmacy_name());
                                }
                            }
                        }
                        progressDialog.dismiss();

                    }
                });


        db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mMedecineModel.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            MedicineModel medicineModel = document.toObject(MedicineModel.class);
                            if(firebaseUser.getUid().equals(medicineModel.getPharmacy_id())){
                                mMedecineModel.add(medicineModel);
                                Log.d(TAG, "onComplete: " + medicineModel.getMedecine_name());
                            }

                        }

                        if(mMedecineModel.size() == 0){
                            iv_empty.setVisibility(View.VISIBLE);
                            tv_empty.setVisibility(View.VISIBLE);
                            cv_side.setVisibility(View.GONE);

                            cv_medicine.setVisibility(View.GONE);
                            cv_vitamins.setVisibility(View.GONE);
                            cv_supplements.setVisibility(View.GONE);

                            tv_medicine_lb.setVisibility(View.GONE);
                            tv_sup_lb.setVisibility(View.GONE);
                            tv_vit_lb.setVisibility(View.GONE);


                            rv_medecineList.setVisibility(View.GONE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }else {
                            iv_empty.setVisibility(View.GONE);
                            tv_empty.setVisibility(View.GONE);
                            cv_side.setVisibility(View.VISIBLE);

                            cv_medicine.setVisibility(View.VISIBLE);
                            cv_supplements.setVisibility(View.VISIBLE);
                            cv_vitamins.setVisibility(View.VISIBLE);

                            tv_medicine_lb.setVisibility(View.VISIBLE);
                            tv_sup_lb.setVisibility(View.VISIBLE);
                            tv_vit_lb.setVisibility(View.VISIBLE);

                            rv_medecineList.setVisibility(View.VISIBLE);
                            parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                        }



                        //Vitamins
                        //Medicine
                        //Supplements

                        if (mMedecineModel != null){
                            ArrayList<MedicineModel> lists = new ArrayList<>();


                            for (int i = 0; i<mMedecineModel.size(); i++){
                                Log.d(TAG, "mMedecineModel: " + mMedecineModel.get(i).getCategory());
                                if (mMedecineModel.get(i).getCategory().equals("Medicine")){
                                    lists.add(mMedecineModel.get(i));

                                }
                            }

                            MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                            rv_medecineList.setAdapter(medicineListAdapter);
                            cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                            cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                            cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));



                            cv_medicine.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MedicineModel> lists = new ArrayList<>();
                                    for (MedicineModel med : mMedecineModel) {


                                        if (med.getCategory().equals("Medicine")){
                                            lists.add(med);

                                        }


                                    }
                                    MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                    rv_medecineList.setAdapter(medicineListAdapter);
                                    cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                                    cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                    cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                }
                            });

                            cv_vitamins.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MedicineModel> lists = new ArrayList<>();
                                    for (MedicineModel med : mMedecineModel) {


                                        if (med.getCategory().equals("Vitamins")){
                                            lists.add(med);

                                        }


                                    }
                                    MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                    rv_medecineList.setAdapter(medicineListAdapter);
                                    cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                    cv_vitamins.setBackgroundColor(Color.parseColor("#47C6E9"));
                                    cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                }
                            });

                            cv_supplements.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<MedicineModel> lists = new ArrayList<>();
                                    for (MedicineModel med : mMedecineModel) {


                                        if (med.getCategory().equals("Supplements")){
                                            lists.add(med);

                                        }


                                    }
                                    MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                    rv_medecineList.setAdapter(medicineListAdapter);
                                    cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                    cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                    cv_supplements.setBackgroundColor(Color.parseColor("#47C6E9"));

                                }
                            });
                        }


                    }
                });

        btn_addMedecine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddMedicineDialog dialog = new AddMedicineDialog(null,firebaseUser.getUid());
                dialog.show(getActivity().getSupportFragmentManager(), "PharmaGo");

            }
        });



        return  view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {




        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                searchData(s);
                return false;
            }
        });


    }

    private void searchData(String s) {
        if(s.equals("")){
            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            mMedecineModel.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                            {
                                MedicineModel medicineModel = document.toObject(MedicineModel.class);
                                if(firebaseUser.getUid().equals(medicineModel.getPharmacy_id())){
                                    mMedecineModel.add(medicineModel);
                                    Log.d(TAG, "onComplete: " + medicineModel.getMedecine_name());
                                }

                            }

                            if(mMedecineModel.size() == 0){
                                iv_empty.setVisibility(View.VISIBLE);
                                tv_empty.setVisibility(View.VISIBLE);
                                cv_side.setVisibility(View.GONE);

                                cv_medicine.setVisibility(View.GONE);
                                cv_vitamins.setVisibility(View.GONE);
                                cv_supplements.setVisibility(View.GONE);

                                tv_medicine_lb.setVisibility(View.GONE);
                                tv_sup_lb.setVisibility(View.GONE);
                                tv_vit_lb.setVisibility(View.GONE);


                                rv_medecineList.setVisibility(View.GONE);
                                parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                            }else {
                                iv_empty.setVisibility(View.GONE);
                                tv_empty.setVisibility(View.GONE);
                                cv_side.setVisibility(View.VISIBLE);

                                cv_medicine.setVisibility(View.VISIBLE);
                                cv_supplements.setVisibility(View.VISIBLE);
                                cv_vitamins.setVisibility(View.VISIBLE);

                                tv_medicine_lb.setVisibility(View.VISIBLE);
                                tv_sup_lb.setVisibility(View.VISIBLE);
                                tv_vit_lb.setVisibility(View.VISIBLE);

                                rv_medecineList.setVisibility(View.VISIBLE);
                                parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                            }



                            //Vitamins
                            //Medicine
                            //Supplements

                            if (mMedecineModel != null){
                                ArrayList<MedicineModel> lists = new ArrayList<>();

                                for (int i = 0; i<mMedecineModel.size(); i++){
                                    Log.d(TAG, "mMedecineModel: " + mMedecineModel.get(i).getCategory());
                                    if (mMedecineModel.get(i).getCategory().equals("Medicine")){
                                        lists.add(mMedecineModel.get(i));

                                    }
                                }


                                MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                rv_medecineList.setAdapter(medicineListAdapter);
                                cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                                cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));



                                cv_medicine.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Medicine")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                    }
                                });

                                cv_vitamins.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Vitamins")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                    }
                                });

                                cv_supplements.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Supplements")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#47C6E9"));

                                    }
                                });
                            }


                        }
                    });
        }else{
            db.collection(getString(R.string.COLLECTION_MEDICINELIST))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            mMedecineModel.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                            {
                                MedicineModel medicineModel = document.toObject(MedicineModel.class);
                                if(firebaseUser.getUid().equals(medicineModel.getPharmacy_id())){
                                    if (medicineModel.getMedecine_name().toLowerCase().contains(s.toLowerCase())){
                                        mMedecineModel.add(medicineModel);
                                        Log.d(TAG, "onComplete: " + medicineModel.getMedecine_name());
                                    }

                                }

                            }

                            if(mMedecineModel.size() == 0){
                                iv_empty.setVisibility(View.VISIBLE);
                                tv_empty.setVisibility(View.VISIBLE);
                                cv_side.setVisibility(View.GONE);

                                cv_medicine.setVisibility(View.GONE);
                                cv_vitamins.setVisibility(View.GONE);
                                cv_supplements.setVisibility(View.GONE);

                                tv_medicine_lb.setVisibility(View.GONE);
                                tv_sup_lb.setVisibility(View.GONE);
                                tv_vit_lb.setVisibility(View.GONE);


                                rv_medecineList.setVisibility(View.GONE);
                                parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                            }else {
                                iv_empty.setVisibility(View.GONE);
                                tv_empty.setVisibility(View.GONE);
                                cv_side.setVisibility(View.VISIBLE);

                                cv_medicine.setVisibility(View.VISIBLE);
                                cv_supplements.setVisibility(View.VISIBLE);
                                cv_vitamins.setVisibility(View.VISIBLE);

                                tv_medicine_lb.setVisibility(View.VISIBLE);
                                tv_sup_lb.setVisibility(View.VISIBLE);
                                tv_vit_lb.setVisibility(View.VISIBLE);

                                rv_medecineList.setVisibility(View.VISIBLE);
                                parent_layout.setBackgroundColor(Color.parseColor("#255265"));

                            }



                            //Vitamins
                            //Medicine
                            //Supplements

                            if (mMedecineModel != null){
                                ArrayList<MedicineModel> lists = new ArrayList<>();

                                for (int i = 0; i<mMedecineModel.size(); i++){
                                    Log.d(TAG, "mMedecineModel: " + mMedecineModel.get(i).getCategory());
//                                    if (mMedecineModel.get(i).getCategory().equals("Medicine")){
//                                        lists.add(mMedecineModel.get(i));
//
//                                    }
                                    lists.add(mMedecineModel.get(i));
                                }

                                if (lists.size() > 0){
                                    if(lists.get(0).getCategory().equals("Medicine")){
                                        cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                    }else if(lists.get(0).getCategory().equals("Vitamins")){
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                    }else{
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                    }
                                }




//                                cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
//                                cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
//                                cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));



                                cv_medicine.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Medicine")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                    }
                                });

                                cv_vitamins.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Vitamins")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#47C6E9"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#ffffff"));

                                    }
                                });

                                cv_supplements.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<MedicineModel> lists = new ArrayList<>();
                                        for (MedicineModel med : mMedecineModel) {


                                            if (med.getCategory().equals("Supplements")){
                                                lists.add(med);

                                            }


                                        }
                                        MedicineListAdapter medicineListAdapter = new MedicineListAdapter(getActivity(), lists);
                                        rv_medecineList.setAdapter(medicineListAdapter);
                                        cv_medicine.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_vitamins.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cv_supplements.setBackgroundColor(Color.parseColor("#47C6E9"));

                                    }
                                });
                            }


                        }
                    });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            Log.d("Logout", "logout: " + editor);
            startActivity(new Intent(getActivity(), MainActivity.class));
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toasty.info(getActivity(), "Logging Out", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}