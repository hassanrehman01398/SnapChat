package nl.renevane.employeeofthemonth;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ShareFragment extends Fragment implements View.OnClickListener {

    private String currentImage;
    private List<String> imageList = new ArrayList<>();
    private int lastImageInList;
    private int imageListIndex;

    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

    // make a pattern-matched list of image paths from the storage folder
    public void createImageList(String storageFolder, String pattern) {

        File folder = new File(storageFolder);
        File[] files = folder.listFiles((file, s) -> s.matches(pattern));

        for (File f : files) {
            try {
                addToImageList(f.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* previous non-lambda version included here for reference
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.matches(pattern);
            }
        });*/
    }

    // called by EditFragment through MainActivity
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

        View view = inflater.inflate(R.layout.fragment_share, container, false);

        FloatingActionButton fabSelect = view.findViewById(R.id.fab_next);
        FloatingActionButton fabShare = view.findViewById(R.id.fab_share_image);

        fabSelect.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView sharePreview = view.findViewById(R.id.share_preview);
        TextView textView = view.findViewById(R.id.text_fragment_share);

        showImageInView(sharePreview);
        fadeInThenWaitThenFadeOut(textView);
    }

    private void fadeInThenWaitThenFadeOut(TextView textView) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater
                .loadAnimator(getActivity(), R.animator.fade_in_then_out);
        set.setTarget(textView);
        set.start();
    }

    private void showImageInView(ImageView editPreview) {
        GlideApp.with(this)
                .load(currentImage)
                .transition(withCrossFade())
                .into(editPreview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_next:
                showNextImage();
                break;
            case R.id.fab_share_image:
                // TODO shareEditedImage();
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

}
