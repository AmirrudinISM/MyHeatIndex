package unikl.finalyearproject.myheatindex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import unikl.finalyearproject.myheatindex.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    MaterialToolbar appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomAppBar = findViewById(R.id.bottom_nav);
        appBar = findViewById(R.id.topbar);
        bottomAppBar.getMenu().findItem(R.id.nav_weather).setChecked(true);
        appBar.setTitle("My Heat Index");
        replaceFragment(new WeatherFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_hi_tool){
                appBar.setTitle("Heat Index Tool");
                replaceFragment(new HIToolFragment());
            } else if (item.getItemId() == R.id.nav_weather) {
                appBar.setTitle("Weather");
                replaceFragment(new WeatherFragment());
            } else if (item.getItemId() == R.id.nav_hi_recommendations) {
                appBar.setTitle("Recommendations");
                replaceFragment(new RecommendationFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}