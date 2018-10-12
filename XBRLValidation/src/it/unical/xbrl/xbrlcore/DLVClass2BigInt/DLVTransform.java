/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.DLVClass2BigInt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unical.xbrl.xbrlcore.OntoClass.ObjectSd;
import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Fact;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.Concept;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.DiscoverableTaxonomySet;
import it.unical.xbrl.xbrlcore.xbrlcore.taxonomy.TaxonomySchema;
import it.unical.xbrl.xbrlcore.xbrlcore.xlink.Arc;
import it.unical.xbrl.xbrlcore.xbrlcore.xlink.Locator;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

/**
 *
 * @author Admin
 */
public class DLVTransform implements OntoDLVTransform{

    private Instance instance;
    private ArrayList<ObjectSd> lsDLVObjectSd;

    public DLVTransform(Instance instance) {
        this.instance = instance;
        lsDLVObjectSd = new ArrayList<>();

        readFact();
        readArc();
        //checkFactGroup();
        writeFileCollection();
//        execDLVValidator();
    }

    public void readFact() {
//        ExtrasStatus.log("Read Fact");
//        ArrayList<Fact> lsFact = new ArrayList<>();
//        lsFact.add((Fact) getFactSet());
        List factSet = instance.getFactSet();
        Iterator factSetIterator = factSet.iterator();
        ExtrasStatus.log("read Fact : " + factSet.size());
        while (factSetIterator.hasNext()) {
            try {
//                ExtrasStatus.log("Pass 0:");
                Fact fact = (Fact) factSetIterator.next();
                ExtrasStatus.log("Concept fact :" + fact.getConcept().getId());
                DLVFact dFact = new DLVFact();
//                ExtrasStatus.log("Pass 2");
                dFact.setContextRef(fact.getContextRef());
//                ExtrasStatus.log("Pass 3");
                dFact.setDecimals(fact.getDecimals());
                if (!dFact.setValue(fact.getValue())) {
//                    ExtrasStatus.log("#### can not get value of fact:"+fact.getConcept().getId());
                    continue;
                }
//                ExtrasStatus.log("Pass 4:" + fact.getInstanceUnit());
//                ExtrasStatus.log("Pass 4:" + fact.getInstanceUnit().getId());
                try {
                    dFact.setUnitRef(fact.getInstanceUnit().getId());
                } catch (Exception e) {
//                    ExtrasStatus.log("#### can not get unit of fact :"+fact.getConcept().getId());
                    continue;
                }
                dFact.setFactGroupId(fact.getFactGroupId());
                dFact.setConceptRef(fact.getConcept().getId());
                addToCollectDLVObjectSd(dFact);
//                ExtrasStatus.log(dFact.getContext());
//                ExtrasStatus.log("    Fact:" + fact.getConcept().getId());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void readArc() {
        /*
        The idea is that:
        CalculationalLink will contain a list of CalculationalArc
        CalculationalArc : class (from : content, to : content, weight : Integer)
        =>>> another way:
        CalculationalLink : class (extendedLink : String)
        CalculationalArc : class (from : content, to : content, weight : Integer, calLink : CalculationalLink)
         */
        Iterator dtsSetIteratorForContext = instance.getDiscoverableTaxonomySet().iterator();
        while (dtsSetIteratorForContext.hasNext()) {
//            LogStatus.log("Scan----"+dtsSetIterator.toString());
            DiscoverableTaxonomySet currDts = (DiscoverableTaxonomySet) dtsSetIteratorForContext
                    .next();
            Map taxonomyMap = currDts.getTaxonomyMap();
            Iterator taxonomyEntrySetIterator = taxonomyMap.entrySet()
                    .iterator();
            while (taxonomyEntrySetIterator.hasNext()) {
                Map.Entry currEntryIter = (Map.Entry) taxonomyEntrySetIterator
                        .next();
                TaxonomySchema currTaxonomy = (TaxonomySchema) currEntryIter
                        .getValue();
//                LogStatus.log("currTaxonomy.getNamespace():" + currTaxonomy.getNamespace());
                Set<Concept> lsTmpConcept = currTaxonomy.getConcepts();
                for (Concept tmpConceptIter : lsTmpConcept) {
//                    ExtrasStatus.log("tmpConcept:" + tmpConceptIter.getId());
                    Set dtsSet = instance.getDiscoverableTaxonomySet();
                    DiscoverableTaxonomySet currDTS = null;
                    Iterator dtsSetIterator = dtsSet.iterator();
                    while (dtsSetIterator.hasNext()) {
                        DiscoverableTaxonomySet tmpDTS = (DiscoverableTaxonomySet) dtsSetIterator
                                .next();
                        if (tmpDTS.getConceptByID(tmpConceptIter.getId()) != null) {
                            currDTS = tmpDTS;
//                            LogStatus.log("  set currDTS:" + currDTS.getTopTaxonomy().getName());
                            break;
                        }
                    }
                    if (currDTS == null) {
                        continue;
                    }
                    if (currDTS.getCalculationLinkbase() == null) {
                        /* no calculations defined */
                        continue;
                    }
                    /*
         * check for every extended link role whether there are calculation
         * rules defined
                     */
                    Set extendedLinkRoleSet = currDTS.getCalculationLinkbase()
                            .getExtendedLinkRoles();
                    Iterator extendedLinkRoleSetIterator = extendedLinkRoleSet.iterator();
                    while (extendedLinkRoleSetIterator.hasNext()) {
                        String currExtendedLinkRole = (String) extendedLinkRoleSetIterator
                                .next();
//                        Map calculationRules;
//                        try {
//                            calculationRules = currDTS.getCalculationLinkbase()
//                                    .getCalculations(tmpConceptIter, currExtendedLinkRole);
//                        } catch (Exception e) {
//                            continue;
//                        }
////                        ExtrasStatus.log("  currExtendedLinkRole:" + currExtendedLinkRole + "  calculationRules.size():" + calculationRules.size());
//                        if (calculationRules.size() > 0) {
//                            /* calculate currentResult */
//                            Set calculationRulesEntrySet = calculationRules.entrySet();
//                            Iterator calculationRulesIterator = calculationRulesEntrySet
//                                    .iterator();
//
//                            while (calculationRulesIterator.hasNext()) {
//                                Map.Entry currEntry = (Map.Entry) calculationRulesIterator
//                                        .next();
//                                Concept tmpConcept = (Concept) currEntry.getKey();
//                                float currWeight = ((Float) currEntry.getValue()).floatValue();
////                                ExtrasStatus.log("->>>" + tmpConcept.getId() + " " + currWeight + " " + currExtendedLinkRole);
//                                DLVArc dArc = new DLVArc();
//                                dArc.setCalculationalLinkId(currExtendedLinkRole);
//                                dArc.setFromId(tmpConceptIter.getId());
//                                dArc.setToId(tmpConcept.getId());
//                                dArc.setWeight(currWeight);
//                                ExtrasStatus.log(dArc.getContext());
//                                addToCollectDLVObjectSd(dArc);
//                            }
//
//                        }
                        List arcRules;
                        try {
                            arcRules = currDTS.getCalculationLinkbase()
                                    .getCalculationArcs(tmpConceptIter, currExtendedLinkRole);
                        } catch (Exception e) {
                            continue;
                        }
//                        ExtrasStatus.log("  currExtendedLinkRole:" + currExtendedLinkRole + "  calculationRules.size():" + calculationRules.size());
                        if (arcRules.size() > 0) {
                            /* calculate currentResult */
                            Iterator arcRulesIterator = arcRules.iterator();

                            while (arcRulesIterator.hasNext()) {
                                Arc currArc = (Arc) arcRulesIterator.next();
                                Concept tmpConcept = ((Locator) currArc.getTargetElement()).getConcept();
                                float currWeight = currArc.getWeightAttribute();
//                                ExtrasStatus.log("->>>" + tmpConcept.getId() + " " + currWeight + " " + currExtendedLinkRole);
                                DLVArc dArc = new DLVArc();
                                dArc.setCalculationalLinkId(currExtendedLinkRole);
                                dArc.setFromId(tmpConceptIter.getId());
                                dArc.setToId(tmpConcept.getId());
                                dArc.setWeight(currWeight);
                                dArc.setUseAttribute(currArc.getUseAttribute());
                                ExtrasStatus.log(dArc.getContext());
                                addToCollectDLVObjectSd(dArc);
                            }

                        }
                    }
                }
            }
        }
    }

//    public void checkFactGroup() {
//        for (ObjectSd osF : lsDLVObjectSd) {
//            if (!osF.getClassType().equals("Fact")) {
//                continue;
//            }
//            DLVFact currDFact = (DLVFact) osF;
//            if (currDFact.getFactGroupId() == null) {
//                continue;
//            }
//            for (ObjectSd osA : lsDLVObjectSd) {
//                if (!osA.getClassType().equals("Arc")) {
//                    continue;
//                }
//                DLVArc currDArc = (DLVArc) osA;
//                if (
//                        //currDArc.getFromId().equals(currDFact.getConceptRef()) ||
//                        currDArc.getToId().equals(currDFact.getConceptRef())) {
//                    currDArc.setCalculationalLinkId(currDFact.getFactGroupId());
//                }
//            }
//        }
//    }
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

    private String query = "%____QUERY\r\n" + 
    		"chooseArc(CP, CP1, CT, V , \"+\", CL,GR) :- fact(CP,CT,X,U,FS1,GR), fact (CP1,CT,Y,U,FS2,GR), arc (CP, CP1, M, S, CL), *(Y,M,V), FS2=S.\r\n" + 
    		"chooseArc(CP, CP1, CT, V , \"-\", CL,GR) :- fact(CP,CT,X,U,FS1,GR), fact (CP1,CT,Y,U,FS2,GR), arc (CP, CP1, M, S, CL), *(Y,M,V), FS2<>S.\r\n" + 
    		"\r\n" + 
    		"chooseArc(CP, CP1, CT, 0 , \"-\", CL,GR) :- chooseArc(CP, CP1, CT, V , \"+\", CL,GR), V >= 0 .\r\n" + 
    		"chooseArc(CP, CP1, CT, 0 , \"+\", CL,GR) :- chooseArc(CP, CP1, CT, V , \"-\", CL,GR), V >= 0 .\r\n" + 
    		"\r\n" + 
    		"\r\n" + 
    		"calSignedArc(CP,CT,SG, S , CL,GR) :- chooseArc(CP,_, CT, _ , S,CL,GR), #sum{ V,CP1 : chooseArc(CP, CP1,CT, V , S , CL,GR) } = SG .\r\n" + 
    		"\r\n" + 
    		"nonValidFact(CP,CT,X,U,GR) :- fact (CP,CT,X,U,\"+\",GR), calSignedArc(CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CP,CT,SGN,\"-\",CL,GR), SGP < SGN.\r\n" + 
    		"nonValidFact(CP,CT,X,U,GR) :- fact (CP,CT,X,U,\"-\",GR), calSignedArc(CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CP,CT,SGN,\"-\",CL,GR), SGP > SGN.\r\n" + 
    		"\r\n" + 
    		"calExpectedVFact(CP,CT,CL,THO,\"+\",GR) :- fact (CP,CT,X,U,\"+\",GR), calSignedArc(CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CP,CT,SGN,\"-\",CL,GR) , -(SGP,SGN,EX), #absdiff(X, EX, TH), *(TH,100,THO).\r\n" + 
    		"calExpectedVFact(CP,CT,CL,THO,\"-\",GR) :- fact (CP,CT,X,U,\"-\",GR), calSignedArc(CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CP,CT,SGN,\"-\",CL,GR) , -(SGN,SGP,EX), #absdiff(X, EX, TH), *(TH,100,THO).%, /(THO,X,TP).\r\n" + 
    		"\r\n" + 
    		"nonValidFact(CP,CT,X,U,GR) :- fact (CP,CT,X,U,FS,GR), calSignedArc(CP,CT,SG, S , CL,GR), #count{SC : calExpectedVFact(CP,CT,CL,_,SC,GR) } = 0.\r\n" + 
    		"\r\n" + 
    		"fcalExpectedVFact(CP,CT,CL,THO,S,GR) :- fact (CP,CT,X,U,S1,GR), calExpectedVFact(CP,CT,CL,THO,S,GR), X = 0.\r\n" + 
    		"fcalExpectedVFact(CP,CT,CL,TP,S,GR) :- fact (CP,CT,X,U,S1,GR), calExpectedVFact(CP,CT,CL,THO,S,GR), X <> 0, /(THO,X,TP).\r\n" + 
    		"\r\n" + 
    		"validFactAtCL(CP,CT,CL,\"N\",GR) :- fcalExpectedVFact(CP,CT,CL,TP,FS,GR), TP > 0.\r\n" + 
    		"nonValidFact(CP,CT,X,U,GR) :- fact (CP,CT,X,U,FS,GR), validFactAtCL(CP,CT,_,\"N\",GR) .\r\n" + 
    		"validDocument :- #count{CP : nonValidFact(CP,CT,X,U,GR)} = 0 .\r\n" + 
    		"nonValidDocument :- #count{CP : nonValidFact(CP,CT,X,U,GR)} > 0 .";

    public void writeFileCollection() {
        try {
            FileWriter fw = new FileWriter(new File(ExtrasStatus.dlvFilePath));
            for (ObjectSd osd : lsDLVObjectSd) {
                fw.write(osd.getContext() + "\n");
            }
            fw.write(query);
            fw.close();
        } catch (Exception e) {
            ExtrasStatus.log("writeFileCollection:" + e.toString());
        }
    }

    public boolean execDLVValidator() {
        try {
            Runtime rt = Runtime.getRuntime();
            String commandLine = ExtrasStatus.prefixDLVToolPath+"dlv.x86-64-linux-elf-static.bin -silent "+ ExtrasStatus.dlvFilePath+" " + (ExtrasStatus.printable ? "" : " -nofacts");
            System.out.println(commandLine);
            Process proc = rt.exec(commandLine);

            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String linea = null;
            linea = br.readLine();
            ExtrasStatus.logIntoFile(linea);
            if (linea == null || !linea.contains("validDocument}")) {
//                ExtrasStatus.log("The document is not valid");
//                throw new Exception("Not valid");
                return false;
            } else {
//                ExtrasStatus.log("The document is valid");
//                System.out.println("The document is valid");
                return true;
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
        return false;
    }

	@Override
	public List getListOfFact() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List getListOfPureFact(String traceCalFact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListOfArc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> execDLVValidatorHashMap() {
		// TODO Auto-generated method stub
		HashMap<String, Object> rs = new HashMap<>();
		rs.put("validated", execDLVValidator());
		return rs;
	}


}
