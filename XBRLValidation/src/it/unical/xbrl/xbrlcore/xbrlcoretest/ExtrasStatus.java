/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.xbrl.xbrlcore.xbrlcoretest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import it.unical.xbrl.xbrlcore.OntoClass.OntoDLVTransform;
import it.unical.xbrl.xbrlcore.xbrlcore.instance.Instance;

/**
 *
 * @author Admin
 */
public class ExtrasStatus {

	public static int ERROR = 1;
	public static boolean printable = true;

	public static boolean isLinux = false;

	public static boolean isIDLV = true;

	public static boolean isALL = false;
	
	public static boolean isBasicResult = false;

	public static String prefixDLVToolPath = "";

	public static String logFilePath = prefixDLVToolPath + "logFile.log";
	public static String logFileTimePath = prefixDLVToolPath + "logFileTime.log";
	public static String logFileResultCSVPath = prefixDLVToolPath + "logFileResult.csv";
	public static String dlvFilePath = prefixDLVToolPath + "xbrlExport.dlv";
	public static String transactionsFilePath = prefixDLVToolPath + "transactions.dlv";

	public static String dlvExecutePath = prefixDLVToolPath + "DLV";
	public static String dlvComplexExecutePath = prefixDLVToolPath + "DLV";

	public static String idlvExecutePath = prefixDLVToolPath + "idlv";
	public static String idlvExecutePythonPath = prefixDLVToolPath + "calculation.py";

	public static String XBRLDirPath = "/home/lvtan/git/XBRLValidator/XBRLFiles";

	public static String currentCompanyId = "UNKNOWN";
	// public static boolean appendToDLVFile = true;

	public static void setIsLinux(boolean isLinux) {
		ExtrasStatus.isLinux = isLinux;
		if (!isLinux) {
			prefixDLVToolPath = "C:\\Users\\Admin\\git\\XBRLValidator\\DLVTools\\";
			updateAllPath();
		} else {
			prefixDLVToolPath = "/home/lvtan/git/XBRLValidator/DLVTools/";
			updateAllPath();
			ExtrasStatus.idlvExecutePath = prefixDLVToolPath + "idlv";
			ExtrasStatus.idlvExecutePythonPath = prefixDLVToolPath + "calculation.py";
			ExtrasStatus.dlvExecutePath = prefixDLVToolPath + "dlv.x86-64-linux-elf-static.bin";
		}
	}

	public static void updateAllPath() {
		ExtrasStatus.logFilePath = prefixDLVToolPath + "logFile.log";
		ExtrasStatus.logFileTimePath = prefixDLVToolPath + "logFileTime.log";
		ExtrasStatus.dlvFilePath = prefixDLVToolPath + "xbrlExport.dlv";
		ExtrasStatus.transactionsFilePath = prefixDLVToolPath + "transactions.dlv";

		ExtrasStatus.logFileResultCSVPath = prefixDLVToolPath + "logFileResult.csv";

		ExtrasStatus.dlvExecutePath = prefixDLVToolPath + "DLV";
		ExtrasStatus.dlvComplexExecutePath = prefixDLVToolPath + "dl-complex.win.20110713.beta";
	}

	public static OntoDLVTransform getOntoDLVTransform(Instance i) {
		if (isIDLV) {
			if (isALL) {
				return new it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVTransform(i);
			} else {
				return new it.unical.xbrl.xbrlcore.I_DLVClass.DLVTransform(i);
			}
		} else {
			if (isALL) {
				return new it.unical.xbrl.xbrlcore.DLVClass2BigIntALL.DLVTransform(i);
			} else {
				return new it.unical.xbrl.xbrlcore.DLVClass2BigInt.DLVTransform(i);
			}
		}
	}

	public static void log(String text) {
		if (printable) {
			System.out.println(text);
		}
	}

	public static void print(String text) {
		if (printable) {
			System.out.println(text);
		}
	}

	// public static void log(String text, int status) {
	// if (status == ERROR) {
	// System.err.println(text);
	// }
	// }
	public static void log(Exception e) {
		if (printable) {
			e.printStackTrace();
		}
	}

	public static String normalize(String text) {
		text = text.replace("://", "_");
		text = text.replace("/", "_");
		text = text.replace(":", "_");
		text = text.replace(".", "_");
		text = text.replace("-", "_");
		return text.toLowerCase();
	}

	public static void logIntoFile(String text) {
		if (printable) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(logFilePath));
				writer.write(text);
				writer.close();
			} catch (Exception e) {
				log(e);
				return;
			}
		}
	}

	public static void logIntoFileTime(String text) {
		BufferedWriter writer = null;
		if (!ExtrasStatus.printable) {
			return;
		}
		try {
			writer = new BufferedWriter(new FileWriter(logFileTimePath, true));
			writer.write(text);
			writer.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			return;
		}
	}

	public static void logIntoFileResultCSV(String text) {
		// System.out.println("logIntoFileResultCSV:"+logFileResultCSVPath);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(logFileResultCSVPath, true));
			writer.write(text);
			writer.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			return;
		}
	}

	public static boolean execArelleValiate(File fn) {
		try {
			Runtime rt = Runtime.getRuntime();
			// String commandLine
			// = xbrlValidationCommandLineBeforeInstanceDocument + " \"" + xbrlFileName +
			// "\" " + xbrlValidationCommandLineAfterInstanceDocument;
			String arellePath = "C:\\Program Files\\Arelle\\arelleCmdLine.exe ";
			String commandLine = arellePath + " -f " + fn.getAbsolutePath() + " -v ";
			ExtrasStatus.log("commandLine:" + commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String linea = null;
			linea = br.readLine();
			System.out.println(
					"Arelle :" + linea.substring(linea.indexOf("in ") + "in ".length(), linea.indexOf(" secs")));
			linea = br.readLine();
			System.out.println(
					"Arelle :" + linea.substring(linea.indexOf("in ") + "in ".length(), linea.indexOf(" secs")));
		} catch (Exception t) {
			t.printStackTrace();
		}
		return false;
	}

	public static void deleteDLVFile() {
		try {
			System.out.println("Delete file");
			File fn = new File(ExtrasStatus.dlvFilePath);
			fn.delete();
		} catch (Exception e) {
			ExtrasStatus.log("writeFileCollection:" + e.toString());
		}
	}

	public static void deleteDLVFileResultCSVPath() {
		try {
			System.out.println("Delete file");
			File fn = new File(ExtrasStatus.logFileResultCSVPath);
			fn.delete();
		} catch (Exception e) {
			ExtrasStatus.log("writeFileCollection:" + e.toString());
		}
	}
}
/*
 * Issues: XBRL : SetFacts: Fact with no context, no unit... : this maybe the
 * fact of information Fact is a group of fact Footnotelink 2 fact similar
 */
