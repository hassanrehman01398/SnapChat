package nl.renevane.employeeofthemonth;

import android.app.Activity;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditFragment extends Fragment implements View.OnClickListener {

    private String currentImage;
    private final List<String> imageList = new ArrayList<>();
    private int lastImageInList;
    private int imageListIndex;
    private ImageView editPreview;

    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

    private EditFragmentListener editFragmentListener;

    // make a pattern-matched list of image paths from the storage folder
    public void createImageList(String storageFolder, String pattern) {
        File folder = new File(storageFolder);
        File[] files = folder.listFiles((file, s) -> s.matches(pattern));

        // listFiles method does not guarantee an ordered list
        Arrays.sort(files);

        for (File f : files) {
            try {
                addToImageList(f.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // called by CameraFragment through MainActivity
    public void addToImageList(String path) {
        currentImage = path;
        imageList.add(path);
        lastImageInList = imageList.size() - 1;
        imageListIndex = lastImageInList; // point to last added image
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        FloatingActionButton fabSelect = view.findViewById(R.id.fab_next_image);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save_image);

        fabSelect.setOnClickListener(this);
        fabSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editPreview = view.findViewById(R.id.edit_preview);
        ImageView motionView = view.findViewById(R.id.motion_view);

        showImageInEditPreview(currentImage);
        showImageIdInView(R.drawable.sticker_beard, motionView);

    }

    // Glide (https://bumptech.github.io/glide/) makes image handling much easier
    // Also recommended by Google (https://developer.android.com/topic/performance/graphics/)

    private void showImageInEditPreview(String path) {
        GlideApp.with(this)
                .load(path)
                .into(editPreview);
    }

    // use Integer resource id (so that the .toString() method can be called on it)
    private void showImageIdInView(Integer id, ImageView view) {
        GlideApp.with(this)
                .load(id)
                .into(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_next_image:
                showNextImageInEditPreview();
                break;
            case R.id.fab_save_image:
                // TODO: save the combined images
                saveEditedImage();
                break;
            case R.id.fab_sticker_add:
                // TODO add a sticker (start sticker select activity)
                break;
            case R.id.fab_sticker_remove:
                // TODO remove sticker
                break;
            case R.id.fab_sticker_done:
                // TODO done editing (if needed)

        }
    }

    private void showNextImageInEditPreview() {
        if (!imageList.isEmpty()) {
            if (imageListIndex == lastImageInList) {
                imageListIndex = 0;
            } else imageListIndex++;
            currentImage = imageList.get(imageListIndex);

            showImageInEditPreview(currentImage);
        }
    }

    // save and pass the location of the edited image
    private void saveEditedImage() {
        // TODO: 'currentImage' only used for debugging now, nothing actually saved
        showToast(getString(R.string.toast_image_saved));
        editFragmentListener.onEditedImageSaved(currentImage);
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

    // remove the listener when the fragment is removed from the activity
    @Override
    public void onDetach() {
        super.onDetach();
        editFragmentListener = null;
    }

}
