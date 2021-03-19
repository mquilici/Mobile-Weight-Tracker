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
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class BmiFragment extends Fragment {

    private static int minHeight = 56;  // inches
    private static int maxHeight = 84;  // inches
    private static int minWeight = 80;  // lbs
    private static int maxWeight = 400; // lbs

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bmi, container, false);

        LineChartView lineChartView = root.findViewById(R.id.bmi_chart);

        // Get weight database
        WeightDatabase mWeightDb = WeightDatabase.getInstance(getActivity().getApplicationContext());

        // Load weights from database
        List<Weight>weights = mWeightDb.getWeights();

        // Get current username from shared preferences
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Define scale factor to convert stored weight and height to the correct units
        String unitSystem = mSharedPrefs.getString(SettingsFragment.PREFERENCE_UNIT_SYSTEM,getString(R.string.english));
        String weightUnit = unitSystem.equals(getString(R.string.english)) ? "lb" : "kg";
        String heightUnit = unitSystem.equals(getString(R.string.english)) ? "in" : "cm";
        float poundsPerKilogram = 2.20462f;
        float cmPerInch = 2.54f;
        float weightScale = unitSystem.equals(getString(R.string.english)) ? 1f : 1f / poundsPerKilogram;
        float heightScale = unitSystem.equals(getString(R.string.english)) ? 1f : cmPerInch;
        float bmiScale = unitSystem.equals(getString(R.string.english)) ? 703f : 10000f;

        // Plot limits
        int yMin = (int)(minHeight*heightScale);
        int yMax = (int)(maxHeight*heightScale);
        int xMin = (int)(minWeight*weightScale);
        int xMax = (int)(maxWeight*weightScale);
        int nPts = xMax-xMin+1;

        // Arrays to hold BMI line data points
        float[] weightValues = new float[nPts]; // x-axis
        float[] BMI19 = new float[nPts];
        float[] BMI22 = new float[nPts];
        float[] BMI25 = new float[nPts];
        float[] BMI28 = new float[nPts];
        float[] BMI30 = new float[nPts];
        float[] BMI35 = new float[nPts];
        float[] BMI40 = new float[nPts];

        // Weight values (x-axis)
        for (int i=0; i<nPts; i++) {
            weightValues[i] = xMin + (float)i*(xMax-xMin)/nPts;
        }

        // BMI region definitions (y-axis)
        float[] bmiRegions = {18.5f, 21.75f, 25f, 27.5f, 30f, 35f, 40f};
        for (int i=0; i<nPts; i++) {
            BMI19[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[0]);
            BMI22[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[1]);
            BMI25[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[2]);
            BMI28[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[3]);
            BMI30[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[4]);
            BMI35[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[5]);
            BMI40[i] = (float)Math.sqrt(bmiScale*weightValues[i]/bmiRegions[6]);
        }

        /* Define axis labels using integer values for cleaner appearance */

        // x-axis labels
        List<AxisValue> xAxisValues = new ArrayList<>();
        //int xMax = (int)Math.ceil((maxTime - minTime) * 1.0f / (60 * 60 * 24 * 1000))+1;
        for (int i=0; i<xMax+1; i++) {
            xAxisValues.add(i, new AxisValue(i).setLabel(Integer.toString(i)));
        }

        // y-axis labels
        List<AxisValue> yAxisValues = new ArrayList<>();
        //int yMax = (int)Math.ceil((maxWeight+1)/10) * 10;
        //int yMin = (int)Math.floor((minWeight-1)/10) * 10;
        for (int i=0; i<yMax+1; i++) {
            yAxisValues.add(i, new AxisValue(i).setLabel(Integer.toString(i)));
        }

        // List to hold lines
        List<Line> lines = new ArrayList<>();

        // ===== BMI < 18.5 ========================================================================

        // Add underweight filled area
        List<PointValue> bmi0Filled = new ArrayList<>();
        Line line = new Line(bmi0Filled);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.colorUnderweight));
        line.setFilled(true);
        line.setAreaTransparency(255);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi0Filled.add(new PointValue(weightValues[i], maxHeight*heightScale));
        }

        lines.add(line);

        // ===== BMI = 18.5 ========================================================================

        // Add normal weight filled area
        List<PointValue> bmi19Filled = new ArrayList<>();
        line = new Line(bmi19Filled);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.colorNormal));
        line.setFilled(true);
        line.setAreaTransparency(255);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi19Filled.add(new PointValue(weightValues[i], BMI19[i]));
        }

        lines.add(line);

        // Add weight line
        List<PointValue> bmi19Line = new ArrayList<>();
        line = new Line(bmi19Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorDark));
        line.setStrokeWidth(1);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi19Line.add(new PointValue(weightValues[i], BMI19[i]));
        }

        lines.add(line);

        // ===== BMI = 21.75 =======================================================================

        // Add weight center line
        List<PointValue> bmi22Line = new ArrayList<>();
        line = new Line(bmi22Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorLight));
        line.setStrokeWidth(1);
        line.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi22Line.add(new PointValue(weightValues[i], BMI22[i]));
        }

        lines.add(line);

        // ===== BMI = 25 ==========================================================================

        // Add filled area
        List<PointValue> bmi25Filled = new ArrayList<>();
        line = new Line(bmi25Filled);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.colorOverweight));
        line.setFilled(true);
        line.setAreaTransparency(255);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi25Filled.add(new PointValue(weightValues[i], BMI25[i]));
        }

        lines.add(line);

        // Add line
        List<PointValue> bmi25Line = new ArrayList<>();
        line = new Line(bmi25Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorDark));
        line.setStrokeWidth(1);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi25Line.add(new PointValue(weightValues[i], BMI25[i]));
        }

        lines.add(line);

        // ===== BMI = 27.5 ========================================================================

        // Add overweight center line
        List<PointValue> bmi28Line = new ArrayList<>();
        line = new Line(bmi28Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorLight));
        line.setStrokeWidth(1);
        line.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi28Line.add(new PointValue(weightValues[i], BMI28[i]));
        }

        lines.add(line);

        // ===== BMI = 30 ==========================================================================

        // Add obese filled area
        List<PointValue> bmi30Filled = new ArrayList<>();
        line = new Line(bmi30Filled);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.colorObese));
        line.setFilled(true);
        line.setAreaTransparency(255);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi30Filled.add(new PointValue(weightValues[i], BMI30[i]));
        }

        lines.add(line);

        // Add line
        List<PointValue> bmi30Line = new ArrayList<>();
        line = new Line(bmi30Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorDark));
        line.setStrokeWidth(1);

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi30Line.add(new PointValue(weightValues[i], BMI30[i]));
        }

        lines.add(line);

        // ===== BMI = 35 ==========================================================================

        List<PointValue> bmi35Line = new ArrayList<>();
        line = new Line(bmi35Line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorLight));
        line.setStrokeWidth(1);
        line.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi35Line.add(new PointValue(weightValues[i], BMI35[i]));
        }

        lines.add(line);

        // ===== BMI = 40 ==========================================================================

        List<PointValue> bmi40line = new ArrayList<>();
        line = new Line(bmi40line);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasPoints(false);
        line.setColor(getResources().getColor(R.color.bmiLineColorLight));
        line.setStrokeWidth(1);
        line.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));

        // Loop over points and add data to line
        for (int i = 0; i < nPts; i++) {
            bmi40line.add(new PointValue(weightValues[i], BMI40[i]));
        }

        lines.add(line);

        // ===== Labels ============================================================================

        // Underweight Label
        List<PointValue> underWeightPoint = new ArrayList<>();
        line = new Line(underWeightPoint);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasPoints(false);
        float labelXPosition = 95f;
        PointValue point = new PointValue(labelXPosition*weightScale, (maxHeight-1)*heightScale);
        line.setPointColor(getResources().getColor(R.color.bmiLabelColor));
        point.setLabel("Underweight");
        underWeightPoint.add(point);
        lines.add(line);

        // Normal Weight Label
        List<PointValue> normalWeightPoint = new ArrayList<>();
        line = new Line(normalWeightPoint);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasPoints(false);
        labelXPosition = 210f;
        point = new PointValue(labelXPosition*weightScale, (maxHeight-1)*heightScale);
        line.setPointColor(getResources().getColor(R.color.bmiLabelColor));
        point.setLabel("Normal");
        normalWeightPoint.add(point);
        lines.add(line);

        // Overweight Label
        List<PointValue> overWeightPoint = new ArrayList<>();
        line = new Line(overWeightPoint);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasPoints(false);
        labelXPosition = 270f;
        point = new PointValue(labelXPosition*weightScale, (maxHeight-1)*heightScale);
        line.setPointColor(getResources().getColor(R.color.bmiLabelColor));
        point.setLabel("Overweight");
        overWeightPoint.add(point);
        lines.add(line);

        // Obese Label
        List<PointValue> obeseWeightPoint = new ArrayList<>();
        line = new Line(obeseWeightPoint);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasPoints(false);
        labelXPosition = 350f;
        point = new PointValue(labelXPosition*weightScale, (maxHeight-1)*heightScale);
        line.setPointColor(getResources().getColor(R.color.bmiLabelColor));
        point.setLabel("Obese");
        obeseWeightPoint.add(point);
        lines.add(line);

        // Current BMI Point
        if (weights.size() > 0) {
            float userWeight = weights.get(0).getWeight();
            int userHeight = mSharedPrefs.getInt(SettingsFragment.PREFERENCE_HEIGHT_ENGLISH, 0);
            if (userHeight > 0) {
                List<PointValue> currentBmiPoint = new ArrayList<>();
                line = new Line(currentBmiPoint);
                line.setHasLabels(true);
                line.setHasLabelsOnlyForSelected(false);
                line.setHasPoints(false);
                point = new PointValue(userWeight * weightScale, userHeight * heightScale);
                float bmi = (float) (bmiScale * userWeight * weightScale / (userHeight * userHeight * heightScale * heightScale));
                String bmiString = String.format(Locale.US, "BMI = %.1f", bmi);
                point.setLabel(bmiString);
                line.setPointRadius(7);
                line.setPointColor(getResources().getColor(R.color.bmiPointColor1));
                currentBmiPoint.add(point);
                lines.add(line);

                List<PointValue> currentBmiPoint2 = new ArrayList<>();
                line = new Line(currentBmiPoint2);
                line.setHasLabels(false);
                line.setHasPoints(false);
                point = new PointValue(userWeight * weightScale, userHeight * heightScale);
                point.setLabel(bmiString);
                line.setPointColor(getResources().getColor(R.color.bmiPointColor2));
                line.setPointRadius(5);
                currentBmiPoint2.add(point);
                lines.add(line);
            }
        }

        // ===== Create Chart ======================================================================

        // Add lines to chart
        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Configure x-axis
        Axis xAxis = new Axis();
        xAxis.setValues(xAxisValues);
        xAxis.setName("Weight (" + weightUnit + ")");
        xAxis.setTextSize(16);
        xAxis.setLineColor(getResources().getColor(R.color.colorDarkText));
        xAxis.setTextColor(getResources().getColor(R.color.colorDarkText));
        data.setAxisXBottom(xAxis);

        // Configure y-axis
        Axis yAxis = new Axis();
        yAxis.setValues(yAxisValues);
        yAxis.setName("Height (" + heightUnit + ")");
        yAxis.setTextSize(16);
        yAxis.setHasLines(false);
        yAxis.setLineColor(getResources().getColor(R.color.colorDarkText));
        yAxis.setTextColor(getResources().getColor(R.color.colorDarkText));
        data.setAxisYLeft(yAxis);

        // Label color
        data.setValueLabelsTextColor(getResources().getColor(R.color.bmiTextColor));
        data.setValueLabelTextSize(10);
        data.setValueLabelBackgroundEnabled(false);

        // Define chart
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