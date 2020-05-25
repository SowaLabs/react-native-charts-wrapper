package com.github.wuxudong.rncharts.markers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.wuxudong.rncharts.R;

public abstract class RNMarkerView extends MarkerView {

    TextView tvContent;
    int digits = 0;

    private Drawable backgroundLeft = ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_marker_left,
            null);
    private Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_marker, null);
    private Drawable backgroundRight = ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_marker_right,
            null);

    private Drawable backgroundTopLeft = ResourcesCompat.getDrawable(getResources(),
            R.drawable.rectangle_marker_top_left, null);
    private Drawable backgroundTop = ResourcesCompat.getDrawable(getResources(), R.drawable.rectangle_marker_top, null);
    private Drawable backgroundTopRight = ResourcesCompat.getDrawable(getResources(),
            R.drawable.rectangle_marker_top_right, null);

    public RNMarkerView(Context context) {
        super(context, R.layout.rounded_marker);

        tvContent = (TextView) findViewById(R.id.rounded_tvContent);
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {

        MPPointF offset = getOffset();

        MPPointF offset2 = new MPPointF();

        Chart chart = getChartView();
        offset2.x = offset.x;
        offset2.y = chart != null ? chart.getHeight() - (posY + chart.getHeight()) : offset.y;
        float width = getWidth();

        if (posX + offset2.x < 0) {
            offset2.x += -(posX + offset2.x);
        } else if (chart != null && posX + width + offset2.x > chart.getWidth()) {
            offset2.x -= (posX + width + offset2.x) - chart.getWidth();
        }

        return offset2;
    }

    public TextView getTvContent() {
        return tvContent;
    }
}
