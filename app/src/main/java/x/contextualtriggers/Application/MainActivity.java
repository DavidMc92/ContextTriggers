package x.contextualtriggers.Application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;

import x.contextualtriggers.R;
import x.contextualtriggers.Services.AccelerometerService;
import x.contextualtriggers.Services.BarometerService;
import x.contextualtriggers.Services.WeatherService;
import x.contextualtriggers.Triggers.TriggerManager;

public class MainActivity extends AppCompatActivity {

    private Switch accSwitch, barSwitch, weatherSwitch;
    private AlarmManager alarmManager;

    private TriggerManager triggerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.triggerManager = new TriggerManager();
        this.alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        accSwitch = (Switch) findViewById(R.id.switchAcc);
        accSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(new Intent(getApplicationContext(),
                            AccelerometerService.class));
                }
                else{
                    stopService(new Intent(getApplicationContext(), AccelerometerService.class));
                }
                triggerManager.checkTriggerConditions(getApplicationContext(),
                        isChecked,
                        barSwitch.isChecked(),
                        weatherSwitch.isChecked());
            }
        });

        barSwitch = (Switch) findViewById(R.id.switchBar);
        barSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(new Intent(getApplicationContext(),
                            BarometerService.class));
                }
                else{
                    stopService(new Intent(getApplicationContext(), BarometerService.class));
                }
                triggerManager.checkTriggerConditions(getApplicationContext(),
                        accSwitch.isChecked(),
                        isChecked,
                        weatherSwitch.isChecked());
            }
        });

        weatherSwitch = (Switch) findViewById(R.id.switchWeather);
        weatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(new Intent(getApplicationContext(),
                            WeatherService.class));
                    // TODO Set proper time, i.e. AlarmManager.INTERVAL_FIFTEEN_MINUTES
                    // Use this to schedule a child of BackgroundService
                    scheduleAlarm(WeatherService.class, 500);
                }
                else{
                    stopService(new Intent(getApplicationContext(), WeatherService.class));
                    unscheduleAlarm(WeatherService.class);
                }
                triggerManager.checkTriggerConditions(getApplicationContext(),
                        accSwitch.isChecked(),
                        barSwitch.isChecked(),
                        isChecked);
            }
        });
    }

    private void scheduleAlarm(Class clz, long period){
        if(this.alarmManager != null) {
            final Intent intent = new Intent(getApplicationContext(), clz);
            final PendingIntent pi = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            long trigger = System.currentTimeMillis() + 500;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, trigger, period, pi);
        }
    }

    private void unscheduleAlarm(Class clz){
        if(this.alarmManager != null){
            final Intent intent = new Intent(getApplicationContext(), clz);
            final PendingIntent pi = PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pi);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // e.g user rotates phone
    @Override
    protected void onResume() {
        super.onResume();
        this.triggerManager.checkTriggerConditions(getApplicationContext(),
                accSwitch.isChecked(),
                barSwitch.isChecked(),
                weatherSwitch.isChecked());
    }

    // For destroying broadcast receivers + preventing dangling references
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.triggerManager.checkTriggerConditions(getApplicationContext(),
                false,
                false,
                false);
    }
}
