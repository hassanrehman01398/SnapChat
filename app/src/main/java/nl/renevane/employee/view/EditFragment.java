package nl.renevane.employee.view;
/*

Qais Safdary
 praktijk 1
*
* */

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nl.renevane.employee.Models.entity.Image;
import nl.renevane.employee.Models.entity.MotionLayer;
import nl.renevane.employee.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment implements View.OnClickListener {

    private String currentImage;
    private final List<String> imageList = new ArrayList<>();
    private int lastImageInList;
    private int imageListIndex;
    private ImageView editPreview;
    private MotionView motion;
    private FrameLayout combinedView;
    private FrameLayout employeeOfTheMonthFrame;
    static final int SELECT_STICKER_REQUEST_CODE = 123;
    private EditFragmentListener editFragmentListener;


    private static int RESULT_LOAD_IMAGE = 1;

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

        FloatingActionButton fabStickerAdd = view.findViewById(R.id.fab_sticker_add);
        FloatingActionButton fabStickerRemove = view.findViewById(R.id.fab_sticker_remove);
        FloatingActionButton fabStickerFlip = view.findViewById(R.id.fab_sticker_flip);
        FloatingActionButton fabNext = view.findViewById(R.id.fab_next);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);
        FloatingActionButton gallery = view.findViewById(R.id.gallery);

        fabStickerAdd.setOnClickListener(this);
        fabStickerRemove.setOnClickListener(this);
        fabStickerFlip.setOnClickListener(this);
        fabNext.setOnClickListener(this);
        fabSave.setOnClickListener(this);
gallery.setOnClickListener(this);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            editPreview.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
        if (resultCode == RESULT_OK && requestCode == SELECT_STICKER_REQUEST_CODE && data != null) {
            int stickerId = data.getIntExtra(StickerSelect.EXTRA_STICKER_ID, 0);
            if (stickerId != 0) {
                addSticker(stickerId);
            }
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editPreview = view.findViewById(R.id.edit_preview);

        motion = view.findViewById(R.id.motion_view);
        combinedView = view.findViewById(R.id.combined_view);
        employeeOfTheMonthFrame = view.findViewById(R.id.frame_employeeofthemonth);

        TextView fragmentText = view.findViewById(R.id.text_fragment_edit);

        TextView currentMonthOverlayText = view.findViewById(R.id.text_overlay_currentmonth);
        currentMonthOverlayText.setText(getCurrentMonth());

        showImageInEditPreview(currentImage);
        fadeInThenWaitThenFadeOut(fragmentText);
    }

    private CharSequence getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        // get the month name in the language set on the device
        return new SimpleDateFormat(getString(R.string.month_pattern)).format(calendar.getTime());
    }

    private void showImageInEditPreview(String path) {
        GlideApp.with(getContext())
                .load(path)
                .into(editPreview);
    }

    private void fadeInThenWaitThenFadeOut(TextView textView) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater
                .loadAnimator(getActivity(), R.animator.fade_in_then_out);
        set.setTarget(textView);
        set.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_sticker_add:
                addSticker();
                break;
            case R.id.fab_sticker_remove:
                removeSelectedSticker();
                break;
            case R.id.fab_sticker_flip:
                flipSelectedSticker();
                break;
            case R.id.fab_next:
                showNextImageInEditPreview();
                break;
            case R.id.fab_save:
                saveMergedImage();
                break;
            case R.id.gallery:

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    private void addSticker() {
        Intent intent = new Intent(getActivity(), StickerSelect.class);
        startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
    }

    private void removeSelectedSticker() {
        motion.deletedSelectedEntity();
    }

    private void flipSelectedSticker() {
        motion.flipSelectedEntity();
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


    private void addSticker(final int stickerResId) {
        MotionLayer layer = new MotionLayer();
        Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
        Image entity = new Image(layer, pica, motion.getWidth(), motion.getHeight());
        motion.addEntityAndPosition(entity);
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

    // combined image will be saved only if there is an image AND at least one sticker
    private void saveMergedImage() {
        if (currentImage != null && !motion.getEntities().isEmpty()) {
            unhideEmployeeOfTheMonthFrame();
            motion.unselectEntity();
            saveBitmap(getBitmapFromView(combinedView));
            hideEmployeeOfTheMonthFrame();
        }
    }

    private void unhideEmployeeOfTheMonthFrame() {
        employeeOfTheMonthFrame.setVisibility(View.VISIBLE);
    }

    private void hideEmployeeOfTheMonthFrame() {
        employeeOfTheMonthFrame.setVisibility(View.INVISIBLE);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap
                .createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void saveBitmap(Bitmap bitmap) {
        // the actual file name with complete path
        String saveFolder = getActivity().getExternalFilesDir(null).toString();
        String fileName = (new SimpleDateFormat(getString(R.string.pattern_filename_image), Locale.US)
                .format(new Date()));

        String outputStreamDestination = saveFolder + "/" + fileName;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputStreamDestination);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // pass the path of the saved image to the activity and from there to the share fragment
        editFragmentListener.onEditedImageSaved(outputStreamDestination);

        showToast(getString(R.string.toast_image_saved));
    }

    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

}
