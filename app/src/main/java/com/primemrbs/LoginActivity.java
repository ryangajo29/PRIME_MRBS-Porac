package com.primemrbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {

    //GENERAL VARIABLES//
    InputMethodManager imm;
    TextView TV_vDate, TV_vTime;
    Button BTN_login;
    Spinner SP_username;
    String username;
    EditText ET_password;
    DatabaseHelper databaseHelper;

    //THIS IS THE VERY FIRST PROCEDURE DURING THE LOADING OF THE LoginActivity.java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //HIDE THE ON SCREEN KEYS UPON APPLICATION START//
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        //INITIALIZE FORM CONTROLS AND CONNECT THEM TO VARIABLES//
        BTN_login = findViewById(R.id.BTN_login);
        SP_username = findViewById(R.id.SP_username);
        ET_password = findViewById(R.id.ET_password);
        TV_vDate = findViewById(R.id.TV_vDate);
        TV_vTime = findViewById(R.id.TV_vTime);

        //INITIALIZE DATABSE AND LOAD DATA INTO A DATABASE ADAPTER AS A LIST//
        databaseHelper = new DatabaseHelper(this);
        ArrayList<String> listpro = databaseHelper.loadUsers();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.txt, listpro);
        SP_username.setAdapter(adapter);

        //INITIALIZE, SET, AND DISPLAY THE CURRENT DATE//
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat time = new SimpleDateFormat("MM/dd/yyyy");
        time.setTimeZone(TimeZone.getDefault());
        String fdate = time.format(currentLocalTime);
        TV_vDate.setText(fdate + ", ");

        //PROCEDURE USED TO CREATE A RUNNING TIME//
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                TV_vTime.setText(new SimpleDateFormat("hh:mm:ss a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        //INITIALIZE THE ET_password TEXTBOX//
        ET_password.setText(null);
        ET_password.setFocusableInTouchMode(true);
        ET_password.requestFocus();

        //CREATE KEY LISTENER PROCEDURE FOR THE KEY EVENT ON THE ET_password FIELD//
        //IF PHYSICAL KEYBOARD ENTER KEY IS PRESSED//
        ET_password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                username = SP_username.getSelectedItem().toString();
                //If the event is a keydown event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loadUser();
                    return true;
                }
                return false;
            }
        });
        //IF ON SCREEN ENTER KEY IS PRESSED//
        ET_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                username = SP_username.getSelectedItem().toString();
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    loadUser();
                    handled = true;
                }
                return handled;
            }
        });
        //IF ET_password IS CLICKED, HIDE THE ON SCREEN KEYBOARD//
        ET_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(ET_password.getWindowToken(), 0);
            }

        });
        //CLICKING ON BTN_login WILL INITIATE THE loadUser() PROCEDURE//
        BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = SP_username.getSelectedItem().toString();
                loadUser();
            }
        });
    }
    @Override
    public void onBackPressed() { }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }
    //INITIATE COMMANDS FOR THE MENU ITEMS//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //PRESSING EXIT WILL EXIT THE USER FROM THE APPLICATION//
        if (item.getItemId() == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Confirmation");
            builder.setCancelable(false);
            builder.setMessage("Do you want to exit the application?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ET_password.requestFocus();
                }
            });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setFocusable(true);
                    positive.setFocusableInTouchMode(true);
                    positive.requestFocus();
                }
            });
            alert.show();
            return true;
        }
        return false;
    }
    //PROCEDURE USED TO CHECK AND INITIATE LOGIN FOR THE USER IF USERNAME AND PASSWORD IS CORRECT//
    void loadUser()
    {
        boolean isExist = databaseHelper.checkUserExist(username, ET_password.getText().toString());

        if (isExist)
        {
            finish();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);

            databaseHelper.InsertUser(username);
        }
        else
        {
            ET_password.setText(null);
            Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }
}
