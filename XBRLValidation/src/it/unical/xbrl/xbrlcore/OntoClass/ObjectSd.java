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
public interface ObjectSd {

    public long getIdDLV();
    public String getContext();
    public String getString();
    public void setIdDLV(long idDLV);
    public String getClassType();
    public String getIdDLVStr();
    public String getId();
    public void setId(String id);
}
