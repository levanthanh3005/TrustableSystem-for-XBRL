/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.blockchain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;

@Service

public class TransactionService {
	@PostConstruct
	public void initialization() {
		ExtrasStatus.setIsLinux(false);
		ExtrasStatus.printable = false;
	}

	public void addTx(Transaction tx) {
		appendDLVFile(tx);
	}

	public void resetDLVFile() {
		if (!ExtrasStatus.isLinux) {
			try {
				FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath);
				fw.write("#include \"math_R.dll\"\r\n" + "#include \"ListAndSet.20100303.dll\"\r\n");// appends the
																										// string
																										// to the file
				fw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		} else {
			try {
				FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath); // to the file
				fw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void appendDLVFile(Transaction tx) {
		if (!ExtrasStatus.isLinux) {
			try {
				FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath, true); // the true will append the new
																							// data
				fw.write("tx(\"" + tx.getId() + "\", \"" + tx.getFrom() + "\", \"" + tx.getTo() + "\", " + tx.getValue()
						+ ").\n");// appends the string to the file
				System.out.println("Write done");
				fw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		} else {
			try {
				FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath, true); // the true will append the new
																							// data
				fw.write("tx(\"" + tx.getId() + "\", \"" + tx.getFrom() + "\", \"" + tx.getTo() + "\", \"" + tx.getValue()
						+ "\").\n");// appends the string to the file
				System.out.println("Write done");
				fw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public String executeQuery() {
		if (!ExtrasStatus.isLinux) {
			return executeQueryDLVComplex();
		} else {
			return executeQueryIDLV();
		}
	}

	public String executeQueryIDLV() {
		try {
			addQueryIDLVFile();

			Runtime rt = Runtime.getRuntime();
			String commandLine = ExtrasStatus.idlvExecutePath + " " + ExtrasStatus.transactionsFilePath + " "
					+ ExtrasStatus.idlvExecutePythonPath;
			System.out.println(commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String rsline = "";
			while (true) {
				String linea = br.readLine();
				if (linea == null) {
					break;
				}
				rsline = rsline.concat(linea + "\n");
			}

			ExtrasStatus.logIntoFile(rsline);
			return rsline;
		} catch (Exception t) {
			t.printStackTrace();
		}
		return "error";
	}

	public void addQueryIDLVFile() {
		String query = "%____QUERY\r\n" + 
				"tx(TXID,N1,N3,V) :- tx(TXID1,N1,N2,V12), tx(TXID2,N2,N3,V23), &mergeTransaction(TXID1,TXID2,V12,V23;TXID,V).\r\n" + 
				"\r\n" + 
				"presum(NX,NY,V) :- tx(_,NX,NY,VXY),&presumTx(NX,NY,VXY;V).\r\n" + 
				"\r\n" + 
				"latestsum(NX,NY,V) :- presum(NX,NY,_),&latestsumTx(NX,NY;V).\r\n" + 
				"";
		try {
			FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath, true);
			fw.write(query);
			fw.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

	public String executeQueryDLVComplex() {
		try {
			addQueryDLVComplexFile();

			Runtime rt = Runtime.getRuntime();
			String commandLine = ExtrasStatus.dlvComplexExecutePath + " -silent " + ExtrasStatus.transactionsFilePath
					+ " " + (ExtrasStatus.printable ? "" : " -nofacts") + " -nofdcheck ";
			System.out.println(commandLine);
			Process proc = rt.exec(commandLine);

			InputStreamReader isr = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String linea = null;
			linea = br.readLine();
			System.out.println(linea);
			ExtrasStatus.logIntoFile(linea);
			return linea;
		} catch (Exception t) {
			t.printStackTrace();
		}
		return "error";
	}

	public void addQueryDLVComplexFile() {
		String query = "%____QUERY\r\n" + "path([TXID],N1,N2,V) :- tx(TXID,N1,N2,V).\r\n" + "\r\n"
				+ "path([TXID2|[TXID1|L]],N1,N3,V2) :- path([TXID1|L],N1,N2,V1) ,tx(TXID2,N2,N3,V2),not #member(TXID2,[TXID1|L]), V1 >= V2.\r\n"
				+ "path([TXID2|[TXID1|L]],N1,N3,V1) :- path([TXID1|L],N1,N2,V1) ,tx(TXID2,N2,N3,V2),not #member(TXID2,[TXID1|L]), V1 < V2.\r\n"
				+ "\r\n" + "total(NX,NY,V) :- path(_,NX,NY,_),#sum{VI,P : path(P,NX,NY,VI)} = V.";
		try {
			FileWriter fw = new FileWriter(ExtrasStatus.transactionsFilePath, true);
			fw.write(query);
			fw.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
}