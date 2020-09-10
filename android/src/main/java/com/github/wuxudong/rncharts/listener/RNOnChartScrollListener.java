package com.github.wuxudong.rncharts.listener;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.OnChartScrollListener;

import java.lang.ref.WeakReference;

/**
 * Created by xudong on 07/03/2017.
 */
public class RNOnChartScrollListener implements OnChartScrollListener {

    private WeakReference<Chart> mWeakChart;

    public RNOnChartScrollListener(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    @Override
    public void onScrollStart() {
        if (mWeakChart != null) {
            Chart chart = mWeakChart.get();
            String eventName = "scrollStart";
            ReactContext reactContext = (ReactContext) chart.getContext();
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    chart.getId(),
                    eventName,
                    getEvent(eventName));
        }
    }

    @Override
    public void onScrollEnd() {
        if (mWeakChart != null) {
            Chart chart = mWeakChart.get();
            String eventName = "scrollEnd";
            ReactContext reactContext = (ReactContext) chart.getContext();
            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    chart.getId(),
                    eventName,
                    getEvent(eventName));
        }

    }

    private WritableMap getEvent(String eventName) {
        WritableMap event = Arguments.createMap();
        event.putString("action", eventName);
        return event;
    }

}
