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
public class OntoUnit extends ObjectEsd{

    private String measure;
    private String id;
    private String namespace;

    @Override
    public String getContext() {
        return " : unit (id : \""+id+"\" measure:\"" + getMeasure()  + "\" namespace : \""+namespace+"\" ).";
    }

    @Override
    public String getClassType() {
        return "unit";
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    
}
