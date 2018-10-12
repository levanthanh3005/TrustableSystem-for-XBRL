/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.I_DLVClassALL;

import it.unical.xbrl.xbrlcore.OntoClass.ObjectEsd;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 *
 * @author Admin
 */
public class DLVArc extends ObjectEsd {
    
	private String companyId;
    private String fromId;
    private String toId;
    private float weight;
    private String calculationalLinkId;

    public void setCompanyId(String companyId) {
        this.companyId = ExtrasStatus.normalize(companyId);
    }
    
    @Override
    public String getClassType() {
        return "Arc";
    }

    @Override
    public String getContext() {
        try {
            //return "arc (" + fromId + "," + toId + "," + weight + ",\"" + sign + "\"," + calculationalLinkId + ","+useAttribute+").";
            return "arc ("+ companyId +"," + fromId + "," + toId + ",\"" + weight + "\"," + calculationalLinkId +").";
        } catch (NullPointerException e) {
            return "arc NULL";
        }
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = ExtrasStatus.normalize(fromId);
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = ExtrasStatus.normalize(toId);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }


    public String getCalculationalLinkId() {
        return calculationalLinkId;
    }

    public void setCalculationalLinkId(String calculationalLinkId) {
        calculationalLinkId = ExtrasStatus.normalize(calculationalLinkId);
        this.calculationalLinkId = calculationalLinkId;
    }
    
}
