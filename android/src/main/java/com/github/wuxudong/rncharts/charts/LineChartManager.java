package com.github.wuxudong.rncharts.charts;


import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.wuxudong.rncharts.data.DataExtract;
import com.github.wuxudong.rncharts.data.LineDataExtract;
import com.github.wuxudong.rncharts.listener.RNOnChartValueSelectedListener;
import com.github.wuxudong.rncharts.listener.RNOnChartScrollListener;
import com.github.wuxudong.rncharts.listener.RNOnChartGestureListener;

import java.util.Map;

public class LineChartManager extends BarLineChartBaseManager<LineChart, Entry> {

    protected LineChart mLineChart;

    @Override
    public String getName() {
        return "RNLineChart";
    }

    @Override
    protected LineChart createViewInstance(ThemedReactContext reactContext) {
        mLineChart = new LineChart(reactContext);
        if (mEnableSelectEvent) { // Hook up the listener only if needed
            mLineChart.setOnChartValueSelectedListener(new RNOnChartValueSelectedListener(mLineChart));
        }
        mLineChart.setOnChartGestureListener(new RNOnChartGestureListener(mLineChart));
        mLineChart.setOnChartScrollListener(new RNOnChartScrollListener(mLineChart));
        return mLineChart;
    }

    @Override
    DataExtract getDataExtract() {
        return new LineDataExtract();
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
            .put(
                "scrollStart",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onChartScroll")))
            .put(
                "scrollEnd",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onChartScroll")))
                    .build();
    }
}
