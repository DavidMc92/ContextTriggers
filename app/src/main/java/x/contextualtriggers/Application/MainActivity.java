package x.contextualtriggers.Application;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x.contextualtriggers.R;
import x.contextualtriggers.Triggers.ElevatorDetectorTrigger;
import x.contextualtriggers.Triggers.ITrigger;
import x.contextualtriggers.Triggers.LunchTimeLocatorTrigger;
import x.contextualtriggers.Triggers.PedometerPrompterTrigger;
import x.contextualtriggers.Triggers.RouteRecommenderTrigger;
import x.contextualtriggers.Triggers.TriggerManager;

public class MainActivity extends AppCompatActivity {
    private static final String ACTIVITY_PREFERENCES =
            "x.contextualtriggers.Application.MainActivity$UserPreferenceActivity";


    private Switch elevSwitch, lunchSwitch, pedSwitch, routeSwitch;

    private TriggerManager triggerManager;
    private Map<Integer, ITrigger> triggerMap; // TODO Change to Set; need to lookup proper equals()

    private List<ITrigger> triggers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.triggerManager = new TriggerManager(this);
        this.triggerMap = new HashMap<>();

        this.elevSwitch = (Switch) findViewById(R.id.switchElev);
        this.elevSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !triggerMap.containsKey(0)){
                    final ITrigger trigger = new ElevatorDetectorTrigger(getApplicationContext());
                    triggerManager.enableTrigger(trigger);
                    triggerMap.put(0, trigger);
                }
                else if(triggerMap.containsKey(0)){
                    final ITrigger trigger = triggerMap.remove(0);
                    triggerManager.disableTrigger(trigger);
                }
            }
        });

        this.lunchSwitch = (Switch) findViewById(R.id.switchLunch);
        this.lunchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !triggerMap.containsKey(1)){
                    final ITrigger trigger = new LunchTimeLocatorTrigger(getApplicationContext());
                    triggerManager.enableTrigger(trigger);
                    triggerMap.put(1, trigger);
                }
                else if(triggerMap.containsKey(1)){
                    final ITrigger trigger = triggerMap.remove(1);
                    triggerManager.disableTrigger(trigger);
                }
            }
        });

        this.pedSwitch = (Switch) findViewById(R.id.switchPed);
        this.pedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !triggerMap.containsKey(2)){
                    final ITrigger trigger = new PedometerPrompterTrigger(getApplicationContext());
                    triggerManager.enableTrigger(trigger);
                    triggerMap.put(2, trigger);
                }
                else if(triggerMap.containsKey(2)){
                    final ITrigger trigger = triggerMap.remove(2);
                    triggerManager.disableTrigger(trigger);
                }
            }
        });

        this.routeSwitch = (Switch) findViewById(R.id.switchRoute);
        this.routeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !triggerMap.containsKey(3)){
                    final ITrigger trigger = new RouteRecommenderTrigger(getApplicationContext());
                    triggerManager.enableTrigger(trigger);
                    triggerMap.put(3, trigger);
                }
                else if(triggerMap.containsKey(3)){
                    final ITrigger trigger = triggerMap.remove(3);
                    triggerManager.disableTrigger(trigger);
                }
            }
        });
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
    }

    // For destroying broadcast receivers + preventing dangling references
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ret;
        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent().setClassName(this, ACTIVITY_PREFERENCES);
                startActivity(intent);
                ret = true;
                break;

            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }

    public static class UserPreferenceActivity extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new UserPreferenceFragment()).commit();
        }

        public static class UserPreferenceFragment extends PreferenceFragment {
            @Override
            public void onCreate(final Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.user_settings);
            }
        }
    }
}
