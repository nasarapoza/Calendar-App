    package com.team9.calbuddy;
import java.util.Calendar;

import org.joda.time.DateTime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.Calendar.Freebusy;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.Calendar.Freebusy.Query;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class AddEvent extends AppCompatActivity
        implements View.OnClickListener {

    GoogleAccountCredential mCredential = null;
    com.google.api.services.calendar.Calendar mService = null;
    EditText mEdit = null;
    String calID;

    Button btn_sd, btn_ed, btn_st, btn_et;
    int year_x, month_x, day_x, hour_x, minute_x;
    //static final int DIALOG_ID = 0;
    static final int startDate = 1;
    static final int startTime = 2;
    static final int endDate = 3;
    static final int endTime = 4;

    EditText txtSDate, txtSTime, txtEDate, txtETime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        findViewById(R.id.addButton).setOnClickListener(this);

        Globals globalState = ((Globals)getApplication());
        mCredential = globalState.getCredential();
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("CalBuddy")
                .build();
        showDialogOnButtonClick();
        showTimePickerDialog();

        txtSDate = (EditText) findViewById(R.id.startEdit);
        txtSTime = (EditText) findViewById(R.id.startEdit);
        txtEDate = (EditText) findViewById(R.id.endEdit);
        txtETime = (EditText) findViewById(R.id.endEdit);
    }

    /* displays calendar when users pick up date */
    public void showDialogOnButtonClick() {
        btn_sd = (Button)findViewById(R.id.button_SD);
        btn_ed = (Button)findViewById(R.id.button_ED);
        btn_sd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(startDate);
                    }
                }
        );
        btn_ed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(endDate);
                    }
                }
        );
    }

    private DatePickerDialog.OnDateSetListener sdpickerListner
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            Toast.makeText(AddEvent.this, year_x+ "/" + month_x + "/" + day_x, Toast.LENGTH_LONG).show();
            // Display Selected date in textbox
            //txtSDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            txtSDate.setText(year_x + "-" + (month_x + 1) + "-" + day_x);
            //txtSDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
        }
    };

    private DatePickerDialog.OnDateSetListener edpickerListner
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            Toast.makeText(AddEvent.this, year_x+ "/" + month_x + "/" + day_x, Toast.LENGTH_LONG).show();
            txtEDate.setText(year_x + "-" + (month_x + 1) + "-" + day_x);
            //txtSDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        }
    };

    public void showTimePickerDialog() {
        btn_st = (Button)findViewById(R.id.button_ST);
        btn_et = (Button)findViewById(R.id.button_ET);
        btn_st.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(startTime);
                    }
                }
        );
        btn_et.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(endTime);
                    }
                }
        );

    }


    @Override
    protected Dialog onCreateDialog(int id) {

        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        year_x = c.get(Calendar.YEAR);
        month_x = c.get(Calendar.MONTH);
        day_x = c.get(Calendar.DAY_OF_MONTH);
        hour_x = c.get(Calendar.HOUR_OF_DAY);
        minute_x = c.get(Calendar.MINUTE);


        switch (id) {
            case startDate:
                return new DatePickerDialog (AddEvent.this, sdpickerListner, year_x, month_x, day_x);
            case startTime:
                return new TimePickerDialog(AddEvent.this, stTimePickerListener, hour_x, minute_x, false);
            case endDate:
                return new DatePickerDialog(AddEvent.this, edpickerListner, year_x, month_x, day_x);
            case endTime:
                return new TimePickerDialog(AddEvent.this, etTimePickerListener, hour_x, minute_x, false);
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener stTimePickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            Toast.makeText(AddEvent.this, hour_x+ ": " + minute_x + ": " + day_x, Toast.LENGTH_LONG).show();
            // Display Selected time in textbox
            if(minute_x < 10)
                txtSTime.append(" " + hourOfDay + ":0" + minute + ":00");
            //txtSTime.setText(year_x + "-" + (month_x + 1) + "-" + day_x + " " + hourOfDay + ":0" + minute + ":00");
            else {
                txtSTime.append(" " + hourOfDay + ":" + minute + ":00");
                //txtSTime.setText(year_x + "-" + (month_x + 1) + "-" + day_x + " " + hourOfDay + ":" + minute + ":00");
            }
        }
    };

    protected TimePickerDialog.OnTimeSetListener etTimePickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            Toast.makeText(AddEvent.this, hour_x+ ": " + minute_x + ": " + day_x, Toast.LENGTH_LONG).show();
            if(minute_x < 10)
                txtETime.append(" " + hourOfDay + ":0" + minute + ":00");
                //txtSTime.setText(year_x + "-" + (month_x + 1) + "-" + day_x + " " + hourOfDay + ":0" + minute + ":00");
            else {
                txtETime.append(" " + hourOfDay + ":" + minute + ":00");
                //txtETime.setText(year_x + "-" + (month_x + 1) + "-" + day_x + " " + hourOfDay + ":" + minute + ":00");
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                AsyncTask<Void, Void, Void> task = new AddEventTask();
                task.execute();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class AddEventTask extends AsyncTask<Void,Void,Void> {
        private Exception mLastError = null;
        private String title;
        private String loc;
        private String id;
        private String startDate;
        private String endDate;
        private DateFormat df;

        com.google.api.client.util.DateTime startTime;
        com.google.api.client.util.DateTime endTime;
        Date start;
        Date end;

        public AddEventTask() {
            mEdit = (EditText) findViewById(R.id.titleEdit);
            title = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.locEdit);
            loc = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.idEdit);
            id = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.startEdit);
            startDate = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.endEdit);
            endDate = mEdit.getText().toString();
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                start = df.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                end = df.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startTime = new com.google.api.client.util.DateTime(start, TimeZone.getDefault());
            endTime = new com.google.api.client.util.DateTime(end, TimeZone.getDefault());
        }


        @Override
        protected Void doInBackground(Void... params) {
            Event event = new Event()
                    .setSummary(title)
                    .setLocation(loc);
            EventDateTime st = new EventDateTime()
                    .setDateTime(startTime);
            EventDateTime et = new EventDateTime()
                    .setDateTime(endTime);
            event.setStart(st);
            event.setEnd(et);
            try {
                mService.events().insert("primary", event).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
}
}

