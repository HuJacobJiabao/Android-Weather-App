package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.common.hichartsclasses.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class WeeklyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        TextView chartTitle = view.findViewById(R.id.chart_title);
        chartTitle.setText("Temperature Range");


        ArrayList<Object[]> seriesData = getArguments().getSerializable("seriesData", ArrayList.class);

        HIChartView chartView = view.findViewById(R.id.highcharts_view);
        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("arearange");
        chart.setZoomType("x");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Temperature variation by day");
        HICSSObject titleStyle = new HICSSObject();
        titleStyle.setColor(HIColor.initWithRGB(111, 111,111)); // Set text color to grey
        titleStyle.setFontSize("18px");
        title.setStyle(titleStyle);
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        xaxis.setType("datetime");
        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

        HITooltip tooltip = new HITooltip();
        tooltip.setValueSuffix("°F");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);

        HIArearange series = new HIArearange();
        series.setName("Temperatures");

        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(232, 180, 130)));
        stops.add(new HIStop(1, HIColor.initWithRGB(160, 180, 213)));
        HIColor gradientColor = HIColor.initWithLinearGradient(
                new HIGradient(0, 0, 0, 1),
                stops
        );
        series.setColor(gradientColor);

        series.setData(seriesData);
        options.setSeries(new ArrayList<>(Arrays.asList(series)));

        chartView.setOptions(options);

        return view;
    }



}
