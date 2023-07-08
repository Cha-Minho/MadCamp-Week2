package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
//import okhttp3.Response;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Callback;
//import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import java.io.File;
import java.io.IOException;

public class Frag2 extends Fragment {
    private boolean isProcessingImage = false;
    ImageView imageView;
    CameraSurfaceView surfaceView;
    Timer timer;
    private final OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frag2, container, false);
        imageView = root.findViewById(R.id.imageView);
        surfaceView = root.findViewById(R.id.surfaceView);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                capture();
            }
        };

        timer = new Timer();
        timer.schedule(task, 0, 500);  // 0.5초마다 capture() 호출

        return root;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 101:
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getContext(), "카메라 권한 사용자가 승인함",Toast.LENGTH_LONG).show();
                    }
                    else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(getContext(), "카메라 권한 사용자가 허용하지 않음.",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(), "수신권한 부여받지 못함.",Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    public void capture() {
        if (isProcessingImage) {
            Log.d("not taken", "123123132121313123322133");
            return;
        }
        else {
            Log.d("taken", "1231231231231");
        }
        isProcessingImage = true;
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // 1/8사이즈로 보여주기
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); //data 어레이 안에 있는 데이터 불러와서 비트맵에 저장

                // 이미지 전처리
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = 200;
                int newHeight = 200;

                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.setScale(-1, 1);
                matrix.postRotate(90);

                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0,0,width,height,matrix,true);
                BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

                imageView.setImageDrawable(new BitmapDrawable(resizedBitmap));//이미지뷰에 사진 보여주기
                camera.startPreview();

                // HTTP 요청을 이용하여 이미지를 서버에 전송
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapData = bos.toByteArray();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "people.jpg",
                                RequestBody.create(bitmapData, MediaType.parse("image/jpeg")))
                        .build();

                Request request = new Request.Builder()
                        .url("https://6ef2-192-249-19-234.ngrok-free.app/api/")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        isProcessingImage = false;
                        Log.d("OkHttpError", e.getMessage()); // 에러 메시지 출력
                        capture();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        isProcessingImage = false;
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            capture();
                        }
                    }
                });
            }
        });
    }


    public class RetrofitClient {
        private static Retrofit retrofit = null;

        private RetrofitClient() {}

        public static Retrofit getClient(String baseUrl) {
            if (retrofit == null) {
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();
            }

            return retrofit;
        }
    }

    public interface ApiService {
        @Multipart
        @POST("api/")
        retrofit2.Call<ResponseBody> uploadImage(@Part MultipartBody.Part image);
    }


}
