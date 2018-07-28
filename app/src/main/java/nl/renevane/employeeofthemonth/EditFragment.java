package nl.renevane.employeeofthemonth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditFragment extends Fragment implements View.OnClickListener {

    private EditFragmentListener editFragmentListener;
    private String currentImage;
    private final List<String> cameraRoll = new ArrayList<>();
    private int cameraRollIndex;
    private int cameraRollIndexMaxValue;

    // called by CameraFragment through MainActivity
    public void addToCameraRoll(String path) {
        currentImage = path;
        cameraRoll.add(path);
        cameraRollIndexMaxValue = cameraRoll.size() - 1;
        cameraRollIndex = cameraRollIndexMaxValue; // point to last added photo
    }

    // load the camera roll with saved photos
    public void loadCameraRoll() {
        String storageFolder = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getExternalFilesDir(null)).toString();

        File folder = new File(storageFolder);

        File[] files = folder.listFiles((file, fileName) -> fileName
                .matches(getString(R.string.photo_filename_regex)));

        for (File f : files) {
            try {
                addToCameraRoll(f.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // assign the fragment listener to the activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // the activity needs to implement the interface for this to work
        if (context instanceof EditFragmentListener) {
            editFragmentListener = (EditFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditFragmentListener!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        FloatingActionButton fabSelect = view.findViewById(R.id.fab_select);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);

        fabSelect.setOnClickListener(this);
        fabSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView editPreview = view.findViewById(R.id.edit_preview);

        /*
         * Glide makes image handling easier and more robust
         * Google also recommends it
         *
         * More info:
         *
         * https://bumptech.github.io/glide/
         * https://developer.android.com/topic/performance/graphics/
         */

        Glide.with(this).load(currentImage).into(editPreview);

    }

    // remove the listener when the fragment is removed from the activity
    @Override
    public void onDetach() {
        super.onDetach();
        editFragmentListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_select:
                // TODO: cycle through a list of saved photos
                getNextPhoto();
                refreshView();
                break;
            case R.id.fab_save:
                // TODO: should save the edited picture.
                // During debugging the button is used to call loadCameraRoll
                //saveEditedPicture();
                loadCameraRoll();
                refreshView();
                break;
        }
    }

    // make the camera roll behave like a circular linked list

    private void getNextPhoto() {
        if (cameraRollIndex == cameraRollIndexMaxValue) {
            cameraRollIndex = 0;
        } else cameraRollIndex++;
        currentImage = cameraRoll.get(cameraRollIndex);
    }

    private void refreshView() {
        if (getFragmentManager() != null) {
            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }

    }

    // TODO: save and pass the location of the edited picture
    private void saveEditedPicture() {
        editFragmentListener.onEditedPictureSaved(currentImage);
    }

}
