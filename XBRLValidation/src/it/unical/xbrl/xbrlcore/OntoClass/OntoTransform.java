/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.OntoClass;

import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Fact;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceContext;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceUnit;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.Concept;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.DiscoverableTaxonomySet;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.TaxonomySchema;

/**
 *
 * @author Admin
 */
public class OntoTransform {

    private Instance instance;
    private ArrayList<ObjectSd> lsDLVObjectSd;

    public OntoTransform(Instance instance) {
        this.instance = instance;
        lsDLVObjectSd = new ArrayList<>();
        readContext();
        readConcept();
        readUnit();
        readFact();
        readArc();
        printCollection();
    }

    public void readUnit() {
        Map unitMap = instance.getUnitMap();
        Iterator unitInterator = unitMap.keySet().iterator();
        while (unitInterator.hasNext()) {
            String unitId = (String) unitInterator.next();
            InstanceUnit unit = instance.getUnit(unitId);
            OntoUnit dUnit = new OntoUnit();
            dUnit.setId(unitId);
            dUnit.setMeasure(unit.getValue());
            dUnit.setNamespace(unit.getNamespaceURI());
            addToCollectDLVObjectSd(dUnit);
        }
    }

    public void readContext() {
        Map contextMap = instance.getContextMap();
        Iterator contextIterator = contextMap.keySet().iterator();
        while (contextIterator.hasNext()) {
            String currID = (String) contextIterator.next();
            InstanceContext currCtx = (InstanceContext) contextMap.get(currID);
            OntoContext dContext = new OntoContext();
            dContext.setId(currID);

            //set Entity
            OntoEntity dEntity = new OntoEntity();
            dEntity.setIdentifier(currCtx.getIdentifier());
            dEntity.setIdentifierScheme(currCtx.getIdentifierScheme());
            addToCollectDLVObjectSd(dEntity);
            dContext.setEntity(dEntity);

            //set Period
            OntoPeriod dPeriod = new OntoPeriod();
            OntoDate startDate = new OntoDate();
            OntoDate endDate = new OntoDate();
            if (currCtx.getPeriodStartDate() == null || currCtx.getPeriodEndDate() == null) {
                startDate.setDate(currCtx.getPeriodValue());
                endDate.setDate(currCtx.getPeriodValue());
            } else {
                startDate.setDate(currCtx.getPeriodStartDate());
                endDate.setDate(currCtx.getPeriodEndDate());
            }
            addToCollectDLVObjectSd(startDate);
            addToCollectDLVObjectSd(endDate);
            dPeriod.setStarting(startDate);
            dPeriod.setEnding(endDate);
            addToCollectDLVObjectSd(dPeriod);

            dContext.setPeriod(dPeriod);
            addToCollectDLVObjectSd(dContext);
        }
    }

    public void readConcept() {
        ArrayList<Concept> lsConcept = new ArrayList<>();
        Iterator dtsSetIterator = instance.getDiscoverableTaxonomySet().iterator();
        while (dtsSetIterator.hasNext()) {
//            LogStatus.log("Scan----"+dtsSetIterator.toString());
            DiscoverableTaxonomySet currDts = (DiscoverableTaxonomySet) dtsSetIterator
                    .next();
            Map taxonomyMap = currDts.getTaxonomyMap();
            Iterator taxonomyEntrySetIterator = taxonomyMap.entrySet()
                    .iterator();
            while (taxonomyEntrySetIterator.hasNext()) {
                Map.Entry currEntry = (Map.Entry) taxonomyEntrySetIterator
                        .next();
                TaxonomySchema currTaxonomy = (TaxonomySchema) currEntry
                        .getValue();
//                LogStatus.log("currTaxonomy.getNamespace():" + currTaxonomy.getNamespace());
                lsConcept.addAll(currTaxonomy.getConcepts());
            }
        }
        for (Concept c : lsConcept) {
            OntoConcept dc = new OntoConcept(c.getId(), c.getName(), c.getNamespace().getPrefix(), c.getNamespace().getURI());
            addToCollectDLVObjectSd(dc);
        }
    }

    public void readFact() {
//        System.out.println("Read Fact");
//        ArrayList<Fact> lsFact = new ArrayList<>();
//        lsFact.add((Fact) getFactSet());
        List factSet = instance.getFactSet();
        Iterator factSetIterator = factSet.iterator();
        while (factSetIterator.hasNext()) {
            try {
//                System.out.println("Pass 0:");
                Fact fact = (Fact) factSetIterator.next();
//                System.out.println("Pass 1:" + fact.getConcept().getId());
                OntoFact dFact = new OntoFact();
//                System.out.println("Pass 2");
                dFact.setContextRef((OntoContext) findDLVObjectEsdById("context", fact.getContextRef()));
//                System.out.println("Pass 3");
                dFact.setDecimals(fact.getDecimals());
                if (!dFact.setValue(fact.getValue())) {
                    continue;
                }
//                System.out.println("Pass 4:" + fact.getInstanceUnit());
//                System.out.println("Pass 4:" + fact.getInstanceUnit().getId());
                try {
                    dFact.setUnitRef((OntoUnit) findDLVObjectEsdById("unit", fact.getInstanceUnit().getId()));
                } catch (Exception e) {
                    return;
                }
                dFact.setConceptRef((OntoConcept) findDLVObjectEsdById("concept", fact.getConcept().getId()));
                addToCollectDLVObjectSd(dFact);
//                System.out.println("dFact:" + dFact.getContext());
//                System.out.println("    Fact:" + fact.getConcept().getId());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    public void readArc(){
        /*
        The idea is that:
        CalculationalLink will contain a list of CalculationalArc
        CalculationalArc : class (from : content, to : content, weight : Integer)
        =>>> another way:
        CalculationalLink : class (extendedLink : String)
        CalculationalArc : class (from : content, to : content, weight : Integer, calLink : CalculationalLink)
        */
    }

    public void addToCollectDLVObjectSd(ObjectSd osd) {
        long lastId = -1;
        for (ObjectSd os : lsDLVObjectSd) {
            if (os.getContext().equals(osd.getContext())) {
                osd.setIdDLV(os.getIdDLV());
                return;
            } else if (osd.getClassType().equals(os.getClassType())) {
                lastId = os.getIdDLV();
            }
        }

        osd.setIdDLV(lastId + 1);
        lsDLVObjectSd.add(osd);
    }

    public void printCollection() {
        for (ObjectSd osd : lsDLVObjectSd) {
            System.out.println(osd.getString());
        }
    }

    public ObjectSd findDLVObjectEsdById(String className, String id) {
        System.out.println("findDLVObjectEsdById:" + className + " " + id);
        for (ObjectSd osd : lsDLVObjectSd) {
            if (osd.getClassType().equals(className)) {
                if (osd.getId() != null && osd.getId().equals(id)) {
                    return osd;
                }
            }
        }
        System.out.println("Unit nullk");
        return null;
    }
}
