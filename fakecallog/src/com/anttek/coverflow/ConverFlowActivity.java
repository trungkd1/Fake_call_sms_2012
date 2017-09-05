
package com.anttek.coverflow;

import com.anttek.coverflow.CoverAdapterView.OnItemSelectedListener;

import org.baole.fakelog.R;
import org.baole.fakelog.model.Configuration;
import org.baole.fakelog.model.FileItems;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class ConverFlowActivity extends Activity implements
        OnItemSelectedListener, ViewFactory {

    private ImageSwitcher mSwitcher;
    private Configuration mConfig;

    private TextView text;

    public static Integer[] mThumbIds = {
            R.drawable.ic_1, R.drawable.ic_1,
            R.drawable.ic_1, R.drawable.ic_4, R.drawable.ic_5,
            R.drawable.background_sony, R.drawable.background_sony2,
            R.drawable.background_sony3, R.drawable.background_sony4,
            R.drawable.background_sony5, R.drawable.background_sony6
    };

    public static Integer[] mImageIds = {
            R.drawable.receive1,
            R.drawable.receive2, R.drawable.receive3, R.drawable.receive4,
            R.drawable.receive5, R.drawable.receive_sony,
            R.drawable.receive_sony, R.drawable.receive_sony,
            R.drawable.receive_sony, R.drawable.receive_sony,
            R.drawable.receive_sony
    };

    Bitmap[] mBitmapWithReflections = new Bitmap[mThumbIds.length];

    private static String[] mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.coverflow_activity);
        Resources res = getResources();
        mVersion = res.getStringArray(R.array.version);

        reload();
        setImage();
        setSwitcher();

    }

    private void setImage() {
        CoverFlow coverFlow;
        coverFlow = (CoverFlow) findViewById(R.id.coverflow);

        coverFlow.setAdapter(new ImageAdapter(this));

        ImageAdapter coverImageAdapter = new ImageAdapter(this);

        coverImageAdapter.createReflectedImages();

        coverFlow.setAdapter(coverImageAdapter);

        coverFlow.setSpacing(-15);
        coverFlow.setSelection(mConfig.mId_image, true);

        coverFlow.setOnItemSelectedListener(this);
    }

    private void setSwitcher() {
        mSwitcher = (ImageSwitcher) findViewById(R.id.imgswitcher);
        text = (TextView) findViewById(R.id.textView1);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        mSwitcher.setImageResource(mThumbIds[mConfig.mId_image]);
        text.setText("Selected :" + mVersion[mConfig.mId_image]);
    }

    private void reload() {
        mConfig = Configuration.getInstance();
        mConfig.init(this);
    }

    @Override
    public void onItemSelected(CoverAdapterView<?> parent, View view,
            int position, long id) {
        FileItems.id_image = position;
        mSwitcher.setImageResource(mThumbIds[position]);
        text.setText("Selected :" + mVersion[position]);
    }

    @Override
    public void onNothingSelected(CoverAdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        return i;
    }

    @Override
    protected void onDestroy() {
        mConfig.mId_image = FileItems.id_image;
        mConfig.saveConfig();

        for (int i = 0; i < mImageIds.length; i++) {

            mBitmapWithReflections[i].recycle();
            mBitmapWithReflections[i] = null;
        }

        System.gc();

        super.onDestroy();
    }

    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public boolean createReflectedImages() {
            // The gap we want between the reflection and the original image
            final int reflectionGap = 4;

            int index = 0;
            for (int imageId : mImageIds) {
                Bitmap originalImage = BitmapFactory.decodeResource(
                        getResources(), imageId);
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                // This will not scale but will flip on the Y axis
                Matrix matrix = new Matrix();
                matrix.preScale(1, -1);

                // Create a Bitmap with the flip matrix applied to it.
                // We only want the bottom half of the image
                Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                        height / 2, width, height / 2, matrix, false);

                // Create a new bitmap with same width but taller to fit
                // reflection
                Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                        (height + height / 2), Config.ARGB_4444);

                // Create a new Canvas with the bitmap that's big enough for
                // the image plus gap plus reflection
                Canvas canvas = new Canvas(bitmapWithReflection);
                // Draw in the original image
                canvas.drawBitmap(originalImage, 0, 0, null);
                // Draw in the gap
                Paint deafaultPaint = new Paint();
                canvas.drawRect(0, height, width, height + reflectionGap,
                        deafaultPaint);
                // Draw in the reflection
                canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
                        null);

                // Create a shader that is a linear gradient that covers the
                // reflection
                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(0,
                        originalImage.getHeight(), 0,
                        bitmapWithReflection.getHeight() + reflectionGap,
                        0x70ffffff, 0x00ffffff, TileMode.CLAMP);
                // Set the paint to use this shader (linear gradient)
                paint.setShader(shader);
                // Set the Transfer mode to be porter duff and destination in
                paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
                // Draw a rectangle using the paint with our linear gradient
                canvas.drawRect(0, height, width,
                        bitmapWithReflection.getHeight() + reflectionGap, paint);

                reflectionImage.recycle();
                originalImage.recycle();

                mBitmapWithReflections[index++] = bitmapWithReflection;
            }
            return true;
        }

        public Bitmap createReflected(int imageId) {
            // The gap we want between the reflection and the original image
            final int reflectionGap = 4;

            Options opt = new Options();
            opt.outHeight = 100;
            opt.outWidth = 60;

            Bitmap originalImage = BitmapFactory.decodeResource(
                        getResources(), imageId, opt);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);

            // Create a Bitmap with the flip matrix applied to it.
            // We only want the bottom half of the image
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                        height / 2, width, height / 2, matrix, false);

            // Create a new bitmap with same width but taller to fit
            // reflection
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                        (height + height / 2), Config.ARGB_4444);

            // Create a new Canvas with the bitmap that's big enough for
            // the image plus gap plus reflection
            Canvas canvas = new Canvas(bitmapWithReflection);
            // Draw in the original image
            canvas.drawBitmap(originalImage, 0, 0, null);
            // Draw in the gap
            Paint deafaultPaint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap,
                        deafaultPaint);
            // Draw in the reflection
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
                        null);

            // Create a shader that is a linear gradient that covers the
            // reflection
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0,
                        originalImage.getHeight(), 0,
                        bitmapWithReflection.getHeight() + reflectionGap,
                        0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            // Set the paint to use this shader (linear gradient)
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            // Draw a rectangle using the paint with our linear gradient
            canvas.drawRect(0, height, width,
                        bitmapWithReflection.getHeight() + reflectionGap, paint);

            reflectionImage.recycle();
            originalImage.recycle();

            return bitmapWithReflection;

        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            // Use this code if you want to load from resources
            if (convertView == null) {
                ImageView i = new ImageView(mContext);
                i.setImageResource(mImageIds[position]);
                i.setLayoutParams(new CoverFlow.LayoutParams(70, 130));
                i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                convertView = i;
            }

            ((ImageView) convertView)
                    .setImageBitmap(mBitmapWithReflections[position]);
            return convertView;
        }

        /**
         * Returns the size (0.0f to 1.0f) of the views depending on the
         * 'offset' to the center.
         */
        public float getScale(boolean focused, int offset) {
            /* Formula: 1 / (2 ^ offset) */
            return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
        }

    }

}
