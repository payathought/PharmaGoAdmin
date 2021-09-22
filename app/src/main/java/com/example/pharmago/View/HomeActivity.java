package com.example.pharmago.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmago.FunctionMethod.FunctionMethod;
import com.example.pharmago.MainActivity;
import com.example.pharmago.R;
import com.example.pharmago.View.Dialog.ViewUserInfoInToolBarDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    Toolbar toolbar;
    TextView txtUserToolbar;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    FunctionMethod functionMethod = new FunctionMethod();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottomNav);

        toolbar  = findViewById(R.id.toolBar);
        txtUserToolbar  = findViewById(R.id.txtUserToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        sharedpreferences = getSharedPreferences(getString(R.string.USERPREF), Context.MODE_PRIVATE);

        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            txtUserToolbar.setText(sharedpreferences.getAll().get(getString(R.string.USERNAME)).toString());
        }
        txtUserToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserInfoInToolBarDialog dialog = new ViewUserInfoInToolBarDialog(firebaseUser.getUid());
                dialog.show(getSupportFragmentManager(), "PharmaGo");
            }
        });
        bottomNav.setOnNavigationItemSelectedListener(navlistener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragmentContainer, new PharmacyListFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_medecine:
                            selectedFragment = new PharmacyListFragment();
                            break;
                        case R.id.nav_order:
                            selectedFragment = new OrderListFragment();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fl_fragmentContainer, selectedFragment).commit();
                    return true;
                }

    };

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

    public void onBackPressed() {
        super.onBackPressed();

        if (sharedpreferences.getAll().isEmpty())
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else
        {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

        }
        moveTaskToBack(true);

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
        functionMethod.sendSMSPermission(getApplicationContext());
    }
}