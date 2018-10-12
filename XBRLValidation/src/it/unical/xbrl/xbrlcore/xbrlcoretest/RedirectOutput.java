/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.xbrlcoretest;

/**
 *
 * @author Admin
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *  * This class captures standard output and standard error.  *  
 */
public class RedirectOutput extends Thread {

    private InputStream is;
    private OutputStream os;

    /**
     *      * outputHistory provides an ArrayList of string, being every one an
     * output line from the command line.      * There is no setter for this
     * variable, just only a getter to access from other objects.      
     */
    private ArrayList<String> outputHistory = new ArrayList<String>();

    /**
     *      * Constructor      *      * @param input Input stream, usually
     * proc.getInputStream() or proc.getErrorStream()      
     */
    public RedirectOutput(InputStream input) {
        this(input, null);
    }

    /**
     *      * Constructor      *      * @param input Input stream, usually
     * proc.getInputStream() or proc.getErrorStream()      * @param newOutput To
     * redirect the output.      
     */
    public RedirectOutput(InputStream input, OutputStream newOutput) {
        this.is = input;
        this.os = newOutput;
    }

    /**
     *      * Do the job. Read from the BufferedReader from the InputStream
     * (this.is) every line, and store in outputHistory.      * Also, if an
     * OutputStream is defined, redirect these lines (one by one) to this
     * OutputStream.      
     */
    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String linea = null;
            linea = br.readLine();
            while (linea != null) {
                if (pw != null) {
                    pw.println(linea);
                }
                outputHistory.add(linea);
                linea = br.readLine();
            }
            if (pw != null) {
                pw.flush();
            }
        } catch (java.io.IOException ioe) {
            ExtrasStatus.log(ioe);
        }
    }

    /**
     *      * outputHistory getter. No public setter.      * @return      
     */
    public ArrayList<String> getOutputHistory() {
        return outputHistory;
    }

}
