package cn.ryanman.app.offlineipo.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.ryanman.app.offlineipo.R;
import cn.ryanman.app.offlineipo.adapter.MarketSpinnerAdapter;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.model.Status;
import cn.ryanman.app.offlineipo.model.User;
import cn.ryanman.app.offlineipo.utils.AppUtils;
import cn.ryanman.app.offlineipo.utils.DatabaseUtils;
import cn.ryanman.app.offlineipo.utils.Value;

/**
 * Created by hanyan on 12/26/2016.
 */

public class IpoDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String ipoCode;
    private TextView nameText;
    private TextView codeText;
    private TextView priceText;
    private TextView listedDateText;
    private TextView submitText;
    private TextView split_1;
    private TextView inquiryText;
    private TextView split_2;
    private TextView applyText;
    private TextView split_3;
    private TextView paymentText;
    private ImageButton paymentNotifyButton;
    private LinearLayout subscriptionLayout;
    private LinearLayout paymentLayout;
    private LinearLayout benefitLayout;
    private TextView soldDateText;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipo_detail);

        getBundle();

        nameText = (TextView) findViewById(R.id.ipo_detail_name);
        codeText = (TextView) findViewById(R.id.ipo_detail_code);
        priceText = (TextView) findViewById(R.id.ipo_detail_price);
        listedDateText = (TextView) findViewById(R.id.ipo_detail_listed_date);

        submitText = (TextView) findViewById(R.id.ipo_progress_submit);
        split_1 = (TextView) findViewById(R.id.ipo_progress_split_1);
        inquiryText = (TextView) findViewById(R.id.ipo_progress_inquiry);
        split_2 = (TextView) findViewById(R.id.ipo_progress_split_2);
        applyText = (TextView) findViewById(R.id.ipo_progress_apply);
        split_3 = (TextView) findViewById(R.id.ipo_progress_split_3);
        paymentText = (TextView) findViewById(R.id.ipo_progress_payment);
        paymentNotifyButton = (ImageButton) findViewById(R.id.payment_amount_notify_button);
        subscriptionLayout = (LinearLayout) findViewById(R.id.ipo_detail_subscription_layout);
        paymentLayout = (LinearLayout) findViewById(R.id.ipo_detail_payment_layout);
        benefitLayout = (LinearLayout) findViewById(R.id.ipo_detail_benefit_layout);
        soldDateText = (TextView) findViewById(R.id.ipo_detail_sold_date);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        GetIpoDetailAsyncTask getIpoDetailAsyncTask = new GetIpoDetailAsyncTask(this);
        getIpoDetailAsyncTask.execute(ipoCode);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("-").append(month + 1).append("-").append(day);
        DatabaseUtils.updateSoldDate(IpoDetailActivity.this, ipoCode, sb.toString());
        soldDateText.setText(sb.toString());
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        ipoCode = bundle.getString(Value.IPO_CODE);
    }

    private class GetIpoDetailAsyncTask extends AsyncTask<String, Integer, MyIpo> {

        private Context context;

        public GetIpoDetailAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected MyIpo doInBackground(String... params) {
            return DatabaseUtils.getIpoDetail(context, params[0]);
        }

        @Override
        protected void onPostExecute(final MyIpo result) {
            nameText.setText(result.getIpoItem().getName());
            codeText.setText(result.getIpoItem().getCode());

            if (result.getIpoItem().getIssuePrice() == 0) {
                priceText.setText("-");
            } else {
                priceText.setText("￥" + String.valueOf(result.getIpoItem().getIssuePrice()));
            }

            if (result.getIpoItem().getListedDate() == null) {
                listedDateText.setText("-");
            } else {
                listedDateText.setText(result.getIpoItem().getListedDate());
            }

            cn.ryanman.app.offlineipo.model.Status status = result.getIpoItem().getProgress();
            switch (status) {
                case PAYMENT:
                    paymentText.setTextColor(getColor(R.color.green));
                    split_3.setTextColor(getColor(R.color.green));
                    paymentText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    split_3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                case OFFLINE:
                    applyText.setTextColor(getColor(R.color.green));
                    split_2.setTextColor(getColor(R.color.green));
                    applyText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    split_2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                case INQUIRY:
                    inquiryText.setTextColor(getColor(R.color.green));
                    split_1.setTextColor(getColor(R.color.green));
                    inquiryText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    split_1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                case NOTICE:
                    submitText.setTextColor(getColor(R.color.green));
                    submitText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                default:
                    break;
            }

            if (result.getIpoItem().getProgress() == cn.ryanman.app.offlineipo.model.Status.NONE) {
                subscriptionLayout.setVisibility(View.GONE);
            } else {
                subscriptionLayout.setVisibility(View.VISIBLE);
                List<String> users = result.getUserName();
                if (users != null && users.size() > 0) {
                    for (int i = 0; i < users.size(); i++) {
                        final int index = i;
                        final String user = users.get(i);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LayoutInflater inflater = LayoutInflater.from(IpoDetailActivity.this);
                        View view = inflater.inflate(R.layout.layout_payment_info, null);
                        TextView userText = (TextView) view.findViewById(R.id.user_name_text);
                        EditText stockNumber = (EditText) view.findViewById(R.id.stock_number_text);
                        final TextView paymentText = (TextView) view.findViewById(R.id.payment_amount);
                        if (result.getStockShare().get(i) != 0) {
                            stockNumber.setText(String.valueOf(result.getStockShare().get(i)));
                            try {
                                BigDecimal stock = new BigDecimal(String.valueOf(result.getStockShare().get(i)));
                                BigDecimal price = new BigDecimal(Double.toString(result.getIpoItem().getIssuePrice()));
                                DecimalFormat df = new DecimalFormat("#0.00");
                                paymentText.setText(df.format(stock.multiply(price).doubleValue()));
                            } catch (Exception e) {
                            }
                        }
                        userText.setText(user);
                        stockNumber.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                                double payment = 0;
                                try {
                                    BigDecimal stock = new BigDecimal(charSequence.toString());
                                    BigDecimal price = new BigDecimal(Double.toString(result.getIpoItem().getIssuePrice()));
                                    payment = stock.multiply(price).doubleValue();
                                } catch (Exception e) {
                                }
                                DecimalFormat df = new DecimalFormat("#0.00");
                                paymentText.setText(df.format(payment));
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try{
                                    int stockNumber = Integer.parseInt(editable.toString());
                                    DatabaseUtils.updateStockNumber(IpoDetailActivity.this, result.getIpoItem().getCode(), user, stockNumber);
                                    result.getStockShare().set(index, stockNumber);
                                } catch (Exception e) {
                                }
                            }
                        });

                        paymentNotifyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                List<String> users = result.getUserName();
                                List<Integer> stockNumber = result.getStockShare();
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < users.size(); i++){
                                    if (stockNumber.get(i) == 0 || result.getIpoItem().getIssuePrice() == 0)
                                        continue;
                                    String notification = getString(R.string.payment_notification);
                                    BigDecimal stock = new BigDecimal(String.valueOf(result.getStockShare().get(i)));
                                    BigDecimal price = new BigDecimal(Double.toString(result.getIpoItem().getIssuePrice()));
                                    DecimalFormat df = new DecimalFormat("#0.00");
                                    notification = notification.replace("#MONEY#", df.format(stock.multiply(price).doubleValue())).replace("#CODE#", ipoCode);
                                    sb.append(notification).append("\n");
                                }
                                if (sb.length() != 0) {
                                    Intent intent = new Intent();
                                    ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                                    intent.setComponent(comp);
                                    intent.setAction("android.intent.action.SEND");
                                    intent.setType("text/plain");
                                    intent.setFlags(0x3000001);
                                    intent.putExtra(intent.EXTRA_TEXT, sb.toString().trim());
                                    context.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(IpoDetailActivity.this, "缴款金额不正确", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        view.setLayoutParams(lp);
                        paymentLayout.addView(view);

//                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        LayoutInflater inflater2 = LayoutInflater.from(IpoDetailActivity.this);
                        View view2 = inflater.inflate(R.layout.layout_benefit_info, null);
                        TextView userText2 = (TextView) view2.findViewById(R.id.ipo_detail_earn_name);
                        userText2.setText(user);
                        EditText earnAmount = (EditText) view2.findViewById(R.id.ipo_detail_earn_amount);
                        if (result.getEarnAmount().get(i) != 0) {
                            earnAmount.setText(String.valueOf(result.getEarnAmount().get(i)));
                        }
                        earnAmount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                DecimalFormat df = new DecimalFormat("#0.00");
                                DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, result.getIpoItem().getCode(), user, df.format(Double.parseDouble(editable.toString())));
                            }
                        });
                        view2.setLayoutParams(lp);
                        benefitLayout.addView(view2);
                    }
                }

                Date now = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                if (result.getSoldDate() == null) {
                    soldDateText.setText("请选择");
                }
                else{
                    soldDateText.setText(result.getSoldDate());
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
            }
        }
    }
}
