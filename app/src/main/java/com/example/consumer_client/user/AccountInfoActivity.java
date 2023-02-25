package com.example.consumer_client.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.consumer_client.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface RegisterService {

    @POST("signup/phone-check")
    Call<ResponseBody> checkPhone(@Body JsonObject body);

    @POST("signup/phone-check/verify")
    Call<ResponseBody> phoneVerify(@Body JsonObject body);
}

public class AccountInfoActivity extends AppCompatActivity {
    String TAG = AccountInfoActivity.class.getSimpleName();

    RegisterService service;
    JsonParser jsonParser;

    private TextView code_verify_txt;
    private EditText code_verify_input, name, mobile_no;
    private Button nextStep, phone_verify_btn, code_verify_btn;
    String code_confirm;
    Boolean code_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseurl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RegisterService.class);
        jsonParser = new JsonParser();

        name = (EditText) findViewById(R.id.inputName);
        mobile_no = (EditText) findViewById(R.id.inputCall);
        phone_verify_btn = findViewById(R.id.mobileAuth);
        nextStep = findViewById(R.id.nextStep);

        phone_verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCheck(mobile_no.getText().toString());
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                code_verify_input = findViewById(R.id.inputNum);

                if (phoneVerify(code_verify_input.getText().toString(), mobile_no.getText().toString())) {
                    goNext();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "인증 번호 및 전화번호를 확인해주세요.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    void phoneCheck(String phone_number) {
        JsonObject body = new JsonObject();
        body.addProperty("phone_number", phone_number);
        Call<ResponseBody> call = service.checkPhone(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("response", response.toString());
                if (response.isSuccessful()) {
                    try {
                        JsonObject res = (JsonObject) jsonParser.parse(response.body().string());
                        Log.d(TAG, res.get("msg").getAsString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d(TAG, "Fail " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: e " + t.getMessage());
            }
        });
    }

    Boolean phoneVerify(String code, String phone_number) {
        JsonObject body = new JsonObject();
        body.addProperty("phone_number", phone_number);
        body.addProperty("code", code);

        Call<ResponseBody> call = service.phoneVerify(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JsonObject res = (JsonObject) jsonParser.parse(response.body().string());
                    if (res.get("phone_valid").getAsBoolean()) {
                        code_ver = true;
                    } else {
                        code_ver = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return code_ver;
    }

    void goNext() {
        name.setError(null); //오류 있으면 or 비어있으면 멘트 띄우는 거 일단 null로
        mobile_no.setError(null); //오류 있으면 or 비어있으면 멘트 띄우기

        boolean cancel = false;
        View focusView = null;

        String uname = name.getText().toString();
        String umobile_no = mobile_no.getText().toString();

        // 이름 빈칸 검사
        if (uname.isEmpty()) {
            name.setError("이름을 입력해주세요.");
            focusView = name;
            cancel = true;
        }

        //전화번호 빈칸 검사
        if (umobile_no.isEmpty()) {
            mobile_no.setError("전화번호를 입력해주세요.");
            focusView = mobile_no;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            intent.putExtra("name", uname);
            intent.putExtra("phone_number", umobile_no);
            startActivity(intent);
        }
    }
}


