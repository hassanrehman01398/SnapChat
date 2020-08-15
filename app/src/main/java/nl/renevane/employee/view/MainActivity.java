package nl.renevane.employee.view;
/*

Qais Safdary
 praktijk 1
*
* */

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import nl.renevane.employee.R;

public class MainActivity extends AppCompatActivity
        implements CameraFragmentListener, EditFragmentListener{

    private final CameraFragment cameraFragment = new CameraFragment();
    private final EditFragment editFragment = new EditFragment();
    private final ShareFragment shareFragment = new ShareFragment();

    @Override
    public void onCameraPhotoSaved(String path) {
        editFragment.addToImageList(path);
    }

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

        showFragment(selectedFragment);

        return true;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_edit);

            showFragment(editFragment);

            String storageFolder = getExternalFilesDir(null).toString();
            String photoFilterPattern = getString(R.string.pattern_filter_photo);
            String imageFilterPattern = getString(R.string.pattern_filter_image);

            editFragment.createImageList(storageFolder, photoFilterPattern);
            shareFragment.createImageList(storageFolder, imageFilterPattern);

        }
    }

}
