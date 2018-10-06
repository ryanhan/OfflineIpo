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
import android.view.MenuItem;
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
    private TextView personNumberText;
    private ImageButton weixinButton;
    private LinearLayout subscriptionLayout;
    private TextView soldDateText;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipo_detail);
        getBundle();
        setActionBar();

        nameText = (TextView) findViewById(R.id.ipo_detail_name);
        codeText = (TextView) findViewById(R.id.ipo_detail_code);
        priceText = (TextView) findViewById(R.id.ipo_detail_price);
        listedDateText = (TextView) findViewById(R.id.ipo_detail_listed_date);

        weixinButton = (ImageButton) findViewById(R.id.ipo_detail_weixin_button);
        subscriptionLayout = (LinearLayout) findViewById(R.id.ipo_detail_subscription_layout);
        personNumberText = (TextView) findViewById(R.id.ipo_detail_person_number);
        soldDateText = (TextView) findViewById(R.id.ipo_detail_sold_date);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        GetIpoDetailAsyncTask getIpoDetailAsyncTask = new GetIpoDetailAsyncTask(this);
        getIpoDetailAsyncTask.execute(ipoCode);
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

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        ipoCode = bundle.getString(Value.IPO_CODE);
    }

    private void setActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
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
                priceText.setText(R.string.none);
            } else {
                priceText.setText("￥" + String.valueOf(result.getIpoItem().getIssuePrice()));
            }

            if (result.getIpoItem().getListedDate() == null) {
                listedDateText.setText(R.string.none);
            } else {
                listedDateText.setText(result.getIpoItem().getListedDate());
            }

            if (!result.getIpoItem().isApplied()) {
                subscriptionLayout.setVisibility(View.GONE);
            } else {
                subscriptionLayout.setVisibility(View.VISIBLE);

                EditText stockNumber = (EditText) findViewById(R.id.ipo_detail_stock_number);
                final TextView paymentText = (TextView) findViewById(R.id.ipo_detail_payment_amount);
                if (result.getStockShare() != 0) {
                    stockNumber.setText(String.valueOf(result.getStockShare()));
                    try {
                        BigDecimal stock = new BigDecimal(String.valueOf(result.getStockShare()));
                        BigDecimal price = new BigDecimal(Double.toString(result.getIpoItem().getIssuePrice()));
                        DecimalFormat df = new DecimalFormat("#0.00");
                        paymentText.setText(df.format(stock.multiply(price).doubleValue()));
                    } catch (Exception e) {
                    }
                }
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
                        int stockNumber = 0;
                        try {
                            stockNumber = Integer.parseInt(editable.toString());
                        } catch (Exception e) {
                        }
                        DatabaseUtils.updateStockNumber(IpoDetailActivity.this, result.getIpoItem().getCode(), stockNumber);
                        result.setStockShare(stockNumber);
                    }
                });

                weixinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (result.getStockShare() != 0 && result.getIpoItem().getIssuePrice() != 0) {
                            String notification = getString(R.string.payment_notification);
                            BigDecimal stock = new BigDecimal(String.valueOf(result.getStockShare()));
                            BigDecimal price = new BigDecimal(Double.toString(result.getIpoItem().getIssuePrice()));
                            DecimalFormat df = new DecimalFormat("#0.00");
                            notification = notification.replace("#MONEY#", df.format(stock.multiply(price).doubleValue())).replace("#CODE#", ipoCode);
                            if (notification.length() != 0) {
                                Intent intent = new Intent();
                                ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                                intent.setComponent(comp);
                                intent.setAction("android.intent.action.SEND");
                                intent.setType("text/plain");
                                intent.putExtra(intent.EXTRA_TEXT, notification.trim());
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(IpoDetailActivity.this, "缴款金额不正确", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                EditText earnAmount = (EditText) findViewById(R.id.ipo_detail_earn_amount);
                if (result.getEarnAmount() != 0) {
                    earnAmount.setText(String.valueOf(result.getEarnAmount()));
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
                        try {
                            DecimalFormat df = new DecimalFormat("#0.00");
                            DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, result.getIpoItem().getCode(), df.format(Double.parseDouble(editable.toString())));
                        } catch (Exception e) {
                            DatabaseUtils.updateEarnAmount(IpoDetailActivity.this, result.getIpoItem().getCode(), "0");
                        }
                    }
                });

                for (int i = 0; i < result.getPersonNumber(); i++) {

                }

                personNumberText.setText(result.getPersonNumber() + "人");

                Date now = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                if (result.getSoldDate() == null) {
                    soldDateText.setText("请选择");
                } else {
                    soldDateText.setText(result.getSoldDate());
                }
                soldDateText.setOnClickListener(new View.OnClickListener()

                {
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
