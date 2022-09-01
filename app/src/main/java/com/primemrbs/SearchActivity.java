package com.primemrbs;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    InputMethodManager imm;
    Spinner SP_search;
    EditText ET_search;
    int textlength = 0;
    DatabaseHelper database;
    RecyclerView recyclerView;
    SearchAdapter searchRecycler;
    List<DataModel> datamodel;
    Cursor cursorVersion;
    String Zonetag, _load;
    int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();

        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        getSupportActionBar().setTitle("Search..");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        datamodel = new ArrayList<DataModel>();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);

        database = new DatabaseHelper(SearchActivity.this);
        datamodel = database.loadSearchAll();

        searchRecycler = new SearchAdapter(datamodel);
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchRecycler);

        SP_search = (Spinner) findViewById(R.id.SP_search);
        ET_search = (EditText) findViewById(R.id.ET_search);

        ET_search.requestFocus();
        imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
        ET_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
        ET_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //after the change calling the method and passing the search input
                filter(s.toString());
            }
        });
        SP_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if (selectedItem.equals("Meter No.")) {
                    ET_search.setText("");
                    //ET_search.setShowSoftInputOnFocus(false);
                    ET_search.setFocusable(true);
                    ET_search.setFocusableInTouchMode(true);
                    ET_search.requestFocus();
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
                }
                if (selectedItem.equals("Name")) {
                    ET_search.setText("");
                    //ET_search.setShowSoftInputOnFocus(true);
                    ET_search.setFocusable(true);
                    ET_search.setFocusableInTouchMode(true);
                    ET_search.requestFocus();
                    ET_search.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(ET_search, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 1);
                }
                if (selectedItem.equals("Account No.")) {
                    ET_search.setText("");
                    //ET_search.setShowSoftInputOnFocus(true);
                    ET_search.setFocusable(true);
                    ET_search.setFocusableInTouchMode(true);
                    ET_search.requestFocus();
                    ET_search.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(ET_search, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 1);
                }
                if (selectedItem.equals("Old Account No.")) {
                    ET_search.setText("");
                    //ET_search.setShowSoftInputOnFocus(true);
                    ET_search.setFocusable(true);
                    ET_search.setFocusableInTouchMode(true);
                    ET_search.requestFocus();
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
                }
                if (selectedItem.equals("Unread")) {
                    ET_search.setText("U");
                    //ET_search.setShowSoftInputOnFocus(false);
                    ET_search.setFocusable(false);
                    ET_search.setFocusableInTouchMode(false);
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        ET_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !SP_search.getSelectedItem().toString().equals("Name")) {
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
                }
            }
        });
        ET_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SP_search.getSelectedItem().toString().equals("Name")) {
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 0);
                } else{
                    imm.hideSoftInputFromWindow(ET_search.getWindowToken(), 1);
                }
            }

        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
    void filter(String text){
        List<DataModel> temp = new ArrayList();
        for(DataModel d: datamodel){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            String SPsearch = SP_search.getSelectedItem().toString();
            if (SPsearch.equals("Meter No.")) {
                if(d.getMtrno().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            else if (SPsearch.equals("Name")) {
                if(d.getName().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            else if (SPsearch.equals("Account No.")) {
                if(d.getAcctno().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            else if (SPsearch.equals("Old Account No.")) {
                if(d.getOldacctno().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }
            else if (SPsearch.equals("Unread")) {
                if(d.getReadstat().toLowerCase().equals(text.toLowerCase())){
                    temp.add(d);
                }
            }
        }
        //update recyclerview
        searchRecycler.updateList(temp);
    }
}
