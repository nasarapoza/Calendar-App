package com.team9.calbuddy;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
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


public class AddEvent extends Activity
        implements View.OnClickListener {

    GoogleAccountCredential mCredential = null;
    com.google.api.services.calendar.Calendar mService = null;
    EditText mEdit = null;
    String calID;

    Button btn_sd, btn_ed, btn_st, btn_et;
    int year_x, month_x, day_x, hour_x, minute_x;
    static final int startDate = 1;
    static final int startTime = 2;
    static final int endDate = 3;
    static final int endTime = 4;

    EditText txtSDT, txtEDT;
    EditText displaySDT, displayEDT;
    String tempEDate, tempSDate, tempSTime, tempETime = " ";
    boolean checked = false;
    String startDT, endDT, tempDisplaySD, tempDisplayED, tempDisplayST, tempDisplayET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        findViewById(R.id.group_event_check).setOnClickListener(this);
        findViewById(R.id.addButton).setOnClickListener(this);
        /*Button v = (Button) findViewById(R.id.addButton);
        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{0xff006daf});
        v.setSupportBackgroundTintList(csl);
        v = (Button) findViewById(R.id.button_SD);
        v.setSupportBackgroundTintList(csl);
        v = (Button) findViewById(R.id.button_ST);
        v.setSupportBackgroundTintList(csl);
        v = (Button) findViewById(R.id.button_ED);
        v.setSupportBackgroundTintList(csl);
        v = (Button) findViewById(R.id.button_ET);
        v.setSupportBackgroundTintList(csl);*/
        View b = findViewById(R.id.textViewGroup);
        b.setVisibility(View.GONE);

        b = findViewById(R.id.editTextEmail);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.duration);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.editDuration);
        b.setVisibility(View.GONE);
        b = findViewById(R.id.minutes);
        b.setVisibility(View.GONE);
        Globals globalState = ((Globals)getApplication());
        mCredential = globalState.getCredential();
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("CalBuddy")
                .build();
        showDialogOnButtonClick();
        //showTimePickerDialog();

        //To keep inout as format(yyyy-MM-dd HH:mm:ss) for input
        txtSDT = (EditText) findViewById(R.id.startEdit);
        txtEDT = (EditText) findViewById(R.id.endEdit);

        //Display American time in the textbox
        //displaySDT  = (EditText) findViewById(R.id.startEdit);
        //displayEDT  = (EditText) findViewById(R.id.endEdit);
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
            //Display selected date in a pop-up textbox at the bottom of the screen
            Toast.makeText(AddEvent.this, year_x+ "/" + (month_x + 1) + "/" + day_x, Toast.LENGTH_LONG).show();

            // Display selected date in a textbox
            //if(tempSTime == null) {
                //txtSDT.setText((month_x + 1) + "/" + day_x + "/" + year_x);
                startDT = year_x + "-" + (month_x + 1) + "-" + day_x;
            //}
            //else {
                //txtSDT.setText((month_x + 1) + "/" + day_x + "/" + year_x + "  " + tempDisplayST);
                //startDT = year_x + "-" + (month_x + 1) + "-" + day_x + tempSTime;
            //}
            tempDisplaySD = (month_x + 1) + "/" + day_x + "/" + year_x;
            //tempSDate = year_x + "-" + (month_x + 1) + "-" + day_x;
            showDialog(startTime);
        }
    };

    private DatePickerDialog.OnDateSetListener edpickerListner
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            //Display selected date in a pop-up textbox at the bottom of the screen
            Toast.makeText(AddEvent.this, year_x+ "/" + (month_x + 1) + "/" + day_x, Toast.LENGTH_LONG).show();

            //Display selected time ina  textbox
/*            if(tempETime == null) {
                txtEDT.setText((month_x + 1) + "/" + day_x + "/" + year_x);
*/                endDT = year_x + "-" + (month_x + 1) + "-" + day_x;
/*            }
            else {
                txtEDT.setText((month_x + 1) + "/" + day_x + "/" + year_x + "  " + tempDisplayET);
                endDT = year_x + "-" + (month_x + 1) + "-" + day_x + tempETime;
            }
*/            tempDisplayED = (month_x + 1) + "/" + day_x + "/" + year_x;
            //tempEDate = year_x + "-" + (month_x + 1) + "-" + day_x;
            showDialog(endTime);
        }
    };
/*
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
*/

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

            // Display Selected time in textbox
/*            if(tempSDate == null) {
                if (minute_x < 10) {
                    if(hourOfDay <= 12) {
                        txtSDT.setText(hourOfDay + ":0" + minute + "AM");
                        Toast.makeText(AddEvent.this, hour_x + ":0 " + minute_x + "AM", Toast.LENGTH_LONG).show();
                        tempDisplayST = hourOfDay + ":0" + minute + "AM";
                    } else {
                        txtSDT.setText(hourOfDay%12 + ":0" + minute + "PM");
                        Toast.makeText(AddEvent.this, hour_x%12 + ":0 " + minute_x + "PM", Toast.LENGTH_LONG).show();
                        tempDisplayST = hourOfDay%12 + ":0" + minute + "PM";
                    }
                    tempSTime = " " + hour_x + ":0" + minute_x + ":00";
                } else {
                    if (hourOfDay <= 12) {
                        txtSDT.setText(hourOfDay + ":" + minute + "AM");
                        Toast.makeText(AddEvent.this, hour_x + ":" + minute_x + "AM", Toast.LENGTH_LONG).show();
                        tempDisplayST = hourOfDay + ":" + minute + "AM";
                    } else {
                        txtSDT.setText(hourOfDay%12 + ":" + minute + "PM");
                        Toast.makeText(AddEvent.this, hour_x%12 + ":" + minute_x + "PM", Toast.LENGTH_LONG).show();
                        tempDisplayST = hourOfDay%12 + ":" + minute + "PM";
                    }
                    tempSTime = " " + hour_x + ":" + minute_x + ":00";
                }
            }
            else {
*/                if (minute_x < 10) {
                    if (hourOfDay == 0) {
                        txtSDT.setText(tempDisplaySD + "  12:0" + minute + "AM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  12:0" + minute_x + "AM", Toast.LENGTH_LONG).show();
                    }
                    else if (hourOfDay < 12) {
                        txtSDT.setText(tempDisplaySD + "  " + hourOfDay + ":0" + minute + "AM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  " + hour_x + ":0 " + minute_x + "AM", Toast.LENGTH_LONG).show();
                    }
                    else if (hourOfDay == 12) {
                            txtSDT.setText(tempDisplaySD + "  12:0" + minute + "PM");
                            Toast.makeText(AddEvent.this, tempDisplaySD + "  12:0" + minute_x + "PM", Toast.LENGTH_LONG).show();
                    }
                    else {
                        txtSDT.setText(tempDisplaySD + "  " + hourOfDay%12 + ":0" + minute + "PM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  " + hour_x%12 + ":0 " + minute_x + "PM", Toast.LENGTH_LONG).show();
                    }
                    startDT = startDT + " " + hourOfDay + ":0" + minute + ":00";
                    //tempSTime = " " + hour_x + ":0" + minute_x + ":00";
                } else {
                    if (hourOfDay == 0) {
                        txtSDT.setText(tempDisplaySD + "  12:" + minute + "AM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  12:" + minute_x + "AM", Toast.LENGTH_LONG).show();
                    }
                    else if (hourOfDay < 12) {
                        txtSDT.setText(tempDisplaySD + "  " + hourOfDay + ":" + minute + "AM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  " + hour_x + ":" + minute_x + "AM", Toast.LENGTH_LONG).show();
                        //tempDisplayST = hourOfDay + ":" + minute + "AM";
                    }
                    else if (hourOfDay == 12) {
                        txtSDT.setText(tempDisplaySD + "  12:" + minute + "PM");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  12:" + minute_x + "PM", Toast.LENGTH_LONG).show();
                    }
                    else {
                        txtSDT.setText(tempDisplaySD + "  " + hourOfDay%12 + ":" + minute + "PM ");
                        Toast.makeText(AddEvent.this, tempDisplaySD + "  " + hour_x%12 + ":" + minute_x + "PM", Toast.LENGTH_LONG).show();
                        //tempDisplayST = hourOfDay%12 + ":" + minute + "PM";
                    }
                    startDT = startDT + " " + hourOfDay + ":" + minute + ":00";
                    //tempSTime = " " + hour_x + ":" + minute_x + ":00";
                //}
            }
        }
    };

    protected TimePickerDialog.OnTimeSetListener etTimePickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;

            //Display selected time in a textbox
/*            if(tempEDate == null) {
                if (minute_x < 10) {
                    if (hourOfDay <= 12) {
                        txtEDT.setText(hourOfDay + ":0" + minute + "AM");
                        Toast.makeText(AddEvent.this, hour_x + ":0 " + minute_x + "AM", Toast.LENGTH_LONG).show();
                        tempDisplayET = hourOfDay + ":0" + minute + "AM";
                    } else {
                        txtEDT.setText(hourOfDay%12 + ":0" + minute + "PM");
                        Toast.makeText(AddEvent.this, hour_x%12 + ":0 " + minute_x + "PM ", Toast.LENGTH_LONG).show();
                        tempDisplayET = hourOfDay%12 + ":0" + minute + "PM";
                    }
                    tempETime = " " + hour_x + ":0" + minute_x + ":00";
                } else {
                    if (hourOfDay <= 12) {
                        txtEDT.setText(hourOfDay + ":" + minute + "AM");
                        Toast.makeText(AddEvent.this, hour_x + ":" + minute_x + "AM", Toast.LENGTH_LONG).show();
                        tempDisplayET = hourOfDay + ":" + minute + "AM";
                    } else {
                        txtEDT.setText(hourOfDay%12 + ":" + minute + "PM ");
                        Toast.makeText(AddEvent.this, hour_x%12 + ":" + minute_x + "PM", Toast.LENGTH_LONG).show();
                        tempDisplayET = hourOfDay%12 + ":" + minute + "PM";
                    }
                    tempETime = " " + hour_x + ":" + minute_x + ":00";
                }
            }
            else {
*/          if (minute_x < 10) {
                if (hourOfDay == 0) {
                    txtEDT.setText(tempDisplayED + "  12:0" + minute + "AM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  12:0" + minute_x + "AM", Toast.LENGTH_LONG).show();
                }
                else if (hourOfDay < 12) {
                    txtEDT.setText(tempDisplayED + "  " + hourOfDay + ":0" + minute + "AM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  " + hour_x + ":0 " + minute_x + "AM", Toast.LENGTH_LONG).show();
                }
                else if (hourOfDay == 12) {
                    txtEDT.setText(tempDisplayED + "  12:0" + minute + "PM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  12:0" + minute_x + "PM", Toast.LENGTH_LONG).show();
                }
                else {
                    txtEDT.setText(tempDisplayED + "  " + hourOfDay%12 + ":0" + minute + "PM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  " + hour_x%12 + ":0 " + minute_x + "PM", Toast.LENGTH_LONG).show();
                }
                    endDT = endDT + " " + hourOfDay + ":0" + minute + ":00";
                    //tempETime = " " + hour_x + ":0" + minute_x + ":00";
            } else {
                if (hourOfDay == 0) {
                    txtEDT.setText(tempDisplayED + "  12:" + minute + "AM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  12:" + minute_x + "AM", Toast.LENGTH_LONG).show();
                }
                else if (hourOfDay < 12) {
                    txtEDT.setText(tempDisplayED + "  " + hourOfDay + ":" + minute + "AM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  " + hour_x + ":" + minute_x + "AM", Toast.LENGTH_LONG).show();
                    //tempDisplayST = hourOfDay + ":" + minute + "AM";
                }
                else if (hourOfDay == 12) {
                    txtEDT.setText(tempDisplayED + "  12:" + minute + "PM");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  12:" + minute_x + "PM", Toast.LENGTH_LONG).show();
                }
                else {
                    txtEDT.setText(tempDisplayED + "  " + hourOfDay%12 + ":" + minute + "PM ");
                    Toast.makeText(AddEvent.this, tempDisplayED + "  " + hour_x%12 + ":" + minute_x + "PM", Toast.LENGTH_LONG).show();
                    //tempDisplayST = hourOfDay%12 + ":" + minute + "PM";
                }
                endDT = endDT + " " + hourOfDay + ":" + minute + ":00";
                    //tempETime = " " + hour_x + ":" + minute_x + ":00";
            }
            //}
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                if (checked) {
                    AsyncTask<Void,Void,FreeBusyResponse> gTask;
                    gTask = new addGroupEventTask(this);
                    gTask.execute();
                    break;
                }
                AsyncTask<Void, Void, Void> task = new AddEventTask(this);
                task.execute();
                break;
            case R.id.group_event_check:
                checked = ((CheckBox) v).isChecked();
                if (checked)
                {
                    View b = findViewById(R.id.textViewGroup);
                    b.setVisibility(View.VISIBLE);
                    b = findViewById(R.id.editTextEmail);
                    b.setVisibility(View.VISIBLE);
                    b = findViewById(R.id.duration);
                    b.setVisibility(View.VISIBLE);
                    b = findViewById(R.id.editDuration);
                    b.setVisibility(View.VISIBLE);
                    b = findViewById(R.id.minutes);
                    b.setVisibility(View.VISIBLE);
                }
                else
                {

                    View b = findViewById(R.id.textViewGroup);
                    b.setVisibility(View.GONE);
                    b = findViewById(R.id.editTextEmail);
                    b.setVisibility(View.GONE);
                    b = findViewById(R.id.duration);
                    b.setVisibility(View.GONE);
                    b = findViewById(R.id.editDuration);
                    b.setVisibility(View.GONE);
                    b = findViewById(R.id.minutes);
                    b.setVisibility(View.GONE);
                }
                break;
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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
        ProgressDialog mProg;
        com.google.api.client.util.DateTime startTime;
        com.google.api.client.util.DateTime endTime;
        Date start;
        Date end;
        Context mContext;

        public AddEventTask(Context c) {
            mContext = c;
            mEdit = (EditText) findViewById(R.id.titleEdit);
            title = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.locEdit);
            loc = mEdit.getText().toString();

            //mEdit = (EditText) findViewById(R.id.idEdit);
            //id = mEdit.getText().toString();

            //mEdit = txtSDT;
            //mEdit = (EditText) findViewById(R.id.startEdit);
            startDate = startDT;
            //startDate = mEdit.getText().toString();

            //mEdit = txtEDT;
            //mEdit = (EditText) findViewById(R.id.endEdit);
            //endDate = mEdit.getText().toString();
            endDate = endDT;
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
        protected void onPreExecute() {
            mProg = new ProgressDialog(mContext);
            mProg.setMessage("Adding Event...");
            mProg.show();
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

        @Override
        protected void onPostExecute(Void v) {
            mProg.dismiss();
            startActivity(new Intent(mContext, WeeklyViewActivity.class));
        }
    }





    private class addGroupEventTask extends AsyncTask<Void, Void, FreeBusyResponse> {
        private String title;
        private String loc;
        private String startDate;
        private String endDate;
        private int eventDur;
        private DateFormat df;
        private String emailID;

        com.google.api.client.util.DateTime startTime;
        com.google.api.client.util.DateTime endTime;
        Date start;
        Date end;
        private Context mContext;


        public addGroupEventTask(Context c) {
            mContext = c;
            mEdit = (EditText) findViewById(R.id.editTextEmail);
            emailID = mEdit.getText().toString();

            //mEdit = txtSDT;
            //mEdit = (EditText) findViewById(R.id.startEdit);
            //startDate = mEdit.getText().toString();
            startDate = startDT;

            //mEdit = txtEDT;
            //mEdit = (EditText) findViewById(R.id.endEdit);
            //endDate = mEdit.getText().toString();
            endDate = endDT;

            mEdit = (EditText) findViewById(R.id.editDuration);
            eventDur = Integer.parseInt(mEdit.getText().toString());

            mEdit = (EditText) findViewById(R.id.titleEdit);
            title = mEdit.getText().toString();

            mEdit = (EditText) findViewById(R.id.locEdit);
            loc = mEdit.getText().toString();
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
        protected FreeBusyResponse doInBackground(Void...params) {
            List<FreeBusyRequestItem> calList = new ArrayList<FreeBusyRequestItem>();
            FreeBusyRequestItem gItem = new FreeBusyRequestItem();
            FreeBusyRequestItem gItem2 = new FreeBusyRequestItem();
            gItem.setId(mCredential.getSelectedAccountName());
            gItem2.setId(emailID);
            calList.add(gItem);
            calList.add(gItem2);

            FreeBusyRequest req = new FreeBusyRequest();
            req.setTimeMin(startTime);
            req.setTimeMax(endTime);
            req.setItems(calList);
            com.google.api.services.calendar.Calendar.Freebusy.Query fbq = null;
            try {
                fbq = mService.freebusy().query(req);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FreeBusyResponse fbresp = null;
            try {
                fbresp = fbq.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fbresp;
        }
        @Override
        protected void onPostExecute(FreeBusyResponse output) {

            try {
                determineFreeEvent(output);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results returned.");
            } else {
                /*new AlertDialog.Builder(mContext)
                        .setTitle("Output")
                        .setMessage(output.toString())
                        .show();*/
            }
        }

        public void determineFreeEvent(FreeBusyResponse fbr) throws ParseException {
            List<TimePeriod> frBsyLst = fbr.getCalendars().get(emailID).getBusy();
            List<TimePeriod> myBsyLst = fbr.getCalendars().get(mCredential.getSelectedAccountName()).getBusy();


            int year = 2015;
            int monthOfYear = 11;
            int dayOfMonth = 20;
            int hour = 10;
            int min = 0;
            DateTime startExtract = new DateTime(start);
            DateTime endExtract = new DateTime(end);

            year = startExtract.getYear();
            monthOfYear = startExtract.getMonthOfYear();
            dayOfMonth = startExtract.getDayOfMonth();
            hour = startExtract.getHourOfDay();
            min = startExtract.getMinuteOfHour();
            DateTime initTime = new DateTime(year, monthOfYear, dayOfMonth, hour, min);
            DateTime eTime = initTime.plusMinutes(eventDur);
            //DateTime eTime = new DateTime(year, monthOfYear, dayOfMonth, hour, min+eventDur);

            year = endExtract.getYear();
            monthOfYear = endExtract.getMonthOfYear();
            dayOfMonth = endExtract.getDayOfMonth();
            hour = endExtract.getHourOfDay();
            min = endExtract.getMinuteOfHour();
            DateTime endTime = new DateTime(year, monthOfYear, dayOfMonth, hour, min);

            hour = initTime.getHourOfDay();
            min = initTime.getMinuteOfHour();
            dayOfMonth = initTime.getDayOfMonth();
            year = initTime.getYear();

            final List<TimePeriod> possibleTimes = new ArrayList<TimePeriod>();


            boolean timeFound = false;
            boolean doesThisTimeWork = false;
            DateTime mTime = new DateTime(initTime);
            org.joda.time.Duration dur = new org.joda.time.Duration(mTime,eTime);
            /*new AlertDialog.Builder(mContext)
                    .setTitle("Info")
                    .setMessage(mTime.toString()+" "+eTime.toString()+" "+dur.toString()+" "+endTime.toString()
                    +" "+eTime.plus(dur).toString())
                    .show();*/
            //mTime and eTime are new Intervals
            int count = 0;
            while (eTime.isBefore(endTime) || eTime.isEqual(endTime) ) {
                DateTime stEx;
                DateTime enEx;
                Interval myTime = new Interval(mTime, eTime);
                doesThisTimeWork = true;

                //my cal
                for ( int i =0; i<myBsyLst.size(); i++ ) {
                    stEx = new DateTime(myBsyLst.get(i).getStart().getValue());
                    enEx = new DateTime(myBsyLst.get(i).getEnd().getValue());
                    Interval theirTime = new Interval(stEx, enEx);
                    if ( myTime.overlaps(theirTime)) {
                        doesThisTimeWork = false;
                        break;
                    }
                }

                //friendsBusyList
                for ( int i =0; i<frBsyLst.size(); i++) {
                    stEx = new DateTime(frBsyLst.get(i).getStart().getValue());
                    enEx = new DateTime(frBsyLst.get(i).getEnd().getValue());
                    Interval theirTime = new Interval(stEx, enEx);
                    if (myTime.overlaps(theirTime)) {
                        doesThisTimeWork = false;
                        break;
                    }
                }

                if ( doesThisTimeWork ) {
                    TimePeriod tp = new TimePeriod();
                    mTime.toDateTime();
                    com.google.api.client.util.DateTime st = new com.google.api.client.util.DateTime(mTime.toDate(),TimeZone.getDefault());
                    com.google.api.client.util.DateTime et = new com.google.api.client.util.DateTime(eTime.toDate(),TimeZone.getDefault());
                    tp.setStart(st);
                    tp.setEnd(et);

                    possibleTimes.add(tp);
                }

                mTime = mTime.plus(dur);
                eTime = eTime.plus(dur);

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Pick a Time");

            final String t[] = new String[3];
            String temp;
            int ctr = 0;
            final String prettyT[] = new String[3];
            final List<Interval> intL = new ArrayList<Interval>();
            while ( ctr < 3) {
                Interval intrr;
                if ( ctr < possibleTimes.size()) {
                    String st = possibleTimes.get(ctr).getStart().toString();
                    String en = possibleTimes.get(ctr).getEnd().toString();
                    //com.google.api.client.util.DateTime strx = new com.google.api.client.util.DateTime(st);
                    //com.google.api.client.util.DateTime endx = new com.google.api.client.util.DateTime(st);
                    DateFormat dfe = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                    Date star = dfe.parse(st);
                    Date etar = dfe.parse(en);
                    //Date star = new Date(strx.getValue());
                    //Date etar = new Date(endx.getValue());
                    DateTime str = new DateTime(star);
                    DateTime end = new DateTime(etar);

                    intrr = new Interval(str,end);
                    //String starter = String.valueOf(str.getYear());
                    //String ender = end.getDayOfMonth();

                    String starter;
                    String ender;
                    if ( str.getMinuteOfHour() < 10 ) {
                        starter = str.getMonthOfYear()+"/"+str.getDayOfMonth()+"/"+str.getYear()+" on "
                                +str.getHourOfDay()+":0"+str.getMinuteOfHour();
                    }
                    else {
                        starter = str.getMonthOfYear()+"/"+str.getDayOfMonth()+"/"+str.getYear()+" on "
                                +str.getHourOfDay()+":"+str.getMinuteOfHour();
                    }

                    if (end.getMinuteOfHour() < 10 ) {
                        ender = end.getMonthOfYear()+"/"+end.getDayOfMonth()+"/"+end.getYear()+" on "
                                +end.getHourOfDay()+":0"+end.getMinuteOfHour();
                    }

                    else {
                        ender = end.getMonthOfYear()+"/"+end.getDayOfMonth()+"/"+end.getYear()+" on "
                                +end.getHourOfDay()+":"+end.getMinuteOfHour();
                    }
                    temp = "From "+starter+" till "+ender;
                    if ( temp == null ) {
                        t[ctr] = " empty ";

                    }
                    else
                        t[ctr] = temp;
                }
                else{
                    intrr = null;
                    t[ctr] = " friend not available at additional time ";
                }
                intL.add(ctr,intrr);
                ctr++;
            }
            builder.setItems(t, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onSelectedEvent(t, which, intL);
                }
            });
            builder.show();
        }

        public void onSelectedEvent( String [] t, int which, List<Interval> intL) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            ProgressDialog mProg = new ProgressDialog(mContext);
            mProg.setMessage("Adding Selected Time...");
            mProg.show();
            AddRawEventTask aret;
            aret = new AddRawEventTask(intL.get(which),title, loc, emailID, mProg, mContext);
            aret.execute();


        }

    }

    private class AddRawEventTask extends AsyncTask<Void,Void,Void> {
        private Exception mLastError = null;
        private String title;
        private String loc;
        private String id;
        private String startDate;
        private String endDate;
        private DateFormat df;
        private Interval intr;
        private String emailID;
        com.google.api.client.util.DateTime startTime;
        com.google.api.client.util.DateTime endTime;
        Date start;
        Date end;
        ProgressDialog mProg;
        Context mContext;
        public AddRawEventTask(Interval intrr, String ti, String lo, String eID, ProgressDialog mP, Context c ) {
            mContext = c;
            mProg = mP;
            intr = intrr;
            title = ti;
            loc = lo;
            emailID = eID;
            String startDate = intr.getStart().toString();
            String endDate = intr.getEnd().toString();
            df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
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
            } catch (UserRecoverableAuthIOException e) {
                e.printStackTrace();
            } catch ( IOException e) {
                e.printStackTrace();
            }

            try {
                mService.events().insert(emailID, event).execute();
            } catch ( UserRecoverableAuthIOException e ) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void vo) {
            mProg.dismiss();
            startActivity(new Intent(mContext, WeeklyViewActivity.class));
        }
    }

}

