package it.unical.xbrl.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import it.unical.xbrl.xbrlcore.I_DLVClass.DLVTransform;
import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVArc;
import it.unical.xbrl.xbrlcore.I_DLVClassALL.DLVQuery;
import it.unical.xbrl.xbrlcore.OntoClass.ObjectSd;
import it.unical.xbrl.xbrlcore.xbrlcoretest.ExtrasStatus;
import it.unical.xbrl.xbrlcore.xbrlcoretest.XbrlExplorer;
import it.unical.xbrl.xbrlcore.xbrlcoretest.XbrlExplorer2;

@Controller
public class IndexController {
	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpSession session) {
		return "Index";
	}

	@RequestMapping(value = { "validation" })
	public String validation(Model model, HttpSession session) {
		return "Validation";
	}
	
	@RequestMapping(value = { "validationhash" })
	public String validationhash(Model model, HttpSession session) {
		return "ValidationHash";
	}
	
	@RequestMapping(value = { "validation2" })
	public String validation2(Model model, HttpSession session) {
		return "Validation2";
	}
	
	@RequestMapping(value = { "validationManyFiles" })
	public String validationManyFiles(Model model, HttpSession session) {
		return "ValidationManyFiles";
	}
	
	@RequestMapping(value = { "updatereport" })
	public String updateReport(Model model, HttpSession session) {
		return "UpdateReport";
	}
	
	@RequestMapping(value = { "updatereporthash" })
	public String updateReportHash(Model model, HttpSession session) {
		return "UpdateReportHash";
	}

	@RequestMapping(value = { "/transactions" })
	public String transactions(Model model, HttpSession session) {
		ExtrasStatus.setIsLinux(true);
		return "Transactions";
	}
	
	@RequestMapping(value = { "/wallet" })
	public String accessWallet(Model model, HttpSession session) {
		ExtrasStatus.setIsLinux(true);
		return "Wallet";
	}

	@RequestMapping(value = { "/contracts" })
	public String contracts(Model model, HttpSession session) {
		ExtrasStatus.setIsLinux(true);
		return "Contracts";
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> uploadFile(@RequestParam("file") MultipartFile fileData, HttpSession session) {
		HashMap<String, String> result = new HashMap<>();

		String fileName = ExtrasStatus.XBRLDirPath + File.separator + UUID.randomUUID().toString() + ".xbrl";

		try {
			byte[] bytes = fileData.getBytes();

			Path path = Paths.get(fileName);
			Files.write(path, bytes);

			result.put("state", "done");
			result.put("filepath", fileName);

			return result;
		} catch (Exception e) {
			result.put("state", "error");
			return result;
		}

	}
	
	@RequestMapping(value = "/uploadManyFile", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> uploadFiles(@RequestParam("file") MultipartFile[] fileData, HttpSession session) {
		HashMap<String, String> result = new HashMap<>();

//		System.out.println("AAAAAAAAAAAAAAAA");
		String sresult = "";
		try {
//			System.out.println(fileData.length);
			for (int i = 0;i < fileData.length;i++) {
				String fileName = ExtrasStatus.XBRLDirPath + File.separator + UUID.randomUUID().toString() + ".xbrl";
//				System.out.println(fileData[i].getSize());
				byte[] bytes = fileData[i].getBytes();
				Path path = Paths.get(fileName);
				Files.write(path, bytes);
				sresult = sresult.concat(fileName + ",");
			}
			sresult = sresult.substring(0,sresult.length()-1);
			
			result.put("state", "done");
			result.put("filepaths", sresult);

			return result;
		} catch (Exception e) {
			result.put("state", "error");
			System.out.println(e);
			return result;
		}

	}

	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> validateXBRLFile(@RequestParam("filePath") String filePath, HttpSession session) {
		ExtrasStatus.setIsLinux(true);
		XbrlExplorer lvEx = new XbrlExplorer();
		// ExtrasStatus.printable = true;
		return lvEx.scanInstanceHashMap(new File(filePath));
	}
	
	@RequestMapping(value = "/validateManyFiles", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> validateManyFiles(@RequestParam("filePath") String filePath, HttpSession session) {
		return new XbrlExplorer2().scanInstanceHashMap(filePath);
	}

	@RequestMapping(value = "/configureUserInfor", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> configureUserInfor(Model model, 
			@RequestParam String userAddress,
			@RequestParam String userPass, 
			HttpSession session) {
		System.out.println("Save infor:"+userAddress + " vs "+userPass);
		session.setAttribute("userAddress", userAddress);
		session.setAttribute("userPass", userPass);
		
		HashMap<String, String> result = new HashMap<>();

		result.put("state","done");

		return result;
	}
	
	@RequestMapping(value = "/checkUpdatingReport", method = RequestMethod.POST)
	@ResponseBody
	public String checkUpdatingReport(Model model, 
			@RequestParam String facts,
			@RequestParam String arcs, 
			HttpSession session) {
		
		try {
			ExtrasStatus.setIsLinux(true);
//			System.out.println("checkUpdatingReport");
//			System.out.println(ExtrasStatus.dlvFilePath);
			FileWriter fw = new FileWriter(new File(ExtrasStatus.dlvFilePath));
			fw.write(facts+"\n");
			fw.write(arcs+"\n");
			fw.write(DLVQuery.query);
			fw.close();
			
			HashMap<String, HashMap<String, Object>> queryResult = new HashMap<>();

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("error", "");
			obj.put("arcs", new ArrayList<DLVArc>());
			obj.put("debtAndInterestInfo", "");
			obj.put("validated", "false");
			obj.put("maxCalculatedThreshold", "");
			obj.put("debtDiff", "");
			obj.put("rateRs", "");
			obj.put("facts", facts);

			queryResult.put("company", obj);
			DLVQuery dq = new DLVQuery();
			dq.setQueryResult(queryResult);
			dq.execDLVValidatorHashMap();
			return queryResult.get("company").get("validated").toString();
			
		} catch (Exception e) {
//			System.out.println("Exception:"+e);
			ExtrasStatus.log("writeFileCollection:" + e.toString());
		}
//		System.out.println("Return break");
		return "false";
	}
}
