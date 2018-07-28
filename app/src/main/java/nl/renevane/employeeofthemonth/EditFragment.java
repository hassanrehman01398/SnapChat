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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditFragment extends Fragment implements View.OnClickListener {

    private EditFragmentListener editFragmentListener;
    private String currentPhoto;
    private final List<String> cameraRoll = new ArrayList<>();
    private int cameraRollIndex;
    private int cameraRollIndexMaxValue;

    // called by CameraFragment through MainActivity
    public void addToCameraRoll(String path) {
        currentPhoto = path;
        cameraRoll.add(path);
        cameraRollIndexMaxValue = cameraRoll.size() - 1;
        cameraRollIndex = cameraRollIndexMaxValue; // point to last added photo
    }

    // called from MainActivity
    public void loadPhotos(String storageFolder, String pattern) {
        File folder = new File(storageFolder);

        /*
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.matches(pattern);
            }
        });*/

        // this is Java 8's more elegant lambda expression equivalent of the code above
        File[] files = folder.listFiles((file, s) -> s.matches(pattern));

        // call addToCameraRoll for each file that matches the filter pattern
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

    // using Glide makes image handling so much easier!
    // https://bumptech.github.io/glide/
    // https://developer.android.com/topic/performance/graphics/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView editPreview = view.findViewById(R.id.edit_preview);
        // Glide.with(this).load(currentPhoto).into(editPreview);
        GlideApp.with(this)
                .load(currentPhoto)
                .into(editPreview);
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
                showNextPhoto();
                break;
            case R.id.fab_save:
                // TODO: save the picture.
                // saveEditedPicture();
                refreshView();
                break;
        }
    }

    private void showNextPhoto() {
        if (!cameraRoll.isEmpty()) {
            if (cameraRollIndex == cameraRollIndexMaxValue) {
                cameraRollIndex = 0;
            } else cameraRollIndex++;
            currentPhoto = cameraRoll.get(cameraRollIndex);
            refreshView();
        }
    }

    // reload the fragment instance which effectively refreshes the view
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
        editFragmentListener.onEditedPictureSaved(currentPhoto);
    }

}
