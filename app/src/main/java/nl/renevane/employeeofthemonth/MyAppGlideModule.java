package nl.renevane.employeeofthemonth;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public final class MyAppGlideModule extends AppGlideModule {

    // Needed for the Glide generated API
    // See https://bumptech.github.io/glide/doc/generatedapi.html

    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        // Better image quality (and a bit more memory usage) than the default RGB_565
        builder.setDefaultRequestOptions(new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888));
    }

}
