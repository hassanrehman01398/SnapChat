package nl.renevane.employeeofthemonth;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // create fragment instances
    private final CameraFragment cameraFragment = new CameraFragment();
    private final EditFragment editFragment = new EditFragment();
    private final ShareFragment shareFragment = new ShareFragment();

    // react to clicks on the nav buttons
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener
            = item -> {

                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_camera:
                        selectedFragment = cameraFragment;
                        break;
                    case R.id.nav_edit:
                        selectedFragment = editFragment;
                        break;
                    case R.id.nav_share:
                        selectedFragment = shareFragment;
                        break;
                }

                // show the selected fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true; // returning 'true' visually highlights the clicked navigation item
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set a reference to the bottom navigation view
        // and pass the nav listener to it
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // on first run, show the Photo fragment
        if (null == savedInstanceState) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cameraFragment)
                    .commit();
        }
    }

}
