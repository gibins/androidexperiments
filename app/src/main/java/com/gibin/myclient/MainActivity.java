package com.gibin.myclient;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.gibin.myclient.bluetooth.BluetoothMainActivity;
import com.gibin.myclient.reminder.ReminderActivity;
import com.gibin.myclient.server.ServerActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.gibin.myclient.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        ListView lvMainList = findViewById(R.id.lvmain);

        /**
         * 0 : Reminder Activity
         */

        String[] lvMainData = new String[]{
                "One","Two","Three","Four",
                "Five","Six","Seven","Eight",
                "Nine","Ten"
        };
        ArrayAdapter<String> lvMainAdpter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,lvMainData);
        lvMainList.setAdapter(lvMainAdpter);

        lvMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplication(), "Selected "+i+" "+l+" "+lvMainList.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                if(i == 0)
                {
                    Intent toReminder = new Intent(MainActivity.this, ReminderActivity.class);
                    startActivity(toReminder);
                }

                if(i == 1)
                {
                    Intent toServer = new Intent(MainActivity.this, ServerActivity.class);
                    startActivity(toServer);
                }

                if(i == 2)
                {
                    Intent toBluetooth = new Intent(MainActivity.this, BluetoothMainActivity.class);
                    startActivity(toBluetooth);
                }



            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Snackbar.make(view, "Oh Not Implemented yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Toast.makeText(getApplicationContext(),"Oh Not Implemented yet",Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public boolean onSupportNavigateUp() {
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return true;
    }


}