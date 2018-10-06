package cn.ryanman.app.offlineipo.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by ryanh on 2016/11/25.
 */

public class MyIpoFragment extends Fragment {

    private RelativeLayout myIpoLayout;
    private BarChart mChart;
    private TextView chartInfoText;
    private HashMap<String, Double> benefitMap;
    private ArrayList<String> dateValueList;
    private LinearLayout settingsLayout;
    private LinearLayout checkUpdateLayout;
    private LinearLayout aboutLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ipo, container, false);
        myIpoLayout = view.findViewById(R.id.my_ipo_layout);
        mChart = view.findViewById(R.id.my_ipo_chart);
        chartInfoText = view.findViewById(R.id.my_ipo_chart_info_text);
        initBenefitData(Value.BY_MONTH);
        initChart();

        settingsLayout = view.findViewById(R.id.my_ipo_settings_layout);
        checkUpdateLayout = view.findViewById(R.id.my_ipo_check_update_layout);
        aboutLayout = view.findViewById(R.id.my_ipo_about_layout);

        myIpoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyIpoFragment.this.getActivity(),
                        MyIpoListActivity.class);
                startActivity(intent);
            }
        });

//        settingsLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MyIpoFragment.this.getActivity(),
//                        SettingsActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void initBenefitData(int interval) {
        List<MyIpo> myIpoList = DatabaseUtils.getAllSubscription(this.getContext());
        benefitMap = new HashMap<>();
        dateValueList = new ArrayList<>();
        int noDateCount = 0;
        int minDate = 0;
        int maxDate = 0;
        for (MyIpo myIpo : myIpoList) {
            String date = myIpo.getSoldDate();
            double benefit = myIpo.getEarnAmount();
            if (benefit > 0) {
                if (date != null && !date.equals("")) {
                    String yyyymm = date.substring(0, 4).concat(date.substring(5, 7));
                    if (benefitMap.containsKey(yyyymm)) {
                        benefitMap.put(yyyymm, benefitMap.get(yyyymm) + benefit);
                    } else {
                        int ym = Integer.parseInt(yyyymm);
                        if (minDate == 0 || ym < minDate) {
                            minDate = ym;
                        }
                        if (maxDate == 0 || ym > maxDate) {
                            maxDate = ym;
                        }
                        benefitMap.put(yyyymm, benefit);
                    }
                } else {
                    noDateCount++;
                }
            }
        }

        if (noDateCount == 0){
            chartInfoText.setVisibility(View.INVISIBLE);
        }
        else {
            chartInfoText.setVisibility(View.VISIBLE);
            chartInfoText.setText(noDateCount + getResources().getString(R.string.no_sold_date));
        }
        if (interval == Value.BY_MONTH) {
            int curDate = minDate;
            while (curDate <= maxDate) {
                dateValueList.add(String.valueOf(curDate));
                curDate++;
                if (curDate % 100 > 12) {
                    curDate = curDate + 88;
                }
            }

        } else if (interval == Value.BY_QUARTER) {

        } else if (interval == Value.BY_YEAR) {

        }


    }

    private void initChart() {

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);
        mChart.setScaleYEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        YAxis rightAxis = mChart.getAxisRight();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        rightAxis.setEnabled(false);

        // add a nice and smooth animation
        mChart.animateY(2500);

        mChart.getLegend().setEnabled(false);

        setData();

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String yyyymm = dateValueList.get((int) value);
                return yyyymm.substring(0, 4).concat("-").concat(yyyymm.substring(4, 6));
            }
        });

    }

    private void setData() {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < dateValueList.size(); i++) {
            if (benefitMap.containsKey(dateValueList.get(i))) {
                float val = (float) benefitMap.get(dateValueList.get(i)).doubleValue();
                yVals1.add(new BarEntry(i, val));
            } else {
                yVals1.add(new BarEntry(i, 0));
            }
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Benefit Chart");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(true);
            set1.setValueTextSize(12f);

            BarData data = new BarData(set1);

            mChart.setData(data);
            mChart.setFitBars(true);
        }

        mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
        mChart.invalidate();

    }


}
