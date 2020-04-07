package com.devsoftzz.numfacts.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.core.content.res.ResourcesCompat;

import com.devsoftzz.numfacts.R;

public class WriteOnImage {

    public static Bitmap drawMultilineTextToImage(Context context, String text, float size, Integer textColor) {

        // prepare canvas
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap whiteboard = BitmapFactory.decodeResource(resources, R.drawable.whiteboard)
                                .copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(whiteboard);

        // new anti-aliased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color
        paint.setColor(textColor);
        // text size in pixels
        paint.setTextSize((int) (size * 3 * scale));
        //font-family
        Typeface font = ResourcesCompat.getFont(context, R.font.roboto_light);
        paint.setTypeface(font);
        // text Shadow
        paint.setShadowLayer(2, size / 10, size / 10, Color.parseColor("#D6D6D6"));

        // set text width to canvas width minus padding
        int textWidth = canvas.getWidth() - (int) (100 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(text,
                paint,
                textWidth,
                Layout.Alignment.ALIGN_OPPOSITE,
                1.0f,
                0.0f,
                false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (whiteboard.getWidth() - textWidth) / 2;
        float y = (whiteboard.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        // Adding TextBox to Background
        Bitmap background = BitmapFactory.decodeResource(resources, R.drawable.backdrop)
                                .copy(Bitmap.Config.ARGB_8888, true);

        Canvas back = new Canvas(background);

        x = (background.getWidth() - whiteboard.getWidth()) / 2;
        y = (background.getHeight() - whiteboard.getHeight()) / 2;

        back.drawBitmap(whiteboard, x, y, new Paint());

        return background;
    }

}
