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

import it.unical.xbrl.xbrlcore.DLVClass2BigIntALL.DLVQuery;
import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceFactory;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.InstanceValidator;

/**
 *
 * @author Admin
 */
public class XbrlExplorer3 {

	// DTSFactory ivFactory = null;
	/**
	 * Creates a new instance of XbrlExplorer
	 */
	public XbrlExplorer3() {
	}

	public void readXBRL(File fn) {
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
			} catch (Exception e) {
				System.err.println(fn.getName() + " " + e.toString());
				stop = true;
			}
		}
	}

	public void scanFolder(String path) {
		System.out.println("Start");
		Long startTime;
		Long timeToLoad = (long) 0;
		Long timeToTranslator = (long) 0;
		Long timeToValidate = (long) 0;
		
		ExtrasStatus.setIsLinux(true);
		ExtrasStatus.isIDLV = false;
		ExtrasStatus.isALL = true;
		ExtrasStatus.printable = false;
		ExtrasStatus.deleteDLVFile();

		File dirF = new File(path);

		File[] xbrlF = dirF.listFiles((dir, name) -> {
			return name.toLowerCase().endsWith(".xbrl");
		});
		startTime = System.currentTimeMillis();

		for (int i = 0; i < xbrlF.length; i++) {
			// System.out.println(i);
			ExtrasStatus.currentCompanyId = "company" + String.valueOf(i);
			readXBRL(xbrlF[i]);
		}
		timeToLoad = System.currentTimeMillis() - startTime;

		System.out.println("End write");
		startTime = System.currentTimeMillis();

		DLVQuery dq = new DLVQuery();
		dq.writeQuery();

		System.out.println("Exe cmd");

		System.out.println("Run----");
		dq.refresh();
		
		dq.execDLVValidatorHashMap();
		timeToValidate = System.currentTimeMillis() - startTime;
		System.out.println("timeToLoad:"+timeToLoad+" timeToValidate:"+ timeToValidate);
		
		dq.writeQueryResult();
		System.out.println("Finish");
	}

	/**
	 * @param args
	 *            the command line argumenyts
	 */
	public static void main(String[] args) {

		XbrlExplorer3 lvEx = new XbrlExplorer3();
		System.out.println("DLV and Big number");
		lvEx.scanFolder("/home/lvtan/git/XBRLValidator/XBRLFiles/");
	}
}