/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.blockchain;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class Transaction implements Serializable{
    private String id;
    private String from;
    private String to;
    private String value;
    private String blockNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Transaction(String id, String from, String to, String value, String blockNumber) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.value = value;
        this.blockNumber = blockNumber;
    }
    
}
