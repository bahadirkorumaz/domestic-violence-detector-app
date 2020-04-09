package com.agu.domesticviolencedetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.weiwangcn.betterspinner.library.BetterSpinner;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity3 extends AppCompatActivity {

    @BindView(R.id.ageS)
    EditText _age;
    @BindView(R.id.btn_signup)
    Button btn_signup;
    User user;

    BetterSpinner educationSpinner;
    BetterSpinner alcoholSpinner;
    BetterSpinner threatenSpinner;
    BetterSpinner welfareSpinner;
    BetterSpinner blameSpinner;
    BetterSpinner violenceSpinner;
    String[] EDUCATION_DATA = {"Primary School","Secondary School","High School","University"};
    String[] ALCOHOL_DATA = {"Never","Occasional","Moderate","Usual"};
    String[] THREATEN = {"Yes","No"};
    String[] WELFARE_DATA = {"Poor", "Middle Class", "Rich"};
    String[] BLAME_DATA = {"Yes", "No"};
    String[] VIOLENCE_DATA = {"Never","Occasional","Moderate","Usual"};
    DatabaseReference databaseUsers;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        ButterKnife.bind(this);


        educationSpinner = findViewById(R.id.educationS);
        alcoholSpinner = findViewById(R.id.alcoholS);
        threatenSpinner = findViewById(R.id.threatenS);
        welfareSpinner = findViewById(R.id.welfareS);
        blameSpinner = findViewById(R.id.blameS);
        violenceSpinner = findViewById(R.id.violenceS);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, EDUCATION_DATA);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, ALCOHOL_DATA);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, THREATEN);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, WELFARE_DATA);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, BLAME_DATA);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(SignupActivity3.this, android.R.layout.simple_dropdown_item_1line, VIOLENCE_DATA);
        educationSpinner.setAdapter(adapter1);
        alcoholSpinner.setAdapter(adapter2);
        threatenSpinner.setAdapter(adapter3);
        welfareSpinner.setAdapter(adapter4);
        blameSpinner.setAdapter(adapter5);
        violenceSpinner.setAdapter(adapter6);

        user = (User) getIntent().getSerializableExtra("user");

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                Intent intent = new Intent(SignupActivity3.this,MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }


    public void createUser() {
        btn_signup.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity3.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        int age = Integer.parseInt(_age.getText().toString());
        String education = educationSpinner.getText().toString();
        String alcohol = alcoholSpinner.getText().toString();
        String threaten = threatenSpinner.getText().toString();
        String welfare = welfareSpinner.getText().toString();
        String blame = blameSpinner.getText().toString();
        String violence = violenceSpinner.getText().toString();

        int ageGroup;
        if(age<29){
            ageGroup = 3;
        } else if(age>29 && age<40){
            ageGroup = 0;
        } else if(age>40 && age<55){
            ageGroup = 2;
        } else {
            ageGroup = 1;
        }


        int educationGroup;
        if(education.equals("Primary School")){
            educationGroup = 1;
        } else if(education.equals("Secondary Shool")){
            educationGroup = 2;
        } else if(education.equals("High School")){
            educationGroup = 0;
        } else {
            educationGroup = 3;
        }


        int alcoholGroup;
        if(alcohol.equals("Never")){
            alcoholGroup = 1;
        } else if(alcohol.equals("Occasional")){
            alcoholGroup = 2;
        } else if(alcohol.equals("Moderate")){
            alcoholGroup = 0;
        } else {
            alcoholGroup = 3;
        }


        int threatenGroup;
        if(threaten.equals("Yes")){
            threatenGroup = 1;
        } else {
            threatenGroup = 0;
        }


        int welfareGroup;
        if(welfare.equals("Poor")){
            welfareGroup = 1;
        } else if(welfare.equals("Mid Class")){
            welfareGroup = 0;
        } else {
            welfareGroup = 2;
        }


        int blameGroup;
        if(blame.equals("Yes")){
            blameGroup = 1;
        } else{
            blameGroup = 0;
        }


        int violenceGroup;
        if(violence.equals("Never")){
            violenceGroup = 1;
        } else if(violence.equals("Occasional")){
            violenceGroup = 2;
        } else if(violence.equals("Moderate")){
            violenceGroup = 0;
        } else {
            violenceGroup = 3;
        }

        try{
            user.setEducationS(educationGroup);
            user.setAgeS(ageGroup);
            user.setViolenceS(violenceGroup);
            user.setAlcoholS(alcoholGroup);
            user.setThreatenS(threatenGroup);
            user.setWelfareS(welfareGroup);
            user.setBlameS(blameGroup);

            int dangerLevel = predictDanger();
            user.setDangerLevel(dangerLevel);
            user.setRegitrationComplete(true);
            pushDatabase();
        } catch (Exception e){
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        progressDialog.dismiss();
    }

    public int predictDanger() {
        final int[] result = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("age-of-suspect", user.getAgeS());
                    jsonParam.put("education-level-of-suspect", user.getEducationS());
                    jsonParam.put("harmful-use-of-alcohol", user.getAlcoholS());
                    jsonParam.put("threatens-with-violence", user.getThreatenS());
                    jsonParam.put("welfare-level", user.getWelfareS());
                    jsonParam.put("blame", user.getBlameS());
                    jsonParam.put("violence-history", user.getBlameS());

                    URL url = new URL("https://dvd-flask-model.herokuapp.com/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                    osw.write(jsonParam.toString());
                    osw.flush();
                    osw.close();
                    os.close();
                    int a = conn.getResponseCode();
                    String b = conn.getResponseMessage();

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    sb.toString();
                    String context = "";
                    ArrayList<String> contexts = new ArrayList<String>();
                    JSONObject obj = new JSONObject(sb.toString());
                    result[0] =  new JSONObject(obj.get("results").toString()).getInt("results");
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    btn_signup.setEnabled(true);
                }
            }
        }
        );
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public void pushDatabase() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   Toast.makeText(SignupActivity3.this, "Database Successful", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignupActivity3.this, "Registeration Error", Toast.LENGTH_SHORT).show();
                    btn_signup.setEnabled(true);
                }
            }
        });

    }


}
