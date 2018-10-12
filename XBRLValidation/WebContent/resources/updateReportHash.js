$(document).ready(function() {
   
	$(".bigFrame").hide();
	$("#popupModel").hide();
	var reportId = undefined;
    facts = undefined;
    arcs = undefined;
    validated = undefined;
    
    var reportSize = undefined;
    var companyIndex = undefined;
    var isYourCompany = true;
    
    var web3;
    var myContract;
    
    initWeb3();
    
    var sender = $("#userAddress").val();
    var pass = $("#userPass").val();
    console.log("sender:"+sender);
    if (sender && pass) {
    	getCompany(sender, function(){
    		console.log(reportSize);
    		reportSize = new web3.BigNumber(reportSize).toString();
    		console.log(reportSize);
    		$(".bigFrame").show();
    		getReportsHTML();
    	})
    } else {
    	alert("Please add your password and address");
    	
    }
    
    function getReportsHTML(){
    	$(".lsReports").html("");
    	for (var i = 0;i < reportSize;i++) {
    		getReportByIndex(i, function(rindex,result){
    			reportIndex = result[1];
    			$(".lsReports").append(
    					"<tr>" +
    					"<td>"+result[0]+"</td>" +
    					"<td>"+result[2]+"</td>" +
    					"<td>"+result[3]+"</td>"+
    					"<td>"+result[4]+"</td>"+
    					"<td></td>" +
    					"</tr>");
    		})
    	}
    }
    
    $('body').on('click', '.readReportBtn', function() {
    	console.log($(this).attr("class"));
    	factSize = $(this).attr("class").split(" ")[1];
    	arcSize = $(this).attr("class").split(" ")[2];
    	reportIndex = $(this).attr("class").split(" ")[3];
    	validated = $($(this).parent().parent().children()[3]).html();//$(this).attr("class").split(" ")[4];
    	reportId = $(this).attr("class").split(" ")[5];
        console.log("Current validated:"+validated);
                
    	facts = [];
        arcs = [];
        
    	getFactsHTML(reportIndex , factSize);
    	getArcsHTML(reportIndex , arcSize);
    });
    
    function getFactsHTML(reportIndex , factSize){
    	$(".lsFacts").html("");
    	for (var i = 0;i < factSize;i++) {
    		getFactByIndex(companyIndex, reportIndex, i, function(findex,result){
//    			console.log(result);
    			htmlText = 	"<tr>\r\n" + 
    			"    <td><input type=\"text\" class=\"fact concept\" value='"+result[0]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"fact context\" value='"+result[1]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"fact value\" value='"+result[2]+"'/></td>\r\n" +
    			"    <td><input type=\"text\" class=\"fact unit\" value='"+result[3]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"fact factgroup\" value='"+result[4]+"'/></td>\r\n" + 
    			"    <td>\r\n" + 
    			"        <div class='updateFactBtn "+findex+" "+reportIndex+"'>Update</div>\r\n" + 
    			"    </td>\r\n" + 
    			"</tr>"
    			$(".lsFacts").append(htmlText);
    			//fact (itcc_ci_TotaleCreditiVersoSociVersamentiAncoraDovuti,contextiEsC,"15000",euro,fgroupGlobal).
    			facts.push("fact ("+result[0]+","+result[1]+",\""+result[2]+"\","+result[3]+","+result[4]+").");
    		})
    	}
    }
    
    function getArcsHTML(reportIndex , arcSize){
    	$(".lsArcs").html("");
    	for (var i = 0;i < arcSize;i++) {
    		getArcByIndex(companyIndex, reportIndex, i, function(aindex,result){
//    			console.log(result);
    			htmlText = 	"<tr>\r\n" + 
    			"    <td><input type=\"text\" class=\"arc concept from\" value='"+result[0]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"arc concept to\" value='"+result[1]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"arc weigth\" value='"+result[2]+"'/></td>\r\n" + 
    			"    <td><input type=\"text\" class=\"fact calLinkBase\" value='"+result[3]+"'/></td>\r\n" + 
    			"    <td>\r\n" + 
    			"        <div class='updateArcBtn "+aindex+" "+reportIndex+"'>Update</div>\r\n" + 
    			"    </td>\r\n" + 
    			"</tr>"
    			$(".lsArcs").append(htmlText);
    			//arc (from,to,"1.0",http_www_infocamere_it_itnn_fr_itcc_role_CEAbbreviata).
    			arcs.push("arc ("+result[0]+","+result[1]+",\""+result[2]+"\","+result[3]+").");
    		})
    	}
    }
    
    $('body').on('click', '.updateFactBtn', function() {
    	if (isYourCompany == false) {
    		window.alert("This is not your company");
    		return;
    	}
    	
    	console.log($(this).attr("class"));
    	findex = $(this).attr("class").split(" ")[1];
    	reportIndex = $(this).attr("class").split(" ")[2];
//    	console.log(findex);
//    	console.log(reportIndex);

    	chiltr = $(this).parent().parent().children();
    	concept = $($(chiltr[0]).children("input")[0]).val();
    	context = $($(chiltr[1]).children("input")[0]).val();
    	value = $($(chiltr[2]).children("input")[0]).val();
    	unit = $($(chiltr[3]).children("input")[0]).val();
    	factgroup = $($(chiltr[4]).children("input")[0]).val();
    	console.log(concept+" "+context+" "+value+" "+factgroup);
    	
    	facts[findex] = "fact ("+concept+","+context+",\""+value+"\","+unit+","+factgroup+").";
    	
//    	console.log(facts);
    	
    	chiltr = $("."+reportId).parent().parent().children();
    	
    	var btn = $(this);
    	
    	var updateFunction = function(check) {
    		$("#popupModel").show();
        	tx = editFact(companyIndex, reportIndex, findex, concept, context, value,unit, factgroup);
        	btn.html("Updating");
        	waitForTx(tx, function(){
        		if (validated != check) {
	        		btn.html("Update validated");
	        		validated = check;
	        		updateValidatedValue(function(){
	        			btn.html("Update");
	        			$(chiltr[3]).html(validated);
	        			$("#popupModel").hide();
	        		});
        		} else {
        			btn.html("Update");
        			$("#popupModel").hide();
        		}
        	});
    	} 
    	var callback = function(check) {
    		console.log("check:"+check+" current:"+validated);
	    	if (check == "false" && validated != "false") {	    		
		    	if (confirm("Update will lead to invalid report!")) {
		    	    console.log("You pressed OK!");
		    	    $(chiltr[3]).html("checking");
		    	    updateFunction(check);
		    	} else {
		    		console.log("You pressed Cancel!");
		    	}
	    	} else {
	    		updateFunction(check);
	    		//console.log("no case");
	    	}
    	}
    	checkValidationForUpdate(callback);
    });
    
	function updateValidatedValue(callback) {
		console.log("updateValidatedValue:"+validated)
		tx = updateValidateReport(reportId,validated);
		waitForTx(tx, function(){   			
			console.log("finished");
			callback();
		})
	}
	
    function updateValidateReport(reportId, validated) {
    	console.log("updateValidateReport>"+reportId+">validated>"+validated);
    	
    	var getData = myContract.updateValidateReport.getData(reportId, validated);
    	web3.personal.unlockAccount(sender, "feal1234")

    	tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas : 300000
        });
        console.log(tx);
        return tx;
    }
    
    $('body').on('click', '.updateArcBtn', function() {
    	if (isYourCompany == false) {
    		window.alert("This is not your company");
    		return;
    	}
    	console.log($(this).attr("class"));
    	aindex = $(this).attr("class").split(" ")[1];
    	reportIndex = $(this).attr("class").split(" ")[2];
    	
    	console.log(aindex);
    	console.log(reportIndex);
    	
    	chiltr = $(this).parent().parent().children();
    	conceptFrom = $($(chiltr[0]).children("input")[0]).val();
    	conceptTo = $($(chiltr[1]).children("input")[0]).val();
    	weight = $($(chiltr[2]).children("input")[0]).val();
    	calLinkBase = $($(chiltr[3]).children("input")[0]).val();

    	arcs[aindex] = "arc ("+conceptFrom+","+conceptTo+",\""+weight+"\","+calLinkBase+").";

    	console.log(conceptFrom+" "+conceptTo+" "+weight+" "+calLinkBase);
    	
//    	tx = editArc(companyIndex, reportIndex, aindex, conceptFrom, conceptTo, weight,calLinkBase);
//    	var btn = $(this);
//    	btn.html("Updating");
//    	waitForTx(tx, function(){
//    		btn.html("Update");
//    	});
    	
    	chiltr = $("."+reportId).parent().parent().children();
    	
    	    	
    	var btn = $(this);
    	
    	var updateFunction = function(check) {
    		$("#popupModel").show();
    		tx = editArc(companyIndex, reportIndex, aindex, conceptFrom, conceptTo, weight,calLinkBase);
        	btn.html("Updating");
        	waitForTx(tx, function(){
        		if (validated != check) {
	        		btn.html("Update validated");
	        		validated = check;
	        		updateValidatedValue(function(){
	        			btn.html("Update");
	        			$(chiltr[3]).html(validated);
	        			$("#popupModel").hide();
	        		});
        		} else {
        			btn.html("Update");
        			$("#popupModel").hide();
        		}
        	});
    	} 
    	var callback = function(check) {
    		console.log("check:"+check+" current:"+validated);
	    	if (check == "false" && validated != "false") {
	    		
		    	if (confirm("Update will lead to invalid report!")) {
		    	    console.log("You pressed OK!");
		    	    $(chiltr[3]).html("checking");
		    	    updateFunction(check);
		    	} else {
		    		console.log("You pressed Cancel!");
		    	}
	    	} else {
	    		updateFunction(check);
	    		//console.log("no case");
	    	}
    	}
    	checkValidationForUpdate(callback);
    });
    
    function checkValidationForUpdate(callback) {
    	console.log("checkValidationForUpdate");
    	factList = "";
    	for (e in facts) {
    		factList = factList + facts[e] + "\n";
    	}
    	
    	arcList = "";
    	for (e in arcs) {
    		arcList = arcList + arcs[e] + "\n";
    	}
    	
    	$.ajax({
            type: "POST",
            url: "checkUpdatingReport",
            data: {
                facts : factList,
                arcs : arcList,
            }
        }).done(function(response) {
        	console.log("Response:"+response);
        	callback(response);
        
        }).fail(function(xhr, status, errorThrown) {
            console.log("Error: " + errorThrown);
            console.log("Status: " + status);
            console.dir(xhr);
            callback("false");
        });
    }
    
    ///////////web3 areas
    function initWeb3(){
    	var Web3 = require('web3');
    	web3 = new Web3();

    	//web3.setProvider(new web3.providers.HttpProvider('http://127.0.0.1:8545'));
    	web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:8545"));

    	contractAddress = "0x70C82AACBA1e1CA5a4F2C9d12341E5e5D77084d2";
    	var abiContract = [ { "constant": true, "inputs": [], "name": "name", "outputs": [ { "name": "", "type": "string", "value": "XBRLH 0.1" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "date", "type": "string" }, { "name": "validated", "type": "string" }, { "name": "shash", "type": "string" } ], "name": "addReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "uint256" } ], "name": "companies", "outputs": [ { "name": "companyAddress", "type": "address" }, { "name": "companyName", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "reportId", "type": "string" }, { "name": "date", "type": "string" }, { "name": "validated", "type": "string" }, { "name": "shash", "type": "string" } ], "name": "updateValidateReport", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "ownCompany", "outputs": [ { "name": "rs", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "standard", "outputs": [ { "name": "", "type": "string", "value": "Token 0.1" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyAddress", "type": "address" } ], "name": "getCompany", "outputs": [ { "name": "cpAddress", "type": "address", "value": "0x0000000000000000000000000000000000000000" }, { "name": "companyName", "type": "string", "value": "" }, { "name": "reportSize", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "companyIndex", "type": "uint256" }, { "name": "reportIndex", "type": "uint256" } ], "name": "getReportByIndex", "outputs": [ { "name": "rpId", "type": "string" }, { "name": "rpIndex", "type": "uint256" }, { "name": "date", "type": "string" }, { "name": "validated", "type": "string" }, { "name": "shash", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "owner", "outputs": [ { "name": "", "type": "address", "value": "0xd231809c87ecd60f34da30c0127139f06c9100af" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [], "name": "symbol", "outputs": [ { "name": "", "type": "string", "value": "X" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" } ], "name": "frozenAccount", "outputs": [ { "name": "", "type": "bool", "value": false } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "companyName", "type": "string" } ], "name": "registerNewCompany", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "reportId", "type": "string" } ], "name": "ownReport", "outputs": [ { "name": "rs", "type": "uint256", "value": "0" }, { "name": "companyIndex", "type": "uint256", "value": "0" }, { "name": "reportIndex", "type": "uint256", "value": "0" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "newOwner", "type": "address" } ], "name": "transferOwnership", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "inputs": [ { "name": "tokenName", "type": "string", "index": 0, "typeShort": "string", "bits": "", "displayName": "token Name", "template": "elements_input_string", "value": "XBRLH 0.1" }, { "name": "tokenSymbol", "type": "string", "index": 1, "typeShort": "string", "bits": "", "displayName": "token Symbol", "template": "elements_input_string", "value": "X" } ], "payable": false, "stateMutability": "nonpayable", "type": "constructor" }, { "anonymous": false, "inputs": [ { "indexed": false, "name": "target", "type": "address" }, { "indexed": false, "name": "frozen", "type": "bool" } ], "name": "FrozenAccount", "type": "event" } ];

    	myContract = web3.eth.contract(abiContract).at(contractAddress);
    }
    
    function editFact(companyIndex, reportIndex, findex, concept, context, value,unit, factgroup) {
        var getData = myContract.updateFactByIndex.getData(companyIndex, reportIndex, findex, concept, context, value,unit, factgroup);
        web3.personal.unlockAccount(sender, pass)

        tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas: 300000
        });
        console.log(tx);
        return tx;
    }
    
    function editArc(companyIndex, reportIndex, aindex, conceptFrom, conceptTo, weight,calLinkBase) {
        var getData = myContract.updateArcByIndex.getData(companyIndex, reportIndex, aindex, conceptFrom, conceptTo, weight+"",calLinkBase);
        web3.personal.unlockAccount(sender, pass)

        tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas: 300000
        });
        console.log(tx);
        return tx;
    }
    
    function addFact(reportId, concept, context, value, factgroup) {
        var getData = myContract.addFact.getData(reportId, concept, context, value, factgroup);
        web3.personal.unlockAccount(sender, pass)

        tx = web3.eth.sendTransaction({
            from: sender,
            to: contractAddress,
            data: getData,
            gas: 300000
        });
        console.log(tx);
        return tx;
    }
    
    function getFactByIndex(companyIndex, reportIndex, findex, callback) {
    	myContract.getFactByIndex(companyIndex,reportIndex,findex ,function (error, result) {
            if (!error) {
//                console.log(result)
                callback(findex, result);
            } else
                console.error(error);
        })
    }
    
    function getArcByIndex(companyIndex, reportIndex, index, callback) {
    	myContract.getArcByIndex(companyIndex,reportIndex,index ,function (error, result) {
            if (!error) {
//                console.log(result)
                callback(index, result);
            } else
                console.error(error);
        })
    }

    function getReportByIndex(rindex, callback) {
    	myContract.getReportByIndex(companyIndex,rindex,function (error, result) {
            if (!error) {
//                console.log(result);
                callback(rindex, result);
            } else {
                console.error(error);
            }
        })
    }
    function getCompany(address, callback) {
    	if (address == sender) {
    		msg = "Your company information :";
    		isYourCompany = true;
    	} else {
    		msg = "The company information :";
    		isYourCompany = false;
    	}
    	myContract.getCompany(address,function (error, result) {
            if (!error){
            	console.log(result);
                $("#showCompany").html("<div>"+msg +
                "</br>"+
                "Address:"+result[0]+
                "</br>" +
                "Company name:"+result[1]+
                "</br>" +
                "Number of report:"+result[2]+
                "</div>");
                reportSize = result[2];
                companyIndex = result[3];
                console.log("companyIndex:"+companyIndex);
                callback();
            }else {
            	alert("You dont have any company");
            	console.log("Error");
            	console.error(error);
            	$("#newCompany").show();
            	$("#showCompany").html("You need to submit your company name");
            }
        })
    }
    $("#dropdownCompany").hide();
//    $('body').on('click', '#companyName', function() {
//    	console.log("click");
//    	$("#myDropdown").show();
//    })
    
    window.onclick = function(event) {
	  if (event.target.matches('#companyName')) {
//		  console.log("Show here");
		  $("#dropdownCompany").show();
		  searchCompany();
	  } else {
//		  console.log("hide");
		  $("#dropdownCompany").hide();
	  }
    }
    
    $('#companyName').on('input',function(e){
//		  console.log("Show here");
		  $("#dropdownCompany").show();
		  searchCompany();
    });
    $('body').on('click', '.dropdownSubLink', function() {
    	sclass = $(this).attr("class").split(" ");
    	if (sclass.length < 3) {
    		return;
    	}
    	address = $(this).attr("class").split(" ")[1];
    	name = $(this).attr("class").split(" ")[2];
    	console.log(address);
    	console.log(name);
    	getCompany(address, function(){
    		console.log(reportSize);
    		reportSize = new web3.BigNumber(reportSize).toString();
    		console.log(reportSize);
    		$(".bigFrame").show();
    		getReportsHTML();
    	})
    })
    function searchCompany() {
    	text = $("#companyName").val();
    	if (text == null || text.length == 0) {
    		$("#dropdownCompany").html("<div class=\"dropdownSubLink\">Please fill your company name</div>");
    		return;
    	}
//    	console.log("find");
    	$("#dropdownCompany").html("");
        text = text.toUpperCase();
        count = 0;
        stop = false;
        loop = function(index, callback) {
	        myContract.companies(index, function (error, result) {
	            if (!error) {
	            	//console.log(index);
	                //console.log(result);
	                if (result[1].toUpperCase().includes(text)) {
	                	//rs.push({address : result[0], name : result[1]});
	                	count = count + 1;
	                	$("#dropdownCompany").append("<div class=\"dropdownSubLink "+result[0]+" "+result[1]+"\">"+result[1]+"</div>");
	                }
	                if (count >= 5) {
	                	callback()
	                } else {
	                	loop(index+1, callback);
	                }
	            } else {
	                //console.error(error);
	                callback();
	            }
	            
	        })
        };
        loop(0,function(){
        	if (count == 0) {
        		$("#dropdownCompany").html("<div class=\"dropdownSubLink\">No result</div>");
        	}
//        	console.log("Done search");
        });
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