/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unical.blockchain;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransactionController {
	@Autowired
	private TransactionService txController;

	@RequestMapping(value = "/addTransactions", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> addTransactions(@RequestParam String id, @RequestParam String from, @RequestParam String to,
			@RequestParam String value, @RequestParam String blocknum, HttpSession session) {
		HashMap<String, String> result = new HashMap<>();
		System.out.println("Add Transactions:" + from + " " + to + " " + value + " " + blocknum);
		txController.addTx(new Transaction(id, from, to, value, blocknum));
		result.put("state", "done");
		return result;
	}

	@RequestMapping(value = "/resetDLVTransactionFile", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> resetDLVTransactionFile(HttpSession session) {
		HashMap<String, String> result = new HashMap<>();
		txController.resetDLVFile();
		result.put("state", "done");
		return result;
	}

	@RequestMapping(value = "/executeDLVTransaction", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, String> executeDLVTransaction(HttpSession session) {
		HashMap<String, String> result = new HashMap<>();
		result.put("state", "done");
		result.put("result", txController.executeQuery());
		return result;
	}
}
