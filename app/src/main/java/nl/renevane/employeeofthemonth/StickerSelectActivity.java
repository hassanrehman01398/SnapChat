package nl.renevane.employeeofthemonth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * selects stickers
 * result - int, resource id of the sticker, bundled at key EXTRA_STICKER_ID
 * <p>
 * Stickers from : http://www.stickpng.com
 */
public class StickerSelectActivity extends AppCompatActivity {

    public static final String EXTRA_STICKER_ID = "extra_sticker_id";

    private final int[] stickerIds = {
            R.drawable.sticker_bar,
            R.drawable.sticker_beard_brown_mustache,
            R.drawable.sticker_beard_hipster,
            R.drawable.sticker_beard_light_long,
            R.drawable.sticker_ear_pink_left,
            R.drawable.sticker_ear_pink_small,
            R.drawable.sticker_glasses_black,
            R.drawable.sticker_glasses_cool,
            R.drawable.sticker_glasses_groucho,
            R.drawable.sticker_glasses_round,
            R.drawable.sticker_hair_black,
            R.drawable.sticker_hair_full_hippie,
            R.drawable.sticker_hair_long,
            R.drawable.sticker_hair_long_feminine,
            R.drawable.sticker_hair_purple,
            R.drawable.sticker_hair_red,
            R.drawable.sticker_hat_capone,
            R.drawable.sticker_hat_fez,
            R.drawable.sticker_hat_police_uk,
            R.drawable.sticker_hat_police_usa,
            R.drawable.sticker_hat_sheriff,
            R.drawable.sticker_mouth_cartoonish,
            R.drawable.sticker_mouth_clipart,
            R.drawable.sticker_mouth_drawing,
            R.drawable.sticker_mouth_hmm,
            R.drawable.sticker_mouth_vintage,
            R.drawable.sticker_mustache_classic_black,
            R.drawable.sticker_mustache_classic_brown,
            R.drawable.sticker_mustache_light_brown,
            R.drawable.sticker_mustache_long,
            R.drawable.sticker_wound_bleeding,
            R.drawable.sticker_wound_bloody,
            R.drawable.sticker_wound_bullet,
            R.drawable.sticker_wound_scar,
            R.drawable.sticker_wound_scars,
            R.drawable.sticker_wound_scaved
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_select);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.sticker_select_view);
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(glm);

        List<Integer> stickers = new ArrayList<>(stickerIds.length);
        for (Integer id : stickerIds) {
            stickers.add(id);
        }

        recyclerView.setAdapter(new StickersAdapter(stickers, this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onStickerSelected(int stickerId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STICKER_ID, stickerId);
        setResult(RESULT_OK, intent);
        finish();
    }

    class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerViewHolder> {

        private final List<Integer> stickerIds;
        private final Context context;
        private final LayoutInflater layoutInflater;

        StickersAdapter(@NonNull List<Integer> stickerIds, @NonNull Context context) {
            this.stickerIds = stickerIds;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StickerViewHolder(layoutInflater.inflate(R.layout.sticker_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, getItem(position)));
        }

        @Override
        public int getItemCount() {
            return stickerIds.size();
        }

        private int getItem(int position) {
            return stickerIds.get(position);
        }

        class StickerViewHolder extends RecyclerView.ViewHolder {

            ImageView image;

            StickerViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.sticker_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos >= 0) { // might be NO_POSITION
                            onStickerSelected(getItem(pos));
                        }
                    }
                });
            }
        }
    } // class StickersAdapter

}
