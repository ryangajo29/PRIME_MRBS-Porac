package com.primemrbs;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesCarlo on 09/07/2021.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{

    String DB_PATH;
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    SQLiteDatabase db;
    private final String USER_TABLE = "users";

    //SETTING THE DATABASE//
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.DB_PATH = Environment.getExternalStorageDirectory() + File.separator + "databases";
        Log.e("Path 1", DB_PATH);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //DO NOTHING
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        //DO NOTHING
    }
    //SETTING THE DATABASE PATH//
    public void openDatabase() throws SQLException
    {
        String myPath = Environment.getExternalStorageDirectory() + File.separator + "databases" + File.separator + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    //CLOSING PROCEDURE FOR THE DATABASE, SYNCHRONIZED DURING DATA CLOSING EVENT//
    @Override
    public synchronized void close()
    {
        if (db != null)
            //db.close();
        super.close();
    }
    //Check if account is downloads are for reprinting/
    public boolean checkIfDownloadrePrint()
    {
        openDatabase();


        Cursor cursor = db.rawQuery("SELECT AccntNo from FORREAD where co_remarks='FOR PRINTING' order by rowid asc limit 1", null);
        int count = cursor.getCount();

        cursor.close();
        close();

        if(count > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //VERIFICATION FOR USERNAME AND PASSWORD IN THE LOGIN FORM//
    public boolean checkUserExist(String username, String password)
    {
        String[] columns = {"username"};
        openDatabase();

        String selection = "username=? and password=?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();

        cursor.close();
        close();

        if(count > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //Check if account is for reprinting/
    public boolean checkIfPrint(String accntno)
    {
        openDatabase();


        Cursor cursor = db.rawQuery("SELECT AccntNo from FORREAD where accntno = '" + accntno + "' and co_remarks='FOR PRINTING'", null);
        int count = cursor.getCount();

        cursor.close();
        close();

        if(count > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //LOAD USERS LIST TO BE DISPLAYED IN THE DROP DOWN BOX IN THE LOGIN FORM//
    public ArrayList<String> loadUsers()
    {
        ArrayList<String> list = new ArrayList<String>();

        openDatabase();
        db.beginTransaction();
        try
        {
            String selectQuery = "SELECT * FROM " + USER_TABLE;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0)
            {
                while (cursor.moveToNext())
                {
                    String uname = cursor.getString(cursor.getColumnIndex("username"));
                    list.add(uname);
                }
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
            //db.close();
        }
        return list;
    }
    //PROCEDURE TO CHECK THE READING STATUS//
    public void CheckReadStat(String _book)
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorUnread = db.rawQuery("SELECT AccntNo from FORREAD where co_Activity='U'", null);
        if (cursorUnread != null)
        {
            cursorUnread.moveToFirst();
        }
        int count = cursorUnread.getCount();

        if (String.valueOf(count).equals("0"))
        {
            ContentValues con = new ContentValues();
            con.put("BOOK", _book);
            con.put("STATUS", "ALL READ");
            db.update("FACTORS", con, null, null);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO FETCH LIST OF USERS//
    public Cursor fetchUsers()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM users" , null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO SELECT PRINTER LIST SAVED IN THE DATABASE//
    public Cursor fetchPrinter()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM PRINTER" , null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO SELECT DATA FROM THE FACTORS TABLE//
    public Cursor fetchFactors()
    {
        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM FACTORS" , null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //SELECTED USER FROM LOGIN FORM WILL BE SAVED HERE. THIS IS THEN SELECTED TO BE PRINTED IN THE SOA//
    public Cursor fetchFixedUser()
    {
        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM FIXEDUSER" , null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO SELECT ALL DATA FROM THE FORREAD TABLE//
    public Cursor fetchAll()
    {
        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM FORREAD" , null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO SELECT DATA FROM THE WATER RATES TABLE//
    public Cursor fetchWRATES(String _CMCode, String _BillType)
    {
        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM WRATES where CMCode = '" + _CMCode.trim() + "' AND BillType = '" + _BillType + "'", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO SELECT DATA FROM THE NEW METERS TABLE//
    public Cursor fetchNewMtrs()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM NEW_ACCOUNTS"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT ACTIVE RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchActive()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE Status='A'"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT INACTIVE RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchInactive()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE Status='I'"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT READ RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchRead()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE co_ACTIVITY='R'"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT 1 TO 100 ACTIVE RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchRead1to100()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE co_ACTIVITY='R' LIMIT 100 OFFSET 0"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT 101 TO 200 ACTIVE RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchRead101to200()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE co_ACTIVITY='R' LIMIT 100 OFFSET 100"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT 200 AND UP ACTIVE RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchRead201up()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE co_ACTIVITY='R' LIMIT 1000 OFFSET 200"  , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT UNREAD RECORDS FROM THE FORREAD TABLE//
    public Cursor fetchUnread()
    {
        openDatabase();
        db.beginTransaction();

        Cursor cursorRead = db.rawQuery("SELECT * FROM FORREAD WHERE co_ACTIVITY='U'" , null);
        if (cursorRead != null)
        {
            cursorRead.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursorRead;
    }
    //PROCEDURE TO SELECT DATA FROM THE FIELD FINDINGS TABLE//
    public Cursor fetchFF()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM FNDNGS ORDER BY F_CODE" , null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO COMPUTE AND SELECT COMPUTED AMOUNT OF THE CUM FIELD IN THE FORREAD TABLE//
    public Cursor fetchResultCUMTotal()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT SUM(co_CUM) as TotalCUM FROM FORREAD", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO COMPUTE AND SELECT COMPUTED AMOUNT OF THE CUM FIELD IN THE FORREAD TABLE//
    public Cursor fetchResultCUMTotal1to100()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT SUM(co_CUM) as TotalCUM FROM FORREAD LIMIT 100 OFFSET 0", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO COMPUTE AND SELECT COMPUTED AMOUNT OF THE CUM FIELD IN THE FORREAD TABLE//
    public Cursor fetchResultCUMTotal101to200()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT SUM(co_CUM) as TotalCUM FROM FORREAD LIMIT 100 OFFSET 100", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO COMPUTE AND SELECT COMPUTED AMOUNT OF THE CUM FIELD IN THE FORREAD TABLE//
    public Cursor fetchResultCUMTotal201up()
    {

        openDatabase();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT SUM(co_CUM) as TotalCUM FROM FORREAD LIMIT 1000 OFFSET 200", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return cursor;
    }
    //PROCEDURE TO INSERT SELECT USER FROM THE LOGIN FORM TO THE FIXED USER TABLE//
    public void InsertUser(String _user)
    {

        openDatabase();
        db.beginTransaction();
        db.delete("FIXEDUSER", null, null);
        ContentValues con = new ContentValues();
        con.put("FUSER", _user);

        db.insert("FIXEDUSER", null, con);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO INSERT NEW PRINTER MAC ADDRESS IN THE PRINTER TABLE FOR EASE OF RECONNECTION//
    public void InsertPrinter(String _printer)
    {

        openDatabase();
        db.beginTransaction();
        db.delete("PRINTER", null, null);
        ContentValues con = new ContentValues();
        con.put("PRNTMAC", _printer);

        db.insert("PRINTER", null, con);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO SAVE NEW METER ADDED IN THE NEW METER TABLE//
    public void InsertNewMtr(String _nmName, String _nmMtrNo, String _nmAddress, String _nmRdg, String _nmNote) {

        openDatabase();
        db.beginTransaction();
        ContentValues con = new ContentValues();
        con.put("Name", _nmName);
        con.put("MtrNo", _nmMtrNo);
        con.put("Address", _nmAddress);
        con.put("Rdng", _nmRdg);
        con.put("Note", _nmNote);

        db.insert("NEW_ACCOUNTS", null, con);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO SAVE READING OUTPUT IN THE FORREAD TABLE//
    public void updateRec(String _readcount, String _activity, String _presrdg,
                          String _cum, String _totalbill, String _presdate, String _lh_rdg, String _gpsloc, String _acctNo,String _rTime)
    {

        openDatabase();
        db.beginTransaction();

        ContentValues con = new ContentValues();
        con.put("SumCounter", _readcount);
        con.put("co_Activity", _activity);
        con.put("co_PresRdg", _presrdg);
        con.put("co_CUM", _cum);
        con.put("co_Amount", _totalbill);
        con.put("PresRdgDate", _presdate);
        con.put("lh_rdg", _lh_rdg);
        con.put("GPSLOC", _gpsloc);
        con.put("timeread", _rTime);
        db.update("FORREAD", con, "AccntNo = '" + _acctNo + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO SAVE THE CURRENT GPS LOCATION OF THE CONSUMER IN THE FORREAD TABLE//
    public void updateGPSloc(String _gpsloc, String _acctNo)
    {

        openDatabase();
        db.beginTransaction();

        ContentValues con = new ContentValues();
        con.put("GPSHLOC", _gpsloc);
        db.update("FORREAD", con, "AccntNo = '" + _acctNo + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO SAVE THE INCREMENTING PRINT COUNTER IN THE FORREAD TABLE//
    public void updatePrintCounter(String _printcount, String _acctNo)
    {

        openDatabase();
        db.beginTransaction();

        ContentValues con = new ContentValues();
        con.put("PrintCounter", _printcount);
        db.update("FORREAD", con, "AccntNo = '" + _acctNo + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO SAVE THE CHOSEN FIELD FINDINGS FOR THE CONSUMER IN THE FORREAD TABLE//
    public void updateFF(String _ffcode, String _ffdesc, String _ffremarks, String _activity, String _acctNo,String _rTime)
    {

        openDatabase();
        db.beginTransaction();


        ContentValues con = new ContentValues();
        con.put("co_Remarks", _ffremarks);
        con.put("FieldRemarks", _ffcode);
        con.put("Remarks", _ffdesc);
        con.put("co_Activity", _activity);
        con.put("timeread", _rTime);
        db.update("FORREAD", con, "AccntNo = '" + _acctNo + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO UPDATE EXIISTING PRINTER MAC ADDRESS IN THE PRINTER TABLE FOR EASE OF RECONNECTION//
    public void updatePrntCode(String _prntcode)
    {

        openDatabase();
        db.beginTransaction();

        ContentValues con = new ContentValues();
        con.put("PRNTMAC", _prntcode);
        db.update("PRINTER", con, null, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }
    //PROCEDURE TO LOAD CARDS DISPLAY IN THE SEARCH FORM//
    public List<DataModel> loadSearchAll()
    {

        openDatabase();
        db.beginTransaction();

        List<DataModel> data=new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from FORREAD" + " ;",null);
        StringBuffer stringBuffer = new StringBuffer();
        DataModel dataModel = null;
        while (cursor.moveToNext())
        {
            dataModel= new DataModel();
            String pageno = cursor.getString(cursor.getColumnIndexOrThrow("SConNo"));
            String readstat = cursor.getString(cursor.getColumnIndexOrThrow("co_Activity"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String mtrno = cursor.getString(cursor.getColumnIndexOrThrow("MtrNo"));
            String acctno = cursor.getString(cursor.getColumnIndexOrThrow("AccntNo"));
            String oldacctno = cursor.getString(cursor.getColumnIndexOrThrow("OdCustNum"));
            String searchid = cursor.getString(cursor.getColumnIndexOrThrow("AccntNo"));
            dataModel.setReadstat(readstat);
            dataModel.setName(name);
            dataModel.setMtrno(mtrno);
            dataModel.setAcctno(acctno);
            dataModel.setOldacctno(oldacctno);
            dataModel.setID(searchid);
            stringBuffer.append(dataModel);
            data.add(dataModel);
        }

        for (DataModel mo:data )
        {

            Log.i("Hello",""+mo.getName());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        //

        return data;
    }
    //PROCEDURE TO Get the last consumption//
    public String fetchLastConsumption(String _acctno)
    {

        openDatabase();
        db.beginTransaction();
        String _lastcons = "";
        Cursor cursor = db.rawQuery("SELECT lastcons FROM FORREAD where accntno = '" + _acctno + "'", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            _lastcons = cursor.getString(0);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return _lastcons;
    }
    //PROCEDURE to get the last input reading//
    public String fetchLastInput(String _acctno)
    {

        openDatabase();
        db.beginTransaction();
        String _lastentry = "";
        Cursor cursor = db.rawQuery("SELECT ifnull(co_PresRDG,'') as co_PresRDG FROM FORREAD where accntno = '" + _acctno + "'", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            _lastentry = cursor.getString(0);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return _lastentry;
    }
    //PROCEDURE to get the last input reading//
    public double fetchSurchargeBases(String _acctno)
    {

        openDatabase();
        db.beginTransaction();
        double _SurchargeBasis = 0;
        Cursor cursor = db.rawQuery("SELECT ifnull(surcharge_bases,0) as surcharge_bases FROM FORREAD where accntno = '" + _acctno + "'", null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            _SurchargeBasis = Double.parseDouble(cursor.getString(0));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
        return _SurchargeBasis;
    }
}
