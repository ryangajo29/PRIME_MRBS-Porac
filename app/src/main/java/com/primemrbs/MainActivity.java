//PACKAGE AND LIBRARIES BEING USED//
package com.primemrbs;
import android.app.Activity;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.woosim.printer.WoosimBarcode;
import com.woosim.printer.WoosimCmd;
import com.woosim.printer.WoosimImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    //GENERAL VARIABLES//
    String presDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
    Integer _CursorID;
    String _SpacedBillNo, dueDate_save, _id, _qrpayable;
    String localtime = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a", Locale.getDefault()).format(new Date());
    InputMethodManager imm;
    String str22, str23, str24, str25, str26, str27, str28, str29, str30, str31;
    String SearchString = null, FFString = null, UserString = null, NMString = null;
    String ReadCounter, PrintCounter, AfterDue, GPSHLoc;
    Cursor cursorReadTotalCUM, cursorSummaryRead;
    Cursor cursor, cursorWRate, cursorFactors, cursorRead, cursorPrinter;
    Cursor cursorLastCons;
    Cursor cursorUnread, cursorUser, cursorActive, cursorInactive, cursorNewMtrs;
    DatabaseHelper databaseHelper;
    Double high_comp, low_comp, _ReadCounter, _PrintCounter, EVATCharge, OTHERS_total;
    String c_penStat, PrimeBarCode, PrimePayable;
    LocationManager locationManager;
    Double WRmin, WR1, WR2, WR3, WR4, WR5, BillType, c_otherChrgs;
    Double temp_amount, CUM;
    Double SenDisc = 0.00;
    String f1_acctno, _printer, c_oldAcctNo;
    EditText ETpresrdg, ETprevrdg;
    TextView f1_recno, fl_name, fl_mtrno, f1_view_acctno, f1_oldacctno, TV_gpstag,
            ETcum, ETbillamt, TVaddress, TVrecstat, TV_status, TV_gpstagdetails,
            f1_findings, TV_gps, TV_gpsstat;
    ImageButton BTN_first, BTN_prev, BTN_next, BTN_last, BTN_saveGPS;
    String c_mtrsize, c_cmcode, c_billtype, c_cmdesc;
    String c_billDate, c_aveCum, c_arrears, c_bookno, c_atmref, c_billno;
    String c_lastcons;
    String c_prevRdgDate, c_dueDate, c_discodate, c_activity, c_mosarr, c_CSStat, c_seq, c_evat;
    String c_lh_rdg = "", c_status, c_noofdays, OthersDesc;
    String c_ffcode = "";
    String Disc1, Disc2, Disc3, Disc4, Disc5, Disc6, Disc7, Disc8, Disc9, Disc10;
    String Oth1, Oth2, Oth3, Oth4, Oth5, Oth6, Oth7, Oth8, Oth9, Oth10;
    String D1, D2, D3, D4, D5, telno1, telno2, GPSTAG;
    String BP1, BP2, BP3, BP4, BP5, BP6, BP7, BP8, BP9, BP10, BP11, BP12;
    String BPC1, BPC2, BPC3, BPC4, BPC5, BPC6, BPC7, BPC8, BPC9, BPC10, BPC11, BPC12;
    Calendar DBP1 = Calendar.getInstance();
    Calendar DBP2 = Calendar.getInstance();
    Calendar DBP3 = Calendar.getInstance();
    Calendar DBP4 = Calendar.getInstance();
    Calendar DBP5 = Calendar.getInstance();
    Calendar DBP6 = Calendar.getInstance();
    Calendar DBP7 = Calendar.getInstance();
    String S_DBP1, S_DBP2, S_DBP3, S_DBP4, S_DBP5, S_DBP6, S_DBP7, S_DBP8, S_DBP9, S_DBP10, S_DBP11, S_DBP12;
    Date PresBP;
    CheckBox autoprint;
    //AMOUNT FORMAT USED TO DISPLAY 2 DECIMALS//
    DecimalFormat formatter = new DecimalFormat("#,##0.00");
    DecimalFormat Initformatter = new DecimalFormat("#.00");

    //Message type & name received from the BluetoothPrintService handler
    protected static final int MESSAGE_DEVICE_NAME = 1;
    protected static final int MESSAGE_TOAST = 2;
    protected static final int MESSAGE_READ = 3;
    protected static final String DEVICE_NAME = "device_name";
    protected static final String TOAST = "toast";

    //INTENT REQUEST CODES. DO NOT EDIT AS THIS ARE FIXED//
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    protected static BluetoothPrintService mPrintService = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //THIS IS THE VERY FIRST PROCEDURE DURING THE LOADING OF THE MainActivity.java
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //USED TO SET THE CONTENT TO BE VIEWED AND USED IN activity_main.xml//
        setContentView(R.layout.activity_main);
        //ENSURE STORAGE PERMISSIONS CAN BE SET UPON THE LOADING OF THIS FORM//
        //Added By Jake
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
        }
        //End of Added Jake
        verifyStoragePermissions(this);
        //THIS LINE OF CODE IS USED TO ALWAYS HIDE THE ON-SCREEN KEYBOARD//
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        //USED TO INITIATE EXTRAS FROM PREVIOUSLY LOADED FORMS//
        Bundle extras = getIntent().getExtras();
        //USED TO INITIATE THE TOOLBAR AND DRAWER OF THE FORM//
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.bringToFront();
        drawer.requestLayout();
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        autoprint = findViewById(R.id.CB_alwaysPrint);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        //INITIALIZE FORM CONTROLS AND CONNECT THEM TO VARIABLES//
        fl_name = findViewById(R.id.TV_name);
        f1_view_acctno = findViewById(R.id.TV_acctno);
        f1_oldacctno = findViewById(R.id.TV_oldacctno);
        fl_mtrno = findViewById(R.id.TV_mtrno);
        ETpresrdg = findViewById(R.id.ET_presrdg);
        ETprevrdg = findViewById(R.id.ET_prevrdg);
        ETbillamt = findViewById(R.id.ET_bill);
        f1_findings = findViewById(R.id.TV_findings);
        ETcum = findViewById(R.id.ET_cum);
        f1_recno = findViewById(R.id.TV_recno);
        TVaddress = findViewById(R.id.TV_address);
        TV_gpstag = findViewById(R.id.TV_gpstag);
        TV_gpsstat = findViewById(R.id.TV_GPSstat);
        TV_gpstagdetails = findViewById(R.id.TV_gpstagdetails);
        TV_gps = findViewById(R.id.TV_GPS);
        TV_status = findViewById(R.id.TV_status);
        BTN_first = findViewById(R.id.BTN_first);
        BTN_prev = findViewById(R.id.BTN_prev);
        BTN_next = findViewById(R.id.BTN_next);
        BTN_last = findViewById(R.id.BTN_last);
        BTN_saveGPS = findViewById(R.id.BTN_savegps);
        TVrecstat = findViewById(R.id.TV_recstat);
        ETprevrdg.setFocusable(false);
        ETcum.setFocusable(false);
        ETbillamt.setFocusable(false);

        //INITIALIZE DATABASEHELPER//
        databaseHelper = new DatabaseHelper(this);

        cursorFactors = databaseHelper.fetchFactors();
        D1 = cursorFactors.getString(0);
        D2 = cursorFactors.getString(1);
        D3 = cursorFactors.getString(2);
        D4 = cursorFactors.getString(3);
        D5 = cursorFactors.getString(4);
        GPSTAG = cursorFactors.getString(9);

        telno1 = cursorFactors.getString(7);
        telno2 = cursorFactors.getString(8);
        if (telno1 == null) {
            telno1 = "";
        }
        if (telno2 == null) {
            telno2 = "";
        }

        //LOAD GPS UPON LOGIN//
        if (GPSTAG.equals("TRUE")) {
            TV_gpsstat.setText("GPS LOADING..");
            TV_gpsstat.setTextColor(Color.GREEN);
        } else {
            TV_gpsstat.setText("GPS DISABLED");
            TV_gpsstat.setTextColor(Color.GRAY);
        }

        //ONCE DATABASEHELPER IS INITIALIZED, LOAD QUERY INTO CURSORS//
        //FETCH PRINTER BT ADDRESS AND LOAD IT INTO ITS OWN CURSOR//
        cursorPrinter = databaseHelper.fetchPrinter();
        _printer = cursorPrinter.getString(0);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //FETCH CONSUMER FILE DATA AND LOAD IT INTO ITS OWN CURSOR//
        cursor = databaseHelper.fetchAll();
        GetSQLiteDatabaseRecords();
        //FETCH CURSOR DETAILS FOR WATER RATES BASED ON SELECTED CONSUMER TYPE//
        cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
        GetSQLiteDatabaseWRates();
        //FETCH USER DETAILS AND LOAD IT INTO ITS OWN CURSOR//
        cursorUser = databaseHelper.fetchFixedUser();
        UserString = cursorUser.getString(0);

        //ON LOAD, TEXT CURSOR WILL FOCUS ON THE PRESENT READING TEXTBOX//
        ETpresrdg.setFocusableInTouchMode(true);

        //LOAD EXTRAS FROM STRINGS GATHERED FROM PREVIOUS FORMS, IF ANY EXTRAS EXIST. IF NOT, ELSE STATEMENT WILL TAKE EFFECT//
        if (extras != null) {
            SearchString = extras.getString("SearchFlag");
            FFString = extras.getString("FF_Pos");
            NMString = extras.getString("nmPos");
            //IF CONSUMER EXISTS IN THE SEARCH FORM, SELECT ACCOUNT NUMBER LOADED TO THE SearchString Extra FROM THE SEARCH FROM//
            //AFTER LOADING THE CONSUMER, RELOAD ALL DATA THRU THE GetSQLiteDatabaseRecords() PROCEDURE//
            //THE GetSQLiteDatabaseWRates() PROCEDURE RELOADS ALL THE RATES//
            if (SearchString != null) {
                cursor.moveToFirst();
                GetSQLiteDatabaseRecords();
                //THIS WHILE STATEMENT IS USED TO GO BACK TO THE LAST READ CONSUMER//
                while (!f1_acctno.equals(SearchString)) {
                    cursor.moveToNext();
                    GetSQLiteDatabaseRecords();
                }
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
            }
            //IF USER CLICKS ON A FIELD FINDING, THE SELECTED FIELD FINDING IS STORED IN THE FFString EXTRA,
            //THEN LOADED HERE//
            if (FFString != null) {
                cursor.moveToFirst();
                GetSQLiteDatabaseRecords();
                //THIS WHILE STATEMENT IS USED TO GO BACK TO THE LAST READ CONSUMER//
                while (!f1_acctno.equals(FFString)) {
                    cursor.moveToNext();
                    GetSQLiteDatabaseRecords();
                }
                //IF FIELD FINDING IS PRESENT AND PRESENT READING IS BLANK, USER IS REQUIRED TO TAKE A PICTURE TO VERIFY
                //THE FIELD FINDINGS//
                if (TextUtils.isEmpty(ETpresrdg.getText())) {
                    CameraFunc();
                }
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
            }
            //THIS LOADS THE MAIN FORM GOES BACK TO THE LAST READ CONSUMER//
            if (NMString != null) {
                cursor.moveToFirst();
                GetSQLiteDatabaseRecords();
                //THIS WHILE STATEMENT IS USED TO GO BACK TO THE LAST READ CONSUMER//
                while (!f1_acctno.equals(NMString)) {
                    cursor.moveToNext();
                    GetSQLiteDatabaseRecords();
                }
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
            }
            //ON LOAD, TEXT CURSOR WILL FOCUS ON THE PRESENT READING TEXTBOX//
            ETpresrdg.requestFocus();
            ETpresrdg.setSelection(ETpresrdg.getText().length());
            //IF NO EXTRAS EXIST, THIS ELSE STATEMENT WILL TAKE EFFECT. THIS WILL ALWAYS TAKE EFFECT DURING FIRST LOGIN//
        } else {
            //ON LOAD, TEXT CURSOR WILL FOCUS ON THE PRESENT READING TEXTBOX//
            ETpresrdg.requestFocus();
            ETpresrdg.setSelection(ETpresrdg.getText().length());
            //THIS WILL CHECK IF THE APPLICATION IS PAIRED TO A BLUETOOTH PRINTER. IF NO BLUETOOTH PRINTER IS PAIRED,
            //A LIST OF BLUETOOTH DEVICES WILL SHOW UP FOR THE USER TO PAIR TO THE PRINTER//
            Intent intent;
            // Launch the DeviceListActivity to see devices and do scan
            if (mPrintService != null) mPrintService.start();
            intent = new Intent(this, DeviceCheck.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
        }
        //IF THE PRESENT READING TEXTBOX GETS FOCUSED BY THE CURSOR, ON SCREEN KEYBOARD HIDES//
        ETpresrdg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    imm.hideSoftInputFromWindow(ETpresrdg.getWindowToken(), 0);
                }
            }
        });
        //THESE KEY EVENTS SETS WHAT THE PHYSICAL KEYS WILL PERFORM WHILE THE CURSOR IS FOCUSED ON THE PRESENT READING TEXTBOX//
        ETpresrdg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //KEYDOWN EVENT ON ANY KEY//
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                switch (keyCode) {
                    //IF LEFT KEY IS PRESSED, NAVIGATE TO PREVIOUS CONSUMER//
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        NavPrev();
                        break;
                    //IF UP KEY IS PRESSED, NAVIGATE TO PREVIOUS UNREAD CONSUMER//
                    case KeyEvent.KEYCODE_DPAD_UP:
                        NavPrevUnread();
                        break;
                    //IF DOWN KEY IS PRESSED, NAVIGATE TO NEXT UNREAD CONSUMER//
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        NavNextUnread();
                        break;
                    //IF RIGHT KEY IS PRESSED, NAVIGATE TO NEXT CONSUMER//
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        NavNext();
                        break;
                    //IF F3 KEY IS PRESSED, NAVIGATE TO NEXT UNREAD CONSUMER//
                    case KeyEvent.KEYCODE_F3:
                        NavNextUnread();
                        break;
                    //IF F2 KEY IS PRESSED, NAVIGATE TO PREVIOUS UNREAD CONSUMER//
                    case KeyEvent.KEYCODE_F2:
                        NavPrevUnread();
                        break;
                    //IF F4 KEY IS PRESSED, NAVIGATE TO NEXT CONSUMER//
                    case KeyEvent.KEYCODE_F4:
                        NavNext();
                        break;
                    //IF F1 KEY IS PRESSED, NAVIGATE TO PREVIOUS CONSUMER//
                    case KeyEvent.KEYCODE_F1:
                        NavPrev();
                        break;
                    case KeyEvent.KEYCODE_1:
                        ETpresrdg.setText(ETpresrdg.getText() + "1");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_2:
                        ETpresrdg.setText(ETpresrdg.getText() + "2");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_3:
                        ETpresrdg.setText(ETpresrdg.getText() + "3");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_4:
                        ETpresrdg.setText(ETpresrdg.getText() + "4");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_5:
                        ETpresrdg.setText(ETpresrdg.getText() + "5");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_6:
                        ETpresrdg.setText(ETpresrdg.getText() + "6");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_7:
                        ETpresrdg.setText(ETpresrdg.getText() + "7");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_8:
                        ETpresrdg.setText(ETpresrdg.getText() + "8");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_9:
                        ETpresrdg.setText(ETpresrdg.getText() + "9");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    case KeyEvent.KEYCODE_0:
                        ETpresrdg.setText(ETpresrdg.getText() + "0");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        break;
                    //IF THE PHYSICAL ENTER KEY IS PRESSED, CHECK IF READING IS PRESENT AND PROCEED TO EnterRdg() PROCEDURE.
                    //IF NOT, MESSAGE BOX WILL DISPLAY ASKING USER TO PLEASE INPUT READING//
                    case KeyEvent.KEYCODE_ENTER:
                        String original = "";
                        if (TextUtils.isEmpty(ETpresrdg.getText())) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("Please input Reading");
                            dlgAlert.setTitle("Attention");
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            dlgAlert.create().show();
                        } else if (databaseHelper.checkIfDownloadrePrint() == true) {
                            original = databaseHelper.fetchLastInput(f1_acctno);
                            if (original != ETpresrdg.getText().toString()) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("You are only reprinting. Editing the actual reading and are already proofread reading are not permitted. Entry reverted back to original.");
                                dlgAlert.setTitle("Attention");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                dlgAlert.create().show();
                                ETpresrdg.setText(original);
                            }
                            EnterRdg();
                        } else {
                            EnterRdg();
                        }
                        break;
                    //IF PERIOD KEY IS PRESSED, PERIOD WILL BE INDICATED IN THE PRESENT READING TEXTBOX//
                    case KeyEvent.KEYCODE_PERIOD:
                        ETpresrdg.setText(ETpresrdg.getText() + ".");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        String str = ETpresrdg.getText().toString();
                        if (str.length() >= 1) {
                            str = str.substring(0, str.length() - 1);
                            ETpresrdg.setText(str);
                            //IF CHARACTER LENGTH IS LESS THAN OR EQUAL TO 1, PRESENT READING WILL BE BLANK//
                        } else if (str.length() <= 1) {
                            ETpresrdg.setText("");
                            break;
                        }
                        break;
                    //IF ASTERISK KEY IS PRESSED, PERIOD WILL BE INDICATED IN THE PRESENT READING TEXTBOX//
                    case KeyEvent.KEYCODE_STAR:
                        ETpresrdg.setText(ETpresrdg.getText() + ".");
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                        String str1 = ETpresrdg.getText().toString();
                        if (str1.length() >= 1) {
                            str1 = str1.substring(0, str1.length() - 1);
                            ETpresrdg.setText(str1);
                            //IF CHARACTER LENGTH IS LESS THAN OR EQUAL TO 1, PRESENT READING WILL BE BLANK//
                        } else if (str1.length() <= 1) {
                            ETpresrdg.setText("");
                            break;
                        }
                        break;
                    //IF DELETE KEY IS PRESSED, TEXT WILL BE DELETED STARTING FROM THE END OF THE TEXT//
                    case KeyEvent.KEYCODE_DEL:
                        String str2 = ETpresrdg.getText().toString();
                        if (str2.length() >= 1) {
                            str2 = str2.substring(0, str2.length() - 1);
                            ETpresrdg.setText(str2);
                        } else if (str2.length() <= 1) {
                            ETpresrdg.setText("");
                            break;
                        }
                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                }

                return true;
            }
        });
        //IF THE USER CLICKS ON THE PRESENT READING TEXTBOX, ON SCREEN KEYBOARD HIDES//
        ETpresrdg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(ETpresrdg.getWindowToken(), 0);
            }

        });
        //IF THE SAVE GPS BUTTON IS CLICKED, PERFORM THE BELOW PROCEDURE//
        BTN_saveGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //IF THE GPS STAT TEXTBOX DISPLAYS EITHER OF THESE TWO TEXTS, ONLY A MESSAGEBOX ERROR WILL SHOW//
                if (TV_gpsstat.getText().equals("GPS LOADING..") || TV_gpsstat.getText().equals("GPS DISABLED")) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                    dlgAlert.setMessage("No GPS signal, or GPS is disabled.");
                    dlgAlert.setTitle("ERROR");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dlgAlert.create().show();
                    //IF GPS STAT TEXTBOX DOES NOT SHOW ANY OF THE TWO TEXTS ABOVE, CLICKING ON BTN_saveGPS WILL SAVE THE CURRENT GPS LOCATION//
                } else {
                    //IF THE GPS LOCATION IS EMPTY, CLICKING ON BTN_saveGPS WILL AUTOMATICALLY SAVE THE CURRENT GPS LOCATION//
                    if (TextUtils.isEmpty(GPSHLoc)) {
                        //UPDATE GPSHLOC FIELD WITH CURRENT GPS LOCATION//
                        databaseHelper.updateGPSloc(TV_gps.getText().toString(), f1_acctno);
                        //REFRESH CURSOR DATA AFTER UPDATING DATA CONTENT//
                        _CursorID = cursor.getPosition();
                        cursor = databaseHelper.fetchAll();
                        cursor.moveToPosition(_CursorID);
                        //END REFRESH CURSOR DATA AFTER UPDATING DATA CONTENT//
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("Current GPS location saved for " + f1_acctno + ".");
                        dlgAlert.setTitle("SUCCESS");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        dlgAlert.create().show();
                        //IF THE GPS LOCATION IS EMPTY, NULL, OR ZERO, CLICKING ON BTN_saveGPS WILL AUTOMATICALLY SAVE THE CURRENT GPS LOCATION//
                    } else if (GPSHLoc.equals("0") || GPSHLoc.equals("") || GPSHLoc.equals(null)) {
                        //UPDATE GPSHLOC FIELD WITH CURRENT GPS LOCATION//
                        databaseHelper.updateGPSloc(TV_gps.getText().toString(), f1_acctno);
                        //REFRESH CURSOR DATA TO PREVIOUS CURSOR LOCATION AFTER SAVING//
                        _CursorID = cursor.getPosition();
                        cursor = databaseHelper.fetchAll();
                        cursor.moveToPosition(_CursorID);
                        //END REFRESH CURSOR DATA TO PREVIOUS CURSOR LOCATION AFTER SAVING//
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("Current GPS location saved for " + f1_acctno + ".");
                        dlgAlert.setTitle("SUCCESS");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        dlgAlert.create().show();
                        //BUT IF THE GPS LOCATION IS NOT EMPTY, CLICKING ON BTN_saveGPS WILL SHOW A MESSAGE BOX ASKING THE USER FOR CONFIRMATION//
                        //IN SAVING THE CURRENT GPS LOCATION//
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setCancelable(false);
                        builder.setMessage("This accounts address is already tagged. Do you want to update the GPS tag of this account?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                //UPDATE GPSHLOC FIELD WITH CURRENT GPS LOCATION//
                                databaseHelper.updateGPSloc(TV_gps.getText().toString(), f1_acctno);
                                //REFRESH CURSOR DATA TO PREVIOUS CURSOR LOCATION AFTER SAVING//
                                _CursorID = cursor.getPosition();
                                cursor = databaseHelper.fetchAll();
                                cursor.moveToPosition(_CursorID);
                                //END REFRESH CURSOR DATA TO PREVIOUS CURSOR LOCATION AFTER SAVING//
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("New GPS location saved for " + f1_acctno + ".");
                                dlgAlert.setTitle("SUCCESS");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                dlgAlert.create().show();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ETpresrdg.requestFocus();
                                ETpresrdg.setSelection(ETpresrdg.getText().length());
                                dialog.dismiss();
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
                    }
                }
            }
        });
        //IF BTN_first KEY IS PRESSED, NAVIGATE TO PREVIOUS CONSUMER//
        BTN_first.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NavPrev();
            }
        });
        //IF BTN_prev KEY IS PRESSED, NAVIGATE TO PREVIOUS UNREAD CONSUMER//
        BTN_prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NavPrevUnread();
            }
        });
        //IF BTN_next KEY IS PRESSED, NAVIGATE TO NEXT UNREAD CONSUMER//
        BTN_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NavNextUnread();
            }
        });
        //IF BTN_last KEY IS PRESSED, NAVIGATE TO NEXT CONSUMER//
        BTN_last.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NavNext();
            }
        });

        //IF THE ON SCREEN ENTER KEY IS PRESSED, CHECK IF READING IS PRESENT AND PROCEED TO EnterRdg() PROCEDURE.
        //IF NOT, MESSAGE BOX WILL DISPLAY ASKING USER TO PLEASE INPUT READING//
        ETpresrdg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyAction, KeyEvent keyEvent) {
                if (keyAction == EditorInfo.IME_ACTION_DONE || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (TextUtils.isEmpty(ETpresrdg.getText())) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("Please input Reading");
                        dlgAlert.setTitle("Attention");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        dlgAlert.create().show();
                    } else {
                        EnterRdg();
                    }
                }
                return true;
            }
        });
    }

    //GET SERIAL # of DEVICE
    String SerialNo="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    SerialNo = Build.getSerial().toString();
                } else {
                    SerialNo = "";
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //PROCEDURE TO JUMP TO THE LAST RECORD//
    void NavLast() {
        cursor.moveToLast();
        GetSQLiteDatabaseRecords();
        cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
        GetSQLiteDatabaseWRates();
        ETpresrdg.requestFocus();
        ETpresrdg.setSelection(ETpresrdg.getText().length());
    }

    //PROCEDURE TO JUMP TO THE FIRST RECORD//
    void NavFirst() {
        cursor.moveToFirst();
        GetSQLiteDatabaseRecords();
        cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
        GetSQLiteDatabaseWRates();
        ETpresrdg.requestFocus();
        ETpresrdg.setSelection(ETpresrdg.getText().length());
    }

    //PROCEDURE TO GO TO THE NEXT RECORD//
    void NavNext() {
        if (!cursor.isLast()) {
            cursor.moveToNext();
            GetSQLiteDatabaseRecords();
            cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
            GetSQLiteDatabaseWRates();
        }
    }

    //PROCEDURE TO GO TO THE PREVIOUS RECORD//
    void NavPrev() {
        if (!cursor.isFirst()) {
            cursor.moveToPrevious();
            GetSQLiteDatabaseRecords();
            cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
            GetSQLiteDatabaseWRates();
        }
    }

    //PROCEDURE TO GO TO THE PREVIOUS UNREAD RECORD//
    void NavPrevUnread() {
        for (int x = cursor.getPosition(); ; x++) {
            if (x == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Beginning of record reached.");
                dlgAlert.setTitle("Attention!!!");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.create().show();
                ETpresrdg.requestFocus();
                ETpresrdg.setSelection(ETpresrdg.getText().length());
                return;
            }
            cursor.moveToPrevious();
            GetSQLiteDatabaseRecords();
            cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
            GetSQLiteDatabaseWRates();
            if (!c_activity.equals("R")) {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
                return;
            }
            if (cursor.isFirst() || cursor.getPosition() < 0) {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("Beginning of record reached.");
                dlgAlert.setTitle("Attention!!!");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.create().show();
                ETpresrdg.requestFocus();
                ETpresrdg.setSelection(ETpresrdg.getText().length());
                return;
            }
        }
    }

    //PROCEDURE TO GO TO THE NEXT UNREAD RECORD//
    void NavNextUnread() {
        for (int x = cursor.getPosition(); ; x++) {
            cursor.moveToNext();
            GetSQLiteDatabaseRecords();
            cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
            GetSQLiteDatabaseWRates();
            if (!c_activity.equals("R")) {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
                return;
            }
            if (cursor.isLast()) {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                dlgAlert.setMessage("End of record reached.");
                dlgAlert.setTitle("Attention!!!");
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.create().show();
                ETpresrdg.requestFocus();
                ETpresrdg.setSelection(ETpresrdg.getText().length());
                return;
            }
        }
    }

    //ADDITIONAL PROCEDURE USED TO REFRESH THE DATA//
    public void RefreshData() {
        cursor = databaseHelper.fetchAll();
        cursor.moveToPosition(_CursorID + 1);
    }

    //RESTART PROCEDURE THAT WILL TAKE EFFECT IF THE APPLICATION RESTARTS//
    @Override
    public void onRestart() {
        super.onRestart();
        super.onResume();
        ETpresrdg.requestFocus();
        ETpresrdg.setSelection(ETpresrdg.getText().length());
        getLocation();
    }

    //PROCEDURE THAT WILL TAKE EFFECT IF USER CLICKS THE ON SCREEN BACK BUTTON IN THE SIDE DRAWER MENU//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //PROCEDURE THAT ALLOWS THE APPLICATION TO RESUME DURING DEVICE WAKE UP//
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SerialNo = Build.getSerial().toString();
    }

    //PROCEDURE THAT TAKES EFFECT WHEN THE APPLICATION FIRST STARTS//
    @Override
    public void onStart() {
        super.onStart();
        //IF BLUETOOTH IS NOT ENABLED, APPLICATION WILL ASK THE USER TO ENABLE THE BLUETOOTH//
        if (!mBluetoothAdapter.isEnabled()) {
            // Request to enable bluetooth.
            // Bluetooth session will then be setup during onActivityResult
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            //IF BLUETOOTH IS ENABLED, APPLICATION WILL TRY TO ESTABLISH A CONNECTION WITH THE BLUETOOTH DEVICE//
        } else {
            if (mPrintService == null) {
                // Initialize the BluetoothPrintService to perform bluetooth connections
                mPrintService = new BluetoothPrintService(mHandler);
            }
        }
        //THIS GET LOCATION PROCEDURE WILL START AS WELL DURING APPLICATION START//
        getLocation();
    }

    //BLUETOOTH SERVICE PROCEDURES//
    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                String deviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + deviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getInt(TOAST), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up bluetooth connections
                    mPrintService = new BluetoothPrintService(mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Bluetooth was not enabled. Leaving Bluetooth print.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        String address = null;
        // Get the device MAC address
        if ((data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS) != null) || (data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS) != "WOOSIM"))
            address = data.getExtras().getString(DeviceList.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mPrintService.connect(device, secure);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //END BLUETOOTH SERVICE PROCEDURES//

    //BELOW PROCEDURE POPULATES THE OPTIONS MENU//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //FUNCTIONS OF EACH MENU ITEM IN THE OPTIONS MENU//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //IF THE PRINT SOA MENU ITEM IS PRESSED, SOA OF THE CONSUMER WILL BE PRINTED//
        /*if (id == R.id.printonly) {
            try {
                //IF THE PRESENT READING TEXTBOX IS EMPTY, MESSAGE BOX WILL DISPLAY INDICATING THAT SOA CANNOT BE PRINTED IF
                //READING IS NOT PRESENT//
                if (ETpresrdg.getText().toString().isEmpty()) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("This consumer still has NO READING! Please input reading before you can print.");
                    dlgAlert.setTitle("No reading!!!");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dlgAlert.create().show();
                    ETpresrdg.requestFocus();
                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                //IF PRESENT READING TEXTBOX IS NOT EMPTY, SOA WILL BE PRINTED//
                } else {
                    //RELOAD DATA OF THE CONSUMER//
                    GetSQLiteDatabaseRecords();
                    cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                    GetSQLiteDatabaseWRates();
                    _CursorID = cursor.getPosition();
                    //PRINT SOA PROCEDURE//
                    printSOA();
                    //RELOAD DATA AND CURSOR LOCATION OF THE CONSUMER//
                    cursor = databaseHelper.fetchAll();
                    cursor.moveToPosition(_CursorID);
                }
            } catch (IOException ex) {
                //Do something with the exception
            }
            return true;
        }*/
        //CLICKING ON THE PRINT ALL READ MENU ITEM WILL PRINT ALL READ CONSUMERS//
        if (id == R.id.suballread) {
            try {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
                _CursorID = cursor.getPosition();
                printRead();
                cursor = databaseHelper.fetchAll();
                cursor.moveToPosition(_CursorID);
            } catch (IOException ex) {
                //Do something with the exception
            }
            return true;
        }
        //CLICKING ON THE PRINT 1 TO 100 MENU ITEM WILL PRINT ALL READ CONSUMERS FROM 1 TO 100//
        if (id == R.id.sub1to100) {
            try {
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
                _CursorID = cursor.getPosition();
                print1to100();
                cursor = databaseHelper.fetchAll();
                cursor.moveToPosition(_CursorID);
            } catch (IOException ex) {
                //Do something with the exception
            }
            return true;
        }
        //CLICKING ON THE PRINT 101 to 200 MENU ITEM WILL PRINT ALL READ CONSUMERS FROM 101 TO 200//
        if (id == R.id.sub101to200) {
            try {
                if (cursor.getCount() >= 101) {
                    GetSQLiteDatabaseRecords();
                    cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                    GetSQLiteDatabaseWRates();
                    _CursorID = cursor.getPosition();
                    print101to200();
                    cursor = databaseHelper.fetchAll();
                    cursor.moveToPosition(_CursorID);
                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("Record count is not above 100. Cannot print.");
                    dlgAlert.setTitle("Limit Reached!");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dlgAlert.create().show();
                    ETpresrdg.requestFocus();
                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                }
            } catch (IOException ex) {
                //Do something with the exception
            }
            return true;
        }
        //CLICKING ON THE PRINT 200 AND ABOVE MENU ITEM WILL PRINT ALL READ CONSUMERS FROM 200 AND ABOVE//
        if (id == R.id.sub200above) {
            try {
                if (cursor.getCount() >= 201) {
                    GetSQLiteDatabaseRecords();
                    cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                    GetSQLiteDatabaseWRates();
                    _CursorID = cursor.getPosition();
                    print201up();
                    cursor = databaseHelper.fetchAll();
                    cursor.moveToPosition(_CursorID);
                } else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("Record count is not above 200. Cannot print.");
                    dlgAlert.setTitle("Limit Reached!");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    dlgAlert.create().show();
                    ETpresrdg.requestFocus();
                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                }
            } catch (IOException ex) {
                //Do something with the exception
            }
            return true;
        }
        //CLICKING ON THE NEXT UNREAD MENU ITEM WILL NAVIGATE TO THE NEXT UNREAD CONSUMER//
        if (id == R.id.nextunread) {
            NavLast();
            return true;
        }
        //CLICKING ON THE PREVIOUS UNREAD MENU ITEM WILL NAVIGATE TO THE PREVIOUS UNREAD CONSUMER//
        if (id == R.id.prevunread) {
            NavFirst();
            return true;
        }
        //CLICKING ON THE SEARCH ICON (MAGNIFYING GLASS) FROM THE MENU WILL BRING THE USER TO THE SEARCH FORM//
        if (id == R.id.search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            return true;
        }
        //CLICKING ON THE INPUT FINDINGS MENU ITEM WILL BRING THE USER TO THE INPUT FIELD FINDINGS FORM//
        if (id == R.id.inputfindings) {
            OpenRemarks();
            return true;
        }
        //CLICKING ON THE ADD NEW METER MENU ITEM WILL BRING THE USER TO THE ADD NEW METER FORM//
        if (id == R.id.addnewmtr) {
            Intent intent = new Intent(MainActivity.this, NewMtrFragment.class);
            intent.putExtra("nmPos", f1_acctno);
            startActivity(intent);
            ;
            return true;
        }
        //CLICKING ON THE STATISTICS MENU ITEM WILL DISPLAY THE STATISTICS OF THE CURRENTLY LOADED DATA//
        if (id == R.id.statistics) {
            cursorRead = databaseHelper.fetchRead();
            cursorUnread = databaseHelper.fetchUnread();
            cursorActive = databaseHelper.fetchActive();
            cursorInactive = databaseHelper.fetchInactive();
            cursorNewMtrs = databaseHelper.fetchNewMtrs();
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("BOOK NO.     : " + c_bookno +
                    "\nDUE DATE    : " + c_dueDate +
                    "\n\nTOTAL RECORDS    : " + cursor.getCount() +
                    "\nREAD RECORDS      : " + cursorRead.getCount() +
                    "\nUNREAD RECORDS : " + cursorUnread.getCount() +
                    "\n\nACTIVE RECORDS    : " + cursorActive.getCount() +
                    "\nINACTIVE RECORDS : " + cursorInactive.getCount() +
                    "\n\nNEW METERS  : " + cursorNewMtrs.getCount());
            dlgAlert.setTitle("RECORDS SUMMARY");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
            return true;
        }
        //CLICKING ON THE CAMERA MENU ITEM WILL LET THE USER USE THE CAMERA FUNCTION OF THE DEVICE//
        if (id == R.id.camera) {
            CameraFunc();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //PROCEDURES ON THE MENU ITEMS IN THE LEFT PANEL//
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //CLICKING ON THE CONNECT TO BLUETOOTH MENU ITEM WILL ALLOW THE USER TO OPEN THE BLUETOOTH CONNECTIVITY WINDOW//
        if (id == R.id.nav_connect) {
            Intent intent;

            // Launch the DeviceListActivity to see devices and do scan
            if (mPrintService != null) mPrintService.start();
            intent = new Intent(this, DeviceList.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        //CLICKING ON THE ABOUT WILL DISPLAY THE APPLICATION VERSION AND INFO//
        if (id == R.id.nav_about) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("PRIME WATER MRBS\n" +
                    "Version No. 1.0\n" +
                    "ePrime Business Solutions Inc ©");
            dlgAlert.setTitle("About..");
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

        }
        //CLICKING ON THE LOGOUT MENU ITEM WILL SEND THE USER BACK TO THE LOGIN FORM//
        if (id == R.id.nav_return) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return true;
    }

    //CAMERA LOADING PROCEDURE//
    public void CameraFunc() {
        //camera stuff
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("MMdd_HHmmSSS").format(new Date());

        //CREATE FOLDER
        File imagesFolder = new File(Environment.getExternalStorageDirectory() + File.separator + "MRBS_images" + File.separator);
        imagesFolder.mkdirs();
        imagesFolder.setExecutable(true);
        imagesFolder.setReadable(true);
        imagesFolder.setWritable(true);

        MediaScannerConnection.scanFile(this, new String[]{imagesFolder.toString()}, null, null);

        File image = new File(imagesFolder, f1_acctno + "_" + timeStamp + ".jpg");
        Uri uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        MediaScannerConnection.scanFile(this, new String[]{image.toString()}, null, null);
        startActivityForResult(imageIntent, 100);
    }

    //ENTER PRESENT READING PROCEDURE//
    public void EnterRdg() {
        //IF READING IS BLANK, APPLICATION WILL ASK USER TO INPUT READING//
        if (ETpresrdg.getText().equals("")) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("Please input Reading");
            dlgAlert.setTitle("Attention");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
            //IF READING IS PRESENT, BELOW PROCEDURE WILL TAKE EFFECT//
        } else {

            //Added Line for Last Consumption

            c_lastcons = databaseHelper.fetchLastConsumption(f1_acctno);

            //IMMEDIATELY COMPUTE FOR THE CUBIC METER CONSUMPTION//
            CUM = (Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString())) + Double.parseDouble(c_lastcons);
            //IF c_noofdays FIELD IS LESS THAN 30 AND CUBIC METER CONSUMPTION IS LESS THAN 10, THEN THE APPLICATION WILL DISPLAY A
            //MESSAGEBOX TELLING THE USER THAT THE CONSUMER IS NOT FOR BILLING YET AND NO READING WILL BE SAVED//
            if ((Double.parseDouble(c_noofdays) < 30) && Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString()) <= 10) {
                //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                    dlgAlert.setMessage("This account is not for billing yet. No reading will be saved.");
                    dlgAlert.setTitle("NOTICE!");
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            c_lh_rdg = "";
                            _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                            ETcum.setText("0");
                            ETpresrdg.setText("");
                            SaveReadings();
                            ETpresrdg.requestFocus();
                            ETpresrdg.setSelection(ETpresrdg.getText().length());
                        }
                    });
                    dlgAlert.create().show();
                    //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//

                //IF c_noofdays FIELD IS MORE THAN 30 AND CUBIC METER CONSUMPTION IS MORE THAN 10, THEN THE APPLICATION WILL
                //DO THE BELOW PROCEDURE//
            } else {
                //compute for High CUM and Low CUM for Reading Notification
                if (Double.parseDouble(ETpresrdg.getText().toString()) >= Double.parseDouble(ETprevrdg.getText().toString())) {
                    CUM = (Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString())) + Double.parseDouble(c_lastcons);

//                    if(c_cmcode.substring(0,1) == "R") {
                    high_comp = Double.parseDouble(c_aveCum) * 1.5;
                    high_comp = Double.parseDouble(ETprevrdg.getText().toString()) + high_comp;
                    low_comp = Double.parseDouble(c_aveCum) * 0.5;
                    low_comp = Double.parseDouble(ETprevrdg.getText().toString()) + low_comp;
                   /* }
                    else {
                        high_comp = Double.parseDouble(c_aveCum) * 1.75;
                        high_comp = Double.parseDouble(ETprevrdg.getText().toString()) + high_comp;
                        low_comp = Double.parseDouble(c_aveCum) * 0.25;
                        low_comp = Double.parseDouble(ETprevrdg.getText().toString()) + low_comp;}*/
                }

                //if More than 3 attemps
                if(Double.parseDouble(ReadCounter) >= 3)
                {
                    String original = "";
                    original = databaseHelper.fetchLastInput(f1_acctno);
                    ETpresrdg.setText(original);
                }

                //if For Reprinting
                if (databaseHelper.checkIfPrint(f1_acctno) == true && (Double.parseDouble(ETpresrdg.getText().toString()) >= Double.parseDouble(ETprevrdg.getText().toString()))) {
                    c_ffcode = "FOR PRINTING";
                    GetSQLiteDatabaseRecords();
                    ETcum = findViewById(R.id.ET_cum);
                    ETcum.setText(ETcum.getText().toString().replace(".00", ""));
                    ETcum.setText(ETcum.getText().toString().replace(".0", ""));
                    ComputeBill();
                    PrintAsk();
                }
                //IF PRESENT READING IS WITHIN THE NORMAL RANGE IS NOT CONSIDERED HIGH OR LOW READING, DO THIS PROCEDURE//
                else if
                (
                        (Double.parseDouble(c_noofdays) >= 30 && Double.parseDouble(ETpresrdg.getText().toString()) > Double.parseDouble(ETprevrdg.getText().toString()) && Double.parseDouble(ETpresrdg.getText().toString()) <= high_comp && Double.parseDouble(ETpresrdg.getText().toString()) >= low_comp)
                                ||
                                (Double.parseDouble(c_noofdays) >= 30 && Double.parseDouble(ETpresrdg.getText().toString()) >= Double.parseDouble(ETprevrdg.getText().toString()) && Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString()) <= 10 && Double.parseDouble(c_aveCum) <= 10)
                                ||
                                (Double.parseDouble(c_noofdays) < 30 && Double.parseDouble(ETpresrdg.getText().toString()) >= Double.parseDouble(ETprevrdg.getText().toString()) && Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString()) > 10)
                ) {
                    //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//
                    if (Double.parseDouble(ReadCounter) <= 2 || databaseHelper.checkIfDownloadrePrint() == true) {
                        //READING STATUS INDICATOR//
                        c_lh_rdg = "NORMAL";
                        //READING COUNTER INCREMENTS AND IS SAVED PER CONSUMER//
                        _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                        ETcum.setText(String.valueOf(CUM));
                        //BILL COMPUTATION PROCEDURE//
                        ComputeBill();
                        //DATABASE SAVING PROCEDURE//
                        SaveReadings();
                        //SOA PRINTING PROCEDURE//
                        PrintAsk();
                        //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//
                    } else {

                        //Update Jake
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Limit Reached");
                        builder.setCancelable(false);
                        builder.setMessage("Maximum input limit reached. You can only re-print the Statement of Account." + "\nPrint?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                c_lh_rdg = "NORMAL";
                                c_ffcode="";
                                String original = "";
                                original = databaseHelper.fetchLastInput(f1_acctno);
                                ETpresrdg.setText(original);
                                CUM = Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString());
                                ETcum.setText(String.valueOf(CUM));
                                //BILL COMPUTATION PROCEDURE//
                                ComputeBill();
                                try {
                                    printSOA();
                                } catch (IOException ex) {
                                }

                            }
                        });
                        //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String original = "";
                                original = databaseHelper.fetchLastInput(f1_acctno);
                                ETpresrdg.setText(original);
                                dialog.dismiss();
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

                        //End Update

                        /*
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("Maximum input limit reached. You can only re-print the Statement of Account. Click on Options, then select Print Statement.");
                        dlgAlert.setTitle("Limit Reached!");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                ETpresrdg.requestFocus();
                                ETpresrdg.setSelection(ETpresrdg.getText().length());
                            }
                        });
                        dlgAlert.create().show();Commented By Jake*/
                    }


                }
                //IF PRESENT READING IS HIGH READING, DO THIS PROCEDURE WITH CONFIRMATION MESSAGEBOX//
                else if ((Double.parseDouble(ETpresrdg.getText().toString()) > Double.parseDouble(ETprevrdg.getText().toString())) && (Double.parseDouble(ETpresrdg.getText().toString()) >= high_comp)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setCancelable(false);
                    builder.setMessage("Reading is HIGH! Accept?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//

                            if (databaseHelper.checkIfDownloadrePrint() == true && databaseHelper.checkIfPrint(f1_acctno) == false) {
                                c_lh_rdg = "HIGH";
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("Reading is High and not Approved. Please have the billing department recheck the reading.");
                                dlgAlert.setTitle("Reprinting no approval");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        ETpresrdg.requestFocus();
                                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                                    }
                                });
                                dlgAlert.create().show();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                            }
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//
                            else if (Double.parseDouble(ReadCounter) <= 2) {
                                //READING STATUS INDICATOR//
                                c_lh_rdg = "HIGH";
                                //READING COUNTER INCREMENTS AND IS SAVED PER CONSUMER//
                                _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                                ETcum.setText(String.valueOf(CUM));
                                //BILL COMPUTATION PROCEDURE//
                                ComputeBill();
                                //DATABASE SAVING PROCEDURE//
                                SaveReadings();
                                //CAMERA WILL BE ACTIVATED SINCE PICTURE IS REQUIRED FOR HIGH AND LOW READINGS//
                                CameraFunc();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                                //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//
                            } else {

                                    //Update Jake
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Limit Reached");
                                    builder.setCancelable(false);
                                    builder.setMessage("Maximum input limit reached. You can only re-print the Statement of Account." + "\nPrint?");
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            c_lh_rdg = "HIGH";
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            CUM = Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString());
                                            ETcum.setText(String.valueOf(CUM));
                                            //BILL COMPUTATION PROCEDURE//
                                            ComputeBill();
                                            try {
                                                printSOA();
                                            } catch (IOException ex) {
                                            }

                                        }
                                    });
                                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            dialog.dismiss();
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

                                    //End Update

                            /*
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("Maximum input limit reached. You can only re-print the Statement of Account. Click on Options, then select Print Statement.");
                            dlgAlert.setTitle("Limit Reached!");
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    ETpresrdg.requestFocus();
                                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                                }
                            });
                            dlgAlert.create().show();Commented By Jake*/
                            }
                        }
                    });
                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ETpresrdg.setText("");
                            dialog.dismiss();
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
                    //IF PRESENT READING IS LOW READING, DO THIS PROCEDURE WITH CONFIRMATION MESSAGEBOX//
                } else if ((Double.parseDouble(ETpresrdg.getText().toString()) > Double.parseDouble(ETprevrdg.getText().toString())) && (Double.parseDouble(ETpresrdg.getText().toString()) < low_comp)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setCancelable(false);
                    builder.setMessage("Reading is LOW! Accept?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//

                            if (databaseHelper.checkIfDownloadrePrint() == true && databaseHelper.checkIfPrint(f1_acctno) == false) {
                                c_lh_rdg = "LOW";
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("Reading is Low and not Approved. Please have the billing department recheck the reading.");
                                dlgAlert.setTitle("Reprinting no approval");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        ETpresrdg.requestFocus();
                                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                                    }
                                });
                                dlgAlert.create().show();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                            }
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//
                            else if (Double.parseDouble(ReadCounter) <= 2) {
                                //READING STATUS INDICATOR//
                                c_lh_rdg = "LOW";
                                //READING COUNTER INCREMENTS AND IS SAVED PER CONSUMER//
                                _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                                ETcum.setText(String.valueOf(CUM));
                                //BILL COMPUTATION PROCEDURE//
                                ComputeBill();
                                //DATABASE SAVING PROCEDURE//
                                SaveReadings();
                                //CAMERA WILL BE ACTIVATED SINCE PICTURE IS REQUIRED FOR HIGH AND LOW READINGS//
                                CameraFunc();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                                //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//
                            } else {

                                //Update Jake
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Limit Reached");
                                builder.setCancelable(false);
                                builder.setMessage("Maximum input limit reached. You can only re-print the Statement of Account." + "\nPrint?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        c_lh_rdg = "LOW";
                                        String original = "";
                                        original = databaseHelper.fetchLastInput(f1_acctno);
                                        ETpresrdg.setText(original);
                                        CUM = Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString());
                                        ETcum.setText(String.valueOf(CUM));
                                        //BILL COMPUTATION PROCEDURE//
                                        ComputeBill();
                                        try {
                                            printSOA();
                                        } catch (IOException ex) {
                                        }

                                    }
                                });
                                //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String original = "";
                                        original = databaseHelper.fetchLastInput(f1_acctno);
                                        ETpresrdg.setText(original);
                                        dialog.dismiss();
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

                                //End Update

                        /*
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                        dlgAlert.setMessage("Maximum input limit reached. You can only re-print the Statement of Account. Click on Options, then select Print Statement.");
                        dlgAlert.setTitle("Limit Reached!");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                ETpresrdg.requestFocus();
                                ETpresrdg.setSelection(ETpresrdg.getText().length());
                            }
                        });
                        dlgAlert.create().show();Commented By Jake*/
                            }
                        }
                    });
                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ETpresrdg.setText("");
                            dialog.dismiss();
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
                    //IF PRESENT READING IS LESS THAN THE PREVIOUS READING, DO THIS PROCEDURE WITH CONFIRMATION MESSAGEBOX//
                } else if (Double.parseDouble(ETpresrdg.getText().toString()) < Double.parseDouble(ETprevrdg.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setCancelable(false);
                    builder.setMessage("Present reading is less than previous. Pressing YES will average the CuM." + "\nAre you sure?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//

                            if (databaseHelper.checkIfDownloadrePrint() == true && databaseHelper.checkIfPrint(f1_acctno) == false) {
                                c_lh_rdg = "AVERAGE";
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("Reading is reversed and not Approved. Please have the billing department recheck the reading.");
                                dlgAlert.setTitle("Reprinting no approval");
                                dlgAlert.setCancelable(false);
                                dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        ETpresrdg.requestFocus();
                                        ETpresrdg.setSelection(ETpresrdg.getText().length());
                                    }
                                });
                                dlgAlert.create().show();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                            }
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//
                            else if (Double.parseDouble(ReadCounter) <= 2) {
                                //READING STATUS INDICATOR//
                                c_lh_rdg = "AVERAGE";
                                //READING COUNTER INCREMENTS AND IS SAVED PER CONSUMER//
                                _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                                ETcum.setText(String.valueOf(c_aveCum));
                                //BILL COMPUTATION PROCEDURE//
                                ComputeBill();
                                //DATABASE SAVING PROCEDURE//
                                SaveReadings();
                                //CAMERA WILL BE ACTIVATED SINCE PICTURE IS REQUIRED FOR HIGH AND LOW READINGS//
                                CameraFunc();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                                //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//
                            } else {

                                    //Update Jake
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Limit Reached");
                                    builder.setCancelable(false);
                                    builder.setMessage("Maximum input limit reached. You can only re-print the Statement of Account." + "\nPrint?");
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            c_lh_rdg = "AVERAGE";
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            CUM = Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString());
                                            ETcum.setText(String.valueOf(CUM));
                                            //BILL COMPUTATION PROCEDURE//
                                            ComputeBill();
                                            try {
                                                printSOA();
                                            } catch (IOException ex) {
                                            }

                                        }
                                    });
                                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            dialog.dismiss();
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

                                    //End Update

                            /*
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("Maximum input limit reached. You can only re-print the Statement of Account. Click on Options, then select Print Statement.");
                            dlgAlert.setTitle("Limit Reached!");
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    ETpresrdg.requestFocus();
                                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                                }
                            });
                            dlgAlert.create().show();Commented By Jake*/
                            }
                        }
                    });
                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ETpresrdg.setText("");
                            dialog.dismiss();
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
                    //IF PRESENT READING IS THE SAME WITH THE PREVIOUS READING, DO THIS PROCEDURE WITH CONFIRMATION MESSAGEBOX//
                } else if (Double.parseDouble(ETpresrdg.getText().toString()) == Double.parseDouble(ETprevrdg.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Confirmation");
                    builder.setCancelable(false);
                    builder.setMessage("Reading is same with previous." + "\nAccept Reading?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            //IF READING COUNTER IS LESS THAN OR EQUAL TO 2, READ THE PRESENT READING//
                            if (Double.parseDouble(ReadCounter) <= 2) {
                                if (Double.parseDouble(c_aveCum) == 0) {
                                    //READING STATUS INDICATOR//
                                    c_lh_rdg = "NORMAL";
                                    ETcum.setText("0");
                                } else {
                                    //READING STATUS INDICATOR//
                                    c_lh_rdg = "LOW";
                                    ETcum.setText("0");
                                }
                                //READING COUNTER INCREMENTS AND IS SAVED PER CONSUMER//
                                _ReadCounter = Double.parseDouble(ReadCounter) + 1;
                                //BILL COMPUTATION PROCEDURE//
                                ComputeBill();
                                //DATABASE SAVING PROCEDURE//
                                SaveReadings();
                                //CAMERA WILL BE ACTIVATED SINCE PICTURE IS REQUIRED FOR HIGH AND LOW READINGS//
                                CameraFunc();
                                //SOA PRINTING PROCEDURE//
                                PrintAsk();
                                //OTHERWISE, IF READING COUNTER IS MORE THAN OR IS EQUAL TO 3, INPUT OF PRESENT READING IS DISABLED//
                            } else {

                                    //Update Jake
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Limit Reached");
                                    builder.setCancelable(false);
                                    builder.setMessage("Maximum input limit reached. You can only re-print the Statement of Account." + "\nPrint?");
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Double.parseDouble(c_aveCum) == 0) {
                                                //READING STATUS INDICATOR//
                                                c_lh_rdg = "NORMAL";
                                                ETcum.setText("0");
                                            } else {
                                                //READING STATUS INDICATOR//
                                                c_lh_rdg = "LOW";
                                                ETcum.setText("0");
                                            }
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            CUM = Double.parseDouble(ETpresrdg.getText().toString()) - Double.parseDouble(ETprevrdg.getText().toString());
                                            ETcum.setText(String.valueOf(CUM));
                                            //BILL COMPUTATION PROCEDURE//
                                            ComputeBill();
                                            try {
                                                printSOA();
                                            } catch (IOException ex) {
                                            }

                                        }
                                    });
                                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String original = "";
                                            original = databaseHelper.fetchLastInput(f1_acctno);
                                            ETpresrdg.setText(original);
                                            dialog.dismiss();
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

                                    //End Update

                            /*
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("Maximum input limit reached. You can only re-print the Statement of Account. Click on Options, then select Print Statement.");
                            dlgAlert.setTitle("Limit Reached!");
                            dlgAlert.setCancelable(false);
                            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    ETpresrdg.requestFocus();
                                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                                }
                            });
                            dlgAlert.create().show();Commented By Jake*/
                            }
                        }
                    });
                    //NO BUTTON THAT THE USER CAN CLICK IF USER WILL RE-INPUT THE READING//
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ETpresrdg.setText("");
                            dialog.dismiss();
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
                }
            }
        }
    }

    //INDICATION FOR SENIOR CITIZEN CHARGE VALUE FOR SENIOR CITIZEN CONSUMERS WITH LESS THAN 30 CUM//
    public void ComputeSCdisc() {
        //CHECK IF CUSTOMER IS A SENIOR CITIZEN//
        if (c_CSStat.contains("1")) {
            SenDisc = temp_amount * .05;
        } else {
            SenDisc = 0.00;
        }
    }

    //INDICATION FOR SENIOR CITIZEN CHARGE VALUE FOR SENIOR CITIZEN CONSUMERS WITH MORE THAN 30 CUM//
    public void ComputeSCdiscAfterMax() {
        //CHECK IF CUSTOMER IS A SENIOR CITIZEN//
        if (c_CSStat.contains("1")) {
            //Double _maxamt = WRmin + (10 * WR1) + (10 * WR2);
            //SenDisc = _maxamt * .05;
            SenDisc = 0.00;
        } else {
            SenDisc = 0.00;
        }
    }

    //PROCEDURE FOR CHARGES COMPUTATION//
    public void ComputeBill() {
        //SELECT WATER RATES BASED ON CUSTOMER RATE CODE//
        cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
        //LOAD WATER RATES BASED ON CUSTOMER RATE CODE//
        GetSQLiteDatabaseWRates();
        //IF CUM TEXTBOX IS BLANK, INDICATE ZERO VALUE//
        if (ETcum.getText().toString().equals("")) {
            ETcum.setText("0");
        }
        //IF CUM IS GREATER THAN OR EQUAL TO ZERO, COMPUTE FOR WATER BILL AMOUNT//
        if (Double.parseDouble(ETcum.getText().toString()) >= 0)
        {

             if (Double.parseDouble(ETcum.getText().toString()) <= 10)
             {
                  temp_amount = WRmin;
                  ComputeSCdisc();
             }
             else if (Double.parseDouble(ETcum.getText().toString()) > 10 && Double.parseDouble(ETcum.getText().toString()) <= 20)
             {
                  temp_amount = WRmin + ((Double.parseDouble(ETcum.getText().toString()) - 10) * WR1);
                  ComputeSCdisc();
             }
             else if (Double.parseDouble(ETcum.getText().toString()) > 20 && Double.parseDouble(ETcum.getText().toString()) <= 30)
             {
                 temp_amount = WRmin + (10 * WR1) + ((Double.parseDouble(ETcum.getText().toString()) - 20) * WR2);
                 ComputeSCdisc();
             }
             else if (Double.parseDouble(ETcum.getText().toString()) > 30 && Double.parseDouble(ETcum.getText().toString()) <= 40)
             {
                 temp_amount = WRmin + (10 * WR1) + (10 * WR2) + ((Double.parseDouble(ETcum.getText().toString()) - 30) * WR3);
                 ComputeSCdiscAfterMax();
             }
             else if (Double.parseDouble(ETcum.getText().toString()) > 40 && Double.parseDouble(ETcum.getText().toString()) <= 50)
             {
                  temp_amount = WRmin + (10 * WR1) + (10 * WR2) + (10 * WR3) + ((Double.parseDouble(ETcum.getText().toString()) - 40) * WR4);
                  ComputeSCdiscAfterMax();
             }
             else if (Double.parseDouble(ETcum.getText().toString()) > 50)
             {
                 temp_amount = WRmin + (10 * WR1) + (10 * WR2) + (10 * WR3) + (10 * WR4) + ((Double.parseDouble(ETcum.getText().toString()) - 50) * WR5);
                 ComputeSCdiscAfterMax();
             }
            //INFO LOG USED FOR DEBUG TESTING PURPOSES//
            Log.d("CUM", "CuM IS " + ETcum.getText().toString());
            Log.d("AMOUNT", "AMT IS " + temp_amount.toString());
            //SET THE WATER BILL AMOUNT IN THE ETbillamt TEXTBOX//
            ETbillamt.setText(String.valueOf(Double.parseDouble(Initformatter.format(temp_amount))));
        }
    }

    //PROCEDURE FOR LOADING THE FIELD FINDINGS FORM//
    public void OpenRemarks()
    {
        Intent intent = new Intent(MainActivity.this, FindingsFragment.class);
        //EXTRAS ARE LOADED TO BE DISPLAYED IN THE FIELD FINDINGS FORM//
        intent.putExtra("ffacctno", f1_acctno);
        intent.putExtra("ffname", fl_name.getText().toString());
        intent.putExtra("ffmtrno", fl_mtrno.getText().toString());
        intent.putExtra("ffPos", f1_acctno);
        intent.putExtra("selff", f1_findings.getText());
        startActivity(intent);
    }

    //PROCEDURE TO SAVE READINGS IN THE DATABASE//
    public void SaveReadings()
    {
        //SET THE CURRET DATE AND TIME READ//
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat time = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a");
        DateFormat time1 = new SimpleDateFormat("KK:mm:ss a");
        time.setTimeZone(TimeZone.getDefault());
        localtime = time.format(currentLocalTime);
        String localtime1 = time1.format(currentLocalTime);
        //UPDATE CUSTOMER TABLE//
        if (ETbillamt.getText().toString().equals("0") || ETbillamt.getText().toString().equals(""))
        {
            ETbillamt.setText("0.00");
        }
        //UPDATE CUSTOMER TABLE//
        databaseHelper.updateRec(String.valueOf(_ReadCounter), "R", ETpresrdg.getText().toString(), ETcum.getText().toString(),
                ETbillamt.getText().toString(), localtime, c_lh_rdg, TV_gps.getText().toString(), f1_acctno, localtime1);
        //RELOAD READ AND UNREAD STATUS FOR STATISTICS//
        CheckStatus();
    }

    //PROCEDURE TO RELOAD READ AND UNREAD STATUS FOR STATISTICS//
    public void CheckStatus()
    {
        databaseHelper.CheckReadStat(c_bookno);
    }

    //PROCEDURE TO LOAD ALL CONSUMER DATA FROM THE DATABASE TO THE VARIABLES OF THIS APPLICATION//
    public void GetSQLiteDatabaseRecords()
    {
        if ((cursor.getPosition() + 1) > cursor.getCount())
        {
            //DO NOTHING..
        }
        else
        {
            f1_recno.setText("Record number " + (cursor.getPosition() + 1) + " of " + cursor.getCount());
            GPSHLoc = cursor.getString(119);
            TV_gpstagdetails.setText(cursor.getString(119));
            ETpresrdg.setText(cursor.getString(19));
            _id = cursor.getString(0);
            c_cmcode = cursor.getString(4);
            c_billtype = cursor.getString(87);
            c_cmdesc = cursor.getString(5);
            c_mtrsize = cursor.getString(7);
            c_billDate = cursor.getString(13);
            c_aveCum = cursor.getString(16);
            OthersDesc = cursor.getString(32);
            c_otherChrgs = cursor.getDouble(34);
            c_prevRdgDate = cursor.getString(11);
            c_dueDate = cursor.getString(76);
            c_discodate = cursor.getString(80);
            c_CSStat = cursor.getString(79);
            c_mosarr = cursor.getString(83);
            c_activity = cursor.getString(18);
            c_ffcode = cursor.getString(22);
            c_seq = cursor.getString(14);
            dueDate_save = cursor.getString(76);
            ReadCounter = cursor.getString(28);
            PrintCounter = cursor.getString(117);
            c_bookno = cursor.getString(72);
            c_atmref = cursor.getString(74);
            c_oldAcctNo = cursor.getString(75);
            c_billno = cursor.getString(77);
            c_evat = cursor.getString(31);
            c_penStat = cursor.getString(17);
            c_lh_rdg = cursor.getString(115);
            c_status = cursor.getString(15);
            c_noofdays = cursor.getString(81);

            fl_name.setText(cursor.getString(2));
            f1_acctno = cursor.getString(1);
            f1_oldacctno.setText(cursor.getString(75));
            f1_view_acctno.setText(cursor.getString(1));
            fl_mtrno.setText(cursor.getString(8));
            ETprevrdg.setText(cursor.getString(9));
            TVaddress.setText(cursor.getString(3));
            f1_findings.setText(cursor.getString(22));
            ETbillamt.setText(cursor.getString(21));
            ETcum.setText(cursor.getString(20));
            c_arrears = cursor.getString(10);
            AfterDue = cursor.getString(118);

            BP1 = cursor.getString(111);
            BP2 = cursor.getString(110);
            BP3 = cursor.getString(109);
            BP4 = cursor.getString(108);
            BP5 = cursor.getString(107);
            BP6 = cursor.getString(106);

            //COMMAND TO ENSURE THAT ETcum IS BLANK INSTEAD OF .0//
            ETcum.setText(ETcum.getText().toString().replace(".00", ""));
            ETcum.setText(ETcum.getText().toString().replace(".0", ""));
        }

        if (c_status.equals("A") && (Double.parseDouble(c_noofdays) >= 30))
        {
            TV_status.setText("ACTIVE");
            TV_status.setTextColor(Color.BLACK);
            fl_mtrno.setTextColor(Color.BLACK);
            f1_view_acctno.setTextColor(Color.BLACK);
            f1_oldacctno.setTextColor(Color.BLACK);
            fl_name.setTextColor(Color.BLACK);
            TVaddress.setTextColor(Color.BLACK);
        }
        else if (c_status.equals("A") && (Double.parseDouble(c_noofdays) < 30))
        {
            TV_status.setText("NEW CONNECTION");
            TV_status.setTextColor(this.getResources().getColor(R.color.colorOrange));
            fl_mtrno.setTextColor(this.getResources().getColor(R.color.colorOrange));
            f1_view_acctno.setTextColor(this.getResources().getColor(R.color.colorOrange));
            f1_oldacctno.setTextColor(this.getResources().getColor(R.color.colorOrange));
            fl_name.setTextColor(this.getResources().getColor(R.color.colorOrange));
            TVaddress.setTextColor(this.getResources().getColor(R.color.colorOrange));
        }
        else if (c_status.equals("I"))
        {
            TV_status.setText("INACTIVE");
            TV_status.setTextColor(Color.RED);
            TV_status.setTextColor(Color.RED);
            fl_mtrno.setTextColor(Color.RED);
            f1_view_acctno.setTextColor(Color.RED);
            f1_oldacctno.setTextColor(Color.RED);
            fl_name.setTextColor(Color.RED);
            TVaddress.setTextColor(Color.RED);
        }

        if (c_activity.equals("R"))
        {
            TVrecstat.setText("READ CONSUMER");
            TVrecstat.setTextColor(Color.parseColor("#1E90FF"));
        }
        else if (c_activity.equals("U"))
        {
            TVrecstat.setText("UNREAD CONSUMER");
            TVrecstat.setTextColor(Color.RED);
        }

        if (TextUtils.isEmpty(TV_gpstagdetails.getText()))
        {
            TV_gpstag.setText("No GPS location saved");
            TV_gpstag.setTextColor(Color.GRAY);
        }
        else if (TV_gpstagdetails.getText().equals("0") || TV_gpstagdetails.getText().equals("") || TV_gpstagdetails.getText().equals(null))
        {
            TV_gpstag.setText("No GPS location saved");
            TV_gpstag.setTextColor(Color.GRAY);
        }
        else
        {
            TV_gpstag.setText("GPS location tagged");
            TV_gpstag.setTextColor(Color.GREEN);
        }
        getOthers();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (GPSTAG.equals("TRUE"))
            {
                buildAlertMessageNoGps();
            }
        }
        else
        {
            if (GPSTAG.equals("FALSE"))
            {
                buildAlertMessageHaveGps();
            }
        }
    }

    //GPS CONDITIONS//
    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS capture enabled by Administrator. It is HIGHLY recommended that you TURN ON your GPS.")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id)
                    {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getLocation();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageHaveGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS capture is disabled by Administrator. Please TURN OFF your GPS.")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id)
                    {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getLocation();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void getLocation()
    {
        try
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        TV_gps.setText(location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude());

        if (TV_gps.getText().equals("") || TV_gps.getText().equals(null))
        {
            TV_gpsstat.setText("GPS INACTIVE");
            TV_gpsstat.setTextColor(Color.GRAY);
        }
        else
        {
            TV_gpsstat.setText("GPS RECORDING");
            TV_gpsstat.setTextColor(Color.GREEN);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }
    //END GPS CONDITIONS//

    //PROCEDURE TO LOAD OTHERS DATA FROM THE DATABASE TO THE VARIABLES OF THIS APPLICATION//
    public void getOthers()
    {
        if ((cursor.getPosition() + 1) > cursor.getCount())
        {
            NavLast();
        }
        else
        {
            Disc1 = cursor.getString(32);
            Oth1 = cursor.getString(33);
            Disc2 = cursor.getString(34);
            Oth2 = cursor.getString(35);
            Disc3 = cursor.getString(36);
            Oth3 = cursor.getString(37);
            Disc4 = cursor.getString(38);
            Oth4 = cursor.getString(39);
            Disc5 = cursor.getString(40);
            Oth5 = cursor.getString(41);
            Disc6 = cursor.getString(42);
            Oth6 = cursor.getString(43);
            Disc7 = cursor.getString(44);
            Oth7 = cursor.getString(45);
            Disc8 = cursor.getString(46);
            Oth8 = cursor.getString(47);
            Disc9 = cursor.getString(48);
            Oth9 = cursor.getString(49);
            Disc10 = cursor.getString(50);
            Oth10 = cursor.getString(51);

            if (Oth1.equals(""))
            {
                Oth1 = "0";
            }
            if (Oth2.equals(""))
            {
                Oth2 = "0";
            }
            if (Oth3.equals(""))
            {
                Oth3 = "0";
            }
            if (Oth4.equals(""))
            {
                Oth4 = "0";
            }
            if (Oth5.equals(""))
            {
                Oth5 = "0";
            }
            if (Oth6.equals(""))
            {
                Oth6 = "0";
            }
            if (Oth7.equals(""))
            {
                Oth7 = "0";
            }
            if (Oth8.equals(""))
            {
                Oth8 = "0";
            }
            if (Oth9.equals(""))
            {
                Oth9 = "0";
            }
            if (Oth10.equals(""))
            {
                Oth10 = "0";
            }
        }
    }

    //PROCEDURE TO LOAD WATER RATES DATA FROM THE DATABASE TO THE VARIABLES OF THIS APPLICATION//
    public void GetSQLiteDatabaseWRates()
    {
        WRmin = cursorWRate.getDouble(1);
        WR1 = cursorWRate.getDouble(2);
        WR2 = cursorWRate.getDouble(3);
        WR3 = cursorWRate.getDouble(4);
        WR4 = cursorWRate.getDouble(5);
        WR5 = cursorWRate.getDouble(6);
        BillType = cursorWRate.getDouble(10);
    }

    //PROCEDURE FOR PRINTING, WITH MESSAGEBOX FOR CONFIRMATION//
    public void PrintAsk()
    {
        SetupBillingCons();
        if (autoprint.isChecked())
        {
            try
            {
                printSOA();
                _CursorID = cursor.getPosition();
                RefreshData();
                GetSQLiteDatabaseRecords();
                cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                GetSQLiteDatabaseWRates();
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    // handle the exception...
                    // For example consider calling Thread.currentThread().interrupt(); here.
                }
            }
            catch (IOException ex)
            {
                //Do something with the exception
            }
        }
        else if (!autoprint.isChecked())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirmation");
            builder.setCancelable(false);
            builder.setMessage("Print Bill for this Consumer?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int which)
                {
                    try
                    {
                        printSOA();
                    }
                    catch (IOException e)
                    {
                    }
                    _CursorID = cursor.getPosition();
                    RefreshData();
                    GetSQLiteDatabaseRecords();
                    cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                    GetSQLiteDatabaseWRates();
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        // handle the exception...
                        // For example consider calling Thread.currentThread().interrupt(); here.
                    }
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    _CursorID = cursor.getPosition();
                    RefreshData();
                    GetSQLiteDatabaseRecords();
                    cursorWRate = databaseHelper.fetchWRATES(c_cmcode.trim(), c_billtype.trim());
                    GetSQLiteDatabaseWRates();
                    ETpresrdg.requestFocus();
                    ETpresrdg.setSelection(ETpresrdg.getText().length());
                    dialog.dismiss();
                }
            });
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener()
            {

                @Override
                public void onShow(DialogInterface dialog)
                {

                    Button positive = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setFocusable(true);
                    positive.setFocusableInTouchMode(true);
                    positive.requestFocus();
                }
            });
            alert.show();
        }
        ETpresrdg.requestFocus();
        ETpresrdg.setSelection(ETpresrdg.getText().length());
    }

    //PROCEDURE FOR SETTING UP BARCODE DATA//
    public void SetupBarcodes()
    {
        String _mm = c_dueDate.substring(5, 7);
        String _dd = c_dueDate.substring(8, 10);
        Log.i("MM", "MM: " + _mm);
        Log.i("DD", "DD: " + _dd);
        if (c_billno.length() == 4)
        {
            _SpacedBillNo = c_billno + "      ";
        }
        if (c_billno.length() == 5)
        {
            _SpacedBillNo = c_billno + "     ";
        }
        if (c_billno.length() == 6)
        {
            _SpacedBillNo = c_billno + "    ";
        }
        if (c_billno.length() == 7)
        {
            _SpacedBillNo = c_billno + "   ";
        }
        if (c_billno.length() == 8)
        {
            _SpacedBillNo = c_billno + "  ";
        }
        if (c_billno.length() == 9)
        {
            _SpacedBillNo = c_billno + " ";
        }
        if (c_billno.length() == 10)
        {
            _SpacedBillNo = c_billno;
        }
        PrimeBarCode = _SpacedBillNo + c_atmref;
        if (c_atmref.length() > 10)
        {
            _qrpayable = c_atmref.substring(0, c_atmref.length() - 4);
        }
        else
        {
            _qrpayable = c_atmref;
        }
        PrimePayable = "https://conso.pavi.com.ph/PrimewaterApplication/PrimewaterNDA.aspx?atmref=" + _qrpayable;
        Log.i("BarCode", PrimeBarCode);
        Log.i("BarCode", PrimePayable);
    }

    //SETUP 6 MONTHS BILLING CONSUMPTION//
    public void SetupBillingCons()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat sdfm = new SimpleDateFormat("MMM yyyy");

        try
        {
            PresBP = sdf.parse(c_billDate);

            DBP1.setTime(PresBP);
            DBP1.add(Calendar.MONTH, -1);
            S_DBP1 = sdfm.format(DBP1.getTime());
            if (BP1.equals("0") || BP1.equals("0.00") || BP1.equals(""))
            {
                BP1 = "NO BILLING";
            }

            DBP2.setTime(PresBP);
            DBP2.add(Calendar.MONTH, -2);
            S_DBP2 = sdfm.format(DBP2.getTime());
            if (BP2.equals("0") || BP2.equals("0.00") || BP2.equals(""))
            {
                BP2 = "NO BILLING";
            }

            DBP3.setTime(PresBP);
            DBP3.add(Calendar.MONTH, -3);
            S_DBP3 = sdfm.format(DBP3.getTime());
            if (BP3.equals("0") || BP3.equals("0.00") || BP3.equals(""))
            {
                BP3 = "NO BILLING";
            }

            DBP4.setTime(PresBP);
            DBP4.add(Calendar.MONTH, -4);
            S_DBP4 = sdfm.format(DBP4.getTime());
            if (BP4.equals("0") || BP4.equals("0.00") || BP4.equals(""))
            {
                BP4 = "NO BILLING";
            }

            DBP5.setTime(PresBP);
            DBP5.add(Calendar.MONTH, -5);
            S_DBP5 = sdfm.format(DBP5.getTime());
            if (BP5.equals("0") || BP5.equals("0.00") || BP5.equals(""))
            {
                BP5 = "NO BILLING";
            }

            DBP6.setTime(PresBP);
            DBP6.add(Calendar.MONTH, -6);
            S_DBP6 = sdfm.format(DBP6.getTime());
            if (BP6.equals("0") || BP6.equals("0.00") || BP6.equals(""))
            {
                BP6 = "NO BILLING";
            }

        }
        catch (ParseException ex)
        {
            Log.v("Exception", ex.getLocalizedMessage());
        }
    }

    //PROCEDURE FOR SOA PRINTING//
    public void printSOA() throws IOException
    {

        if (TextUtils.isEmpty(PrintCounter))
        {
            PrintCounter = "0";
        }


//        if ((c_ffcode == "FOR PRINTING" || c_ffcode == ""))
//        {
        //HEADER//
        MainActivity.mPrintService.write(WoosimCmd.initPrinter());
        sendImg(0, 0, R.drawable.logo);
        MainActivity.mPrintService.write(WoosimCmd.printData());
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1600);
        byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
        String str0 = "  ==================================================================\n";
        byteStream.write(str0.getBytes());
        //END HEADER//

        if ((c_lh_rdg == "LOW" || c_lh_rdg == "HIGH" || c_lh_rdg == "AVERAGE" || ETpresrdg.getText().toString() == "") && (c_ffcode != "FOR PRINTING"))
        {

            //CUSTOMER INFO LINE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 2, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            String str2 = " Accnt No. : " + f1_acctno + "\n";
            String str3 = " Name      : " + fl_name.getText() + "\n";
            byteStream.write(str2.getBytes());
            byteStream.write(str3.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            String stroldacctno = "  Old Acct Number     : " + c_oldAcctNo + "\n";
            String str4 = "  ATM Reference Number: " + c_atmref + "\n";
            String str5 = "  Bill Number         : " + c_billno + "\n";
            String str6 = "  Address             : " + TVaddress.getText() + "\n";
            String str7 = "  Meter Number        : " + fl_mtrno.getText() + "\n";
            String strcmdesc = "  Classification      : " + c_cmdesc + "\n\n";
            byteStream.write(stroldacctno.getBytes());
            byteStream.write(str4.getBytes());
            byteStream.write(str5.getBytes());
            byteStream.write(str6.getBytes());
            byteStream.write(str7.getBytes());
            byteStream.write(strcmdesc.getBytes());
            String str59 = "  Date: " + presDate + "\n\n";
            String str60 = "  Dear Concessionaire,\n\n";
            String str61 = "  We have already read your meter. However, to ensure the\n";
            String str62 = "  correctness of your bill, it is subject for verification.\n";
            String str63 = "  You will receive your billing notice once it is verified.\n\n";
            String str64 = "  Thank you for understanding.\n\n";
            String str65 = "  For inquiries, call/text:\n";
            String str66 = "  " + telno1 + "\n";
            String str67 = "  " + telno2 + "\n\n";
            String str68 = "  -The Management\n\n\n\n";
            byteStream.write(str59.getBytes());
            byteStream.write(str60.getBytes());
            byteStream.write(str61.getBytes());
            byteStream.write(str62.getBytes());
            byteStream.write(str63.getBytes());
            byteStream.write(str64.getBytes());
            byteStream.write(str65.getBytes());
            byteStream.write(str66.getBytes());
            byteStream.write(str67.getBytes());
            byteStream.write(str68.getBytes());
        }
        else
        {

            //NOTICE LINE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 2, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
            String str1 = "STATEMENT OF ACCOUNT\n\n";
            byteStream.write(str1.getBytes());
            //CUSTOMER INFO LINE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 2, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            String str2 = " Accnt No. : " + f1_acctno + "\n";
            String str3 = " Name      : " + fl_name.getText() + "\n";
            byteStream.write(str2.getBytes());
            byteStream.write(str3.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            String stroldacctno = "  Old Acct Number     : " + c_oldAcctNo + "\n";
            String str4 = "  ATM Reference Number: " + c_atmref + "\n";
            String str5 = "  Bill Number         : " + c_billno + "\n";
            String str6 = "  Address             : " + TVaddress.getText() + "\n";
            String str7 = "  Meter Number        : " + fl_mtrno.getText() + "\n";
            String strcmdesc = "  Classification      : " + c_cmdesc + "\n\n";
            if (!c_oldAcctNo.isEmpty() || !c_oldAcctNo.equals(""))
            {
                byteStream.write(stroldacctno.getBytes());
            }
            byteStream.write(str4.getBytes());
            byteStream.write(str5.getBytes());
            byteStream.write(str6.getBytes());
            byteStream.write(str7.getBytes());
            byteStream.write(strcmdesc.getBytes());


            //CUSTOMER READING LINE//
            String str8 = "  Billing Month : " + c_billDate + "\n";
            String str9 = "  Billing Period: " + c_prevRdgDate + " to " + presDate + "\n";
            String str10 = "  Due Date      : " + c_dueDate + "\n";
            String str11 = "  Present Rdg   : " + ETpresrdg.getText().toString() + "\n";
            String str12 = "  Previous Rdg  : " + ETprevrdg.getText().toString() + "\n";
            //Added Line for Last Consumption

            c_lastcons = databaseHelper.fetchLastConsumption(f1_acctno);

            String str25="";
            if(Double.parseDouble(c_lastcons) > 0)
            {
                str25 = "  Last M. Cons  : " + c_lastcons.replace(".0", "") + "\n";
            }
            String str13 = "  Cons. (CUM)   : " + CUM.toString().replace(".0", "") + "\n";
            byteStream.write(str8.getBytes());
            byteStream.write(str9.getBytes());
            byteStream.write(str10.getBytes());
            byteStream.write(str11.getBytes());
            byteStream.write(str12.getBytes());
            byteStream.write(str25.getBytes());
            byteStream.write(str13.getBytes());
            String str14 = "  ==================================================================\n";
            byteStream.write(str14.getBytes());


            //TOTAL BILL LINE//
            double Total_Bill=0;
            if(SenDisc >=0)
            {
                Total_Bill = Double.parseDouble(ETbillamt.getText().toString()) - SenDisc;
            }
            else
            {
                Total_Bill = Double.parseDouble(ETbillamt.getText().toString());
            }
            //CHECK IF CUSTOMER IS AN EMPLOYEE THEN SET Discount//

            double _surc = databaseHelper.fetchSurchargeBases(f1_acctno);





            //EVAT LINE//
            if (c_evat.equals("1"))
            {
                String str15 = "  Basic Charge            " + padLeft(formatter.format(Double.parseDouble(ETbillamt.getText().toString()) / 1.12), 38) + "\n";
                byteStream.write(str15.getBytes());
                EVATCharge = Double.parseDouble(ETbillamt.getText().toString()) - (Double.parseDouble(ETbillamt.getText().toString()) / 1.12);
                String _EVATCharge = formatter.format(EVATCharge);
                String str19 = "  12% VAT                 " + padLeft(_EVATCharge, 38) + "\n";
                byteStream.write(str19.getBytes());
            }
            else
            {
                String str15 = "  Basic Charge            " + padLeft(formatter.format(Double.parseDouble(ETbillamt.getText().toString())), 38) + "\n";
                byteStream.write(str15.getBytes());
            }
            //CHECK IF CUSTOMER IS A SENIOR CITIZEN//
            if (c_CSStat.contains("1") && SenDisc > 0)
            {
                String strSen = "  Senior Citizen Discount " + padLeft("-" + formatter.format(SenDisc), 38) + "\n";
                byteStream.write(strSen.getBytes());
            }

            if(_surc > 0)
            {
                /*String strEmployee = "  Employee Discount       " + padLeft(formatter.format((_surc * -1)), 38) + "\n";
                byteStream.write(strEmployee.getBytes());*/
            }


            String str17 = "                                              ----------------------" + "\n";
            String str18 = "  TOTAL CURRENT BILL      " + padLeft(formatter.format(Total_Bill), 38) + "\n";
            byteStream.write(str17.getBytes());
            byteStream.write(str18.getBytes());

            String str20 = "  ==================================================================\n";
            byteStream.write(str20.getBytes());
            //OTHER CHARGES HEADER//
            if ((Double.parseDouble(c_arrears) != 0) || !Disc1.equals("") || !Disc2.equals("") || !Disc3.equals("") || !Disc4.equals("") || !Disc5.equals("") || !Disc6.equals("") || !Disc7.equals("") || !Disc8.equals("") || !Disc9.equals("") || !Disc10.equals(""))
            {
                String str21 = "  OTHER CHARGES\n";
                byteStream.write(str21.getBytes());
            }
            String _arrears = formatter.format(Double.parseDouble(c_arrears));
            if (_arrears.equals(".00"))
            {
                _arrears = "0.00";
            }

            String str16 = "  Balance from last bill  " + padLeft(_arrears, 38) + "\n";
            byteStream.write(str16.getBytes());



            //OTHER DESCRIPTIONS 1 TO 20//
            if (!Disc1.equals(""))
            {
                if ((Double.parseDouble(Oth1) == 0) || Oth1.isEmpty())
                {
                    str22 = "  " + padRight(Disc1, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str22 = "  " + padRight(Disc1, 30) + padLeft(formatter.format(Double.parseDouble(Oth1)), 32) + "\n";
                }
                byteStream.write(str22.getBytes());
            }
            if (!Disc2.equals(""))
            {
                if ((Double.parseDouble(Oth2) == 0) || Oth2.isEmpty())
                {
                    str23 = "  " + padRight(Disc2, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str23 = "  " + padRight(Disc2, 30) + padLeft(formatter.format(Double.parseDouble(Oth2)), 32) + "\n";
                }
                byteStream.write(str23.getBytes());
            }
            if (!Disc3.equals(""))
            {
                if ((Double.parseDouble(Oth3) == 0) || Oth3.isEmpty())
                {
                    str24 = "  " + padRight(Disc3, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str24 = "  " + padRight(Disc3, 30) + padLeft(formatter.format(Double.parseDouble(Oth3)), 32) + "\n";
                }
                byteStream.write(str24.getBytes());
            }
            if (!Disc4.equals(""))
            {
                if ((Double.parseDouble(Oth4) == 0) || Oth4.isEmpty())
                {
                    str25 = "  " + padRight(Disc4, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str25 = "  " + padRight(Disc4, 30) + padLeft(formatter.format(Double.parseDouble(Oth4)), 32) + "\n";
                }
                byteStream.write(str25.getBytes());
            }
            if (!Disc5.equals(""))
            {
                if ((Double.parseDouble(Oth5) == 0) || Oth5.isEmpty())
                {
                    str26 = "  " + padRight(Disc5, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str26 = "  " + padRight(Disc5, 30) + padLeft(formatter.format(Double.parseDouble(Oth5)), 32) + "\n";
                }
                byteStream.write(str26.getBytes());
            }
            if (!Disc6.equals(""))
            {
                if ((Double.parseDouble(Oth6) == 0) || Oth6.isEmpty())
                {
                    str27 = "  " + padRight(Disc6, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str27 = "  " + padRight(Disc6, 30) + padLeft(formatter.format(Double.parseDouble(Oth6)), 32) + "\n";
                }
                byteStream.write(str27.getBytes());
            }
            if (!Disc7.equals(""))
            {
                if ((Double.parseDouble(Oth7) == 0) || Oth7.isEmpty())
                {
                    str28 = "  " + padRight(Disc7, 30) + padLeft("0.00", 32) + "\n";
                }
                str28 = "  " + padRight(Disc7, 30) + padLeft(formatter.format(Double.parseDouble(Oth7)), 32) + "\n";
                byteStream.write(str28.getBytes());
            }
            if (!Disc8.equals(""))
            {
                if ((Double.parseDouble(Oth8) == 0) || Oth8.isEmpty())
                {
                    str29 = "  " + padRight(Disc8, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str29 = "  " + padRight(Disc8, 30) + padLeft(formatter.format(Double.parseDouble(Oth8)), 32) + "\n";
                }
                byteStream.write(str29.getBytes());
            }
            if (!Disc9.equals(""))
            {
                if ((Double.parseDouble(Oth9) == 0) || Oth9.isEmpty())
                {
                    str30 = "  " + padRight(Disc9, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str30 = "  " + padRight(Disc9, 30) + padLeft(formatter.format(Double.parseDouble(Oth9)), 32) + "\n";
                }
                byteStream.write(str30.getBytes());
            }
            if (!Disc10.equals(""))
            {
                if ((Double.parseDouble(Oth10) == 0) || Oth10.isEmpty())
                {
                    str31 = "  " + padRight(Disc10, 30) + padLeft("0.00", 32) + "\n";
                }
                else
                {
                    str31 = "  " + padRight(Disc10, 30) + padLeft(formatter.format(Double.parseDouble(Oth10)), 32) + "\n";
                }
                byteStream.write(str31.getBytes());
            }
            OTHERS_total = Double.parseDouble(Oth1) + Double.parseDouble(Oth2) + Double.parseDouble(Oth3) + Double.parseDouble(Oth4);
            OTHERS_total = OTHERS_total + Double.parseDouble(Oth5) + Double.parseDouble(Oth6) + Double.parseDouble(Oth7);
            OTHERS_total = OTHERS_total + Double.parseDouble(Oth8) + Double.parseDouble(Oth9) + Double.parseDouble(Oth10);
            OTHERS_total = OTHERS_total + Double.parseDouble(c_arrears);


            //GRAND TOTAL LINE//
            Double GRANDTOTAL = OTHERS_total + Total_Bill;
            String str43 = "  ==================================================================\n";
            String str44 = " TOTAL AMOUNT DUE     " + padLeft(formatter.format(GRANDTOTAL), 10) + "\n";
            byteStream.write(str43.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 2, 1));
            byteStream.write(str44.getBytes());
            //AMOUNT AFTER DUE DATE LINE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            if (AfterDue.equals("1"))
            {
                Double AmntAfterDue = (Double.parseDouble(ETbillamt.getText().toString())) * .10;
                AmntAfterDue = AmntAfterDue + Total_Bill + OTHERS_total;
                if (c_penStat.equals("Y"))
                {
                    String str45 = "  AMOUNT AFTER DUE        " + padLeft(formatter.format(AmntAfterDue), 38) + "\n";
//                    Double penaltyamt = (Double.parseDouble(ETbillamt.getText().toString())) * .10;
//
//                    String str451 = "  10% Penalty:            " + padLeft(formatter.format(penaltyamt), 38) + "\n";
//                    byteStream.write(str451.getBytes());
                    byteStream.write(str45.getBytes());
                }
            }


//            //BILLING CONSUMPTIONS//
//            String strBC1 = "  ==================================================================\n";
//            byteStream.write(strBC1.getBytes());
//            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
//            String strBCHeader = "  CONSUMPTION FOR THE LAST 6 MONTHS (IN CUBIC METERS): \n";
//            byteStream.write(strBCHeader.getBytes());
//            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
//            String strBP1 = "     " + S_DBP1 + ": " + padLeft(BP1, 11) + "\n";
//            byteStream.write(strBP1.getBytes());
//            String strBP2 = "     " + S_DBP2 + ": " + padLeft(BP2, 11) + "\n";
//            byteStream.write(strBP2.getBytes());
//            String strBP3 = "     " + S_DBP3 + ": " + padLeft(BP3, 11) + "\n";
//            byteStream.write(strBP3.getBytes());
//            String strBP4 = "     " + S_DBP4 + ": " + padLeft(BP4, 11) + "\n";
//            byteStream.write(strBP4.getBytes());
//            String strBP5 = "     " + S_DBP5 + ": " + padLeft(BP5, 11) + "\n";
//            byteStream.write(strBP5.getBytes());
//            String strBP6 = "     " + S_DBP6 + ": " + padLeft(BP6, 11) + "\n";
//            byteStream.write(strBP6.getBytes());
            //METER READER LINE//
            String str46 = "  ==================================================================\n";
            byteStream.write(str46.getBytes());
            String str47 = "  Meter Reader Name : " + UserString + "\n";
            byteStream.write(str47.getBytes());
            String strdt = "  Date and Time Read: " + localtime + "\n\n";
            byteStream.write(strdt.getBytes());
            //BARCODE LINE//
            SetupBarcodes();
            String Spacer = "      ";
            byteStream.write(Spacer.getBytes());
            byteStream.write(WoosimBarcode.createBarcode(WoosimBarcode.CODE128, 3, 100, false, PrimeBarCode.getBytes()));
            String Spacer2 = "\n\n\n                            ";
            byteStream.write(Spacer2.getBytes());
            byteStream.write(WoosimBarcode.create2DBarcodeQRCode(0, (byte) 0x4d, 5, PrimeBarCode.getBytes()));



            //CUSTOMIZED FOOTER//


//            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));

            String str48 = "\nRemarks \n\n";
            byteStream.write(str48.getBytes());
            /*String strvat1 = "  " + "Please be informed that 12% VAT will be charged to customers\n";
            String strvat2 = "  " + "effective April 01, 2022 billing. VAT collected will be remitted\n";
            String strvat3 = "  " + "to the Bureau of Internal Revenue (BIR).\n";

            byteStream.write(strvat1.getBytes());
            byteStream.write(strvat2.getBytes());
            byteStream.write(strvat3.getBytes());*/
            if (!D1.equals(""))
            {
                String str49 = "  " + D1 + "\n";
                byteStream.write(str49.getBytes());
            }
            if (!D2.equals(""))
            {
                String str50 = "  " + D2 + "\n";
                byteStream.write(str50.getBytes());
            }
            if (!D3.equals(""))
            {
                String str51 = "  " + D3 + "\n";
                byteStream.write(str51.getBytes());
            }
            if (!D4.equals(""))
            {
                String str52 = "  " + D4 + "\n";
                byteStream.write(str52.getBytes());
            }
            if (!D5.equals(""))
            {
                String str53 = "  " + D5 + "\n";
                byteStream.write(str53.getBytes());
            }
            String str54 = " \n";
            byteStream.write(str54.getBytes());

            //FINAL FOOTER//
            String str55 = "               To avoid penalty, please pay your bill               " + "\n";
            String str56 = "                      on or before " + c_dueDate + "\n";
            String str57 = "                            THANK YOU." + "\n\n";
            byteStream.write(str55.getBytes());
            byteStream.write(str56.getBytes());
            byteStream.write(str57.getBytes());


            //DISCONNECTION FOOTER//
            if ((Double.parseDouble(c_mosarr) > 1) && Double.parseDouble(_arrears.replace(",","")) >= 0)
            {

                String strx = "  ==================================================================\n\n";
                byteStream.write(strx.getBytes());

                String strdisc1 = "           Please disregard previous arrears if payment has                   " + "\n";
                String strdisc2 = "                           already been made                   " + "\n";
                String strdisc3 = "                 ******Please present this bill upon                   " + "\n";
                String strdisc4 = "                             payment******                   " + "\n\n";


                String strdisc5 = "                        DISCONNECTION NOTICE        " + "\n\n";
                String strdisc6 = "  " + "Account No.:" + f1_acctno + "\n";
                String strdisc7 = "  " + "Name:" + fl_name.getText() + "\n\n";
                String strdisc8 = "  " + "We would like to inform you of your total amount due" + "\n";
                String strdisc9 = "  " + "of " + formatter.format(GRANDTOTAL) + ".\n\n";
                String strdisc10 = "  " + "Please pay this bill on or before due date to avoid" + "\n";
                String strdisc11 = "  " + "penalty and service interruption. Thank you." + "\n\n";
                String strdisc12 = "                   Your Disconnection date is on " + c_discodate + "                   " + "\n";
                String strdisc13 = "                       Please pay your bills on time.                   " + "\n\n";


                String strdisc14 = "  ==================================================================\n";

                byteStream.write(strdisc1.getBytes());
                byteStream.write(strdisc2.getBytes());
                byteStream.write(strdisc3.getBytes());
                byteStream.write(strdisc4.getBytes());
                byteStream.write(strdisc5.getBytes());
                byteStream.write(strdisc6.getBytes());
                byteStream.write(strdisc7.getBytes());
                byteStream.write(strdisc8.getBytes());
                byteStream.write(strdisc9.getBytes());
                byteStream.write(strdisc10.getBytes());
                byteStream.write(strdisc11.getBytes());
                byteStream.write(strdisc12.getBytes());
                byteStream.write(strdisc13.getBytes());
                byteStream.write(strdisc14.getBytes());
            }


            String str58 = "  ==================================================================\n\n";
            byteStream.write(str58.getBytes());
            String strPayDet1 = "  To receive further notification and to receive your electronic    \n";
            String strPayDet2 = "  billing via SMS and Email, please enroll your details by scanning \n";
            String strPayDet3 = "  the QR code below. Thank you!                                     \n\n";
            byteStream.write(strPayDet1.getBytes());
            byteStream.write(strPayDet2.getBytes());
            byteStream.write(strPayDet3.getBytes());
            String SpacerPay = "                            ";
            byteStream.write(SpacerPay.getBytes());
            byteStream.write(WoosimBarcode.create2DBarcodeQRCode(0, (byte) 0x4d, 5, PrimePayable.getBytes()));
            String FinalFeed = "\n\n\n";
            byteStream.write(FinalFeed.getBytes());
        }
        //FINAL PRINT CMD//
        byteStream.write(WoosimCmd.PM_printStdMode());
        MainActivity.mPrintService.write(byteStream.toByteArray());

        _PrintCounter = Double.parseDouble(PrintCounter) + 1;
        databaseHelper.updatePrintCounter(String.valueOf(_PrintCounter), f1_acctno);
        //}
    }
    //PROCEDURE FOR PRINTING READ RECORDS//
    public void printRead() throws IOException
    {
        cursorSummaryRead = databaseHelper.fetchRead();
        cursorReadTotalCUM = databaseHelper.fetchResultCUMTotal();

        if (cursorSummaryRead.getCount() > 0)
        {
            MainActivity.mPrintService.write(WoosimCmd.initPrinter());
            sendImg(0, 0, R.drawable.logo);
            MainActivity.mPrintService.write(WoosimCmd.printData());
            presDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String str0 = "\nMASTER LIST OF READ CONSUMERS\n\n";
            String str_presdate = "Date Read : " + presDate + "\n\n";
            String str_1sthead  = "Account No.                            Consumer Name        Prev Rdg  Pres Rdg     CuM   FF " + "\n";
            String str_2ndhead  = "--------------------------------------------------------------------------------------------" + "\n";
            String str_footer   = "--------------------------------------------------------------------------------------------" + "\n";
            String str_total    = "TOTAL RECORDS    : " + cursorSummaryRead.getCount() + "\n";
            String str_totalCUM = "TOTAL CU.M CONS  : " + cursorReadTotalCUM.getString(cursorReadTotalCUM.getColumnIndex("TotalCUM")) + "\n\n";
            String str_reader   = "Meter Reader     : " + UserString + "\n\n\n";

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(800);
            //TITLE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
            byteStream.write(str0.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            byteStream.write(str_presdate.getBytes());
            byteStream.write(str_1sthead.getBytes());
            byteStream.write(str_2ndhead.getBytes());
            do
            {
                String _ACCTNO = cursorSummaryRead.getString(1);
                String _NAME = cursorSummaryRead.getString(2);
                String _FFC = cursorSummaryRead.getString(27);
                String _PREVRDG = cursorSummaryRead.getString(9);
                String _PRESRDG = cursorSummaryRead.getString(19);
                String _CUM = cursorSummaryRead.getString(20);

                if (TextUtils.isEmpty(_ACCTNO)) { _ACCTNO = " "; }
                if (TextUtils.isEmpty(_NAME)) { _NAME = " "; }
                if (TextUtils.isEmpty(_FFC)) { _FFC = " "; }
                if (TextUtils.isEmpty(_PREVRDG)) { _PREVRDG = " "; }
                if (TextUtils.isEmpty(_PRESRDG)) { _PRESRDG = " "; }
                if (TextUtils.isEmpty(_CUM)) { _CUM = " "; }
                String str_read = padLeft(_ACCTNO, 11) +
                        padLeft(_NAME, 41) +
                        padLeft(_PREVRDG, 13) +
                        padLeft(_PRESRDG, 10) +
                        padLeft(_CUM, 8) +
                        padLeft(_FFC, 5) + "\n";
                byteStream.write(str_read.getBytes());
            }
            while (cursorSummaryRead.moveToNext());
            byteStream.write(str_footer.getBytes());
            byteStream.write(str_total.getBytes());
            byteStream.write(str_totalCUM.getBytes());
            byteStream.write(str_reader.getBytes());
            byteStream.write(WoosimCmd.PM_printStdMode());
            MainActivity.mPrintService.write(byteStream.toByteArray());
        }
        else
        {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("No Master list of read records to print yet.");
            dlgAlert.setTitle("WARNING!!!");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
        }
    }
    //PROCEDURE FOR PRINTING RECORDS 1 TO 100//
    public void print1to100() throws IOException
    {
        cursorSummaryRead = databaseHelper.fetchRead1to100();
        cursorReadTotalCUM = databaseHelper.fetchResultCUMTotal1to100();

        if (cursorSummaryRead.getCount() > 0)
        {
            MainActivity.mPrintService.write(WoosimCmd.initPrinter());
            sendImg(0, 0, R.drawable.logo);
            MainActivity.mPrintService.write(WoosimCmd.printData());
            presDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String str0 = "\nMASTER LIST OF READ CONSUMERS (1 to 100)\n\n";
            String str_presdate = "Date Read : " + presDate + "\n\n";
            String str_1sthead  = "Account No.                            Consumer Name        Prev Rdg  Pres Rdg     CuM   FF " + "\n";
            String str_2ndhead  = "--------------------------------------------------------------------------------------------" + "\n";
            String str_footer   = "--------------------------------------------------------------------------------------------" + "\n";
            String str_total    = "TOTAL RECORDS    : " + cursorSummaryRead.getCount() + "\n";
            String str_totalCUM = "TOTAL CU.M CONS  : " + cursorReadTotalCUM.getString(cursorReadTotalCUM.getColumnIndex("TotalCUM")) + "\n\n";
            String str_reader   = "Meter Reader     : " + UserString + "\n\n\n";

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(800);
            //TITLE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
            byteStream.write(str0.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            byteStream.write(str_presdate.getBytes());
            byteStream.write(str_1sthead.getBytes());
            byteStream.write(str_2ndhead.getBytes());
            do
            {
                String _ACCTNO = cursorSummaryRead.getString(1);
                String _NAME = cursorSummaryRead.getString(2);
                String _FFC = cursorSummaryRead.getString(27);
                String _PREVRDG = cursorSummaryRead.getString(9);
                String _PRESRDG = cursorSummaryRead.getString(19);
                String _CUM = cursorSummaryRead.getString(20);

                if (TextUtils.isEmpty(_ACCTNO)) { _ACCTNO = " "; }
                if (TextUtils.isEmpty(_NAME)) { _NAME = " "; }
                if (TextUtils.isEmpty(_FFC)) { _FFC = " "; }
                if (TextUtils.isEmpty(_PREVRDG)) { _PREVRDG = " "; }
                if (TextUtils.isEmpty(_PRESRDG)) { _PRESRDG = " "; }
                if (TextUtils.isEmpty(_CUM)) { _CUM = " "; }
                String str_read = padLeft(_ACCTNO, 11) +
                        padLeft(_NAME, 41) +
                        padLeft(_PREVRDG, 13) +
                        padLeft(_PRESRDG, 10) +
                        padLeft(_CUM, 8) +
                        padLeft(_FFC, 5) + "\n";
                byteStream.write(str_read.getBytes());
            }
            while (cursorSummaryRead.moveToNext());
            byteStream.write(str_footer.getBytes());
            byteStream.write(str_total.getBytes());
            byteStream.write(str_totalCUM.getBytes());
            byteStream.write(str_reader.getBytes());
            byteStream.write(WoosimCmd.PM_printStdMode());
            MainActivity.mPrintService.write(byteStream.toByteArray());
        }
        else
        {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("No Master list of read records to print yet.");
            dlgAlert.setTitle("WARNING!!!");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
        }
    }
    //PROCEDURE FOR PRINTING RECORDS 101 TO 200//
    public void print101to200() throws IOException
    {
        cursorSummaryRead = databaseHelper.fetchRead101to200();
        cursorReadTotalCUM = databaseHelper.fetchResultCUMTotal101to200();

        if (cursorSummaryRead.getCount() > 0)
        {
            MainActivity.mPrintService.write(WoosimCmd.initPrinter());
            sendImg(0, 0, R.drawable.logo);
            MainActivity.mPrintService.write(WoosimCmd.printData());
            presDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String str0 = "\nMASTER LIST OF READ CONSUMERS (101 to 200)\n\n";
            String str_presdate = "Date Read : " + presDate + "\n\n";
            String str_1sthead  = "Account No.                            Consumer Name        Prev Rdg  Pres Rdg     CuM   FF " + "\n";
            String str_2ndhead  = "--------------------------------------------------------------------------------------------" + "\n";
            String str_footer   = "--------------------------------------------------------------------------------------------" + "\n";
            String str_total    = "TOTAL RECORDS    : " + cursorSummaryRead.getCount() + "\n";
            String str_totalCUM = "TOTAL CU.M CONS  : " + cursorReadTotalCUM.getString(cursorReadTotalCUM.getColumnIndex("TotalCUM")) + "\n\n";
            String str_reader   = "Meter Reader     : " + UserString + "\n\n\n";

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(800);
            //TITLE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
            byteStream.write(str0.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            byteStream.write(str_presdate.getBytes());
            byteStream.write(str_1sthead.getBytes());
            byteStream.write(str_2ndhead.getBytes());
            do
            {
                String _ACCTNO = cursorSummaryRead.getString(1);
                String _NAME = cursorSummaryRead.getString(2);
                String _FFC = cursorSummaryRead.getString(27);
                String _PREVRDG = cursorSummaryRead.getString(9);
                String _PRESRDG = cursorSummaryRead.getString(19);
                String _CUM = cursorSummaryRead.getString(20);

                if (TextUtils.isEmpty(_ACCTNO)) { _ACCTNO = " "; }
                if (TextUtils.isEmpty(_NAME)) { _NAME = " "; }
                if (TextUtils.isEmpty(_FFC)) { _FFC = " "; }
                if (TextUtils.isEmpty(_PREVRDG)) { _PREVRDG = " "; }
                if (TextUtils.isEmpty(_PRESRDG)) { _PRESRDG = " "; }
                if (TextUtils.isEmpty(_CUM)) { _CUM = " "; }
                String str_read = padLeft(_ACCTNO, 11) +
                        padLeft(_NAME, 41) +
                        padLeft(_PREVRDG, 13) +
                        padLeft(_PRESRDG, 10) +
                        padLeft(_CUM, 8) +
                        padLeft(_FFC, 5) + "\n";
                byteStream.write(str_read.getBytes());
            }
            while (cursorSummaryRead.moveToNext());
            byteStream.write(str_footer.getBytes());
            byteStream.write(str_total.getBytes());
            byteStream.write(str_totalCUM.getBytes());
            byteStream.write(str_reader.getBytes());
            byteStream.write(WoosimCmd.PM_printStdMode());
            MainActivity.mPrintService.write(byteStream.toByteArray());
        }
        else
        {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("No Master list of read records to print yet.");
            dlgAlert.setTitle("WARNING!!!");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
        }
    }
    //PROCEDURE FOR PRINTING RECORDS 200 AND UP//
    public void print201up() throws IOException
    {
        cursorSummaryRead = databaseHelper.fetchRead201up();
        cursorReadTotalCUM = databaseHelper.fetchResultCUMTotal201up();

        if (cursorSummaryRead.getCount() > 0)
        {
            MainActivity.mPrintService.write(WoosimCmd.initPrinter());
            sendImg(0, 0, R.drawable.logo);
            MainActivity.mPrintService.write(WoosimCmd.printData());
            presDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            String str0 = "\nMASTER LIST OF READ CONSUMERS (201 above)\n\n";
            String str_presdate = "Date Read : " + presDate + "\n\n";
            String str_1sthead  = "Account No.                            Consumer Name        Prev Rdg  Pres Rdg     CuM   FF " + "\n";
            String str_2ndhead  = "--------------------------------------------------------------------------------------------" + "\n";
            String str_footer   = "--------------------------------------------------------------------------------------------" + "\n";
            String str_total    = "TOTAL RECORDS    : " + cursorSummaryRead.getCount() + "\n";
            String str_totalCUM = "TOTAL CU.M CONS  : " + cursorReadTotalCUM.getString(cursorReadTotalCUM.getColumnIndex("TotalCUM")) + "\n\n";
            String str_reader   = "Meter Reader     : " + UserString + "\n\n\n";

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(800);
            //TITLE//
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_LARGE));
            byteStream.write(WoosimCmd.setTextStyle(true, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_CENTER));
            byteStream.write(str0.getBytes());
            byteStream.write(WoosimCmd.setCodeTable(WoosimCmd.MCU_RX, WoosimCmd.CT_CP437, WoosimCmd.FONT_MEDIUM));
            byteStream.write(WoosimCmd.setTextStyle(false, false, false, 1, 1));
            byteStream.write(WoosimCmd.setTextAlign(WoosimCmd.ALIGN_LEFT));
            byteStream.write(str_presdate.getBytes());
            byteStream.write(str_1sthead.getBytes());
            byteStream.write(str_2ndhead.getBytes());
            do
            {
                String _ACCTNO = cursorSummaryRead.getString(1);
                String _NAME = cursorSummaryRead.getString(2);
                String _FFC = cursorSummaryRead.getString(27);
                String _PREVRDG = cursorSummaryRead.getString(9);
                String _PRESRDG = cursorSummaryRead.getString(19);
                String _CUM = cursorSummaryRead.getString(20);

                if (TextUtils.isEmpty(_ACCTNO)) { _ACCTNO = " "; }
                if (TextUtils.isEmpty(_NAME)) { _NAME = " "; }
                if (TextUtils.isEmpty(_FFC)) { _FFC = " "; }
                if (TextUtils.isEmpty(_PREVRDG)) { _PREVRDG = " "; }
                if (TextUtils.isEmpty(_PRESRDG)) { _PRESRDG = " "; }
                if (TextUtils.isEmpty(_CUM)) { _CUM = " "; }
                String str_read = padLeft(_ACCTNO, 11) +
                        padLeft(_NAME, 41) +
                        padLeft(_PREVRDG, 13) +
                        padLeft(_PRESRDG, 10) +
                        padLeft(_CUM, 8) +
                        padLeft(_FFC, 5) + "\n";
                byteStream.write(str_read.getBytes());
            }
            while (cursorSummaryRead.moveToNext());
            byteStream.write(str_footer.getBytes());
            byteStream.write(str_total.getBytes());
            byteStream.write(str_totalCUM.getBytes());
            byteStream.write(str_reader.getBytes());
            byteStream.write(WoosimCmd.PM_printStdMode());
            MainActivity.mPrintService.write(byteStream.toByteArray());
        }
        else
        {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
            dlgAlert.setMessage("No Master list of read records to print yet.");
            dlgAlert.setTitle("WARNING!!!");
            dlgAlert.setCancelable(false);
            dlgAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();
                }
            });
            dlgAlert.create().show();
        }
    }
    //PROCEDURE FOR LOADING IMAGE FOR SOA PRINTING//
    private void sendImg(int x, int y, int id)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "databases" + File.separator + "logo.bmp");
        if (bmp == null) return;

        byte[] data = WoosimImage.drawBitmap(x, y, bmp);
        bmp.recycle();

        MainActivity.mPrintService.write(data);
    }
    //PROCEDURE FOR LEFT PADDING USED IN SOA PRINTING//
    public static String padLeft(String s, int n)
    {
        return String.format("%1$" + n + "s", s);
    }
    //PROCEDURE FOR RIGHT PADDING USED IN SOA PRINTING//
    public static String padRight(String s, int n)
    {
        return String.format("%1$-" + n + "s", s);
    }
    //PROCEDURE FOR STORAGE PERMISSIONS VERIFICATION//
    public static void verifyStoragePermissions(Activity activity)
    {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
