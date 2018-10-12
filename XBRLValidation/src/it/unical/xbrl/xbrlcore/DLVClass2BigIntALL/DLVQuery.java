/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.DLVClass2BigIntALL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVArc;
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

	private HashMap<String, HashMap<String, Object>> queryResult;

	public DLVQuery() {

	}

	private String query = "%____QUERY\r\n"
			+ "chooseArc(CY, CP, CP1, CT, V , \"+\", CL,GR) :- fact(CY, CP,CT,X,U,FS1,GR), fact (CY, CP1,CT,Y,U,FS2,GR), arc (CY, CP, CP1, M, S, CL), *(Y,M,V), FS2=S.\r\n"
			+ "chooseArc(CY, CP, CP1, CT, V , \"-\", CL,GR) :- fact(CY, CP,CT,X,U,FS1,GR), fact (CY, CP1,CT,Y,U,FS2,GR), arc (CY, CP, CP1, M, S, CL), *(Y,M,V), FS2<>S.\r\n"
			+ "\r\n"
			+ "chooseArc(CY, CP, CP1, CT, 0 , \"-\", CL,GR) :- chooseArc(CY, CP, CP1, CT, V , \"+\", CL,GR), V >= 0 .\r\n"
			+ "chooseArc(CY, CP, CP1, CT, 0 , \"+\", CL,GR) :- chooseArc(CY, CP, CP1, CT, V , \"-\", CL,GR), V >= 0 .\r\n"
			+ "\r\n"
			+ "calSignedArc(CY, CP,CT,SG, S , CL,GR) :- chooseArc(CY, CP,_, CT, _ , S,CL,GR), #sum{ V,CP1 : chooseArc(CY, CP, CP1,CT, V , S , CL,GR) } = SG .\r\n"
			+ "\r\n"
			+ "nonValidFact(CY,CP,CT,X,U,GR) :- fact (CY, CP,CT,X,U,\"+\",GR), calSignedArc(CY,CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CY, CP,CT,SGN,\"-\",CL,GR), SGP < SGN.\r\n"
			+ "nonValidFact(CY,CP,CT,X,U,GR) :- fact (CY,CP,CT,X,U,\"-\",GR), calSignedArc(CY,CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CY,CP,CT,SGN,\"-\",CL,GR), SGP > SGN.\r\n"
			+ "\r\n"
			+ "calExpectedVFact(CY,CP,CT,CL,THO,\"+\",GR) :- fact (CY,CP,CT,X,U,\"+\",GR), calSignedArc(CY,CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CY,CP,CT,SGN,\"-\",CL,GR) , -(SGP,SGN,EX), #absdiff(X, EX, TH), *(TH,100,THO).\r\n"
			+ "calExpectedVFact(CY,CP,CT,CL,THO,\"-\",GR) :- fact (CY,CP,CT,X,U,\"-\",GR), calSignedArc(CY,CP,CT,SGP,\"+\",CL,GR) , calSignedArc(CY,CP,CT,SGN,\"-\",CL,GR) , -(SGN,SGP,EX), #absdiff(X, EX, TH), *(TH,100,THO).%, /(THO,X,TP).\r\n"
			+ "\r\n"
			+ "nonValidFact(CY,CP,CT,X,U,GR) :- fact (CY,CP,CT,X,U,FS,GR), calSignedArc(CY,CP,CT,SG, S , CL,GR), #count{SC : calExpectedVFact(CY,CP,CT,CL,_,SC,GR) } = 0.\r\n"
			+ "\r\n"
			+ "fcalExpectedVFact(CY,CP,CT,CL,THO,S,GR) :- fact (CY,CP,CT,X,U,S1,GR), calExpectedVFact(CY,CP,CT,CL,THO,S,GR), X = 0.\r\n"
			+ "fcalExpectedVFact(CY,CP,CT,CL,TP,S,GR) :- fact (CY,CP,CT,X,U,S1,GR), calExpectedVFact(CY,CP,CT,CL,THO,S,GR), X <> 0, /(THO,X,TP).\r\n"
			+ "\r\n" + "validFactAtCL(CY,CP,CT,CL,\"N\",GR) :- fcalExpectedVFact(CY,CP,CT,CL,TP,FS,GR), TP > 0.\r\n"
			+ "nonValidFact(CY,CP,CT,X,U,GR) :- fact (CY,CP,CT,X,U,FS,GR), validFactAtCL(CY,CP,CT,_,\"N\",GR) .\r\n"
			+ "validDocument(CY) :- fact(CY, _,_,_,_,_,_), #count{CP : nonValidFact(CY,CP,CT,X,U,GR)} = 0 .\r\n"
			+ "nonValidDocument(CY) :- fact(CY, _,_,_,_,_,_), #count{CP : nonValidFact(CY,CP,CT,X,U,GR)} > 0 .";

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
		queryResult = new HashMap<>();
	}
	
	public void checkExistCompanyId(String companyId) {
		// System.out.println("checkExistCompanyId:"+companyId+"<");
		if (queryResult.containsKey(companyId)) {
			return;
		} else {
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("validated", "false");
			queryResult.put(companyId, obj);
		}
	}
	
	public boolean execDLVValidatorHashMap() {
		// rs.put("validated", "false");

		// List<DLVArc> usedArcs = new ArrayList<DLVArc>();
		BufferedReader br = null;

		boolean runCorrect = true;
		;

		try {
			Runtime rt = Runtime.getRuntime();
			String commandLine = ExtrasStatus.prefixDLVToolPath + "dlv.x86-64-linux-elf-static.bin -silent "
					+ ExtrasStatus.dlvFilePath + " " + (ExtrasStatus.printable ? "" : " -nofacts");
			System.out.println(commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			br = new BufferedReader(isr);
		} catch (Exception t) {
			t.printStackTrace();
		}

		String linea = null;
		String scompanyId = "UNKOWN";
		int count = 0;
		try {
			linea = br.readLine();
			while (linea.contains("validDocument") && linea != null) {
				linea = linea.substring(linea.indexOf("validDocument(")+5);
				int start = linea.indexOf("(") + 1;
				int end = linea.indexOf(")");
//				System.out.println(linea.substring(start, end) + " "+start+" "+end);
				String companyId = linea.substring(start, end);
				checkExistCompanyId(companyId);
				queryResult.get(companyId).put("validated", "true");
				linea = linea.substring(end);
				count++;
			}
			System.out.println(count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// while (true) {
		// try {
		// linea = br.readLine();
		// if (linea == null) {
		// // System.out.println("Run2");
		// break;
		// } else
		//
		// if (linea.contains("nonValidDocument")) {
		// System.out.println(linea);
		// linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
		//// System.out.println(linea);
		// String[] infor = linea.split(",");
		// scompanyId = infor[0];
		// queryResult.get(scompanyId).put("validated", "false");
		// } else if (linea.contains("validDocument")) {
		// System.out.println(linea);
		// linea = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
		//// System.out.println(linea);
		// String[] infor = linea.split(",");
		// scompanyId = infor[0];
		// queryResult.get(scompanyId).put("validated", "true");
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.err.println("Error at line:" + linea + " the last company:" +
		// scompanyId);
		// runCorrect = false;
		// }
		// }

		// rs.put("calFacts", traceCalFact);
		// rs.put("error", traceError);
		// rs.put("usedArc",usedArcs);
		// rs.put("debtAndInterestInfo",debtAndInterestInfo);
		return runCorrect;
	}

	public void writeQueryResult() {
		System.out.println("writeQueryResult");
		ExtrasStatus.deleteDLVFileResultCSVPath();

		for (String companyId : queryResult.keySet()) {
			System.out.println(companyId);
			ExtrasStatus
					.logIntoFileResultCSV(companyId + "|" + queryResult.get(companyId).get("validated") + "|" + "\n");
		}
	}
}
