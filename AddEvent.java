package com.team9.calbuddy;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.Dialog;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Arrays;


import java.io.IOException;


public class AddEvent extends AppCompatActivity
        implements View.OnClickListener {

    GoogleAccountCredential mCredential = null;
    com.google.api.services.calendar.Calendar mService = null;
    EditText mEdit = null;

    Button btn_sd, btn_ed;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    Button btn_st, btn_et;
    int hour_x, minute_x;

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
    }

    /* displays calendar when users pick up date */
    public void showDialogOnButtonClick() {
        btn_sd = (Button)findViewById(R.id.button_SD);
        btn_ed = (Button)findViewById(R.id.button_ED);
        btn_sd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
        btn_ed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }
/*
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListner, year_x, month_x, day_x);
        return null;
    }
*/
    private DatePickerDialog.OnDateSetListener sdpickerListner
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            Toast.makeText(AddEvent.this, year_x+ "/" + month_x + "/" + day_x, Toast.LENGTH_LONG).show();
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
        }
    };

    public void showTimePickerDialog() {
        btn_st = (Button)findViewById(R.id.button_ST);
        btn_et = (Button)findViewById(R.id.button_ET);
        btn_st.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
        btn_et.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );

    }

    static final int startDate = 0;
    static final int startTime = 5;
    static final int endDate = 6;
    static final int endTime = 7;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case startDate:
                return new DatePickerDialog(AddEvent.this, sdpickerListner, year_x, month_x, day_x);
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
        }
    };

    protected TimePickerDialog.OnTimeSetListener etTimePickerListener
            = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            Toast.makeText(AddEvent.this, hour_x+ ": " + minute_x + ": " + day_x, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public void onClick(View v) {
        AsyncTask<Void, Void, Void> task = new AddEventTask();
        task.execute();
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
        private String startTime;
        private String endTime;

        public AddEventTask() {
            mEdit = (EditText) findViewById(R.id.titleEdit);
            title = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.locEdit);
            loc = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.idEdit);
            id = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.startEdit);
            startDate = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.startTimeEdit);
            startTime = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.endEdit);
            endDate = mEdit.getText().toString();
            mEdit = (EditText) findViewById(R.id.endTimeEdit);
            endTime = mEdit.getText().toString();


            try {
                addEventToCal();
            } catch (Exception e) {

            }
        }

        private void addEventToCal() throws IOException {

            //mProgress.setMessage("Added Event");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //String eventText = "TESTING at UCSD on June 3rd 10am-10:25am";
            String calendarId = "primary";
            String eventText = title + " at " + loc + " from " + startDate + " " + startTime + " till " + endDate + " " + endTime;
            try {
                AddEvents();
                mService.events().quickAdd(calendarId, eventText).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void AddEvents() throws IOException {
            //mService.events().quickAdd(calendarId, eventText).execute();
            Event event = new Event()
                .setSummary("Google I/O 2015")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("A chance to hear more about Google's developer products.");

            DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/Los_Angeles");
            event.setStart(start);

            DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("America/Los_Angeles");
            event.setEnd(end);

            String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
            event.setRecurrence(Arrays.asList(recurrence));

            EventAttendee[] attendees = new EventAttendee[] {
                    new EventAttendee().setEmail("lpage@example.com"),
                    new EventAttendee().setEmail("sbrin@example.com"),
            };
            event.setAttendees(Arrays.asList(attendees));

            EventReminder[] reminderOverrides = new EventReminder[] {
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(10),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);

            String calendarId = "primary";
            try {
                event = mService.events().insert(calendarId, event).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}


