package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WeatherDataFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        double cloudCover = getArguments().getDouble("cloudCover");
        int precipitation = getArguments().getInt("precipitation");
        double humidity = getArguments().getDouble("humidity");

        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        TextView chartTitle = view.findViewById(R.id.chart_title);
        chartTitle.setText("Weather Data");

        Log.d("What happened", "onCreateView: ");
        HIChartView chartView = view.findViewById(R.id.highcharts_view);
        chartView.theme = "brand-light";
        HIOptions options = new HIOptions();

        // Configure chart
        HIChart chart = new HIChart();
        chart.setType("solidgauge");
        chart.setEvents(new HIEvents());
        chart.getEvents().setRender(new HIFunction(renderIconsString));
        options.setChart(chart);

        // Configure title
        HITitle title = new HITitle();
        title.setText("Stat Summary");
        HICSSObject titleStyle = new HICSSObject();
        titleStyle.setColor(HIColor.initWithRGB(111, 111,111)); // Set text color to grey
        titleStyle.setFontSize("20px");
        title.setStyle(titleStyle);

        options.setTitle(title);

        HITooltip tooltip = new HITooltip();
        tooltip.setBorderWidth(0);
        tooltip.setBackgroundColor(HIColor.initWithName("none"));
        tooltip.setStyle(new HICSSObject());
        tooltip.getStyle().setFontSize("16px");
        tooltip.setPointFormat("{series.name}<br><span style=\"font-size:2em; color: {point.color}; font-weight: bold\">{point.y}%</span>");
        tooltip.setPositioner(
                new HIFunction(
                        "function (labelWidth) {" +
                                "   return {" +
                                "       x: (this.chart.chartWidth - labelWidth) /2," +
                                "       y: (this.chart.plotHeight / 2) + 15" +
                                "   };" +
                                "}"
                ));
        options.setTooltip(tooltip);

        HIPane pane = new HIPane();
        pane.setStartAngle(0);
        pane.setEndAngle(360);

        HIBackground paneBackground1 = new HIBackground();
        paneBackground1.setOuterRadius("112%");
        paneBackground1.setInnerRadius("88%");
        paneBackground1.setBackgroundColor(HIColor.initWithRGBA(157, 235, 123, 0.35));
        paneBackground1.setBorderWidth(0);

        HIBackground paneBackground2 = new HIBackground();
        paneBackground2.setOuterRadius("87%");
        paneBackground2.setInnerRadius("63%");
        paneBackground2.setBackgroundColor(HIColor.initWithRGBA(119, 163, 226, 0.35));
        paneBackground2.setBorderWidth(0);

        HIBackground paneBackground3 = new HIBackground();
        paneBackground3.setOuterRadius("62%");
        paneBackground3.setInnerRadius("38%");
        paneBackground3.setBackgroundColor(HIColor.initWithRGBA(239, 136, 102, 0.35));
        paneBackground3.setBorderWidth(0);

        pane.setBackground(new ArrayList<>(Arrays.asList(paneBackground1, paneBackground2, paneBackground3)));
        options.setPane(pane);

        HIYAxis yaxis = new HIYAxis();
        yaxis.setMin(0);
        yaxis.setMax(100);
        yaxis.setLineWidth(0);
        yaxis.setTickPositions(new ArrayList<>()); // to remove ticks from the chart
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HIPlotOptions plotOptions = new HIPlotOptions();
        plotOptions.setSolidgauge(new HISolidgauge());
        plotOptions.getSolidgauge().setDataLabels(new ArrayList<>(Collections.singletonList(new HIDataLabels())));
        plotOptions.getSolidgauge().setLinecap("round");
        plotOptions.getSolidgauge().setStickyTracking(false);
        plotOptions.getSolidgauge().setRounded(true);
        options.setPlotOptions(plotOptions);

        HISolidgauge solidgauge1 = new HISolidgauge();
        solidgauge1.setName("Move");
        HIData data1 = new HIData();
        data1.setColor(HIColor.initWithRGB(157, 235, 123));
        data1.setRadius("112%");
        data1.setInnerRadius("88%");
        data1.setY(cloudCover);
        solidgauge1.setData(new ArrayList<>(Collections.singletonList(data1)));

        HISolidgauge solidgauge2 = new HISolidgauge();
        solidgauge2.setName("Exercise");
        HIData data2 = new HIData();
        data2.setColor(HIColor.initWithRGB(119, 163, 226));
        data2.setRadius("87%");
        data2.setInnerRadius("63%");
        data2.setY(precipitation);
        solidgauge2.setData(new ArrayList<>(Collections.singletonList(data2)));

        HISolidgauge solidgauge3 = new HISolidgauge();
        solidgauge3.setName("Stand");
        HIData data3 = new HIData();
        data3.setColor(HIColor.initWithRGB(239, 136, 102));
        data3.setRadius("62%");
        data3.setInnerRadius("38%");
        data3.setY(humidity);
        solidgauge3.setData(new ArrayList<>(Collections.singletonList(data3)));

        options.setSeries(new ArrayList<>(Arrays.asList(solidgauge1, solidgauge2, solidgauge3)));

        // Attach options to chart view
        chartView.setOptions(options);

        return view;
    }

    private String renderIconsString = "function renderIcons() {" +
            "                            if(!this.series[0].icon) {" +
            "                               this.series[0].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[0].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[0].points[0].shapeArgs.innerR -(this.series[0].points[0].shapeArgs.r - this.series[0].points[0].shapeArgs.innerR) / 2); if(!this.series[1].icon) {this.series[1].icon = this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8,'M', 8, -8, 'L', 16, 0, 8, 8]).attr({'stroke': '#ffffff','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[1].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[1].points[0].shapeArgs.innerR -(this.series[1].points[0].shapeArgs.r - this.series[1].points[0].shapeArgs.innerR) / 2); if(!this.series[2].icon) {this.series[2].icon = this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0]).attr({'stroke': '#303030','stroke-linecap': 'round','stroke-linejoin': 'round','stroke-width': 2,'zIndex': 10}).add(this.series[2].group);}this.series[2].icon.translate(this.chartWidth / 2 - 10,this.plotHeight / 2 - this.series[2].points[0].shapeArgs.innerR -(this.series[2].points[0].shapeArgs.r - this.series[2].points[0].shapeArgs.innerR) / 2);}";
}

