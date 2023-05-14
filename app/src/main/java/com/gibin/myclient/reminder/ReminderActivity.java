package com.gibin.myclient.reminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.gibin.myclient.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.gibin.myclient.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private TextView tvCurrentTime, tvTitle;
    private EditText edSetTime;
    private Button btnSetTime,btnBack,btnStart;

    AlertDialog.Builder builder;

    private final String CURRENT_TIME = "Current Time : ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminderlayout);

        tvCurrentTime = (TextView) findViewById(R.id.tvcurrenttime);
        tvTitle = (TextView) findViewById(R.id.tvtitle);
        edSetTime = (EditText) findViewById(R.id.editTextTime);
        btnSetTime = (Button) findViewById(R.id.btntime);
        btnBack = (Button) findViewById(R.id.btnback);
        btnStart = (Button) findViewById(R.id.btnstart);

        updateTimer();

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Set Time Interval",Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Back to Main Menu",Toast.LENGTH_LONG).show();
                Intent toHome = new Intent(ReminderActivity.this, MainActivity.class);
                startActivity(toHome);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compareLogic(edSetTime.getText().toString());
            }
        });




    }

    public void updateTimer()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvCurrentTime.setText(CURRENT_TIME + new Date().toString());
                    }
                });
            }
        }, 1000, 1000);
    }

    public void compareLogic(String targetTime)
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String currentDateTime = sdf.format(new Date());


                        String[] hourMin = targetTime.split(":");

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.HOUR, Integer.parseInt(hourMin[0]));
                        calendar.add(Calendar.MINUTE, Integer.parseInt(hourMin[1]));

                        String endingTime = sdf.format(  calendar.getTime());

                        Toast.makeText(getApplicationContext(),"You get notification in "+endingTime,Toast.LENGTH_LONG).show();

                        if(currentDateTime.equals(endingTime))
                        {
                            timer.purge();

                        builder = new AlertDialog.Builder(ReminderActivity.this);
                        builder.setTitle("Break Time").setMessage("You Need a Break");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("AlertDialogExample");
                        alert.show();

                        }


                    }
                });
            }
        }, 1000, 1000);
    }

}