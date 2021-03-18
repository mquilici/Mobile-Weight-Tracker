package com.zybooks.weighttracker;

// This plotting utility is based on the CodingDemos example found here
// https://www.codingdemos.com/draw-android-line-chart/

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class PlotFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate layout
        View root = inflater.inflate(R.layout.fragment_plot, container, false);
        LineChartView lineChartView = root.findViewById(R.id.weight_chart);

        // Get weight database
        WeightDatabase mWeightDb = WeightDatabase.getInstance(getActivity().getApplicationContext());

        // Get current username from shared preferences
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Define weight scale factor based on weight unit
        String unitSystem = mSharedPrefs.getString(SettingsFragment.PREFERENCE_UNIT_SYSTEM,getString(R.string.english));
        String weightUnit = unitSystem.equals(getString(R.string.english)) ? "lb" : "kg";
        float poundsPerKilogram = 2.20462f;
        float scaleFactor = unitSystem.equals(getString(R.string.english)) ? 1f : 1/ poundsPerKilogram;

        // Get weight data from database
        List<Weight> weights = mWeightDb.getWeights();
        long[] timeData = new long[weights.size()];
        double[] weightData = new double[weights.size()];
        for (int i=0; i<weights.size(); i++) {
            timeData[i] = weights.get(i).getTime();
            weightData[i] = weights.get(i).getWeight()*scaleFactor;
        }

        // Get goal weight
        float goalWeight = mSharedPrefs.getFloat("goal_weight",0)*scaleFactor;

        // Find min and max weights
        double maxWeight = Double.MIN_VALUE;
        double minWeight = Double.MAX_VALUE;
        for(double weight : weightData){
            maxWeight = Math.max(maxWeight,weight);
            minWeight = Math.min(minWeight,weight);
        }
        maxWeight = Math.max(maxWeight,goalWeight);
        minWeight = (goalWeight == 0) ? minWeight : Math.min(minWeight,goalWeight);

        // Variables define axis limits
        int xMin, xMax, yMin, yMax;
        xMin = xMax = yMin = yMax = 0;
        long minTime = 0;
        long maxTime = 0;

        // Define x-axis min and max in units of days with margin at end
        int xMargin = 1;
        float dayInMillis = 60 * 60 * 24 * 1000;
        if (timeData.length > 0) {
            minTime = timeData[timeData.length - 1];
            maxTime = timeData[0];

            xMax = (int)Math.ceil((maxTime - minTime) * 1.0f / dayInMillis) + xMargin;
            xMin = 0;
        }

        // Define y-axis min and max with margin at top and bottom
        int yMargin = 5;
        if (weightData.length > 0) {
            yMax = (int)Math.ceil((maxWeight+1)/yMargin) * yMargin;
            yMin = (int)Math.floor((minWeight-1)/yMargin) * yMargin;
        }


        /* Define axis labels using integer values for cleaner appearance */

        // x-axis labels
        List<AxisValue> xAxisValues = new ArrayList<>();
        for (int i=0; i<xMax+2; i++) {
            xAxisValues.add(i, new AxisValue(i).setLabel(Integer.toString(i)));
        }

        // y-axis labels
        List<AxisValue> yAxisValues = new ArrayList<>();
        for (int i=0; i<yMax+1; i++) {
            yAxisValues.add(i, new AxisValue(i).setLabel(Integer.toString(i)));
        }

        // List to hold lines
        List<Line> lines = new ArrayList<>();

        // Add goal line
        if (goalWeight > 0 && weightData.length > 0) {
            List<PointValue> goalPoints = new ArrayList<>();

            // Define line color and path style
            Line goalLine = new Line(goalPoints);
            goalLine.setColor(getResources().getColor(R.color.colorGoalLine));
            goalLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
            goalLine.setHasPoints(false);

            // Define line using only min and max x-values
            goalPoints.add(new PointValue(xMin, goalWeight));
            goalPoints.add(new PointValue(xMax, goalWeight));
            lines.add(goalLine);

            // Define label
            List<PointValue> goalLabel = new ArrayList<>();
            Line line = new Line(goalLabel);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(false);
            line.setHasPoints(false);
            PointValue point = new PointValue(xMax,goalWeight);
            line.setPointColor(getResources().getColor(R.color.colorGoalLine));
            point.setLabel(getString(R.string.goal));
            goalLabel.add(point);
            lines.add(line);
        }

        // Add weight line
        if (weightData.length > 0) {
            List<PointValue> weightPoints = new ArrayList<>();

            // line properties
            Line line = new Line(weightPoints);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(true);
            line.setColor(getResources().getColor(R.color.colorPlotLine));

            // Loop over points and add data to line
            for (int i = 0; i < weightData.length; i++) {
                float time = (float) (timeData[weightData.length - i - 1] - minTime) / (24 * 60 * 60 * 1000);
                weightPoints.add(new PointValue(time, (float) weightData[weightData.length - i - 1]));
            }

            lines.add(line);
        }

        // Add lines to chart
        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Configure x-axis
        Axis xAxis = new Axis();
        xAxis.generateAxisFromRange(xMin, xMax, 1).setHasLines(true);
        xAxis.setName("Day");
        xAxis.setTextSize(16);
        xAxis.setTextColor(getResources().getColor(R.color.colorDarkText));
        xAxis.setLineColor(getResources().getColor(R.color.colorDarkText));
        xAxis.setValues(xAxisValues);
        data.setAxisXBottom(xAxis);

        // Configure y-axis
        Axis yAxis = new Axis();
        yAxis.generateAxisFromRange(yMin, yMax, yMargin).setHasLines(true);
        yAxis.setName("Weight (" + weightUnit + ")");
        yAxis.setTextColor(getResources().getColor(R.color.colorDarkText));
        yAxis.setLineColor(getResources().getColor(R.color.colorDarkText));
        yAxis.setTextSize(16);
        yAxis.setHasLines(true);
        yAxis.setValues(yAxisValues);
        data.setAxisYLeft(yAxis);

        // Label color
        data.setValueLabelsTextColor(getResources().getColor(R.color.bmiTextColor));
        data.setValueLabelBackgroundEnabled(false);

        // Create chart
        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = yMax;
        viewport.bottom = yMin;
        viewport.left = xMin;
        viewport.right = xMax;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
        lineChartView.setViewportCalculationEnabled(false);

        return root;
    }

}
