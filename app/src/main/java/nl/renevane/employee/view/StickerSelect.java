package nl.renevane.employee.view;
/*

Qais Safdary
 praktijk 1
*
* */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import nl.renevane.employee.R;


public class StickerSelect extends AppCompatActivity {

    public static final String EXTRA_STICKER_ID = "extra_sticker_id";

    private final int[] stickerIds = {
            R.drawable.filter1,
            R.drawable.filter2,
            R.drawable.filter3,
            R.drawable.filter4,
            R.drawable.filter5,
            R.drawable.filter6

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_select);

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
    }

}
