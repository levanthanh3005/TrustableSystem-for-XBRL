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
public class OntoContext extends ObjectEsd {

    private OntoEntity entity;
    private OntoPeriod period;

    @Override
    public String getContext() {
        try {
            return " : context( id : \""+getId()+"\" entity : " + entity.getIdDLVStr()+ ", period : " + period.getIdDLVStr()+ " ). ";
        } catch (NullPointerException e) {
            return ": context NULL";
        }
    }
    @Override
    public String getClassType() {
        return "context";
    }

    public void setEntity(OntoEntity entity) {
        this.entity = entity;
    }

    public void setPeriod(OntoPeriod period) {
        this.period = period;
    }
   
}
