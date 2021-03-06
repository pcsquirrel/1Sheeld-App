package com.integreight.onesheeld.shields.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.integreight.onesheeld.R;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.shields.controller.GpsShield;
import com.integreight.onesheeld.shields.controller.GpsShield.GpsEventHandler;
import com.integreight.onesheeld.utils.Log;

public class GpsFragment extends ShieldFragmentParent<GpsFragment> {
    TextView Latit, Longit;
    Button startGps, stopGps;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.gps_shield_fragment_layout, container,
                false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onStart() {
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            if (!reInitController())
                return;
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.d("Gps Sheeld::OnActivityCreated()", "");

        Latit = (TextView) v.findViewById(R.id.lat_value_txt);
        Longit = (TextView) v.findViewById(R.id.lang_value_txt);
        startGps = (Button) v.findViewById(R.id.start_listener_bt);
        stopGps = (Button) v.findViewById(R.id.stop_listener_bt);

        startGps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((GpsShield) getApplication().getRunningShields().get(
                        getControllerTag()))
                        .isGooglePlayServicesAvailableWithDialog();

            }
        });

        stopGps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((GpsShield) getApplication().getRunningShields().get(
                        getControllerTag())).stopGps();

            }
        });

    }

    private GpsEventHandler gpsEventHandler = new GpsEventHandler() {

        @Override
        public void onLatChanged(final String lat) {
            // TODO Auto-generated method stub
            Latit.post(new Runnable() {

                @Override
                public void run() {
                    if (canChangeUI())
                        Latit.setText("Latitude\n" + lat);
                }
            });
        }

        @Override
        public void onLangChanged(final String lang) {
            // TODO Auto-generated method stub
            Longit.post(new Runnable() {

                @Override
                public void run() {
                    if (canChangeUI())
                        Longit.setText("Longitude\n" + lang);
                }
            });
        }
    };

    private void initializeFirmata() {
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            getApplication().getRunningShields().put(getControllerTag(),
                    new GpsShield(activity, getControllerTag()));

        }

    }

    public void doOnServiceConnected() {
        initializeFirmata();
    }

    ;

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ((GpsShield) getApplication().getRunningShields().get(
                getControllerTag())).setGpsEventHandler(gpsEventHandler);
        ((GpsShield) getApplication().getRunningShields().get(
                getControllerTag())).isGooglePlayServicesAvailableWithDialog();

    }

}
