package nl.renevane.employeeofthemonth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class EditFragment extends Fragment implements View.OnClickListener {

    private String currentImage;
    private final List<String> imageList = new ArrayList<>();
    private int lastImageInList;
    private int imageListIndex;

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

        // because the listFiles method does not guarantee any order
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

    // Glide (https://bumptech.github.io/glide/) makes image handling much easier
    // Also recommended by Google (https://developer.android.com/topic/performance/graphics/)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView editPreview = view.findViewById(R.id.edit_preview);
        ImageView motionView = view.findViewById(R.id.motion_view);

        showImageInView(currentImage, editPreview);
        // TODO: debugging
        showImageInView("/storage/emulated/0/Android/data/nl.renevane.employeeofthemonth/files/transparency_test.png", motionView);

    }

    private void showImageInView(String path, ImageView view) {
        Log.i("EditFragment.showImageInView:", path);
        GlideApp.with(this)
                .load(path)
                .transition(withCrossFade()) // default is 300 ms
                .into(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_next_image:
                showNextImage();
                break;
            case R.id.fab_save_image:
                // TODO: save the image
                saveEditedImage();
                break;
        }
    }

    private void showNextImage() {
        if (!imageList.isEmpty()) {
            if (imageListIndex == lastImageInList) {
                imageListIndex = 0;
            } else imageListIndex++;
            currentImage = imageList.get(imageListIndex);
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

    // save and pass the location of the edited image
    private void saveEditedImage() {
        // TODO: 'currentImage' only used for debugging now, nothing actually saved
        editFragmentListener.onEditedImageSaved(currentImage);
        showToast("Image saved");
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
