package view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;

import view.fragment.HomeFragment;
import view.fragment.ProfileFragment;
import com.example.soundlab.R;
import view.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        head = findViewById(R.id.head);

        replaceFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
                head.setText(getString(R.string.home));
            }
            else if (item.getItemId() == R.id.search) {
                replaceFragment(new SearchFragment());
                head.setText(getString(R.string.search));
            }
            else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
                head.setText(getString(R.string.profile));
            }

            return true;
        });
    }

    /**
     * Rimpiazza il fragment attuale con quello passato per parametro
     * @param fragment Fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_fragments, fragment);
        fragmentTransaction.commit();
    }
}