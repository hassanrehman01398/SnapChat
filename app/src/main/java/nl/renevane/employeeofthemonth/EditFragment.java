package nl.renevane.employeeofthemonth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EditFragment extends Fragment implements View.OnClickListener {

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

}
