package cn.ryanman.app.offlineipo.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.model.Notice;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;
import cn.ryanman.app.offlineipo.utils.WebUtils;

/**
 * Created by hanyan on 12/26/2016.
 */

public class IpoDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String ipoCode;
    private String ipoName;
    private MyIpo myIpo;
    private DatePickerDialog datePickerDialog;
    private TextView soldDateText;
    private LinearLayout noticeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipo_detail);
        getBundle();
        setActionBar();
        noticeLayout = findViewById(R.id.ipo_detail_notice_layout);
        myIpo = DatabaseUtils.getIpoDetail(this, ipoCode);
        IpoNoticeAsyncTask ipoNoticeAsyncTask = new IpoNoticeAsyncTask();
        ipoNoticeAsyncTask.execute(ipoCode);
        setViews();
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        ipoName = bundle.getString(Value.IPO_NAME);
        ipoCode = bundle.getString(Value.IPO_CODE);
    }

    private void setActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(
                R.layout.actionbar_ipo_detail, null);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        TextView ipoNameText = view.findViewById(R.id.actionbar_ipo_name);
        TextView ipoCodeText = view.findViewById(R.id.actionbar_ipo_code);
        ImageView backButton = view.findViewById(R.id.actionbar_left);
        ipoNameText.setText(ipoName);
        ipoCodeText.setText(ipoCode);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String date = String.valueOf(year) + "-";
        if (month < 9) {
            date += "0" + (month + 1);
        } else {
            date += (month + 1);
        }

        if (day < 10) {
            date += "-0" + day;
        } else {
            date += "-" + day;
        }

        DatabaseUtils.updateSoldDate(IpoDetailActivity.this, ipoCode, date);
        soldDateText.setText(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViews() {
        final DecimalFormat df = new DecimalFormat("#,###.00");
        LinearLayout layout = findViewById(R.id.activity_ipo_detail);
        addTextView(layout, getString(R.string.issue_price), "￥" + String.valueOf(myIpo.getIpoItem().getIssuePrice()));
        addTextView(layout, getString(R.string.list_date), myIpo.getIpoItem().getListedDate() == null ? "-" : myIpo.getIpoItem().getListedDate());

        if (myIpo.getIpoItem().isApplied()) {
            //股数
            LinearLayout stockLayout = (LinearLayout) addEditText(layout, getString(R.string.stock_amount));
            EditText stockEdit = stockLayout.findViewById(R.id.table_content_edit);

            //缴款
            LinearLayout paymentLayout = (LinearLayout) addTextView(layout, getString(R.string.should_pay), "");
            final TextView paymentText = paymentLayout.findViewById(R.id.table_content_text);
            LinearLayout paymentTitleLayout = paymentLayout.findViewById(R.id.table_title_layout);
            //添加卖出日期
            final LinearLayout soldDateLayout = (LinearLayout) addTextView(layout, getString(R.string.sold_date), "");
            soldDateText = soldDateLayout.findViewById(R.id.table_content_text);
            //添加卖出价格
            final EditText[] et = new EditText[myIpo.getPersonNumber()];
            for (int i = 0; i < myIpo.getPersonNumber(); i++) {
                LinearLayout soldPriceLayout = (LinearLayout) addEditText(layout, getString(R.string.sold_price) + "（" + (i + 1) + "）");
                EditText soldPriceEdit = soldPriceLayout.findViewById(R.id.table_content_edit);
                soldPriceEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                et[i] = soldPriceEdit;
            }
            //添加收益总额
            LinearLayout benefitLayout = (LinearLayout) addTextView(layout, getString(R.string.benefit), "");
            final TextView benefitText = benefitLayout.findViewById(R.id.table_content_text);
            if (myIpo.getEarnAmount() > 0) {
                benefitText.setText("￥" + df.format(myIpo.getEarnAmount()));
                benefitText.setTextColor(getColor(R.color.colorAccent));
            } else {
                benefitText.setText("-");
            }

            //添加微信按钮
            LayoutInflater inflater = LayoutInflater.from(IpoDetailActivity.this);
            ImageButton weixinButton = (ImageButton) inflater.inflate(R.layout.weixin_button, null);
            paymentTitleLayout.addView(weixinButton);

            weixinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myIpo.getStockShare() != 0 && myIpo.getIpoItem().getIssuePrice() != 0) {
                        String notification = getString(R.string.payment_notification);
                        BigDecimal stock = new BigDecimal(String.valueOf(myIpo.getStockShare()));
                        BigDecimal price = new BigDecimal(Double.toString(myIpo.getIpoItem().getIssuePrice()));
                        notification = notification.replace("#MONEY#", df.format(stock.multiply(price).doubleValue())).replace("#CODE#", ipoCode);
                        if (notification.length() != 0) {
                            Intent intent = new Intent();
                            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                            intent.setComponent(comp);
                            intent.setAction("android.intent.action.SEND");
                            intent.setType("text/plain");
                            intent.putExtra(intent.EXTRA_TEXT, notification.trim());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(IpoDetailActivity.this, "缴款金额不正确", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //根据股数计算缴款信息
            if (myIpo.getStockShare() != 0) {
                stockEdit.setText(String.valueOf(myIpo.getStockShare()));
                try {
                    BigDecimal stock = new BigDecimal(String.valueOf(myIpo.getStockShare()));
                    BigDecimal price = new BigDecimal(Double.toString(myIpo.getIpoItem().getIssuePrice()));
                    paymentText.setText("￥" + df.format(stock.multiply(price).doubleValue()) + " × " + myIpo.getPersonNumber() + "人");
                    paymentText.setTextColor(getColor(R.color.colorAccent));
                } catch (Exception e) {
                    paymentText.setText("-");
                }
            } else {
                paymentText.setText("-");
            }
            //股数输入监听器
            stockEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    double payment = 0;
                    try {
                        BigDecimal stock = new BigDecimal(charSequence.toString());
                        BigDecimal price = new BigDecimal(Double.toString(myIpo.getIpoItem().getIssuePrice()));
                        payment = stock.multiply(price).doubleValue();
                    } catch (Exception e) {
                        paymentText.setText("-");
                    }
                    if (payment == 0) {
                        paymentText.setText("-");
                        paymentText.setTextColor(getColor(R.color.dark_grey));
                    } else {
                        paymentText.setText("￥" + df.format(payment) + " × " + myIpo.getPersonNumber() + "人");
                        paymentText.setTextColor(getColor(R.color.colorAccent));
                    }
                    try {
                        double benefit = calcBenefit(et, Integer.parseInt(charSequence.toString()));
                        if (benefit > 0) {
                            benefitText.setText("￥" + df.format(benefit));
                            benefitText.setTextColor(getColor(R.color.colorAccent));
                        } else {
                            benefitText.setText("-");
                            benefitText.setTextColor(getColor(R.color.dark_grey));
                        }
                    } catch (Exception e) {
                        benefitText.setText("-");
                        benefitText.setTextColor(getColor(R.color.dark_grey));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int stockNumber = 0;
                    try {
                        stockNumber = Integer.parseInt(editable.toString());
                    } catch (Exception e) {
                    }
                    DatabaseUtils.updateStockNumber(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), stockNumber);
                    myIpo.setStockShare(stockNumber);

                    double benefit = calcBenefit(et);
                    if (benefit > 0) {
                        DecimalFormat df2 = new DecimalFormat("#.00");
                        DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), df2.format(benefit));
                    } else {
                        DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), "0");
                    }
                    myIpo.setEarnAmount(benefit);
                }
            });

            //日期时间选择器
            final Calendar calendar = Calendar.getInstance();
            datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

            if (myIpo.getSoldDate() == null) {
                soldDateText.setText(R.string.please_select);
                soldDateText.setTextColor(getColor(R.color.colorAccent));
                soldDateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                soldDateText.setText(myIpo.getSoldDate());
                soldDateText.setTextColor(getColor(R.color.colorAccent));
            }
            soldDateText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePickerDialog.setVibrate(false);
                    datePickerDialog.setYearRange(1985, 2028);
                    datePickerDialog.setCloseOnSingleTapDay(false);
                    datePickerDialog.show(getSupportFragmentManager(), "datepicker");
                }
            });

            String[] priceArray;
            if (myIpo.getSoldPrice() != null && !myIpo.getSoldPrice().equals("")) {
                priceArray = myIpo.getSoldPrice().split(Value.SOLD_PRICE_SPLIT);
                for (int i = 0; i < myIpo.getPersonNumber(); i++) {
                    if (!priceArray[i].equals("0")) {
                        et[i].setText(priceArray[i]);
                    }
                }
            }

            for (int i = 0; i < myIpo.getPersonNumber(); i++) {
                et[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                        double benefit = calcBenefit(et);
                        if (benefit > 0) {
                            benefitText.setText("￥" + df.format(benefit));
                            benefitText.setTextColor(getColor(R.color.colorAccent));
                        } else {
                            benefitText.setText("-");
                            benefitText.setTextColor(getColor(R.color.dark_grey));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String soldPriceText = "";
                        for (int j = 0; j < myIpo.getPersonNumber(); j++) {
                            String str = et[j].getText().toString();
                            if (str != null && !str.equals("")) {
                                soldPriceText += str + Value.SOLD_PRICE_SPLIT;
                            } else {
                                soldPriceText += "0" + Value.SOLD_PRICE_SPLIT;
                            }
                        }
                        DatabaseUtils.updateSoldPrice(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), soldPriceText);
                        myIpo.setSoldPrice(soldPriceText);
                        double benefit = calcBenefit(et);
                        if (benefit > 0) {
                            DecimalFormat df2 = new DecimalFormat("#.00");
                            DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), df2.format(benefit));
                        } else {
                            DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, myIpo.getIpoItem().getCode(), "0");
                        }
                        myIpo.setEarnAmount(benefit);
                    }
                });
            }
        }
    }

    private View addTextView(LinearLayout layout, String str1, String str2) {
        LayoutInflater inflater = LayoutInflater.from(IpoDetailActivity.this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.table_title_text, null);
        TextView view1 = view.findViewById(R.id.table_title_text);
        TextView view2 = view.findViewById(R.id.table_content_text);
        view1.setText(str1);
        view2.setText(str2);
        layout.addView(view);
        return view;
    }

    private View addEditText(LinearLayout layout, String str1) {
        LayoutInflater inflater = LayoutInflater.from(IpoDetailActivity.this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.table_title_edit, null);
        TextView view1 = view.findViewById(R.id.table_title_edit);
        view1.setText(str1);
        layout.addView(view);
        return view;
    }

    private View addLinkText(LinearLayout layout, final Notice notice) {
        LayoutInflater inflater = LayoutInflater.from(IpoDetailActivity.this);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.table_notice, null);
        TextView title = view.findViewById(R.id.table_notice_title);
        TextView date = view.findViewById(R.id.table_notice_date);
        title.setText(notice.getTitle());
        date.setText(notice.getDate());
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("http://static.sse.com.cn" + notice.getUrl()));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });
        layout.addView(view);
        return view;
    }

    @SuppressLint("ParcelCreator")
    public static class NoUnderLineSpan extends URLSpan {
        public NoUnderLineSpan(String src) {
            super(src);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(Color.parseColor("#000000"));
        }
    }

    private double calcBenefit(EditText[] et, int stockNumber) {
        double benefit = 0;
        try {
            BigDecimal sum = new BigDecimal("0");
            for (int i = 0; i < et.length; i++) {
                String str = et[i].getText().toString();
                if (str != null && !str.equals("")) {
                    BigDecimal curPrice = new BigDecimal(str);
                    sum = sum.add(curPrice);
                }
            }
            BigDecimal stock = new BigDecimal(stockNumber);
            BigDecimal issuePrice = new BigDecimal(Double.toString(myIpo.getIpoItem().getIssuePrice()));
            benefit = sum.subtract(issuePrice.multiply(new BigDecimal(myIpo.getPersonNumber()))).multiply(stock).doubleValue();
        } catch (Exception e) {
        }
        return benefit;
    }

    private double calcBenefit(EditText[] et) {
        return calcBenefit(et, myIpo.getStockShare());
    }

    private void setNoticeBoard(List<Notice> noticeList) {
        for (Notice notice : noticeList) {
            addLinkText(noticeLayout, notice);
        }
    }

    private class IpoNoticeAsyncTask extends AsyncTask<String, Integer, List<Notice>> {

        @Override
        protected List<Notice> doInBackground(String... params) {
            try {
                return WebUtils.getNoticeList(params[0]);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Notice> result) {
            if (result != null && result.size() > 0) {
                noticeLayout.setVisibility(View.VISIBLE);
                setNoticeBoard(result);
            } else {
                noticeLayout.setVisibility(View.GONE);
            }
        }
    }
}