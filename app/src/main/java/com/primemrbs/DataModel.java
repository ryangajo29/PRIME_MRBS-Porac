package com.primemrbs;

/**
 * Created by JesCarlo on 10/03/2021.
 */

public class DataModel {
    //GENERAL VARIABLES//
    public String name;
    public String mtrno;
    public String acctno;
    public String oldacctno;
    public String ID;
    public String readstat;

    //GET AND SET VALUES FOR VARIABLES TO DISPLAY IN THE CARDS IN THE SEARCH FORM BASED ON CONSUMER DATA//
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getMtrno() { return mtrno; }
    public void setMtrno(String mtrno) {
        this.mtrno = mtrno;
    }

    public String getAcctno() {
        return acctno;
    }
    public void setAcctno(String acctno) {
        this.acctno = acctno;
    }

    public String getOldacctno() {
        return oldacctno;
    }
    public void setOldacctno(String oldacctno) {
        this.oldacctno = oldacctno;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getReadstat() {
        return readstat;
    }
    public void setReadstat(String readstat) {
        this.readstat = readstat;
    }
}