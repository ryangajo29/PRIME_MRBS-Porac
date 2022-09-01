package com.primemrbs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class NewMtrFragment extends AppCompatActivity {

    InputMethodManager imm;
    DatabaseHelper databaseHelper;
    String nm_pos;
    EditText ET_nm_name, ET_nm_mtrno, ET_nm_address, ET_nm_presrdg, ET_nm_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmtr_fragment);
        Bundle extras = getIntent().getExtras();

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        getSupportActionBar().setTitle("Add New Meter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ET_nm_name = findViewById(R.id.ET_nm_name);
        ET_nm_mtrno = findViewById(R.id.ET_nm_mtrno);
        ET_nm_address = findViewById(R.id.ET_nm_address);
        ET_nm_presrdg = findViewById(R.id.ET_nm_presrdg);
        ET_nm_note = findViewById(R.id.ET_nm_note);

        nm_pos = extras.getString("nmPos");
        databaseHelper = new DatabaseHelper(this);

        ET_nm_name.requestFocus();
        ET_nm_name.setInputType(InputType.TYPE_CLASS_TEXT);
        imm.hideSoftInputFromWindow(ET_nm_mtrno.getWindowToken(), 0);
        ET_nm_note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyAction, KeyEvent keyEvent) {
                if (keyAction == EditorInfo.IME_ACTION_DONE || KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()) {
                    if (TextUtils.isEmpty(ET_nm_mtrno.getText().toString()) || TextUtils.isEmpty(ET_nm_address.getText().toString()) || TextUtils.isEmpty(ET_nm_presrdg.getText().toString()) || TextUtils.isEmpty(ET_nm_note.getText().toString())) {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NewMtrFragment.this);
                        dlgAlert.setMessage("Meter Number, Address, Reading, and Note CANNOT BE EMPTY!");
                        dlgAlert.setTitle("Information required");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        dlgAlert.create().show();
                    } else {
                        databaseHelper.InsertNewMtr(ET_nm_name.getText().toString(), ET_nm_mtrno.getText().toString(), ET_nm_address.getText().toString(), ET_nm_presrdg.getText().toString(), ET_nm_note.getText().toString());

                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(NewMtrFragment.this);
                        dlgAlert.setMessage("New Meter Added");
                        dlgAlert.setTitle("Success");
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent i = new Intent(NewMtrFragment.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("nmPos", nm_pos);
                                startActivity(i);
                            }
                        });
                        dlgAlert.create().show();
                        return true;
                    }
                }
                return true;
            }
        });
        /*ET_nm_mtrno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                ET_nm_mtrno.setText("");
                ET_nm_mtrno.setFocusable(true);
                ET_nm_mtrno.setFocusableInTouchMode(true);
                ET_nm_mtrno.requestFocus();
                ET_nm_mtrno.setInputType(InputType.TYPE_CLASS_NUMBER);
                imm.hideSoftInputFromWindow(ET_nm_mtrno.getWindowToken(), 1);
                }
            }
        });
        ET_nm_presrdg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ET_nm_presrdg.setText("");
                    ET_nm_presrdg.setFocusable(true);
                    ET_nm_presrdg.setFocusableInTouchMode(true);
                    ET_nm_presrdg.requestFocus();
                    ET_nm_presrdg.setInputType(InputType.TYPE_CLASS_NUMBER);
                    imm.hideSoftInputFromWindow(ET_nm_presrdg.getWindowToken(), 1);
                }
            }
            });
        ET_nm_note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ET_nm_note.setText("");
                    ET_nm_note.setFocusable(true);
                    ET_nm_note.setFocusableInTouchMode(true);
                    ET_nm_note.requestFocus();
                    ET_nm_note.setInputType(InputType.TYPE_CLASS_TEXT);
                    imm.hideSoftInputFromWindow(ET_nm_note.getWindowToken(), 1);
                }
            }
        });
        ET_nm_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ET_nm_name.setText("");
                    ET_nm_name.setFocusable(true);
                    ET_nm_name.setFocusableInTouchMode(true);
                    ET_nm_name.requestFocus();
                    ET_nm_name.setInputType(InputType.TYPE_CLASS_TEXT);
                    imm.hideSoftInputFromWindow(ET_nm_name.getWindowToken(), 1);
                }
            }
        });
        ET_nm_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ET_nm_address.setText("");
                    ET_nm_address.setFocusable(true);
                    ET_nm_address.setFocusableInTouchMode(true);
                    ET_nm_address.requestFocus();
                    ET_nm_address.setInputType(InputType.TYPE_CLASS_TEXT);
                    imm.hideSoftInputFromWindow(ET_nm_address.getWindowToken(), 1);
                }
            }
        });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ffmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.ff_save) {
            if (TextUtils.isEmpty(ET_nm_mtrno.getText().toString()) || TextUtils.isEmpty(ET_nm_address.getText().toString()) || TextUtils.isEmpty(ET_nm_presrdg.getText().toString()) || TextUtils.isEmpty(ET_nm_note.getText().toString())) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NewMtrFragment.this);
                dlgAlert.setMessage("Meter Number, Address, Reading, and Note CANNOT BE EMPTY!");
                dlgAlert.setTitle("Information required");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.create().show();
            } else {
                databaseHelper.InsertNewMtr(ET_nm_name.getText().toString(), ET_nm_mtrno.getText().toString(), ET_nm_address.getText().toString(), ET_nm_presrdg.getText().toString(), ET_nm_note.getText().toString());

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(NewMtrFragment.this);
                dlgAlert.setMessage("New Meter Added");
                dlgAlert.setTitle("Success");
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent i = new Intent(NewMtrFragment.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("nmPos", nm_pos);
                        startActivity(i);
                    }
                });
                dlgAlert.create().show();
                return true;
            }
        }
        return false;
    }
}
