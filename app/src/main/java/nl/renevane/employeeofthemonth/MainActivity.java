package nl.renevane.employeeofthemonth;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements CameraFragmentListener, EditFragmentListener {

    // create fragment instances
    private final CameraFragment cameraFragment = new CameraFragment();
    private final EditFragment editFragment = new EditFragment();
    private final ShareFragment shareFragment = new ShareFragment();

    // when a new photo is saved from the camera fragment, send its path to the edit fragment
    @Override
    public void onCameraPhotoSaved(String path) {
        editFragment.addToCameraRoll(path);
    }

    // when a picture is saved from the edit fragment, send its path to the share fragment
    @Override
    public void onEditedPictureSaved(String path) {
        shareFragment.addToEditedPicturesList(path);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

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

        // set a reference to the bottom navigation view and pass the nav listener to it
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // highlight the camera button (not strictly necessary, but more elegant)
        bottomNav.setSelectedItemId(R.id.nav_camera);

        // show the camera fragment when the app is first started
        if (savedInstanceState == null) {
            showFragment(cameraFragment);
            // TODO: nullpointer error. make it work!
            // editFragment.loadCameraRoll();
        }
    }
}
