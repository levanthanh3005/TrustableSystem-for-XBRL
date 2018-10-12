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
public class ObjectEsd implements ObjectSd {

    private long idDLV;
    private String id;
    @Override
    public String getContext() {
        return "";
    }

    @Override
    public String getString() {
        return getClassType() + getIdDLV()+ getContext();
    }

    @Override
    public String getClassType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getIdDLV() {
        return idDLV;
    }

    @Override
    public void setIdDLV(long idDLV) {
        this.idDLV = idDLV;
    }

    @Override
    public String getIdDLVStr() {
        return getClassType() + getIdDLV();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
