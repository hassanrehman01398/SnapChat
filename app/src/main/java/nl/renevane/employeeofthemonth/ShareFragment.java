package nl.renevane.employeeofthemonth;

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

public class ShareFragment extends Fragment implements View.OnClickListener {

    private String currentImage;

    // also called by EditFragment through MainActivity
    public void addToEditedPicturesList(String path) {
        currentImage = path;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        FloatingActionButton fabSelect = view.findViewById(R.id.fab_select);
        FloatingActionButton fabShare = view.findViewById(R.id.fab_share);

        fabSelect.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView sharePreview = view.findViewById(R.id.share_preview);
        Glide.with(this).load(currentImage).into(sharePreview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_select:
                // TODO select an edited photo
                break;
            case R.id.fab_share:
                // TODO share the edited photo
                break;
        }
    }

}
