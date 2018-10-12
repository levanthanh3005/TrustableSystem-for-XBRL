$(document).ready(function() {
    $("#showFile").hide();
    $("#btnValidate").hide();
    $("#showValidation").hide();
    $("#btnPushToBlockchain").hide();
    $(".fileform").hide();
    $("#newCompany").hide();
    $("#showPushedResult").hide();
    $("#showPushedReport").hide();
    $("#showPushedValidation").hide();
    
    $("#back").hide();
    
    
	var reportId = undefined;
    fileToValidate = undefined;
    facts = undefined;
    arcs = undefined;
    validated = undefined;
    
    var web3;
    var myContract;
    
    initWeb3();
    
    var sender = $("#userAddress").val();
    var pass = $("#userPass").val();
    
    getCompany();
    
    var currentBalance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
    console.log(currentBalance);
    
    $('body').on('click', '#btnChooseFile', function() {
//    	reportId = $("#reportId").val();
//    	if (!reportId) {
//    		alert("Enter your report Id, please");
//    	} else {
//    		var date = new Date()+"";
//    		tx = addReport(reportId,date);
//    		$("#showFile").show();
//    		$("#showFile").html("Publishing your report Id");
    		var fileForm = $(".file"); 
//    		waitForTx(tx, function(){
    			fileForm.trigger('click');
    			
//    			balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
//    			$("#showFile").html("Published your report Id, costed :"+(currentBalance - balance)+" ETH");
//    			currentBalance = balance;
//    		})
//    	}
    });
    $('body').on('change', '.file', function() {
        console.log("Change");
        
        $("#showFile").hide();
        $("#btnValidate").hide();
        $("#showValidation").hide();
        $("#btnPushToBlockchain").hide();
        
        console.log($('.file')[0].files[0]);

        filedata = new FormData($(".fileform")[0]);

        formData = new FormData();
        formData.append("file", filedata.get("file"));
        console.log(formData);
        uploadFile(formData);
        $("#showFile").show();
        $("#showFile").html("Ready for " + $('.file')[0].files[0].name);
    });
    $('body').on('click', '#btnSubmitCompany', function() {
    	companyName = $("#companyName").val();
    	if (companyName) {
    		console.log("addCompany:"+companyName);
    		tx = addCompany(companyName);
    		waitForTx(tx, function(){
    			getCompany();
    			$("#newCompany").hide();
    			balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
        		console.log("add company, costed :"+(currentBalance - balance)+" ETH");
        		currentBalance = balance;
    		})
    	};
    })
    
    $('body').on('click', '#showCalculation', function() {
        htmlText = "<div>" +
        	$("#calEval").html()
        "</div>";

    	$("#modelContent").html($.parseHTML(htmlText));
    	$("#showModal").show();
    })
    
    $('body').on('click', '#modelClose', function() {
    	$("#showModal").hide();
    })
    
    $('body').on('click', '#showDebtAndInterestInfoEval', function() {
        htmlText = "<div>" +
        	$("#debtAndInterestInfoEval").html()
        "</div>";

    	$("#modelContent").html($.parseHTML(htmlText));
    	$("#showModal").show();
    })
    
    
    function stringToJson(data) {
    	jsonStr= "["+data.replace(/'/g, '"')+"]";
        //				console.log(err);
        //				var obj = JSON.stringify(JSON.parse(err));
        //				console.log(obj);

        regeStr = '', // A EMPTY STRING TO EVENTUALLY HOLD THE FORMATTED STRINGIFIED OBJECT
            f = {
                brace: 0
            }; // AN OBJECT FOR TRACKING INCREMENTS/DECREMENTS,
        // IN PARTICULAR CURLY BRACES (OTHER PROPERTIES COULD BE ADDED)

        regeStr = jsonStr.replace(/({|}[,]*|[^{}:]+:[^{}:,]*[,{]*)/g, function(m, p1) {
            var rtnFn = function() {
                    return '<div style="text-indent: ' + (f['brace'] * 20) + 'px;">' + p1 + '</div>';
                },
                rtnStr = 0;
            if (p1.lastIndexOf('{') === (p1.length - 1)) {
                rtnStr = rtnFn();
                f['brace'] += 1;
            } else if (p1.indexOf('}') === 0) {
                f['brace'] -= 1;
                rtnStr = rtnFn();
            } else {
                rtnStr = rtnFn();
            }
            return rtnStr;
        });
        return regeStr;
    }
    function validateFile() {
        console.log("validateFile");
        $("#btnValidate").show();
        $.ajax({
            type: "POST",
            url: "validate",
            data: {
                filePath: fileToValidate
            }
        }).done(function(response) {
            console.log(response);

            htmlError = "";
            if (response.error) {
                htmlError = "<div class='errorDiv'>" + stringToJson(response.error) + "</div>";
            }
            htmlDebtAndInterestInfo = ""
            
            if (response.debtAndInterestInfo) {
            	htmlDebtAndInterestInfo = stringToJson(response.debtAndInterestInfo);
            }
            
            htmlText = "<div>" +
                "<div class='validatedDiv'>Calculation Validated: " + response.validated + "</div>" +
                "<div>" + response.time + "</div>" +
                "<div>Maximum calculated threshold:" + response.maxCalculatedThreshold + 
                "	<a class='showMore' id='showCalculation'>More detail</a> " +
                "</div>" +
                "<div id='calEval' style=\"display:none\">"+htmlError +"</div>" +
                
                "<div>Difference between rates of debt and interest:" + response.debtDiff + 
                "	<a class='showMore' id='showDebtAndInterestInfoEval'>More detail</a> " +
                "<div class='showMore'>" +
                	(response.rateRs == false ? "Alert:In 2 contexts the difference between 2 rates is too high, more than 0.5" : "") +
                "</div>" +
                "</div>" +
                
                "<div id='debtAndInterestInfoEval' style=\"display:none\">"+htmlDebtAndInterestInfo +"</div>" +
                "</div>";
            $("#showValidation").show();
            $("#showValidation").html($.parseHTML(htmlText));
            
            facts = response.facts;
        	arcs = response.arcs;
        	validated = response.validated;
        	
            if (response.validated == "true") {
            	console.log("Validation is correct");
            } else {
            	console.log("Validation is not correct");
            }
            
            $("#btnPushToBlockchain").show();
        
        }).fail(function(xhr, status, errorThrown) {
            console.log("Error: " + errorThrown);
            console.log("Status: " + status);
            console.dir(xhr);
        });
    }

    function uploadFile(formData) {
        console.log("uploadFile");
        $.ajax({
            type: "POST",
            url: "uploadFile",
            data: formData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000
        }).done(function(response) {
            console.log(response);
            fileToValidate = response.filepath;
            validateFile();
        }).fail(function(xhr, status, errorThrown) {
            console.log("Error: " + errorThrown);
            console.log("Status: " + status);
            console.dir(xhr);
        });
    }
    
    $('body').on('click', '#btnPushReport', function() {
    	console.log("currentBalance:"+currentBalance);
    	reportId = $("#reportId").val();
    	if (!reportId) {
    		alert("Enter your report Id, please");
    	} else {
    		var date = new Date()+"";
    		tx = addReport(reportId,date);
    		$("#showPushedReport").show();
    		$("#showPushedReport").html("Publishing your report Id");
    		var fileForm = $(".file"); 
    		waitForTx(tx, function(){   			
    			balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
    			console.log("balance:"+balance);
    			$("#showPushedReport").html("Published your report Id, costed :"+(currentBalance - balance)+" ETH");
    			currentBalance = balance;
    			pushFactsAndArcs();
    		})
    	}
    })
    
    function pushFactsAndArcs() {
    	$("#showPushedResult").show();
    	lsTx = [];
    	total = facts.length + arcs.length;
    	var index = 0;
    	   	
    	for (e in facts) {
        	$("#showPushedResult").html((total - e) +" transactions are in queue to submit");
        	lsTx[index++] = addFact(reportId, facts[e].conceptRef, facts[e].contextRef, facts[e].svalue,facts[e].unitRef, facts[e].factGroupId);    	
    	}
    	
    	for (e in arcs) {
        	$("#showPushedResult").html((total - facts.length - e ) +" transactions are in queue to submit");
        	lsTx[index++] = addArc(reportId, arcs[e].fromId, arcs[e].toId, arcs[e].weight, arcs[e].calculationalLinkId);
    	}
    	
    	console.log("Total:"+total+" > length:"+lsTx.length);
    	$("#showPushedResult").html("All transactions are waiting for confirmation");
    	
    	waitForLsTx(lsTx, function(k){
    		$("#showPushedResult").html(k + " transactions are executed");
    	}, function(){
    		balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
    		$("#showPushedResult").html("All transactions are executed, costed :"+(currentBalance - balance)+" ETH");
    		currentBalance = balance;
    		updateValidatedValue();
    	});
    	
	};
    
    ///////////web3 areas
    function initWeb3(){
    	var Web3 = require('web3');
    	web3 = new Web3();

    	//web3.setProvider(new web3.providers.HttpProvider('http://127.0.0.1:8545'));
    	web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:8545"));

    	contractAddress = "0x28c9bc4abB938B1bC6c788e5b89Dc9925392718c";
    	var abiContract = [ { "constant": true, "inputs": [], "name": "name", "outputs": [ { "name": "", "type": "string", "value": "" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "uint256" } ], "name": "companies", "outputs": [ { "name": "companyAddress", "type": "address", "value": "0xd231809c87ecd60f34da30c0127139f06c9100af" }, { "name": "companyName", "type": "string", "value": "cpTest" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "validated", "type": "string" } ], "name": "updateValidateReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "arcIndex", "type": "uint256" }, { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "name": "updateArcByIndex", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "ownCompany", "outputs": [ { "name": "rs", "type": "uint256", "value": "1" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "standard", "outputs": [ { "name": "", "type": "string", "value": "Token 0.1" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyAddress", "type": "address" } ], "name": "getCompany", "outputs": [ { "name": "cpAddress", "type": "address", "value": "0x0000000000000000000000000000000000000000" }, { "name": "companyName", "type": "string", "value": "" }, { "name": "reportSize", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "name": "addFact", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" } ], "name": "getReportByIndex", "outputs": [ { "name": "rpId", "type": "string", "value": "test1" }, { "name": "rpIndex", "type": "uint256", "value": "0" }, { "name": "date", "type": "string", "value": "Wed May 02 2018 10:52:59 GMT+0200 (CEST)" }, { "name": "validated", "type": "string", "value": "" }, { "name": "factSize", "type": "uint256", "value": "0" }, { "name": "arcSize", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "date", "type": "string" } ], "name": "addReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "owner", "outputs": [ { "name": "", "type": "address", "value": "0xd231809c87ecd60f34da30c0127139f06c9100af" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "symbol", "outputs": [ { "name": "", "type": "string", "value": "" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" } ], "name": "frozenAccount", "outputs": [ { "name": "", "type": "bool", "value": false } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "arcIndex", "type": "uint256" } ], "name": "getArcByIndex", "outputs": [ { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyName", "type": "string" } ], "name": "registerNewCompany", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "reportId", "type": "string" } ], "name": "ownReport", "outputs": [ { "name": "rs", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" }, { "name": "reportIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "conceptFrom", "type": "string" }, { "name": "conceptTo", "type": "string" }, { "name": "weight", "type": "string" }, { "name": "calLinkBase", "type": "string" } ], "name": "addArc", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "factIndex", "type": "uint256" }, { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "name": "updateFactByIndex", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" }, { "name": "factIndex", "type": "uint256" } ], "name": "getFactByIndex", "outputs": [ { "name": "concept", "type": "string" }, { "name": "context", "type": "string" }, { "name": "value", "type": "string" }, { "name": "unit", "type": "string" }, { "name": "factgroup", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyAddress", "type": "address" }, { "name": "reportId", "type": "string" } ], "name": "getReport", "outputs": [ { "name": "rpId", "type": "string", "value": "" }, { "name": "reportIndex", "type": "uint256", "value": "0" }, { "name": "date", "type": "string", "value": "" }, { "name": "validated", "type": "string", "value": "" }, { "name": "factSize", "type": "uint256", "value": "0" }, { "name": "arcSize", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "newOwner", "type": "address" } ], "name": "transferOwnership", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "inputs": [ { "name": "tokenName", "type": "string", "index": 0, "typeShort": "string", "bits": "", "displayName": "token Name", "template": "elements_input_string", "value": "XBRL 1.0" }, { "name": "tokenSymbol", "type": "string", "index": 1, "typeShort": "string", "bits": "", "displayName": "token Symbol", "template": "elements_input_string", "value": "$" } ], "payable": false, "stateMutability": "nonpayable", "type": "constructor" }, { "anonymous": false, "inputs": [ { "indexed": false, "name": "target", "type": "address" }, { "indexed": false, "name": "frozen", "type": "bool" } ], "name": "FrozenAccount", "type": "event" } ];

//    	sender = web3.eth.accounts[0];
//    	sender = "0x72b153C2F511B9De962A5a664209b08621A1C340";

    	myContract = web3.eth.contract(abiContract).at(contractAddress);
    }
    
	function updateValidatedValue() {
		console.log("updateValidatedValue:"+validated)
		$("#showPushedValidation").show();
		$("#showPushedValidation").html("Updating validated value");
		tx = updateValidateReport(reportId,validated);
		waitForTx(tx, function(){   			
			console.log("finished")
			balance = parseFloat(web3.fromWei(web3.eth.getBalance(sender), "ether").toString());
        	console.log("updateValidatedValue, costed :"+(currentBalance - balance)+" ETH");
        	currentBalance = balance;
			
        	$("#showPushedValidation").html("Finished")
			$("#back").show();
		})
	}
    
    function addCompany(companyName) {
    	var getData = myContract.registerNewCompany.getData(companyName);
    	web3.personal.unlockAccount(sender, pass);

    	tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData
        });
        console.log(tx);
        return tx;
    }
    
    function addFact(reportId, concept, context, value, unit, factgroup) {
        var getData = myContract.addFact.getData(reportId, concept, context, value, unit, factgroup)
        web3.personal.unlockAccount(sender, pass)

        tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas: 2500000
        });
        console.log(tx);
        return tx;
    }
    
    function addArc(reportId, conceptFrom, conceptTo, weight, calLinkBase) {
        var getData = myContract.addArc.getData(reportId, conceptFrom, conceptTo, weight+"", calLinkBase)
        web3.personal.unlockAccount(sender, pass)

        tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas: 2500000
        });
        console.log(tx);
        return tx;
    }
    
    function getFactByIndex(reportId, findex) {
    	myContract.getFactByIndex(sender,reportId,findex ,function (error, result) {
            if (!error)
                console.log(result)
            else
                console.error(error);
        })
    }

    function getReportByIndex(rindex) {
    	myContract.getReportByIndex(sender,rindex,function (error, result) {
            if (!error)
                console.log(result)
            else
                console.error(error);
        })
    }
    function getCompany() {
    	myContract.getCompany(sender,function (error, result) {
            if (!error && result[1]!= ""){
            	console.log(result);
                $("#showCompany").html("<div>Your company information : " +
                "</br>"+
                "Address:"+result[0]+
                "</br>" +
                "Company name:"+result[1]+
                "</div>")   
                $(".fileform").show();
            }else {
            	console.log("Error");
            	console.error(error);
            	$("#newCompany").show();
            	$("#showCompany").html("You need to submit your company name");
            }
        })
    }
    
    function updateValidateReport(reportId, validated) {
    	console.log("updateValidateReport>"+reportId+">validated>"+validated);
    	
    	var getData = myContract.updateValidateReport.getData(reportId, validated);
    	web3.personal.unlockAccount(sender, pass)

    	tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas : 300000
        });
        console.log(tx);
        return tx;
    }
    
    function addReport(reportId, date) {
    	console.log("addReport>"+reportId+">date>"+date+">by:"+sender);
    	
    	var getData = myContract.addReport.getData(reportId, date);
    	web3.personal.unlockAccount(sender, pass)

    	tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas : 300000
        });
        console.log(tx);
        return tx;
    }
    
    function waitForLsTx(lsTx, callbackEach, callbackEnd) {
    	bsize = lsTx.length;
    	var myTime = setInterval(function(){ 
    		var nlsTx = [];
    		var k = 0;
    		for (e in lsTx) {
    			if(!checkTx(lsTx[e])){
    				k = k + 1;
    				nlsTx[k] = lsTx[e];
    			}
    		}
    		lsTx = nlsTx;
    		callbackEach(bsize - k);
    		if (k == 0) {
    			clearInterval(myTime);	
    			callbackEnd();
    		}
    	}, 1000);
    }
    
    function waitForTx(txid, callback) {
    	var myTime = setInterval(function(){ 
    		if(checkTx(txid)){
    			clearInterval(myTime);	
    			callback();
    		}
    	}, 1000);
    }
    
    function checkTx(txid) {
    	console.log(web3.eth.getTransaction(txid));
    	if(web3.eth.getTransaction(txid).blockNumber){
    		return true;
    	}
    	return false;
    }
})