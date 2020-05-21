package com.github.wuxudong.rncharts.markers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;
import java.util.Map;

public class RNPriceMarkerView extends RNMarkerView {

    private String positiveColor;
    private String negativeColor;

    public RNPriceMarkerView(Context context, String positiveColor, String negativeColor) {
        super(context);

        this.positiveColor = positiveColor;
        this.negativeColor = negativeColor;
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

                if (marker instanceof Map) {
                    Map markerData = (Map) marker;
                    try {
                        String entity = markerData.get("entity").toString();
                        String price = markerData.get("price").toString();
                        String priceDiff = markerData.get("priceDiff").toString();
                        String dateTime = markerData.get("dateTime").toString();
                        String direction = markerData.get("direction").toString();
                        String color = direction.equals("positive") ? this.positiveColor
                                : direction.equals("negative") ? this.negativeColor : "inherit";

                        spannedText = Html.fromHtml(
                                "<span>" + entity + " </span>" + "<b>" + price + "</b><br>" + "<span style=\"color: "
                                        + color + "\">" + priceDiff + "</span><br>" + "<span>" + dateTime + "</span>");
                    } catch (NullPointerException npe) {
                        // If something is not present, fall back to toString
                        text = marker.toString();
                    }
                } else {
                    text = marker.toString();
                }

                if (highlight.getStackIndex() != -1 && marker instanceof List) {
                    text = ((List) marker).get(highlight.getStackIndex()).toString();
                }

            }
        }

        TextView tvContent = getTvContent();
        if (TextUtils.isEmpty(text)) {
            tvContent.setVisibility(INVISIBLE);
        } else {
            tvContent.setText(spannedText != null ? spannedText : text);
            tvContent.setVisibility(VISIBLE);
        }

        super.refreshContent(e, highlight);
    }
}
