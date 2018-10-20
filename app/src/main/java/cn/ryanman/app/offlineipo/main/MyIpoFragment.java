package cn.ryanman.app.offlineipo.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
    private RelativeLayout myBenefitLayout;
    private BarChart mChart;
    private TextView chartInfoText;
    private HashMap<String, Double> benefitMonthMap;
    private HashMap<String, Double> benefitYearMap;
    private ArrayList<String> dateValueList;
    private int time;
    private double totalBenefit;
    private LinearLayout settingsLayout;
    private TextView myIpoNumber;
    private TextView myBenefitNumber;
    private TextView segMonth;
    private TextView segYear;
    private SharedPreferences pref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_ipo, container, false);
        myIpoLayout = view.findViewById(R.id.my_ipo_layout);
        myBenefitLayout = view.findViewById(R.id.my_ipo_benefit_layout);
        settingsLayout = view.findViewById(R.id.my_ipo_settings_layout);
        mChart = view.findViewById(R.id.my_ipo_chart);
        chartInfoText = view.findViewById(R.id.my_ipo_chart_info_text);
        myIpoNumber = view.findViewById(R.id.my_ipo_number);
        myBenefitNumber = view.findViewById(R.id.my_ipo_benefit_number);
        segMonth = view.findViewById(R.id.segment_month);
        segYear = view.findViewById(R.id.segment_year);
        pref = this.getActivity().getSharedPreferences(Value.APPINFO, Context.MODE_PRIVATE);

        time = pref.getInt(Value.TIME, Value.BY_MONTH);
        setMonthActive(time == Value.BY_MONTH);

        segMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time == Value.BY_YEAR) {
                    setMonthActive(true);
                    initBenefitData();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt(Value.TIME, Value.BY_MONTH);
                    editor.commit();
                }
            }
        });

        segYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time == Value.BY_MONTH) {
                    setMonthActive(false);
                    initBenefitData();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt(Value.TIME, Value.BY_YEAR);
                    editor.commit();
                }
            }
        });

        ImageView stockImage = view.findViewById(R.id.my_ipo_stock_image);
        stockImage.setColorFilter(ContextCompat.getColor(MyIpoFragment.this.getActivity(), R.color.colorPrimary));

        benefitMonthMap = new HashMap<>();
        benefitYearMap = new HashMap<>();
        dateValueList = new ArrayList<>();

        myIpoLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyIpoFragment.this.getActivity(),
                        MyIpoListActivity.class);
                startActivity(intent);
            }
        });

        myBenefitLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyIpoFragment.this.getActivity(),
                        BenefitListActivity.class);
                startActivity(intent);
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyIpoFragment.this.getActivity(),
                        SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initBenefitData();
    }

    private void setMonthActive(boolean b) {
        if (b) {
            time = Value.BY_MONTH;
            segMonth.setTextColor(getResources().getColor(R.color.white));
            segMonth.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            segYear.setTextColor(getResources().getColor(R.color.colorAccent));
            segYear.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            time = Value.BY_YEAR;
            segMonth.setTextColor(getResources().getColor(R.color.colorAccent));
            segMonth.setBackgroundColor(getResources().getColor(R.color.white));
            segYear.setTextColor(getResources().getColor(R.color.white));
            segYear.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void initBenefitData() {
        List<MyIpo> myIpoList = DatabaseUtils.getAllSubscription(this.getContext());
        benefitMonthMap.clear();
        benefitYearMap.clear();
        dateValueList.clear();
        int noDateCount = 0;
        int minDate = 0;
        int maxDate = 0;
        totalBenefit = 0;
        for (MyIpo myIpo : myIpoList) {
            String date = myIpo.getSoldDate();
            double benefit = myIpo.getEarnAmount();
            totalBenefit += benefit;
            if (benefit > 0) {
                if (date != null && !date.equals("")) {
                    String yyyymm = date.substring(0, 4).concat(date.substring(5, 7));
                    int ym = Integer.parseInt(yyyymm);
                    int year = ym / 100;
                    if (benefitMonthMap.containsKey(yyyymm)) {
                        benefitMonthMap.put(yyyymm, benefitMonthMap.get(yyyymm) + benefit);
                        benefitYearMap.put(String.valueOf(year), benefitYearMap.get(String.valueOf(year)) + benefit);
                    } else {
                        if (minDate == 0 || ym < minDate) {
                            minDate = ym;
                        }
                        if (maxDate == 0 || ym > maxDate) {
                            maxDate = ym;
                        }
                        benefitMonthMap.put(yyyymm, benefit);
                        if (benefitYearMap.containsKey(String.valueOf(year))){
                            benefitYearMap.put(String.valueOf(year), benefitYearMap.get(String.valueOf(year)) + benefit);
                        }else{
                            benefitYearMap.put(String.valueOf(year), benefit);
                        }
                    }
                } else {
                    noDateCount++;
                }
            }
        }

        DecimalFormat df = new DecimalFormat("#,###.00");

        myIpoNumber.setText(String.valueOf(myIpoList.size()));
        if (totalBenefit == 0) {
            myBenefitNumber.setText(R.string.no_benefit);
        } else {
            myBenefitNumber.setText("￥" + df.format(totalBenefit));
        }
        if (noDateCount == 0) {
            chartInfoText.setVisibility(View.INVISIBLE);
        } else {
            chartInfoText.setVisibility(View.VISIBLE);
            chartInfoText.setText(noDateCount + getResources().getString(R.string.no_sold_date));
        }

        if (benefitMonthMap.size() > 0) {
            if (time == Value.BY_MONTH) {
                int curDate = minDate;
                while (curDate <= maxDate) {
                    dateValueList.add(String.valueOf(curDate));
                    curDate++;
                    if (curDate % 100 > 12) {
                        curDate = curDate + 88;
                    }
                }
            } else if (time == Value.BY_YEAR) {
                int maxYear = maxDate / 100;
                int minYear = minDate / 100;
                for (int curYear = minYear; curYear <= maxYear; curYear++) {
                    dateValueList.add(String.valueOf(curYear));
                }
            }
            initChart();
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
        mChart.animateY(1500);

        mChart.getLegend().setEnabled(false);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (time == Value.BY_MONTH) {
                    String yyyymm = dateValueList.get((int) value);
                    return yyyymm.substring(0, 4).concat("-").concat(yyyymm.substring(4, 6));
                } else {
                    if (value > dateValueList.size() - 1){
                        return "";
                    }
                    return dateValueList.get((int) value);
                }
            }
        });

        setData();
    }

    private void setData() {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < dateValueList.size(); i++) {
            if (time == Value.BY_MONTH) {
                if (benefitMonthMap.containsKey(dateValueList.get(i))) {
                    float val = (float) benefitMonthMap.get(dateValueList.get(i)).doubleValue();
                    yVals1.add(new BarEntry(i, val));
                } else {
                    yVals1.add(new BarEntry(i, 0));
                }
            } else if (time == Value.BY_YEAR) {
                if (benefitYearMap.containsKey(dateValueList.get(i))) {
                    float val = (float) benefitYearMap.get(dateValueList.get(i)).doubleValue();
                    yVals1.add(new BarEntry(i, val));
                } else {
                    yVals1.add(new BarEntry(i, 0));
                }
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
