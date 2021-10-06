package com.example.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;

public class LiveCameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private TextureView myTexture;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_camera);
        myTexture=findViewById(R.id.textureView);
        myTexture = new TextureView(this);
        myTexture.setSurfaceTextureListener(this);
        setContentView(myTexture);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();

        myTexture.setLayoutParams(new FrameLayout.LayoutParams(
                previewSize.width, previewSize.height, Gravity.CENTER));

        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException t) {
        }

        mCamera.startPreview();
        myTexture.setAlpha(1.0f);
        myTexture.setRotation(90.0f);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}