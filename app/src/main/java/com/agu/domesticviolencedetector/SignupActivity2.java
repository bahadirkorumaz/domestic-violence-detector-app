package com.agu.domesticviolencedetector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
public class SignupActivity2 extends AppCompatActivity {


    @BindView(R.id.input_contact)
    EditText _contact1;
    @BindView(R.id.input_contact2)
    EditText _contact2;
    @BindView(R.id.input_contact3)
    EditText _contact3;
    @BindView(R.id.input_location)
    EditText _address;
    @BindView(R.id.btn_next)
    Button _nextButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        ButterKnife.bind(this);
        user = (User) getIntent().getSerializableExtra("user");

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
                sendToAPI(user);
                Intent intent = new Intent(SignupActivity2.this,SignupActivity3.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    public void sendData() {
        Log.v("deneme", "debug");

        final String contact1 = _contact1.getText().toString();
        final String contact2 = _contact2.getText().toString();
        final String contact3 = _contact3.getText().toString();
        final String address = _address.getText().toString();

        user.setEmergencyContact1(contact1);
        user.setEmergencyContact2(contact2);
        user.setEmergencyContact3(contact3);
        user.setLocation(address);
    }



    public void sendToAPI(final User user) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonParam = new JSONObject();


                    jsonParam.put("userId", user.getUserId());
                    jsonParam.put("name", user.getName());
                    jsonParam.put("gender", user.getGender());
                    jsonParam.put("age", user.getAge());
                    jsonParam.put("location", user.getLocation());
                    jsonParam.put("emergencyContactOne", user.getEmergencyContact1());
                    jsonParam.put("emergencyContactTwo", user.getEmergencyContact2());
                    jsonParam.put("emergencyContactThree", user.getEmergencyContact3());



                    URL url = new URL("https://domesticviolenceapi.azurewebsites.net/api/user");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("ApiKey", "AdminApiKey");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                    osw.write(jsonParam.toString());
                    osw.flush();
                    osw.close();
                    os.close();
                    int a = conn.getResponseCode();
                    String b = conn.getResponseMessage();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
