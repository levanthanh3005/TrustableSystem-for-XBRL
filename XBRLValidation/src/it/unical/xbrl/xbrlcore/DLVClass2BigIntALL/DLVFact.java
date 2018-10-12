/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.DLVClass2BigIntALL;

import it.unical.xbrl.xbrlcore.OntoClass.*;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 *
 * @author Admin
 */
public class DLVFact extends ObjectEsd {

    private String companyId;

    private String contextRef;
    private String unitRef;
    private String conceptRef;
    private int decimals;
    private BigDecimal value; //skip all fact that does not contain number
    private String sign;
    private String factGroupId;

    @Override
    public String getContext() {
//        System.out.println("Fact context:");
//        System.out.println(getName());
//        System.out.println(getValue().toString());
//        System.out.println(contextRef.getIdStr());
//        System.out.println(getDecimals());
//        System.out.println(getUnitRef().getIdStr());
        try {
            return "fact (" + ExtrasStatus.normalize(ExtrasStatus.currentCompanyId) + ", " + conceptRef + "," + contextRef + "," + getValue() + "," + unitRef + ",\"" + sign + "\"," + factGroupId + ").";
        } catch (NullPointerException e) {
            return "fact NULL";
        }
    }

    public void setCompanyId(String companyId) {
        this.companyId = "company" + ExtrasStatus.normalize(ExtrasStatus.currentCompanyId);
    }
    
    @Override
    public String getClassType() {
        return "Fact";
    }

    public String getContextRef() {
        return contextRef;
    }

    public void setContextRef(String contextRef) {
        this.contextRef = "context" + ExtrasStatus.normalize(contextRef);
    }

    public String getFactGroupId() {
        return factGroupId;
    }

    public void setFactGroupId(String factGroupId) {
        if (factGroupId == null) {
            factGroupId = "Global";
        }
        this.factGroupId = "fgroup" + ExtrasStatus.normalize(factGroupId);
    }

    public String getUnitRef() {
        return unitRef;
    }

    public void setUnitRef(String unitRef) {
        this.unitRef = unitRef.toLowerCase().replace("-", "");
    }

    public String getConceptRef() {
        return conceptRef;
    }

    public void setConceptRef(String conceptRef) {
        this.conceptRef = ExtrasStatus.normalize(conceptRef);
    }

    public String getValue() {
        return value.toBigInteger().toString();
    }

    public BigInteger getValueBigInteger() {
        return value.toBigInteger();
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public void setDecimals(String tdecimals) {
        try {
            decimals = Integer.parseInt(tdecimals);
        } catch (Exception e) {
            decimals = 2;
        }
        setDecimals(decimals);
    }

    public boolean setValue(String tvalue) {
        float ff;
        try {
            ff = new Float(tvalue).floatValue();
        } catch (Exception e) {
            return false;
        }
        try {
            value = new BigDecimal(.0F);
            value = new BigDecimal(Math.abs(ff)).setScale(decimals, RoundingMode.HALF_EVEN);
            if (ff >= 0) {
                this.sign = "+";
            } else {
                this.sign = "-";
            }
//            value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        } catch (Exception e) {
            System.out.println("fact : setValue errror:" + tvalue);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
/*
able 2: Regulator specified accuracy
Description of accuracy of amount	Attribute and attribute value to be used
exact monetary, percentage, basis point or any other amount	decimals="INF"
rounded or truncated to millions	decimals="-6"
rounded or truncated to thousands	decimals="-3"
rounded or truncated to units	decimals="0"
rounded or truncated to cents	decimals="2"
rounded or truncated to a whole percentage	decimals="2"
rounded or truncated to basis points	decimals="4"
 */
