/*  Copyright (C) 2015-2020 Andreas Shimokawa, Carsten Pfeiffer, Cre3per,
    Daniele Gobbetti, Dikay900, Pavel, Pavel Elagin

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package dev.makhanov.test.activities.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import dev.makhanov.test.GBApplication;
import dev.makhanov.test.R;
import dev.makhanov.test.activities.HeartRateUtils;
import dev.makhanov.test.database.DBHandler;
import dev.makhanov.test.impl.GBDevice;
import dev.makhanov.test.model.ActivitySample;
import dev.makhanov.test.model.DeviceService;

public class LiveActivityFragment extends AbstractChartFragment {
    private TextView mMaxHeartRateView;

    private ScheduledExecutorService pulseScheduler;
    private int mHeartRate;
    private int mMaxHeartRate = 0;
    private TimestampTranslation tsTranslation;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case DeviceService.ACTION_REALTIME_SAMPLES: {
                    ActivitySample sample = (ActivitySample) intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE);
                    addSample(sample);
                    break;
                }
            }
        }
    };

    private void addSample(ActivitySample sample) {
        int heartRate = sample.getHeartRate();
        int timestamp = tsTranslation.shorten(sample.getTimestamp());
        if (HeartRateUtils.getInstance().isValidHeartRateValue(heartRate)) {
            setCurrentHeartRate(heartRate, timestamp);
        }
    }
    private int translateTimestamp(long tsMillis) {
        int timestamp = (int) (tsMillis / 1000); // translate to seconds
        return tsTranslation.shorten(timestamp); // and shorten
    }

    private void setCurrentHeartRate(int heartRate, int timestamp) {
        addHistoryDataSet(true);
        mHeartRate = heartRate;
        if (mMaxHeartRate < mHeartRate) {
            mMaxHeartRate = mHeartRate;
        }
        mMaxHeartRateView.setText(heartRate + "");
    }

    private int getCurrentHeartRate() {
        int result = mHeartRate;
        mHeartRate = -1;
        return result;
    }

    private void addEntries(int timestamp) {



        if (!addHistoryDataSet(false)) {
            return;
        }

        int hr = getCurrentHeartRate();
        if (hr > HeartRateUtils.getInstance().getMinHeartRate()) {
        }
    }

    private boolean addHistoryDataSet(boolean force) {

        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        tsTranslation = new TimestampTranslation();

        View rootView = inflater.inflate(R.layout.fragment_live_activity, container, false);


        mMaxHeartRateView = rootView.findViewById(R.id.livechart_max_heart_rate);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filterLocal);

        return rootView;
    }

    @Override
    public void onPause() {
        enableRealtimeTracking(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableRealtimeTracking(true);
    }

    private ScheduledExecutorService startActivityPulse() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = LiveActivityFragment.this.getActivity();
                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pulse();
                        }
                    });
                }
            }
        }, 0, getPulseIntervalMillis(), TimeUnit.MILLISECONDS);
        return service;
    }

    private void stopActivityPulse() {
        if (pulseScheduler != null) {
            pulseScheduler.shutdownNow();
            pulseScheduler = null;
        }
    }

    /**
     * Called in the UI thread.
     */
    private void pulse() {
        addEntries(translateTimestamp(System.currentTimeMillis()));
        renderCharts();
        // have to enable it again and again to keep it measuring
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(true);
    }

    private int getPulseIntervalMillis() {
        return 1000;
    }

    @Override
    protected void onMadeVisibleInActivity() {
        super.onMadeVisibleInActivity();
        enableRealtimeTracking(true);
    }

    private void enableRealtimeTracking(boolean enable) {
        if (enable && pulseScheduler != null) {
            // already running
            return;
        }

        GBApplication.deviceService().onEnableRealtimeSteps(enable);
        GBApplication.deviceService().onEnableRealtimeHeartRateMeasurement(enable);
        if (enable) {
            if (getActivity() != null) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            pulseScheduler = startActivityPulse();
        } else {
            stopActivityPulse();
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    @Override
    protected void onMadeInvisibleInActivity() {
        enableRealtimeTracking(false);
        super.onMadeInvisibleInActivity();
    }

    @Override
    public void onDestroyView() {
        onMadeInvisibleInActivity();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.liveactivity_live_activity);
    }

    @Override
    protected void showDateBar(boolean show) {
        // never show the data bar
        super.showDateBar(false);
    }

    @Override
    protected void refresh() {
        // do nothing, we don't have any db interaction
    }

    @Override
    protected ChartsData refreshInBackground(ChartsHost chartsHost, DBHandler db, GBDevice device) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateChartsnUIThread(ChartsData chartsData) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void renderCharts() {
    }

    @Override
    protected List<ActivitySample> getSamples(DBHandler db, GBDevice device, int tsFrom, int tsTo) {
        throw new UnsupportedOperationException("no db access supported for live activity");
    }

    @Override
    protected void setupLegend(Chart chart) {
        // no legend
    }
}
