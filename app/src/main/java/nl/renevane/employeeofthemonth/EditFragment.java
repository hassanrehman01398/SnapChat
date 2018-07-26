package nl.renevane.employeeofthemonth;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class EditFragment extends Fragment implements View.OnClickListener {

    private EditFragmentListener editFragmentListener;
    private String fullPathOfMostRecentlySavedImage;

    private String fullPathOfMostRecentlySavedPhoto;

    // called from MainActivity
    public void updatePath(String path) {
        fullPathOfMostRecentlySavedPhoto = path;
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
        // TODO: check orientation!
        ImageView editPreview = view.findViewById(R.id.edit_preview);
        editPreview.setImageBitmap(BitmapFactory.decodeFile(fullPathOfMostRecentlySavedPhoto));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_select:
                // TODO choose a photo
                break;
            case R.id.fab_save:
                // TODO save the edited photo
                break;
        }
    }

    // TODO: write code for editing and saving
    void dummyImageEditMethod() {
        // pass the location of the saved image to the Main Activity
        fullPathOfMostRecentlySavedImage = null;
        editFragmentListener.onEditedPictureSaved(fullPathOfMostRecentlySavedImage);
    }

}
