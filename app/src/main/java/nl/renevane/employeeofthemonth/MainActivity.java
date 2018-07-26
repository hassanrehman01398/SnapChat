package nl.renevane.employeeofthemonth;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CameraFragmentListener, EditFragmentListener {

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
        showFragment(selectedFragment);

        return true; // returning 'true' will highlight the clicked navigation item
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set a reference to the bottom navigation view
        // and pass the nav listener to it
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // on first run, there is not yet a fragment selected so no fragment would be shown
        // to prevent this, show the camera fragment
        // savedInstanceState
        if (savedInstanceState == null) {
            showFragment(cameraFragment);
        }
    }

    @Override
    public void onCameraPhotoSaved(String path) {
        editFragment.updatePath(path);
    }

    @Override
    public void onEditedPictureSaved(String path) {
        shareFragment.updatePath(path);
    }

    private void showFragment(Fragment selectedFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();
    }

}
