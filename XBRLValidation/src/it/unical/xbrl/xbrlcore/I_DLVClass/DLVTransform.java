/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.I_DLVClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.unical.xbrl.xbrlcore.OntoClass.ObjectSd;
import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Fact;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceContext;
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
public class DLVTransform implements OntoDLVTransform {

	private Instance instance;
	private ArrayList<ObjectSd> lsDLVObjectSd;
	
	private static final String CONCEPTFORCOMPANYNAME = "itcc-ci_DatiAnagraficiDenominazione";
	private String companyName = "UNKNOWN";
	private String contextQuery= "";

	public DLVTransform(Instance instance) {
		this.instance = instance;
		lsDLVObjectSd = new ArrayList<>();

		readFact();
		readArc();
		readContext();
		// checkFactGroup();
		writeFileCollection();
		// execDLVValidator();
	}
	public void readContext() {
		//System.out.println("Read context");
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd");
        
		for (Object obj1 : instance.getContextMap().entrySet()) {
			Map.Entry<String, Object> entry1 = (Entry<String, Object>) obj1;
			InstanceContext context1 = (InstanceContext)entry1.getValue();
			String strStartDate1 = context1.getPeriodStartDate();
			String strEndDate1 = context1.getPeriodEndDate();
			if(strStartDate1 == null && context1.getPeriodValue() != null) {
				strStartDate1 = context1.getPeriodValue();
				strEndDate1 = context1.getPeriodValue();
			};

//			System.out.println(">>>"+entry1.getKey()+" start1:"+strStartDate1 + " endDate1:"+strEndDate1);
			Date startDate1 = null;
			Date endDate1 = null;
			try {
				startDate1 = formatter.parse(strStartDate1);
				endDate1 = formatter.parse(strEndDate1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
			if (startDate1 == null || endDate1 == null) {
				continue;
			}
			
			for (Object obj2 : instance.getContextMap().entrySet()) {
				Map.Entry<String, Object> entry2 = (Entry<String, Object>) obj2;
				if (entry2.getKey() == entry1.getKey()) {
					continue;
				}
				
				InstanceContext context2 = (InstanceContext)entry2.getValue();
				String strStartDate2 = context2.getPeriodStartDate();
				String strEndDate2 = context2.getPeriodEndDate();
				if(strStartDate2 == null && context2.getPeriodValue() != null) {
					strStartDate2 = context2.getPeriodValue();
					strEndDate2 = context2.getPeriodValue();
				};
				
				try {
					if (formatter.parse(strStartDate2).after(startDate1)) {
						if (!formatter.parse(strStartDate2).before(endDate1)) {
//							System.out.println(entry2.getKey()+" start2:"+strStartDate2 + " endDate2:"+strEndDate2);
							contextQuery = contextQuery + "contextRelation( context"
									+ ExtrasStatus.normalize(entry2.getKey())+", context"
									+ ExtrasStatus.normalize(entry1.getKey()) + ").\n";
						}
					}
				}catch (Exception e) {
					continue;
				}
			}
		}
//		System.out.println(contextQuery);
	}
	public void readFact() {
		// ExtrasStatus.log("Read Fact");
		// ArrayList<Fact> lsFact = new ArrayList<>();
		// lsFact.add((Fact) getFactSet());
		List factSet = instance.getFactSet();
		Iterator factSetIterator = factSet.iterator();
		ExtrasStatus.log("read Fact : " + factSet.size());
		while (factSetIterator.hasNext()) {
			try {
				// ExtrasStatus.log("Pass 0:");
				Fact fact = (Fact) factSetIterator.next();
				ExtrasStatus.log("Concept fact :" + fact.getConcept().getId());
				
				if (fact.getConcept().getId().compareTo(CONCEPTFORCOMPANYNAME) == 0) {
					companyName = fact.getValue();
				}
				
				DLVFact dFact = new DLVFact();
				// ExtrasStatus.log("Pass 2");
				dFact.setContextRef(fact.getContextRef());
				// ExtrasStatus.log("Pass 3");
				dFact.setDecimals(fact.getDecimals());
				if (!dFact.setValue(fact.getValue())) {
					// ExtrasStatus.log("#### can not get value of
					// fact:"+fact.getConcept().getId());
					continue;
				}
				// ExtrasStatus.log("Pass 4:" + fact.getInstanceUnit());
				// ExtrasStatus.log("Pass 4:" + fact.getInstanceUnit().getId());
				try {
					dFact.setUnitRef(fact.getInstanceUnit().getId());
				} catch (Exception e) {
					// ExtrasStatus.log("#### can not get unit of fact
					// :"+fact.getConcept().getId());
					continue;
				}
				dFact.setFactGroupId(fact.getFactGroupId());
				dFact.setConceptRef(fact.getConcept().getId());
				addToCollectDLVObjectSd(dFact);
				// ExtrasStatus.log(dFact.getContext());
				// ExtrasStatus.log(" Fact:" + fact.getConcept().getId());
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	public void readArc() {
		/*
		 * The idea is that: CalculationalLink will contain a list of CalculationalArc
		 * CalculationalArc : class (from : content, to : content, weight : Integer)
		 * =>>> another way: CalculationalLink : class (extendedLink : String)
		 * CalculationalArc : class (from : content, to : content, weight : Integer,
		 * calLink : CalculationalLink)
		 */
		Iterator dtsSetIteratorForContext = instance.getDiscoverableTaxonomySet().iterator();
		while (dtsSetIteratorForContext.hasNext()) {
			// LogStatus.log("Scan----"+dtsSetIterator.toString());
			DiscoverableTaxonomySet currDts = (DiscoverableTaxonomySet) dtsSetIteratorForContext.next();
			Map taxonomyMap = currDts.getTaxonomyMap();
			Iterator taxonomyEntrySetIterator = taxonomyMap.entrySet().iterator();
			while (taxonomyEntrySetIterator.hasNext()) {
				Map.Entry currEntryIter = (Map.Entry) taxonomyEntrySetIterator.next();
				TaxonomySchema currTaxonomy = (TaxonomySchema) currEntryIter.getValue();
				// LogStatus.log("currTaxonomy.getNamespace():" + currTaxonomy.getNamespace());
				Set<Concept> lsTmpConcept = currTaxonomy.getConcepts();
				for (Concept tmpConceptIter : lsTmpConcept) {
					// ExtrasStatus.log("tmpConcept:" + tmpConceptIter.getId());
					Set dtsSet = instance.getDiscoverableTaxonomySet();
					DiscoverableTaxonomySet currDTS = null;
					Iterator dtsSetIterator = dtsSet.iterator();
					while (dtsSetIterator.hasNext()) {
						DiscoverableTaxonomySet tmpDTS = (DiscoverableTaxonomySet) dtsSetIterator.next();
						if (tmpDTS.getConceptByID(tmpConceptIter.getId()) != null) {
							currDTS = tmpDTS;
							// LogStatus.log(" set currDTS:" + currDTS.getTopTaxonomy().getName());
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
					 * check for every extended link role whether there are calculation rules
					 * defined
					 */
					Set extendedLinkRoleSet = currDTS.getCalculationLinkbase().getExtendedLinkRoles();
					Iterator extendedLinkRoleSetIterator = extendedLinkRoleSet.iterator();
					while (extendedLinkRoleSetIterator.hasNext()) {
						String currExtendedLinkRole = (String) extendedLinkRoleSetIterator.next();
						// Map calculationRules;
						// try {
						// calculationRules = currDTS.getCalculationLinkbase()
						// .getCalculations(tmpConceptIter, currExtendedLinkRole);
						// } catch (Exception e) {
						// continue;
						// }
						//// ExtrasStatus.log(" currExtendedLinkRole:" + currExtendedLinkRole + "
						// calculationRules.size():" + calculationRules.size());
						// if (calculationRules.size() > 0) {
						// /* calculate currentResult */
						// Set calculationRulesEntrySet = calculationRules.entrySet();
						// Iterator calculationRulesIterator = calculationRulesEntrySet
						// .iterator();
						//
						// while (calculationRulesIterator.hasNext()) {
						// Map.Entry currEntry = (Map.Entry) calculationRulesIterator
						// .next();
						// Concept tmpConcept = (Concept) currEntry.getKey();
						// float currWeight = ((Float) currEntry.getValue()).floatValue();
						//// ExtrasStatus.log("->>>" + tmpConcept.getId() + " " + currWeight + " " +
						// currExtendedLinkRole);
						// DLVArc dArc = new DLVArc();
						// dArc.setCalculationalLinkId(currExtendedLinkRole);
						// dArc.setFromId(tmpConceptIter.getId());
						// dArc.setToId(tmpConcept.getId());
						// dArc.setWeight(currWeight);
						// ExtrasStatus.log(dArc.getContext());
						// addToCollectDLVObjectSd(dArc);
						// }
						//
						// }
						List arcRules;
						try {
							arcRules = currDTS.getCalculationLinkbase().getCalculationArcs(tmpConceptIter,
									currExtendedLinkRole);
						} catch (Exception e) {
							continue;
						}
						// ExtrasStatus.log(" currExtendedLinkRole:" + currExtendedLinkRole + "
						// calculationRules.size():" + calculationRules.size());
						if (arcRules.size() > 0) {
							/* calculate currentResult */
							Iterator arcRulesIterator = arcRules.iterator();

							while (arcRulesIterator.hasNext()) {
								Arc currArc = (Arc) arcRulesIterator.next();
								Concept tmpConcept = ((Locator) currArc.getTargetElement()).getConcept();
								float currWeight = currArc.getWeightAttribute();
								// ExtrasStatus.log("->>>" + tmpConcept.getId() + " " + currWeight + " " +
								// currExtendedLinkRole);
								DLVArc dArc = new DLVArc();
								dArc.setCalculationalLinkId(currExtendedLinkRole);
								dArc.setFromId(tmpConceptIter.getId());
								dArc.setToId(tmpConcept.getId());
								dArc.setWeight(currWeight);
								ExtrasStatus.log(dArc.getContext());
								addToCollectDLVObjectSd(dArc);
							}

						}
					}
				}
			}
		}
	}

	// public void checkFactGroup() {
	// for (ObjectSd osF : lsDLVObjectSd) {
	// if (!osF.getClassType().equals("Fact")) {
	// continue;
	// }
	// DLVFact currDFact = (DLVFact) osF;
	// if (currDFact.getFactGroupId() == null) {
	// continue;
	// }
	// for (ObjectSd osA : lsDLVObjectSd) {
	// if (!osA.getClassType().equals("Arc")) {
	// continue;
	// }
	// DLVArc currDArc = (DLVArc) osA;
	// if (
	// //currDArc.getFromId().equals(currDFact.getConceptRef()) ||
	// currDArc.getToId().equals(currDFact.getConceptRef())) {
	// currDArc.setCalculationalLinkId(currDFact.getFactGroupId());
	// }
	// }
	// }
	// }
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
	
	public static String query = "rateRelation (itcc_ci_TotaleDebiti, itcc_ci_ProventiOneriFinanziariInteressiAltriOneriFinanziariTotaleInteressiAltriOneriFinanziari).\r\n" + 
			"\r\n" + 
			"%____QUERY\r\n" + 
			"rateContextRelation(CT1,CT2) :- fact(CP, CT1, _,U,GR), fact(CP, CT2, _,U,GR), CT1 > CT2.\r\n" + 
			"\r\n" + 
			"checkRate(CP1, CP2, CT1, CT2, V) :- fact(CP1,CT1,X,U,GR), fact (CP2,CT2,Y,U,GR), rateRelation (CP1, CP2), contextRelation(CT1, CT2), &calculateRate(Y,X,\"100\";V) .\r\n" + 
			"\r\n" + 
			"diffRate(CT1,CT2, RS,V) :- checkRate(CP, CP1, CT1, CT11, V1), checkRate(CP, CP1, CT2, CT21, V2), rateContextRelation(CT1, CT2), rateContextRelation(CT11, CT21), &calculateDiffRate(V1,V2,\"0.5\";RS,V).\r\n" + 
			"\r\n" + 
			"%____end check rate\r\n" + 
			"choosePreArc(CP, CP1, CT, CL, GR, M, X, Y) :- fact(CP,CT,X,U,GR), fact (CP1,CT,Y,U,GR), arc (CP, CP1, M, CL) .\r\n" + 
			"chooseArc(CP, CP1, CT, V , CL,GR,M) :- choosePreArc(CP, CP1, CT, CL, GR, M, X, Y), &times(Y,M;V), &presumXBRL(CP,CT,CL,GR,X,\"100\",V;SG) .\r\n" + 
			"validDocument :- chooseArc(CP, CP1, CT, V , CL,GR,M),&isValidatedXBRL(\"0.5\";\"True\").\r\n" + 
			"nonValidDocument :- chooseArc(CP, CP1, CT, V , CL,GR,M),&isValidatedXBRL(\"0.5\";\"False\").";

	public void writeFileCollection() {
		query = contextQuery + query;
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
			String commandLine = ExtrasStatus.idlvExecutePath + " " + ExtrasStatus.dlvFilePath + " "
					+ ExtrasStatus.idlvExecutePythonPath;
			ExtrasStatus.log(commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String linea = null;

			while (true) {
				linea = br.readLine();
				ExtrasStatus.logIntoFileTime(linea + "\n");
				if (linea == null) {
					break;
				}
				if (linea.contains("nonValidDocument")) {
					return false;
				}
				if (linea.contains("validDocument")) {
					return true;
				}
			}

			return false;
		} catch (Exception t) {
			t.printStackTrace();
		}
		return false;
	}

	public HashMap<String, Object> execDLVValidatorHashMap() {
		HashMap<String, Object> rs = new HashMap<>();
		rs.put("company",companyName);
		rs.put("validated", "false");
		
		List<DLVArc> usedArcs = new ArrayList<DLVArc>();
		
		try {
			Runtime rt = Runtime.getRuntime();
			String commandLine = ExtrasStatus.idlvExecutePath + " " + ExtrasStatus.dlvFilePath + " "
					+ ExtrasStatus.idlvExecutePythonPath;
			ExtrasStatus.log(commandLine);
			Process proc = rt.exec(commandLine);
//			System.out.println(proc.waitFor());

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
//			BufferedReader stdError = new BufferedReader(new 
//	                 InputStreamReader(proc.getErrorStream()));
//            
//            // read any errors from the attempted command
//            System.out.println("Here is the standard error of the command (if any):\n");
//            
//            while (true) {
//            	String serror = stdError.readLine();
//            	System.out.println(serror);
//            	if (serror == null) {
//            		break;
//            	}
//            }
	            
			String linea = null;
			String traceError = "";
			String traceCalFact = "";
			String debtAndInterestInfo = "";
			boolean tracable = false;
//			System.out.println("Run1");
			while (true) {
				linea = br.readLine();
				ExtrasStatus.logIntoFileTime(linea + "\n");
//				System.out.println(linea);
				if (linea == null) {
//					System.out.println("Run2");
					break;
				}

				if (linea.contains("choosePreArc")) {
					ExtrasStatus.log("choosePreArc");
//					System.out.println(linea);
					linea = linea.substring(linea.indexOf("(")+1,linea.indexOf(")"));
					String[] lsElement = linea.split(",");
					if (lsElement.length==8) {
						ExtrasStatus.log(">"+lsElement[0]+"<>"+lsElement[1]+"<>"+lsElement[3]+"<>"+lsElement[5]+"<");
						DLVArc dArc = new DLVArc();
						dArc.setFromId(lsElement[0]);
						dArc.setToId(lsElement[1]);
						dArc.setWeight(Float.parseFloat(lsElement[5].replace("\"", "")));
						dArc.setCalculationalLinkId(lsElement[3]);
						usedArcs.add(dArc);
					}
				}
				
//				if (linea.contains("$endValidation$")) {
//					if (traceError.length() > 1) {
//						traceError = traceError.substring(0, traceError.length() - 1);
//					}
//					ExtrasStatus.log("Trace:");
//					ExtrasStatus.log(traceError);
//					tracable = false;
//				}

				if (linea.contains("$maxThreshold:")) {
					rs.put("maxCalculatedThreshold", linea.replace("$maxThreshold:","").trim());
				}
				if (linea.contains("$error:")) {
					// conceptId;contextd;calculation link base;fact group : {'currentResult':
					// 49322.0, 'expectedResult': 16224.0, 'threshold': 2040}
					ExtrasStatus.log(linea);
					linea = linea.replace("$error:","");
					String[] strls = linea.split("#");
					// ExtrasStatus.log(strls.length);
					if (strls.length != 2) {
						traceCalFact = traceCalFact.concat(linea + "#");
						continue;
					}
					String[] infor = strls[0].split(";");
					String evaluate = strls[1].replaceAll(" ", "");

					ExtrasStatus.log(evaluate);

					String data = "{'conceptId':'" + infor[0] + "', 'contextId':'" + infor[1]
							+ "', 'calculationLinkbase':'" + infor[2] + "', 'factGroup':'" + infor[3] + "', " + evaluate
							+ "},";
					traceError = traceError.concat(data);
				}
				
//				if (linea.contains("$validating$")) {
//					tracable = true;
//				}
				if (linea.contains("diffRate")) {
					ExtrasStatus.log(linea);
					String contextId1 = linea.substring(linea.indexOf("(")+1, linea.indexOf(","));
					String contextId2 = linea.substring(linea.indexOf(",")+1, linea.indexOf(",", linea.indexOf(",")+1));
					Boolean rateRs = Boolean.valueOf(linea.substring(linea.indexOf(",\"")+2, linea.lastIndexOf("\",\"")));
					String diffRateValue = linea.substring(linea.lastIndexOf("\",\"")+3, linea.indexOf("\")"));
//					System.out.println("Company:"+companyName);
//					System.out.println(contextId1 + " "+ contextId2 + " "+ rateRs+" "+diffRateValue);
					debtAndInterestInfo = debtAndInterestInfo + "{'contexts':'"+contextId1+" vs "+contextId2+"', 'accepted':'"+rateRs+"', 'diff':'"+diffRateValue+"'},";
					rs.put("debtDiff", diffRateValue);
					rs.put("rateRs", rateRs);
				}
				
				if (linea.contains("nonValidDocument")) {
					rs.put("validated", "false");
					rs.put("error", traceError);
//					System.out.println("NBOTValid");
//					rs.put("usedArc",usedArcs);
//					return rs;
				}
				if (linea.contains("validDocument")) {
					rs.put("validated", "true");
//					System.out.println("Valid");
//					rs.put("calFacts", traceCalFact);
//					rs.put("usedArc",usedArcs);
//					return rs;
				}
			}

//			rs.put("validated", "false");
			rs.put("calFacts", traceCalFact);
			rs.put("error", traceError);
			rs.put("usedArc",usedArcs);
			rs.put("debtAndInterestInfo",debtAndInterestInfo);
//			System.out.println("Run3");
			return rs;
		} catch (Exception t) {
			t.printStackTrace();
		}
		System.out.println("Run4");
		return rs;
	}

	@Override
	public List getListOfFact() {
		// TODO Auto-generated method stub
		List result = new ArrayList<DLVFact>();
		for (ObjectSd osd : lsDLVObjectSd) {
			if (osd.getClassType().equals("Fact")) {
				result.add((DLVFact) osd);
			}
		}
		return result;
	}
	
	@Override
	public List getListOfArc() {
		// TODO Auto-generated method stub
		List result = new ArrayList<DLVArc>();
		for (ObjectSd osd : lsDLVObjectSd) {
			if (osd.getClassType().equals("Arc")) {
				result.add((DLVArc) osd);
			}
		}
		return result;
	}
	
	@Override
	public List getListOfPureFact(String traceCalFact) {
		ExtrasStatus.log("getListOfPureFact");
		List result = new ArrayList<DLVFact>();
		if (traceCalFact == null) {
			return result;
		}

		String[] lsF = traceCalFact.split("#");

		for (ObjectSd osd : lsDLVObjectSd) {

			if (osd.getClassType().equals("Fact")) {
				DLVFact dfact = (DLVFact) osd;
				boolean check = true;
				for (String fs : lsF) {
//					System.out.println(fs);
					String[] lsE = fs.split(";");

					if (dfact.getConceptRef().equals(lsE[0])) {
						if (dfact.getContextRef().equals(lsE[1])) {
							if (dfact.getFactGroupId().equals(lsE[3])) {
								check = false;
							}
						}
					}
				}
				if (check) {
					result.add(dfact);
				}
			}

		}

		return result;
	}
}
