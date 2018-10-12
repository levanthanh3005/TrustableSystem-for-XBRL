/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.DLVClass2BigIntALL;

import it.unical.xbrl.xbrlcore.OntoClass.ObjectEsd;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 *
 * @author Admin
 */
public class DLVArc extends ObjectEsd {

    private String fromId;
    private String toId;
    private Integer weight;
    private String sign;
    private String calculationalLinkId;
    private String useAttribute;

    @Override
    public String getClassType() {
        return "Arc";
    }

    @Override
    public String getContext() {
        try {
            //return "arc (" + fromId + "," + toId + "," + weight + ",\"" + sign + "\"," + calculationalLinkId + ","+useAttribute+").";
            return "arc ("+ ExtrasStatus.normalize(ExtrasStatus.currentCompanyId) + "," + fromId + "," + toId + "," + weight + ",\"" + sign + "\"," + calculationalLinkId +").";
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
        ExtrasStatus.log("     setWeight:"+weight +" : "+ (((int)weight) > 0));
        setSign(((int)weight) > 0);
        this.weight = (int)Math.abs(weight);
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setSign(boolean bsign) {
        if (bsign) {
            this.sign = "+";
        } else {
            this.sign = "-";
        }
    }

    public String getCalculationalLinkId() {
        return calculationalLinkId;
    }

    public void setCalculationalLinkId(String calculationalLinkId) {
        calculationalLinkId = ExtrasStatus.normalize(calculationalLinkId);
        this.calculationalLinkId = calculationalLinkId;
    }

    public void setUseAttribute(String useAttribute) {
        this.useAttribute = useAttribute;
    }
    
}
