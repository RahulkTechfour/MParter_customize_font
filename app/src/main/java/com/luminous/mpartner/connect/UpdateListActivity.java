package com.luminous.mpartner.connect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luminous.mpartner.R;
import com.luminous.mpartner.activities.CreateTicketActivity;
import com.luminous.mpartner.databinding.ActivityUpdateListBinding;
import com.luminous.mpartner.dynamic_home.entities.HomeData;
import com.luminous.mpartner.network.GetticketDatum;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.TicketListEntity;
import com.luminous.mpartner.reports.fragments.PrimaryReportFragment;
import com.luminous.mpartner.utilities.CommonUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateListActivity extends AppCompatActivity {
    private ActivityUpdateListBinding binding;
    private RetrofitInterface apiInterface;
    private Date currentDate;
    private Calendar mMonth;
    private DatePickerDialog DatePickerDialog;
    private String date;
    private SimpleDateFormat dateFormatter, outputFormat;
    private ProgressDialog progressDialog;
    private Calendar mCurrentDate;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list);
        setTitle("");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_list);
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
        initUI();
        initToolbar();
    }

    private void initUI() {
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd");
        date = Dateformat.format(current);
        //date = date.substring(0, 10);
        dateFormatter = new SimpleDateFormat("MM/yyyy", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        outputFormat = new SimpleDateFormat("MM/yyyy");
        try {
            Date date1 = inputFormat.parse(date);
            String outputDateStr = outputFormat.format(date1);
            currentDate = outputFormat.parse(outputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mCurrentDate = Calendar.getInstance();
        mCurrentDate.setTime(currentDate);
        mMonth = Calendar.getInstance();
        mMonth.setTime(currentDate);
        //String outputDateStr = outputFormat.format(date1);


        binding.tvmonth.setText(dateFormatter.format(mMonth
                .getTime()));


        String date = binding.tvmonth.getText().toString();
        String month = date.substring(0, 2);
        String year = date.substring(3);
        getTicketList(month, year);

        Date maxDate = new Date();
        maxDate.setMonth(currentDate.getMonth() + 3);

        // Min date Validator
        Date minDate = new Date();
        minDate.setMonth(currentDate.getMonth() - 3);

        DatePickerDialog = createDialogWithoutDateField(new DatePickerDialog.OnDateSetListener() {

                                                            @Override
                                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                mMonth.set(year, monthOfYear, dayOfMonth);
                                                                binding.tvmonth.setText(dateFormatter.format(mMonth
                                                                        .getTime()));
                                                                getTicketList(String.valueOf(monthOfYear + 1), String.valueOf(year));
                                                            }
                                                        }, mCurrentDate.get(Calendar.YEAR),
                mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH));

        DatePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        DatePickerDialog.getDatePicker().setMinDate(minDate.getTime());
    }

    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getTicketList(String month, String year) {

        if (!CommonUtility.isNetworkAvailable(getApplicationContext())) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getTicketList(month, year), getApplicationContext(), UpdateListActivity.class.getSimpleName());
        apiInterface.getTicketListData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TicketListEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(TicketListEntity entity) {
                        dismissProgressDialog();
                        if (entity != null
                                && entity.getGetticketData() != null && entity.getGetticketData().size() > 0) {
                            binding.llTicList.removeAllViews();
                            binding.svData.setVisibility(View.VISIBLE);
                            binding.tvNoDataFound.setVisibility(View.GONE);
                            for (final GetticketDatum list : entity.getGetticketData()) {
                                LayoutInflater inflater = null;
                                inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View convertView = inflater.inflate(R.layout.item_ticket, null);
                                LinearLayout llTicket = (LinearLayout) convertView.findViewById(R.id.llTicket);
                                TextView tvdate = (TextView) convertView.findViewById(R.id.tvdate);
                                TextView tvserial = (TextView) convertView.findViewById(R.id.tvserial);
                                TextView tvstatus = (TextView) convertView.findViewById(R.id.tvstatus);

                                tvdate.setText(list.getDate());
                                tvserial.setText(list.getSerialno());
                                tvstatus.setText(list.getStatus());

                                binding.llTicList.addView(convertView);

                                llTicket.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("serial_number", list.getSerialno());
                                        bundle.putInt("ticket_id", list.getId());
                                        Intent intent = new Intent(UpdateListActivity.this, CreateTicketActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            binding.svData.setVisibility(View.GONE);
                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }

    private DatePickerDialog createDialogWithoutDateField(android.app.DatePickerDialog.OnDateSetListener dsl, int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog(this, dsl, year, month, date);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    public void selectDate(View v) {
        binding.tvmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });

    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
