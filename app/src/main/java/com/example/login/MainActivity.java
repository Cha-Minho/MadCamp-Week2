package com.example.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.login.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        if(navView == null){
            Toast.makeText(this, "navView is null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "navView is not null", Toast.LENGTH_SHORT).show();
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.frag1, R.id.frag2, R.id.frag3)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController); // Use navView instead of binding.navView

        // Adding a listener to the NavController
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.frag1) {
                    toolbar.setTitle("게시판");
                } else if (destination.getId() == R.id.frag2) {
                    toolbar.setTitle("바른 자세");
                } else if (destination.getId() == R.id.frag3) {
                    toolbar.setTitle("5분 스트레칭");
                }
            }
        });


        ImageView fab = findViewById(R.id.fab_frag2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navView.setSelectedItemId(R.id.frag2);

                // If you want to replace a fragment
                // Frag2 frag2 = new Frag2();
                // getSupportFragmentManager().beginTransaction()
                //       .replace(R.id.nav_host_fragment, frag2)
                //       .commit();
            }
        });

    }
}
