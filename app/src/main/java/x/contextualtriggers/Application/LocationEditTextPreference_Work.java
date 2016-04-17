package x.contextualtriggers.Application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import x.contextualtriggers.R;

public class LocationEditTextPreference_Work extends EditTextPreference {
    static final private int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;
    private int mMinLength;

    public LocationEditTextPreference_Work(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.LocationEditTextPreference_Home, 0, 0);
        try {
            mMinLength = a.getInteger(R.styleable.LocationEditTextPreference_Home_minLength,
                    DEFAULT_MINIMUM_LOCATION_LENGTH);
        } finally {
            a.recycle();
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            // Add the get current location widget to our location preference
            setWidgetLayoutResource(R.layout.preference_current_location);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        View currentLocation = view.findViewById(R.id.current_location);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                // We are in a view right now, not an activity. So we need to get ourselves
                // an activity that we can use to start our Place Picker intent. By using
                // UserPreferenceActivity in this way, we can ensure the result of the Place
                // Picker intent comes to the right place for us to process it.
                Activity activity = (MainActivity.UserPreferenceActivity) context;
                // Fetch the request code id
                /*final Intent i = getIntent();
                final String dataString = i.getData().toString();
                int resCode = 0;
                try{
                    resCode = Integer.parseInt(dataString);
                } catch(NumberFormatException e) {
                    Log.d(getClass().getSimpleName(), "Data in intent should be a number!");}*/
                try {
                    activity.startActivityForResult(builder.build(activity),
                            MainActivity.UserPreferenceActivity.PLACE_PICKER_REQUEST_WORK);

                } catch (GooglePlayServicesNotAvailableException
                        | GooglePlayServicesRepairableException e) {
                }
            }
        });
        return view;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        EditText et = getEditText();
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog d = getDialog();
                if (d instanceof AlertDialog) {
                    AlertDialog dialog = (AlertDialog) d;
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    // Check if the EditText is empty
                    if (s.length() < mMinLength) {
                        // Disable OK button
                        positiveButton.setEnabled(false);
                    } else {
                        // Re-enable the button.
                        positiveButton.setEnabled(true);
                    }
                }
            }
        });
    }
}