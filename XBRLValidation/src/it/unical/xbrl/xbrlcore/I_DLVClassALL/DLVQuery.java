/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.I_DLVClassALL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
public class DLVQuery {

	private ArrayList<String> financialItemRow;
	private ArrayList<String> financialItemColumn;
	private ArrayList<FinancialItem> lsFI;
	private HashMap<String, HashMap<String, Object>> queryResult;

	public DLVQuery() {

	}

	public static String query = "%____QUERY\r\n" + 
			"\r\n" + 
			"financialitem(CY, CP, X) :- fact(CY, CP,CT1,X,U,GR), context(CY, CT1, S1, E1), &year(CY,CP,X,E1,\"2016\";\"True\").\r\n" + 
			"\r\n" + 
			"rateRelation (itcc_ci_totaledebiti, itcc_ci_proventionerifinanziariinteressialtrionerifinanziaritotaleinteressialtrionerifinanziari).\r\n" + 
			"\r\n" + 
			"contextRelation(CY, CT1, CT2) :- context(CY, CT1, S1, E1), context(CY, CT2, S2, E2), &afterDate(E1,S2;\"True\"), CT1<>CT2.\r\n" + 
			"\r\n" + 
			"rateContextRelation(CY, CT1,CT2) :- fact(CY, CP, CT1, _,U,GR), fact(CY, CP, CT2, _,U,GR), CT1 > CT2.\r\n" + 
			"\r\n" + 
			"checkRate(CY, CP1, CP2, CT1, CT2, V) :- fact(CY, CP1,CT1,X,U,GR), fact (CY, CP2,CT2,Y,U,GR), rateRelation (CP1, CP2), contextRelation(CY, CT1, CT2), &calculateRate(Y,X,\"100\";V) .\r\n" + 
			"\r\n" + 
			"diffRate(CY, CT1,CT2, RS,V) :- checkRate(CY, CP, CP1, CT1, CT11, V1), checkRate(CY, CP, CP1, CT2, CT21, V2), rateContextRelation(CY, CT1, CT2), rateContextRelation(CY, CT11, CT21), &calculateDiffRate(CY, CT1,CT2, V1,V2,\"0.5\";RS,V).\r\n" + 
			"\r\n" + 
			"%____end check rate\r\n" + 
			"choosePreArc(CY, CP, CP1, CT, CL, GR, M, X, Y) :- fact(CY, CP,CT,X,U,GR), fact (CY, CP1,CT,Y,U,GR), arc (CY, CP, CP1, M, CL) .\r\n" + 
			"chooseArc(CY, CP, CP1, CT, V , CL,GR,M) :- choosePreArc(CY, CP, CP1, CT, CL, GR, M, X, Y), &times(Y,M;V), &presumXBRL(CY, CP,CT,CL,GR,X,\"100\",V;SG) .\r\n" + 
			"\r\n" + 
			"checkFact(CY, CP,CT,CL,GR, TH) :- chooseArc(CY, CP, CP1, CT, V , CL,GR,M), &checkFact(CY, CP,CT,CL,GR; \"True\", TH).\r\n" + 
			"\r\n" + 
			"validDocument(CY, MTH) :- checkFact(CY, CP,CT,CL,GR, TH), &isValidatedXBRL(CY, \"0.5\";\"True\", MTH).\r\n" + 
			"nonValidDocument(CY, MTH) :- checkFact(CY, CP,CT,CL,GR, TH), &isValidatedXBRL(CY, \"0.5\";\"False\", MTH).";

	public void writeQuery() {
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(ExtrasStatus.dlvFilePath, ExtrasStatus.isALL));
			fw.write(query);
			fw.close();
		} catch (Exception e) {
			ExtrasStatus.log("writeFileCollection:" + e.toString());
		}
	}

	public void refresh() {
		financialItemRow = new ArrayList<>();
		financialItemColumn = new ArrayList<>();
		lsFI = new ArrayList<>();
	}

//	public void checkExistCompanyId(String companyId) {
//		// System.out.println("checkExistCompanyId:"+companyId+"<");
//		if (queryResult.containsKey(companyId)) {
//			return;
//		} else {
//			HashMap<String, Object> obj = new HashMap<>();
//			obj.put("error", "");
//			obj.put("usedArc", new ArrayList<DLVArc>());
//			obj.put("debtAndInterestInfo", "");
//			obj.put("validated", "false");
//			obj.put("maxCalculatedThreshold", "");
//			obj.put("debtDiff", "");
//			obj.put("rateRs", "");
//
//			queryResult.put(companyId, obj);
//		}
//	}

	
	
	public HashMap<String, HashMap<String, Object>> getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(HashMap<String, HashMap<String, Object>> queryResult) {
		this.queryResult = queryResult;
	}

	public HashMap<String, HashMap<String, Object>> execDLVValidatorHashMap() {
		// rs.put("validated", "false");

		// List<DLVArc> usedArcs = new ArrayList<DLVArc>();
		BufferedReader br = null;

		try {
			Runtime rt = Runtime.getRuntime();
			String commandLine = ExtrasStatus.idlvExecutePath + " " + ExtrasStatus.dlvFilePath + " "
					+ ExtrasStatus.idlvExecutePythonPath;
			ExtrasStatus.log(commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			br = new BufferedReader(isr);
		} catch (Exception t) {
			t.printStackTrace();
		}

		String linea = null;
		String scompanyId = "UNKOWN";

		while (true) {
			try {
				linea = br.readLine();
				// System.out.println(linea);
				if (linea == null) {
					// System.out.println("Run2");
					break;
				} else

				if (linea.contains("choosePreArc")) {
					ExtrasStatus.log("choosePreArc");
					// System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
					String[] lsElement = linea.split(",");
					if (lsElement.length == 9) {
						// System.out.println(">"+lsElement[0]+"<>"+lsElement[1]+"<>"+lsElement[3]+"<>"+lsElement[5]+"<");
						DLVArc dArc = new DLVArc();

						scompanyId = lsElement[0];
						dArc.setCompanyId(lsElement[0]);
						dArc.setFromId(lsElement[1]);
						dArc.setToId(lsElement[2]);
						dArc.setWeight(Float.parseFloat(lsElement[6].replace("\"", "")));
						dArc.setCalculationalLinkId(lsElement[4]);
						// System.out.println("1");
//						checkExistCompanyId(lsElement[0]);
						((ArrayList<DLVArc>) queryResult.get(lsElement[0]).get("arcs")).add(dArc);
					}
				} else

				if (linea.contains("checkFact")) {
					// conceptId;contextd;calculation link base;fact group : {'currentResult':
					// 49322.0, 'expectedResult': 16224.0, 'threshold': 2040}
					// System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
					// System.out.println(linea);
					
					String[] infor = linea.split(",");
					
					float threshold = Float.parseFloat(infor[5].replaceAll("\"", ""));
					
					if (threshold == 0) {
						continue;
					}

					String data = "{'conceptId':'" + infor[1] + "', 'contextId':'" + infor[2]
							+ "', 'calculationLinkbase':'" + infor[3] + "', 'factGroup':'" + infor[4] + "', threshold:" + infor[5]
							+ "},";
					// System.out.println("3");
					scompanyId = infor[0];
//					checkExistCompanyId(infor[0]);
					queryResult.get(infor[0]).put("error",
							queryResult.get(infor[0]).get("error").toString().concat(data));
				}

				else if (linea.contains("diffRate")) {
					// System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
					// System.out.println(linea);
					String[] infor = linea.split(",");

					scompanyId = infor[0].trim();
					// System.out.println("4");
//					checkExistCompanyId(scompanyId);

					String contextId1 = infor[1].trim();
					String contextId2 = infor[2].trim();
					Boolean rateRs = Boolean.valueOf(infor[3].trim().replaceAll("\"", ""));
					String diffRateValue = infor[4].trim().replaceAll("\"", "");
					// System.out.println("Company:"+scompanyId);
					// System.out.println(contextId1 + " "+ contextId2 + " "+ rateRs+"
					// "+diffRateValue);
					String debtAndInterestInfo = "{'contexts':'" + contextId1 + " vs " + contextId2 + "', 'accepted':'"
							+ rateRs + "', 'diff':'" + diffRateValue + "'},";
					// rs.put("debtDiff", diffRateValue);
					// rs.put("rateRs", rateRs);

					queryResult.get(scompanyId).put("debtDiff", diffRateValue);
					queryResult.get(scompanyId).put("rateRs", rateRs);
					queryResult.get(scompanyId).put("debtAndInterestInfo", queryResult.get(scompanyId)
							.get("debtAndInterestInfo").toString().concat(debtAndInterestInfo));

				} else if (linea.contains("financialitem")) {
					// System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));

					String[] infor = linea.split(",");

					if (infor.length == 3) {
						scompanyId = infor[1].trim();
						addFinancialItem(infor[0].trim(), infor[1].trim(), infor[2].trim());
					}
				} else if (linea.contains("nonValidDocument")) {
//					System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
//					System.out.println(linea);
					String[] infor = linea.split(",");

//					checkExistCompanyId(infor[0]);
					scompanyId = infor[0];
					queryResult.get(scompanyId).put("maxCalculatedThreshold", infor[1].replaceAll("\"", ""));
					queryResult.get(scompanyId).put("validated", "false");
				} else if (linea.contains("validDocument")) {
//					System.out.println(linea);
					linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
//					System.out.println(linea);
					String[] infor = linea.split(",");

//					checkExistCompanyId(infor[0]);
					scompanyId = infor[0];
					queryResult.get(scompanyId).put("maxCalculatedThreshold", infor[1].replaceAll("\"", ""));
					queryResult.get(scompanyId).put("validated", "true");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error at line:" + linea + " the last company:" + scompanyId);
			}
		}

		// rs.put("calFacts", traceCalFact);
		// rs.put("error", traceError);
		// rs.put("usedArc",usedArcs);
		// rs.put("debtAndInterestInfo",debtAndInterestInfo);
		return queryResult;
	}

	public void writeQueryResult() {
		ExtrasStatus.deleteDLVFileResultCSVPath();

		for (String companyId : queryResult.keySet()) {
			// System.out.println(companyId);
			ExtrasStatus.logIntoFileResultCSV(companyId + "|" + queryResult.get(companyId).get("validated") + "|"
			// + queryResult.get(companyId).get("error") + "|"
					+ queryResult.get(companyId).get("maxCalculatedThreshold") + "|"
					// + queryResult.get(companyId).get("debtAndInterestInfo") + "|"
					+ queryResult.get(companyId).get("debtDiff") + "|" 
					+ queryResult.get(companyId).get("rateRs") + "|"
					+ queryResult.get(companyId).get("expectedRs") + "|"
					+ queryResult.get(companyId).get("expectedValidation") + "|"
					+ "\n");
		}
	}

	private class FinancialItem {
		public int rowIndex;
		public int colIndex;
		public String value;
	}

	public void addFinancialItem(String companyId, String factId, String value) {
		if (!financialItemColumn.contains(factId)) {
			financialItemColumn.add(factId);
		}
		if (!financialItemRow.contains(companyId)) {
			financialItemRow.add(companyId);
		}
		FinancialItem fi = new FinancialItem();
		fi.colIndex = financialItemColumn.indexOf(factId);
		fi.rowIndex = financialItemRow.indexOf(companyId);
		fi.value = value;
		lsFI.add(fi);
	}

	public void printFinancialItem() {
		int maxCols = financialItemColumn.size();
		int maxRows = financialItemRow.size();
		System.out.println(maxCols + " " + maxRows);
		String[][] data = new String[maxRows][maxCols];
		for (int i = 0; i < maxRows; i++) {
			for (int j = 0; j < maxCols; j++) {
				data[i][j] = "";
			}
		}
		for (FinancialItem fi : lsFI) {
			data[fi.rowIndex][fi.colIndex] = fi.value;
		}
		try {
			String filePath = ExtrasStatus.prefixDLVToolPath + "FI.csv";
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

			writer.write(" |");
			for (int j = 0; j < maxCols; j++) {
				writer.write(financialItemColumn.get(j) + "|");
			}
			writer.write("\n");

			for (int i = 0; i < maxRows; i++) {
				writer.write(financialItemRow.get(i) + "|");
				for (int j = 0; j < maxCols; j++) {
					writer.write(data[i][j] + "|");
				}
				writer.write("\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
