package x.contextualtriggers.Application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
}
