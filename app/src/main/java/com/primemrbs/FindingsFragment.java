package com.primemrbs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by JesCarlo on 10/26/2017.
 */

public class FindingsFragment extends AppCompatActivity {
    //GENERAL VARIABLES//
    DatabaseHelper databaseHelper;
    Cursor FFcursor;
    String ffSP, localtime, selff;
    TextView TVffpos, TVffacctno, TVffname, TVffmtrno, ETffremarks, ff_des, ff_code;
    String ffcode_temp, ffdes_temp;
    ArrayList<String> ffcode;
    ArrayList<String> ffdes;

    //THIS IS THE VERY FIRST PROCEDURE DURING THE LOADING OF THE FindingsFragment.java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findings_fragment);
        Bundle extras = getIntent().getExtras();

        //THIS SETS THE MENU BAR ON THE TOP WITH A TITLE//
        getSupportActionBar().setTitle("Findings & Remarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //INITIALIZE FORM CONTROLS AND CONNECT THEM TO VARIABLES//
        TVffpos = findViewById(R.id.TV_ffPos);
        TVffacctno = findViewById(R.id.TV_ffacctno);
        TVffname = findViewById(R.id.TV_ffname);
        TVffmtrno = findViewById(R.id.TV_ffmtrno);
        ETffremarks = (EditText) findViewById(R.id.ET_ffremarks);
        ffcode = new ArrayList<String>();
        ffdes = new ArrayList<String>();

        //GET THE CURRENT DATE AND TIME//
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat time = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a");
        time.setTimeZone(TimeZone.getDefault());
        localtime = time.format(currentLocalTime);

        //FETCHING THE DATA FROM THE EXTRAS LOADED FROM MainActivity.java AND LOADING THE DATA
        //INTO FORM CONTROLS TO BE DISPLAYED ON THE FIELD FINDINGS FORM//
        TVffpos.setText(extras.getString("ffPos"));
        TVffacctno.setText(extras.getString("ffacctno"));
        TVffname.setText(extras.getString("ffname"));
        TVffmtrno.setText(extras.getString("ffmtrno"));
        selff = extras.getString("selff");

        //INITIALIZING THE databaseHelper AND LOADING FIELD FINDINGS DATA TO THE FFcursor//
        databaseHelper = new DatabaseHelper(this);
        FFcursor = databaseHelper.fetchFF();

        //LOADING DATA FROM THE FFcursor TO A DROP DOWN LIST//
        if (FFcursor.moveToFirst()) {
            do {
                ffcode.add(FFcursor.getString(FFcursor.getColumnIndex("F_CODE")));
                ffdes.add(FFcursor.getString(FFcursor.getColumnIndex("F_DESC")));
            }
            while (FFcursor.moveToNext());
        }
        //INITIALIZING AND SETTING THE FIELD FINDINGS LIST ON A DROP DOWN LIST/SPINNER//
        Spinner ffSpinner = findViewById(R.id.ff_spinner);
        ffSpinner.setAdapter(new MyAdapter(this, R.layout.findings_row, ffdes));
        ffSpinner.setSelection(((ArrayAdapter<String>)ffSpinner.getAdapter()).getPosition(selff));
        ffSpinner.setFocusable(true);
        ffSpinner.setFocusableInTouchMode(true);
        ffSpinner.requestFocus();
        ffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //THIS PROCEDURE TAKES EFFECT WHEN AN ITEM IS CLICKED FROM THE DROP DOWN LIST//
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ffdes_temp = ff_des.getText().toString();
                ffcode_temp = ff_code.getText().toString();
                ETffremarks.setText(ffdes_temp);
                //A TEMPORARY VARIABLE WILL BE LOADED WITH THE FIELD FINDINGS DETAILS THAT IS CLICKED AND WILL BE SAVED
                //TO THE CONSUMER DATA ONCE SAVE IS CLICKED//
                if (ffdes_temp.equals("None") || ffdes_temp.equals("") || ffdes_temp.equals("none")) {
                    ETffremarks.setText(selff);
                }
                Log.d("FFActivity", "ITEM CLICK " + ffdes_temp);
            }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //DO NOTHING
                }
            });
        //IF USER CLICKS ON THE ENTER BUTTON IN THE ON SCREEN KEYBOARD OR PHYSICAL KEYBOARD, THIS PROCEDURE WILL TAKE EFFECT//
        ETffremarks.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyAction, KeyEvent keyEvent) {
                Intent i = new Intent(FindingsFragment.this, MainActivity.class);
                //CLEARS THE EXTRA DATA AND LOADS NEW DATA//
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //IF USER CLICKS ON THE ENTER BUTTON IN THE PHYSICAL KEYBOARD OR ON SCREEN KEYBOARD, FIELD FINDINGS WILL BE SAVED FOR THE
                //CONSUMER THAT IS SHOWN ON THE SCREEN//
                if (keyAction == EditorInfo.IME_ACTION_DONE || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    //PROCEDURE TO SAVE THE FIELD FINDINGS OF THE CONSUMER IN THE DATABASE//

                    Date currentLocalTime = cal.getTime();
                    DateFormat time1 = new SimpleDateFormat("KK:mm:ss a");
                    String localtime1 = time1.format(currentLocalTime);

                    databaseHelper.updateFF(ETffremarks.getText().toString(), ffcode_temp, ETffremarks.getText().toString(), "R", TVffacctno.getText().toString(),localtime1);
                    //THE EXTRA WITH THE CURRENTLY SAVED RECORD POSITION WILL BE THROWN BACK TO THE MAIN FORM
                    //SO THE USER WILL LOAD BACK TO THE CURRENT RECORD POSITION AFTER RELOADING THE DATA
                    //UPON FINISHING THIS ACTIVITY, AND RELOADING BACK TO THE MAIN FORM//
                    i.putExtra("FF_Pos", TVffpos.getText().toString());
                    finish();
                    startActivity(i);
                }
                return true;
            }
        });
    }
    //ADAPTER USED TO INITIATE AND POPULATE THE DATA CONNECTION TO THE DROP DOWN LIST//
    public class MyAdapter extends ArrayAdapter<String>
    {

        public MyAdapter(Context context, int textViewResourceId, ArrayList<String> objects)
        {
            super(context, textViewResourceId, objects);
        }
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.findings_row, parent, false);
            ff_des = row.findViewById(R.id.TV_ffdes);
            ff_des.setText(ffdes.get(position));
            ff_code = row.findViewById(R.id.TV_ffcode);
            ff_code.setText(ffcode.get(position));
            return row;
        }
    }
    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ffmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        //IF USER CLICKS ON THE SAVE BUTTON IN THE MENU ITEM, FIELD FINDINGS WILL BE SAVED FOR THE
        //CONSUMER THAT IS SHOWN ON THE SCREEN//
        if (item.getItemId() == R.id.ff_save) {
            Intent i = new Intent(FindingsFragment.this, MainActivity.class);
            //CLEARS THE EXTRA DATA AND LOADS NEW DATA//
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //PROCEDURE TO SAVE THE FIELD FINDINGS OF THE CONSUMER IN THE DATABASE//
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            Date currentLocalTime = cal.getTime();
            DateFormat time1 = new SimpleDateFormat("KK:mm:ss a");
            String localtime1 = time1.format(currentLocalTime);
            databaseHelper.updateFF(ETffremarks.getText().toString(), ffcode_temp, ETffremarks.getText().toString(), "R", TVffacctno.getText().toString(),localtime1);
            i.putExtra("FF_Pos", TVffpos.getText().toString());
            finish();
            startActivity(i);
        }
        return true;
    }
}
