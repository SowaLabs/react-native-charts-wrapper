package com.github.wuxudong.rncharts.markers;

import android.content.Context;
import android.text.Spanned;
import android.text.TextUtils;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;
import java.util.Map;

public class RNRectangleMarkerView extends RNMarkerView {

    public RNRectangleMarkerView(Context context) {
        super(context);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String text;
        Spanned spannedText = null;

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            text = Utils.formatNumber(ce.getClose(), digits, false);
        } else {
            text = Utils.formatNumber(e.getY(), digits, false);
        }

        if (e.getData() instanceof Map) {
            if (((Map) e.getData()).containsKey("marker")) {

                Object marker = ((Map) e.getData()).get("marker");
                text = marker.toString();

                if (highlight.getStackIndex() != -1 && marker instanceof List) {
                    text = ((List) marker).get(highlight.getStackIndex()).toString();
                }

            }
        }

        if (TextUtils.isEmpty(text)) {
            tvContent.setVisibility(INVISIBLE);
        } else {
            tvContent.setText(text);
            tvContent.setVisibility(VISIBLE);
        }

        super.refreshContent(e, highlight);
    }
}
