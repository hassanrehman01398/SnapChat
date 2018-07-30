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

    // send path of saved photo from photo fragment to edit fragment
    @Override
    public void onCameraPhotoSaved(String path) {
        editFragment.addToImageList(path);
    }

    // send path of saved edited image from edit fragment to share fragment
    @Override
    public void onEditedImageSaved(String path) {
        shareFragment.addToImageList(path);
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

        // highlight the clicked navigation item
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set a reference to the bottom navigation view and pass the nav listener to it
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // highlight the edit button
        bottomNav.setSelectedItemId(R.id.nav_edit);

        // show the edit fragment
        showFragment(editFragment);

        // when the app is first started
        if (savedInstanceState == null) {

            String storageFolder = getExternalFilesDir(null).toString();
            String photoFilterPattern = getString(R.string.photo_filter_pattern);
            String imageFilterPattern = getString(R.string.image_filter_pattern);

            // load saved images (if any)
            editFragment.createImageList(storageFolder, photoFilterPattern);
            shareFragment.createImageList(storageFolder, imageFilterPattern);

        }
    }

}
