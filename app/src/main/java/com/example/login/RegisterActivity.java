package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String URL = "https://3f01-192-249-19-234.ngrok-free.app/register/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText usernameEditText = findViewById(R.id.editTextUsernameReg);
        final EditText passwordEditText = findViewById(R.id.editTextPasswordReg);
        final EditText nicknameEditText = findViewById(R.id.editTextNicknameReg);
        Button registerButton = findViewById(R.id.buttonRegister);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues(usernameEditText, passwordEditText, nicknameEditText, registerButton);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        nicknameEditText.addTextChangedListener(textWatcher);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String name = nicknameEditText.getText().toString();

                JSONObject json = new JSONObject();
                try {
                    json.put("email", email);
                    json.put("password", password);
                    json.put("name", name);
                    json.put("is_employed", true); // Or whatever you want this value to be.
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("TAG", "response: " + response.body().string());
                            // Intent to Main Activity
                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            throw new IOException("Unexpected code " + response);
                        }
                    }
                });
            }
        });
    }

    void checkFieldsForEmptyValues(EditText usernameEditText, EditText passwordEditText, EditText nicknameEditText, Button registerButton) {
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String nickname = nicknameEditText.getText().toString();

        // Add your condition here
        if (isValidEmail(email) && isValidPassword(password) && !nickname.isEmpty()) {
            registerButton.setEnabled(true);
            registerButton.setBackgroundResource(R.drawable.register_button_enabled); // Change the background to enabled state
            registerButton.setTextColor(Color.WHITE); // Change the text color to white
        } else {
            registerButton.setEnabled(false);
            registerButton.setBackgroundResource(R.drawable.register_button_background); // Change the background to disabled state
            registerButton.setTextColor(Color.BLACK); // Change the text color to black
        }
    }

    boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
