/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.xbrlcoretest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVArc;
import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVQuery;
import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVQuery;
import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceFactory;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceValidator;

/**
 *
 * @author Admin
 */
public class XbrlExplorer2 {

	// DTSFactory ivFactory = null;
	/**
	 * Creates a new instance of XbrlExplorer
	 */
	public XbrlExplorer2() {
	}

	public List readXBRL(File fn) {
		List listOfFact = null;
		InstanceFactory f = new InstanceFactory();
		boolean stop = false;

		Instance i = null;
		try {
			i = f.createInstance(fn);
		} catch (Exception e) {
			System.err.println(fn.getName() + " " + e.toString());
			stop = true;
		}

		// transform
		OntoDLVTransform dlvt = null;
		if (!stop) {
			try {
				dlvt = ExtrasStatus.getOntoDLVTransform(i);
				listOfFact = dlvt.getListOfFact();
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
				stop = true;
			}
		}
		return listOfFact;
	}

	public void scanFolder(String path) {
		System.out.println("Start");
		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToTranslator = (long) 0;
		Long timeToValidate = (long) 0;
		
		ExtrasStatus.setIsLinux(true);
		ExtrasStatus.isIDLV = true;
		ExtrasStatus.isALL = true;
		ExtrasStatus.printable = false;
		ExtrasStatus.deleteDLVFile();

		File dirF = new File(path);

		File[] xbrlF = dirF.listFiles((dir, name) -> {
			return name.toLowerCase().endsWith(".xbrl");
		});
		startTime = System.currentTimeMillis();

		HashMap<String, HashMap<String, Object>> queryResult = new HashMap<>();
		
		for (int index = 0; index < xbrlF.length; index++) {
			// System.out.println(i);
			ExtrasStatus.currentCompanyId = "company"+String.valueOf(index);
			List listOfFact = readXBRL(xbrlF[index]);
			
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("error", "");
			obj.put("arcs", new ArrayList<DLVArc>());
			obj.put("debtAndInterestInfo", "");
			obj.put("validated", "false");
			obj.put("maxCalculatedThreshold", "");
			obj.put("debtDiff", "");
			obj.put("rateRs", "");
			obj.put("facts", listOfFact);
			
			String expectedRs;
			boolean expectedValidation;
			if (xbrlF[index].getName().substring(14, 16).equals("IC")) {
				obj.put("facts", listOfFact);
				expectedRs = "Incomplete";
				expectedValidation = false;
			} else if (xbrlF[index].getName().substring(14, 15).equals("I")) {
				expectedRs = "Invalid";
				expectedValidation = false;
			} else {
				expectedRs = "correct";
				expectedValidation = true;
			}
			
			obj.put("expectedRs", expectedRs);
			obj.put("expectedValidation", expectedValidation);
			
			queryResult.put(ExtrasStatus.currentCompanyId, obj);
			
		}
		
		timeToLoad = System.currentTimeMillis() - startTime;

		System.out.println("End write");
		startTime = System.currentTimeMillis();

		DLVQuery dq = new DLVQuery();
		dq.writeQuery();

		System.out.println("Exe cmd");

		System.out.println("Run----");
		dq.refresh();
		dq.setQueryResult(queryResult);

		dq.execDLVValidatorHashMap();
		timeToValidate = System.currentTimeMillis() - startTime;
		System.out.println("timeToLoad:"+timeToLoad+" timeToValidate:"+ timeToValidate);

		dq.writeQueryResult();
		dq.printFinancialItem();
		System.out.println("Finish");
	}

	public HashMap<String, Object> scanInstanceHashMap(String fileList) {

		HashMap<String, Object> hrs = new HashMap<>();

		ExtrasStatus.setIsLinux(true);
		ExtrasStatus.isIDLV = true;
		ExtrasStatus.isALL = true;
		ExtrasStatus.printable = false;
		ExtrasStatus.isBasicResult = true;
		ExtrasStatus.deleteDLVFile();
		
		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToValidate = (long) 0;
		InstanceFactory f = new InstanceFactory();
		boolean stop = false;
		// load data
		startTime = System.currentTimeMillis();
		Instance i = null;
		
		String flist[] = fileList.split(",");
		
		HashMap<String, HashMap<String, Object>> queryResult = new HashMap<>();
		
		for (int index = 0; index < flist.length; index++) {
			// System.out.println(i);
			ExtrasStatus.currentCompanyId = "company"+String.valueOf(index);
			List listOfFact = readXBRL(new File(flist[index]));
			
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("error", "");
			obj.put("arcs", new ArrayList<DLVArc>());
			obj.put("debtAndInterestInfo", "");
			obj.put("validated", "false");
			obj.put("maxCalculatedThreshold", "");
			obj.put("debtDiff", "");
			obj.put("rateRs", "");
			obj.put("facts", listOfFact);

			queryResult.put(ExtrasStatus.currentCompanyId, obj);
			
		}
		timeToLoad = System.currentTimeMillis() - startTime;

		System.out.println("End write");
		startTime = System.currentTimeMillis();

		DLVQuery dq = new DLVQuery();
		dq.writeQuery();

		System.out.println("Exe cmd");

		System.out.println("Run----");
		dq.refresh();
		
		dq.setQueryResult(queryResult);
		dq.execDLVValidatorHashMap();
		
		timeToValidate = System.currentTimeMillis() - startTime;
		System.out.println("timeToLoad:"+timeToLoad+" timeToValidate:"+ timeToValidate);
		
		hrs.put("timeToLoad", String.valueOf(timeToLoad));
		hrs.put("timeToValidate", String.valueOf(timeToValidate));
		
//		if (ExtrasStatus.isBasicResult) {
//			for (String companyId : queryResult.keySet()) {
//				queryResult.get(companyId).remove("usedArc");
//				queryResult.get(companyId).remove("error");
//			}
//		}
		
		hrs.put("queryResult", queryResult);


		return hrs;
	}
	
	/**
	 * @param args
	 *            the command line argumenyts
	 */
	public static void main(String[] args) {

		XbrlExplorer2 lvEx = new XbrlExplorer2();
		lvEx.scanFolder("/home/lvtan/git/XBRLValidator/XBRLFiles/");
	}
}