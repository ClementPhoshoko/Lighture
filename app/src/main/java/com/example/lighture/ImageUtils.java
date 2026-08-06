package com.example.lighture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public final class ImageUtils {
    private ImageUtils() {}

    public static void loadAssetImage(Context context, ImageView target, String assetPath) {
        try {
            InputStream stream = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            target.setImageBitmap(bitmap);
        } catch (IOException e) {
            target.setImageDrawable(null);
        }
    }
}
