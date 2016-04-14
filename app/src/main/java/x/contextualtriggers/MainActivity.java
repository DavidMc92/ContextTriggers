package x.contextualtriggers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import x.contextualtriggers.Services.AccelerometerService;
import x.contextualtriggers.Services.BarometerService;
import x.contextualtriggers.Triggers.TriggerManager;

public class MainActivity extends AppCompatActivity {

    private Switch accSwitch, barSwitch;

    private TriggerManager triggerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.triggerManager = new TriggerManager();
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
                        barSwitch.isChecked());
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
                        isChecked);
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
    protected void onDestroy() {
        super.onDestroy();
        this.triggerManager.checkTriggerConditions(getApplicationContext(),
                false,
                false);
    }
}
