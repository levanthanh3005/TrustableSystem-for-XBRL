/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Admin
 */
public class OntoFact extends ObjectEsd {

    private OntoContext contextRef;
    private int decimals;
    private OntoUnit unitRef;
    private OntoConcept conceptRef;
    private BigDecimal value; //skip all fact that does not contain number

    @Override
    public String getContext() {
//        System.out.println("Fact context:");
//        System.out.println(getName());
//        System.out.println(getValue().toString());
//        System.out.println(contextRef.getIdStr());
//        System.out.println(getDecimals());
//        System.out.println(getUnitRef().getIdStr());
        try {
            return " : fact (concept:\"" + getConceptRef().getIdDLVStr() + "\", value :\"" + getValue() + "\" ,contextRef:\"" + contextRef.getIdDLVStr() + "\" , " + " decimals: \"" + getDecimals() + "\" , " + " unitRef: \"" + getUnitRef().getIdDLVStr() + "\").";
        } catch (NullPointerException e) {
            return ": fact NULL";
        }
    }

    @Override
    public String getClassType() {
        return "Fact";
    }

    public OntoContext getContextRef() {
        return contextRef;
    }

    public void setContextRef(OntoContext contextRef) {
        this.contextRef = contextRef;
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

    public OntoUnit getUnitRef() {
        return unitRef;
    }

    public void setUnitRef(OntoUnit unitRef) {
        this.unitRef = unitRef;
    }

    public OntoConcept getConceptRef() {
        return conceptRef;
    }

    public void setConceptRef(OntoConcept conceptRef) {
        this.conceptRef = conceptRef;
    }

    public String getValue() {
        return value.toBigInteger().toString();
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
            value = new BigDecimal(ff).setScale(decimals, RoundingMode.HALF_EVEN);

//            value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        } catch (Exception e) {
            System.out.println("setValue errror:" + tvalue);
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
