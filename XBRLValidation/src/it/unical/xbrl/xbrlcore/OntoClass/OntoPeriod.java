/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

/**
 *
 * @author Admin
 */
public class OntoPeriod extends ObjectEsd {

    private OntoDate starting;
    private OntoDate ending;

    @Override
    public String getContext() {
        try {
            return " : period( starting : " + starting.getIdDLVStr()+ ", ending : " + ending.getIdDLVStr()+ " ).";
        } catch (NullPointerException e) {
            return ": period NULL";
        }
    }

    @Override
    public String getClassType() {
        return "period";
    }

    public void setInstant(OntoDate instant) {
        starting = instant;
        ending = instant;
    }

    public OntoDate getStarting() {
        return starting;
    }

    public void setStarting(OntoDate starting) {
        this.starting = starting;
    }

    public OntoDate getEnding() {
        return ending;
    }

    public void setEnding(OntoDate ending) {
        this.ending = ending;
    }

}
