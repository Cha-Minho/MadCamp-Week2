package com.example.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.*;

public class Frag2 extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean isProcessingImage = false;
    private ImageView imageView;
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Timer timer;
    private final OkHttpClient client = new OkHttpClient();

    private ImageReader reader;

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            //when textureView is available, open the camera
            transformImage(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frag2, container, false);
        textureView = root.findViewById(R.id.textureView);
        imageView = root.findViewById(R.id.imageView); // Add this line

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }

        return root;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "You can't use this app without granting camera permission", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String cameraId = null;
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    cameraId = id;
                    break;
                }
            }
            if (cameraId != null) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                manager.openCamera(cameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != cameraCaptureSession) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private void startPreview() {
        int width = 480;
        int height = 640;
        reader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 1);
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(width, height);
            Surface surface = new Surface(texture);
            Surface readerSurface = reader.getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface, readerSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) {
                        return;
                    }

                    cameraCaptureSession = session;
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        setAutoOrientation();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            capture();
                        }
                    };
                    timer = new Timer();
                    timer.schedule(task, 0, 100);  // Every 0.1 seconds, call capture()
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    private void capture() {
        if (isProcessingImage || cameraDevice == null) {
            return;
        }
        if (cameraCaptureSession == null) {
            return;
        }
        isProcessingImage = true;
        try {
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());

            HandlerThread handlerThread = new HandlerThread("CameraBackground");
            handlerThread.start();
            Handler backgroundHandler = new Handler(handlerThread.getLooper());

            reader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image img = null;
                    try {
                        img = reader.acquireLatestImage();
                        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        // Convert byte array to Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Rotate bitmap 90 degrees counterclockwise
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);

                        matrix.preScale(-1.0f, 1.0f);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        // Convert rotated bitmap back to byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] rotatedBytes = stream.toByteArray();

                        sendImageToServer(rotatedBytes);
                    } finally {
                        if (img != null) {
                            img.close();
                        }
                    }
                }

            }, backgroundHandler);

            cameraCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startPreview(); // Start preview again after capturing the image
                }
            }, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            return;
        }
    }




    private void sendImageToServer(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        // 이미지 전처리
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 200;
        int newHeight = 200;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();


        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0,0,width,height,matrix,true);

        // Http 요청을 이용하여 이미지를 서버에 전송
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "people.jpg",
                        RequestBody.Companion.create(data, MEDIA_TYPE_JPEG))
                .build();

        Request request = new Request.Builder()
                .url("https://3f01-192-249-19-234.ngrok-free.app/api/")
                .post(requestBody)
                .build();

        // ... (기존 코드)
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                isProcessingImage = false;
                Log.d("OkHttpError", e.getMessage()); // 에러 메시지 출력
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                isProcessingImage = false;
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        // Parse the response
                        JSONObject json = new JSONObject(response.body().string());

                        // Get the base64 string from the response
                        String base64Image = json.getString("image");

                        // Convert the base64 string to a bitmap
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        final Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        // Update the ImageView
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    imageView.setImageBitmap(bmp);
                                }
                            });
                        }

                        // Get the bad_posture from the response
                        boolean badPosture = json.getBoolean("bad_posture");

                        // If badPosture is true, play beep sound
                        if(badPosture) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep_sound);
                                        mediaPlayer.start();
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }



        });

    }

    private void setAutoOrientation() {
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int rotation = manager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
                break;
            case Surface.ROTATION_90:
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);
                break;
            case Surface.ROTATION_180:
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 270);
                break;
            case Surface.ROTATION_270:
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 180);
                break;
        }
    }
    private void transformImage(int width, int height) {
        if (textureView == null) {
            return;
        }
        Matrix matrix = new Matrix();
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, textureView.getHeight(), textureView.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();

        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width / textureView.getWidth(), (float)height / textureView.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(0, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }


}
