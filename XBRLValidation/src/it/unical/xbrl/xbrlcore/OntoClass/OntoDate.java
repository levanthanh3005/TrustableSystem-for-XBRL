/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class OntoDate extends ObjectEsd {

    private Integer day;
    private Integer month;
    private Integer year;
    private Date date;

    @Override
    public String getContext() {
        return " :  date(day : " + day + ", month :" + month + ", year :" + year + "). ";
    }

    @Override
    public String getClassType() {
        return "date";
    }

    public OntoDate(String text) {
        setDate(text);
    }

    public OntoDate() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String text) {
        try {
            this.date = new SimpleDateFormat("yyyy-MM-dd").parse(text);
            String sY = new SimpleDateFormat("yyyy").format(date);
            String sM = new SimpleDateFormat("MM").format(date);
            String sD = new SimpleDateFormat("dd").format(date);
            year = Integer.parseInt(sY);
            month = Integer.parseInt(sM);
            day = Integer.parseInt(sD);
        } catch (ParseException ex) {
            System.err.println("ODate:" + ex.toString());
        }
    }
}
