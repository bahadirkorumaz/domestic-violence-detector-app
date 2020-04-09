package com.agu.domesticviolencedetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    TextView uid;
    TextView title;
    TextView numberOfNegative;
    Button btnLogout;
    Button btnUpdate;
    User curUser;
    String userId;
    long DAY_IN_MS = 86400000;
    int nWeek,nMonth,n3Month,n6Month, nAll;
    double rWeek,rMonth,r3Month,r6Month, rAll;
    User user;

    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.title);
        btnLogout = findViewById(R.id.btn_logout);
        btnUpdate = findViewById(R.id.btn_update);
        title = findViewById(R.id.title);
        numberOfNegative = findViewById(R.id.numberOfNegative);

        userId = getUid();
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateAnalysis();
               calculateDanger();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void calculateDanger() {
        int dangerLevel = user.getDangerLevel();
        double probabilityDanger;

        if(dangerLevel==3) {
            probabilityDanger=0.0;
        } else if(dangerLevel==1){
            probabilityDanger=0.33;
        } else if(dangerLevel==0){
            probabilityDanger=0.66;
        } else {
            probabilityDanger=1.0;
        }

        double increaseWeight=0;
        if(r3Month==-1 || r6Month ==-1 || rMonth== -1 || rWeek==-1){
            increaseWeight = probabilityDanger * 25;
        } else {
            if( r3Month==Math.max(r3Month, r6Month)){
                increaseWeight+=10;
            }else if( rMonth==Math.max(r3Month, rMonth)){
                increaseWeight+=16;
            }else if( rWeek==Math.max(rWeek, rMonth)){
                increaseWeight+=24;
            }
        }

        double likelihood = (( probabilityDanger * 50) + increaseWeight);


        numberOfNegative.setText(Double.toString(likelihood)+"%");


    }

    public void updateAnalysis() {
        getNegativeResults();
        numberOfNegative.setText(String.valueOf(getNegativeResults()));
        calculateAnalysis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.child(userId).getValue(User.class);
                if(user!=null) {
                    if(user.isRegitrationComplete()){
                        user.setnWeek(nWeek);
                        user.setnMonth(nMonth);
                        user.setN3Month(n3Month);
                        user.setN6Month(n6Month);
                        user.setrWeek(rWeek);
                        user.setrMonth(rMonth);
                        user.setR3Month(r3Month);
                        user.setR6Month(r6Month);
                        updateUser(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUser(User user) {
        DatabaseReference dataReferenceToChange = databaseUsers.child(userId);
        dataReferenceToChange.setValue(user);
    }

    private void calculateAnalysis() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving and Analyzing...");
        progressDialog.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                nWeek = getLastOneWeek("negative");
                nMonth = getLastOneMonth("negative");
                n3Month = getLastThreeMonths("negative");
                n6Month = getLastSixMonths("negative");
                nAll = getNegativeResults();

                if(getLastOneWeek("all")==0){rWeek=-1;}
                else{rWeek = ((double) nWeek / (double) getLastOneWeek("all"));}

                if(getLastOneMonth("all")==0){rMonth=-1;}
                else{rMonth = ((double) nMonth / (double) getLastOneMonth("all"));}

                if(getLastThreeMonths("all")==0){ r3Month=-1;}
                else{ r3Month = ((double) n3Month / (double)getLastThreeMonths("all"));}

                if(getLastSixMonths("all")==0){rWeek=-1;}
                else{r6Month = ((double) n6Month / (double) getLastSixMonths("all"));}
                progressDialog.dismiss();

                user.setnWeek(nWeek);
                user.setnMonth(nMonth);
                user.setN3Month(n3Month);
                user.setN6Month(n6Month);
                user.setrWeek(rWeek);
                user.setrMonth(rMonth);
                user.setR3Month(r3Month);
                user.setR6Month(r6Month);
                updateUser(user);
            }
        };
        mThread.start();
    }

    public int getLastOneWeek(String bool) {
        Date date = new Date();
        String endingDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
        String startingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (7 * DAY_IN_MS)));
        return getBetweenDate(bool, startingDate,endingDate);
    }

    public int getLastOneMonth(String bool) {
        Date date = new Date();
        String endingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (7 * DAY_IN_MS)));
        String startingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (30 * DAY_IN_MS)));
        return getBetweenDate(bool, startingDate,endingDate);
    }

    public int getLastThreeMonths(String bool) {
        Date date = new Date();
        String endingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (30 * DAY_IN_MS)));
        String startingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (90 * DAY_IN_MS)));
        return getBetweenDate(bool, startingDate,endingDate);
    }

    public int getLastSixMonths(String bool) {
        Date date = new Date();
        String endingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (90 * DAY_IN_MS)));
        String startingDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date(date.getTime() - (180 * DAY_IN_MS)));
        return getBetweenDate(bool, startingDate,endingDate);
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getNegativeResults() {
        final int[] size = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL newUrl = new URL("http://domesticviolenceapi.azurewebsites.net/api/textanalysis/user/negative");
                    HttpURLConnection con1 = (HttpURLConnection) newUrl.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json; utf-8");
                    con1.setRequestProperty("userId", FirebaseAuth.getInstance().getUid());

                    InputStreamReader is = new InputStreamReader((con1.getInputStream()));

                    try{
                        con1.getInputStream();
                    } catch (Exception e){
                    }

                    BufferedReader br = new BufferedReader(is);

                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    String context = "";
                    ArrayList<String> contexts = new ArrayList<String>();
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONArray arr = obj.getJSONArray("textAnalysesInfo");
                    size[0] = arr.length();
                    numberOfNegative.setText(size[0]);
                } catch (Exception e) {
                    e.printStackTrace();
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
        return size[0];
    }


    public int getAllResults() {
        final int[] size = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL newUrl = new URL("http://domesticviolenceapi.azurewebsites.net/api/textanalysis/user/");
                    HttpURLConnection con1 = (HttpURLConnection) newUrl.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json; utf-8");
                    con1.setRequestProperty("userId", FirebaseAuth.getInstance().getUid());

                    BufferedReader br = new BufferedReader(new InputStreamReader((con1.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    String context = "";
                    ArrayList<String> contexts = new ArrayList<String>();
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONArray arr = obj.getJSONArray("textAnalysesInfo");
                    size[0] = arr.length();
                    numberOfNegative.setText(size[0]);
                } catch (Exception e) {
                    e.printStackTrace();
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
        return size[0];
    }

    public int getBetweenDate(final String bool, final String startingDate, final String endingDate) {
        final int[] size = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL newUrl = new URL("http://domesticviolenceapi.azurewebsites.net/api/textanalysis/user/dates");
                    HttpURLConnection con1 = (HttpURLConnection) newUrl.openConnection();
                    con1.setRequestMethod("GET");
                    con1.setRequestProperty("Content-Type", "application/json; utf-8");
                    con1.setRequestProperty("userId", FirebaseAuth.getInstance().getUid());
                    con1.setRequestProperty("startingDate", startingDate);       //
                    con1.setRequestProperty("EndingDate",endingDate);        //12-22
                    if(bool.equals("negative"))
                        con1.setRequestProperty("negative","true");

                    BufferedReader br = new BufferedReader(new InputStreamReader((con1.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    String context = "";
                    ArrayList<String> contexts = new ArrayList<String>();
                    JSONObject obj = new JSONObject(sb.toString());
                    JSONArray arr = obj.getJSONArray("textAnalysesInfo");
                    size[0] = arr.length();
                    numberOfNegative.setText(size[0]);
                } catch (Exception e) {
                    e.printStackTrace();
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
        return size[0];
    }
}
