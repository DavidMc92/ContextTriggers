package x.contextualtriggers.Application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

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

    private PreferenceContainer prefs;

    private List<ITrigger> triggers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.triggerManager = new TriggerManager(this);
        this.triggerMap = new HashMap<>();

        this.prefs = PreferenceContainer.getInstance(this);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(prefs);

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
                if(prefs.getHomeAddress().equals("") ||  prefs.getWorkAddress().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Please specify your work and home locations using the Settings!",
                            Toast.LENGTH_SHORT).show();
                    routeSwitch.setChecked(false);
                }
                else {
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
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(prefs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean ret;
        switch(id){
            case R.id.action_settings:
                startActivity(new Intent().setClassName(this, ACTIVITY_PREFERENCES));
                ret = true;
                break;

            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }

    public static class UserPreferenceActivity extends PreferenceActivity {
        // Coupled to Intents declared in XML
        public static int PLACE_PICKER_REQUEST_HOME = 1000,
                            PLACE_PICKER_REQUEST_WORK = 1001;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new UserPreferenceFragment()).commit();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == PLACE_PICKER_REQUEST_HOME || requestCode == PLACE_PICKER_REQUEST_WORK){
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    if(place != null){
                        String address = place.getAddress().toString();
                        final LatLng ltlg = place.getLatLng();

                        if (TextUtils.isEmpty(address)) {
                            address = String.format("(%.2f, %.2f)",ltlg.latitude, ltlg.longitude);
                        }

                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        int addrKey, latKey, longKey;
                        if(requestCode == PLACE_PICKER_REQUEST_HOME){
                            addrKey = R.string.preference_home_key;
                            latKey = R.string.preference_home_lat;
                            longKey = R.string.preference_home_long;
                        }
                        else{
                            addrKey = R.string.preference_work_key;
                            latKey = R.string.preference_work_lat;
                            longKey = R.string.preference_work_long;
                        }

                        editor.putString(getString(addrKey), address);
                        editor.putFloat(getString(latKey), (float)ltlg.latitude);
                        editor.putFloat(getString(longKey), (float)ltlg.longitude);
                        editor.commit();
                    }
                }

            }
            super.onActivityResult(requestCode, resultCode, data);
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
