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

public class EditFragment extends Fragment implements View.OnClickListener {

    private EditFragmentListener editFragmentListener;
    private String absolutePathOfCurrentImage;
    private String absolutePathOfAppStorageFolderWithTrailingSlash;

    // also called by CameraFragment through MainActivity
    public void updatePath(String path) {
        absolutePathOfCurrentImage = path;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
         * Google also recommends it!
         *
         * More info:
         *
         * https://bumptech.github.io/glide/
         * https://developer.android.com/topic/performance/graphics/
         */

        // for debugging only
        updatePath(absolutePathOfAppStorageFolderWithTrailingSlash + "photo-20180727-112003.jpg");

        Glide.with(this)
                .load(absolutePathOfCurrentImage)
                .into(editPreview);

    }

    // remove the reference to the activity when the fragment is removed from the activity
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
                updatePath(null);
                break;
            case R.id.fab_save:
                // TODO: save the edited picture
                saveEditedPicture();
                break;
        }
    }

    // TODO: write code for editing and saving
    private void saveEditedPicture() {
        // pass the location of the edited picture
        editFragmentListener.onEditedPictureSaved(absolutePathOfCurrentImage);
    }

    private void refreshView() {
        getChildFragmentManager()
                .beginTransaction()
                .commit();
    }

}
