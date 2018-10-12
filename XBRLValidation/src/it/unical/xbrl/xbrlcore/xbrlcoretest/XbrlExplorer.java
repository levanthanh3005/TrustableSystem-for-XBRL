/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.xbrlcoretest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceFactory;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceValidator;

/**
 *
 * @author Admin
 */
public class XbrlExplorer {

	// DTSFactory ivFactory = null;
	/**
	 * Creates a new instance of XbrlExplorer
	 */
	public XbrlExplorer() {
	}

	public int count = 0;

	public void scanInstance(File fn) {
		// Instance instance = new Instance(discoverableTaxonomySet)
		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToTranslator = (long) 0;
		Long timeToValidate = (long) 0;
		boolean isValidate = false;
		InstanceFactory f = new InstanceFactory();
		boolean stop = false;
		// load data
		startTime = System.currentTimeMillis();
		Instance i = null;
		try {
			i = f.createInstance(fn);
		} catch (Exception e) {
			System.err.println(fn.getName() + " " + e.toString());
			stop = true;
		}
		timeToLoad = System.currentTimeMillis() - startTime;
		// transform

		OntoDLVTransform dlvt = null;
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				dlvt = ExtrasStatus.getOntoDLVTransform(i);
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
				stop = true;
			}
			timeToTranslator = System.currentTimeMillis() - startTime;
		}
//		it.unical.xbrl.xbrlcore.DLVClass2Integer.DLVTransform dlvt = null;
//		if (!stop) {
//			startTime = System.currentTimeMillis();
//			try {
//				dlvt = new it.unical.xbrl.xbrlcore.DLVClass2Integer.DLVTransform(i);
//			} catch (Exception e) {
//				System.err.println(fn.getName() + " " + e.toString());
//				stop = true;
//			}
//			timeToTranslator = System.currentTimeMillis() - startTime;
//		}
		
		// validation
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				if (dlvt.execDLVValidator()) {
					// count++;
					isValidate = true;
					System.out.println("The document is valid");
				} else {
					System.out.println("The document is not valid : " + fn.getAbsolutePath());
					isValidate = false;
				}
				;
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
			}
			timeToValidate = System.currentTimeMillis() - startTime;
		}
		String expectedRs = "";
		boolean expectedValidation = true;
		if (fn.getName().substring(14, 16).equals("IC")) {
			expectedRs = "Incomplete";
			expectedValidation = false;
		} else if (fn.getName().substring(14, 15).equals("I")) {
			expectedRs = "Invalid";
			expectedValidation = false;
		} else {
			expectedRs = "correct";
			expectedValidation = true;
		}
		if (isValidate == expectedValidation) {
			count++;
		}
		// System.out.println(fn.getName().substring(14, 15));
		// System.out.println(fn.getName().substring(14, 16));
		// name,filesize, timeToLoad, timeToTranslator, timeToValidate, validatedRs,
		// expectedValidation, expectedRs
		ExtrasStatus.logIntoFileTime(fn.getName() + "," + fn.length() + "," + timeToLoad + "," + timeToTranslator + ","
				+ timeToValidate + "," + isValidate + "," + expectedValidation + "," + expectedRs + "\n");
		ExtrasStatus.logIntoFileResultCSV(fn.getName() + "," + fn.length() + "," + timeToLoad + "," + timeToTranslator + ","
				+ timeToValidate + "," + isValidate + "," + expectedValidation + "," + expectedRs + "\n");
	}

	public HashMap<String, Object> scanInstanceHashMap(File fn) {

		HashMap<String, Object> hrs = new HashMap<>();

		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToTranslator = (long) 0;
		Long timeToValidate = (long) 0;
		InstanceFactory f = new InstanceFactory();
		boolean stop = false;
		// load data
		startTime = System.currentTimeMillis();
		Instance i = null;
		try {
			i = f.createInstance(fn);
		} catch (Exception e) {
			System.err.println(fn.getName() + " " + e.toString());
			stop = true;
		}
		timeToLoad = System.currentTimeMillis() - startTime;
		// transform
		OntoDLVTransform dlvt = null;
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				dlvt = ExtrasStatus.getOntoDLVTransform(i);
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
				stop = true;
			}
			timeToTranslator = System.currentTimeMillis() - startTime;
		}
		HashMap<String, Object> dlvRs = new HashMap<>();
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				dlvRs = dlvt.execDLVValidatorHashMap();
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
			}
			timeToValidate = System.currentTimeMillis() - startTime;
		}

		String rsTime = "File size:" + fn.length() + " B </br>" + "Time to read data:" + timeToLoad + " ms </br>"
				+ "Time to translate to DLV:" + timeToTranslator + " ms </br>" + "Time to vaidate:" + timeToValidate
				+ " ms </br>";
		ExtrasStatus.log("Validate:" + dlvRs.get("validated"));
		hrs.put("validated", dlvRs.get("validated"));
		hrs.put("time", String.valueOf(rsTime));
		hrs.put("error", dlvRs.get("error"));
		String calFacts = (String) dlvRs.get("calFacts");
		// System.out.println("all fact:"+dlvt.getListOfFact().size());
		// System.out.println("pure fact:"+dlvt.getListOfPureFact(calFacts).size());
		hrs.put("facts", dlvt.getListOfFact());
		hrs.put("arcs", dlvRs.get("usedArc"));
		
		hrs.put("debtAndInterestInfo", dlvRs.get("debtAndInterestInfo"));
		hrs.put("debtDiff", dlvRs.get("debtDiff"));
		hrs.put("maxCalculatedThreshold", dlvRs.get("maxCalculatedThreshold"));
		hrs.put("rateRs", dlvRs.get("rateRs"));

		return hrs;
	}

	
	public void scanInstanceHashMapLogCSV(File fn) {

		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToTranslator = (long) 0;
		Long timeToValidate = (long) 0;
		InstanceFactory f = new InstanceFactory();
		boolean stop = false;
		// load data
		startTime = System.currentTimeMillis();
		Instance i = null;
		try {
			i = f.createInstance(fn);
		} catch (Exception e) {
			System.err.println(fn.getName() + " " + e.toString());
			stop = true;
		}
		timeToLoad = System.currentTimeMillis() - startTime;
		// transform
		OntoDLVTransform dlvt = null;
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				dlvt = ExtrasStatus.getOntoDLVTransform(i);
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
				stop = true;
			}
			timeToTranslator = System.currentTimeMillis() - startTime;
		}
		HashMap<String, Object> dlvRs = new HashMap<>();
		if (!stop) {
			startTime = System.currentTimeMillis();
			try {
				dlvRs = dlvt.execDLVValidatorHashMap();
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
			}
			timeToValidate = System.currentTimeMillis() - startTime;
		}

//		String rsTime = "File size:" + fn.length() + " B </br>" + "Time to read data:" + timeToLoad + " ms </br>"
//				+ "Time to translate to DLV:" + timeToTranslator + " ms </br>" + "Time to vaidate:" + timeToValidate
//				+ " ms </br>";
//		ExtrasStatus.log("Validate:" + dlvRs.get("validated"));
//		hrs.put("validated", dlvRs.get("validated"));
//		hrs.put("time", String.valueOf(rsTime));
//		hrs.put("error", dlvRs.get("error"));
//		String calFacts = (String) dlvRs.get("calFacts");
//		// System.out.println("all fact:"+dlvt.getListOfFact().size());
//		// System.out.println("pure fact:"+dlvt.getListOfPureFact(calFacts).size());
//		hrs.put("facts", dlvt.getListOfFact());
//		hrs.put("arcs", dlvRs.get("usedArc"));
		String isStringValidated = dlvRs.get("validated") == null ? "false" : dlvRs.get("validated").toString();
		boolean isValidate = Boolean.parseBoolean(isStringValidated);
		String expectedRs = "";
		boolean expectedValidation = true;
		if (fn.getName().substring(14, 16).equals("IC")) {
			expectedRs = "Incomplete";
			expectedValidation = false;
		} else if (fn.getName().substring(14, 15).equals("I")) {
			expectedRs = "Invalid";
			expectedValidation = false;
		} else {
			expectedRs = "correct";
			expectedValidation = true;
		}
		if (isValidate == expectedValidation) {
			count++;
		}
		Object objRs = dlvRs.get("error");
		String strRs = "None";
		if (objRs != null) {
			strRs = dlvRs.get("error").toString();
		}
		String companyName = dlvRs.get("company") == null ? "UNKNOWN" : dlvRs.get("company").toString();
		String debtAndInterestInfo = dlvRs.get("debtAndInterestInfo") == null ? "" : dlvRs.get("debtAndInterestInfo").toString();
		String debtDiff = dlvRs.get("debtDiff") == null ? "" : dlvRs.get("debtDiff").toString();
		String maxCalculatedThreshold = dlvRs.get("maxCalculatedThreshold") == null ? "" : dlvRs.get("maxCalculatedThreshold").toString();
		String rateRs = dlvRs.get("rateRs") == null ? "" : dlvRs.get("rateRs").toString();
		
		ExtrasStatus.logIntoFileResultCSV(
				fn.getName() + "|" 
				+ fn.length() + "|" 
				+ timeToLoad + "|" 
				+ timeToTranslator + "|"
				+ timeToValidate + "|" 
				+ isValidate + "|" 
				+ expectedValidation + "|" 
				+ expectedRs + "|" 
				+ strRs +"|" 
				+ maxCalculatedThreshold+ "|" 
				+ debtAndInterestInfo + "|"
				+ rateRs +"|"
				+ debtDiff +"|"
				+ "\n");
//		System.out.println(isValidate);
	}
	
	/**
	 * @param args
	 *            the command line argumenyts
	 */
	public static void main(String[] args) {
		System.out.println("Start");
		ExtrasStatus.setIsLinux(true);
		ExtrasStatus.isALL = true;
		
		ExtrasStatus.isIDLV = false;
		///media/lvtan/TOSHIBA EXT/dda
		File dirF = new File("/home/lvtan/git/XBRLValidator/XBRLFiles/");
		
		//File dirF = new File("/media/lvtan/TOSHIBA EXT/dda");
		
		File[] xbrlF = dirF.listFiles((dir, name) -> {
			return name.toLowerCase().endsWith(".xbrl");
		});
		XbrlExplorer lvEx = new XbrlExplorer();
		ExtrasStatus.printable = false;
		for (int i = 0;i< 1; i++) {
			System.out.println("Check:"+ i+" :" + xbrlF[i].getName());
			lvEx.scanInstanceHashMapLogCSV(xbrlF[i]);
		}
		System.out.println("Total :" + lvEx.count + " per " + xbrlF.length);
		
//		ExtrasStatus.setIsLinux(true);
//		
//		ExtrasStatus.isIDLV = true;
//		
//		XbrlExplorer lvEx = new XbrlExplorer();
//		ExtrasStatus.printable = false;
//		lvEx.scanInstanceHashMapLogCSV(new File("/home/lvtan/git/XBRLValidator/XBRLFiles/5243038060008.xbrl"));

		///////////// SINGLE//////////////////////////////////////////////////////////
//		 XbrlExplorer lvEx = new XbrlExplorer();
//		 ExtrasStatus.printable = false;
//		 // lvEx.scanInstance(new
//		 //
//		 //File("C:\\Users\\Admin\\git\\XBRLValidator\\XBRLFiles\\5014819290009.xbrl"));
//		 // lvEx.scanInstance(new
//		 // File("/home/lvtan/git/XBRLValidator/XBRLFiles/5244311490006.xbrl"));
//		// lvEx.scanInstanceHashMap(new
//		 //File("/home/lvtan/git/XBRLValidator/XBRLFiles/5244238390008.xbrl"));
//		// lvEx.scanInstanceHashMap(new
//		 //File("/home/lvtan/git/XBRLValidator/XBRLFiles/SmallVer_3708891630006.xbrl"));
//		 HashMap<String, Object> result = lvEx.scanInstanceHashMap(new
//		 File("/media/lvtan/TOSHIBA EXT/dda/5500499420020.xbrl"));
//		 System.out.println(result.get("error"));
////		 lvEx.scanInstance(new File("/home/lvtan/Desktop/IBMdata/ibm-20170930.xml"));
	}
}