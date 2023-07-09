package com.example.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.IOException;
import java.util.List;

public class Stretch1Activity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Camera camera;
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch1);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                String videoId = "S0Q4gqBUs7c";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        FloatingActionButton btn_BackToFrag3 = findViewById(R.id.btn_BackToFrag3);
        btn_BackToFrag3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform your image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void openCamera() {
        int cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
        } else {
            camera = Camera.open(cameraId);
            Camera.Parameters params = camera.getParameters();

            // Set rotation
            params.setRotation(90);

            // Set preview size
            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
            bestSize = sizeList.get(0);
            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }
            params.setPreviewSize(bestSize.width, bestSize.height);

            camera.setParameters(params);

            if (textureView.isAvailable()) {
                try {
                    camera.setPreviewTexture(textureView.getSurfaceTexture());
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "TextureView is not available", Toast.LENGTH_LONG).show();
            }
        }
    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }
}
