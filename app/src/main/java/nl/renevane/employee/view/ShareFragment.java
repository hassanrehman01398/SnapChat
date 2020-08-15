package nl.renevane.employee.view;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.renevane.employee.R;


public class ShareFragment extends Fragment implements View.OnClickListener {

    private String currentImage;
    private List<String> imageList = new ArrayList<>();
    private int lastImageInList;
    private int imageListIndex;
    private ImageView sharePreview;

    // make a pattern-matched list of image paths from the storage folder
    public void createImageList(String storageFolder, String pattern) {
        File folder = new File(storageFolder);
        File[] files = folder.listFiles((file, s) -> s.matches(pattern));


        Arrays.sort(files);

        for (File f : files) {
            try {
                addToImageList(f.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


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
        FloatingActionButton fabShare = view.findViewById(R.id.fab_share);

        fabSelect.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharePreview = view.findViewById(R.id.share_preview);
        TextView fragmentText = view.findViewById(R.id.text_fragment_share);

        showImageInSharePreview(currentImage);
        fadeInThenWaitThenFadeOut(fragmentText);
    }

    private void fadeInThenWaitThenFadeOut(TextView textView) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater
                .loadAnimator(getActivity(), R.animator.fade_in_then_out);
        set.setTarget(textView);
        set.start();
    }

    private void showImageInSharePreview(String path) {
        GlideApp.with(getContext())
                .load(path)
                .into(sharePreview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_next:
                showNextImageInSharePreview();
                break;
            case R.id.fab_share:
                shareCurrentImage();
                break;
        }
    }

    private void showNextImageInSharePreview() {
        if (!imageList.isEmpty()) {
            if (imageListIndex == lastImageInList) {
                imageListIndex = 0;
            } else imageListIndex++;
            currentImage = imageList.get(imageListIndex);

            showImageInSharePreview(currentImage);
        }
    }

    private void shareCurrentImage() {
        if (currentImage != null) {
            Uri sharedFileUri
                    = FileProvider.getUriForFile(getActivity()
                    , getString(R.string.provider_authority), new File(currentImage));

            Intent intentShare = new Intent(Intent.ACTION_SEND);

            intentShare.setType("image/jpeg");
            intentShare.putExtra(Intent.EXTRA_STREAM, sharedFileUri);
            intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intentShare, ""));
        }
    }

    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

}